package cn.cerc.jui.phone;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jui.parts.UIComponent;
import cn.cerc.jui.vcl.UIImage;

/**
 * 
 * @author 张弓
 *
 */
public class Block602 extends UIComponent {
    private List<UIImage> items = new ArrayList<>();

    /**
     * 多图片显示，上下陈列
     * 
     * @param owner
     *            内容显示区
     */
    public Block602(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.println("<div class=\"block602\">");
        for (UIImage button : items)
            button.output(html);
        html.println("</div>");
    }

    public UIImage addImage(String imgUrl) {
        UIImage image = new UIImage();
        image.setSrc(imgUrl);
        items.add(image);
        return image;
    }
}
