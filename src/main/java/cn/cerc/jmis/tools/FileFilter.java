package cn.cerc.jmis.tools;

import java.io.File;

public interface FileFilter {
    boolean check(File file);
}
