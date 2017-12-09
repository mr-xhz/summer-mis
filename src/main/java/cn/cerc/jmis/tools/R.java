package cn.cerc.jmis.tools;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.cerc.jbean.core.Application;
import cn.cerc.jbean.other.SystemTable;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.core.TDateTime;
import cn.cerc.jdb.core.Utils;
import cn.cerc.jdb.mysql.SqlQuery;

public class R {
    private static final Logger log = Logger.getLogger(R.class);

    public static String getLanguage(IHandle handle) {
        Object temp = handle.getProperty(Application.deviceLanguage);
        if (temp == null || "".equals(temp)) {
            log.info("handle langage is null");
            Object request = handle.getProperty("request");
            if (request != null) {
                log.info(request.getClass().getName());
                if (request instanceof HttpServletRequest) {
                    HttpServletRequest req = (HttpServletRequest) request;
                    temp = req.getSession().getAttribute(Application.deviceLanguage);
                    log.info("session langage value " + temp);
                }
            }
        }
        String langage = temp == null ? Application.getLangage() : (String) temp;
        log.info("application langage: " + langage);
        return langage;
    }

    public static String asString(IHandle handle, String text) {
        String langage = getLanguage(handle);
        if (Application.LangageDefault.equals(langage))
            return text;

        // 处理英文界面
        if ("en".equals(langage)) {
            SqlQuery ds = new SqlQuery(handle);
            ds.add("select en_ from %s", SystemTable.getLangDict);
            ds.add("where cn_='%s'", Utils.safeString(text));
            ds.open();
            if (ds.eof()) {
                ds.append();
                ds.setField("cn_", text);
                ds.post();
                return text;
            }
            String result = ds.getString("en_");
            return result.length() > 0 ? result : text;
        }

        return text;
    }

    public static String get(IHandle handle, String text) {
        String language = getLanguage(handle);
        if ("cn".equals(language))
            return text;

        // 处理英文界面
        SqlQuery ds = new SqlQuery(handle);
        ds.add("select value_ from %s", SystemTable.getLanguage);
        ds.add("where key_='%s'", Utils.safeString(text));
        if (!"en".equals(language)) {
            ds.add("and (lang_='en' or lang_='%s')", language);
            ds.add("order by value_ desc");
        } else {
            ds.add("and lang_='en'", language);
        }
        ds.open();
        if (ds.eof()) {
            ds.append();
            ds.setField("key_", text);
            ds.setField("lang_", language);
            ds.setField("value_", "");
            ds.setField("updateUser_", handle.getUserCode());
            ds.setField("updateTime_", TDateTime.Now());
            ds.setField("createUser_", handle.getUserCode());
            ds.setField("createTime_", TDateTime.Now());
            ds.post();
            return text;
        }
        String result = "";
        String en_result = ""; // 默认英文
        while (ds.fetch()) {
            if ("en".equals(ds.getString("lang_")))
                en_result = ds.getString("value_");
            else
                result = ds.getString("value_");
        }
        if (!"".equals(result)) {
            return result;
        }
        if (!"".equals(en_result)) {
            return en_result;
        }
        return text;
    }
}
