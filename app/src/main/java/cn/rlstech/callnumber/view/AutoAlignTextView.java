package cn.rlstech.callnumber.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.TextView;

/**
 * 内容根据行数进行左对齐或居中
 * Created by huangYx on 2017/3/6.
 */
public class AutoAlignTextView extends TextView {
    public AutoAlignTextView(Context context) {
        super(context);
        init();
    }

    public AutoAlignTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoAlignTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                if (getLineCount() <= 1) {
                    if (getGravity() != Gravity.CENTER) {
                        setGravity(Gravity.CENTER);
                    }
                } else {
                    if (getGravity() != Gravity.LEFT) {
                        setGravity(Gravity.LEFT);
                    }
                }
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
