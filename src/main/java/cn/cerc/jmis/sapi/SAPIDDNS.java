package cn.cerc.jmis.sapi;

import java.util.HashMap;
import java.util.Map;

import cn.cerc.jbean.tools.CURL;
import net.sf.json.JSONObject;

public class SAPIDDNS extends SAPICustom {

    /**
     * 根据 token 获取最靠近调用者的主机IP
     * 
     * @param token
     *            聚安应用令牌
     * @param remoteIP
     *            调用者ip
     * @return 调用成功时返回 true
     */
    public boolean getIP(String token, String remoteIP) {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("remoteIP", remoteIP);
        String result = CURL.doPost(String.format("%s/forms/ddns.getIP", getHost()), params, "UTF-8");
        JSONObject json = JSONObject.fromObject(result);
        if (json.has("result")) {
            this.setMessage(json.getString("message"));
            return json.getBoolean("result");
        } else {
            this.setMessage(result);
            return false;
        }
    }

}
