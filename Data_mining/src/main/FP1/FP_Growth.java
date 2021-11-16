
import java.util.*;

/**
 * @author HJY
 * @Description
 * @create 2021/11/08 21:48
 */

//项头表
class Flist{
    int item; //项元素
    double fre; //项支持度计数
    Node head; //指向FP树节点
    public Flist(){}
    public Flist(int item,double fre,Node head){
        this.item=item;
        this.fre=fre;
        this.head=head;
    }

}
//FP树节点
class Node{
    int item; //项元素
    double fre; //项支持度计数
    Node prev; //父节点
    List<Node> next;  //孩子节点
    Node Flistnext;  //从项头表为根后续连接
    public Node(){}
    public Node(int item,double fre,Node prev,List<Node> list){
        this.item=item;
        this.fre=fre;
        this.prev=prev;
        this.next=list;
    }
}

public class FP_Growth {
    List<List<Integer>> DB;  //读入的数据集
    int sumN;    //事务总数
    int Cnt = 0;   //频繁项集总数
    Flist[] FHead;  //项头表
    double minS;
    public Map<Integer,Double> Fitems_1=new HashMap<>();   //频繁1项集
    Node treeRoot; //FP根节点
//    List<Map<Integer[], Double>> Items = new ArrayList<>();   //频繁项集
    List<List<Integer>> Items=new ArrayList<>();

    public void fpgrowth(String fileName, double minS) {
        this.DB = new readFile().readFileByLines(fileName, 1000000);
        this.sumN = DB.size();
        this.minS=minS*this.sumN;
        treeRoot=new Node(-1,0.0,null,new ArrayList<>());
    //扫描第一遍数据库，找到频繁1项集
        findFitems_1();
    // 扫描第二遍数据库，构建FP树
        FPBuild();
    //根据FP挖掘频繁项集
        FPMining();
//        System.out.println("共有"+this.Cnt+"个频繁项集");
    }

    public void findFitems_1(){
        Map<Integer,Double> mp1=new HashMap<>(); //[1项集，支持度]
        for (List<Integer> arr1 : DB) {
            for (Integer num : arr1) {
                mp1.put(num, mp1.getOrDefault(num, 0.0) + 1);
            }
        }
        //统计频繁项集个数
        int size=0;
        for(Integer num:mp1.keySet()){
            double s=mp1.get(num);
            if(s>=minS){
                size++;
            }
        }
        //将频繁1项集加入到项头表中
        this.FHead=new Flist[size];
        int f=0;
        for(Integer num:mp1.keySet()){
            double s=mp1.get(num);
            if(s>=minS){
                this.FHead[f++]=new Flist(num,s,new Node());
                Fitems_1.put(num,s);
            }
        }
//测试
//        for(int i=0;i<this.FHead.length;i++){
//            System.out.println(FHead[i].item+":"+FHead[i].fre);
//        }
//
        //对FList按支持度从大到小排列
        Arrays.sort(FHead, new Comparator<Flist>() {
            @Override
            public int compare(Flist o1, Flist o2) {
                return o2.fre==o1.fre?o1.item-o2.item:(int)((o2.fre-o1.fre)*100000);
            }
        });

////测试
//        for(int i=0;i<this.FHead.length;i++){
//            System.out.println(FHead[i].item+":"+FHead[i].fre);
//        }
//        for(Integer key:Fitems_1.keySet()){
//            System.out.println(key+":"+Fitems_1.get(key));
//        }
////
    }

    public void FPBuild(){
        // 扫描第二遍数据库，构建FP树
        for(List<Integer>arr : DB){
            //剔除非频繁项
            List<Integer> list=new ArrayList<>();
            for(Integer a:arr){
                if(Fitems_1.containsKey(a)){
                    list.add(a);
                }
            }
            //对数据项按支持度排序
            Integer[] order=new Integer[list.size()];
            int k=0;
            for(Integer a:list){
                order[k++]=a;
            }
            Arrays.sort(order, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return (int)((Fitems_1.get(o2)-Fitems_1.get(o1))*100000);
                }
            });
//测试
//            for(Integer i:order){
//                System.out.println(i);
//            }
//            System.out.println();
//
            //根据支持度计数构建FP树
            Node p=treeRoot;
            int len=order.length;
            int i=0;
            while(i<len){
                boolean flag=false;
                Integer target=order[i];
                //遍历公共前缀
                if(!p.next.isEmpty()){
                    for(Node node:p.next){
                        if(node.item==target){
                            node.fre++;
                            p=node;
                            i++;
                            flag=true;
                            break;
                        }
                    }
                }
                //无公共前缀，在当前节点下新增节点，并连接上FP树和Flist项头表
                if(!flag){
                    Node node=new Node(target,1.0,p,new ArrayList<>());
                    for(Flist f:FHead){
                        if(f.item==target){
                            Node phead=f.head;
                            while(phead.Flistnext!=null){
                                phead=phead.Flistnext;
                            }
                            phead.Flistnext=node;
                            break;
                        }
                    }
                    p.next.add(node);
                    p=node;
                    i++;
                }
            }

        }
