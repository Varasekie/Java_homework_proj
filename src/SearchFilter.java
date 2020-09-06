import java.lang.reflect.Field;

public interface SearchFilter<T> {
    public abstract boolean accept(T obj);
}

class ProvinceCityFilter implements SearchFilter<Person> {
    String province, city;

    //传进来这个province是对的
    public ProvinceCityFilter(String province, String city) {
        this.province = province;
        this.city = city;
    }

    @Override
    public boolean accept(Person per) {
        //如果城市为空，只比较省份
        //这里改动过了
        if (per.city.equals("")) {
            return per.province.equals(province);
        } else
            //城市是有内容的，比较省和城市
            return (province.equals("") || per.province.equals(province) && city.equals("") || per.city.equals(city));
    }

    public boolean accept(Student student) {
        return (province.equals("") || student.province.equals(province) && city.equals("") || student.city.equals(city));
    }
}

class FieldFilter<T> implements SearchFilter<T> {

    Field field;
    Object keyvalue;

    public FieldFilter(T key, String fieldname) {
        try {
            //这里是要改的。不能直接用的
            this.field = key.getClass().getField(fieldname);
            //发现这里为空
            System.out.println(key.getClass());
            this.keyvalue = this.field.get(key);
        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
        } catch (IllegalAccessException e) {
//            e.printStackTrace();
        }
    }

    //    field的成员变量值和key value相匹配就返回true
    @Override
    public boolean accept(T obj) {
        try {
            return keyvalue == null || keyvalue.equals(field.get(obj));
        } catch (IllegalAccessException e) {
//            e.printStackTrace();
        }
        return false;
    }
}


class specifyFilter implements SearchFilter<Student> {
    String academy, speciality;

    public specifyFilter(String academy, String speciality) {
        this.academy = academy;
        this.speciality = speciality;
    }

    @Override
    public boolean accept(Student obj) {
        return (academy.equals("") || obj.academy.equals(academy) && obj.speciality.equals("") || obj.speciality.equals(speciality));
    }
}

class province_specifyFilter implements SearchFilter<Student>{
    String academy, speciality,province,city;

    public province_specifyFilter(String province,String city,String academy,String speciality){
        this.academy = academy;
        this.province = province;
        this.speciality = speciality;
        this.city = city;
    }
    @Override
    public boolean accept(Student obj) {
        return ((academy.equals("") || obj.academy.equals(academy) && obj.speciality.equals("") || obj.speciality.equals(speciality)))
                &&((province.equals("") || obj.province.equals(province) && city.equals("") || obj.city.equals(city)));

    }
}
