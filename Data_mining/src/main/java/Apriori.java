import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author HJY
 * @Description Aprior算法实现
 * @create 2021/11/08 9:38
 */

public class Apriori {
    List<List<Integer>> DB;  //读入的数据集
    int sumN;    //事务总数
    int Cnt=0;   //频繁项集总数
    List<Map<Integer[],Double>> Items=new ArrayList<>();

    /**
     * @Description: apriori 算法实现
     * @param arr: 数据集
     * @param minS:  最小支持度
     * @return void:
    */
    public void apriori(List<List<Integer>> arr,double minS){
        //原始事务数据
//        List<List<Integer>> arr=new readFile().readFileByLines(fileName,1000000);
        this.DB=arr;
        int sumN=arr.size();
        this.sumN=sumN;

        Map<Integer[],Double> fitems_k=new HashMap<>(); //频繁k项集
        Map<Integer[],Double> citems_k=new HashMap<>(); //候选k项集

        int k=1;

        Map<Integer,Double> mp1=new HashMap<Integer, Double>(); //[1项集，支持度]
        //计算支持度计数
        for (List<Integer> arr1 : DB) {
            for (Integer num : arr1) {
                mp1.put(num, mp1.getOrDefault(num, 0.0) + 1);
            }
        }
        //计算支持度，并作为候选1项集
        for(Integer num:mp1.keySet()){
            Integer[] tmp=new Integer[]{num};
            citems_k.put(tmp,mp1.get(num)/this.sumN);
        }
        //得到频繁1项集
        fitems_k=candElim(citems_k,minS);
        this.Items.add(fitems_k);

        //从k=1开始迭代，找k>=2项集
        while(!fitems_k.isEmpty()){
//            show(fitems_k,k); //打印k项集
            this.Cnt+=fitems_k.size();
            citems_k=candGen(fitems_k,k+1); //生成k+1项候选集
            citems_k=candPrun(fitems_k,citems_k); //对k+1项集进行剪枝
            supportCount(citems_k); //扫描DB，计算项集支持度
            fitems_k=candElim(citems_k,minS); //淘汰非频繁项集
            this.Items.add(fitems_k);  //加入结果集

            k++;
        }
//        System.out.println("共有"+this.Cnt+"个频繁项集");
    }

    /**
     * @Description: 打印频繁k项集
     * @param fitems: 平凡k项集
     * @param k:
     * @return void:
    */
    public void show(Map<Integer[],Double> fitems,int k){
        if(fitems.isEmpty()){
            return;
        }
        System.out.println("频繁"+k+"项集:");
        for(Integer[] key:fitems.keySet()){
            for(Integer num:key){
                System.out.print(num+" ");
            }
            System.out.println(":"+fitems.get(key));
        }
        System.out.println("共"+fitems.size()+"项");
    }

    /**
     * @Description: 根据频繁k-1项集生成候选k项集
     *              Lk=Lk-1 X Lk-1   [ABC]X[ABD]=[ABCD]  公共前缀长度等于k-1
     * @param fitems: 频繁k-1项集
     * @param k:
     * @return java.util.Map<java.lang.Integer[],java.lang.Double>: 候选k项集
    */
    public Map<Integer[],Double> candGen(Map<Integer[],Double> fitems,int k){
        Map<Integer[],Double> ans=new HashMap<>();
        for(Integer[] arr1:fitems.keySet()){
            for(Integer[] arr2:fitems.keySet()){
                int cnt=0;  //统计两个项集相同元素个数
                for(int i=0;i<arr1.length;i++){
                    if(arr1[i]==arr2[i]){
                        cnt++;
                    }else{
                        break;
                    }
                }
                if(cnt==k-2){
                    Integer[] tmp=new Integer[k];
                    int m=0;
                    for(Integer a:arr1){
                        tmp[m++]=a;
                    }
                    tmp[m]=arr2[k-2];
                    Arrays.sort(tmp);
                    //tmp可能之前已经被加入ans中，去重
                    boolean flag=true;
                    for(Integer[] a:ans.keySet()){
                        if(arr_equ(a,tmp)){
                            flag=false;
                            break;
                        }
                    }

                    if(flag)
                        ans.put(tmp,0.0);
                }
            }
        }
        return ans;
    }

