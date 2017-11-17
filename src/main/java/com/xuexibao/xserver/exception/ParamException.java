package com.xuexibao.xserver.exception;

import com.xuexibao.xserver.Config;

public class ParamException extends LogicalException {

    private static final long serialVersionUID = -2434647353423193324L;

    public ParamException(String msg) {
        super(Config.Error_Code.args_err, msg);
    }
}
