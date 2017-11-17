package com.xuexibao.xserver.service.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.util.Date;

import com.xuexibao.xserver.XServerLogConfig;
import com.xuexibao.xserver.exception.UnInitilized;
import com.xuexibao.xserver.util.Time;

class FileLogger {

    /**
     * 日志文件路径
     */
    private String path = "";

    /**
     * 日志文件最大大小
     */
    private long maxSize;

    /**
     * 距离上次检测文件的大小到目前写了多少条日志
     */
    private int checkCount;

    /**
     * 按天切分日志
     */
    private boolean rotated = false;

    /**
     * 当前写日志的日期
     */
    private String writtingDate;

    /**
     * 进程ID
     */
    private String pid = "";

    /**
     * 文件写入流
     */
    private FileWriter f = null;

    /**
     * 是否将日志打印到控制台
     */
    public static boolean verbose = false;

    /**
     * @param path
     *            日志文件路径
     */
    public FileLogger(String path) {
        new FileLogger(path, false, -1);
    }

    /**
     * @param path
     *            日志文件路径
     * @param rotated
     *            是否按天切分日志
     * @param maxSize
     *            按日志文件大小切分日志
     */
    public FileLogger(String path, boolean rotated, long maxSize) {

        this.path = path;
        this.rotated = rotated;
        this.maxSize = maxSize;

        for (String _segment : ManagementFactory.getRuntimeMXBean().getName().split("@")) {
            this.pid = _segment;
            break;
        }
    }

    private boolean _checkFile() throws IOException {

        if (this.path == null || this.path.isEmpty()) {
            return false;
        }

        boolean isEmptyFile = false;
        File file = new File(this.path);
        if (!file.exists() || file.length() == 0) {
            isEmptyFile = true;
        }

        if (this.f == null) {
            this.f = new FileWriter(this.path, true);
        }

        if (this.rotated) {

            String currentDate = Time.getDateStr();

            if (!isEmptyFile && this.writtingDate == null) {
                long lm = file.lastModified();
                this.writtingDate = Time.getDateStr(new Date(lm));
            }

            if (!isEmptyFile && !currentDate.equalsIgnoreCase(this.writtingDate)) {
                this.f.close();
                String tmp = this.path + "." + this.writtingDate;
                file.renameTo(new File(tmp));
                this.f = new FileWriter(this.path, true);
            }
            this.writtingDate = currentDate;

        } else if (this.maxSize > 0) {

            this.checkCount = ++this.checkCount % 100;
            if (this.checkCount == 0) {
                if (file.length() > this.maxSize) {
                    this.f.close();
                    String tmp = this.path + "." + Time.getDateStr() + " " + Time.currentTimeMillis();
                    file.renameTo(new File(tmp));
                    this.f = new FileWriter(this.path, true);
                }
            }
        }

        return true;
    }

    /**
     *
     * @param name
     *            模块名称
     * @param msg
     *            日志信息
     * @throws IOException
     */
    public synchronized void write(String name, String msg) throws IOException {
        if (!this._checkFile()) {
            return;
        }

        String logLine = String.format("%s %s %s %s\n", Time.getDateTimeStr(), this.pid, name, msg);

        if (FileLogger.verbose) {
            System.out.print(logLine);
        }
        this.f.write(logLine);
        this.f.flush();
    }

    /**
     * @param name
     *            模块名称
     * @param msg
     *            日志信息
     * @param e
     *            异常对象
     * @throws IOException
     */
    public void write(String name, String msg, Throwable e) throws IOException {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));

        msg = String.format("%s Exception: %s", msg, sw.toString());
        this.write(name, msg);
    }
}

public class XLogger {

    /**
     * m模块名称
     */
    private String moduleName;

