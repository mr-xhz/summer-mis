package cn.cerc.jui.parts;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlContent;
import cn.cerc.jpage.core.HtmlWriter;

public class UIContent extends UIComponent {
    private List<HtmlContent> contents = new ArrayList<>();

    public UIContent(Component owner) {
        super(owner);
    }

    public void append(HtmlContent content) {
        contents.add(content);
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<section role='content'>");
        super.output(html);
        // 输出追加过来的内容
        for (HtmlContent content : contents)
            content.output(html);
        html.println("</section>");
    }
}
