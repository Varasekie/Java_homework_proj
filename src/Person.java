import java.io.Serializable;

public class Person implements Comparable<Person>, Serializable {
    public String name;
    public MyDate birthday;
    public String gender, province, city;
    private static int count = 0;

    //如果一个ArryList要进行排序，就会默认用compareTo
    @Override
    public int compareTo(Person o) {
        return this.birthday.compareTo(o.birthday);
    }


    public Person(String name, MyDate birthday, String gender, String province, String city) {
        this.set(name, birthday, gender, province, city);
        count++;
    }


    public Person(String name, MyDate birthday) {
        //调用本类声明的成员方法
        this(name, birthday, "", "", "");
        count++;
    }

    public Person(Person person) {
        this(person.name, new MyDate(person.birthday), person.gender, person.province, person.city);
    }


    public Person() {
        count++;
    }


    public void set(String name, MyDate birthday, String gender, String province, String city) {
        this.name = name == null ? "" : name;
        this.birthday = birthday;
        this.gender = gender == null ? "" : gender;
        this.city = city == null ? "" : city;
        this.province = province == null ? "" : province;
    }

    public static void howMany() {
        System.out.println(Person.count + "person  count");
    }

    public int getAge(int year) {
        return year - this.birthday.getYear();
    }

    public int getAge() {
        return getAge(MyDate.getThisYear());
    }

    public int older(Person per) {
        return this.getAge() - per.getAge();
    }


    public static Object search(Object[] objs, Object key) {
        if (objs == null) {
            return null;
        }
        for (Object obj : objs) {
            if (key.equals(obj)) {
                return obj;
            }
        }
        return null;
    }

    public static Person oldest(Person[] pers) {
        Person max = null;
        if (pers == null) {
            return null;
        }
        max = pers[0];
        for (Person p : pers) {
            if (max.getAge() < p.getAge()) {
                max = p;
            } else if (max.getAge() == p.getAge() && max.birthday.getMonth() > p.birthday.getMonth()) {
                max = p;
            } else if (max.getAge() == p.getAge() && max.birthday.getMonth() == p.birthday.getMonth()
                    && max.birthday.getDate() > p.birthday.getDate()) {
                max = p;
            }
        }
        return max;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Person) {
            Person p = (Person) obj;
            return this.name.equals(p.name) && this.birthday.equals(p.birthday) && this.city.equals(p.city)
                    && this.province.equals(p.province);
        }
        return false;
    }

    public String toString() {
        return this.name + "    出生日期" + birthday.toString() + " " + this.gender + " " + this.province + " " + this.city;
    }

    public void finalize() {
        count--;
    }

}


