package cn.cerc.jui.parts;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;

public class UIToolBar extends UIComponent {
    private List<UIToolSheet> sheets = new ArrayList<>();

    public UIToolBar(Component owner) {
        super(owner);
        this.setId("rightSide");
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("\n<aside role='toolBar' id='%s'>", this.getId());
        if (sheets.size() > 0) {
            for (UIToolSheet sheet : sheets) {
                html.print(sheet.toString());
            }
        } else {
            super.output(html);
        }
        html.print("</aside>");
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
