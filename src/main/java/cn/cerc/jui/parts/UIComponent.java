package cn.cerc.jui.parts;

import cn.cerc.jpage.core.Component;

public class UIComponent extends Component {
    protected String cssClass;
    protected String cssStyle;

    public UIComponent(Component owner) {
        super(owner);
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getCssStyle() {
        return cssStyle;
    }

    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

}
