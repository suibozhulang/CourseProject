import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineChart_AWT extends ApplicationFrame {

    /**
     * @Description: 画二维折线图
     * @param applicationTitle: 应用名称
     * @param chartTitle: 图名称
     * @param categoryAxisLabel: 横坐标名称
     * @param valueAxisLabel: 纵坐标名称
     * @param data:  Map<String,List<Object[]>> 保存点坐标及标签，
     *            如：{“schools”:[[1970,1980],[15,25]],
     *            ...}
     * @return null:
    */
    public LineChart_AWT(String applicationTitle , String chartTitle , String categoryAxisLabel, String valueAxisLabel,
                         Map<String,List<List<Object>>> data)
    {
        super(applicationTitle);
        JFreeChart lineChart = ChartFactory.createLineChart(
                chartTitle, categoryAxisLabel,valueAxisLabel, createDataset(data), PlotOrientation.VERTICAL,
                true,true,false);

        ChartPanel chartPanel = new ChartPanel( lineChart );
        chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
        setContentPane( chartPanel );
    }

    private DefaultCategoryDataset createDataset(Map<String,List<List<Object>>> data)
    {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
        for(String key: data.keySet()){
            String legend=key;
            List<List<Object>> points=data.get(legend);
            List<Object> X=points.get(0);
            List<Object> Y=points.get(1);
            for(int i=0;i<X.size();i++){
                dataset.addValue((double) Y.get(i),legend,X.get(i).toString());
            }
        }
        return dataset;
    }

    /**
     * @Description: 测试用例
     * @param args:
     * @return void:
    */
    public static void main( String[ ] args )
    {
        Map<String,List<List<Object>>> data=new HashMap<>();
        List<List<Object>> tmp=new ArrayList<>();
        List<Object> X=new ArrayList<>();
        X.add(1970.0);
        X.add(1980.0);
        X.add(1990.0);
        X.add(2000.0);
        X.add(2010.0);
        List<Object> Y=new ArrayList<>();
        Y.add(15.5);
        Y.add(25.0);
        Y.add(35.0);
        Y.add(45.0);
        Y.add(55.0);
        tmp.add(X);
        tmp.add(Y);
        data.put("schools",tmp);

        LineChart_AWT chart = new LineChart_AWT(
                "School Vs Years" ,
                "Numer of Schools vs years",
                "Years",
                "Number of Schools",data);

        chart.pack( );
        RefineryUtilities.centerFrameOnScreen( chart );
        chart.setVisible( true );
    }
}

