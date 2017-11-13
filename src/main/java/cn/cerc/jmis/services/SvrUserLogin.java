package cn.cerc.jmis.services;

import static cn.cerc.jdb.other.utils.intToStr;
import static cn.cerc.jdb.other.utils.newGuid;
import static cn.cerc.jdb.other.utils.random;

import cn.cerc.jbean.client.LocalService;
import cn.cerc.jbean.core.Application;
import cn.cerc.jbean.core.CustomHandle;
import cn.cerc.jbean.core.CustomService;
import cn.cerc.jbean.core.DataValidateException;
import cn.cerc.jbean.core.ServerConfig;
import cn.cerc.jbean.core.Webfunc;
import cn.cerc.jbean.other.BookVersion;
import cn.cerc.jbean.other.BufferType;
import cn.cerc.jbean.other.MemoryBuffer;
import cn.cerc.jbean.other.SystemTable;
import cn.cerc.jbean.tools.MD5;
import cn.cerc.jdb.core.DataSet;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.core.Record;
import cn.cerc.jdb.core.TDateTime;
import cn.cerc.jdb.jiguang.ClientType;
import cn.cerc.jdb.mysql.BuildQuery;
import cn.cerc.jdb.mysql.SqlOperator;
import cn.cerc.jdb.mysql.SqlQuery;
import cn.cerc.jdb.mysql.Transaction;
import cn.cerc.jdb.oss.OssSession;

/**
 * 用于用户登录
 * 
 * @author 张弓
 *
 */
public class SvrUserLogin extends CustomService {
    private static String GuidNull = "";
    private static int Max_Viability = 1;
    public static int VerifyCodeTimeout = 5; // 效验代码超时时间（分钟）

