package cn.cerc.jpage.grid;

import cn.cerc.jmis.page.AbstractJspPage;
import cn.cerc.jui.parts.UIComponent;

public class GridFactory {

    public static AbstractGrid build(AbstractJspPage page, UIComponent owner) {
        AbstractGrid grid;
        if (page.getForm().getClient().isPhone())
            grid = new PhoneGrid(page.getForm(), owner);
        else
            grid = new DataGrid(page.getForm(), owner);
        return grid;
    }
}
