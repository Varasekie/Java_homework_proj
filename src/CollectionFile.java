
import java.io.*;
import java.util.Collection;

public class CollectionFile {
    public static <T> void readFrom(String filename, Collection<T> collection)  {

        try {
            InputStream in = new FileInputStream(filename);
            ObjectInputStream objin = new ObjectInputStream(in);
            collection.clear();
            while (true) {
                try {
                    //这里已经进行了强制类型转化
                    Object o = objin.readObject();
                    collection.add((T)o);
                } catch (ClassNotFoundException e) {
                    //文件没了就不读了
                    break;
                }
            }
            objin.close();
            in.close();
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }


    public static <T> void writeTo(String filename, Collection<T> collection) {
        try {
            OutputStream out = new FileOutputStream(filename);
            ObjectOutputStream objout = new ObjectOutputStream(out);
            for (T obj : collection) {
                objout.writeObject(obj);
            }
            objout.close();
            out.close();
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

}
