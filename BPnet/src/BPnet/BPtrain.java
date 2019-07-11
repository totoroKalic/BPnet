package BPnet;

import java.text.*;
import java.util.*;
import java.io.*;
import Test.CsvFile;
import Test.ImageModel;

public class BPtrain {
    private int time;     //训练的迭代次数
    private String fileOne;       //训练数据
    private String fileTwo;       //训练结果
    private String fileThree;     //测试数据
    private String fileFour;      //测试结果
    private List<ImageModel> imageModel = null;
    private BPnetwork BPtext = null;
    //网络的测试
    public BPtrain(String fileOneS, String fileTwoS, String fileThreeS, String fileFourS, int timesS, BPnetwork netOne) {
        this.time = timesS;
        this.fileOne = fileOneS;
        this.fileTwo = fileTwoS;
        this.fileThree = fileThreeS;
        this.fileFour = fileFourS;
        this.BPtext = netOne;
    }
    //手写网络的训练
    public BPtrain(List<ImageModel> List,BPnetwork netTwo,int timeT){
        this.time = timeT;
        this.imageModel = List;
        this.BPtext = netTwo;
    }

    public void Train() throws Exception {
        CsvFile file_One = new CsvFile(fileTwo);
        int trainAnsRow = file_One.getRowNum(); //获得训练结果行数
        int trainAnsCal = file_One.getColNum(); //获得训练结果列数(其实没多大必要)
        CsvFile file_Two = new CsvFile(fileOne);
        int trainRow = file_Two.getRowNum();    //获得训练数据的行数
        int trainCal = file_Two.getColNum();    //获得训练数据的列数
        double[] trainData = new double[784];
        double[] trainResult = new double[10];
        for (int p = 0; p < 5; p++) {
            for (int j = 0; j < trainAnsRow; j++) {
                for (int ooo = 0; ooo < 10; ooo++)
                    trainResult[ooo] = 0;
                String demo = file_One.getString(j, 0);
                trainResult[Integer.parseInt(demo)] = 1;
                int flag, k = 0;
                for (; k < trainCal; k++) {
                    flag = Integer.parseInt(file_Two.getString(j, k));
                    if (flag > 255 / 2)
                        trainData[k] = 1.0;
                    else
                        trainData[k] = 0.0;
                }
                //优化，自适应学习率
                double old = 0, newl = 0;
                BPtext.setStudy(3);
                for (int i = 0; i < time; i++) {
                    BPtext.train(trainData, trainResult);
                    if (BPtext.getError() < 0.005)
                        break;
                    if (i % 20 == 0) {
                        old = newl;
                        newl = BPtext.getError();
                        if ((newl - old) > 0 && old != 0)
                            BPtext.setStudy(1);
                        else
                            BPtext.setStudy(2);
                    }
                }
            }
        }
    }

    public void writeTrain() {
        double flag = 0,old = 0,newl = 0;
        double[] trainData = new double[784];
        double[] trainResult = new double[10];
        int size = imageModel.size();
        for (int p = 0; p < 5; p++) {
            for (int j = 0; j < size; j++) {
                System.arraycopy(imageModel.get(j).getGrayMatrix(),0,trainData,0,imageModel.get(j).getGrayMatrix().length);
                System.arraycopy(imageModel.get(j).getOutputList(),0,trainResult,0,imageModel.get(j).getOutputList().length);
               for (int k = 0; k <trainData.length ; k++) {
                    flag = trainData[k];
                    if (flag >= 0.5)
                        trainData[k] = 0;
                    else
                        trainData[k] = 1;
                }
                //优化，自适应学习率
                BPtext.setStudy(3);
                for (int i = 0; i < time; i++) {
                    BPtext.train(trainData, trainResult);
                    if (BPtext.getError() < 0.005)
                        break;
                    if (i % 20 == 0) {
                        old = newl;
                        newl = BPtext.getError();
                        if ((newl - old) > 0 && old != 0)
                            BPtext.setStudy(1);
                        else
                            BPtext.setStudy(2);
                    }
                }
            }
        }
    }

    public double Text() throws Exception {
        CsvFile file_One = new CsvFile(fileThree);
        int trainAnsRow = file_One.getRowNum(); //获得训练结果行数
        int trainAnsCal = file_One.getColNum(); //获得训练结果列数(其实没多大必要)
        CsvFile file_Two = new CsvFile(fileFour);
        int trainRow = file_Two.getRowNum();    //获得训练数据的行数
        int trainCal = file_Two.getColNum();    //获得训练数据的列数
        int[] trainResult = new int[trainRow];
        for (int j = 0; j < trainAnsRow; j++) {
            int demo = Integer.parseInt(file_One.getString(j, 0));
            double[] trainData = new double[784];
            int flag, k = 0;
            for (; k < trainCal; k++) {
                flag = Integer.parseInt(file_Two.getString(j, k));
                if (flag > 255 / 2) {
                    trainData[k] = 1.0;
                } else {
                    trainData[k] = 0.0;
                }
            }
            double[] answer = BPtext.test(trainData);
            double max = answer[0];
            int idx = 0;
            for (int i = 1; i < 10; i++) {
                if (answer[i] > max) {
                    max = answer[i];
                    idx = i;
                }
            }
            if (idx == demo)
                trainResult[j] = 1;
            else
                trainResult[j] = 0;
        }
        double number = 0;
        for (int ll = 0; ll < trainRow; ll++) {
            if (trainResult[ll] == 1)
                number++;
        }
        return number / trainRow;
    }
}



