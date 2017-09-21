package cn.wuchengjun.callnumber.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Project: trunk
 * Author: GaoYang
 * Date: 2017/1/23 0023
 */
public class StopMoveGridView extends GridView {
    public StopMoveGridView(Context context) {
        super(context);
    }

    public StopMoveGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StopMoveGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE){
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }
}
