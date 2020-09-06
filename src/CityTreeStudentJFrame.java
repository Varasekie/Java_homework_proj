import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

public class CityTreeStudentJFrame extends CityTreePersonJFrame implements ActionListener {
    private String speciality_filename;
    private static MutableJTree spec_tree;
    private LinkedList<Student> list_stu;
    private String objectFilename;
    private DefaultTableModel defaultTableModel;
    private JTable jTable;

    static {
        spec_tree = new MutableJTree("speciality.txt");

    }

    //上面的studentJPanel删掉参数
    public CityTreeStudentJFrame(String objectFilename, String[] titles, String speciality_filename) {
        super(objectFilename, titles, new StudentJPanel(tree, spec_tree));
        this.setTitle("student");
        this.setSize(1681, 600);

        this.objectFilename = objectFilename;
        this.speciality_filename = speciality_filename;

        this.list_stu = new LinkedList<Student>();
        //通过读取文件，来实现返回一个student
        CollectionFile.readFrom(objectFilename, this.list_stu);
        System.out.println(this.list_stu);
        //学生表格要添加列
        this.defaultTableModel = new DefaultTableModel(titles, 10);
        this.jTable = new JTable(this.defaultTableModel);

        //添加最右边的分割窗栏,是学生专业
        this.spec_tree = new MutableJTree(speciality_filename);
        this.spec_tree.addTreeSelectionListener(this);


        super.jp_tree.add(new JScrollPane(spec_tree));


        this.spec_tree.addSelectionRow(0);//选中根节点
        this.getContentPane().add(new JScrollPane(spec_tree), "East");

        //学生面板也要改
        this.setVisible(true);
    }


