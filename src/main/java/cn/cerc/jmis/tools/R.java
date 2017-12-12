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
        String language = getLanguage(handle);
        if (Application.LangageDefault.equals(language))
            return text;

        if (text == null || "".equals(text.trim())) {
            log.error("字符串为空");
            return "file error";
        }

        if (text.length() > 150) {
            log.error("字符串长度超过150，key:" + text);
            return text;
        }
        // 校验key
        validateKey(handle, text, language);
        // 将翻译内容返回前台
        // TODO 添加前缀，发布时需去掉前缀，lyy - 2017-12-12
        return language + ":" + getValue(handle, text, language);
    }

    private static void validateKey(IHandle handle, String text, String language) {
        SqlQuery ds1 = new SqlQuery(handle);
        ds1.add("select value_ from %s", SystemTable.getLanguage);
        ds1.add("where key_='%s'", Utils.safeString(text));
        ds1.add("and lang_='%s'", language);
        ds1.open();
        if (ds1.eof()) {
            ds1.append();
            ds1.setField("key_", Utils.safeString(text));
            ds1.setField("lang_", language);
            ds1.setField("value_", "");
            ds1.setField("supportAndroid_", false);
            ds1.setField("supportIphone_", false);
            ds1.setField("enable_", true);
            ds1.setField("createDate_", TDateTime.Now());
            ds1.setField("createUser_", handle.getUserCode());
            ds1.setField("updateDate_", TDateTime.Now());
            ds1.setField("updateUser_", handle.getUserCode());
            ds1.post();
        }
    }

    private static String getValue(IHandle handle, String text, String language) {
        SqlQuery ds2 = new SqlQuery(handle);
        ds2.add("select key_,max(value_) as value from %s", SystemTable.getLanguage);
        ds2.add("where key_='%s'", Utils.safeString(text));
        if ("en".equals(language)) {
            ds2.add("and (lang_='%s')", language);
        } else {
            ds2.add("and (lang_='%s' or lang_='en')", language);
        }
        ds2.add("group by key_");
        ds2.open();
        String result = ds2.getString("value_");
        return result.length() > 0 ? result : text;
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
