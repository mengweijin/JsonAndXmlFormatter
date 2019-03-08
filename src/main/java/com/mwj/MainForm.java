package com.mwj;

import com.alibaba.fastjson.JSONObject;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

/**
 * @Description:
 * @Author: mengweijin
 * @Date: Create in 2017/11/13 19:11
 * @Modified:
 */
public class MainForm {
    private TextAreaMenu textArea1;
    private TextAreaMenu textArea2;
    private JToolBar toolBar1;
    private JPanel mainPanel;

    private JButton 清空Button;
    private JButton 格式化XMLButton;
    private JButton 格式化JSONButton;
    private JButton XML转JSONButton;
    private JButton JSON转XMLButton;
    private JScrollPane jscrollPane1;
    private JScrollPane jscrollPane2;
    private JButton 帮助Button;
    private JButton 显示行号Button;
    private JButton 查找Button;
    private JButton 打开文件Button;

    private boolean showLineNumber = true;

    public MainForm() {
        清空Button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                textArea1.setText("");
                textArea2.setText("");
                textArea1RequestFocus();
            }
        });
        格式化XMLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text1 = textArea1.getText().trim();
                if(!"".equals(text1)) {
                    try {
                        String formatXml = XmlAndJsonUtils.formatXml(text1);
                        textArea2.setText(formatXml);
                        textArea2ToFirstRow();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        Tools.msg(e1.getMessage());
                    }
                }
                textArea1RequestFocus();
            }
        });
        格式化JSONButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text1 = textArea1.getText().trim();
                if(!"".equals(text1)) {
                    String formatJson = XmlAndJsonUtils.formatFastJSON(JSONObject.parseObject(text1));
                    textArea2.setText(formatJson);
                    textArea2ToFirstRow();
                }
                textArea1RequestFocus();
            }
        });
        XML转JSONButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text1 = textArea1.getText().trim();
                if(!"".equals(text1)) {
                    try {
                        textArea2.setText(XmlAndJsonUtils.xml2Json(text1));
                        textArea2ToFirstRow();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        Tools.msg(e1.getMessage());
                    }
                }
                textArea1RequestFocus();
            }
        });
        JSON转XMLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text1 = textArea1.getText().trim();
                if(!"".equals(text1)) {
                    try {
                        String xmlStr = XmlAndJsonUtils.json2Xml(text1);
                        String formatXml = XmlAndJsonUtils.formatXml(xmlStr);
                        textArea2.setText(formatXml);
                        textArea2ToFirstRow();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        Tools.msg(e1.getMessage());
                    }
                }
                textArea1RequestFocus();
            }
        });
        帮助Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Tools.msg(Tools.loadFile(this.getClass().getClassLoader().getResourceAsStream("info.txt")));
                textArea1RequestFocus();
            }
        });
        显示行号Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(showLineNumber) {
                    // 隐藏行号
                    jscrollPane1.setRowHeaderView(null);
                    jscrollPane2.setRowHeaderView(null);
                    显示行号Button.setText("    显示行号（F7）    ");
                } else {
                    // 显示行号
                    jscrollPane1.setRowHeaderView(new LineNumberHeaderView());
                    jscrollPane2.setRowHeaderView(new LineNumberHeaderView());
                    显示行号Button.setText("    隐藏行号（F7）   ");
                }
                showLineNumber = !showLineNumber;
                textArea1RequestFocus();
            }
        });
        查找Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = Tools.inmsd("查找");
                if(keyword == null || "".equals(keyword)) {
                    textArea1RequestFocus();
                    return;
                }

                // 1、在textArea1中查找
                String textArea1Context = textArea1.getText() == null ? "" : textArea1.getText();
                ArrayList<Integer> list1 = searchAllIndex(keyword, textArea1Context);
                if(!list1.isEmpty()) {
                    try {
                        Highlighter highlighter1 = textArea1.getHighlighter();
                        highlighter1.removeAllHighlights();
                        int listSize = list1.size();
                        int index;
                        int keywordLength = keyword.length();
                        for (int i = 0; i < listSize; i++) {
                            index = list1.get(i);
                            highlighter1.addHighlight(index, index + keywordLength, DefaultHighlighter.DefaultPainter);
                        }
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }

                    // 设置光标位置
                    textArea1.setCaretPosition(list1.get(0));
                }

                // 2、在textArea2中查找
                String textArea2Context = textArea2.getText() == null ? "" : textArea2.getText();
                ArrayList<Integer> list2 = searchAllIndex(keyword, textArea2Context);
                if(!list2.isEmpty()) {
                    try {
                        Highlighter highlighter2 = textArea2.getHighlighter();
                        highlighter2.removeAllHighlights();
                        int listSize = list2.size();
                        int index;
                        int keywordLength = keyword.length();
                        for (int i = 0; i < listSize; i++) {
                            index = list2.get(i);
                            highlighter2.addHighlight(index, index + keywordLength, DefaultHighlighter.DefaultPainter);
                        }
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }

                    // 设置光标位置
                    textArea2.setCaretPosition(list2.get(0));
                }

                textArea1RequestFocus();
            }
        });
        打开文件Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();

                FileSystemView fsv = FileSystemView.getFileSystemView();
                fileChooser.setCurrentDirectory(fsv.getHomeDirectory());
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                Dimension dimension = new Dimension(mainPanel.getWidth()/2, mainPanel.getHeight()/2);
                fileChooser.setPreferredSize(dimension);
                // 设置文件过滤
                JsonAndXmlFileFilter fileFilter = new JsonAndXmlFileFilter();
                fileChooser.setFileFilter(fileFilter);
                int value = fileChooser.showOpenDialog(mainPanel);
                // 点击选择按钮
                if(value == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    textArea1.setText(Tools.loadFile(file));
                }

                textArea1RequestFocus();
            }
        });
    }

    public static void main(String[] args) {
        final MainForm mainForm = new MainForm();

        // 显示行号
        mainForm.jscrollPane1.setRowHeaderView(new LineNumberHeaderView());
        mainForm.jscrollPane2.setRowHeaderView(new LineNumberHeaderView());

        JFrame frame = new JFrame("JsonAndXmlFormatter    Version 1.0.1");
        frame.setContentPane(mainForm.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //注意,被打包进jar文件的资源,是不能通过路径来读取的
        //需要读取该资源时,可以通过URL和InputStream来进行,具体使用如下
        URL iconURL = mainForm.getClass().getClassLoader().getResource("logo.png");
        ImageIcon imageIcon = new ImageIcon(iconURL);

        frame.setIconImage(imageIcon.getImage());
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle rectangle = graphicsEnvironment.getMaximumWindowBounds();
        Point point = graphicsEnvironment.getCenterPoint();
        Dimension dimension = new Dimension(rectangle.width, rectangle.height);
        frame.setPreferredSize(dimension);
        frame.setMinimumSize(new Dimension(1300, 600));
        frame.setLocation(point.x - dimension.width/2, point.y - dimension.height/2);
        frame.setResizable(true);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent evt) {
                mainForm.textArea1RequestFocus();
            }
        });

        frame.pack();
        frame.setVisible(true);

        // 添加全局键盘事件
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                KeyEvent keyEvent = (KeyEvent) event;
                if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                    switch(keyEvent.getKeyCode()) {
                        // F1 ~ F9
                        case KeyEvent.VK_F1:
                            mainForm.清空Button.doClick();
                            break;
                        case KeyEvent.VK_F2:
                            mainForm.打开文件Button.doClick();
                            break;
                        case KeyEvent.VK_F3:
                            mainForm.格式化XMLButton.doClick();
                            break;
                        case KeyEvent.VK_F4:
                            mainForm.格式化JSONButton.doClick();
                            break;
                        case KeyEvent.VK_F5:
                            mainForm.XML转JSONButton.doClick();
                            break;
                        case KeyEvent.VK_F6:
                            mainForm.JSON转XMLButton.doClick();
                            break;
                        case KeyEvent.VK_F7:
                            mainForm.显示行号Button.doClick();
                            break;
                        case KeyEvent.VK_F8:
                            mainForm.查找Button.doClick();
                            break;
                        case KeyEvent.VK_F9:
                            mainForm.帮助Button.doClick();
                            break;
                        case KeyEvent.VK_F11:
                            if(mainForm.textArea1.hasFocus()) {
                                mainForm.textArea1.setCaretPosition(0);
                            } else {
                                mainForm.textArea2.setCaretPosition(0);
                            }
                            break;
                        case KeyEvent.VK_F12:
                            if(mainForm.textArea1.hasFocus()) {
                                mainForm.textArea1.setCaretPosition(mainForm.textArea1.getText().length());
                            } else {
                                mainForm.textArea2.setCaretPosition(mainForm.textArea2.getText().length());
                            }
                            break;
                        case KeyEvent.VK_Z:
                            if(keyEvent.isControlDown()) {
                                if(mainForm.textArea1.hasFocus()) {
                                    mainForm.textArea1.undo();
                                } else {
                                    mainForm.textArea2.undo();
                                }
                            }
                            break;
                        case KeyEvent.VK_R:
                            if(keyEvent.isControlDown()) {
                                if(mainForm.textArea1.hasFocus()) {
                                    mainForm.textArea1.redo();
                                } else {
                                    mainForm.textArea2.redo();
                                }
                            }
                            break;
                        case KeyEvent.VK_F:
                            if(keyEvent.isControlDown()) {
                                mainForm.查找Button.doClick();
                            }
                            break;
                    }
                }
            }
        }, AWTEvent.KEY_EVENT_MASK);

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    /**
     * textArea1 获得焦点
     */
    private void textArea1RequestFocus() {
        textArea1.requestFocus();
    }

    /**
     * textArea1 文本定位到行首
     */
    private void textArea1ToFirstRow() {
        textArea1.setCaretPosition(0);
    }

    /**
     * textArea1 文本定位到行尾
     */
    private void textArea1ToEndRow() {
        textArea1.setCaretPosition(textArea1.getText().length());
    }

    /**
     * textArea2 文本定位到行首
     */
    private void textArea2ToFirstRow() {
        textArea2.setCaretPosition(0);
    }

    /**
     * textArea2 文本定位到行尾
     */
    private void textArea2ToEndRow() {
        textArea2.setCaretPosition(textArea2.getText().length());
    }

    /**
     * 查找字符串key在text中出现的所有的位置索引
     * @param key
     * @param text
     * @return
     */
    private ArrayList<Integer> searchAllIndex(String key, String text) {
        String keyLower = key.toLowerCase(Locale.CHINA);
        String textLower = text.toLowerCase(Locale.CHINA);
        ArrayList<Integer> list = new ArrayList<Integer>();
        int index= textLower.indexOf(keyLower);//第一个出现的索引位置

        while (index != -1) {
            list.add(index);
            index = textLower.indexOf(keyLower, index + keyLower.length());// 从这个索引往后开始第一个出现的位置
        }

        return list;
    }
}
