package cn.cerc.jui.parts;

import java.util.List;

import cn.cerc.jbean.form.IForm;
import cn.cerc.jmis.page.AbstractJspPage;
import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jpage.core.UrlRecord;
import cn.cerc.jpage.other.UrlMenu;

public class UIHeader extends UIComponent {
    private UIAdvertisement advertisement; // 可选
    private Component left = new Component();
    private Component right = new Component();
    // 主菜单
    private MainMenu mainMenu = new MainMenu();

    public UIHeader(AbstractJspPage owner) {
        super(owner);
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
        html.println("<nav role='mainMenu' class=\"navigation\">");
        int i = 0;
        html.println("<div class=\"menu\">");
        for (Component item : left.getComponents()) {
            if (i > 1)
                html.println("<a style=\"padding: 0.5em 0\">→</a>");
            item.output(html);
            i++;
        }
        html.println("</div>");
        html.println("<div class=\"menu\" style=\"float: right;\">");
        for (Component item : right.getComponents()) {
            item.output(html);
        }
        html.println("</div>");
        html.println("</nav>");
        html.println("</header>");
    }

    public Component getLeft() {
        return left;
    }

    public Component getRight() {
        return right;
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
        IForm form = (IForm) this.getOwner();
        Component left = this.getLeft();
        List<UrlRecord> barMenus = mainMenu.getBarMenus(form);
        if (barMenus == null) {
            new UrlMenu(left, "首页", "/");
            new UrlMenu(left, "刷新", "javascript:history.go(-1);");
        } else {
            for (UrlRecord menu : barMenus) {
                new UrlMenu(left, menu.getName(), menu.getUrl());
            }
        }
        form.getRequest().setAttribute("barMenus", barMenus);

        Component right = this.getRight();

        List<UrlRecord> subMenus = this.mainMenu.getRightMenus();
        if (subMenus.size() > 0) {
            int i = subMenus.size() - 1;
            while (i > -1) {
                UrlRecord menu = subMenus.get(i);
                new UrlMenu(right, menu.getName(), menu.getUrl());
                i--;
            }
        }
        form.getRequest().setAttribute("subMenus", subMenus);
    }
}
