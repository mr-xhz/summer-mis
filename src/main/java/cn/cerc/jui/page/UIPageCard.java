package cn.cerc.jui.page;

import static cn.cerc.jmis.core.ClientDevice.device_ee;

import java.io.IOException;
import java.util.ArrayList;
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
import cn.cerc.jpage.core.HtmlContent;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jpage.core.UrlRecord;
import cn.cerc.jpage.other.UrlMenu;
import cn.cerc.jui.parts.AdHeader;
import cn.cerc.jui.parts.HeaderSide;
import cn.cerc.jui.parts.MainMenu;
import cn.cerc.jui.parts.RightMenus;

/**
 * 数据卡片
 * 
 * @author 张弓
 *
 */
public class UIPageCard extends AbstractJspPage {
    private MainMenu mainMenu = new MainMenu();
    private UIContentCard content;

    public UIPageCard(IForm form) {
        super(form);
        this.init();
    }

    protected void init() {
        this.content = new UIContentCard(this);
        this.put("document", this.content);
    }

    public void addExportFile(String service, String key) {
        if (device_ee.equals(this.getForm().getClient().getDevice())) {
            ExportFile item = new ExportFile(service, key);
            this.put("export", item);
        }
    }

    public UIContentCard getContent() {
        return content;
    }

    @Override
    public void execute() throws ServletException, IOException {
        this.getStatusBar(); // 此行代码不能删除！

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
            this.put("barMenus", mainMenu.getBarMenus(this.getForm()));
            if (mainMenu.getRightMenus().size() > 0)
                this.put("subMenus", mainMenu.getRightMenus());
            UIPageSearch.buildHeaderSide(this);
            request.setAttribute(content.getId(), content);
            for (Component component : content.getComponents()) {
                request.setAttribute(component.getId(), component);
            }
        }
        String msg = form.getParam("message", "");
        request.setAttribute("msg", msg == null ? "" : msg.replaceAll("\r\n", "<br/>"));
        request.setAttribute("formno", form.getParam("formNo", "000"));
        request.setAttribute("form", form);

        // 输出jsp模版
        String url = String.format("/WEB-INF/%s/%s", Application.getAppConfig().getPathForms(), this.getViewFile());
        getRequest().getServletContext().getRequestDispatcher(url).forward(getRequest(), getResponse());
    }

    public MainMenu getMainMenu() {
        return mainMenu;
    }

    public void installAD() {
        super.put("_showAd_", new AdHeader());
    }

    public class UIContentCard extends Component {
        private HeaderSide header;
        private List<HtmlContent> codes1 = new ArrayList<>();
        private List<HtmlContent> contents = new ArrayList<>();
        private AbstractJspPage page;

        public UIContentCard(UIPageCard owner) {
            super(owner);
            this.page = owner;
            owner.setJspFile("jui/vine/document-card.jsp");
            owner.addScriptFile("js/jquery.js");
            owner.addScriptFile("js/summer.js");
            owner.addScriptFile("js/myapp.js");
        }

        public HtmlWriter getScript() {
            HtmlWriter html = new HtmlWriter();

            // 加入脚本文件
            for (String file : this.page.getScriptFiles()) {
                html.println("<script src=\"%s\"></script>", file);
            }
            // 加入脚本代码
            List<HtmlContent> scriptCodes = this.getPage().getScriptCodes();
            if (codes1.size() > 0 || scriptCodes.size() > 0) {
                html.println("<script>");
                for (HtmlContent func : codes1) {
                    func.output(html);
                }
                if (scriptCodes.size() > 0) {
                    html.println("$(function(){");
                    for (HtmlContent func : scriptCodes) {
                        func.output(html);
                    }
                    html.println("});");
                }
                html.println("</script>");
            }
            return html;
        }

        /*
         * 注册到jsp文件中
         */
        public void register() {
            HttpServletRequest request = page.getRequest();
            Boolean _showMenu_ = (Boolean) request.getAttribute("_showMenu_");
            if (_showMenu_ != null && _showMenu_) {
                header = new HeaderSide();

                Component left = header.getLeft();
                @SuppressWarnings("unchecked")
                List<UrlRecord> barMenus = (List<UrlRecord>) request.getAttribute("barMenus");
                if (barMenus == null) {
                    new UrlMenu(left, "首页", "/");
                    new UrlMenu(left, "刷新", "javascript:history.go(-1);");
                    // new GoBackButton(left);
                } else {
                    // new GoBackButton(left);
                    for (UrlRecord menu : barMenus) {
                        new UrlMenu(left, menu.getName(), menu.getUrl());
                    }
                }

                Component right = header.getRight();
                @SuppressWarnings("unchecked")
                List<UrlRecord> subMenus = (List<UrlRecord>) request.getAttribute("subMenus");
                int i = subMenus.size() - 1;
                while (i > -1) {
                    UrlRecord menu = subMenus.get(i);
                    new UrlMenu(right, menu.getName(), menu.getUrl());
                    i--;
                }
            }

            request.setAttribute(this.getId(), this);
            for (Component component : getComponents()) {
                request.setAttribute(component.getId(), component);
            }
        }

        public String getHeader() {
            return header != null ? header.getHtml() : null;
        }

        @Deprecated
        public HtmlWriter getCss() {
            return page.getCss();
        }

        public void appendContent(HtmlContent content) {
            contents.add(content);
        }

        public void addDefineScript(HtmlContent scriptCode) {
            codes1.add(scriptCode);
        }

        public HtmlWriter getContents() {
            HtmlWriter html = new HtmlWriter();
            if (contents.size() == 0)
                return html;
            for (HtmlContent content : contents)
                content.output(html);
            return html;
        }

        public AbstractJspPage getPage() {
            return page;
        }

        public CardItem addItem() {
            return new CardItem(this);
        }
    }

    public class CardItem extends Component {
        private String title;
        private UrlMenu url;

        public CardItem(UIContentCard owner) {
            super(owner);
        }

        @Override
        public void output(HtmlWriter html) {
            html.println("<section>");
            html.print("<label>");
            html.println(this.title);
            if (url != null)
                url.output(html);
            html.println("</label>");
            for (Component component : this.getComponents()) {
                html.print("<div>");
                component.output(html);
                html.print("</div>");
            }
            html.println("</section>");
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public UrlMenu getUrl() {
            if (url == null)
                url = new UrlMenu(null);
            return url;
        }

        public void setUrl(UrlMenu url) {
            this.url = url;
        }
    }
}
