package xserver.exception;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

public class LogicalException extends Exception {

    private static final long serialVersionUID = -2247331292377127222L;

    public int status;
    public String msg;
    public Map<String, Object> reaultMap;
    public int httpCode = HttpServletResponse.SC_OK;

    public LogicalException(int status, String msg) {
        super(status + "\t" + msg);
        this.status = status;
        this.msg = msg;
        this.reaultMap = new HashMap<String, Object>();
    }
}
