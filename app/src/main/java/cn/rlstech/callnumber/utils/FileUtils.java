package cn.rlstech.callnumber.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import cn.rlstech.callnumber.module.ErrorMsgException;
import cn.rlstech.callnumber.module.GlobalConstants;

/**
 * Project: trunk
 * Author: GaoYang
 */
public class FileUtils {

    private static final String TAG = "FileUtils";

    private static void createDir(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (!file.exists() || !file.isDirectory()) {
                file.mkdirs();
            }
        }
    }

    public static void writeFile(String path, String str) {
        if (!TextUtils.isEmpty(path)) {
            File f = new File(path);
            createDir(f.getParent());
            FileWriter fw = null;
            try {
                fw = new FileWriter(f);
                fw.write(str == null ? "" : str);
                fw.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fw != null) {
                    try {
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
