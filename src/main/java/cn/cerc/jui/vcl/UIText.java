package cn.cerc.jui.vcl;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jui.parts.UIComponent;

/*
 * 专用于简单或原始文字输出
 */
public class UIText extends UIComponent {
    private String content;
    private List<String> lines;

    public UIText() {
        super();
    }

    public UIText(Component owner) {
        super(owner);
    }

    public void output(HtmlWriter html) {
        if (content != null)
            html.print(content);
        if (lines != null) {
            for (String line : lines)
                html.println("<p>%s</p>", line);
        }
    }

    public String getContent() {
        return content;
    }

    public UIText setContent(String content) {
        this.content = content;
        return this;
    }

    public List<String> getLines() {
        if (lines == null)
            lines = new ArrayList<>();
        return lines;
    }

    public UIText setLines(List<String> lines) {
        this.lines = lines;
        return this;
    }
}
