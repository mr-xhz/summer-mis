package cn.cerc.jpage.fields;

import cn.cerc.jdb.core.Record;
import cn.cerc.jpage.core.Component;

public class TextAreaField extends AbstractField {
	public TextAreaField(Component owner, String name, String field) {
		super(owner, name, 0);
		this.setField(field);
		this.setHtmlTag("textarea");
	}

	public TextAreaField(Component owner, String name, String field, int width) {
		super(owner, name, 0);
		this.setField(field);
		this.setWidth(width);
		this.setHtmlTag("textarea");
	}

	@Override
	public String getText(Record rs) {
		return getDefaultText(rs);
	}

}
