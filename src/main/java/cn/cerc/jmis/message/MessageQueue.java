package cn.cerc.jmis.message;

import cn.cerc.jbean.core.AppHandle;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.core.Record;
import cn.cerc.jdb.core.TDate;
import cn.cerc.jdb.queue.QueueDB;
import cn.cerc.jdb.queue.QueueMode;
import cn.cerc.jdb.queue.QueueQuery;
import cn.cerc.jpage.core.UrlRecord;

public class MessageQueue {
	private String corpNo;
	private String userCode;
	private String subject;
	private StringBuilder content = new StringBuilder();
	private MessageLevel level = MessageLevel.General;
	private int process;

	public MessageQueue() {
	}

	public MessageQueue(String userCode) {
		this.userCode = userCode;
	}

	public MessageQueue(String userCode, String subject) {
		this.userCode = userCode;

		if (subject.length() > 80) {
			this.subject = subject.substring(0, 77) + "...";
			this.content.append(subject);
		} else {
			this.subject = subject;
		}
	}

	public void send(IHandle handle) {
		if (subject == null || "".equals(subject)) {
			throw new RuntimeException("消息标题不允许为空");
		}

		if (userCode == null || "".equals(userCode)) {
			throw new RuntimeException("用户代码不允许为空");
		}

		String sendCorpNo = corpNo != null ? corpNo : handle.getCorpNo();
		if ("".equals(sendCorpNo)) {
			throw new RuntimeException("公司别不允许为空");
		}

		// 将消息发送至阿里云MNS
		QueueQuery query = new QueueQuery(handle);
		query.setQueueMode(QueueMode.append);
		query.add("select * from %s", QueueDB.MESSAGE);
		query.open();

		Record headIn = query.getHead();
		headIn.setField("CorpNo_", sendCorpNo);
		headIn.setField("UserCode_", userCode);
		headIn.setField("Level_", level.ordinal());
		headIn.setField("Process_", process);
		headIn.setField("Subject_", subject);
		headIn.setField("Content_", content.toString());
		query.save();
	}

	public String getContent() {
		return content.toString();
	}

	public void append(Object obj) {
		content.append(obj);
	}

	public void append(String format, Object... args) {
		content.append(String.format(format, args));
	}

	public MessageLevel getLevel() {
		return level;
	}

	public MessageQueue setLevel(MessageLevel level) {
		this.level = level;
		return this;
	}

	public String getUserCode() {
		return userCode;
	}

	public MessageQueue setUserCode(String userCode) {
		this.userCode = userCode;
		return this;
	}

	public String getCorpNo() {
		return corpNo;
	}

	public MessageQueue setCorpNo(String corpNo) {
		this.corpNo = corpNo;
		return this;
	}

	public String getSubject() {
		return subject;
	}

	public MessageQueue setSubject(String format, Object... args) {
		this.subject = String.format(format, args);
		return this;
	}

	public MessageQueue setContent(String content) {
		this.content = new StringBuilder(content);
		return this;
	}

	public int getProcess() {
		return process;
	}

	public MessageQueue setProcess(int process) {
		this.process = process;
		return this;
	}

	public static void main(String[] args) {
		AppHandle handle = new AppHandle();
		MessageQueue queue = new MessageQueue("91100124");
		queue.setCorpNo("911001");

		// 发送消息给上游
		queue.append("单据日期：%s ", TDate.Today());
		queue.append("<br />");
		queue.append("订货单号：%s ", "DE170617001");
		queue.append("<br />");
		queue.append("订货说明：%s", "(空)");
		queue.append("<br />");

		UrlRecord url = new UrlRecord();
		url.setName("点击查看");
		url.setSite("TFrmTranOE.modify");
		url.addParam("tbNo", "DE170617001");
		queue.append(String.format("订货明细：<a href=\"%s\">%s</a>", url.getUrl(), url.getName()));

		String subject = String.format("您的客户已向您采购商品，请接收并给予确认，谢谢！");
		queue.setSubject(subject);
		queue.send(handle);
	}

}
