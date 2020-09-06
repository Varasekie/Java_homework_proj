
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PersonJPanel extends JPanel implements ActionListener {
    private JTextField text_name, text_date;
    protected JRadioButton[] radios;
//    private static String[] province = {"江苏", "浙江", "安徽"};
//    private static String[][] cities = {{"南京", "无锡", "苏州", "南通", "常州"}, {"杭州", "嘉兴", "义乌"}, {"合肥", "芜湖"}};
    public JComboBox<String> province_combo, city_combo;
    protected MutableJTree tree_city;

    public PersonJPanel(MutableJTree tree_city) {
        this.setBorder(new TitledBorder("Person"));
        this.setLayout(new GridLayout(5, 1));
        this.add(this.text_name = new JTextField("姓名"));
        this.add(this.text_date = new JTextField("2000年1月1日"));

        String[] str = {"男", "女"};
        JPanel sex = new JPanel(new GridLayout(1, 2));
        ButtonGroup sex_bg = new ButtonGroup();
        this.radios = new JRadioButton[str.length];
        for (int i = 0; i < str.length; i++) {
            this.radios[i] = new JRadioButton(str[i]);
            sex_bg.add(this.radios[i]);
            sex.add(this.radios[i]);
        }
        this.radios[0].setSelected(true);
        this.add(sex);

        this.tree_city = tree_city;

        //添加字符串
        this.province_combo = new JComboBox<>();
        this.city_combo = new JComboBox<>();
        this.add(this.province_combo);
        this.add(this.city_combo);
        this.tree_city.addChild(tree_city.root, this.province_combo);

    }

    public void set(Person per) {
        if (per == null) {
            return;
        }

        this.text_name.setText(per.name);
        this.text_date.setText(per.birthday.toString());
        if (per.gender.equals("男")) {
            this.radios[0].setSelected(true);
        } else this.radios[1].setSelected(true);

        this.province_combo.setSelectedItem(per.province);
        this.city_combo.setSelectedItem(per.city);
    }

    public Person get() {
        //sex
        String gender = radios[0].isSelected() ? radios[0].getText() : radios[1].getText();

        //date
        try {
            MyDate birth = new MyDate(this.text_date.getText());
            return new Person(text_name.getText(), birth, gender,
                    (String) province_combo.getSelectedItem(), (String) city_combo.getSelectedItem());
        } catch (DateFormatException ex1) {
            JOptionPane.showMessageDialog(this, ex1.getMessage());
        } catch (NumberFormatException ex2) {
            JOptionPane.showMessageDialog(this, ex2.getMessage() + "不转化成整数");
        }catch (NullPointerException ex3){
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //树的下拉框
        if (e.getSource() == this.province_combo) {
            String province = (String) this.province_combo.getSelectedItem();
            TreeNode node = tree_city.search(province);
            if (node != null) {
                //addchild是添加这个下拉框里面的内容
                this.tree_city.addChild(node, this.city_combo);
            }
        }
    }
}
