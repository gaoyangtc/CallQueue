package cn.rlstech.callnumber.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class BusinessAdapter extends PagerAdapter {
    private List<View> mList;

    public BusinessAdapter() {
        mList = new ArrayList<>();
    }

    public void addItem(View itemView) {
        if (itemView != null && !mList.contains(itemView)) {
            mList.add(itemView);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mList.get(position));
        return mList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
