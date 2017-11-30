package cn.cerc.jui.phone;

import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jui.parts.UIComponent;
import cn.cerc.jui.vcl.ext.UISpan;

/**
 * 提示块
 * 
 * @author 郭向军
 *
 */
public class Block126 extends UIComponent {
    private UISpan title = new UISpan();

    public Block126(UIComponent owner) {
        super(owner);
        title.setText("部门一");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block126'>");
        this.title.output(html);
        html.print("</div>");
    }

    public UISpan getTitle() {
        return title;
    }

}
