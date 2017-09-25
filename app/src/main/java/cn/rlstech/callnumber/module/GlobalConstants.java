package cn.rlstech.callnumber.module;

import android.os.Environment;

/**
 * 全局常量
 * Project: trunk
 * Author: GaoYang
 */
public class GlobalConstants {
    // 存储根目录
    public static final String ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/likePad";
    // 崩溃日志存储目录
    public static final String CRASH_ROOT = ROOT + "/crash";
}
