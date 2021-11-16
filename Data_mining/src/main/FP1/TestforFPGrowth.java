import java.util.List;

/**
 * @author HJY
 * @Description
 * @create 2021/11/09 10:05
 */

public class TestforFPGrowth {
    public static void main(String[] args) {
        FP_Growth fp= new FP_Growth();
//        fp.fpgrowth("./src/main/resources/mushrooms.txt",0.6);
//        fp.fpgrowth("./src/main/resources/test2.dat",0.6);
        fp.fpgrowth("./src/main/resources/FP_test.dat",0.2);
//        fp.fpgrowth("./src/main/resources/chess.dat",0.8);
        for(Integer key:fp.Fitems_1.keySet()){
            System.out.println(key+":"+fp.Fitems_1.get(key));
        }
        for(List<Integer> list:fp.Items){
            for(Integer li:list){
                System.out.print(li+ " ");
            }
            System.out.println();
        }
        System.out.println("共有"+fp.Cnt+"个频繁项集");
//        System.out.println("频繁1项集");
//        int cnt=0;
//        for(List<Integer> list:fp.Items){
//            if(list.size()==5){
//                cnt++;
//                for(Integer li:list){
//                    System.out.print(li+ " ");
//                }
//                System.out.println();
//            }
//        }
//        System.out.println("5项集个数："+cnt);

    }
}
