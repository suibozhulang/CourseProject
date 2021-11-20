import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Description
 * @Author HJY
 * @Create 2021/11/14 19:36
 * @Version 1.0
 */

public class test {
    public static void main(String[] args) {
        List<Object> list=new ArrayList<>();
        list.add(1);
        list.add(1.0);
        List<Integer> li=new ArrayList<>();
        li.add(1);
        li.add(2);
        Collections.sort(li, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return 0;
            }
        });

    }
}
