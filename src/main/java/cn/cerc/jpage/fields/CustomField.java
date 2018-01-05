package cn.cerc.jpage.fields;

import cn.cerc.jdb.core.Record;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jui.parts.UIComponent;

public class CustomField extends AbstractField {

    public CustomField(UIComponent dataView, String name, int width) {
        super(dataView, name, width);
        this.setField("_selectCheckBox_");
    }

    @Override
    public String getText(Record ds) {
        if (buildText == null)
            return "";
        HtmlWriter html = new HtmlWriter();
        buildText.outputText(ds, html);
        return html.toString();
    }

}
