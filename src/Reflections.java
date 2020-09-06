
import java.lang.reflect.Field;

public class Reflections {
    //    提供反射技术的通用方法，但是为了避免和已有的类撞就加了个s

    public static Field[] getFields(Object obj, int n) {
//        if (obj == null){
//            System.out.println("test");
//        }
        Class<?> c = obj.getClass();
        //获得c的所有共有成员变量
        Field[] fields_super = c.getSuperclass().getFields();
        Field[] fields_sub = c.getDeclaredFields();

        Field[] fields = new Field[n];
        int i = 0;
        //把父类和子类的一起合起来了
        for (; i < fields_super.length; i++) {
            fields[i] = fields_super[i];
        }
        //这里报错
        //我知道了。一定要传进去的参数和那个一样才行
        for (int j = 0; i < n; j++, i++) {
            fields[i] = fields_sub[j];
        }
        return fields;
    }

    //    描述字符串，返回fields成员变量
    public static String[] toString(Field[] fields) {
        String[] str = new String[fields.length];
        for (int i = 0; i < str.length; i++) {
            //用getname
            str[i] = fields[i].getName();
        }
        return str;
    }

    public static Object[] toArray(Object object, Field[] fields) {
        Object[] arow = new Object[fields.length];
        for (int i = 0; i < arow.length; i++) {
            try {
                //object引用实例的fields成员变量值。
                if (object instanceof Student) {
                    Student s = (Student) object;
                    arow[i] = fields[i].get(s);
//                    System.out.println(arow[i]);
                } else arow[i] = fields[i].get(object);

                //这里自己加了一个NullPointerException的异常捕获
            } catch (IllegalAccessException | NullPointerException e) {
//                e.printStackTrace();
                break;//无效存取异常
            }
        }
        for (int i = 0;i<arow.length;i++){
            arow[i] = "" + arow[i].toString();
        }
        return arow;
    }
}
