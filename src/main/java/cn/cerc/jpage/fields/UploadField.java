package cn.cerc.jpage.fields;

import cn.cerc.jdb.core.Record;
import cn.cerc.jpage.core.Component;

public class UploadField extends AbstractField {

    public UploadField(Component owner, String name, String field) {
        super(owner, name, 5);
        this.setField(field);
        this.setHtmType("file");
    }

    @Override
    public String getText(Record ds) {
        return ds.getString(field);
    }
}
