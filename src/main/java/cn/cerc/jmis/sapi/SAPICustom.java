package cn.cerc.jmis.sapi;

public class SAPICustom {
    private static String host = "http://api.cerc.cn";
    private Object data;
    private String message;

    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        SAPICustom.host = host;
    }

    public Object getData() {
        return data;
    }

    protected void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    protected void setMessage(String message) {
        this.message = message;
    }

}
