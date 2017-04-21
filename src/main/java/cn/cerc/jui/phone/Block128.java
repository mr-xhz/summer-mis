package cn.cerc.jui.phone;

import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jpage.vcl.Span;

/**
 * 首页消息提示
 */
public class Block128 extends Component {
	private Span title = new Span();

	public Block128(Component owner) {
		super(owner);
		title.setText("(title)");
	}

	@Override
	public void output(HtmlWriter html) {
		html.println("<!-- %s -->", this.getClass().getName());
		html.print("<div class='block128'>");
		title.output(html);
		html.println("</div>");
	}

	public Span getTitle() {
		return title;
	}
}
