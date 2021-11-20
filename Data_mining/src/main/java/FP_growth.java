import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author HJY
 * @Description
 * @create 2021/11/08 21:48
 */
public class FP_growth {

    double minSupport; //设置最小支持度
    Map<String, Integer> allFrequentSetMap = new LinkedHashMap<>();//用来存放所有频繁项集和支持度
    int Size=0;  //数据集规模

    /**
     * @Description: fp_growth算法
     * @param transactions:  原始数据集
     * @param minSupport:  最小支持度阈值
     * @return void:
    */
    public void fp_growth(List<List<String>> transactions ,double minSupport){
        int size = transactions.size();
        this.Size=size; //记录事务数据量
        double support = minSupport*size;
//        System.out.println("支持度为：" + support);
        this.minSupport=support; //百分比支持度转换为数据集绝对值
        ArrayList<TreeNode> HeaderTable = buildHeaderTable(transactions);  //创建项头表、即频繁1项集
        for(TreeNode h: HeaderTable){
            //System.out.println(h.getName()+ ": " + h.getCount());
            allFrequentSetMap.put(h.getName()+" ", h.getCount());   //将1项集加入结果集中
        }
        FP_gen(transactions, null);   //递归查找2、3...项集
    }


    /**
     * @Description: 根据传入的事务构建条件FP树，递归迭代找到频繁项集
     * @param transRecords: 事务，除了第一次是原始数据集，后续都是根据条件FP树构建的临时事务
     * @param postPattern:  模式后缀
     * @return void:
    */
    public void FP_gen(List<List<String>> transRecords, List<String> postPattern) {
        // 构建项头表，同时也是频繁1项集
        ArrayList<TreeNode> HeaderTable = buildHeaderTable(transRecords);
        // 构建FP-Tree
        TreeNode treeRoot = buildFPTree(transRecords, HeaderTable);
        // 如果FP-Tree为空则返回
        if (treeRoot.getChildren()==null || treeRoot.getChildren().size() == 0)
            return;
        //输出项头表的每一项+postPattern
        if(postPattern!=null){
            String s = "";//单个频繁集
            for (TreeNode header : HeaderTable) {
                //System.out.print(header.getCount() + "\t" + header.getName());//输出所有符合的组合
                s += header.getName() + " ";
                for (String ele : postPattern){ //输出模式后缀
                    //System.out.print("\t" + ele);
                    s += ele + " ";
                }
                allFrequentSetMap.put(s, header.getCount());
                //System.out.println();
            }
        }
        // 找到项头表的每一项的条件模式基，进入递归迭代
        for (TreeNode header : HeaderTable) {
            // 后缀模式增加一项
            List<String> newPostPattern = new LinkedList<String>();
            newPostPattern.add(header.getName());
            if (postPattern != null)
                newPostPattern.addAll(postPattern);
            // 寻找header的条件模式基CPB，放入newTransRecords中
            List<List<String>> newTransRecords = new LinkedList<List<String>>();
            TreeNode backnode = header.getNextHomonym();
            while (backnode != null) {
                int counter = backnode.getCount();
                List<String> prenodes = new ArrayList<String>();
                TreeNode parent = backnode;
                // 遍历backnode的祖先节点，放到prenodes中
                while ((parent = parent.getParent()).getName() != null) {
                    prenodes.add(parent.getName());
                }
                while (counter-- > 0) { //说明生成的FP树仍然是多路径树
                    newTransRecords.add(prenodes);
                }
                backnode = backnode.getNextHomonym();
            }
            // 递归迭代
            FP_gen(newTransRecords, newPostPattern);
        }
    }

    // 构建项头表，同时也是频繁1项集
    /**
     * @Description: 根据事务构建项头表，即频繁1项集
     * @param transRecords:  事务
     * @return java.util.ArrayList<TreeNode>: 项头表
    */
    public ArrayList<TreeNode> buildHeaderTable(List<List<String>> transRecords) {
        ArrayList<TreeNode> F1 = null;//要返回的L(1)级频繁项集
        if (transRecords.size() > 0) {
            F1 = new ArrayList<TreeNode>();
            Map<String, TreeNode> map = new HashMap<String, TreeNode>();
            // 计算事务数据库中各项的支持度
            for (List<String> record : transRecords) {
                for (String item : record) {
                    if (!map.keySet().contains(item)) {//
                        TreeNode node = new TreeNode(item);
                        node.setCount(1);
                        map.put(item, node);
                    } else {
                        map.get(item).countIncrement(1);//支持度加一
                    }
                }
            }
            // 把支持度大于（或等于）minSup的项加入到F1中
            Set<String> names = map.keySet();//导入k值
            for (String name : names) {
                TreeNode tnode = map.get(name);
                if (tnode.getCount() >= this.minSupport) {
                    F1.add(tnode);
                }
            }
            Collections.sort(F1);//降序排列
            return F1;
        } else {
            return null;
        }
    }

