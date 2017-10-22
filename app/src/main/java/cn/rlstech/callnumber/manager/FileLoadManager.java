package cn.rlstech.callnumber.manager;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.rlstech.callnumber.R;
import cn.rlstech.callnumber.application.GlobalApp;
import cn.rlstech.callnumber.utils.LogUtil;
import cn.rlstech.callnumber.utils.MD5Util;
import cn.rlstech.callnumber.utils.ToastUtil;

/**
 * Created by gaoyang on 2017/7/13.
 * <p>
 * 下载apk和压缩文件使用
 */

public class FileLoadManager {
    private static final String TAG = "FileLoadManager";

    public static final String AD_DIR = "/downloadLikeApk";

    private static Context mContext;
    private static FileLoadManager INSTANCE;

    private CopyOnWriteArraySet<String> mLoading;
    private CopyOnWriteArrayList<FileLoadListener> mListener;
    private ExecutorService mThreadPool;

    private FileLoadManager() {
        mLoading = new CopyOnWriteArraySet<>();
        mListener = new CopyOnWriteArrayList<>();
        mThreadPool = Executors.newCachedThreadPool();
    }

    public void init(Context context) {
        if (mContext == null) {
            mContext = context;
        }
    }

    public static synchronized FileLoadManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FileLoadManager();
        }
        return INSTANCE;
    }

    /**
     * 获取文件存储地址
     *
     * @return
     */
    public static String getPath() {
        return new File(Environment.getExternalStorageDirectory(), AD_DIR).toString();
    }

    /**
     * 添加下载监听
     *
     * @param listener
     * @return
     */
    public boolean addListener(FileLoadListener listener) {
        boolean result = false;
        if (mListener == null) {
            mListener = new CopyOnWriteArrayList<>();
        }
        if (listener != null) {
            if (!mListener.contains(listener) && mListener.add(listener)) {
                Log.d(TAG, "listener add success!");
                result = true;
            } else {
                Log.d(TAG, "listener is aleardy added!");
                result = false;
            }
        } else {
            LogUtil.d(TAG, "listener is null");
        }
        return result;
    }

    /**
     * 移除下载监听
     *
     * @param listener
     * @return
     */
    public boolean removeListener(FileLoadListener listener) {
        boolean result = false;
        if (mListener != null) {
            if (mListener.size() > 0 && mListener.contains(listener) && mListener.remove(listener)) {
                LogUtil.d(TAG, "remove listener success!");
                result = true;
            } else {
                Log.d(TAG, "listener not exit!");
                result = false;
            }
        } else {
            LogUtil.d(TAG, "listener is null");
        }
        return result;
    }

    /**
     * 开启下载
     *
     * @param fileUrl
     */
    public void commit(String fileUrl) {
        mThreadPool.submit(new FileLoadThread(mContext, fileUrl, mListener)); // 开启下载功能
    }

    /**
     *
     */
    private class FileLoadThread extends Thread {
        private static final String TAG = "FileLoadThread";

        private Context mContext;
        private CopyOnWriteArrayList<FileLoadListener> mListener;
        private NotificationManager mNm;
        private String mFileUrl;

        public FileLoadThread(Context context, String fileUrl, CopyOnWriteArrayList<FileLoadListener> listener) {
            mContext = context;
            mFileUrl = fileUrl;
            mListener = listener;
        }

        @Override
        public void run() {
            super.run();
            String filename = "CallNumber_1.0_release6.apk";
            mNm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            try {
                URL path = new URL(mFileUrl);
                HttpURLConnection connection = (HttpURLConnection) path.openConnection();
                connection.setReadTimeout(10 * 1000);
                connection.setConnectTimeout(10 * 1000);
                connection.setRequestMethod("GET");
                long fileLength = (long) connection.getContentLength(); // 文件包大小
                InputStream input = new BufferedInputStream(connection.getInputStream());

                File adDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), AD_DIR);
                if (!adDir.exists()) {
                    adDir.mkdirs();
                }
                File adFile = new File(adDir, filename);

                OutputStream output = new FileOutputStream(adFile);
                byte data[];
                if (mFileUrl.endsWith(".apk")) {
                    data = new byte[1024 * 30]; // 30k
                } else {
                    data = new byte[1024 * 2]; // 2K
                }
                long total = 0;
                int count = 0, tmp = 0;

                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
                builder.setContentTitle("正在下载: 为我点赞");
                builder.setSmallIcon(R.mipmap.icon_pad_like);

                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                    //int progress = (int) (total * 100 / fileLength); // 在免电中运行出现溢出现象，独立安装没有问题
                    int progress = (int) (total / (fileLength / 100));
                    if (tmp != 0 && tmp != progress) {
                        builder.setProgress(100, progress, false);
                        builder.setContentText(String.valueOf(progress) + "%");
                        mNm.notify("为我点赞".hashCode(), builder.build());
                    }
                    tmp = progress;
                }
                mNm.cancel("为我点赞".hashCode());
                output.flush();
                output.close();
                input.close();

                if (mListener != null) {
                    for (FileLoadListener l : mListener) {
                        l.onFileLoadListener();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 安装apk文件
     *
     * @param context
     * @param file
     */
    public static void installAPK(Context context, File file) {
        if (!file.exists()) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file://" + file.toString());
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public interface FileLoadListener {
        void onFileLoadListener();
    }
}
