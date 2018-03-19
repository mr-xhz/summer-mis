package cn.cerc.jmis.sms;

import java.util.HashMap;
import java.util.Map;

import cn.cerc.jbean.tools.CURL;
import net.sf.json.JSONObject;

public class SecurityAPI {
    private static String host = "http://api.cerc.cn";
    private Object data;

    private String message;

    /**
     * 向聚安平台注册用户所关联手机号
     * 
     * @param user:用户账号
     * @param mobile:手机号
     * @return true:成功，若失败可用getMessage取得错误信息
     */
    public boolean register(String user, String mobile) {
        Map<String, String> params = new HashMap<>();
        params.put("user", user);
        params.put("mobile", mobile);
        String result = CURL.doPost(String.format("%s/forms/FrmSecurity.register", host), params, "UTF-8");
        JSONObject json = JSONObject.fromObject(result);
        if (json.has("result")) {
            this.message = json.getString("message");
            return json.getBoolean("result");
        } else {
            this.message = result;
            return false;
        }
    }

    /**
     * 检测用户访问IP或者设备是否是安全
     * 
     * @param user:访问IP
     * @param deviceId:设备号
     * @return true:成功，若失败可用getMessage取得错误信息
     */
    public boolean isSecurity(String user, String remoteIP, String deviceId) {
        Map<String, String> params = new HashMap<>();
        params.put("user", user);
        params.put("remoteIP", remoteIP);
        params.put("deviceId", deviceId);
        String result = CURL.doPost(String.format("%s/forms/FrmSecurity.isSecurity", host), params, "UTF-8");
        JSONObject json = JSONObject.fromObject(result);
        if (json.has("result")) {
            this.message = json.getString("message");
            return json.getBoolean("result");
        } else {
            this.message = result;
            return false;
        }
    }

    /**
     * 发送验证码
     * 
     * @param user:用户账号
     * @return true:成功，若失败可用getMessage取得错误信息
     */
    public boolean sendVerify(String user, String remoteIP, String deviceId) {
        Map<String, String> params = new HashMap<>();
        params.put("user", user);
        params.put("remoteIP", remoteIP);
        params.put("deviceId", deviceId);
        String result = CURL.doPost(String.format("%s/forms/FrmSecurity.sendVerify", host), params, "UTF-8");
        JSONObject json = JSONObject.fromObject(result);
        if (json.has("result")) {
            this.message = json.getString("message");
            return json.getBoolean("result");
        } else {
            this.message = result;
            return false;
        }
    }

    /**
     * 检测验证码
     * 
     * @param user:用户账号
     * @param verifyCode:验证码
     * @param remoteIP:访问IP
     * @param deviceId:设备号
     * @return true:成功，若失败可用getMessage取得错误信息
     */
    public boolean checkVerify(String user, String verifyCode) {
        Map<String, String> params = new HashMap<>();
        params.put("user", user);
        params.put("verifyCode", verifyCode);
        String result = CURL.doPost(String.format("%s/forms/FrmSecurity.checkVerify", host), params, "UTF-8");
        JSONObject json = JSONObject.fromObject(result);
        if (json.has("result")) {
            this.message = json.getString("message");
            return json.getBoolean("result");
        } else {
            this.message = result;
            return false;
        }
    }

    public boolean getHostIP(String token, String remoteIP) {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("remoteIP", remoteIP);
        String result = CURL.doPost(String.format("%s/forms/FrmSecurity.getHostIP", host), params, "UTF-8");
        JSONObject json = JSONObject.fromObject(result);
        if (json.has("result")) {
            this.message = json.getString("message");
            return json.getBoolean("result");
        } else {
            this.message = result;
            return false;
        }
    }

    public boolean sendSMSByUser(String user, String templateId, String... args) {
        return sendSMS(true, user, templateId, args);
    }

    public boolean sendSMSByMobile(String mobile, String templateId, String... args) {
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
        String result = CURL.doPost(String.format("%s/forms/FrmSecurity.sendSMS", host), params, "UTF-8");
        JSONObject json = JSONObject.fromObject(result);
        if (json.has("result")) {
            this.message = json.getString("message");
            return json.getBoolean("result");
        } else {
            this.message = result;
            return false;
        }
    }

    public String getMessage() {
        return message;
    }

    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        SecurityAPI.host = host;
    }

    public Object getData() {
        return data;
    }
}
