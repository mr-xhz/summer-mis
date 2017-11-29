package cn.cerc.jpage.docs;

import cn.cerc.jbean.form.IForm;
import cn.cerc.jui.parts.UISheetHelp;
import cn.cerc.jui.parts.UIToolBar;

public class MarkdownHelp extends UISheetHelp {

    public MarkdownHelp(UIToolBar owner) {
        super(owner);
    }

    public void loadResourceFile(IForm form, String mdFileName) {
        MarkdownDoc doc = new MarkdownDoc(form);
        doc.setOutHtml(true);
        this.setContent(doc.getContext("/docs/" + mdFileName, "(暂未编写相应的说明)"));
    }
}
