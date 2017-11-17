package xserver.service.log;

public class ApiLogInfo {
    public String requestId; // 请求标识
    public String url; // 接口名
    public String IP; // 客户端IP
    public String reqCookies; // 请求的cookie
    public String queryString; // 请求的queryString
    public String body; // 请求体
    public String respCookie; // 返回的cookie
    public String responseTxt; // 返回结果
    public String params; // 整理出来的请求参数
    public String logParams; // logParams
    public String logErrors; // 错误日志
    public Long time; // API访问的时间
    public int httpStatus; // http返回码
    public int costTime; // api调用耗时
    public String userId; // 学生id
    public String teacherId; // 教师id
    public Integer friendTeacherId; // 友好老师Id
}