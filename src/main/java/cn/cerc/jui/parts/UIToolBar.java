package cn.cerc.jui.parts;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.jpage.core.Component;

public class UIToolBar extends UIComponent {
    private List<UIToolSheet> sheets = new ArrayList<>();

    public UIToolBar(Component owner) {
        super(owner);
    }

    public List<UIToolSheet> getSheets() {
        return sheets;
    }

    public int size() {
        return sheets.size();
    }

    public UIToolSheet addSheet() {
        UIToolSheet sheet = new UIToolSheet(this);
        sheets.add(sheet);
        return sheet;
    }
}
