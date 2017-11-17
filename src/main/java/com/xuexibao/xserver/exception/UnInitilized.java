package com.xuexibao.xserver.exception;

//@javadoc

public class UnInitilized extends Exception {

    private static final long serialVersionUID = 1230554992550126245L;

    public UnInitilized() {
        super();
    }

    public UnInitilized(String message) {
        super(message);
    }

    public UnInitilized(String message, Throwable cause) {
        super(message, cause);
    }

    public UnInitilized(Throwable cause) {
        super(cause);
    }

}
