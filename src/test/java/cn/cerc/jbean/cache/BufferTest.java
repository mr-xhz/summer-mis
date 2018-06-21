package cn.cerc.jbean.cache;

import org.junit.Test;

import cn.cerc.jdb.cache.Buffer;

public class BufferTest {

    @Test
    // Ignore
    public void test() {
        Buffer buff = new Buffer("key");
        if (buff.isNull()) {
            System.out.println("key not exists.");
            buff.setField("num", 1);
        } else {
            System.out.println("key exists.");
            buff.setField("num", buff.getInt("num") + 1);
        }
        buff.post();
        System.out.println(buff);
    }

}
