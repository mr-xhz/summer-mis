package cn.cerc.jmis.sms;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class SeucirtyAPITest {
    private SeucirtyAPI api;

    @Before
    public void setUp() {
        SeucirtyAPI.setHost("http://127.0.0.1");
        api = new SeucirtyAPI();
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
        boolean result = api.sendVerify("jason");
        System.out.println(api.getMessage());
        assertTrue(result);
    }

    @Test
    public void testCheckVerify() {
        boolean result = api.checkVerify("jason", "246882", "127.0.0.1", "abcd");
        System.out.println(api.getMessage());
        assertTrue(result);
    }

}
