package cn.rlstech.callnumber.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import cn.rlstech.callnumber.R;

/**
 * 基础dialog
 * Created by huangYx on 2016/11/10.
 */
public abstract class BaseBottomDialog extends DialogFragment {

    private static final String KEY_REBUILD = "rebuild";
    // 允许昏暗
    protected static final String KEY_DIM = "dim";

    private View mRootLayout;
    private Dialog.OnDismissListener mDismissListener;
    protected boolean isDim = false;
    protected int width;
    protected int height;

    public BaseBottomDialog() {
    }

    public BaseBottomDialog(Context context) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        setStyle(R.style.dialog, 0);
        if (getArguments() != null) {
            isDim = getArguments().getBoolean(KEY_DIM, false);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        if (getActivity() != null && getActivity().isFinishing()) {
            return;
        }
        super.onStart();
        if (getDialog() != null) {
            initDialogStyle(getDialog());
        }
    }

    protected void initDialogStyle(Dialog dialog) {
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        if (width == 0)
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        else
            wlp.width = width;
        if (height == 0)
            wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        else
            wlp.height = height;
        if (isDim) {
            wlp.dimAmount = 0.5f;
        } else {
            wlp.dimAmount = 0f;
        }
        window.setAttributes(wlp);
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
    }

    @Nullable
    @Override
    final public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootLayout = inflater.inflate(getLayoutID(), null);
        boolean isRebuild = false;
        if (savedInstanceState != null) {
            isRebuild = savedInstanceState.getBoolean(KEY_REBUILD, false);
        }
        if (!isRebuild) {
            initView(savedInstanceState == null ? getArguments() : savedInstanceState);
        }
        return mRootLayout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_REBUILD, true);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(KEY_REBUILD, false)) {
                dismiss();
            }
        }
    }

    public View findViewById(int id) {
        return mRootLayout.findViewById(id);
    }

    abstract protected int getLayoutID();

    abstract protected void initView(Bundle savedInstanceState);

    public void show(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            show(activity.getFragmentManager());
        }
    }

    public void show(FragmentManager ft) {
        if (isShowing()) {
            return;
        }
        try {
            Fragment f = ft.findFragmentByTag(this.getClass().getName());
            if (f != null && f.isAdded()) {
                ft.beginTransaction().remove(f).commit();
            }
            super.show(ft, this.getClass().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        if (Build.VERSION.SDK_INT > 11) {
            dismissAllowingStateLoss();
        } else {
            try {
                super.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setOnDismissListener(Dialog.OnDismissListener listener) {
        this.mDismissListener = listener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mDismissListener != null) {
            mDismissListener.onDismiss(dialog);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }
}
