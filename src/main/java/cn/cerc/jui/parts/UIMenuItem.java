package cn.cerc.jui.parts;

import cn.cerc.jmis.core.MenuItem;
import cn.cerc.jpage.core.HtmlWriter;

public class UIMenuItem extends UIComponent {
    private String img = "";
    private String name;
    private String url;
    private int hrip;

    public UIMenuItem(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        // 输出菜单图像
        html.print("<div role='menuIcon'>");
        html.print("<a href='%s' target=\"_blank\"><img src='%s'></a>", this.getUrl(), this.getImg());
        html.print("</div>");
        // 输出菜单名称
        html.print("<div role='menuName'>");
        html.print("<a href='%s'target=\"_blank\">%s</a>", this.getUrl(), this.getName());
        html.print("</div>");
    }

    public UIMenuItem init(String name, String url, String img) {
        this.name = name;
        this.url = url;
        this.img = img;
        return this;
    }

    public UIMenuItem init(MenuItem item) {
        setHrip(item.getHrip());
        setUrl(item.getId());

        String str = item.getTitle();
        str = str.substring(str.indexOf("]") + 1);
        str = str.substring(str.indexOf("\\") + 1);

        setName(str);
        setImg("menu/" + item.getId() + ".png");
        return this;
    }

    public String getImg() {
        return img;
    }

    public UIMenuItem setImg(String img) {
        this.img = img;
        return this;
    }

    public String getName() {
        return name;
    }

    public UIMenuItem setName(String name) {
        this.name = name;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public UIMenuItem setUrl(String url) {
        this.url = url;
        return this;
    }

    public int getHrip() {
        return hrip;
    }

    public void setHrip(int hrip) {
        this.hrip = hrip;
    }

}