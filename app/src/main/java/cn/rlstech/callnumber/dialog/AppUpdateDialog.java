package cn.rlstech.callnumber.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import cn.rlstech.callnumber.R;
import cn.rlstech.callnumber.utils.AndroidUtil;

/**
 * Created by cao on 2017/7/10.
 */

public class AppUpdateDialog extends BaseBottomDialog implements View.OnClickListener {
    private static final String TAG = "AppUpdateDialog";

    private static final String KEY_TITLE = "KEY_TITLE";
    private static final String KEY_BTN_COUNT = "KEY_BTN_COUNT";
    private static final String KEY_POSITIVE = "KEY_POSITIVE";
    private static final String KEY_NEGATIVE = "KEY_NEGATIVE";
    private static final String KEY_MSG = "KEY_MSG";

    private static final int DFT_BTN_COUNT = 2;


    private AppUpdateDialogListener mListener;

    public static AppUpdateDialog newInstance(AppUpdateDialogListener listener, CharSequence message) {
        return newInstance(listener, 2, null, null, null, message);
    }

    public static AppUpdateDialog newInstance(AppUpdateDialogListener listener, String title, String positiveTxt, String negativeTxt, CharSequence message) {
        return newInstance(listener, 2, title, positiveTxt, negativeTxt, message);
    }

    public static AppUpdateDialog newInstance(AppUpdateDialogListener listener, int btnCount,
                                              String title, String positiveTxt, String negativeTxt, CharSequence message) {
        AppUpdateDialog dialog = new AppUpdateDialog();
        dialog.setConfirmDialogListener(listener);
        dialog.setArguments(createBundle(btnCount, title, positiveTxt, negativeTxt, message));
        return dialog;
    }

    protected static Bundle createBundle(int btnCount, String title, String positiveTxt, String negativeTxt, CharSequence message) {
        Bundle bundle = new Bundle();

        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_POSITIVE, positiveTxt);
        bundle.putString(KEY_NEGATIVE, negativeTxt);
        bundle.putCharSequence(KEY_MSG, message);
        bundle.putInt(KEY_BTN_COUNT, btnCount);
        return bundle;
    }

    /**
     * 禁止直接使用
     */
    public AppUpdateDialog() {
        super();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.dlg_confirm_layout;
    }

    @Override
    protected void initView(Bundle bundle) {
        TextView titleView = (TextView) findViewById(R.id.dlg_confirm_title_view);
        ((ViewGroup) titleView.getParent()).setBackgroundResource(R.drawable.dialog_app_update_bg);
        TextView contentView = (TextView) findViewById(R.id.dlg_confirm_content_view);
        View btmLineView = findViewById(R.id.dlg_confirm_bottom_line_view);
        Button positiveView = (Button) findViewById(R.id.dlg_confirm_positive_view);
        positiveView.setOnClickListener(this);
        Button negativeView = (Button) findViewById(R.id.dlg_confirm_negative_view);
        negativeView.setOnClickListener(this);

        positiveView.setTextColor(getResources().getColor(R.color.main_tab_txt_selected));


        int btnCount = bundle.getInt(KEY_BTN_COUNT, DFT_BTN_COUNT);
        String title = bundle.getString(KEY_TITLE);
        String positiveText = bundle.getString(KEY_POSITIVE);
        String negativeTxt = bundle.getString(KEY_NEGATIVE);

        contentView.setText(bundle.getCharSequence(KEY_MSG));

        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
        }
        if (btnCount == 1) {
            if (TextUtils.isEmpty(positiveText)) {
                positiveText = negativeTxt;
            }
            if (TextUtils.isEmpty(positiveText)) {
                positiveText = getString(R.string.confirm);
            }
            positiveView.setText(positiveText);
            btmLineView.setVisibility(View.GONE);
            negativeView.setVisibility(View.GONE);
        } else {
            if (TextUtils.isEmpty(positiveText)) {
                positiveText = getString(R.string.confirm);
            }
            if (TextUtils.isEmpty(negativeTxt)) {
                negativeTxt = getString(R.string.cancel);
            }
            positiveView.setText(positiveText);
            negativeView.setText(negativeTxt);
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (mListener != null) {
            if (v.getId() == R.id.dlg_confirm_positive_view) {
                mListener.onClicked(AppUpdateDialogButton.POSITIVE);
            } else if (v.getId() == R.id.dlg_confirm_negative_view) {
                mListener.onClicked(AppUpdateDialogButton.NEGATIVE);
            }
        }
    }

    protected void setConfirmDialogListener(AppUpdateDialogListener listener) {
        this.mListener = listener;
    }

    @Override
    protected void initDialogStyle(Dialog dialog) {
        Window window = dialog.getWindow();
        dialog.setCanceledOnTouchOutside(false);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = Math.round(AndroidUtil.getScreenSize().first);
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlp.dimAmount = 0.5f;
        window.setAttributes(wlp);
        window.setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mListener != null) {
            mListener.onDismiss();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (mListener != null) {
            mListener.onCancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mListener = null;
    }

    public interface AppUpdateDialogListener {
        /**
         * 如果只有一个按钮时，参数是POSITIVE
         *
         * @param btn 哪个按钮
         */
        void onClicked(AppUpdateDialogButton btn);

        void onCancel();

        void onDismiss();
    }

    public enum AppUpdateDialogButton {
        POSITIVE, NEGATIVE
    }
}
