package cn.cerc.jui.phone;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jpage.vcl.Image;

/**
 * 
 * @author 张弓
 *
 */
public class Block601 extends Component {
	private List<Image> items = new ArrayList<>();

	/**
	 * 多图片显示，左右滑动更换
	 * 
	 * @param owner
	 *            内容显示区
	 */
	public Block601(Component owner) {
		super(owner);
	}

	@Override
	public void output(HtmlWriter html) {
		if (items.size() == 0) {
			Image image = new Image();
			image.setAlt("(image)");
			image.setWidth("100%").setHeight("192px");
			image.setSrc("");
			items.add(image);
		}
		html.println("<!-- %s -->", this.getClass().getName());
		html.println("<div class=\"block601\">");
		html.println("<div class=\"swiper-wrapper\">");
		for (Image image : items){
			html.println("<div class=\"swiper-slide\">");
			image.output(html);
			html.println("</div>");
		}
		html.println("</div>");
		html.println("<div class=\"swiper-pagination\"></div>");
		html.println("</div>");
	}

	public Image addImage(String imgUrl) {
		Image image = new Image();
		image.setSrc(imgUrl);
		items.add(image);
		return image;
	}
}
