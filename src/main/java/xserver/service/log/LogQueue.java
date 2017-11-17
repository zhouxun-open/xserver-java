package xserver.service.log;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class LogQueue extends Thread {
    public static LinkedBlockingQueue<ApiLogInfo> queue;
    public LogModel logModel;
    private static LogQueue logQueue;

    public LogQueue() {
        this.logModel = new LogModel();
        LogQueue.queue = new LinkedBlockingQueue<ApiLogInfo>();
    }

    public static LogQueue getInst() {
        if (null == LogQueue.logQueue) {
            LogQueue.logQueue = new LogQueue();
        }
        return LogQueue.logQueue;
    }

    public static synchronized void push(ApiLogInfo logInfo) {
        try {
            Boolean success = LogQueue.queue.offer(logInfo, 5, TimeUnit.MILLISECONDS);
            if (false == success) {
                LogThread logThread = new LogThread(logInfo);
                logThread.start();
            }
        } catch (InterruptedException e) {
            XLogger.getInst().crit("日志记录到数据库失败！", e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                ApiLogInfo logInfo = LogQueue.queue.take();
                this.logModel.logAccess(logInfo);
            } catch (Exception e) {
                XLogger.getInst().crit("向数据库中更新访问日志失败！", e);
            }
        }
    }

    public static class LogThread extends Thread {
        private ApiLogInfo apiLogInfo;

        public LogThread(ApiLogInfo logInfo) {
            this.apiLogInfo = logInfo;
        }

        @Override
        public void run() {
            try {
                LogQueue.queue.put(this.apiLogInfo);
            } catch (Exception e) {
                XLogger.getInst().crit("日志记录到数据库失败！", e);
            }
        }
    }
}
