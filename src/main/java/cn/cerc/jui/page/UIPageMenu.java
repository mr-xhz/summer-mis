package cn.cerc.jui.page;

import cn.cerc.jbean.form.IForm;
import cn.cerc.jmis.core.RequestData;

/**
 * 系统登录页
 */
public class UIPageMenu extends UIPageView {

    public UIPageMenu(IForm form) {
        super(form);
        super.setJspFile("jui/vine/Vine005.jsp");
    }

    public void setDisableAccountSave(boolean value) {
        super.add("disableAccountSave", value);
    }

    public void setDisablePasswordSave(boolean value) {
        super.add("DisablePasswordSave", value);
    }

    public void setStartVine(boolean value) {
        super.add("startVine", value);
    }

    public void setStartHost(String value) {
        super.add("startHost", value);
    }

    public void setSessionKey(Object value) {
        super.add(RequestData.appSession_Key, value);
    }

    public void setMenus(Object value) {
        super.add("menus", value);
    }

    public void setOnlineUsers(int value) {
        super.add("onlineUsers", value);
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
        super.add("isViewOldMenu", value);
    }

    public void setUnReadMessage(int value) {
        super.add("unReadMessage", value);
    }
}
