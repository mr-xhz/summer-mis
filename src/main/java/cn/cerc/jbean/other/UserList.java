package cn.cerc.jbean.other;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import cn.cerc.jdb.cache.Buffer;
import cn.cerc.jdb.cache.IMemcache;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.mysql.SqlQuery;
import cn.cerc.jdb.mysql.SqlSession;

public class UserList implements IDataList {
    private static final Logger log = LoggerFactory.getLogger(UserList.class);
    private IHandle handle;
    private Map<String, UserRecord> buff = new HashMap<>();
    private String buffKey;
    private static final int Version = 4;

    public UserList(IHandle handle) {
        super();
        this.handle = handle;
        if (handle != null)
            buffKey = String.format("%d.%s.%s.%d", BufferType.getObject.ordinal(), handle.getCorpNo(),
                    this.getClass().getName(), Version);
    }

    public String getNameDef(String key) {
        // 不允许用户帐号为空
        if (key == null || "".equals(key))
            return "";

        // 初始化缓存
        this.init();

        // 从缓存中取回值
        UserRecord result = buff.get(key);
        return result == null ? key : result.getName();
    }

    public UserRecord get(String userCode) {
        if (userCode == null || "".equals(userCode))
            throw new RuntimeException("用户代码不允许为空！");

        // 初始化缓存
        this.init();

        // 从缓存中取回值
        return buff.get(userCode);
    }

    private void init() {
        if (buff.size() > 0)
            return;

        // 从缓存中读取
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        IMemcache cache = Buffer.getMemcache();
        String data = (String) cache.get(buffKey);
        if (data != null && !"".equals(data)) {
            Type type = new TypeToken<Map<String, UserRecord>>() {
            }.getType();
            Map<String, UserRecord> items = gson.fromJson(data, type);
            for (String key : items.keySet()) {
                buff.put(key, items.get(key));
            }
            log.debug(this.getClass().getName() + " 缓存成功！");
            return;
        }

        // 从数据库中读取
        SqlQuery ds = new SqlQuery(handle);
        ds.add("select ID_,CorpNo_,Code_,Name_,QQ_,Mobile_,SuperUser_,");
        ds.add("LastRemindDate_,EmailAddress_,RoleCode_,ProxyUsers_,Enabled_,DiyRole_ ");
        ds.add("from %s ", SystemTable.get(SystemTable.getUserInfo));
        ds.add("where CorpNo_='%s'", handle.getCorpNo());
        ds.open();
        while (ds.fetch()) {
            String key = ds.getString("Code_");
            UserRecord value = new UserRecord();

            value.setId(ds.getString("ID_"));
            value.setCorpNo(ds.getString("CorpNo_"));
            value.setCode(ds.getString("Code_"));
            value.setName(ds.getString("Name_"));
            Map<String, Integer> priceValue = getPriceValue(ds.getString("Code_"));
            value.setShowInUP(priceValue.get(UserOptions.ShowInUP));
            value.setShowOutUP(priceValue.get(UserOptions.ShowOutUP));
            value.setShowWholesaleUP(priceValue.get(UserOptions.ShowWholesaleUP));
            value.setShowBottomUP(priceValue.get(UserOptions.ShowBottomUP));

            value.setQq(ds.getString("QQ_"));
            value.setMobile(ds.getString("Mobile_"));
            value.setAdmin(ds.getBoolean("SuperUser_"));
            value.setLastRemindDate(ds.getDateTime("LastRemindDate_").getDate());
            value.setEmail(ds.getString("EmailAddress_"));
            if (ds.getBoolean("DiyRole_"))
                value.setRoleCode(ds.getString("Code_"));
            else
                value.setRoleCode(ds.getString("RoleCode_"));
            value.setProxyUsers(ds.getString("ProxyUsers_"));
            value.setEnabled(ds.getBoolean("Enabled_"));
            buff.put(key, value);
        }

        // 存入到缓存中
        cache.set(buffKey, gson.toJson(buff));
        log.debug(this.getClass().getName() + " 缓存初始化！");
    }

    private Map<String, Integer> getPriceValue(String userCode) {
        Map<String, Integer> value = new HashMap<>();
        value.put(UserOptions.ShowInUP, 0);
        value.put(UserOptions.ShowOutUP, 0);
        value.put(UserOptions.ShowWholesaleUP, 0);
        value.put(UserOptions.ShowBottomUP, 0);
        SqlQuery ds = new SqlQuery(handle);

        ds.add("select Code_,Value_ from %s ", SystemTable.get(SystemTable.getUserOptions));
        ds.add("where UserCode_='%s' and (Code_='%s' or Code_='%s' or Code_='%s' or Code_='%s')", userCode,
                UserOptions.ShowInUP, UserOptions.ShowOutUP, UserOptions.ShowWholesaleUP, UserOptions.ShowBottomUP);
        ds.open();
        while (ds.fetch())
            value.put(ds.getString("Code_"), ds.getInt("Value_"));

        return value;
    }

    @Override
    public boolean exists(String key) {
        this.init();
        return buff.get(key) != null;
    }

    @Override
    public void clear() {
        IMemcache cache = Buffer.getMemcache();
        cache.delete(buffKey);
    }

    /*
     * 切换帐号到指定的公司别
     */
    public void changeCorpNo(IHandle handle, String corpNo, String userCode, String roleCode)
            throws UserNotFindException {
        String buffKey = String.format("%d.%s.%s.%d", BufferType.getObject.ordinal(), corpNo, this.getClass().getName(),
                Version);
        Buffer.getMemcache().delete(buffKey);
        SqlQuery ds = new SqlQuery(handle);

        ds.add("select ID_ from %s where Code_='%s'", SystemTable.get(SystemTable.getUserInfo), userCode);
        ds.open();
        if (ds.eof())
            throw new UserNotFindException(userCode);

        SqlSession conn = (SqlSession) handle.getProperty(SqlSession.sessionId);
        String sql = String.format("update %s set CorpNo_='%s',ShareAccount_=1 where Code_='%s'",
                SystemTable.get(SystemTable.getUserInfo), corpNo, userCode);
        conn.execute(sql);

        sql = String.format("update %s set Name_='%s' where UserCode_='%s' and Code_='GroupCode'",
                SystemTable.get(SystemTable.getUserOptions), roleCode, userCode);
        conn.execute(sql);

        log.info(String.format("%s 已被切换到 corpNo=%s, roleCode=%s", userCode, corpNo, roleCode));
    }

}
