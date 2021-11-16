
import java.util.*;

/**
 * @author HJY
 * @Description
 * @create 2021/11/12 20:23
 */

public class K_means {

    /**
     * @Description: k_means算法实现
     * @param fileName: 传入数据集
     * @param K:  聚类数量
     * @return Point[]: 打完标签的点
    **/
    public Point[] k_means(String fileName,int K){
        //加载所有点[x,y]，存入
        List<List<Integer>> DB=new readFile().readFileByLines(fileName, 1000000);
        int size=DB.size();
        Point[] Points=new Point[size];
        for(int i=0;i<size;i++){
            Point p=new Point(DB.get(i).get(0),DB.get(i).get(1));
            Points[i]=p;
        }
        //随机选择K个初始点为聚类中心
        Point[] Centers=new Point[K];
        Random r=new Random();
        //防止取到重复点
        Set<Integer> set=new HashSet<>();
        for(int i=0;i<K;i++){
            int num=r.nextInt(size);
            while(set.contains(num)){
                num=r.nextInt(size);
            }
            set.add(num);
            Point p=new Point(Points[num].getX(),Points[num].getY());
            Centers[i]=p;
            Centers[i].setLable(i);
        }
        int p=0;
        //不断聚类直到，聚类中心不再改变
        Point[] newCenters=Cluster(Points,Centers);
        while(isChange(newCenters,Centers)){
            Centers=newCenters;
            newCenters=Cluster(Points,newCenters);
            p++;
////测试
//        for(Point p:newCenters){
//            System.out.print("["+p.getX()+","+p.getY()+"] ");
//        }
//        System.out.println();
////
        }
        //System.out.println("聚类结束");
        System.out.println(p);
        return Points;

    }

    /**
     * @Description: 聚类，重新生成聚类中心
     * @param Points: 传入的待分类点集
     * @param Centers:  聚类中心
     * @return Point[]: 新的聚类中心
    **/
    public Point[] Cluster(Point[] Points,Point[] Centers){
        int k=Centers.length;

        List<List<Double>> X=new ArrayList<>(); //记录归为i类的点的x坐标
        for(int i=0;i<k;i++){
            List<Double> tmp=new ArrayList<>();
            X.add(tmp);
        }

        List<List<Double>> Y=new ArrayList<>(); //记录归为i类的点的y坐标
        for(int i=0;i<k;i++){
            List<Double> tmp=new ArrayList<>();
            Y.add(tmp);
        }

        //遍历点集，根据聚类中心给点打标签
        for(Point p:Points){
            double mind=Integer.MAX_VALUE;
            int label=-1;
            for(int i=0;i<k;i++){
                double d=p.dist(Centers[i]);
                if(d<mind){
                    mind=d;
                    label=i;
                }
            }
            p.setLable(label);
            X.get(label).add(p.getX());
            Y.get(label).add(p.getY());
        }
        //根据聚类结果重新生成聚类中心
        Point[] res=new Point[k];
        for(int i=0;i<k;i++){

            Double sumx=0.0;
            int n=X.get(i).size();
            for(Double x:X.get(i)){
                sumx+=x;
            }

            Double sumy=0.0;
            for(Double y:Y.get(i)){
                sumy+=y;
            }
            Point q=new Point(sumx/n,sumy/n);
            res[i]=q;
        }
        return res;
    }
    /**
     * @Description: 判断两个聚类中心集是否改变
     * @param Centers1: 第一个聚类中心集
     * @param Centers2:  第二个聚类中心集
     * @return boolean: 改变为true，否则为false
    */
    public boolean isChange(Point[] Centers1,Point[] Centers2){
        int k=Centers1.length;
        for(int i=0;i<k;i++){
            if(Centers1[i].getX()!=Centers2[i].getX()||Centers1[i].getY()!=Centers2[i].getY())
                return true;
        }
        return false;
    }
}
