package cn.cerc.jmis.tools;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class DirectoryTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    @Ignore
    public void test() {
        Directory dir = new Directory();
        dir.setOnFilter(file -> {
            // 列出所有的java文件
            return file.getName().endsWith(".java");
        });

        if (dir.list("d:\\temp") > 0) {
            for (String item : dir.getPaths()) {
                System.out.println(item);
            }
            for (String item : dir.getFiles()) {
                System.out.println(item);
            }
        } else {
            System.out.println("没有找到任何目录与文件");
        }
    }

}
