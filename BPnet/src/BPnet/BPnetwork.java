package BPnet;

import java.util.Random;

public class BPnetwork {
    private final double[] input;       //输入层
    private final double[] hidden;      //隐藏层
    private final double[] hiddenValue;
    private final double output[];      //输出层
    private final double outValue[];
    private final double target[];      //预测的输出值
    private  final double[] outError;    //输出误差
    private final double[][] iptHidWeight;      //输入层到隐藏层的参数
    private final double[][] hidOutWeight;      //隐藏层到输出层的参数
    private final double[][] preIptHidWeight;   //更新之前输入层到隐藏层的权值
    private final double[][] preHidOutWeight;   //更新之前隐藏层到输出层的权值
    private final double[][] preIptHidChange;
    private final double[][] preHidOutChange;
    private double study;     //学习率
    private final double vector;    //动向量
    private final double allowError;    //误差允许值

    //初始化各个参数(输入层节点、隐藏层节点、输出层节点、学习率、动向量、误差允许值)
    public BPnetwork(int inputS,int hiddenS,int outputS,double studyS,double vectorS,double allowErrorS){
        input = new double[inputS];
        hidden = new double[hiddenS];
        output = new double[outputS];
        outError = new double[outputS];
        target = new double[outputS];
        outValue = new double[outputS];
        hiddenValue = new double[hiddenS];
        iptHidWeight = new double[inputS][hiddenS];
        hidOutWeight = new double[hiddenS][outputS];
        preHidOutWeight = new double[hiddenS][outputS];
        preIptHidWeight = new double[inputS][hiddenS];
        preHidOutChange = new double[hiddenS][outputS];
        preIptHidChange = new double[inputS][hiddenS];
        this.study = studyS;
        this.vector = vectorS;
        this.allowError = allowErrorS;
        //对数组的内部参数初始化
        setWeight(iptHidWeight);
        setWeight(hidOutWeight);
        setWeight(hidden);
        setWeight(output);
    }
    //改变学习率
    public void setStudy(int number){
        if(number == 2)
            this.study = 1.2 * this.study;
        else if(number == 1)
            this.study = 0.8 * this.study;
        else
            this.study = 0.8;
    }
    public double getStudy(){
        return this.study;
    }
    //初始化权重函数
    private void setWeight(double[][] demo){
        int i,j,len1,len2;
        Random random = new Random();
        for(i = 0,len1 = demo.length;i<len1;i++) {
            for (j = 0, len2 = demo[i].length; j < len2; j++) {
                double number = random.nextDouble();
                double judge = random.nextDouble();
                if(judge > 0.5)
                    demo[i][j] = number;
                else
                    demo[i][j] = -number;
            }
        }
    }
    private void setWeight(double[] demo){
        int i,len1;
        Random random = new Random();
        for(i = 0,len1 = demo.length;i<len1;i++) {
            double number = random.nextDouble();
            double judge = random.nextDouble();
            if(judge > 0.5)
                demo[i] = number;
            else
                demo[i] = -number;
        }
    }

    //训练数据
    public void train(double[] trainData,double[] targetS){
        loadInput(trainData);       //加载输入数据
        loadTarget(targetS);         //加载输出数据
        forward();                  //正向计算输出公式
        calculateError();           //计算结果误差
        correctError();             //纠正每个权值的误差
    }

    public double[] test(double[] inData){
        if(inData.length != inData.length){
            throw new IllegalArgumentException("Size don`t match");
        }
        double[] answer = forward(inData);
        return answer;
    }

    //将需要的数据(输入)加载进来
    private void loadInput(double[] Data){
        if(Data.length != input.length)
            throw new IllegalArgumentException("Size don`t match");
        System.arraycopy(Data,0,input,0,Data.length);
    }
    //将需要的数据(输出)加载进来
    private void loadTarget(double[] Data){
        if(Data.length != output.length)
            throw new IllegalArgumentException("Size don`t match");
        System.arraycopy(Data,0,target,0,Data.length);
    }

