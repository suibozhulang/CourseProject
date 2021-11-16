import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author HJY
 * @Description
 * @create 2021/11/12 20:57
 */

public class Point {
    private double x;  //x坐标
    private double y;  //y坐标
    private int lable=-1;   //类标签，初始为-1
    private int type=-1;  //DBScan,点类别，0为边界点，1为核心点
    private List<Point> coreNgb;  //DBScan,该点是某个点的边界点
    private boolean visited; //DBScan,是否被访问过,true表示访问过
    public Point(double x,double y){
        this.x=x;
        this.y=y;
    }
    public void setX(double x){
        this.x=x;
    }
    public void setY(double y){
        this.y=y;
    }
    public void setLable(int lable){
        this.lable=lable;
    }
    public int getLable(){
        return this.lable;
    }
    public double getX(){
        return this.x;
    }
    public double getY(){
        return this.y;
    }
    public void setType(int type){ this.type=type; };
    public int getType(){ return this.type; };
    public void hasvisited(){ this.visited=true; }
    public boolean isvisited(){ return this.visited; };
    //计算该点与p2的欧式距离
    public double dist(Point p2){
        double x1=this.x;
        double y1=this.y;
        double x2=p2.getX();
        double y2=p2.getY();
        return Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
    }
    //DBScan， 将点p加入CoreNgb中
    public void coreNgbadd(Point p){
        if(this.coreNgb==null){
            this.coreNgb=new ArrayList<>();
        }
        coreNgb.add(p);
    }
    public List<Point> getCoreNgb(){
        return coreNgb;
    }
}
