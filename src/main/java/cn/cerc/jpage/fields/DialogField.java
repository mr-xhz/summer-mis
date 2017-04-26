package cn.cerc.jpage.fields;

import java.util.ArrayList;
import java.util.List;

public class DialogField {
	private List<String> params = new ArrayList<>();
	private String inputId;
	private String dialogfun;

	public DialogField(String dialogfun) {
		this.dialogfun = dialogfun;
	}

	public String getUrl() {
		StringBuilder build = new StringBuilder();

		if (dialogfun == null) {
			throw new RuntimeException("dialogfun is null");
		}

		build.append("javascript:");
		build.append(dialogfun);
		build.append("(");

		build.append("'");
		build.append(inputId);
		build.append("',");

		int i = 0;
		for (String param : params) {
			build.append("'");
			build.append(param);
			build.append("'");
			if (i != params.size() - 1) {
				build.append(",");
			}
			i++;
		}
		build.append(")");

		return build.toString();
	}

	public List<String> getParams() {
		return params;
	}

	public DialogField add(String param) {
		params.add(param);
		return this;
	}

	public String getDialogfun() {
		return dialogfun;
	}

	public void setDialogfun(String dialogfun) {
		this.dialogfun = dialogfun;
	}

	public String getInputId() {
		return inputId;
	}

	public DialogField setInputId(String inputId) {
		this.inputId = inputId;
		return this;
	}

	public static void main(String[] args) {
		DialogField obj = new DialogField("showVIpInfo");
		obj.setInputId("inputid");
		obj.add("1");
		obj.add("2");
		obj.add("3");
		System.out.println(obj.getUrl());
	}

}
