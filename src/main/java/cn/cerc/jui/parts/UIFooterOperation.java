package cn.cerc.jui.parts;

import cn.cerc.jpage.core.Component;

public class UIFooterOperation extends UIComponent {
    private UICheckAll checkAll;

    public UIFooterOperation(Component owner) {
        super(owner);
    }

    public UICheckAll getCheckAll() {
        if (checkAll == null)
            checkAll = new UICheckAll(this);
        return checkAll;
    }
}
