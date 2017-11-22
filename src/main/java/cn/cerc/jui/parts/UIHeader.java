package cn.cerc.jui.parts;

import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;

public class UIHeader extends UIComponent {
    private UIAdvertisement advertisement;
    private Component left = new Component();
    private Component right = new Component();
    // 主菜单
    private MainMenu mainMenu = new MainMenu();

    public UIHeader(Component owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<header>");
        html.println("<nav class=\"navigation\">");
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
}
