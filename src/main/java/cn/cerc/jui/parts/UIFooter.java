package cn.cerc.jui.parts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cn.cerc.jbean.form.IForm;
import cn.cerc.jmis.page.AbstractJspPage;
import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jpage.core.UrlRecord;

public class UIFooter extends UIComponent {
    private static final int MAX_MENUS = 6;
    protected UrlRecord checkAll;
    private boolean flag = false;
    private UIFooterOperation operation;
    private List<UIButton> buttons = new ArrayList<>();

    public UIFooter(Component owner) {
        super(owner);
        this.setId("bottom");
    }

    public UrlRecord getCheckAll() {
        return checkAll;
    }

    public void enableCheckAll(String targetId) {
        if (targetId == null || "".equals(targetId))
            throw new RuntimeException("targetId is null");
        if (checkAll != null)
            throw new RuntimeException("checkAll is not null");
        checkAll = new UrlRecord(String.format("selectItems('%s')", targetId), "全选");
    }

    @Override
    public void output(HtmlWriter html) {
        if (this.getComponents().size() > MAX_MENUS)
            throw new RuntimeException(String.format("底部菜单区最多只支持 %d 个菜单项", MAX_MENUS));

        html.println("\n<footer role='footer' class=\"operaBottom\">");
        if (this.checkAll != null) {
            html.println("<section role='operation'>");
            html.print("<input type=\"checkbox\"");
            html.print(" id=\"selectAll\"");
            html.print(" onclick=\"%s\"/>", checkAll.getUrl());
            html.println("<label for=\"selectAll\">全选</label>");
            html.println("</section>");
        }
        html.println("<section role='buttons'>");
        super.output(html);
        html.println("</section>");
        HttpServletRequest request = getForm().getRequest();
        if (request != null) {
            if (!getForm().getClient().isPhone()) {
                html.print("<div class=\"bottom-message\"");
                html.print(" id=\"msg\">");
                String msg = request.getParameter("msg");
                if (msg != null)
                    html.print(msg.replaceAll("\r\n", "<br/>"));
                html.println("</div>");
            }
        }
        html.print("</footer>");
    }

    public IForm getForm() {
        return ((AbstractJspPage) this.getOwner()).getForm();
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public List<UIButton> getButtons() {
        return buttons;
    }

    public UIButton addButton() {
        UIButton button = new UIButton(this);
        buttons.add(button);
        return button;
    }

    public void addButton(String caption, String url) {
        int count = 1;
        for (Component obj : this.getComponents()) {
            if (obj instanceof UIButton) {
                count++;
            }
        }
        UIButton item = addButton();
        item.setCaption(caption);
        item.setUrl(url);

        item.setCssClass("bottomBotton");
        item.setId("button" + count);
        if (!getForm().getClient().isPhone())
            item.setCaption(String.format("F%s:%s", count, item.getName()));
    }

    public UIFooterOperation getOperation() {
        if (operation == null)
            operation = new UIFooterOperation(this);
        return operation;
    }
}
