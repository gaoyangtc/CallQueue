package com.gaoyang.bluetoothprinter.manager;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理基础类
 * Created by huangYx on 2016/11/4.
 */
public class BaseManager<T> {

    protected final List<T> mListeners;

    protected BaseManager() {
        mListeners = new ArrayList<T>();
    }

    final public void addListener(T t) {
        synchronized (mListeners) {
            if (t != null && !mListeners.contains(t)) {
                mListeners.add(t);
            }
        }
    }

    final public void removeListener(T t) {
        synchronized (mListeners) {
            if (t != null && mListeners.contains(t)) {
                mListeners.remove(t);
            }
        }
    }
}
