package cn.cerc.jui.phone;

import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jui.vcl.UIImage;
import cn.cerc.jui.vcl.UITextBox;
import cn.cerc.jui.vcl.ext.UISpan;

public class Block109 extends Component {
    private UISpan label = new UISpan();
    private UITextBox input = new UITextBox();
    private UIImage select = new UIImage();

    /**
     * 文本 + 输入框 + 弹窗选择按钮
     * 
     * @param owner
     *            内容显示区
     */
    public Block109(Component owner) {
        super(owner);
        label.setText("(label)");
        select.setSrc("jui/phone/block109-select.png");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block109'>");
        label.output(html);
        input.output(html);
        select.output(html);
        html.println("</div>");
    }

    public UISpan getLabel() {
        return label;
    }

    public UITextBox getInput() {
        return input;
    }

    public UIImage getSelect() {
        return select;
    }
}
