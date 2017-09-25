package cn.rlstech.callnumber.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import cn.rlstech.callnumber.R;
import cn.rlstech.callnumber.utils.AndroidUtil;

/**
 * 通用确认、取消类弹窗
 * Created by gaoyang on 2016/9/21.
 */
public class ConfirmDialog extends BaseBottomDialog implements View.OnClickListener {

    private static final String KEY_BTN_COUNT = "KEY_BTN_COUNT";
    private static final String KEY_POSITIVE = "KEY_POSITIVE";
    private static final String KEY_NEGATIVE = "KEY_NEGATIVE";
    private static final String KEY_MSG = "KEY_MSG";

    private static final int DFT_BTN_COUNT = 2;

    private ConfirmDialogListener mListener;

    public static ConfirmDialog newInstance(ConfirmDialogListener listener, CharSequence message) {
        return newInstance(listener, 2, null, null, message);
    }

    public static ConfirmDialog newInstance(ConfirmDialogListener listener, String positiveTxt, String negativeTxt, CharSequence message) {
        return newInstance(listener, 2, positiveTxt, negativeTxt, message);
    }

    public static ConfirmDialog newInstance(ConfirmDialogListener listener, int btnCount,
                                            String positiveTxt, String negativeTxt, CharSequence message) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setConfirmDialogListener(listener);
        dialog.setArguments(createBundle(btnCount, positiveTxt, negativeTxt, message));
        return dialog;
    }

    protected static Bundle createBundle(int btnCount, String positiveTxt, String negativeTxt, CharSequence message) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_POSITIVE, positiveTxt);
        bundle.putString(KEY_NEGATIVE, negativeTxt);
        bundle.putCharSequence(KEY_MSG, message);
        bundle.putInt(KEY_BTN_COUNT, btnCount);
        return bundle;
    }

    /**
     * 禁止直接使用
     */
    public ConfirmDialog() {
        super();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.dlg_confirm_layout;
    }

    @Override
    protected void initView(Bundle bundle) {
        TextView contentView = (TextView) findViewById(R.id.dlg_confirm_content_view);
        View btmLineView = findViewById(R.id.dlg_confirm_bottom_line_view);
        Button positiveView = (Button) findViewById(R.id.dlg_confirm_positive_view);
        positiveView.setOnClickListener(this);
        Button negativeView = (Button) findViewById(R.id.dlg_confirm_negative_view);
        negativeView.setOnClickListener(this);

        contentView.setText(bundle.getCharSequence(KEY_MSG));

        int btnCount = bundle.getInt(KEY_BTN_COUNT, DFT_BTN_COUNT);
        String positiveText = bundle.getString(KEY_POSITIVE);
        String negativeTxt = bundle.getString(KEY_NEGATIVE);
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
            switch (v.getId()) {
                case R.id.dlg_confirm_positive_view:
                    mListener.onClicked(ConfirmDialogButton.POSITIVE);
                    break;
                case R.id.dlg_confirm_negative_view:
                    mListener.onClicked(ConfirmDialogButton.NEGATIVE);
                    break;
            }
        }
    }

    protected void setConfirmDialogListener(ConfirmDialogListener listener) {
        this.mListener = listener;
    }

    @Override
    protected void initDialogStyle(Dialog dialog) {
        Window window = dialog.getWindow();
        dialog.setCanceledOnTouchOutside(false);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = Math.round(AndroidUtil.getScreenSize().first * 0.75f);
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlp.dimAmount = 0.5f;
        window.setAttributes(wlp);
        window.setGravity(Gravity.CENTER);
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

    public interface ConfirmDialogListener {
        /**
         * 如果只有一个按钮时，参数是POSITIVE
         *
         * @param btn 哪个按钮
         */
        void onClicked(ConfirmDialogButton btn);

        void onCancel();

        void onDismiss();
    }

    public enum ConfirmDialogButton {
        POSITIVE, NEGATIVE
    }
}
