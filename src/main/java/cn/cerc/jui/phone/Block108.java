package cn.cerc.jui.phone;

import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jui.vcl.UITextBox;
import cn.cerc.jui.vcl.ext.UISpan;

public class Block108 extends Component {
    private UISpan label = new UISpan();
    private UITextBox input = new UITextBox();

    /**
     * 文本 + 输入框
     * 
     * @param owner
     *            内容显示区
     */
    public Block108(Component owner) {
        super(owner);
        label.setText("(label)");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block108'>");
        label.output(html);
        input.output(html);
        html.println("</div>");
    }

    public UISpan getLabel() {
        return label;
    }

    public UITextBox getInput() {
        return input;
    }
}
