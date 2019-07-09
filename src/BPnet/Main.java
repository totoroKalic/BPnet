package BPnet;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;


public class Main {
    //整个工程的Main入口
    public static void main(String[] args)throws Exception{
        String filename = "E:\\IDEA workplace\\BPnet\\BPnet\\image";
        Constant.trainFolder = filename;
        new MainFrame();
    }
}
