package cn.wuchengjun.callnumber.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * adapter基类
 * Created by huangyx on 2015.9.14.
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {

    protected List<T> mDatas;
    protected Context mContext;
    protected LayoutInflater mInflater;

    public BaseListAdapter(Context context) {
        this.mDatas = new ArrayList<T>();
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
    }

    public void addDatas(List<T> datas) {
        if (datas != null && !datas.isEmpty()) {
            boolean changed = false;
            for (T t : datas) {
                if (!mDatas.contains(t)) {
                    mDatas.add(t);
                    changed = true;
                }
            }
            if (changed) {
                notifyDataSetChanged();
            }
        }
    }

    public void setDatas(List<T> datas) {
        mDatas.clear();
        if (datas != null && !datas.isEmpty()) {
            mDatas.addAll(datas);
        }
        notifyDataSetChanged();
    }

    public void addData(T t) {
        if (t != null && !mDatas.contains(t)) {
            mDatas.add(t);
            notifyDataSetChanged();
        }
    }

    public void deleteData(T t) {
        if (t != null && !mDatas.isEmpty() && mDatas.remove(t)) {
            notifyDataSetChanged();
        }
    }

    public void deleteDatas(List<T> datas) {
        boolean changed = false;
        if (datas != null && !datas.isEmpty() && !mDatas.isEmpty()) {
            for (T t : datas) {
                if (mDatas.contains(t)) {
                    mDatas.remove(t);
                    changed = true;
                }
            }
        }
        if (changed) {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void destroy() {
        mContext = null;
        mDatas.clear();
        mDatas = null;
        mInflater = null;
    }
}