    // 构建FP-Tree
    /**
     * @Description: 根据事务和1项集构建FP树
     * @param transRecords: 事务
     * @param F1:  1项集
     * @return TreeNode: FP树根节点
    */
    public TreeNode buildFPTree(List<List<String>> transRecords,
                                ArrayList<TreeNode> F1) {
        TreeNode root = new TreeNode(); // 创建树的根节点
        for (List<String> transRecord : transRecords) {
            LinkedList<String> record = sortByF1(transRecord, F1);//将事务中的数据按支持度降序排列
            TreeNode subTreeRoot = root;
            TreeNode tmpRoot = null;
            if (root.getChildren() != null) {
                while (!record.isEmpty()
                        && (tmpRoot = subTreeRoot.findChild(record.peek())) != null) {
                    tmpRoot.countIncrement(1);
                    subTreeRoot = tmpRoot;
                    record.poll();
                }
            }
            addNodes(subTreeRoot, record, F1);//record作为subTreeRoot的后代插入树中
        }
        return root;
    }

    /**
     * @Description: 将事务根据项的频繁程度降序排列
     * @param transRecord: 事务
     * @param F1:  1项集
     * @return java.util.LinkedList<java.lang.String>:
    */
    public LinkedList<String> sortByF1(List<String> transRecord,
                                       ArrayList<TreeNode> F1) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (String item : transRecord) {
            // 由于F1已经是按降序排列的，
            for (int i = 0; i < F1.size(); i++) {
                TreeNode tnode = F1.get(i);
                if (tnode.getName().equals(item)) {
                    map.put(item, i);
                }
            }
        }
        ArrayList<Entry<String, Integer>> al = new ArrayList<Entry<String, Integer>>(
                map.entrySet());//映射项，映射项包含Key和Value
        Collections.sort(al, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> arg0,
                               Entry<String, Integer> arg1) {
                // 降序排列
                return arg0.getValue() - arg1.getValue();
            }
        });
        LinkedList<String> rest = new LinkedList<String>();
        for (Entry<String, Integer> entry : al) {
            rest.add(entry.getKey());
        }
        return rest;
    }

    // 把record作为ancestor的后代插入树中
    /**
     * @Description: 将record中元素插入以ancestor为根节点的树中
     * @param ancestor:
     * @param record:
     * @param F1:
     * @return void:
    */
    public void addNodes(TreeNode ancestor, LinkedList<String> record,
                         ArrayList<TreeNode> F1) {
        if (record.size() > 0) {
            while (record.size() > 0) {
                String item = record.poll();//获取队列首个元素
                TreeNode leafnode = new TreeNode(item);
                leafnode.setCount(1);
                leafnode.setParent(ancestor);
                ancestor.addChild(leafnode);

                for (TreeNode f1 : F1) {//寻找同名子节点
                    if (f1.getName().equals(item)) {
                        while (f1.getNextHomonym() != null) {
                            f1 = f1.getNextHomonym();
                        }
                        f1.setNextHomonym(leafnode);
                        break;
                    }
                }

                addNodes(leafnode, record, F1);   //递归增加节点
            }
        }
    }

    /**
     * @Description: 输入数据集
     * @param filename:  数据集路径
     * @return java.util.List<java.util.List<java.lang.String>>:
    */
    public List<List<String>> readTransRocords(String filename) {
        List<List<String>> transaction = new LinkedList<>();
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);
            try {
                String line;
                List<String> record;
                while ((line = br.readLine()) != null) {
                    if(line.trim().length()>0){
                        String str[] = line.split(" ");
                        record = new LinkedList<String>();
                        for (String w : str)
                            record.add(w);
                        transaction.add(record);
                    }
                }
            } finally {
                br.close();
            }
        } catch (IOException ex) {
            System.out.println("Read transaction records failed."
                    + ex.getMessage());
            System.exit(1);
        }
        return transaction;
    }

    /**
     * @Description:  测试用例
     * @param args:
     * @return void:
    */
    public static void main(String[] args) {
        FP_growth fp = new FP_growth();
//        List<List<String>> transactions=fp.readTransRocords("./src/main/resources/T10I4D100K.dat");
         List<List<String>> transactions=new FP_growth().readTransRocords("./src/main/resources/mushrooms.txt");

        fp.fp_growth(transactions,0.2);
        // 测试C1
        System.out.println("-----------------------------------");
        Set<String> stringSet1 = fp.allFrequentSetMap.keySet();
        int term = 1;
        for (String s : stringSet1) {
            System.out.println(term + "：" + s + "，支持度" + fp.allFrequentSetMap.get(s)*1.0/fp.Size);
            term++;
        }
    }
}

