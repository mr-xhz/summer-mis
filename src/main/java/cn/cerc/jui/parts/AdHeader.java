package cn.cerc.jui.parts;

import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jui.UIConfig;

public class AdHeader extends Component {

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
