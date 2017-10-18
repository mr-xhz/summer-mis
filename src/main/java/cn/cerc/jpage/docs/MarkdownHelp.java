package cn.cerc.jpage.docs;

import cn.cerc.jbean.form.IForm;
import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.other.HelpSide;

public class MarkdownHelp extends HelpSide {

    public MarkdownHelp(Component owner) {
        super(owner);
    }

    public void loadResourceFile(IForm form, String mdFileName) {
        MarkdownDoc doc = new MarkdownDoc(form);
        doc.setOutHtml(true);
        this.setContent(doc.getContext("/docs/" + mdFileName, "(暂未编写相应的说明)"));
    }
}
