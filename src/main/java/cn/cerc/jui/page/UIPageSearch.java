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
import cn.cerc.jbean.other.MemoryBuffer;
import cn.cerc.jbean.rds.PassportRecord;
import cn.cerc.jdb.core.DataSet;
import cn.cerc.jmis.form.AbstractForm;
import cn.cerc.jmis.page.AbstractJspPage;
import cn.cerc.jmis.page.ExportFile;
import cn.cerc.jmis.page.IMenuBar;
import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlContent;
import cn.cerc.jpage.core.UrlRecord;
import cn.cerc.jpage.grid.AbstractGrid;
import cn.cerc.jpage.grid.GridFactory;
import cn.cerc.jpage.grid.MutiPage;
import cn.cerc.jpage.other.OperaPages;
import cn.cerc.jui.parts.RightMenus;
import cn.cerc.jui.parts.UIComponent;
import cn.cerc.jui.parts.UIFormHorizontal;
import cn.cerc.jui.parts.UIFormVertical;

/**
 * 主体子页面
 * 
 * @author 张弓
 *
 */
public class UIPageSearch extends AbstractJspPage {
    private MutiPage pages;
    private String searchWaitingId = "";

    public UIPageSearch(IForm form) {
        super(form);
        initCssFile();
        initJsFile();
        if (!this.getForm().getClient().isPhone()) {
            this.getHeader().getAdvertisement();
        }
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

        // 添加分页控制
        Component operaPages = null;
        if (pages != null) {
            this.put("pages", pages);
            operaPages = new OperaPages(this.getToolBar(), this.getForm(), pages);
            this.put("_operaPages_", operaPages);
        }
        IForm form = this.getForm();
        CustomHandle sess = (CustomHandle) form.getHandle().getProperty(null);
        if (sess.logon()) {
            List<UrlRecord> rightMenus = getHeader().getRightMenus();
            RightMenus menus = Application.getBean("RightMenus", RightMenus.class);
            menus.setHandle(form.getHandle());
            for (IMenuBar item : menus.getItems())
                item.enrollMenu(form, rightMenus);
        } else {
            getHeader().getHomePage().setSite(Application.getAppConfig().getFormWelcome());
        }

        // 系统通知消息
        Component content = this.getContent();
        if (form instanceof AbstractForm) {
            this.getHeader().initHeader();
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

        // 所有的请求都不发送 referrer
        out.println("<meta name=\"referrer\" content=\"no-referrer\" />");

        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n");
        out.println("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=9; IE=8; IE=7;\"/>");
        out.println(
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0\"/>");
        out.println(this.getCssHtml());
        out.println(getScriptHtml());
        out.println("<script>");
        out.println("var Application = new TApplication();");
        out.printf("Application.device = '%s';\n", form.getClient().getDevice());

        out.printf("Application.bottom = '%s';\n", this.getFooter().getId());

        String msg = form.getParam("message", "");
        msg = msg == null ? "" : msg.replaceAll("\r\n", "<br/>");
        out.printf("Application.message = '%s';\n", msg.replace("'", "\\'"));
        out.printf("Application.searchFormId = '%s';\n", this.searchWaitingId);
        out.println("$(document).ready(function() {");
        out.println("Application.init();");
        out.println("});");
        out.println("</script>");
        out.println("</head>");
        outBody(out);
        out.println("</html>");
    }

    public void appendContent(HtmlContent content) {
        this.getDocument().getContent().append(content);
    }

    public UIFormHorizontal createSearch(MemoryBuffer buff) {
        UIFormHorizontal search = new UIFormHorizontal(this.getDocument().getControl(), this.getRequest());
        search.setBuffer(buff);
        this.setSearchWaitingId(search.getId());
        return search;
    }

    public AbstractGrid createGrid(UIComponent owner, DataSet dataSet) {
        AbstractGrid grid = GridFactory.build(this.getForm(), owner);
        grid.setDataSet(dataSet);
        pages = grid.getPages();
        return grid;
    }

    public String getSearchWaitingId() {
        return searchWaitingId;
    }

    public void setSearchWaitingId(String searchWaitingId) {
        this.searchWaitingId = searchWaitingId;
    }

    public void add(String id, AbstractGrid grid) {
        getRequest().setAttribute(id, grid);
        pages = grid.getPages();
    }

    public void add(String id, UIFormHorizontal value) {
        put(id, value);
    }

    public void add(String id, UIFormVertical value) {
        put(id, value);
    }

    public void add(String id, ExportFile value) {
        put(id, value);
    }

    public void add(String id, PassportRecord value) {
        put(id, value);
    }
}
