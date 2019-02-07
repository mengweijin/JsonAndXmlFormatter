package com.mwj;

import javax.swing.*;
import javax.swing.event.MenuKeyEvent;
import javax.swing.undo.UndoManager;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;

/**
 * @Description:带有功能菜单的JTextArea
 * @Author: mengweijin
 * @Date: Create in 2017/11/18 14:01
 * @Modified:
 */

public class TextAreaMenu extends JTextArea implements MouseListener {

    // 弹出菜单
    private JPopupMenu pop = null;

    private JMenuItem copy = null;
    private JMenuItem paste = null;
    private JMenuItem cut = null;
    private JMenuItem selectAll = null;
    private JMenuItem undo = null;
    private JMenuItem redo = null;
    private JMenuItem firstRow = null;
    private JMenuItem endRow = null;

    // 撤销管理器
    UndoManager undoManager = new UndoManager();

    public TextAreaMenu() {
        super();
        init();
    }

    private void init() {
        this.addMouseListener(this);
        pop = new JPopupMenu();
        pop.add(copy = new JMenuItem("复制"));
        pop.add(paste = new JMenuItem("粘贴"));
        pop.add(cut = new JMenuItem("剪切"));
        pop.add(selectAll = new JMenuItem("全选"));
        pop.add(undo = new JMenuItem("撤销"));
        pop.add(redo = new JMenuItem("恢复"));
        pop.add(firstRow = new JMenuItem("行首"));
        pop.add(endRow = new JMenuItem("行尾"));

        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        selectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
        firstRow.setAccelerator(KeyStroke.getKeyStroke("F11"));
        endRow.setAccelerator(KeyStroke.getKeyStroke("F12"));

        copy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });
        paste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });
        cut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });
        selectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });
        undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });
        redo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });
        firstRow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });
        endRow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });
        // 添加撤销管理器
        this.getDocument().addUndoableEditListener(undoManager);

        this.add(pop);
    }

    /**
     * 菜单动作
     *
     * @param e
     */
    public void action(ActionEvent e) {
        String str = e.getActionCommand();

        if (str.equals(copy.getText())) {
            // 复制
            this.copy();
        } else if (str.equals(paste.getText())) {
            // 粘贴
            this.paste();
        } else if (str.equals(cut.getText())) {
            // 剪切
            this.cut();
        } else if (str.equals(selectAll.getText())) {
            // 全选
            this.selectAll();
        } else if (str.equals(undo.getText())) {
            // 撤销
            this.undo();
        } else if (str.equals(redo.getText())) {
            // 恢复
            this.redo();
        } else if (str.equals(firstRow.getText())) {
            // 去行首
            this.firstRow();
        } else if (str.equals(endRow.getText())) {
            // 去行尾
            this.endRow();
        }
    }

    public void undo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
        }

    }

    public void redo() {
        if (undoManager.canRedo()) {
            undoManager.redo();
        }
    }

    public void firstRow() {
        this.setCaretPosition(0);
    }

    public void endRow() {
        this.setCaretPosition(this.getText().length());
    }

    public JPopupMenu getPop() {
        return pop;
    }

    public void setPop(JPopupMenu pop) {
        this.pop = pop;
    }

    /**
     * 剪切板中是否有文本数据可供粘贴
     *
     * @return true为有文本数据
     */
    public boolean isClipboardString() {
        boolean b = false;
        Clipboard clipboard = this.getToolkit().getSystemClipboard();
        Transferable content = clipboard.getContents(this);
        try {
            if (content.getTransferData(DataFlavor.stringFlavor) instanceof String) {
                b = true;
            }
        } catch (Exception e) {
        }
        return b;
    }

    /**
     * 文本组件中是否具备复制的条件
     *
     * @return true为具备
     */
    public boolean isCanCopy() {
        boolean b = false;
        int start = this.getSelectionStart();
        int end = this.getSelectionEnd();
        if (start != end) {
            b = true;
        }
        return b;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // MouseEvent.BUTTON1=鼠标左键单击
        if (e.getButton() == MouseEvent.BUTTON1) {
            // 移除高亮
            this.getHighlighter().removeAllHighlights();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // MouseEvent.BUTTON3=鼠标右键单击
        if (e.getButton() == MouseEvent.BUTTON3) {
            copy.setEnabled(isCanCopy());
            paste.setEnabled(isClipboardString());
            cut.setEnabled(isCanCopy());
            pop.show(this, e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }
}
