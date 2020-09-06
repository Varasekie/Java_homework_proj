
import java.lang.reflect.Field;
import java.util.Comparator;

public class CompareField<T> implements Comparator<T> {
    private String fieldname;

    public CompareField(String fieldname) {
        this.fieldname = fieldname;
    }

    @Override
    public int compare(T o1, T o2) {
        Field field = null;
        try {
            field = o1.getClass().getField(fieldname);
            return ((Comparable)field.get(o1)).compareTo((Comparable)field.get(o2));
        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
        } catch (IllegalAccessException e) {
//            e.printStackTrace();
        }
        return -1;
    }
}
