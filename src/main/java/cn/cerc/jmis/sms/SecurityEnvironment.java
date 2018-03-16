package cn.cerc.jmis.sms;

import org.apache.log4j.Logger;

import cn.cerc.jbean.core.AbstractHandle;
import cn.cerc.jbean.core.DataValidateException;
import cn.cerc.jbean.form.IForm;
import cn.cerc.jbean.other.SystemTable;
import cn.cerc.jdb.core.TDateTime;
import cn.cerc.jdb.mysql.SqlQuery;
import cn.cerc.jmis.form.AbstractForm;
import cn.cerc.jmis.language.R;
import cn.cerc.jmis.page.AbstractJspPage;

public class SecurityEnvironment {
    private static final Logger log = Logger.getLogger(SecurityEnvironment.class);

    /**
     * 用于Form中，向UI(jsp)传递当前是否安全，若不安全则显示输入验证码画面
     * 
     * @param jspPage
     */
    public static boolean check(AbstractJspPage jspPage) {
        AbstractForm form = (AbstractForm) jspPage.getForm();
        boolean result = isSecurity(form);
        if (form.getRequest().getParameter("checkSecurity") != null) {
            try {
                safetyCheck(form);
                result = true;
            } catch (DataValidateException e) {
                jspPage.add("message", e.getMessage());
                result = false;
            }
        }
        if (result) {
            jspPage.add("securityEnvironment", true);
        } else {
            jspPage.setJspFile("common/SecurityVerify.jsp");
        }
        return result;
    }

    /**
     * 后台环境安全检测
     */
    public static boolean backCheck(AbstractJspPage jspPage) {
        AbstractForm form = (AbstractForm) jspPage.getForm();
        boolean result = isSecurity(form);
        if (form.getRequest().getParameter("checkSecurity") != null) {
            try {
                safetyCheck(form);
                result = true;
            } catch (DataValidateException e) {
                jspPage.add("message", e.getMessage());
                result = false;
            }
        }
        if (result) {
            jspPage.add("securityEnvironment", true);
        } else {
            jspPage.setJspFile("common/SecurityVerify-back.jsp");
        }
        return result;
    }

    private static boolean isSecurity(AbstractForm form) {
        String remoteIP = RemoteIP.get(form);
        String clientId = form.getClient().getId();
        String userId = form.getHandle().getUserCode();
        log.debug(String.format("ip: %s, clientId:%s, userId: %s", remoteIP, clientId, userId));

        String mobile = getUserSecuirtyMobile(form);
        if ("".equals(mobile)) {
            return false;

        }

        SqlQuery ds2 = new SqlQuery(form.getHandle());
        ds2.add("select * from %s", SystemTable.getSecurityMobile);
        ds2.add("where mobile_='%s'", mobile);
        ds2.open();
        if (ds2.eof()) {
            return false;
        }
        if (!clientId.equals(ds2.getString("clientId_"))) {
            return false;
        }
        if (!remoteIP.equals(ds2.getString("remoteIP_"))) {
            return false;
        }
        // 临时关闭帐号不同需要校验的功能
        // if (!userId.equals(ds2.getString("updateUser_"))) {
        // return false;
        // }
        return true;
    }

    /**
     * 用于Service中，检查若当前环境不安全时，需要检查 验证码是否正确
     * 
     * @param service
     * @throws DataValidateException
     */
    public static void check(AbstractHandle service) throws DataValidateException {
        if (!(service.getHandle() instanceof AbstractForm)) {
            log.error("程序调用错误，需要修正！");
            DataValidateException.stopRun(R.asString(service, "程序调用错误，需要修正！"), true);
        }
        AbstractForm form = (AbstractForm) service.getHandle();
        if (isSecurity(form)) {
            return;
        }
        safetyCheck(form);
    }

    /**
     * 校验短信验证码
     * 
     * @param form
     * @throws DataValidateException
     */
    private static void safetyCheck(AbstractForm form) throws DataValidateException {
        String securityCode = form.getRequest().getParameter("securityCode");
        if (securityCode == null) {
            DataValidateException.stopRun(R.asString(form, "关键操作，请输入安全手机的验证码"), true);
        }
        PhoneVerify mv = new PhoneVerify(form).init();
        String mobile = mv.getMobile();
        switch (mv.checkVerify(securityCode)) {
        case PASS:
            if (!"".equals(mobile)) {
                updateSecurityRecord(mobile, form, false);
            }
            break;
        case DIFFERENCE:
            if (!"".equals(mobile)) {
                updateSecurityRecord(mobile, form, true);
            }
            DataValidateException.stopRun(R.asString(form, "验证码输入有误，请检查"), true);
            break;
        case ERROR:
            DataValidateException.stopRun(mv.getMessage(), true);
            break;
        }
    }

    public static void updateSecurityRecord(String mobile, AbstractForm form, boolean hasError) {
        if ("".equals(mobile)) {
            return;
        }
        if (!mobile.startsWith("+")) {
            SqlQuery ds = new SqlQuery(form.getHandle());
            ds.add("select countryCode_ from %s", SystemTable.getUserInfo);
            ds.add("where mobile_='%s'", mobile);
            ds.open();
            if (ds.eof()) {
                return;
            }
            mobile = ds.getString("countryCode_") + mobile;
        }

        SqlQuery ds = new SqlQuery(form.getHandle());
        ds.add("select * from %s", SystemTable.getSecurityMobile);
        ds.add("where mobile_='%s'", mobile);
        ds.open();
        if (ds.eof()) {
            if (hasError) {
                return;
            }
            ds.append();
            ds.setField("mobile_", mobile);
            ds.setField("clientId_", form.getClient().getId());
            ds.setField("remoteIP_", RemoteIP.get(form));
            ds.setField("errorCount_", 0);
            ds.setField("userMax_", 20);
            ds.setField("userCount_", 1);
            ds.setField("updateUser_", form.getHandle().getUserCode());
            ds.setField("updateDate_", TDateTime.Now());
            ds.setField("createUser_", form.getHandle().getUserCode());
            ds.setField("createDate_", TDateTime.Now());
            ds.post();
        } else {
            ds.edit();
            if (hasError) {
                ds.setField("errorCount_", ds.getInt("errorCount_") + 1);
            } else {
                ds.setField("clientId_", form.getClient().getId());
                ds.setField("remoteIP_", RemoteIP.get(form));
                ds.setField("updateUser_", form.getHandle().getUserCode());
            }
            ds.setField("updateDate_", TDateTime.Now());
            ds.post();
        }
    }

    public static String getUserSecuirtyMobile(IForm form) {
        SqlQuery ds1 = new SqlQuery(form.getHandle());
        ds1.add("SELECT mobile_,securityMobile_,countryCode_ FROM %s", SystemTable.getUserInfo);
        ds1.add("WHERE id_='%s'", form.getHandle().getUserCode());
        ds1.open();
        if (ds1.eof()) {
            log.error(String.format("userCode %s 找不到", form.getHandle().getUserCode()));
            return "";
        }
        String mobile = ds1.getString("mobile_");
        if (!"".equals(ds1.getString("securityMobile_"))) {
            mobile = ds1.getString("securityMobile_");
        } else {
            mobile = ds1.getString("countryCode_") + mobile;
        }
        return mobile;
    }

}
