package cn.cerc.jui.parts;

import cn.cerc.jpage.core.Component;

public class UIDocument extends UIComponent {
    private UIControl control; // 可选存在
    private UIContext context; // 必须存在
    private UIMessage message; // 必须存在

    public UIDocument(Component owner) {
        super(owner);
        context = new UIContext(this);
        message = new UIMessage(this);
    }

    public UIControl getControl() {
        if (control == null) {
            control = new UIControl(this);
        }
        return control;
    }

    public UIContext getContext() {
        return context;
    }

    public UIMessage getMessage() {
        return message;
    }

}
