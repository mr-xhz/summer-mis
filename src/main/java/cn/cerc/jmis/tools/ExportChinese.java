package cn.cerc.jmis.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.jbean.other.SystemTable;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.mysql.SqlQuery;

public class ExportChinese {
    private static final Logger log = LoggerFactory.getLogger(ExportChinese.class);
    private List<String> items = new ArrayList<>();

    // 添加到数据库
    public void scanFile(String srcPath) {
        // 调用查找文件方法
        List<File> ll = getFiles(new File(srcPath), "java");
        // 循环出文件
        for (File ff : ll) {
            // 再查找java文件中的字符串
            FileReader fr = null;
            BufferedReader br = null;
            String temp = "";
            try {
                // 输入流
                fr = new FileReader(ff);
                br = new BufferedReader(fr);
                log.info(ff.getName());
                // 按行读取
                while ((temp = br.readLine()) != null) {
                    String subStr = getChinese(temp);
                    if (subStr != null) {
                        log.info(subStr);
                        items.add(subStr);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fr.close();
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void writeDict(IHandle handle) {
        SqlQuery ds = new SqlQuery(handle);
        ds.add("select * from %s", SystemTable.getLangDict);
        ds.open();
        for (String text : this.getItems()) {
            if (!ds.locate("cn_", text)) {
                ds.append();
                ds.setField("cn_", text);
                ds.post();
            }
        }
    }

    public List<String> getItems() {
        return this.items;
    }

    // 查找文件
    private List<File> getFiles(File fileDir, String fileType) {
        List<File> lfile = new ArrayList<File>();
        File[] fs = fileDir.listFiles();
        for (File f : fs) {
            if (f.isFile()) {
                if (fileType.equals(f.getName().substring(f.getName().lastIndexOf(".") + 1, f.getName().length())))
                    lfile.add(f);
            } else {
                List<File> ftemps = getFiles(f, fileType);
                lfile.addAll(ftemps);
            }
        }
        return lfile;
    }

    private static String getChinese(String temp) {
        int ix = temp.indexOf("R.asString");
        if (ix > -1) {
            String s1 = temp.substring(ix, temp.length());
            if (s1.indexOf("\"") > -1) {
                String s2 = s1.substring(s1.indexOf("\"") + 1, s1.length());
                if (s2.indexOf("\")") > -1) {
                    String s3 = s2.substring(0, s2.indexOf("\")"));
                    if (s3.length() > 0)
                        return s3;
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        ExportChinese ec = new ExportChinese();
        // 扫描指定目录下所有的java文件
        ec.scanFile("F:\\summer/mojinpai/src/main/java");
        // 将扫描的结果存入到数据库
        // ec.writeDict(new AppHandle());
    }

}
