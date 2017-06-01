package cn.cerc.jui.phone;

import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jpage.core.UrlRecord;
import cn.cerc.jpage.vcl.Image;

/**
 * 用于生成厂商、客户、帐套选择
 */
public class Block301 extends Component {
	private Image leftIcon = new Image();
	private String title = "(title)";
	private String describe = "(describe)";
	private Image rightIcon = new Image();
	private UrlRecord operator;
	private StringBuilder builder = new StringBuilder();

	public Block301(Component owner) {
		super(owner);
		operator = new UrlRecord();
		leftIcon.setSrc("jui/phone/block301-leftIcon.jpg");
		leftIcon.setRole("icon");

		rightIcon.setSrc("jui/phone/block301-rightIcon.png");
		rightIcon.setRole("right");
	}

	@Override
	public void output(HtmlWriter html) {
		html.println("<!-- %s -->", this.getClass().getName());
		html.print("<div class='block301'>");
		leftIcon.output(html);
		html.print("<a href='%s'>", operator.getUrl());
		html.print("<div>");
		html.print("<div role='title'>");
		html.print("<span role='title'>%s</span>", this.title);
		rightIcon.output(html);
		html.print("</div>");

		if (builder.length() > 0) {
			html.print("<div role='describe'>%s</div>", builder.toString());
		} else {
			html.print("<div role='describe'>%s</div>", describe);
		}

		html.print("</div>");
		html.print("</a>");
		html.print("<div style='clear: both'></div>");
		html.println("</div>");
	}

	public Image getLeftIcon() {
		return leftIcon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public UrlRecord getOperator() {
		return operator;
	}

	public void setOperator(UrlRecord operator) {
		this.operator = operator;
	}

	public Image getRightIcon() {
		return rightIcon;
	}

	public Block301 add(String describe) {
		builder.append(describe);
		return this;
	}

	public Block301 add(String format, Object... args) {
		builder.append(String.format(format, args));
		return this;
	}
}
