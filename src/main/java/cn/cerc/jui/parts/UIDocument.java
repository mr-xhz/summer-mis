package cn.cerc.jui.parts;

import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;

public class UIDocument extends UIComponent {
    private UIControl control; // 可选存在
    private UIContent content; // 必须存在
    private UIMessage message; // 必须存在

    public UIDocument(Component owner) {
        super(owner);
        content = new UIContent(this);
        message = new UIMessage(this);
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("\n<article role='document'>");
        // 可选
        if (control != null) {
            html.println("<section role='control'>");
            html.println(control.toString());
            html.println("</section>");
        }
        // 必须存在
        html.println(content.toString());
        // 必须存在
        html.println(message.toString());

        html.print("</article>");
    }

    public UIControl getControl() {
        if (control == null) {
            control = new UIControl(this);
        }
        return control;
    }

    public UIContent getContent() {
        return content;
    }

    public UIMessage getMessage() {
        return message;
    }

}
