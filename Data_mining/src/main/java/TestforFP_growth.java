import org.jfree.ui.RefineryUtilities;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author HJY
 * @Create 2021/11/15 19:26
 * @Version 1.0
 */

public class TestforFP_growth {
    public static void main(String[] args) {
        List<List<String>> transactions=new FP_growth().readTransRocords("./src/main/resources/chess.dat");
//        List<List<String>> transactions=new FP_growth().readTransRocords("./src/main/resources/mushrooms.txt");
//        List<List<String>> transactions=new FP_growth().readTransRocords("./src/main/resources/T10I4D100K.dat");

        DecimalFormat df=new DecimalFormat("0.000");
        List<Object> X=new ArrayList<>();
        List<Object> Y=new ArrayList<>();
        for(double i=0.75;i<1;i+=0.025){
            long starttime = System.currentTimeMillis(); //开始时间（毫秒）
            new FP_growth().fp_growth(transactions,i);
            long endtime = System.currentTimeMillis(); //结束时间（毫秒）
            X.add(df.format(i));
            Y.add((double)(endtime-starttime));
            System.out.println("支持度："+i+" 运行时间："+(endtime-starttime));
        }

        Map<String,List<List<Object>>> data=new HashMap<>();
        List<List<Object>> tmp=new ArrayList<>();
        tmp.add(X);
        tmp.add(Y);
        data.put("FP_growth",tmp);

        //画折线图
        LineChart_AWT chart = new LineChart_AWT(
                "Runtime" ,
                "FP_growth Runtime",
                "MinSupport",
                "Time(ms)",data);

        chart.pack( );
        RefineryUtilities.centerFrameOnScreen( chart );
        chart.setVisible( true );
//
    }
}
