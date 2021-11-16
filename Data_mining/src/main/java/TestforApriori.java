import org.jfree.ui.RefineryUtilities;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.SimpleFormatter;

/**
 * @author
 * @Description:
 * @create 2021/11/08 16:22
 */

public class TestforApriori {
    public static void main(String[] args) {
        List<List<Integer>> arr=new Apriori().readTransRocords("./src/main/resources/chess.dat",1000000);
//        List<List<Integer>> arr=new Apriori().readTransRocords("./src/main/resources/mushrooms.txt",1000000);
//        List<List<Integer>> arr=new Apriori().readTransRocords("./src/main/resources/T10I4D100K.dat",1000000);
        DecimalFormat df=new DecimalFormat("0.000");
        List<Object> X=new ArrayList<>();
        List<Object> Y=new ArrayList<>();
        for(double i=0.75;i<1;i+=0.025){
            long starttime = System.currentTimeMillis(); //开始时间（毫秒）
            new Apriori().apriori(arr,i);
            long endtime = System.currentTimeMillis(); //结束时间（毫秒）
            X.add(df.format(i));
            Y.add((double)(endtime-starttime));
            System.out.println("支持度："+i+" 运行时间："+(endtime-starttime));
        }

        Map<String,List<List<Object>>> data=new HashMap<>();
        List<List<Object>> tmp=new ArrayList<>();
        tmp.add(X);
        tmp.add(Y);
        data.put("Apriori",tmp);

        //画折线图
        LineChart_AWT chart = new LineChart_AWT(
                "Runtime" ,
                "Apriori Runtime",
                "MinSupport",
                "Time(ms)",data);

        chart.pack( );
        RefineryUtilities.centerFrameOnScreen( chart );
        chart.setVisible( true );
//
    }
}