    /**
     * @Description: 候选集剪枝，候选集中减去一个元素后的集合如果不是频繁项集，则该候选集非频繁
     * @param fitems: 平凡k-1项集
     * @param citems:  候选k项集
     * @return java.util.Map<java.lang.Integer[],java.lang.Double>: 剪枝后的候选k项集
    */
    public Map<Integer[],Double> candPrun(Map<Integer[],Double> fitems,Map<Integer[],Double> citems){
        Map<Integer[],Double> ans=new HashMap<>();
        for(Integer[] item:citems.keySet()){
            boolean flag=true;
            for(int i=0;i<item.length;i++){
                //生成候选集减一集合
                Integer[] tmp=new Integer[item.length-1];
                for(int j=0;j<item.length-1;j++){
                    if(j<i){
                        tmp[j]=item[j];
                    }else if(j>=i){
                        tmp[j]=item[j+1];
                    }
                }
                //候选集减一集合不包含在k频繁项集中
                if(!contain(tmp,fitems)){
                    flag=false;
                    break;
                }
            }
            if(flag){
                ans.put(item,citems.get(item));
            }
//
//            else{
//                System.out.println("确实剪过枝");
//            }
        }
        return ans;
    }

    /**
     * @Description: 频繁k项集中是否包含项集arr
     * @param arr:
     * @param fitems:
     * @return boolean:
    */
    public boolean contain(Integer[] arr,Map<Integer[],Double> fitems){
        for(Integer[] item:fitems.keySet()){
            if(arr_equ(arr,item)){
                return true;
            }
        }
        return false;
    }

    /**
     * @Description: 扫遍一遍数据库，完成对候选集的支持度计数
     * @param citems:  候选k项集
     * @return void:
    */
    public void supportCount(Map<Integer[],Double> citems){
        //计算支持度计数
        for(Integer[] item:citems.keySet()){
                for(List<Integer> transfer:DB){
                    if(match(item,transfer)){
                        citems.put(item,citems.get(item)+1);
                    }
                }
            }
        //计算支持度
        for(Integer[] item:citems.keySet()){
            citems.put(item,citems.get(item)/this.sumN);
        }
    }

    /**
     * @Description: 判断事务中是否包含项集中全部元素
     * @param items: 项集
     * @param transfer: 事务
     * @return boolean: 若全包含items中元素则返回true
    */
    public boolean match(Integer[] items,List<Integer> transfer){
        for(int item:items){
            boolean flag=false;
            for(int i=0;i<transfer.size();i++){
                if(item==transfer.get(i)){
                    flag=true;
                }
            }
            if(flag)
                continue;
            else{
                return false;
            }
        }
        return true;
    }

    /**
     * @Description: 判断项集arr1和项集arr2是否相同 ，用于去重
     * @param arr1:
     * @param arr2:
     * @return boolean: 相同返回true
    */
    public boolean arr_equ(Integer[] arr1,Integer[] arr2){
        int m=arr1.length;
        int n=arr2.length;
        if(m!=n)
            return false;
        for(int i=0;i<m;i++){
            if(arr1[i]!=arr2[i]){
                return false;
            }
        }
        return true;
    }


    /**
     * @Description: 根据最小支持度阈值minS，对淘汰候选项集中非频繁项集
     * @param citems: 候选k项集
     * @param minS: 最小支持度阈值
     * @return java.util.Map<java.lang.Integer[],java.lang.Double>: 频繁k项集
    */
    public Map<Integer[],Double> candElim(Map<Integer[],Double> citems,double minS){
        Map<Integer[],Double> ans=new HashMap<>();
        for(Integer[] item:citems.keySet()){
            double s=citems.get(item);
            if(s>=minS){
                ans.put(item,s);
            }
        }
        return ans;
    }

    /**
     * @Description: 从数据集中读取事务
     * @param fileName:
     * @param len:  限制行数
     * @return java.util.List<java.util.List<java.lang.Integer>>:
    */
    public List<List<Integer>> readTransRocords(String fileName,int len) {
        File file = new File(fileName);
        BufferedReader reader = null;
        List<List<Integer>> ans=new ArrayList<List<Integer>>();
        try {
//            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                String[] str=tempString.trim().split(" +");
                List<Integer> list1=new ArrayList<Integer>();
                for(String s:str){
                    list1.add(Integer.valueOf(s));
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
     * @Description: 测试用例
     * @param args:
     * @return void:
    */
    public static void main(String[] args) {
        Apriori ap= new Apriori();
//        List<List<Integer>> arr=ap.readTransRocords("./src/main/resources/T10I4D100K.dat",1000000);
//        List<List<Integer>> arr=ap.readTransRocords("./src/main/resources/mushrooms.txt",1000000);
        List<List<Integer>> arr=new Apriori().readTransRocords("./src/main/resources/chess.dat",1000000);

        ap.apriori(arr,0.9);
        System.out.println("共有"+ap.Cnt+"个频繁项集");
    }
}
