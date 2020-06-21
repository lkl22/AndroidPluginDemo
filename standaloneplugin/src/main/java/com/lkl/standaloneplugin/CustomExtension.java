package com.lkl.standaloneplugin;

/**
 * 自定义扩展配置
 */
public class CustomExtension {
    /**
     * 出错时中断编译
     */
    private boolean abortOnError = true;
    /**
     * 是否允许Log
     */
    private boolean enableLog = true;
    /**
     * 是否开启Debug。Debug模式会输出更详细的Log。
     */
    private boolean enableDebug = false;

    public boolean isAbortOnError() {
        return abortOnError;
    }

    public void setAbortOnError(boolean abortOnError) {
        this.abortOnError = abortOnError;
    }

    public boolean isEnableLog() {
        return enableLog;
    }

    public void setEnableLog(boolean enableLog) {
        this.enableLog = enableLog;
    }

    public boolean isEnableDebug() {
        return enableDebug;
    }

    public void setEnableDebug(boolean enableDebug) {
        this.enableDebug = enableDebug;
    }

    @Override
    public String toString() {
        return "CustomExtension{" +
                "abortOnError=" + abortOnError +
                ", enableLog=" + enableLog +
                ", enableDebug=" + enableDebug +
                '}';
    }
}
