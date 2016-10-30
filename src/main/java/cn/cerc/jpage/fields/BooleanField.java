package cn.cerc.jpage.fields;

import cn.cerc.jdb.core.Record;
import cn.cerc.jpage.common.DataView;
import cn.cerc.jpage.common.HtmlWriter;
import cn.cerc.jpage.common.SearchItem;
import cn.cerc.jpage.grid.extjs.Column;

public class BooleanField extends StringField implements SearchItem {
	private String trueText = "是";
	private String falseText = "否";
	private String title;
	private boolean search;

	public BooleanField(DataView owner, String title, String field) {
		this(owner, title, field, 0);
	}

	public BooleanField(DataView owner, String title, String field, int width) {
		super(owner, title, field, width);
		this.setAlign("center");
	}

	@Override
	public String getText(Record dataSet) {
		if (dataSet == null)
			return null;
		return dataSet.getBoolean(field) ? trueText : falseText;
	}

	public BooleanField setBooleanText(String trueText, String falseText) {
		this.trueText = trueText;
		this.falseText = falseText;
		return this;
	}

	@Override
	public void output(HtmlWriter html) {
		if (!this.search) {
			html.println(String.format("<label for=\"%s\">%s</label>", this.getId(), this.getName() + "："));
			writeInput(html);
			if (this.title != null)
				html.print("<label for=\"%s\">%s</label>", this.getId(), this.title);
		} else {
			writeInput(html);
			html.println(String.format("<label for=\"%s\">%s</label>", this.getId(), this.getName()));
		}
	}

	private void writeInput(HtmlWriter html) {
		html.print(String.format("<input type=\"checkbox\" id=\"%s\" name=\"%s\" value=\"1\"", this.getId(),
				this.getId()));
		boolean val = false;
		Record dataSet = dataView != null ? dataView.getRecord() : null;
		if (dataSet != null)
			val = dataSet.getBoolean(field);
		if (val)
			html.print(" checked");
		if (this.isReadonly())
			html.print(" disabled");
		if (this.onclick != null)
			html.print(" onclick=\"%s\"", this.onclick);
		html.print(">");
	}

	public String getTitle() {
		return title;
	}

	public BooleanField setTitle(String title) {
		this.title = title;
		return this;
	}

	public boolean isSearch() {
		return search;
	}

	@Override
	public void setSearch(boolean search) {
		this.search = search;
	}

	@Override
	public Column getColumn() {
		Column column = super.getColumn();
		column.setEditor(null);
		return column;
	}
}