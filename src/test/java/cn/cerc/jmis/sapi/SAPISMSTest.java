package cn.cerc.jmis.sapi;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

public class SAPISMSTest {
    private SAPISMS api = new SAPISMS();

    @Test
    @Ignore
    public void testSendSMSByMobile() {
        boolean result = api.sendSMSByMobile("13828832477", "000001", "000000");
        System.out.println(api.getMessage());
        assertTrue("简讯发送失败", result);
    }

}
