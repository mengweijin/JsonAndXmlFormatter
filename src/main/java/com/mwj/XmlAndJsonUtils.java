package com.mwj;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.json.XML;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

/**
 * @Description:
 * @Author: mengweijin
 * @Date: Create in 2017/11/13 19:41
 * @Modified:
 */
public class XmlAndJsonUtils {

    /**
     * 使用fastjson
     * @param object 集合或者对象
     * @return
     */
    public static String formatFastJSON(Object object){
        return JSONObject.toJSONString(object,
                // 输出key时是否使用双引号,默认为true
                SerializerFeature.QuoteFieldNames,
                // Enum输出name()或者original,默认为false
                SerializerFeature.WriteEnumUsingToString,
                // 结果是否格式化,默认为false
                SerializerFeature.PrettyFormat,
                // 是否输出值为null的字段,默认为false
                SerializerFeature.WriteMapNullValue,
                // 字符类型字段如果为null,输出为"",而非null
                SerializerFeature.WriteNullStringAsEmpty,
                // Boolean字段如果为null,输出为false,而非null
                // SerializerFeature.WriteNullBooleanAsFalse,
                // 消除对同一对象循环引用的问题，默认为false
                SerializerFeature.DisableCircularReferenceDetect,
                // List字段如果为null,输出为[],而非null
                SerializerFeature.WriteNullListAsEmpty);
    }



    /**
     * use org.json
     * The number of spaces to add to each level of indentation.
     * @param json
     * @param indentFactor
     * @return
     */
    public static String formatJSON(String json, int indentFactor){
        if(indentFactor < 0){
            indentFactor = 0;
        }
        return new org.json.JSONObject(json).toString(indentFactor);
    }

    /**
     * use org.json xml
     * @param xml 转为json
     * @return
     */
    public static String xml2Json(String xml){
        return XML.toJSONObject(xml).toString(8);
    }


    /**
     * json 转 xml
     * @param json
     * @return
     */
    public static String json2Xml(String json) {
        org.json.JSONObject object = new org.json.JSONObject(json);
        return XML.toString(object);
    }

    /**
     * format xml
     * @param xml
     * @return
     * @throws TransformerException
     */
    public static String formatXml(String xml) {

        try(BufferedReader reader = new BufferedReader(new StringReader(xml));
            StringWriter writer = new StringWriter()){

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            transformer.transform(new StreamSource(reader), new StreamResult(writer));
            return writer.toString();
        } catch (TransformerException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
