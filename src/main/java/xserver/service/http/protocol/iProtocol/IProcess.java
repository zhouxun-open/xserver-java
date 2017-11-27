package xserver.service.http.protocol.iProtocol;

import xserver.service.http.XEnv;
import xserver.service.http.protocol.XResp;

public interface IProcess {
    public XResp process(XEnv env);
}
