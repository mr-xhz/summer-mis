package cn.cerc.jui.phone;

import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jui.vcl.UIButton;
import cn.cerc.jui.vcl.UIImage;
import cn.cerc.jui.vcl.ext.UISpan;

/**
 * 
 * @author 张弓
 *
 */
public class Block991 extends Component {
    private UIImage image = new UIImage();
    private UIButton button = new UIButton(this);
    private UISpan remark = new UISpan(this);

    /**
     * 底部状态栏：1个功能按钮+提示文字
     * 
     * @param owner
     *            内容显示区
     */
    public Block991(Component owner) {
        super(owner);
        image.setSrc("jui/phone/block991_back.png");
        button.setText("(button)");
        remark.setText("(remark)");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.println("<div class=\"block991\">");
        image.output(html);
        button.output(html);
        remark.output(html);
        html.println("</div>");
    }

    public UISpan getRemark() {
        return remark;
    }

    public UIButton getButton() {
        return button;
    }

    public UIImage getImage() {
        return image;
    }
}
