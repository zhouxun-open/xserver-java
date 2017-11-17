package xserver.service.log;

import java.sql.SQLException;

import xserver.exception.UnInitilized;

public class LogModel {
    // private XMySql logModel;

    public LogModel() {
        // TODO
        // logModel = new XMySql(url, user, password);
    }

    public void logAccess(ApiLogInfo logInfo) throws SQLException, UnInitilized {
        // String sql = "INSERT INTO `api_access_webapi`(`req_id`, `url`, `ip`,
        // `req_cookie`, `req_query`, `req_body`, `resp_cookie`, `resp_text`,
        // `resp_httpstatus`, `log_param`, `time`, `cost_time`, `user_id`,
        // `teacher_id`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // logModel.pUpdate(sql, logInfo.requestId, logInfo.url, logInfo.IP,
        // logInfo.reqCookies, logInfo.queryString,
        // logInfo.body, logInfo.respCookie, logInfo.responseTxt,
        // logInfo.httpStatus, logInfo.logErrors,
        // logInfo.time, logInfo.costTime, logInfo.userId, logInfo.teacherId);
    }
}
