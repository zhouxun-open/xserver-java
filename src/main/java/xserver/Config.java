package xserver;

/**
 * 管理xserver启动时记载的配置文件
 */
public class Config {
    public static class Error_Code {
        public static final int service_err = -1;
        public static final int need_login = -2;// 慢慢删掉
        public static final int no_user = -3;
        public static final int no_content = -4;
        public static final int no_auth = -5;
        public static final int duplicate_loginname = -6;
        public static final int args_err = -7;
        public static final int account_exist = -8;
        public static final int method_err = -9;
        public static final int password_not_set = -10;
        public static final int server_logical_err = -100;
        public static final int method_limit_err = -101;
        public static final int login_on_other_device = -9527;
    }

    public static class Error_Code_Text {
        public static final String service_err = "unknown exception";
        public static final String need_login = "需要登录";// 慢慢删掉
        public static final String no_user = "没有此用户";
        public static final String no_content = "没有Post流";
        public static final String no_auth = "没有认证";
        public static final String duplicate_loginname = "用户名冲突";
        public static final String args_err = "参数错误";
        public static final String account_exist = "账号已存在";
        public static final String server_logical_err = "服务端逻辑异常";
        public static final String password_logical_err = "手机号未设置密码";
        public static final String method_err = "请求的方法不支持";
        public static final String method_limit_err = "请求太快，请稍后重试";
        public static final String login_on_other_device = "已在其他设备登录";
    }
}
