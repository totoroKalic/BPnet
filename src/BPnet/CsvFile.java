package BPnet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CsvFile {
    private final String fileName;
    private BufferedReader fileT;
    private List<String> list = new ArrayList<>();
    //读取文件信息
    public CsvFile(String Filename) throws Exception{
        this.fileName = Filename;
        fileT = new BufferedReader(new FileReader(Filename));
        String temp;
        while((temp = fileT.readLine()) != null){
            list.add(temp);
        }
    }
    //返回List的内容
    public List getList(){
        return list;
    }

    //读取行数
    public int getRowNum(){
        return list.size();
    }

    //读取列数
    public int getColNum() {
        if (!list.toString().equals("[]")) {
            if (list.get(0).toString().contains(",")) {
                return list.get(0).toString().split(",").length;    //用","分隔开每个数组元素
            } else if (list.get(0).toString().trim().length() != 0) {      //去掉String字符串中的首尾空格
                return 1;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    //读取某个确定的行数
    public String getRow(int index){
        if(this.list.size() != 0)
            return (String)list.get(index);
        else
            return null;
    }

    //读取某个确定的列数
    public String getCol(int index) {
        if (this.getColNum() == 0) {
            return null;
        }
        StringBuffer Testfile = new StringBuffer();
        String tmp = null;
        int colnum = this.getColNum();
        if (colnum > 1) {
            for (Iterator it = list.iterator(); it.hasNext();) {
                tmp = it.next().toString();
                Testfile = Testfile.append(tmp.split(",")[index] + ",");
            }
        } else {
            for (Iterator it = list.iterator(); it.hasNext();) {
                tmp = it.next().toString();
                Testfile = Testfile.append(tmp + ",");
            }
        }
        String str = new String(Testfile.toString());
        str = str.substring(0, str.length() - 1);
        return str;
    }
    //读取某个确定的单元格
    public String getString(int row, int col) {
        String temp = null;
        int colnum = this.getColNum();
        if (colnum > 1) {
            temp = list.get(row).toString().split(",")[col];
        } else if(colnum == 1){
            temp = list.get(row).toString();
        } else {
            temp = null;
        }
        return temp;
    }
    //关闭文件流
    public void CsvClose() throws Exception{
        this.fileT.close();
    }
}
