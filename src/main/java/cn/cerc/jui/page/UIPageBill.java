package cn.cerc.jui.page;

import static cn.cerc.jmis.core.ClientDevice.device_ee;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import cn.cerc.jbean.core.Application;
import cn.cerc.jbean.core.CustomHandle;
import cn.cerc.jbean.form.IForm;
import cn.cerc.jbean.rds.PassportRecord;
import cn.cerc.jmis.form.AbstractForm;
import cn.cerc.jmis.page.AbstractJspPage;
import cn.cerc.jmis.page.ExportFile;
import cn.cerc.jmis.page.IMenuBar;
import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.UrlRecord;
import cn.cerc.jui.parts.MainMenu;
import cn.cerc.jui.parts.RightMenus;
import cn.cerc.jui.parts.UIFormHorizontal;
import cn.cerc.jui.parts.UIFormVertical;

/**
 * 主体子页面
 * 
 * @author 张弓
 *
 */
public class UIPageBill extends AbstractJspPage {
    private String searchWaitingId = "";

    public UIPageBill(IForm form) {
        super(form);
        this.addCssFile("css/summer.css");
        this.addCssFile("css/summer-pc.css");
        this.addScriptFile("js/jquery.js");
        this.addScriptFile("js/summer.js");
        this.addScriptFile("js/myapp.js");
    }

    public void addExportFile(String service, String key) {
        if (device_ee.equals(this.getForm().getClient().getDevice())) {
            ExportFile item = new ExportFile(service, key);
            this.put("export", item);
        }
    }

    @Override
    public void execute() throws ServletException, IOException {
        HttpServletRequest request = getRequest();
        MainMenu mainMenu = getMainMenu();
        IForm form = this.getForm();
        CustomHandle sess = (CustomHandle) form.getHandle().getProperty(null);
        if (sess.logon()) {
            List<UrlRecord> rightMenus = mainMenu.getRightMenus();
            RightMenus menus = Application.getBean("RightMenus", RightMenus.class);
            menus.setHandle(form.getHandle());
            for (IMenuBar item : menus.getItems())
                item.enrollMenu(form, rightMenus);
        } else {
            mainMenu.getHomePage().setSite(Application.getAppConfig().getFormWelcome());
        }

        // 系统通知消息
        Component content = this.getContent();
        if (form instanceof AbstractForm) {
            this.add("barMenus", mainMenu.getBarMenus(this.getForm()));
            if (mainMenu.getRightMenus().size() > 0)
                this.add("subMenus", mainMenu.getRightMenus());
            this.initHeader();
            request.setAttribute(content.getId(), content);
            for (Component component : content.getComponents()) {
                request.setAttribute(component.getId(), component);
            }
        }

        // 开始输出
        PrintWriter out = getResponse().getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.printf("<title>%s</title>\n", this.getForm().getTitle());
        out.printf("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n");
        out.printf(
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0\"/>\n");
        out.printf("<link href=\"css/style-phone.css\" rel=\"stylesheet\">\n");
        if (!form.getClient().isPhone())
            out.printf("<link href=\"css/style-pc.css\" rel=\"stylesheet\">\n");
        out.print(this.getCssHtml());
        out.print(getScriptHtml());
        out.println("<script>");
        out.println("var Application = new TApplication();");
        out.printf("Application.device = '%s';\n", form.getClient().getDevice());
        out.printf("Application.bottom = '%s';\n", getFooter().getId());
        String msg = form.getParam("message", "");
        msg = msg == null ? "" : msg.replaceAll("\r\n", "<br/>");
        out.printf("Application.message = '%s';\n", msg);
        out.printf("Application.searchFormId = '%s';\n", this.searchWaitingId);
        out.println("$(document).ready(function() {");
        out.println("Application.init();");
        out.println("});");
        out.println("</script>");
        out.println("</head>");
        outBody(out);
        out.println("</html>");
    }

    public UIFormHorizontal createSearch() {
        UIFormHorizontal search = new UIFormHorizontal(this.getDocument().getContent(), this.getRequest());
        search.setCssClass("modify");
        this.setSearchWaitingId(search.getId());
        return search;
    }

    public String getSearchWaitingId() {
        return searchWaitingId;
    }

    public void setSearchWaitingId(String searchWaitingId) {
        this.searchWaitingId = searchWaitingId;
    }

    public void add(String id, PassportRecord value) {
        put(id, value);
    }

    public void add(String id, UIFormVertical value) {
        put(id, value);
    }
}
