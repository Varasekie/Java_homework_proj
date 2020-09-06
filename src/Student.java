import java.io.Serializable;

public class Student extends Person implements Serializable {

    public String academy, speciality, number, policy;
    //覆盖父类的count
    private static int count = 0;

    public Student(String name, MyDate birthday, String gender, String province, String city,
                   String academy, String speciality, String number, String policy) {
        super(name, birthday, gender, province, city);
        this.set(academy, speciality, number, policy);
    }

    public Student(Person per, String academy, String speciality, String number, String policy) {
        super(per);
        this.set(academy, speciality, number, policy);
        count++;
    }

    public Student() {
        super();
        this.set("", "", "", "不是");
        count++;
    }

    public void set(String academy, String speciality, String number, String policy) {
        this.academy = academy == null ? "" : academy;
        this.speciality = academy == null ? "" : speciality;
        this.number = number == null ? "" : number;
        this.policy = policy == null ? "" : policy;
    }

    @Override
    public void finalize() {
        super.finalize();
        //子类去调用父类的析构方法
        count--;
    }

    public static void howMany() {
        Person.howMany();
        //静态方法不能用super，无实例化
        System.out.println(Student.count + "student count number");
    }

    public String toString() {
        return super.toString() + "," + this.academy + "," + this.speciality + "," + this.number + "," + this.policy + "团员";
    }

}