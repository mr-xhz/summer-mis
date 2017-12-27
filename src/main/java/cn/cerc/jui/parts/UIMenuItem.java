package cn.cerc.jui.parts;

import cn.cerc.jmis.core.MenuItem;
import cn.cerc.jpage.core.HtmlWriter;

public class UIMenuItem extends UIComponent {
    private String img = "";
    private String name;
    private String code;
    private int hrip;
    private boolean delphi;
    private boolean blank;

    public UIMenuItem(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        // 输出菜单图像
        html.println("<div role='menuIcon'>");
        html.println("<a href='%s' onclick=\"updateUserHit('%s')\" target='_blank'>", getCode(), getCode());
        html.println("<img src='%s'></a>", getImg());
        html.println("</div>");

        // 输出菜单名称
        html.println("<div role='menuName'>");
        if (getHrip() == 2 && isDelphi()) {
            html.println("<a href=\"hrip:%s\" onclick=\"updateUserHit('%s')\">", getCode(), getCode());
            // 闪电 ⚡ 标记
            html.println("<img src=\"%s\"/>", "images/lightning.png");
            html.println("</a>");
        }

        if (isBlank()) {
            html.println("<a href='%s' onclick=\"updateUserHit('%s')\"", getCode(), getCode());
            html.println("target='_blank'>%s</a>", getName());
        } else {
            html.println("<a href='%s'>%s</a>", getCode(), getName());
        }
        html.println("</div>");
    }

    public UIMenuItem init(String name, String code, String img) {
        this.name = name;
        this.code = code;
        this.img = img;
        return this;
    }

    public UIMenuItem init(MenuItem item) {
        setHrip(item.getHrip());
        setCode(item.getId());

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

    public String getCode() {
        return code;
    }

    public UIMenuItem setCode(String url) {
        this.code = url;
        return this;
    }

    public int getHrip() {
        return hrip;
    }

    public void setHrip(int hrip) {
        this.hrip = hrip;
    }

    public boolean isDelphi() {
        return delphi;
    }

    public UIMenuItem setDelphi(boolean delphi) {
        this.delphi = delphi;
        return this;
    }

    public boolean isBlank() {
        return blank;
    }

    public UIMenuItem setBlank(boolean blank) {
        this.blank = blank;
        return this;
    }

}