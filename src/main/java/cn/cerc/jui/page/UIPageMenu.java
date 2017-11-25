package cn.cerc.jui.page;

import cn.cerc.jbean.form.IForm;
import cn.cerc.jmis.core.RequestData;

/**
 * 系统登录页
 */
public class UIPageMenu extends UIPageDialog {

    public UIPageMenu(IForm form) {
        super(form);
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
