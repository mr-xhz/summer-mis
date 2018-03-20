package cn.cerc.jmis.sapi;

import java.util.HashMap;
import java.util.Map;

import cn.cerc.jbean.tools.CURL;
import net.sf.json.JSONObject;

public class SAPISMS extends SAPICustom {

    public boolean sendToUser(String user, String templateId, String... args) {
        return sendSMS(true, user, templateId, args);
    }

    public boolean sendToMobile(String mobile, String templateId, String... args) {
        return sendSMS(false, mobile, templateId, args);
    }

    private boolean sendSMS(boolean isUser, String target, String templateId, String... args) {
        Map<String, String> params = new HashMap<>();
        params.put(isUser ? "user" : "mobile", target);
        if (templateId != null) {
            params.put("templateId", templateId);
        } else {
            if (args.length != 1)
                throw new RuntimeException("args size error.");
        }
        for (int i = 0; i < args.length; i++)
            params.put("arg" + i, args[0]);
        String result = CURL.doPost(String.format("%s/forms/sms.send", getHost()), params, "UTF-8");
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