    /*
     * 用户登录入口
     */
    @Webfunc
    public boolean Check() throws SecurityCheckException {
        Record headIn = getDataIn().getHead();
        getDataOut().getHead().setField("errorNo", 0);

        String deviceId = headIn.getString("MachineID_");
        // 判断是否为浏览器登陆
        if (Application.webclient.equals(deviceId)) {
            throw new SecurityCheckException("系统已不再支持使用web浏览器登录，请使用地藤客户端登录系统！");
        }

        String device_name = "";
        if (headIn.hasValue("ClientName_")) {
            device_name = headIn.getString("ClientName_");
        } else {
            device_name = "unknow";
        }

        CustomHandle sess = (CustomHandle) this.getProperty(null);
        if (headIn.exists("ClientIP_")) {
            sess.setProperty(Application.clientIP, headIn.getString("ClientIP_"));
        } else {
            sess.setProperty(Application.clientIP, "0.0.0.0");
        }

        // 开始进行用户验证
        String userCode = headIn.getString("Account_");
        if (userCode.equals("")) {
            throw new SecurityCheckException("用户帐号不允许为空！");
        }

        SqlQuery dsUser = new SqlQuery(this);
        dsUser.add("select UID_,CorpNo_,ID_,Code_,Name_,Mobile_,DeptCode_,Enabled_,Password_,BelongAccount_,");
        dsUser.add("VerifyTimes_,Encrypt_,SecurityLevel_,SecurityMachine_,PCMachine1_,PCMachine2_,");
        dsUser.add("PCMachine3_,RoleCode_,DiyRole_ from %s where Code_='%s'", SystemTable.get(SystemTable.getUserInfo),
                userCode);
        dsUser.open();
        if (dsUser.eof()) {
            throw new SecurityCheckException(String.format("该帐号(%s)并不存在，禁止登录！", userCode));
        }

        String corpNo = dsUser.getString("CorpNo_");
        BookInfoRecord buff = MemoryBookInfo.get(this, corpNo);
        if (buff == null) {
            throw new SecurityCheckException(String.format("没有找到注册的帐套  %s ", corpNo));
        }

        boolean YGLogin = buff.getCorpType() == BookVersion.ctFree.ordinal();
        if (buff.getStatus() == 3) {
            throw new SecurityCheckException("对不起，您的账套处于暂停录入状态，禁止登录！");
        }
        if (buff.getStatus() == 4) {
            throw new SecurityCheckException("对不起，您的帐套已过期，请联系客服续费！");
        }
        if (dsUser.getInt("Enabled_") < 1) {
            throw new SecurityCheckException(String.format("该帐号(%s)被暂停使用，禁止登录！", userCode));
        }
        // 判断此帐号是否为附属帐号
        if (dsUser.getString("BelongAccount_") != null && !"".equals(dsUser.getString("BelongAccount_"))) {
            throw new SecurityCheckException(
                    String.format("该帐号已被设置为附属帐号，不允许登录，请使用主帐号 %s 登录系统！", dsUser.getString("BelongAccount_")));
        }

        // 取得认证密码，若是微信入口进入，则免密码录入
        String password = headIn.getString("Password_");
        if (password == null || "".equals(password)) {
            if ("".equals(dsUser.getString("Mobile_"))) {
                throw new RuntimeException("您没有登记手机号，请您输入密码进行登陆！");
            } else {
                getDataOut().getHead().setField("Mobile_", dsUser.getString("Mobile_"));
                throw new RuntimeException("用户密码不允许为空！");
            }
        }
        enrollMachineInfo(dsUser.getString("CorpNo_"), userCode, deviceId, device_name);

        if (dsUser.getBoolean("Encrypt_")) {
            if (!headIn.exists("wx") && !"000000".equals(password)) {
                password = MD5.get(dsUser.getString("Code_") + password);
            }
        }

        if (!isAutoLogin(userCode, deviceId) && !"000000".equals(password)) {
            if (!dsUser.getString("Password_").equals(password)) {
                dsUser.edit();
                if (dsUser.getInt("VerifyTimes_") == 6) {
                    // 该账号设置停用
                    dsUser.setField("Enabled_", 0);
                    dsUser.post();
                    throw new RuntimeException("您输入密码的错误次数已超出规定次数，现账号已被自动停用，若需启用，请您联系客服处理！");
                } else {
                    dsUser.setField("VerifyTimes_", dsUser.getInt("VerifyTimes_") + 1);
                    dsUser.post();
                    throw new SecurityCheckException("您的登录密码错误，禁止登录！");
                }
            }
        }

        // 当前设备是否已被停用
        if (!isStopUsed(userCode, deviceId)) {
            throw new SecurityCheckException("您的当前设备已被停用，禁止登录，请联系管理员恢复启用！");
        }

        try (Transaction tx = new Transaction(this)) {
            String sql = String.format(
                    "update %s set LastTime_=Getdate() where UserCode_='%s' and MachineCode_='%s' and Used_=1",
                    SystemTable.get(SystemTable.getDeviceVerify), userCode, deviceId);
            getConnection().execute(sql);

            // 若该账套是待安装，则改为已启用
            SqlQuery dsCorp = new SqlQuery(this);
            dsCorp.add("select * from %s ", SystemTable.getBookInfo);
            dsCorp.add("where CorpNo_='%s' and Status_=1 ", corpNo);
            dsCorp.open();
            if (!dsCorp.eof()) {
                dsCorp.edit();
                dsCorp.setField("Status_", 2);
                dsCorp.post();
                MemoryBookInfo.clear(corpNo);
            }

            sess.setProperty(Application.token, GuidFixStr(newGuid()));
            sess.setProperty(Application.userId, dsUser.getString("ID_"));
            sess.setProperty(Application.bookNo, dsUser.getString("CorpNo_"));
            sess.setProperty(Application.userCode, dsUser.getString("Code_"));
            if (dsUser.getBoolean("DiyRole_")) {
                sess.setProperty(Application.roleCode, dsUser.getString("Code_"));
            } else {
                sess.setProperty(Application.roleCode, dsUser.getString("RoleCode_"));
            }

            // 更新当前用户总数
            updateCurrentUser(device_name, headIn.getString("Screen_"), headIn.getString("Language_"));

            try (MemoryBuffer Buff = new MemoryBuffer(BufferType.getSessionInfo, (String) getProperty("UserID"),
                    deviceId)) {
                Buff.setField("UserID_", getProperty("UserID"));
                Buff.setField("UserCode_", getUserCode());
                Buff.setField("UserName_", getUserName());
                Buff.setField("LoginTime_", sess.getProperty(Application.loginTime));
                Buff.setField("YGUser", YGLogin);
                Buff.setField("VerifyMachine", false);
            }
            // 返回值于前台
            getDataOut().getHead().setField("SessionID_", getProperty("ID"));
            getDataOut().getHead().setField("UserID_", getProperty("UserID"));
            getDataOut().getHead().setField("UserCode_", getUserCode());
            getDataOut().getHead().setField("CorpNo_", handle.getCorpNo());
            getDataOut().getHead().setField("YGUser", YGLogin);

            // 验证成功，将验证次数赋值为0
            dsUser.edit();
            dsUser.setField("VerifyTimes_", 0);
            dsUser.post();
            tx.commit();
            return true;
        }
    }

