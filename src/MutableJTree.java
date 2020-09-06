
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;

public class MutableJTree extends JTree implements MouseListener, ActionListener {

    private DefaultTreeModel treeModel;//树模型
    protected DefaultMutableTreeNode root;//根节点
    private String filename;
    private JPopupMenu popupMenu;
    private JMenuItem[] menuItems;

    //构造一棵树，filename指定保存结点的文件名
    public MutableJTree(String filename) {
        super();
        this.filename = filename;
        //默认获得树模型
        this.treeModel = (DefaultTreeModel) this.getModel();
        this.root = null;
        this.readFrom(filename);
        //这里要比书上多加一行setroot……
        this.treeModel.setRoot(this.root);

        //快捷菜单
        this.popupMenu = new JPopupMenu();
        String mitems[] = {"插入子节点", "插入前一个兄弟节点", "插入后一个兄弟节点", "重命名", "删除", "保存"};
        this.menuItems = new JMenuItem[mitems.length];
        for (int i = 0; i < mitems.length; i++) {
            this.menuItems[i] = new JMenuItem(mitems[i]);
            this.popupMenu.add(this.menuItems[i]);
            this.menuItems[i].addActionListener(this);
        }
        this.add(this.popupMenu);
        //树组件的监听
        this.addMouseListener(this);

        //渲染图标
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setOpenIcon(new ImageIcon("files.png"));
        this.setCellRenderer(renderer);
    }

    //单击菜单项
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("保存")) {
            //横向的写进文件,自定义函数
            this.writeTo(this.root, this.filename);
            return;
        }

        DefaultMutableTreeNode selectnode = null, parent = null;
        //当前选中节点
        selectnode = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
        if (e.getActionCommand().equals("重命名")) {
            //重命名
            String s = JOptionPane.showInputDialog("名称", selectnode.getUserObject().toString());
            if (s != null) {
                selectnode.setUserObject(s);
            }
            return;
        }
        //三个插入节点
        if (e.getActionCommand().startsWith("输入")) {
            String nodename = JOptionPane.showInputDialog("名称");
            if (nodename != null) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodename);
                if (this.root == null && selectnode == null) {
                    this.root = node;
                } else if (e.getActionCommand().equals("插入子节点")) {
                    selectnode.add(node);
                } else {
                    parent = (DefaultMutableTreeNode) selectnode.getParent();
                    if (e.getActionCommand().equals("插入前一个兄弟节点")) {
                        parent.insert(node, parent.getIndex(selectnode));
                    } else parent.insert(node, parent.getIndex(selectnode) + 1);
                }

                this.treeModel.setRoot(this.root);
                this.expandPath(new TreePath(selectnode.getPath()));
            }
            return;
        }
        if (e.getActionCommand().equals("删除") && (JOptionPane.showConfirmDialog(null, "删除" + selectnode.toString())) == 0) {
            if (selectnode == this.root) {
                this.root = null;
            } else {
                parent = (DefaultMutableTreeNode) selectnode.getParent();
//                System.out.println(parent.toString());
                selectnode.removeFromParent();
                this.treeModel.setRoot(this.root);
                try{
                    this.expandPath(new TreePath(parent.getPath()));
                }catch (Exception e1){

                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == 3) {
            //鼠标位置
            int row = this.getRowForLocation(e.getX(), e.getY());
            //……这个判断条件可以优化
            if (this.root == null || (this.root != null && row != -1)) {
                this.setSelectionRow(row);
                //菜单有效
                for (int i = 0; i < this.menuItems.length; i++) {
                    this.menuItems[i].setEnabled(true);
                }

                //不能删除和重命名
                if (this.root == null) {
                    this.menuItems[3].setEnabled(false);
                    this.menuItems[4].setEnabled(false);
                }
                //不能插入
                if (this.root == null || row == 0) {
                    this.menuItems[1].setEnabled(false);
                    this.menuItems[2].setEnabled(false);
                }
                this.popupMenu.show(this, e.getX(), e.getY());
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void writeTo(DefaultMutableTreeNode root, String filename) {
        //传进来的是根节点
        try {
            Writer wr = new FileWriter(filename);

            wr.write(preorder(root, ""));
//            wr.write("test");
            wr.close();

//            System.out.println(preorder(root,""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFrom(String filename) {
        BufferedReader bufr = null;
        try {
            bufr = new BufferedReader(new FileReader(filename));
            String line = "";
            //插入子树中
            while ((line = bufr.readLine()) != null) {
//                System.out.println(line);
                insert(this.root, line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //插入
    private void insert(DefaultMutableTreeNode node, String str) {
        if (this.root == null) {
            //这个创建根节点……
            this.root = new DefaultMutableTreeNode(str);
        } else {
            str = str.substring(1);

            if (!str.substring(0, 1).equals(" ")) {
                node.add(new DefaultMutableTreeNode(str));
            } else insert((DefaultMutableTreeNode) node.getLastChild(), str);
            //递归调用，因为是平衡二叉树所以可以直接获取
        }
    }


    //缩进，函数嵌套函数
    private String preorder(DefaultMutableTreeNode node, String tab) {
        String str = "";
        if (node != null) {
            str = tab + node.toString() + "\r\n";
            int n = node.getChildCount();//获得孩子结点的个数
            for (int i = 0; i < n; i++) {
                str = " " + str + preorder((DefaultMutableTreeNode) node.getChildAt(i), " ");
            }

        }
        return str;
    }

    //给所有的下拉框添加字符串
    public void addChild(TreeNode node, JComboBox<String> comboBox) {
        if (node != null && comboBox != null) {
//            int time = 0;
            comboBox.removeAllItems();
            int n = node.getChildCount();
            for (int i = 0; i < n; i++) {
//                time++;
                //添加项，只能添加字符串，这里只添加了一次
                comboBox.addItem(node.getChildAt(i).toString());
            }
        }
    }

    public TreeNode search(String str) {
        return search(this.root, str);
    }
    //上下两个重载
    private TreeNode search(DefaultMutableTreeNode node, String str) {
        if (node == null || str == null) {
            return null;
        }
        if (node.toString().equals(str)) {
            return node;
        }
        //递归去找node

        int n = node.getChildCount();
        for (int i = 0; i < n; i++) {
            TreeNode find = search((DefaultMutableTreeNode) node.getChildAt(i), str);
            if (find != null) {
                return find;
            }
        }
        return null;
    }
}
