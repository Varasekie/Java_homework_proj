import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeNode;


public class CityTreePersonJFrame extends JFrame implements TreeSelectionListener, ActionListener, WindowListener {

    private String objectFilename;
    protected DefaultTableModel tableModel;
    protected JTable jTable;
    protected LinkedList<Person> list;
    protected PersonJPanel person;
    public JComboBox[] comboBoxes = new JComboBox[2];
    protected JPanel cmdPanel;
    protected Field[] fields;//实例数组，实现person的实例
    protected JPanel jp_tree;
    static MutableJTree tree;

    static {
        tree = new MutableJTree("cities.txt");
    }

    public CityTreePersonJFrame( String objectFilename, String[] titles, PersonJPanel person) {
        super("person");
        this.setSize(1126, 437);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.addWindowListener(this);
        this.objectFilename = objectFilename;
        //空双向链表
        this.list = new LinkedList<Person>();
        CollectionFile.readFrom(this.objectFilename, this.list);

        this.getContentPane().add(new JScrollPane(tree),"West");
        this.jp_tree = new JPanel();

        //上面的表格
        this.tableModel = new DefaultTableModel(titles, 0);
        this.jTable = new JTable(this.tableModel);
        //不用添加，添加的东西应该是在面板中addTable函数
        //？在哪啊我为什么不写完？？？？？？？
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.add(new JScrollPane(this.jTable));
        //添加person面板，下面
        this.person = person;
        this.person.setLayout(new GridLayout(1, 6));
        splitPane.add(this.person);
        this.getContentPane().add(splitPane,"Center");

        this.fields = Reflections.getFields(person.get(), titles.length);

        //控制面板
        this.cmdPanel = new JPanel();
        String[][] str = {{"添加", "删除选中多行"}, {"查找关键字", "排序关键字"}};
        for (int i = 0; i < str[0].length; i++) {
            JButton button = new JButton(str[0][i]);
            button.addActionListener(this);
            this.cmdPanel.add(button);
        }

        this.comboBoxes = new JComboBox[str[1].length];
        for (int i = 0; i < str[1].length; i++) {
            this.cmdPanel.add(new JLabel(str[1][i]));
            this.cmdPanel.add(this.comboBoxes[i] = new JComboBox<String>(Reflections.toString(fields)));
            this.comboBoxes[i].addActionListener(this);
        }
        this.getContentPane().add(cmdPanel,"South");


        this.setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        //放到panel里面了

        if (e.getActionCommand().equals("添加")) {

            Person per = this.person.get();
            //表格模型添加一行
            if (per != null){
                this.tableModel.addRow(Reflections.toArray(per, this.fields));
                this.list.add(per);
            }
        } else if (e.getSource() == this.comboBoxes[0]) {

            String fieldname = (String) this.comboBoxes[0].getSelectedItem();
            addTable(new FieldFilter<Person>(this.person.get(), fieldname));
        } else if (e.getActionCommand().equals("删除选中多行")) {
            removeSelectedAll(this.jTable, this.tableModel, this.list);
        } else if (e.getSource() == this.comboBoxes[1]) {
            //排序
            String fieldname = (String) this.comboBoxes[1].getSelectedItem();
//            System.out.println(this.list);
            Collections.sort(this.list, new CompareField<Person>(fieldname));
            //当前选中根节点，添加全部数据到表格
            if (tree.getSelectionRows()[0] == 0) {
                addTable(new ProvinceCityFilter("", ""));
            } else tree.setSelectionRow(0);
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        CollectionFile.writeTo(this.objectFilename, this.list);
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    //选中树节点的事件处理
    //这里只有一颗树啊,学生列表有两棵树
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (e != null) {
//            展开当前节点
            tree.expandPath(e.getPath());
        }

        TreeNode node = (TreeNode) tree.getLastSelectedPathComponent();
        if (node != null && node == tree.root) {
            //添加全部数据到表格
            addTable(new ProvinceCityFilter("", ""));
        } else if (node != null && node.getParent() == tree.root) {
            //选中省
            //这里不一定是node.tostring是正确的，要验证一下……
//            System.out.println(node.toString());
            this.person.province_combo.setSelectedItem(node.toString());
            this.person.city_combo.setSelectedItem(null);
            addTable(new ProvinceCityFilter(node.toString(), ""));
        } else if (node != null && node.getParent() != null && node.getParent().getParent() == this.tree.root) {
            //选中城市
            this.person.province_combo.setSelectedItem(node.getParent().toString());
            this.person.city_combo.setSelectedItem(node.toString());
            addTable(new ProvinceCityFilter(node.getParent().toString(), node.toString()));
        }
    }

    //根据指定的过滤器，在表格中添加内容
    public <T extends Person> void addTable(SearchFilter<T> filter) {
        for (int i = this.tableModel.getRowCount() - 1; i >= 0; i--) {
            this.tableModel.removeRow(i);
        }

        for (Iterator<? extends Person> it = this.list.iterator(); it.hasNext(); ) {
            T per = (T) it.next();
            //过滤器找指定条件，成功就表格加一行，数组指定列的值
            if (per instanceof Student) {
                if (filter.accept(per)) {
                    this.tableModel.addRow(Reflections.toArray(per, this.fields));
                }
            } else if (filter.accept(per)) {
                this.tableModel.addRow(Reflections.toArray(per, this.fields));
            }
        }
    }

    void removeSelectedAll(JTable jTable, DefaultTableModel tableModel, LinkedList<? extends Person> list) {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "空表格，不删除");
        } else {
            int[] rows = jTable.getSelectedRows();
            if (rows.length == 0) {
                JOptionPane.showMessageDialog(this, "请选中某一项");
            } else if (JOptionPane.showConfirmDialog(this, "删除选中多行？") == 0) {
                for (int i = rows.length - 1; i >= 0; i--) {
                    Person per = get(this.tableModel, rows[i]);
                    list.remove(per);
                    tableModel.removeRow(rows[i]);
                }
            }
        }
    }

    public Person get(TableModel tableModel, int i) {
        //对应构造方法
        //改过
        return new Person((String) tableModel.getValueAt(i, 0), new MyDate(tableModel.getValueAt(i, 1).toString()) ,
                (String) tableModel.getValueAt(i, 2), (String) tableModel.getValueAt(i, 3),
                (String) tableModel.getValueAt(i, 4));
    }

}