    /*
     * 退出系统
     */
    @Webfunc
    public boolean ExitSystem() {
        if ((String) getProperty("UserID") != null) {
            MemoryBuffer.delete(BufferType.getSessionInfo, (String) getProperty("UserID"), "webclient");
        }

        String token = (String) getProperty("ID");
        getConnection().execute(String.format("Update %s Set Viability_=-1,LogoutTime_=GetDate() where LoginID_='%s'",
                SystemTable.get(SystemTable.getCurrentUser), token));
        return true;
    }

    // 获取登录状态
    @Webfunc
    public boolean getState() {
        getDataOut().getHead().setField("UserID_", getProperty("UserID"));
        getDataOut().getHead().setField("UserCode_", getUserCode());
        getDataOut().getHead().setField("CorpNo_", handle.getCorpNo());
        return true;
    }

    @Override
    public boolean checkSecurity(IHandle handle) {
        return true;
    }

    @Webfunc
    public boolean autoLogin() throws SecurityCheckException {
        Record headIn = getDataIn().getHead();

        String token1 = headIn.getString("token");
        // 加入ABCD是为了仅允许内部调用
        ServerConfig config = ServerConfig.getInstance();
        String token2 = config.getProperty(OssSession.oss_accessKeySecret, "") + "ABCD";
        // 如果不是内部调用，则返回false
        if (!token2.equals(token1)) {
            return false;
        }

        String clientId = headIn.getString("openid");
        SqlQuery ds = new SqlQuery(this);
        ds.add("SELECT A.Code_,A.Password_ FROM %s A", SystemTable.get(SystemTable.getDeviceVerify));
        ds.add("inner JOIN %s B", SystemTable.get(SystemTable.getUserInfo));
        ds.add("ON A.UserCode_=B.Code_");
        ds.add("WHERE A.MachineCode_='%s' AND A.AutoLogin_=1", clientId);
        ds.open();
        if (ds.eof()) {
            return false;
        }

        headIn.setField("Account_", ds.getString("Code_"));
        headIn.setField("Password_", ds.getString("Password_"));
        headIn.setField("MachineID_", clientId);
        headIn.setField("ClientName_", "Web浏览器");
        headIn.setField("ClientIP_", "127.0.0.1");
        headIn.setField("wx", true);
        return this.Check();
    }

    // 判断手机号码且账号类型为5是否已存在账号
    @Webfunc
    public boolean getTelToUserCode() {
        Record headIn = getDataIn().getHead();
        String userCode = headIn.getString("UserCode_");

        Record headOut = getDataOut().getHead();
        if ("".equals(userCode)) {
            headOut.setField("Msg_", "手机号不允许为空！");
            return false;
        }

        SqlQuery ds = new SqlQuery(this);
        ds.add("select a.Code_ from %s oi ", SystemTable.get(SystemTable.getBookInfo));
        ds.add("inner join %s a on oi.CorpNo_=a.CorpNo_ and oi.Status_ in(1,2)",
                SystemTable.get(SystemTable.getUserInfo));
        ds.add("where a.Mobile_='%s' and ((a.BelongAccount_ is null) or (a.BelongAccount_=''))", userCode);
        ds.open();
        if (ds.size() == 0) {
            headOut.setField("Msg_", "您的手机号码不存在于系统中，如果您需要注册帐号，请 <a href='TFrmContact'>联系客服</a> 进行咨询");
            return false;
        }

        if (ds.size() != 1) {
            headOut.setField("Msg_",
                    String.format("您的手机绑定了多个帐号，无法登录，建议您使用主账号登陆后，在【我的账号--更改我的资料】菜单中设置主附帐号关系后再使用手机号登录！", userCode));
            return false;
        }
        headOut.setField("UserCode_", ds.getString("Code_"));
        return true;
    }

