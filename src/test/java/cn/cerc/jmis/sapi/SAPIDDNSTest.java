package cn.cerc.jmis.sapi;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

public class SAPIDDNSTest {
    private SAPIDDNS api = new SAPIDDNS();

    @Test
    @Ignore
    public void testGetIP() {
       api.getIP("token", "127.0.0.2");
       assertEquals(api.getMessage(), ""+ api.getData(), "127.0.0.1");
    }

}
