package cn.cerc.jui.page;

import java.io.IOException;

import javax.servlet.ServletException;

import cn.cerc.jbean.form.IForm;
import cn.cerc.jmis.core.RequestData;
import cn.cerc.jui.parts.UIMenuList;

/**
 * 系统登录页
 */
public class UIPageMenu extends UIPageDialog {
    private UIMenuList menus;

    public UIPageMenu(IForm form) {
        super(form);
    }

    @Override
    public void execute() throws ServletException, IOException {
        if (menus != null)
            add("menus", menus);
        super.execute();
    }

    public UIMenuList getMenus() {
        if (menus == null)
            menus = new UIMenuList(this.getDocument().getContent());
        return menus;
    }

    public void setDisableAccountSave(boolean value) {
        super.put("disableAccountSave", value);
    }

    public void setDisablePasswordSave(boolean value) {
        super.put("DisablePasswordSave", value);
    }

    public void setStartVine(boolean value) {
        super.put("startVine", value);
    }

    public void setStartHost(String value) {
        super.add("startHost", value);
    }

    public void setSessionKey(Object value) {
        super.put(RequestData.appSession_Key, value);
    }

    public void setMenus(Object value) {
        super.put("menus", value);
    }

    public void setOnlineUsers(int value) {
        super.put("onlineUsers", value);
    }

    public void setCurrentUserCode(String value) {
        super.add("currentUserCode", value);
    }

    public void setCurrentUserName(String value) {
        super.add("currentUserName", value);
    }

    public void setCurrentCorpName(String value) {
        super.add("currentCorpName", value);
    }

    public void setIsViewOldMenu(boolean value) {
        super.put("isViewOldMenu", value);
    }

    public void setUnReadMessage(int value) {
        super.put("unReadMessage", value);
    }
}
