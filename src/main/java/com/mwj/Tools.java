package com.mwj;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

/**
 * @author mengweijin
 */
public class Tools {

    public static final String[] FILE_TYPE = {"xml", "json", "html", "htm", "txt", "csv", "jsp", "log"};

    public static void msg(String msg) {
        JOptionPane.showMessageDialog(null, msg);
    }

    public static int cfm(String msg) {
        return JOptionPane.showConfirmDialog(null, msg, "确认", JOptionPane.YES_NO_OPTION);
    }

    public static String inmsd(String msg) {
        return JOptionPane.showInputDialog(msg);
    }

    public static String loadFile(File file) {
        InputStream in = null;
        String result = "";

        if (!checkIsTextFile(file)) {
            msg("该类型文件不被允许！");
            return result;
        }

        try {
            in = new FileInputStream(file);
            result = loadFile(in);
        } catch (FileNotFoundException e) {
            Tools.msg(e.getMessage());
            e.printStackTrace();
        } finally {
            closeStream(in);
        }
        return result;
    }

    public static String loadFile(InputStream input) {
        InputStreamReader isr = null;
        BufferedReader br = null;
        StringBuilder info = new StringBuilder();
        String str;
        try {
            isr = new InputStreamReader(input, Charset.forName("UTF-8"));
            br = new BufferedReader(isr);
            while ((str = br.readLine()) != null) {
                info.append(str + '\n');
            }
        } catch (IOException e) {
            Tools.msg(e.getMessage());
            e.printStackTrace();
        } finally {
            closeStream(br, isr);
        }
        return info.toString();
    }

    public static void closeStream(Closeable... stream) {
        for (Closeable closeable : stream) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 检查文件是否被允许
     *
     * @param file
     * @return
     */
    public static boolean checkIsTextFile(File file) {

        boolean flag = false;
        String fileSuffix = getFileSuffix(file);
        if (fileSuffix == null) {
            return flag;
        }
        if (Arrays.asList(FILE_TYPE).contains(fileSuffix.toLowerCase(Locale.CHINA))) {
            flag = true;
        }

        return flag;
    }

    /**
     * 获取文件后缀
     */
    public static String getFileSuffix(File file) {
        if (file != null && file.exists() && file.isFile()) {
            String fileName = file.getName();
            if (fileName.contains(".")) {
                return fileName.substring(fileName.lastIndexOf(".") + 1);
            }
        }

        return null;
    }

}
