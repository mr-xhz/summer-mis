package cn.cerc.jmis.message;

import org.junit.Test;

import cn.cerc.jbean.rds.StubHandle;
import cn.cerc.jdb.core.TDate;
import cn.cerc.jmis.message.MessageQueue;
import cn.cerc.jpage.core.UrlRecord;

/**
 * 消息队列测试用例
 */
public class MessageQueueTest {
    private StubHandle handle;
    private MessageQueue queue;

    @Test
    public void setUp() {
        handle = new StubHandle("911001", "91100124");
        for (int i = 0; i < 5000; i++) {
            test_send();
        }
    }

    public void test_send() {
        queue = new MessageQueue(handle.getUserCode());
        queue.setCorpNo(handle.getCorpNo());

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
