package cn.cerc.jui.vcl;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.jdb.core.Record;
import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.DataSource;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jpage.core.IField;
import cn.cerc.jpage.fields.AbstractField;

//此对象可以取消
@Deprecated
public class FieldSets extends Component implements DataSource {
	private DataSource dataView;
	private List<AbstractField> fields = new ArrayList<>();

	public FieldSets(DataSource dataView) {
		this.dataView = dataView;
	}

	@Override
	public void output(HtmlWriter html) {
		for (AbstractField field : fields) {
			field.output(html);
		}
	}

	@Override
	public void addField(IField field) {
		if (field instanceof AbstractField)
			fields.add((AbstractField) field);
		else
			throw new RuntimeException("不支持的数据类型：" + field.getClass().getName());
	}

	@Override
	public Record getRecord() {
		if (this.dataView == null)
			return null;
		return this.dataView.getRecord();
	}

	public List<AbstractField> getFields() {
		return this.fields;
	}

	public void remove(AbstractField field) {
		fields.remove(field);
	}
}