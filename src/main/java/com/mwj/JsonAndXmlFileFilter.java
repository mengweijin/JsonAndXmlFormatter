package com.mwj;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Arrays;
import java.util.Locale;

/**
 * @Description:
 * @Author: mengweijin
 * @Date: Create in 2017/11/18 13:24
 * @Modified:
 */
public class JsonAndXmlFileFilter extends FileFilter {
    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        for (String str: Tools.FILE_TYPE) {
            sb.append("*." + str + ";");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public boolean accept(File file) {
        if(file.isDirectory()){
            return true;
        }
        String fileSuffix = Tools.getFileSuffix(file);
        if(fileSuffix == null) {
            return false;
        }
        if(Arrays.asList(Tools.FILE_TYPE).contains(fileSuffix.toLowerCase(Locale.CHINA))) {
            return true;
        }

        return false;
    }
}