    public CityTreeStudentJFrame(String treeFilename, String objectFilename, String[] titles, PersonJPanel person) {
        super(objectFilename, titles, person);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        CollectionFile.writeTo(this.objectFilename, this.list_stu);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        //这个树的监听不能继承，super会报错

        if (e != null) {
//            展开当前节点
            this.tree.expandPath(e.getPath());
            this.spec_tree.expandPath(e.getPath());
        }
        //本来就应该是两个不同的节点
        TreeNode node_spec = (TreeNode) this.spec_tree.getLastSelectedPathComponent();
        TreeNode node = (TreeNode) this.tree.getLastSelectedPathComponent();


        //然后这个监听只能监听一个节点，如果要有两个的话……，会监听不到

//        if (node_spec != null) {

        if (node != null && node == this.tree.root) {
            //添加全部数据到表格
            addTable(new ProvinceCityFilter("", ""));
        } else if (node != null && node.getParent() == this.tree.root) {
            //选中省
            super.person.province_combo.setSelectedItem(node.toString());
            //自己加的，选中省的时候没有城市，选中null
            this.person.city_combo.setSelectedItem(null);
            if (node_spec.getParent() != null && node_spec.getParent().getParent() == this.spec_tree.root) {
                new province_specifyFilter(node.getParent().toString(), "", node_spec.getParent().toString(), node_spec.toString());
            } else addTable(new ProvinceCityFilter(node.toString(), ""));
        } else if (node != null && node.getParent() != null && node.getParent().getParent() == this.tree.root && node_spec != null) {
            //选中城市
            this.person.province_combo.setSelectedItem(node.getParent().toString());
            this.person.city_combo.setSelectedItem(node.toString());
            //选院
            if (node_spec.getParent() == this.spec_tree.root) {
                new province_specifyFilter(node.getParent().toString(), node.toString(), node_spec.toString(), "");
            } else if (node_spec.getParent().getParent() == this.spec_tree.root && node_spec.getParent() != null) {
                new province_specifyFilter(node.getParent().toString(), node.toString(), node_spec.getParent().toString(), node_spec.toString());
            } else addTable(new ProvinceCityFilter(node.getParent().toString(), node.toString()));
//
        }
        //一般会先选择城市，再选择专业，当node不论是否选择，都应当可以选择node_spec。
        //现在的版本是只有选了城市才能选专业

        //本质引用数据类型，应该指向同一块内存
        StudentJPanel s = (StudentJPanel) person;
        if (node != null) {
            if (node_spec != null && node_spec == this.spec_tree.root) {
                //添加全部数据到表格
                addTable(new specifyFilter("", ""));
            } else if (node_spec != null && node_spec.getParent() == this.spec_tree.root && node.getParent() == this.tree.root) {
                s.academy_combo.setSelectedItem(node_spec.toString());
                addTable(new province_specifyFilter(node.toString(), "", node_spec.toString(), ""));
            } else if (node_spec != null && node_spec.getParent() != null && node_spec.getParent().getParent() == this.spec_tree.root) {
                //选中专业

                s.academy_combo.setSelectedItem(node_spec.getParent().toString());
                s.specity_combo.setSelectedItem(node_spec.toString());
                s.province_combo.setSelectedItem(node.toString());
                s.city_combo.setSelectedItem(node.toString());
//            System.out.println("test");
                if (node.getParent() != null && node.getParent().getParent() == this.tree.root) {
                    addTable(new province_specifyFilter(node.getParent().toString(), node.toString(), node_spec.getParent().toString(), node_spec.toString()));
                } else
                    addTable(new province_specifyFilter("", "", node_spec.getParent().toString(), node_spec.toString()));
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        //第一次会有studentJPanel==null的空指针报错……为什么，但是if之后就ok了
        //这里用try catch是不是不太适合，毕竟只有刚开始第一次报错,而且不能写成&&，否则报错

        if (e.getActionCommand().equals("添加")) {
            Student student = (Student) super.person.get();
            //表格模型添加一行
            this.tableModel.addRow(Reflections.toArray(student, this.fields));
            this.list_stu.add(student);
            System.out.println(this.list_stu);
        } else if (e.getSource() == super.comboBoxes[0]) {
            //查找
            String fieldname = (String) super.comboBoxes[0].getSelectedItem();
            //对studentJpanel的东西进行过滤操作
            addTable(new FieldFilter<Student>((Student) super.person.get(), fieldname));
        } else if (e.getActionCommand().equals("删除选中多行")) {
            removeSelectedAll(this.jTable, this.defaultTableModel, this.list_stu);
        } else if (e.getSource() == super.comboBoxes[1]) {
            //排序
            //这里不是完全没按照那个排序吗
            String fieldname = (String) super.comboBoxes[1].getSelectedItem();
            Collections.sort(this.list_stu, new CompareField<Student>(fieldname));
            //当前选中根节点，添加全部数据到表格
            //？不然难道还不添加数据了吗
            if (Objects.requireNonNull(tree.getSelectionRows())[0] == 0 || Objects.requireNonNull(spec_tree.getSelectionRows())[0] == 0) {
                addTable(new specifyFilter("", ""));
//                System.out.println("两个都选上了");
            } else {
                this.tree.setSelectionRow(0);
                this.spec_tree.setSelectionRow(0);
            }

        }
    }

    public <T extends Person> void addTable(SearchFilter<T> filter) {
        for (int i = this.tableModel.getRowCount() - 1; i >= 0; i--) {
            this.tableModel.removeRow(i);
        }

        for (Iterator<? extends Person> it = this.list_stu.iterator(); it.hasNext(); ) {
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

    //这里全改成student了
    void removeSelectedAll(JTable jTable, DefaultTableModel tableModel, LinkedList<? extends Person> list) {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "空表格，不删除");
        } else {
            int[] rows = jTable.getSelectedRows();
            if (rows.length == 0) {
                JOptionPane.showMessageDialog(this, "请选中某一项");
            } else if (JOptionPane.showConfirmDialog(this, "删除选中多行？") == 0) {
                for (int i = rows.length - 1; i >= 0; i--) {
                    Student stu = get(this.tableModel, rows[i]);
                    list.remove(stu);
                    tableModel.removeRow(rows[i]);
                }
            }
        }
    }

    public Student get(TableModel tableModel, int i) {
        //对应构造方法
        //这边应该会对应表格的列
        return new Student((String) tableModel.getValueAt(i, 0), (MyDate) tableModel.getValueAt(i, 1),
                (String) tableModel.getValueAt(i, 2), (String) tableModel.getValueAt(i, 3),
                (String) tableModel.getValueAt(i, 4), (String) tableModel.getValueAt(i, 5),
                (String) tableModel.getValueAt(i, 6), (String) tableModel.getValueAt(i, 7), (String) tableModel.getValueAt(i, 8));
    }
    //要重写过滤器

    public static void main(String[] args) {
        String[] titles = {"name", "birthday", "sex", "province", "city", "academy", "speciality", "number", "policy"};
        String[] strings = {"name", "birthday", "sex", "province", "city"};

//        new CityTreePersonJFrame( "persons.obj", strings, new PersonJPanel(tree));
        new CityTreeStudentJFrame("student.obj", titles, "speciality.txt");
    }
}
