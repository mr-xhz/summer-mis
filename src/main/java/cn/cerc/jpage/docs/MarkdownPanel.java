package cn.cerc.jpage.docs;

import cn.cerc.jbean.form.IPage;
import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.other.OperaPanel;

public class MarkdownPanel extends OperaPanel {
    private String fileName;

    public MarkdownPanel(Component owner) {
        super(owner);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        if (this.getOwner() instanceof IPage) {
            IPage page = (IPage) this.getOwner();
            MarkdownDoc doc = new MarkdownDoc(page.getForm());
            doc.setOutHtml(true);
            this.setReadme(doc.getContext("/docs/" + fileName, "(暂未编写相应的说明)"));
        }
    }

}
