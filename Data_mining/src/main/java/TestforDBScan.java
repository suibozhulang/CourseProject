import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * @Description
 * @Author HJY
 * @Create 2021/11/13 11:19
 * @Version 1.0
 */

public class TestforDBScan extends Canvas {
    private static int WIDTH=700;  //画布宽度
    private static int HEIGHT=700;  //画布高度
    private static double X_RATIO=1.0; //x坐标缩放比
    private static double Y_RATIO=1.0; //y坐标缩放比
    private static List<Point> ARR;   //打完标签的点集
//    private static int K=15;    //聚类数量

    public static void main(String[] args) {
        DBScan dbScan=new DBScan();
        List<Point> ans=dbScan.dbscan("./src/main/resources/t4.8k.txt",5,2);
//        List<Point> ans=dbScan.dbscan("./src/main/resources/s2.dat",5,2);

        ARR=ans;
//        System.out.println("++++++++++"+ans.size());
        int i=0;

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Canvas canvas = new TestforDBScan();
        canvas.setSize(WIDTH,HEIGHT);
        frame.getContentPane().add(canvas);
        frame.pack();
        frame.setVisible(true);
    }
    //画聚类图
    public void paint(Graphics g){
        //自适应画边界
        this.WIDTH=this.getWidth();
        this.HEIGHT=this.getHeight();
        g.setColor(Color.black);
        g.drawLine(10,10,10,HEIGHT-10);
        g.drawLine(10,HEIGHT-10,WIDTH-10,HEIGHT-10);
        g.drawLine(10,10,WIDTH-10,10);
        g.drawLine(WIDTH-10,10,WIDTH-10,HEIGHT-10);

        //计算坐标缩放比
        double x_left=Integer.MAX_VALUE;
        double x_right=Integer.MIN_VALUE;
        double y_bottom=Integer.MAX_VALUE;
        double y_top=Integer.MIN_VALUE;
        int maxlabel=0;
        for(Point p:ARR){
            x_left=Math.min(x_left,p.getX());
            x_right=Math.max(x_right,p.getX());
            y_bottom=Math.min(y_bottom,p.getY());
            y_top=Math.max(y_top,p.getY());
            maxlabel=Math.max(maxlabel,p.getLable());
        }
//        System.out.println(maxlabel);
        X_RATIO=(WIDTH-35)/(x_right-x_left);
        Y_RATIO=(HEIGHT-35)/(y_top-y_bottom);

        Color[] colors=new Color[maxlabel+1];
        for(int i=1;i<=maxlabel;i++){
            Random random=new Random();
            int _r=random.nextInt(255);
            int _g=random.nextInt(255);
            int _b=random.nextInt(255);
            colors[i]=new Color(_r,_g,_b);
        }
        int  cnt=0;
        //根据标签打印不同颜色的点
        for(Point p:ARR){
            double x=(p.getX()-x_left)*X_RATIO+15;   //坐标缩放加上偏移量
            double y=(p.getY()-y_bottom)*Y_RATIO+15;    //坐标缩放加上偏移量
            int label=p.getLable();
            g.setColor(colors[label]);
            if(p.getType()==1)
                cnt++;
            g.fillRoundRect((int)x,(int)y,5,5,5,5);
        }
//        System.out.println("**********"+cnt);
    }
}
