package cn.cerc.jui.parts;

import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;

public class UIToolSheet extends UIComponent {
    private String caption = "(无标题)";

    public UIToolSheet(Component owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<section role='toolSheet'>");
        html.println("<h2>%s</h2>", this.caption);
        html.println("<div>");
        super.output(html);
        html.println("</div>");
        html.println("</section>");
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
