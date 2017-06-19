package cn.cerc.jmis.tools;

import org.apache.log4j.Logger;

import cn.cerc.jbean.core.Application;
import cn.cerc.jbean.other.SystemTable;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.core.Utils;
import cn.cerc.jdb.mysql.SqlQuery;

public class R {
	private static final Logger log = Logger.getLogger(R.class);
	private static String langage = null;

	public static String asString(IHandle handle, String text) {
		if (langage == null) {
			langage = Application.getLangage();
			log.info("application current langage: " + langage);
		}

		if (Application.defaultLangage.equals(langage))
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
