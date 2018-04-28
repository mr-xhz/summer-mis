package cn.cerc.jpage.fields;

import cn.cerc.jdb.core.Record;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jui.parts.UIComponent;

public class ItField extends AbstractField {
    public ItField(UIComponent owner) {
        super(owner, "序", 2);
        this.setReadonly(true);
        this.setShortName("");
        this.setAlign("center");
    }

    @Override
    public String getText(Record dataSet) {
        if (dataSet == null)
            return null;
        if (buildText != null) {
            HtmlWriter html = new HtmlWriter();
            buildText.outputText(dataSet, html);
            return html.toString();
        }
        return "" + dataSource.getDataSet().getRecNo();
    }

    @Override
    public String getField() {
        return "_it_";
    }

    @Override
    public Title createTitle() {
        Title title = super.createTitle();
        title.setType("int");
        return title;
    }

    @Override
    public ItField setReadonly(boolean readonly) {
        super.setReadonly(true);
        return this;
    }
}
