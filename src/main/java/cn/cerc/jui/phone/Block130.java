package cn.cerc.jui.phone;

import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jui.vcl.CheckBox;
import cn.cerc.jui.vcl.ext.Span;

public class Block130 extends Component {
    private Span label = new Span();
    private CheckBox checkBox = new CheckBox();

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

    public Span getLabel() {
        return label;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }
}
