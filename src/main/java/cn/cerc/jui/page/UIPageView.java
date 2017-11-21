package cn.cerc.jui.page;

import static cn.cerc.jmis.core.ClientDevice.device_ee;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import cn.cerc.jbean.core.Application;
import cn.cerc.jbean.core.CustomHandle;
import cn.cerc.jbean.form.IForm;
import cn.cerc.jmis.form.AbstractForm;
import cn.cerc.jmis.page.AbstractJspPage;
import cn.cerc.jmis.page.ExportFile;
import cn.cerc.jmis.page.IMenuBar;
import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jpage.core.MutiGrid;
import cn.cerc.jpage.core.UrlRecord;
import cn.cerc.jpage.grid.AbstractGrid;
import cn.cerc.jpage.grid.MutiPage;
import cn.cerc.jpage.other.OperaPages;
import cn.cerc.jui.UIConfig;
import cn.cerc.jui.parts.MainMenu;
import cn.cerc.jui.parts.RightMenus;

public class UIPageView extends AbstractJspPage {
    private MainMenu mainMenu = new MainMenu();
    private boolean showMenus = true; // 是否显示主菜单
    private MutiPage pages;

    public UIPageView() {
        super();
    }

    public UIPageView(IForm form) {
        super(form);
    }

    public void addExportFile(String service, String key) {
        if (device_ee.equals(this.getForm().getClient().getDevice())) {
            ExportFile item = new ExportFile(service, key);
            this.add("export", item);
        }
    }

    @Override
    public void add(String id, Object value) {
        HttpServletRequest request = getRequest();
        if (value instanceof AbstractGrid) {
            AbstractGrid grid = (AbstractGrid) value;
            request.setAttribute(id, value);
            pages = grid.getPages();
        } else if (value instanceof MutiGrid) {
            MutiGrid<?> grid = (MutiGrid<?>) value;
            request.setAttribute(id, grid.getList());
            pages = grid.getPages();
        } else
            request.setAttribute(id, value);
    }

    @Override
    public void execute() throws ServletException, IOException {
        IForm form = this.getForm();
        HttpServletRequest request = form.getRequest();
        CustomHandle sess = (CustomHandle) form.getHandle().getProperty(null);
        request.setAttribute("passport", sess.logon());
        request.setAttribute("logon", sess.logon());
        if (sess.logon()) {
            List<UrlRecord> rightMenus = mainMenu.getRightMenus();
            RightMenus menus = Application.getBean("RightMenus", RightMenus.class);
            menus.setHandle(form.getHandle());
            for (IMenuBar item : menus.getItems())
                item.enrollMenu(form, rightMenus);
        } else {
            mainMenu.getHomePage().setSite(Application.getAppConfig().getFormWelcome());
        }
        // 设置首页
        request.setAttribute("_showMenu_", "true".equals(form.getParam("showMenus", "true")));
        // 系统通知消息
        if (request.getAttribute("message") == null)
            request.setAttribute("message", "");

        if (form instanceof AbstractForm) {
            this.add("barMenus", mainMenu.getBarMenus(this.getForm()));
            if (mainMenu.getRightMenus().size() > 0)
                this.add("subMenus", mainMenu.getRightMenus());
            if (this.isShowMenus())
                UIPageSearch.buildHeaderSide(this);
        }
        String msg = form.getParam("message", "");
        request.setAttribute("msg", msg == null ? "" : msg.replaceAll("\r\n", "<br/>"));
        request.setAttribute("formno", form.getParam("formNo", "000"));
        request.setAttribute("form", form);

        // 添加分页控制
        Component operaPages = null;
        if (pages != null) {
            this.add("pages", pages);
            operaPages = new OperaPages(this.getForm(), pages);
            this.add("_operaPages_", operaPages);
        }

        // 输出jsp模版
        String url = String.format("/WEB-INF/%s/%s", Application.getAppConfig().getPathForms(), this.getViewFile());
        getRequest().getServletContext().getRequestDispatcher(url).forward(getRequest(), getResponse());
    }

    public MainMenu getMainMenu() {
        return mainMenu;
    }

    public void installAD() {
        super.add("_showAd_", new AdHeader());
    }

    private class AdHeader extends Component {
        @Override
        public void output(HtmlWriter html) {
            html.println("<div class=\"ad\">");
            html.println("<div class=\"ban_javascript clear\">");
            html.println("<ul>");
            html.println("<li><img src=\"%s\"></li>", UIConfig.EASY_PIC_5_PC);
            html.println("</ul>");
            html.println("</div>");
            html.println("</div>");
        }
    }

    public boolean isShowMenus() {
        return showMenus;
    }

    public void setShowMenus(boolean showMenus) {
        // this.setParam("showMenus", "false");
        this.showMenus = showMenus;
    }
}
