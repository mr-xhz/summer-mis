package cn.cerc.jmis.sms;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

public class YunpianSMSTest {
    private static final Logger log = Logger.getLogger(YunpianSMSTest.class);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() {
        YunpianSMS sms = new YunpianSMS("13826575465");
        String text = "【MIUGROUP】您的验证码是123456";
        if (sms.sendText(text)) {
            log.info("ok: ");
            log.info(sms.getMessage());
        } else {
            log.info("error: ");
            log.info(sms.getMessage());
        }
    }

}
