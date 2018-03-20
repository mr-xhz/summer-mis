package cn.cerc.jmis.sapi;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

public class SAPISecurityTest {
    private SAPISecurity api = new SAPISecurity();

    @Test
    @Ignore
    public void testRegister() {
        boolean result = api.register("jason", "13812345678");
        System.out.println(api.getMessage());
        assertTrue(result);
    }

    @Test
    @Ignore
    public void testIsSecurity() {
        boolean result = api.isSecurity("jason", "127.0.0.1", "abcd");
        System.out.println(api.getMessage());
        assertTrue(result);
    }

    @Test
    @Ignore
    public void testSendVerify() {
        boolean result = api.sendVerify("jason", "127.0.0.1", "abcd");
        System.out.println(api.getMessage());
        assertTrue(result);
    }

    @Test
    @Ignore
    public void testCheckVerify() {
        boolean result = api.checkVerify("jason", "246882");
        System.out.println(api.getMessage());
        assertTrue(result);
    }

}
