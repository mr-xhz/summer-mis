package cn.cerc.jmis.queue;

import org.apache.log4j.Logger;

import cn.cerc.jbean.client.LocalService;
import cn.cerc.jbean.core.BookHandle;
import cn.cerc.jbean.core.ServerConfig;
import cn.cerc.jdb.core.TDateTime;
import cn.cerc.jdb.queue.QueueMode;
import cn.cerc.jdb.queue.QueueQuery;
import cn.cerc.jdb.queue.QueueSession;
import cn.cerc.jmis.message.MessageProcess;
import cn.cerc.jmis.task.AbstractTask;
import net.sf.json.JSONObject;

public class ProcessQueue extends AbstractTask {
    private static final Logger log = Logger.getLogger(ProcessQueue.class);

    @Override
    public void execute() throws Exception {
        QueueQuery query = new QueueQuery(this);
        query.setQueueMode(QueueMode.recevie);
        query.add("select * from %s ", QueueSession.defaultQueue);
        query.open();
        if (!query.getActive())
            return;
        query.remove();

        // 建立服务执行环境
        String corpNo = query.getHead().getString("_corpNo_");
        if ("".equals(corpNo)) {
            log.error("_corpNo_ is null");
            return;
        }

        String userCode = query.getHead().getString("_userCode_");
        if ("".equals(userCode)) {
            log.error("_userCode_ is null");
            return;
        }

        String service = query.getHead().getString("_service_");
        if ("".equals(service)) {
            log.error("_service_ is null");
            return;
        }

        // 调用队列内容中指定的服务
        BookHandle bh = new BookHandle(this, corpNo);
        bh.setUserCode(userCode);
        LocalService svr = new LocalService(bh);
        svr.setService(service);
        svr.getDataIn().appendDataSet(query, true);

        String msgId = query.getHead().getString("_queueId_");
        JSONObject content = JSONObject.fromObject(query.getHead().getString("_content_"));

        LocalService app = new LocalService(bh, "SvrUserMessages.updateAsyncService");
        if (svr.exec()) {
            content.element("processTime", TDateTime.Now());
            content.element("dataOut", svr.getDataOut().getJSON());
            if (!app.exec("msgId", msgId, "process", MessageProcess.ok.ordinal(), "content", content.toString()))
                log.error(app.getMessage());
        } else {
            content.element("processTime", TDateTime.Now());
            content.element("dataOut", svr.getDataOut().getJSON());
            if (!app.exec("msgId", msgId, "process", MessageProcess.error.ordinal(), "content", content.toString()))
                log.error(app.getMessage());
        }
    }

    @Override
    public void run() {
        if (ServerConfig.enableTaskService())
            super.run();
    }

    public static void main(String[] args) {
        ProcessQueue obj = new ProcessQueue();
        obj.run();
    }
}
