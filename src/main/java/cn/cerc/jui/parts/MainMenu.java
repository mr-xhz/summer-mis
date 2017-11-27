package cn.cerc.jui.parts;

import java.util.List;

import cn.cerc.jpage.core.UrlRecord;

public class MainMenu {
    private UIHeader header;

    public MainMenu(UIHeader header) {
        this.header = header;
    }

    public void addLeftMenu(String url, String name) {
        header.addLeftMenu(new UrlRecord(url, name));
    }

    public void addRightMenu(String url, String name) {
        header.addRightMenu(new UrlRecord(url, name));
    }

    public void setExitUrl(String url) {
        header.setExitPage(url);
    }

    public String getPageTitle() {
        return header.getPageTitle();
    }

    public void setPageTitle(String pageTitle) {
        header.setPageTitle(pageTitle);
    }

    public UrlRecord getHomePage() {
        return header.getHomePage();
    }

    public List<UrlRecord> getRightMenus() {
        return header.getRightMenus();
    }
}
