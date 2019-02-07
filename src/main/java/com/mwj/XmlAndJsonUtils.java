package com.mwj;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedHashMap;

/**
 * @Description:
 * @Author: mengweijin
 * @Date: Create in 2017/11/13 19:41
 * @Modified:
 */
public class XmlAndJsonUtils {

    /**
     * 格式化XML Dom4j方式
     * @param inputXML
     * @return
     */
    public static String formatXML(String inputXML) throws IOException, DocumentException {
        if (null == inputXML || "".equals(inputXML)) {
            return "";
        }
        // 去掉所有换行
        inputXML = inputXML.replaceAll("\\n", "");
        String resultXML = "";
        XMLWriter writer;

        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(inputXML));
        if (document != null) {
            StringWriter stringWriter = new StringWriter();
            // 8个空格
            OutputFormat format = new OutputFormat("        ", true);
            writer = new XMLWriter(stringWriter, format);
            writer.write(document);
            writer.flush();
            resultXML = stringWriter.getBuffer().toString();
            writer.close();
        }

        return resultXML;
    }

    /**
     * 格式化JSON
     * @param jsonStr
     * @return
     */
    public static String formatJSON(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr)) {
            return "";
        }
        // 去掉所有换行
        jsonStr = jsonStr.replaceAll("\\n", "");
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '{':
                case '[':
                    sb.append(current);
                    sb.append('\n');
                    indent++;
                    addIndentBlank(sb, indent);

                    jsonStr = jsonStr.substring(i + 1).trim();
                    i = -1;
                    break;
                case '}':
                case ']':
                    sb.append('\n');
                    indent--;
                    addIndentBlank(sb, indent);
                    sb.append(current);

                    jsonStr = jsonStr.substring(i + 1).trim();
                    i = -1;
                    break;
                case ',':
                    sb.append(current);
                    if (last != '\\') {
                        sb.append('\n');
                        addIndentBlank(sb, indent);

                        jsonStr = jsonStr.substring(i + 1).trim();
                        i = -1;
                    }
                    break;
                case ':':
                    sb.append(current);
                    // 去掉冒号后面的空格
                    jsonStr = jsonStr.substring(i + 1).trim();
                    i = -1;
                    break;
                default:
                    sb.append(current);
            }
        }

        String result = sb.toString().replaceAll("\\n\\s*\\n", "\n");
        result = result.replaceAll("\"\\s+:", "\":");
        result = result.replaceAll("\"\\s+,", "\",");
        return result;
    }

    /**
     * 添加8空格space
     * @param sb
     * @param indent
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append("        ");
        }
    }

    /**
     * xml 转　json
     * 存在相同名称的节点时，转换存在Bug,建议使用fastjson的方式转换。
     * @param xml
     * @return
     */
    public static String xml2Json(String xml) throws IOException, DocumentException {
        // 格式化xml
        xml = formatXML(xml);
        XmlMapper xmlMapper = new XmlMapper();
        ObjectMapper objMapper = new ObjectMapper();
        StringWriter sw = new StringWriter();

        JsonParser jp = xmlMapper.getFactory().createParser(xml);
        JsonGenerator jg = objMapper.getFactory().createGenerator(sw);
        while (jp.nextToken() != null) {
            jg.copyCurrentEvent(jp);
        }
        Tools.closeStream(jp, jg);

        return sw.toString();
    }

    /**
     * json 转 xml
     * @param jsonStr
     * @return
     */
    public static String json2Xml(String jsonStr) throws IOException {
        Object mapObj = json2Obj(jsonStr, LinkedHashMap.class);
        XmlMapper xml = new XmlMapper();
        String resultXml = xml.writeValueAsString(mapObj);

        resultXml = resultXml.replaceFirst("<LinkedHashMap>", "").trim();
        resultXml = resultXml.substring(0, resultXml.length() - "</LinkedHashMap>".length());

        return resultXml;
    }

    /**
     * json转javabean, map, list等对象
     *
     * @param json
     * @param cls
     * @return
     */
    public static Object json2Obj(String json, Class<?> cls) throws IOException {
        ObjectMapper objMapper = new ObjectMapper();
        Object obj = objMapper.readValue(json, cls);

        return obj;
    }

}
