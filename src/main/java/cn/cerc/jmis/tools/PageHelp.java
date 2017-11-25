package cn.cerc.jmis.tools;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import cn.cerc.jpage.core.Component;
import cn.cerc.jui.parts.UIToolHelp;

public class PageHelp {
    private static ApplicationContext app;
    private static String xmlFile = "classpath:page-help.xml";

    public static UIToolHelp get(Component owner, String beanId) {
        if (app == null)
            app = new FileSystemXmlApplicationContext(xmlFile);
        if (!app.containsBean(beanId))
            return null;
        UIToolHelp side = app.getBean(beanId, UIToolHelp.class);
        side.setOwner(owner);
        return side;
    }

    public static void main(String[] args) {
        UIToolHelp help = get(null, "TFrmTranBG");
        System.out.println(help.toString());
    }
}
