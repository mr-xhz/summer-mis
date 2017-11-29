package cn.cerc.jui.phone;

import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jui.vcl.UICheckBox;
import cn.cerc.jui.vcl.ext.UISpan;

public class Block130 extends Component {
    private UISpan label = new UISpan();
    private UICheckBox checkBox = new UICheckBox();

    /**
     * 多选框 + 文本
     * 
     * @param owner
     *            内容显示区
     */
    public Block130(Component owner) {
        super(owner);
        label.setText("(label)");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block130'>");
        html.print("<label>");
        checkBox.output(html);
        label.output(html);
        html.print("</label>");
        html.println("</div>");
    }

    public UISpan getLabel() {
        return label;
    }

    public UICheckBox getCheckBox() {
        return checkBox;
    }
}
