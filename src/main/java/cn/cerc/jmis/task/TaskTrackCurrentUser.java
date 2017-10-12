package cn.cerc.jmis.task;

import cn.cerc.jbean.other.SystemTable;
import cn.cerc.jdb.core.TDateTime;
import cn.cerc.jdb.mysql.SqlSession;

/**
 * 清理在线用户记录表
 */
public class TaskTrackCurrentUser extends AbstractTask {

    @Override
    public void execute() {
        // 清理在线用户记录表
        SqlSession conn = (SqlSession) handle.getProperty(SqlSession.sessionId);

        // 删除超过100天的登录记录
        StringBuffer sql1 = new StringBuffer();
        sql1.append(String.format("delete from %s where datediff(now(),LoginTime_)>100", SystemTable.getCurrentUser));
        conn.execute(sql1.toString());

        // 清除所有未正常登录的用户记录
        StringBuffer sql2 = new StringBuffer();
        sql2.append(String.format("update %s set Viability_=-1 ", SystemTable.getCurrentUser));

        // 在线达24小时以上的用户
        sql2.append("where (Viability_>0) and (");
        sql2.append("(hour(timediff(now(),LoginTime_)) > 24 and LogoutTime_ is null)");

        // 在早上5点以后，清除昨天的用户
        if (TDateTime.Now().getHours() > 5) {
            sql2.append(" or (datediff(now(),LoginTime_)=1)");
        }

        // 已登出超过4小时的用户
        sql2.append(" or (hour(timediff(now(),LogoutTime_)))");
        sql2.append(")");
        conn.execute(sql2.toString());
    }

}
