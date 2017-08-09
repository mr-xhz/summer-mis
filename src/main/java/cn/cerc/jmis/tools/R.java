package cn.cerc.jmis.tools;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.cerc.jbean.core.Application;
import cn.cerc.jbean.other.SystemTable;
import cn.cerc.jdb.core.IHandle;
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

}
