package BPnet;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    //整个工程的Main入口
    public static void main(String[] args)throws Exception{
        BPnetwork BPtext = new BPnetwork(784,40,10,0.8,0.37,0.001);

        Date data = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        File file = new File("try.txt");
        Writer out = new FileWriter(file,true);
        out.write(df.format(data)+"\r\n");

        BPtrain tt = new BPtrain("traindata2.csv", "text2.csv", "text1.csv", "traindata1.csv",450, BPtext);
        System.out.println("隐藏层个数为：" + 40 + " 学习率为：" + 0.7 + " 迭代次数为：" +450);
        out.write("Hider layer:"+40+" Study:"+0.7+" times:"+450+"\r\n");
        tt.Train();
        tt.Text();

        MainFrame ttt = new MainFrame(BPtext);
        out.write("*************************************************************************************"+"\r\n");
        out.close();
    }
}
