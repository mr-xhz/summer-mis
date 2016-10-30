package cn.cerc.jpage.tools;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.jpage.common.Component;
import cn.cerc.jpage.other.Url_Record;

public class OperaPanel extends Component {
	// 操作提示
	private String readme;
	// 基本资料
	private List<String> lines;
	// 相关链接
	private List<Url_Record> menus;

	public OperaPanel(Component owner) {
		super(owner);
		this.setId("right");
	}

	public List<Url_Record> getMenus() {
		return menus;
	}

	public String getReadme() {
		return readme;
	}

	public void setReadme(String readme) {
		this.readme = readme;
	}

	public void addMenu(Url_Record item) {
		if (menus == null)
			menus = new ArrayList<>();
		menus.add(item);
	}

	public Url_Record addMenu(String caption, String url) {
		Url_Record item = new Url_Record(url, caption);
		addMenu(item);
		return item;
	}

	public List<String> getLines() {
		return lines;
	}

	public void addLine(String html) {
		if (lines == null)
			lines = new ArrayList<>();
		lines.add(html);
	}
}