    public XLogger(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * debug类日志
     *
     * @param msg
     *            日志信息
     * @param t
     *            异常对象
     */
    public void debug(String msg, Throwable t) {
        try {
            XLogger.debugLog.write(this.moduleName, msg, t);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 访问类日志
     *
     * @param msg
     *            日志信息
     * @param t
     *            异常对象
     */
    public void acc(String msg, Throwable t) {
        try {
            XLogger.accLog.write(this.moduleName, msg, t);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 普通信息类日志
     *
     * @param msg
     * @param t
     */
    public void info(String msg, Throwable t) {
        try {
            XLogger.infoLog.write(this.moduleName, msg, t);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 警告类日志
     *
     * @param msg
     * @param t
     */
    public void warn(String msg, Throwable t) {
        try {
            XLogger.warnLog.write(this.moduleName, msg, t);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 错误类日志
     *
     * @param msg
     * @param t
     */
    public void err(String msg, Throwable t) {
        try {
            XLogger.errLog.write(this.moduleName, msg, t);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 严重信息类日志
     *
     * @param msg
     * @param t
     */
    public void crit(String msg, Throwable t) {
        try {
            XLogger.critLog.write(this.moduleName, msg, t);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * debug类日志
     *
     * @param msg
     *            日志信息
     */
    public void debug(String msg) {
        try {
            XLogger.debugLog.write(this.moduleName, msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 访问类日志
     *
     * @param msg
     *            日志信息
     */
    public void acc(String msg) {
        try {
            XLogger.accLog.write(this.moduleName, msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 普通信息类日志
     *
     * @param msg
     */
    public void info(String msg) {
        try {
            XLogger.infoLog.write(this.moduleName, msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 警告类日志
     *
     * @param msg
     */
    public void warn(String msg) {
        try {
            XLogger.warnLog.write(this.moduleName, msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 错误类日志
     *
     * @param msg
     */
    public void err(String msg) {
        try {
            XLogger.errLog.write(this.moduleName, msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 严重信息类日志
     *
     * @param msg
     */
    public void crit(String msg) {
        try {
            XLogger.critLog.write(this.moduleName, msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ------------------------------------------------------------ */

    public static FileLogger debugLog, accLog, infoLog, warnLog, errLog, critLog;

    // 日志文件名称
    public static final String debugLogFileName = "/debug.log";
    public static final String accLogFileName = "/acc.log";
    public static final String infoLogFileName = "/info.log";
    public static final String warnLogFileName = "/warn.log";
    public static final String errLogFileName = "/err.log";
    public static final String critLogFileName = "/crit.log";

    public static XLogger globalLogger;

    public static XLogger getInst(String moduleName) throws UnInitilized {
        if (XLogger.debugLog == null) {
            throw new UnInitilized("Logger not inited.");
        }
        return new XLogger(moduleName);
    }

    public static XLogger getInst() {
        return XLogger.globalLogger;
    }

    /**
     * 设置是否将日志信息打印到控制台
     *
     * @param b
     */
    public static void setVerbose(boolean b) {
        FileLogger.verbose = b;
    }

    /**
     *
     * @param path
     *            日志路径
     * @param ratated
     *            是否按天切分日志
     * @param maxFileSize
     *            按文件大小切分日志时的最大文件容量
     */
    public static void init(XServerLogConfig logConfig) {
        File file = new File(logConfig.path);
        if (!file.exists()) {
            file.mkdirs();
        }
        logConfig.path = file.getAbsolutePath();

        XLogger.debugLog = new FileLogger(logConfig.path + XLogger.debugLogFileName, logConfig.ratated,
                logConfig.maxFileSize);
        XLogger.accLog = new FileLogger(logConfig.path + XLogger.accLogFileName, logConfig.ratated,
                logConfig.maxFileSize);
        XLogger.infoLog = new FileLogger(logConfig.path + XLogger.infoLogFileName, logConfig.ratated,
                logConfig.maxFileSize);
        XLogger.warnLog = new FileLogger(logConfig.path + XLogger.warnLogFileName, logConfig.ratated,
                logConfig.maxFileSize);
        XLogger.errLog = new FileLogger(logConfig.path + XLogger.errLogFileName, logConfig.ratated,
                logConfig.maxFileSize);
        XLogger.critLog = new FileLogger(logConfig.path + XLogger.critLogFileName, logConfig.ratated,
                logConfig.maxFileSize);

        XLogger.globalLogger = new XLogger("-");
    }
}
