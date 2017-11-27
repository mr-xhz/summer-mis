package cn.cerc.jui.parts;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.jbean.core.Application;
import cn.cerc.jbean.form.IForm;
import cn.cerc.jmis.page.AbstractJspPage;
import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jpage.core.UrlRecord;

public class UIHeader extends UIComponent {
    private UIAdvertisement advertisement; // 可选
    // 页面标题
    private String pageTitle = null;
    // 首页
    private UrlRecord homePage;
    // 左边菜单
    private List<UrlRecord> leftMenus = new ArrayList<>();
    // 右边菜单
    private List<UrlRecord> rightMenus = new ArrayList<>();
    // 主菜单
    private MainMenu mainMenu;
    // 退出
    private UrlRecord exitPage = null;

    public UIHeader(AbstractJspPage owner) {
        super(owner);
        mainMenu = new MainMenu(this);
        homePage = new UrlRecord(Application.getAppConfig().getFormDefault(), "<img src=\"images/Home.png\"/>");
        leftMenus.add(homePage);
    }

    @Override
    @Deprecated
    public void setOwner(Component owner) {
        super.setOwner(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<header role='header'>");
        if (advertisement != null) {
            html.println("<section role='advertisement'>");
            html.println(advertisement.toString());
            html.println("</section>");
        }
        html.println("<nav role='mainMenu'>");

        // 输出：左边
        html.println("<section role='leftMenu'>");
        if (leftMenus.size() > 0) {
            html.print("<ul>");
            int i = 0;
            for (UrlRecord menu : leftMenus) {
                html.print("<li>");
                if (i > 1)
                    html.println("<span>→</span>");
                html.print("<a href=\"%s\">%s</a>", menu.getUrl(), menu.getName());
                i++;
                html.print("</li>");
            }
            html.print("</ul>");
        }
        html.println("</section>");

        // 降序输出：右边
        html.println("<section role='rightMenu'");
        if (rightMenus.size() > 0) {
            html.print("<ul>");
            int i = rightMenus.size() - 1;
            while (i > -1) {
                UrlRecord menu = rightMenus.get(i);
                html.print("<li>");
                html.print("<a href=\"%s\">%s</a>", menu.getUrl(), menu.getName());
                html.print("</li>");
                i--;
            }
            html.print("</ul>");
        }
        html.println("</section>");

        html.println("</nav>");
        html.println("</header>");
    }

    public MainMenu getMainMenu() {
        return mainMenu;
    }

    public UIAdvertisement getAdvertisement() {
        if (advertisement == null)
            advertisement = new UIAdvertisement(this);
        return advertisement;
    }

    public void initHeader() {
        IForm form = ((AbstractJspPage) this.getOwner()).getForm();
        // 刷新
        if (this.pageTitle != null) {
            leftMenus.add(new UrlRecord("javascript:location.reload()", this.pageTitle));
        }
        if (leftMenus.size() > 2) {
            if (form.getClient().isPhone()) {
                UrlRecord first = leftMenus.get(0);
                UrlRecord last = leftMenus.get(leftMenus.size() - 1);
                leftMenus.clear();
                leftMenus.add(first);
                leftMenus.add(last);
            }
        }
        if (leftMenus.size() == 0) {
            leftMenus.add(new UrlRecord("/", "首页"));
            leftMenus.add(new UrlRecord("javascript:history.go(-1);", "刷新"));
        }
        // 兼容老的jsp文件使用
        form.getRequest().setAttribute("barMenus", leftMenus);
        form.getRequest().setAttribute("subMenus", rightMenus);
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public void addLeftMenu(UrlRecord urlRecord) {
        leftMenus.add(urlRecord);
    }

    public void addRightMenu(UrlRecord urlRecord) {
        rightMenus.add(urlRecord);
    }

    public UrlRecord getHomePage() {
        return homePage;
    }

    public void setHomePage(UrlRecord homePage) {
        this.homePage = homePage;
    }

    public List<UrlRecord> getRightMenus() {
        return this.rightMenus;
    }

    public UrlRecord getExitPage() {
        return exitPage;
    }

    public void setExitPage(UrlRecord exitPage) {
        this.exitPage = exitPage;
    }

    public void setExitPage(String url) {
        if (exitPage == null)
            exitPage = new UrlRecord();
        exitPage.setName("<img src=\"images/return.png\"/>");
        exitPage.setSite(url);
    }
}