    //BP的正向计算公式
    //函数为sigmoid()函数
    private double sigmoid(double demo){
        return 1 / (1 + Math.exp(-demo));
    }
    //正向计算最后的输出值
    private void forward(){
        int i,j,k,len1,len2,len3;
        double temp = 0.0;
        for(i = 0,len1 = hidden.length;i < len1;i++) {
            for (j = 0, len2 = input.length; j < len2; j++) {
                temp += iptHidWeight[j][i] * input[j];
            }
            temp = temp - hidden[i];
            hiddenValue[i] = sigmoid(temp);
            temp = 0.0;
        }
        for(k=0,len3 = output.length;k<len3;k++){
            temp = 0;
            for(i = 0;i < len1;i++){
            temp += hidOutWeight[i][k]*hiddenValue[i];
            }
            temp = temp - output[k];
            outValue[k] = sigmoid(temp);
        }
    }
    private double[] forward(double[] inData){
        int i,j,k,len1,len2,len3;
        double[] outCopy = new double[output.length];
        double temp = 0.0;
        for(i = 0,len1 = hidden.length;i < len1;i++) {
            for (j = 0, len2 = inData.length; j < len2; j++) {
                temp += iptHidWeight[j][i] * inData[j];
            }
            temp = temp - hidden[i];
            hiddenValue[i] = sigmoid(temp);
            temp = 0.0;
        }
        for(k=0,len3 = output.length;k<len3;k++){
            temp = 0;
            for(i = 0;i < len1;i++){
                temp += hidOutWeight[i][k]*hiddenValue[i];
            }
            temp = temp - output[k];
            outCopy[k] = sigmoid(temp);
        }
        return outCopy;
    }

    //计算输出的误差
    private void calculateError(){
        double demo;
        for(int i =0,len = target.length;i<len;i++){
            demo = outValue[i] - target[i];
            outError[i] = Math.pow(demo,2)/2;
        }
    }
    public double getError(){
        double temp = 0;
        for(int i =0;i<output.length;i++){
            temp +=outError[i];
        }
        return temp;
    }
    private void Remberdata_one(){
        for(int m = 0,len = hidOutWeight.length;m<len;m++){
            System.arraycopy(hidOutWeight[m],0,preHidOutWeight[m],0,hidOutWeight[m].length);
        }
    }
    private double One_layer(int a,int b){
        double  num = (target[b] - outValue[b]) * outValue[b] * (1 - outValue[b]) * hiddenValue[a];
        double answer =  study*((1-vector)*num + vector*preHidOutChange[a][b]);
        preHidOutChange[a][b] = num;
        return answer;
    }
    private double Two_layer(int j,int n,int k){
        double num = (target[k] - outValue[k])*outValue[k]*(1-outValue[k])*preHidOutWeight[n][k]*hiddenValue[n]*(1-hiddenValue[n])*input[j];
        double answer =  study*((1-vector)*num + vector*preIptHidChange[j][n]);
        preIptHidChange[j][n] = num;
        return answer;
    }
    //反向纠正权重
    private  void correctError(){
        for(int k = 0,len = target.length;k<len;k++){
            if(outError[k] < this.allowError){
                continue;
            }
            else{
                Remberdata_one();
                //更新最后一层的阈值
                output[k] = output[k] + study * (target[k] - outValue[k]) * outValue[k] * (1 - outValue[k]) * (-1.0);
                //更新最后一层的权重
                for(int i =0,len1 = hidden.length;i<len1;i++){
                    hidOutWeight[i][k] = hidOutWeight[i][k] + One_layer(i,k);
                }
                //更新倒数第二层的权重
                for(int j =0,len2 = input.length;j<len2;j++){
                    for(int n = 0,len1 = hidden.length;n<len1;n++){
                        iptHidWeight[j][n] = iptHidWeight[j][n] + Two_layer(j,n,k);
                    }
                }
                //更新倒数第二层的阈值
                for(int l=0;l<hidden.length;l++){
                    hidden[l] = hidden[l] + study*(target[k]-outValue[k])*outValue[k]*(1-outValue[k])*preHidOutWeight[l][k]*hiddenValue[l]*(1-hiddenValue[l])*(-1.0);
                }
            }
        }
    }
}
