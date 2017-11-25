package cn.cerc.jpage.grid;

import cn.cerc.jmis.page.AbstractJspPage;
import cn.cerc.jui.parts.UIComponent;

public class GridFactory {

    public static AbstractGrid build(AbstractJspPage jspPage, UIComponent owner) {
        AbstractGrid grid;
        if (jspPage.getForm().getClient().isPhone())
            grid = new PhoneGrid(jspPage.getForm(), owner);
        else
            grid = new DataGrid(jspPage.getForm(), owner);
        return grid;
    }
}
