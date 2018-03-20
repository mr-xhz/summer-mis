package cn.cerc.jmis.sapi;

import java.util.HashMap;
import java.util.Map;

import cn.cerc.jbean.tools.CURL;
import net.sf.json.JSONObject;

public class SAPISecurity extends SAPICustom {

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
        String result = CURL.doPost(String.format("%s/forms/security.register", getHost()), params, "UTF-8");
        JSONObject json = JSONObject.fromObject(result);
        if (json.has("result")) {
            this.setMessage(json.getString("message"));
            return json.getBoolean("result");
        } else {
            this.setMessage(result);
            return false;
        }
    }

    /**
     * 检测用户访问IP或者设备是否是安全
     * 
     * @param user
     *            访问IP
     * @param remoteIP
     *            调用者ip
     * @param deviceId
     *            设备号
     * @return true:成功，若失败可用getMessage取得错误信息
     */
    public boolean isSecurity(String user, String remoteIP, String deviceId) {
        Map<String, String> params = new HashMap<>();
        params.put("user", user);
        params.put("ip", remoteIP);
        params.put("deviceId", deviceId);
        String result = CURL.doPost(String.format("%s/forms/security.isSecurity", getHost()), params, "UTF-8");
        JSONObject json = JSONObject.fromObject(result);
        if (json.has("result")) {
            this.setMessage(json.getString("message"));
            return json.getBoolean("result");
        } else {
            this.setMessage(result);
            return false;
        }
    }

    /**
     * 发送验证码
     * 
     * @param user
     *            用户账号
     * @param remoteIP
     *            调用者ip
     * @param deviceId
     *            设备号
     * @return true 成功，若失败可用getMessage取得错误信息
     */
    public boolean sendVerify(String user, String remoteIP, String deviceId) {
        Map<String, String> params = new HashMap<>();
        params.put("user", user);
        params.put("ip", remoteIP);
        params.put("deviceId", deviceId);
        String result = CURL.doPost(String.format("%s/forms/security.sendVerify", getHost()), params, "UTF-8");
        JSONObject json = JSONObject.fromObject(result);
        if (json.has("result")) {
            this.setMessage(json.getString("message"));
            return json.getBoolean("result");
        } else {
            this.setMessage(result);
            return false;
        }
    }

    /**
     * 检测验证码
     * 
     * @param user
     *            用户账号
     * @param verifyCode
     *            验证码
     * @return true:成功，若失败可用getMessage取得错误信息
     */
    public boolean checkVerify(String user, String verifyCode) {
        Map<String, String> params = new HashMap<>();
        params.put("user", user);
        params.put("verifyCode", verifyCode);
        String result = CURL.doPost(String.format("%s/forms/security.checkVerify", getHost()), params, "UTF-8");
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
