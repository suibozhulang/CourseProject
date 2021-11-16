import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @Description:
 * @create 2021/11/08 9:44
 */

public class readFile {
    public List<List<Integer>> readFileByLines(String fileName,int len) {
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

    public static void main(String[] args) {
//        List<List<Integer>> arr=new readFile().readFileByLines("./src/main/resources/T10I4D100K.dat",10);
//        List<List<Integer>> arr=new readFile().readFileByLines("./src/main/resources/mushrooms.txt",1000000);
//        List<List<Integer>> arr=new readFile().readFileByLines("./src/main/resources/FP_test.dat",1000000);
//        List<List<Integer>> arr=new readFile().readFileByLines("./src/main/resources/s2.dat",1000000);
        List<List<Integer>> arr=new readFile().readFileByLines("./src/main/resources/chess.dat",1000000);

        int i=0;
        for(List<Integer> li:arr){
            System.out.print("第"+(++i)+"项事务:");
            for (Integer num :
                    li) {
                System.out.print(num+" ");
            }
            System.out.println();
        }
        System.out.println("事务总数："+arr.size());
    }
}
