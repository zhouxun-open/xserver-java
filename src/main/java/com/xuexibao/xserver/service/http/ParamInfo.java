package com.xuexibao.xserver.service.http;

import java.util.ArrayList;
import java.util.List;

public class ParamInfo {
    public List<XParam> xparams;

    public ParamInfo() {
        this.xparams = new ArrayList<XParam>();
    }

    public void addXParam(XParam xparam) {
        this.xparams.add(xparam);
    }

    public boolean notEmpty() {
        if (null != this.xparams && 0 != this.xparams.size()) {
            return true;
        }
        return false;
    }
}