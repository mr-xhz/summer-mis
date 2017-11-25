package cn.cerc.jui.parts;

import cn.cerc.jdb.core.DataSet;
import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.DataSource;
import cn.cerc.jpage.core.IField;

public class UIForm extends UIComponent implements DataSource {

    public UIForm(Component owner) {
        super(owner);
    }

    @Override
    public void addField(IField field) {

    }

    @Override
    public DataSet getDataSet() {
        return null;
    }

    @Override
    public boolean isReadonly() {
        return false;
    }

    @Override
    public void updateValue(String id, String code) {

    }

}
