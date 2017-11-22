package cn.cerc.jui.parts;

import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;

public class UIButton extends UIComponent {
    private String caption;
    private String url;

    public UIButton(Component owner) {
        super(owner);
    }

    public String getName() {
        return caption;
    }

    public UIButton setCaption(String caption) {
        this.caption = caption;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public UIButton setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<a href=\"%s\"", this.url);
        if (this.cssStyle != null)
            html.print(" style=\"%s\"", this.cssStyle);
        if (this.cssClass != null)
            html.print(" class=\"%s\"", this.cssClass);
        if (this.getId() != null)
            html.print(" id=\"%s\"", this.getId());

        html.println(">%s</a>", this.caption);
    }
}