//测试FP
//            Node n=treeRoot;
//            Deque<Node> qu=new LinkedList<>();
//            for(Node e:n.next){
//                qu.addLast(e);
//            }
//            while(!qu.isEmpty()){
//                System.out.println("*************");
//                int size=qu.size();
//                for(int j=0;j<size;j++){
//                    Node t=qu.removeFirst();
//                    System.out.println("item:"+t.item+" fre:"+t.fre);
//                    for(Node e:t.next){
//                        qu.addLast(e);
//                    }
//                }
//            }
//
//测试Flist
//        System.out.println();
//            for(int m=0;m<FHead.length;m++){
//                Node q=FHead[m].head.Flistnext;
//                while(q!=null){
//                    System.out.println("item:"+q.item+" fre:"+q.fre);
//                    Node no=q.prev;
//                    while(no!=null){
//                        System.out.println("item:"+no.item+" fre:"+no.fre);
//                        no=no.prev;
//                    }
//                    q=q.Flistnext;
//                }
//                System.out.println();
//            }
//
    }

    public void FPMining(){
        for(int i=FHead.length-1;i>=0;i--){
            //记录条件模式基
            List<List<Node>> condition_FP=new ArrayList<>();
            Node p=FHead[i].head.Flistnext;
            while(p!=null){
                Integer item=p.item;
                Double fre=p.fre;
                List<Node> arr=new ArrayList<>();
//                Node q=p.prev;
                Node q=p;
                while(q!=null){
                    arr.add(q);
                    q=q.prev;
                }
                condition_FP.add(arr);
                p=p.Flistnext;
            }

        }
    }

    public void fun1(List<List<Node>> condition_FP,List<Integer> items){
        List<List<Node>> last_condition_FP=new ArrayList<>();


    }

//    public void FPMining(){
//        for(int i=FHead.length-1;i>=0;i--){
//            //记录条件模式基
//            Map<Integer,Double> mp=new HashMap<>();
//            Node p=FHead[i].head.Flistnext;
//            while(p!=null){
//                Integer item=p.item;
//                Double fre=p.fre;
//                Node q=p.prev;
//                while(q!=null){
//                    mp.put(q.item,mp.getOrDefault(q.item,0.0)+fre);
//                    q=q.prev;
//                }
//                p=p.Flistnext;
//            }
//            //set 表示条件模式基中的其他符合要求的项
////            System.out.println(FHead[i].item);
//            Set<Integer> set=new HashSet<>();
//            for(Integer key:mp.keySet()){
////                System.out.println(key+" "+mp.get(key));
//                if(key!=-1&&mp.get(key)>=minS){
//                    set.add(key);
////                    System.out.print(key+" ");
//                }
//            }
////            System.out.println();
//
//            List<List<Integer>> fitems=genFitems(set,FHead[i].item);
//            for (List<Integer> f:fitems){
//                this.Items.add(f);
//                this.Cnt++;
//            }
//        }
//    }


    //根据条件模式基生成频繁项集
    public List<List<Integer>> genFitems(Set<Integer> set, Integer item){
        List<List<Integer>> res=new ArrayList<>();
        List<Integer> f=new ArrayList<>();
        f.add(item);
        res.add(f);
//        for(List<Integer> li:res){
//            for(Integer l:li){
//                System.out.print(l+ " ");
//            }
//            System.out.print(",");
//        }
        for(Integer s:set){
            int size=res.size();
            for(int j=0;j<size;j++){
                List<Integer> tmp=new ArrayList<>(res.get(j));
                tmp.add(s);
                res.add(tmp);
            }
        }
//        for(List<Integer> li:res){
//            for(Integer l:li){
//                System.out.print(l+ " ");
//            }
//            System.out.print(",");
//        }
//        System.out.println();
        return res;
    }
}
