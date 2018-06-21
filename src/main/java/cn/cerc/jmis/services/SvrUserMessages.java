package cn.cerc.jmis.services;

import static cn.cerc.jdb.other.utils.copy;

import java.math.BigInteger;

import cn.cerc.jbean.core.CustomService;
import cn.cerc.jbean.other.BufferType;
import cn.cerc.jbean.other.SystemTable;
import cn.cerc.jdb.cache.Buffer;
import cn.cerc.jdb.cache.IMemcache;
import cn.cerc.jdb.core.Record;
import cn.cerc.jdb.core.TDateTime;
import cn.cerc.jdb.mysql.SqlQuery;
import cn.cerc.jmis.message.JPushRecord;
import cn.cerc.jmis.message.MessageLevel;
import cn.cerc.jmis.message.MessageProcess;
import cn.cerc.jmis.message.MessageRecord;
import cn.cerc.jmis.queue.AsyncService;

//用户消息操作
public class SvrUserMessages extends CustomService {

    /**
     * @return 取出所有的等待处理的消息列表
     */
    public boolean getWaitList() {
        SqlQuery ds = new SqlQuery(this);
        ds.setMaximum(5);
        ds.add("select ms.UID_ from %s ms", SystemTable.get(SystemTable.getUserMessages));
        ds.add("where ms.Level_=%s", MessageLevel.Service.ordinal());
        ds.add("and ms.Process_=%s", MessageProcess.wait.ordinal());
        ds.open();
        this.getDataOut().appendDataSet(ds);
        return true;
    }

    /**
     * @return 增加一条新的消息记录
     */
    public boolean appendRecord() {
        Record headIn = getDataIn().getHead();
        String corpNo = headIn.getString("corpNo");
        String userCode = headIn.getString("userCode");
        String subject = headIn.getString("subject");
        String content = headIn.getString("content");
        int process = headIn.getInt("process");
        int level = headIn.getInt("level");

        // 若为异步任务消息请求
        if (level == MessageLevel.Service.ordinal()) {
            // 若已存在同一公司别同一种回算请求在排队或者执行中，则不重复插入回算请求
            SqlQuery ds2 = new SqlQuery(handle);
            ds2.setMaximum(1);
            ds2.add("select UID_ from %s ", SystemTable.get(SystemTable.getUserMessages));
            ds2.add("where CorpNo_='%s' ", corpNo);
            ds2.add("and Subject_='%s' ", subject);
            ds2.add("and Level_=4 and (Process_ = 1 or Process_=2)");
            ds2.open();
            if (ds2.size() > 0) {
                // 返回消息的编号
                getDataOut().getHead().setField("msgId", ds2.getBigInteger("UID_"));
                return true;
            }
        }

        SqlQuery cdsMsg = new SqlQuery(this);
        cdsMsg.add("select * from %s", SystemTable.get(SystemTable.getUserMessages));
        cdsMsg.setMaximum(0);
        cdsMsg.open();

        // 保存到数据库
        cdsMsg.append();
        cdsMsg.setField("CorpNo_", corpNo);
        cdsMsg.setField("UserCode_", userCode);
        cdsMsg.setField("Level_", level);
        cdsMsg.setField("Subject_", subject);
        if (content.length() > 0)
            cdsMsg.setField("Content_", content.toString());
        cdsMsg.setField("AppUser_", handle.getUserCode());
        cdsMsg.setField("AppDate_", TDateTime.Now());
        // 日志类消息默认为已读
        cdsMsg.setField("Status_", level == MessageLevel.Logger.ordinal() ? 1 : 0);
        cdsMsg.setField("Process_", process);
        cdsMsg.setField("Final_", false);
        cdsMsg.post();

        // 清除缓存
        IMemcache buff = Buffer.getMemcache();
        String buffKey = String.format("%d.%s.%s.%s", BufferType.getObject.ordinal(), MessageRecord.class, corpNo,
                userCode);
        buff.delete(buffKey);

        // 返回消息的编号
        getDataOut().getHead().setField("msgId", cdsMsg.getBigInteger("UID_"));
        return true;
    }

    /**
     * @return 读取指定的消息记录
     */
    public boolean readAsyncService() {
        String msgId = getDataIn().getHead().getString("msgId");

        SqlQuery ds = new SqlQuery(this);
        ds.add("select * from %s", SystemTable.get(SystemTable.getUserMessages));
        ds.add("where Level_=%s", MessageLevel.Service.ordinal());
        ds.add("and Process_=%s", MessageProcess.wait.ordinal());
        ds.add("and UID_='%s'", msgId);
        ds.open();
        if (ds.eof()) // 此任务可能被其它主机抢占
            return false;

        Record headOut = getDataOut().getHead();
        headOut.setField("corpNo", ds.getString("CorpNo_"));
        headOut.setField("userCode", ds.getString("UserCode_"));
        headOut.setField("subject", ds.getString("Subject_"));
        headOut.setField("content", ds.getString("Content_"));
        return true;
    }

    /**
     * @return 更新异步服务进度
     */
    public boolean updateAsyncService() {
        String msgId = getDataIn().getHead().getString("msgId");
        String content = getDataIn().getHead().getString("content");
        int process = getDataIn().getHead().getInt("process");

        SqlQuery cdsMsg = new SqlQuery(this);
        cdsMsg.add("select * from %s", SystemTable.get(SystemTable.getUserMessages));
        cdsMsg.add("where UID_='%s'", msgId);
        cdsMsg.open();
        if (cdsMsg.eof()) {
            // 此任务可能被其它主机抢占
            this.setMessage(String.format("消息号UID_ %s 不存在", msgId));
            return false;
        }
        cdsMsg.edit();
        cdsMsg.setField("Content_", content);
        cdsMsg.setField("Process_", process);
        cdsMsg.post();

        // 极光推送
        pushToJiGuang(cdsMsg);
        return true;
    }

    private void pushToJiGuang(SqlQuery cdsMsg) {
        String subject = cdsMsg.getString("Subject_");
        if ("".equals(subject)) {
            subject = copy(cdsMsg.getString("Content_"), 1, 80);
        }
        if (cdsMsg.getInt("Level_") == MessageLevel.Service.ordinal()) {
            subject += "【" + AsyncService.getProcessTitle(cdsMsg.getInt("Process_")) + "】";
        }

        String corpNo = cdsMsg.getString("CorpNo_");
        String userCode = cdsMsg.getString("UserCode_");
        BigInteger msgId = cdsMsg.getBigInteger("UID_");

        JPushRecord jPush = new JPushRecord(corpNo, userCode, msgId.toString());
        jPush.setAlert(subject);
        jPush.send(this);
    }

}
