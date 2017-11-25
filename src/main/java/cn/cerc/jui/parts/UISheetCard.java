package cn.cerc.jui.parts;

import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jpage.other.UrlMenu;

public class UISheetCard extends UISheet {
    private UrlMenu url;

    public UISheetCard(UIContent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<section>");
        html.print("<label>");
        html.println(this.getCaption());
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

    public UrlMenu getUrl() {
        if (url == null)
            url = new UrlMenu(null);
        return url;
    }

    public void setUrl(UrlMenu url) {
        this.url = url;
    }
}