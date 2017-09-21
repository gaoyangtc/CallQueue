package cn.wuchengjun.callnumber.utils;

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;
import cn.wuchengjun.callnumber.R;
import cn.wuchengjun.callnumber.application.GlobalApp;

public class ToastUtil {

    private static Toast sToast;
    private static TextView mToastTxtView;
    private static Handler sHandler;
    private static String sMsg;
    private static int sMaxWidth;

    static {
        sHandler = new Handler(Looper.getMainLooper());
        sMaxWidth = Math.round(AndroidUtil.getScreenSize().first * 0.8f);
    }

    public static void show(int resId) {
        show(GlobalApp.getContext().getResources().getString(resId));
    }

    public static void show(String tipMsg) {
        sMsg = tipMsg;
        sHandler.post(showRunnable);
    }

    private static Runnable showRunnable = new Runnable() {
        @Override
        public void run() {
            if (sToast == null) {
                sToast = new Toast(GlobalApp.getContext());
                mToastTxtView = (TextView) LayoutInflater.from(GlobalApp.getContext()).inflate(R.layout.wgt_toast_layout, null);
                mToastTxtView.setMaxWidth(sMaxWidth);
                sToast.setView(mToastTxtView);
                sToast.setGravity(Gravity.CENTER, 0, 0);
            }
            mToastTxtView.setText(sMsg);
            sToast.setDuration(Toast.LENGTH_SHORT);
            // fixed 4.2.3
            try {
                sToast.show();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    };

    public static void cancel() {
        if (sToast != null) {
            sToast.cancel();
        }
    }
}
