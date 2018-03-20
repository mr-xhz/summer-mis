package cn.cerc.jmis.sapi;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class SAPISecurityTest {
    private SAPISecurity api;

    @Before
    public void setUp() {
        SAPISecurity.setHost("http://127.0.0.1");
        api = new SAPISecurity();
    }

    @Test
    public void testRegister() {
        boolean result = api.register("jason", "13812345678");
        System.out.println(api.getMessage());
        assertTrue(result);
    }

    @Test
    public void testIsSecurity() {
        boolean result = api.isSecurity("jason", "127.0.0.1", "abcd");
        System.out.println(api.getMessage());
        assertTrue(result);
    }

    @Test
    public void testSendVerify() {
        boolean result = api.sendVerify("jason", "127.0.0.1", "abcd");
        System.out.println(api.getMessage());
        assertTrue(result);
    }

    @Test
    public void testCheckVerify() {
        boolean result = api.checkVerify("jason", "246882");
        System.out.println(api.getMessage());
        assertTrue(result);
    }

}
