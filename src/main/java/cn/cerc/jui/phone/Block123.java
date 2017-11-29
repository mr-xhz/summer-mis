package cn.cerc.jui.phone;

import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jui.parts.UIActionForm;
import cn.cerc.jui.vcl.UIButton;
import cn.cerc.jui.vcl.UITextBox;
import cn.cerc.jui.vcl.ext.UISpan;

/**
 * 一组左边图标右边文字
 * 
 * @author 郭向军
 *
 */
public class Block123 extends Component {
    private UISpan title = new UISpan();
    private UITextBox textBox = new UITextBox();
    private UIButton button = new UIButton();
    private UIActionForm form = new UIActionForm();

    public Block123(Component owner) {
        super(owner);
        this.textBox.setMaxlength("20");
        this.textBox.setPlaceholder("请输入");
        this.textBox.setType("text");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block123'>");
        this.form.outHead(html);
        this.textBox.output(html);
        this.button.output(html);
        this.form.outFoot(html);
        html.print("</div>");
    }

    public UISpan getTitle() {
        return title;
    }

    public UITextBox getTextBox() {
        return textBox;
    }

    public UIActionForm getForm(String id) {
        form.setId(id);
        return form;
    }

    public UIButton getButton() {
        return button;
    }
}
