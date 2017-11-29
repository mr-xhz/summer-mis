package cn.cerc.jui.vcl;

import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;

public class GroupBox extends Component {

    private String cssClass;

    public GroupBox(Component content) {
        super(content);
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<div role='group'");
        if (getId() != null)
            html.print(" id='%s' ", getId());
        if (this.cssClass != null)
            html.print(" class='%s' ", cssClass);
        html.print(">");
        super.output(html);
        html.println("</div>");
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }
}
