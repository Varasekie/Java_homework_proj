
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;

public class StudentJPanel extends PersonJPanel {

    private JRadioButton[] policy;
    private String[] str = {"学号"};
    private JPanel[] jPanels = new JPanel[str.length];
    private JTextField[] jTextFields = new JTextField[str.length];
    //这里重新编辑，要仿照成下拉框的模式
    //院，专业
    public JComboBox<String> academy_combo,specity_combo;
//    public static String[] academies = {"计算机","汽轨"};
//    public static String[][]specities = {{"软工","数媒","大数据"},{"汽"}};

    private MutableJTree tree_spec;

    public StudentJPanel(MutableJTree tree_city,MutableJTree tree_spec) {
        super(tree_city);
        this.tree_spec = tree_spec;


        this.setLayout(new GridLayout(9, 1));
        this.setBorder(new TitledBorder("学生"));

        for (int i = 0;i<str.length;i++){
            this.jPanels[i]=new JPanel(new GridLayout(1,2));
            this.jPanels[i].add(new JLabel(str[i]));
            this.jPanels[i].add(jTextFields[i] = new JTextField("1"));
            this.add(jPanels[i]);
        }


        //combox
        this.academy_combo = new JComboBox<String>();
        this.academy_combo.addActionListener(this);
        //这里都不应该是null的，为什么传进去actionPerformed就是那个啊
        this.add(this.specity_combo = new JComboBox<String>());
        this.specity_combo.addActionListener(this);
        this.add(this.academy_combo);
        this.add(this.specity_combo);

        //团员
        JPanel poli = new JPanel(new GridLayout(1, 3));
        poli.add(new JLabel("团员"));
        String[] poli_str = {"是", "不是"};
        ButtonGroup poli_bg = new ButtonGroup();
        this.policy = new JRadioButton[poli_str.length];
        for (int i = 0; i < poli_str.length; i++) {
            this.policy[i] = new JRadioButton(poli_str[i]);
            poli_bg.add(this.policy[i]);
            poli.add(this.policy[i]);
        }
        this.policy[0].setSelected(true);
        this.add(poli);
        this.tree_spec.addChild(tree_spec.root,this.academy_combo);

        this.setVisible(true);
    }

    public void set(Person p){
        super.set(p);
        if (p instanceof Student){
            Student s = (Student)p;
            this.jTextFields[0].setText(s.number);
            this.jTextFields[1].setText(s.academy);
            this.jTextFields[2].setText(s.speciality);
            if (s.policy.equals("是")){
                this.policy[0].setSelected(true);
            }else this.policy[1].setSelected(true);
        }
    }

    public Student get() {
        //重新写另一个函数？可不可以并成一个啊//完成了，并成一个了6.2//22:52
        //number
        try {
            String poli = policy[0].isSelected() ? policy[0].getText() : policy[1].getText();
            return new Student(super.get(), (String) this.academy_combo.getSelectedItem(), (String) this.specity_combo.getSelectedItem(), this.jTextFields[0].getText(), poli);
        } catch (NumberFormatException ex2) {
            JOptionPane.showMessageDialog(this, ex2.getMessage() + "不能变成整数");
        }catch (NullPointerException ex1){
        }
        return null;
    }

    public void actionPerformed(ActionEvent e) {

        //院系不同改专业
        if (e.getSource() == this.academy_combo) {
            String academy = (String) this.academy_combo.getSelectedItem();
            TreeNode node = this.tree_spec.search(academy);
            if (node != null) {
                this.tree_spec.addChild(node, this.specity_combo);
            }
        }

        if (e.getSource() == this.province_combo) {
            String province = (String) this.province_combo.getSelectedItem();
            TreeNode node = super.tree_city.search(province);

            if (node != null) {
                //addchild是添加这个下拉框里面的内容
                this.tree_city.addChild(node, this.city_combo);
            }
        }
    }
}


