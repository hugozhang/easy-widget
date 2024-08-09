import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class Test {

    public static void main(String[] args) {
        Multimap<String, Integer> arrayListMultimap = ArrayListMultimap.create();
        arrayListMultimap.put("张三", 90);
        arrayListMultimap.put("张三", 80);
        arrayListMultimap.put("张三", 100);
        arrayListMultimap.put("李四", 88);
        System.out.println("Multimap key为HashMap, value为ArrayList: " + arrayListMultimap);

    }

}