    // return 若返回值为 true，表示已校验，否则表示需要进行认证
    @Webfunc
    public boolean verifyMachine() throws SecurityCheckException {
        Record headIn = getDataIn().getHead();
        String userCode = getUserCode();
        String deviceId = headIn.getString("deviceId");
        String verifyCode = headIn.getString("verifyCode");
        SqlQuery ds = new SqlQuery(this);
        ds.add("select * from %s", SystemTable.get(SystemTable.getDeviceVerify));
        ds.add("where UserCode_='%s' and MachineCode_='%s'", userCode, deviceId);
        ds.open();

        SqlQuery dsUser = new SqlQuery(this);
        dsUser.add("select * from %s ", SystemTable.getUserInfo);
        dsUser.add("where Code_='%s' ", userCode);
        dsUser.open();
        if (dsUser.eof()) {
            throw new RuntimeException("没有找到用户帐号：" + userCode);
        }
        if (dsUser.getInt("Enabled_") < 1) {
            throw new RuntimeException("您现登录的帐号已被停止使用，请您联系客服启用后再重新登录！");
        }
        if (ds.eof()) {
            throw new RuntimeException(String.format("系统出错(id=%s)，请您重新进入系统！", deviceId));
        }

        if (ds.size() > 1) {
            while (!ds.eof()) {
                if (ds.getRecNo() == 1) {
                    ds.next();
                } else {
                    ds.delete();
                }
            }
            ds.first();
        }
        if (ds.getInt("Used_") == 2) {
            throw new SecurityCheckException("您正在使用的这台设备，被管理员设置为禁止登入系统！");
        }
        if (ds.getInt("Used_") == 1) {
            return true;
        }

        if ("".equals(verifyCode)) {
            throw new RuntimeException("校验码不允许为空!");
        }

        // 更新认证码
        if (!verifyCode.equals(ds.getString("VerifyCode_"))) {
            updateVerifyCode(ds, verifyCode, dsUser);
        }

        ds.edit();
        ds.setField("Used_", 1);
        ds.setField("FirstTime_", TDateTime.Now());
        ds.post();

        dsUser.edit();
        dsUser.setField("VerifyTimes_", 0);
        dsUser.post();
        return true;
    }

