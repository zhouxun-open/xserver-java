/**
 * @author Xun_Zhou
 */

package com.xuexibao.xserver.service.log;

import com.xuexibao.xserver.exception.UnInitilized;

public class LogCoreApi {
    private static LogCoreApi logCoreApi;

    public static LogCoreApi getInstance() throws UnInitilized {
        if (null == LogCoreApi.logCoreApi) {
            LogCoreApi.logCoreApi = new LogCoreApi();
        }
        return LogCoreApi.logCoreApi;
    }
}
