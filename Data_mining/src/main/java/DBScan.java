import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * @Description
 * @Author HJY
 * @Create 2021/11/13 11:19
 * @Version 1.0
 */

public class DBScan {
    /**
     * @Description: DBScan算法实现，根据PPT的步骤写的，
     *              先找到一个核心点，将它附近的边界点加入队列中，不断遍历队列找到核心点，加入簇中，同时将新的边界点加入队列
     *              一直重复直到队列为空，一个簇聚类完成，再循环找下一个簇。
     *              被标记为边界点归为它附近一个核心点的簇中。
     * @param fileName: 数据集文件名
     * @param Eps: 点半径
     * @param MinPts:  圆内最少的点数
     * @return java.util.List<Point>: 结果点集，去除了噪声点
    */
    public List<Point> dbscan(String fileName,int Eps,int MinPts){
        //从数据集中加载所有点到Points中
        List<List<Double>> DB=readFileByLines(fileName, 1000000);
        int size=DB.size();
        Point[] Points=new Point[size];
        for(int i=0;i<size;i++){
            Point p=new Point(DB.get(i).get(0),DB.get(i).get(1));
            Points[i]=p;
        }
        //Res结果点集
        List<Point> Res=new ArrayList<>();
        int c=0;   //聚类数量
        for(Point i:Points){
            if(!i.isvisited()){
                i.hasvisited();  //标记以访问
                List<Point> N_i=rangeQuery(i,Eps,Points);   //以i点为圆心，以Eps为半径的圆内点集
                if(N_i.size()>=MinPts){      //圆内点数大于阈值
                    List<Point> C=new ArrayList<>();   //创建新类
                    c++;    //类ID
                    C.add(i);   //将i加入新类中
                    i.setLable(c);    //设置i的类标签
                    i.setType(1);    //设为核心点
                    Deque<Point> Q=new LinkedList<>();
                    for(Point j:N_i){
                        if(!j.isvisited()){
                            Q.addLast(j);   //未访问过的点加入队列
                        }
                    }
                    for(Point j:N_i){
                        if(j.isvisited()&&j.getType()==-1){
                            j.setType(0);   //访问过，既不是核心点也不是边界点，设置边界点
                        }
                    }
                    for(Point j:N_i){
                        if(j.getType()==0){
                            j.coreNgbadd(i);    //记录j是谁的边界点
                        }
//                        System.out.println("x:"+j.getX()+",y:"+j.getY()+",label:"+j.getLable()+",visited:"+j.isvisited());
                    }
//                    System.out.println("=================================");
                    while(!Q.isEmpty()){    //队列非空，进入循环，循环内容和上面类似
//                        System.out.println(Q.size());
                        Point j=Q.removeFirst();
                        j.hasvisited();
//                        System.out.println("x:"+j.getX()+",y:"+j.getY()+",label:"+j.getLable()+",visited:"+j.isvisited());
                        List<Point> N_j=rangeQuery(j,Eps,Points);
                        if(N_j.size()>=MinPts){
                            C.add(j);
                            j.setType(1);
                            j.setLable(c);
                            for(Point k:N_j){
                                if(!k.isvisited()&&!Q.contains(k)){
                                    //加入队列前，一定要判断当前队列中有无该点，否则可能进入死循环！！！！！！！！！！
                                    Q.addLast(k);
                                }
                            }
                            for(Point k:N_j){
                                if(k.isvisited()&&k.getType()==-1){
                                    k.setType(0);
                                }
                            }
                            for(Point k:N_j){
                                if(k.getType()==0){
                                    k.coreNgbadd(j);
                                }
                            }
                        }else{
                            j.setType(0);
                            j.coreNgbadd(i);
                        }
                    }
                    Res.addAll(C);   //将新类中的所有点进入结果集，点已被打上类标签
                }
            }

        }
        for(Point i:Points){
            if(i.getType()==0){    //标记为边界点的点，随便找它附近的一个核心点，标记为核心点的类中，加入结果集
                i.setLable(i.getCoreNgb().get(0).getLable());
                Res.add(i);
            }
        }
        return Res;
    }

    /**
     * @Description: 以p点为圆心，以Eps为半径，查找包含在圆内的点
     * @param p: 圆心点
     * @param Eps: 半径
     * @param Points: 总的点集
     * @return java.util.List<Point>: 在圆中的点，不包括圆上的点
    */
    public List<Point> rangeQuery(Point p,int Eps,Point[] Points){
        List<Point> ans=new ArrayList<>();
        for(Point q:Points){
            if((p.getX()!=q.getX()||p.getY()!=q.getY())&&p.dist(q)<Eps){
                ans.add(q);
            }
        }
        return ans;
    }

    /**
     * @Description: 按行读文件，之前的readFile读的数据是Integer。DBScan的数据都是Double类型的，不能复用
     * @param fileName:
     * @param len:
     * @return java.util.List<java.util.List<java.lang.Double>>:
    */
    public List<List<Double>> readFileByLines(String fileName,int len) {
        File file = new File(fileName);
        BufferedReader reader = null;
        List<List<Double>> ans=new ArrayList<>();
        try {
//            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                String[] str=tempString.trim().split(" +");
                List<Double> list1=new ArrayList<>();
                for(String s:str){
//                    System.out.println(s);
                    list1.add(Double.valueOf(s));
                }
                ans.add(list1);
                line++;
                if(line==len){
                    break;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return ans;
    }
    /**
     * @Description: 测试 dbscan算法
     * @param args:
     * @return void:
    */
    public static void main(String[] args) {
        List<Point> ans=new DBScan().dbscan("./src/main/resources/t4.8k.txt",5,2);
        int i=0;
        for(Point p :ans){
            System.out.println((++i)+":"+"x:"+p.getX()+",y:"+p.getY()+",label:"+p.getLable());
        }
    }
}
