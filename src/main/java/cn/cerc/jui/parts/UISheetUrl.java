package cn.cerc.jui.parts;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jpage.core.UrlRecord;

public class UISheetUrl extends Component {

    private String title = "相关操作";
    private List<UrlRecord> urls = new ArrayList<>();

    public UISheetUrl(UIToolBar owner) {
        super(owner);
    }

    public String getTitle() {
        return title;
    }

    public UISheetUrl setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public void output(HtmlWriter html) {
        if (urls.size() == 0)
            return;

        html.println("<section>");
        html.println("<div class=\"title\">%s</div>", this.title);
        html.println("<div class=\"contents\">");
        for (UrlRecord url : urls) {
            html.print("<a href=\"%s\"", url.getUrl());
            if (url.getTitle() != null) {
                html.print(" title=\"%s\"", url.getTitle());
            }
            if (url.getHintMsg() != null) {
                html.print(" onClick=\"return confirm('%s');\"", url.getHintMsg());
            }
            if (url.getTarget() != null) {
                html.print(" target=\"%s\"", url.getTarget());
            }
            html.println(">%s</a>", url.getName());
        }
        html.println("</div>");
        html.println("</section>");
    }

    public UrlRecord addUrl() {
        UrlRecord url = new UrlRecord();
        urls.add(url);
        return url;
    }

    public UrlRecord addUrl(UrlRecord url) {
        urls.add(url);
        return url;
    }
}
