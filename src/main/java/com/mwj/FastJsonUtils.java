package com.mwj;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.List;

/**
 * @Description: 依赖com.alibaba.fastjson
 * FASTJSON是当今处理json效率最高的json处理工具
    <dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.40</version>
    </dependency>
 * @Author: mengweijin
 * @Date: Create in 2017/11/18 11:15
 * @Modified:
 */
public class FastJsonUtils {

    public static String readFile(String path) throws IOException {
        File file=new File(path);
        FileInputStream fis = new FileInputStream(file);
        FileChannel fc = fis.getChannel();
        ByteBuffer bb = ByteBuffer.allocate(new Long(file.length()).intValue());
        //fc向buffer中读入数据
        fc.read(bb);
        bb.flip();
        String str=new String(bb.array(),"UTF-8");
        fc.close();
        fis.close();
        return str;

    }
    /**
     * xml转 json
     * @param xmlStr
     * @return
     * @throws DocumentException
     */
    public static JSONObject xml2Json(String xmlStr) throws DocumentException {
        Document doc = DocumentHelper.parseText(xmlStr);
        Element rootElement = doc.getRootElement();
        // 最终的json
        JSONObject json = new JSONObject(1, true);

        // 先添加一个根节点
        JSONObject rootEle = new JSONObject(16, true);
        json.put(rootElement.getName(), rootEle);
        dom4j2Json(rootElement, rootEle);
        return json;
    }

    /**
     * xml转json
     * @param element
     * @param json
     */
    public static void dom4j2Json(Element element, JSONObject json) {
        //如果是属性
        for(Object o: element.attributes()) {
            Attribute attr = (Attribute) o;
            if(!isEmpty(attr.getValue())) {
                json.put("@"+attr.getName(), attr.getValue());
            }
        }
        List<Element> chdEl = element.elements();
        if(chdEl.isEmpty() && !isEmpty(element.getText())){//如果没有子元素,只有一个值
            json.put(element.getName(), element.getText());
        }

        for(Element e: chdEl){//有子元素
            if(!e.elements().isEmpty()){//子元素也有子元素
                JSONObject chdjson = new JSONObject(16, true);
                dom4j2Json(e, chdjson);
                Object o = json.get(e.getName());
                if(o != null) {
                    JSONArray jsona = null;
                    if(o instanceof JSONObject){//如果此元素已存在,则转为jsonArray
                        JSONObject jsono = (JSONObject)o;
                        json.remove(e.getName());
                        jsona = new JSONArray();
                        jsona.add(jsono);
                        jsona.add(chdjson);
                    }
                    if(o instanceof JSONArray){
                        jsona = (JSONArray) o;
                        jsona.add(chdjson);
                    }
                    json.put(e.getName(), jsona);
                }else{
                    if(!chdjson.isEmpty()){
                        json.put(e.getName(), chdjson);
                    }
                }

            }else{//子元素没有子元素
                for(Object o: element.attributes()) {
                    Attribute attr = (Attribute)o;
                    if(!isEmpty(attr.getValue())){
                        json.put("@"+attr.getName(), attr.getValue());
                    }
                }
                json.put(e.getName(), e.getText());
            }
        }
    }

    public static boolean isEmpty(String str) {
        if (str == null || str.trim().isEmpty() || "null".equals(str)) {
            return true;
        }
        return false;
    }

}