    @Webfunc
    public boolean sendVerifyCode() throws DataValidateException {
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getObject, getUserCode(), SvrUserLogin.class.getName(),
                "sendVerifyCode")) {
            if (!buff.isNull()) {
                throw new RuntimeException(String.format("请勿在  %d 分钟内重复点击获取认证码！", VerifyCodeTimeout));
            }

            Record headIn = getDataIn().getHead();
            String userCode = getUserCode();
            DataValidateException.stopRun("用户帐号不允许为空！", userCode, "");
            String deviceId = headIn.getString("deviceId");
            if ("".equals(deviceId)) {
                throw new RuntimeException("认证码不允许为空！ ");
            }

            SqlQuery ds1 = new SqlQuery(this);
            SqlQuery ds2 = new SqlQuery(this);
            ds1.add("select Mobile_ from %s ", SystemTable.get(SystemTable.getUserInfo));
            ds1.add("where Code_='%s' ", userCode);
            ds1.open();
            DataValidateException.stopRun("系统检测到该帐号还未登记过手机号，无法发送认证码到该手机上，请您联系管理员，让其开一个认证码给您登录系统！", ds1.eof());
            String mobile = ds1.getString("Mobile_");

            ds2.add("select * from %s", SystemTable.get(SystemTable.getDeviceVerify));
            ds2.add("where UserCode_='%s' and MachineCode_='%s'", userCode, deviceId);
            ds2.open();
            DataValidateException.stopRun("系统出错，请您重新进入系统！", ds2.size() != 1);

            String verifyCode;
            if (ServerConfig.getAppLevel() == ServerConfig.appTest) {
                verifyCode = "888888";
            } else {
                verifyCode = intToStr(random(900000) + 100000);
            }
            ds2.edit();
            ds2.setField("VerifyCode_", verifyCode);
            ds2.setField("DeadLine_", TDateTime.Now().incDay(1));
            ds2.post();

            // 发送认证码到手机上
            LocalService svr = new LocalService(handle, "SvrNotifyMachineVerify");
            if (svr.exec("verifyCode", verifyCode, "mobile", mobile)) {
                getDataOut().getHead().setField("Msg_", String.format("系统已将认证码发送到您尾号为 %s 的手机上，并且该认证码 %d 分钟内有效，请注意查收！",
                        mobile.substring(mobile.length() - 4, mobile.length()), VerifyCodeTimeout));
                buff.setExpires(60 * VerifyCodeTimeout);
                buff.setField("VerifyCode", verifyCode);
            } else {
                getDataOut().getHead().setField("Msg_", String.format("验证码发送失败，失败原因：%s！", svr.getMessage()));
            }

            getDataOut().getHead().setField("VerifyCode_", verifyCode);

            return true;
        }
    }

    /**
     * 
     * @return 获取用户的移动设备信息
     * @throws DataValidateException
     *             参数效验异常
     */
    public boolean getMachInfo() throws DataValidateException {
        Record headIn = getDataIn().getHead();
        String userCode = headIn.getString("UserCode_");
        DataValidateException.stopRun("用户帐号不允许为空", "".equals(userCode));

        String corpNo = headIn.getString("CorpNo_");
        DataValidateException.stopRun("用户帐套不允许为空", "".equals(corpNo));

        SqlQuery cdsTmp = new SqlQuery(this);
        cdsTmp.add("select * from %s", SystemTable.get(SystemTable.getDeviceVerify));
        cdsTmp.add("where CorpNo_='%s'and UserCode_='%s'", corpNo, userCode);
        /*
         * FIXME MachineType_代表设备类型，6-iOS、7-Android，用于极光推送 JPushRecord
         * 
         * 黄荣君 2017-06-19
         */
        cdsTmp.add("and Used_=1 and MachineType_ in (6,7)");
        cdsTmp.add("and ifnull(MachineCode_,'')<>''");
        cdsTmp.open();

        getDataOut().appendDataSet(cdsTmp);
        return true;
    }

    private void enrollMachineInfo(String corpNo, String userCode, String deviceId, String deviceName) {
        SqlQuery ds = new SqlQuery(this);
        ds.add("select * from %s", SystemTable.get(SystemTable.getDeviceVerify));
        ds.add("where UserCode_='%s' and MachineCode_='%s'", userCode, deviceId);
        ds.open();
        if (!ds.eof()) {
            return;
        }

        ds.append();
        ds.setField("CorpNo_", corpNo);
        ds.setField("UserCode_", userCode);
        ds.setField("VerifyCode_", intToStr(random(900000) + 100000));
        ds.setField("DeadLine_", TDateTime.Now().incDay(1));
        ds.setField("MachineCode_", deviceId);
        if (deviceId.startsWith("i_")) {
            // iOS
            ds.setField("MachineType_", 6);
            ds.setField("MachineName_", ClientType.IOS.toString());
        } else if (deviceId.startsWith("n_")) {
            // Android
            ds.setField("MachineType_", 7);
            ds.setField("MachineName_", ClientType.Android.toString());
        } else {
            // 系统默认
            ds.setField("MachineType_", 0);
            ds.setField("MachineName_", deviceName);
        }
        ds.setField("Remark_", "");
        ds.setField("Used_", 0);
        ds.setField("UpdateUser_", userCode);
        ds.setField("UpdateDate_", TDateTime.Now());
        ds.setField("AppUser_", userCode);
        ds.setField("AppDate_", TDateTime.Now());
        ds.setField("UpdateKey_", newGuid());
        ds.post();
    }

    private boolean isStopUsed(String userCode, String deviceId) {
        SqlQuery ds = new SqlQuery(this);
        ds.add("select * from %s ", SystemTable.get(SystemTable.getDeviceVerify));
        ds.add("where UserCode_='%s' and MachineCode_='%s' ", userCode, deviceId);
        ds.open();
        ds.edit();
        ds.setField("LastTime_", TDateTime.Now());
        ds.post();
        if (ds.getInt("Used_") == 2) {
            return false;
        }
        return true;
    }

    private String GuidFixStr(String guid) {
        String str = guid.substring(1, guid.length() - 1);
        return str.replaceAll("-", "");
    }

    private boolean isAutoLogin(String userCode, String deviceId) {
        BuildQuery bs = new BuildQuery(this);
        bs.byField("MachineCode_", deviceId);
        bs.byField("Used_", true);
        bs.byField("UserCode_", userCode);
        bs.add("select * from %s", SystemTable.get(SystemTable.getDeviceVerify));
        DataSet ds = bs.open();
        if (!ds.eof()) {
            return ds.getBoolean("AutoLogin_");
        } else {
            return false;
        }
    }

    private void updateVerifyCode(SqlQuery ds, String verifyCode, SqlQuery dsUser) {
        SqlQuery ds1 = new SqlQuery(this);
        ds1.add("select * from %s", SystemTable.get(SystemTable.getDeviceVerify));
        ds1.add("where VerifyCode_='%s'", verifyCode);
        ds1.open();
        if (ds1.eof()) {
            dsUser.edit();
            if (dsUser.getInt("VerifyTimes_") == 6) {
                // 该账号设置停用
                dsUser.setField("Enabled_", 0);
                dsUser.post();
                throw new RuntimeException("您输入验证码的错误次数已超出规定次数，现账号已被自动停用，若需启用，请您联系客服处理！");
            } else {
                dsUser.setField("VerifyTimes_", dsUser.getInt("VerifyTimes_") + 1);
                dsUser.post();
                throw new RuntimeException("没有找到验证码：" + verifyCode);
            }
        }
        if (ds1.getString("MachineCode_") == null || "".equals(ds1.getString("MachineCode_"))) {
            // 先将此认证记录删除
            ds1.delete();
            // 再将该认证码替换之前自动生成的认证码
            ds.edit();
            ds.setField("VerifyCode_", verifyCode);
            ds.post();
        } else {
            throw new RuntimeException("您输入的验证码有误，请重新输入！");
        }
    }

    private void updateCurrentUser(String computer, String screen, String language) {
        getConnection().execute(String.format(
                "Update %s Set Viability_=0 Where Viability_>0 and (TIME_TO_SEC(TIMEDIFF(LogoutTime_,now())))>%d",
                SystemTable.get(SystemTable.getCurrentUser), 3600));
        String SQLCmd = String.format("update %s set Viability_=-1 where Account_='%s' and Viability_>-1",
                SystemTable.get(SystemTable.getCurrentUser), getUserCode());
        getConnection().execute(SQLCmd);

        // 增加新的记录
        Record rs = new Record();
        rs.setField("UserID_", this.getProperty("UserID"));
        rs.setField("CorpNo_", handle.getCorpNo());
        rs.setField("Account_", getUserCode());
        rs.setField("LoginID_", this.getProperty("ID"));
        rs.setField("Computer_", computer);
        rs.setField("clientIP_", this.getProperty(Application.clientIP));
        rs.setField("LoginTime_", TDateTime.Now());
        rs.setField("ParamValue_", handle.getCorpNo());
        rs.setField("KeyCardID_", GuidNull);
        rs.setField("Viability_", intToStr(Max_Viability));
        rs.setField("LoginServer_", ServerConfig.getAppName());
        rs.setField("Screen_", screen);
        rs.setField("Language_", language);
        SqlOperator opear = new SqlOperator(this);
        opear.setTableName(SystemTable.get(SystemTable.getCurrentUser));
        opear.insert(rs);
    }

}
