package xserver.service.http.protocol.iProtocol;

import java.util.Map;

public class XRespContainer {
    public int httpCode;
    public Map<String, String> headers;
    public byte[] respBody;
}