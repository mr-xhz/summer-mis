package cn.cerc.jui.parts;

import javax.servlet.http.HttpServletRequest;

import cn.cerc.jbean.form.IForm;
import cn.cerc.jbean.form.IPage;
import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jpage.core.UrlRecord;
import cn.cerc.jpage.other.UrlMenu;

public class StatusBar extends Component {
    private IForm form;
    private static final int MAX_MENUS = 6;
    protected UrlRecord checkAll;
    private boolean flag = false;

    public StatusBar() {
        super();
    }

    public void init(IPage owner) {
        setOwner((Component) owner);
        this.form = owner.getForm();
        this.setId("bottom");
    }

    public void addButton(String caption, String url) {
        int count = 1;
        for (Component obj : this.getComponents()) {
            if (obj instanceof UrlMenu) {
                count++;
            }
        }
        UrlMenu item = new UrlMenu(this, caption, url);
        item.setCssClass("bottomBotton");
        item.setId("button" + count);
        if (!form.getClient().isPhone())
            item.setName(String.format("F%s:%s", count, item.getName()));
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
        html.println("<div class=\"operaBottom\">");
        if (this.checkAll != null) {
            html.print("<input type=\"checkbox\"");
            html.print(" id=\"selectAll\"");
            html.print(" onclick=\"%s\"/>", checkAll.getUrl());
            html.println("<label for=\"selectAll\">全选</label>");
        }
        super.output(html);
        HttpServletRequest request = getForm().getRequest();
        if (request != null) {
            if (!form.getClient().isPhone()) {
                String msg = request.getParameter("msg");
                html.print("<div class=\"bottom-message\"");
                html.print(" id=\"msg\">");
                if (msg != null)
                    html.print(msg.replaceAll("\r\n", "<br/>"));
                html.println("</div>");
            }
        }
        html.println("</div>");
    }

    public IForm getForm() {
        return form;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
