package Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.Color;
import BPnet.BPnetwork;
import BPnet.BPtrain;

public class MainFrame extends JFrame{
    private int width = 450;
    private int height = 500;

    private Canvas canvas = null;
    private JButton jbAdd = null;
    private JButton jbClear = null;
    private JButton jbBPData = null;
    private JButton jbBPTrain = null;
    private JButton jbBPTest = null;
    private JButton jbCNNData = null;
    private JButton jbCNNTrain = null;
    private JButton jbCNNTest = null;
    private BPnetwork Bpnet;
    private BPtrain Bptrain;

    public MainFrame(){
        super();
        this.setTitle("Handwritten digit recognition system");
        this.setSize(width, height);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(300, 300);
        this.setLayout(null);


        this.canvas = new Canvas(280,280);
        this.canvas.setBounds(new Rectangle(85, 30, 280, 280));
        this.add(this.canvas);

        this.Bpnet = new BPnetwork(784,40,10,0.8,0.37,0.001);

        //设置添加按钮
        this.jbAdd = new JButton();
        this.jbAdd.setText("add");
        this.jbAdd.setBounds(40,330,160,30);
        this.add(jbAdd);
        this.jbAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] outline = getOutline();
                if(outline[0] == -10){
                    canvas.clear();
                    JOptionPane.showMessageDialog(null,"Please draw one number into the canvas");
                }else{
                    String str = (String)JOptionPane.showInputDialog(null,"Please input the number you draw:\n","Tell me number",JOptionPane.PLAIN_MESSAGE,null,null,"");
                    try{
                        int number = Integer.parseInt(str);
                        if(number <=9 && number >=0){
                            Constant.digit = number;
                            String fileName = saveJPanel(outline);
                            canvas.clear();
                            JOptionPane.showMessageDialog(null,"Save success at"+fileName);
                        }else{
                            canvas.clear();
                            JOptionPane.showMessageDialog(null,"Input error");
                            Constant.digit = -1;
                        }
                    }catch (Exception error){
                        canvas.clear();
                        Constant.digit = -1;
                        JOptionPane.showMessageDialog(null,"Only for 0-9number");
                    }
                }
            }
        });

        //设置清除按钮
        this.jbClear = new JButton();
        this.jbClear.setText("clean");
        this.jbClear.setBounds(240,330,180,30);
        this.add(jbClear);
        this.jbClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.clear();
                Constant.digit = -1;
            }
        });

        //数据测试BP网络
        this.jbBPData = new JButton();
        this.jbBPData.setText("Data BP train");
        this.jbBPData.setBounds(40,370,160,30);
        this.jbBPData.setBackground(Color.ORANGE);
        this.add(jbBPData);
        this.jbBPData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BPnetwork Bptext = new BPnetwork(784,40,10,0.8,0.37,0.001);
                BPtrain dataTrain = new BPtrain("traindata2.csv", "text2.csv", "text1.csv", "traindata1.csv",450, Bptext);
                double answer = 0;
                try {
                    dataTrain.Train();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                try {
                    answer = dataTrain.Text();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                JOptionPane.showMessageDialog(null,"Data test finished.The success rate is: "+answer*100+"%");
            }
        });

        //BP手写数据训练
        this.jbBPTrain = new JButton();
        this.jbBPTrain.setText("train");
        this.jbBPTrain.setBackground(Color.ORANGE);
        this.jbBPTrain.setBounds(240,370,80,30);
        this.add(jbBPTrain);
        this.jbBPTrain.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> fileList = ImageUtil.getInstance().getImageList();
                if(fileList.size() < 200)
                    JOptionPane.showMessageDialog(null,"The train number must be greater than 200");
                else{
                    List<ImageModel> imageList = ImageUtil.getInstance().getImageModel(fileList);
                    if(train(imageList))
                        JOptionPane.showMessageDialog(null,"BPnet have trained!");
                }
            }
        });

        //BP手写测试
        this.jbBPTest = new JButton();
        this.jbBPTest.setText("text");
        this.jbBPTest.setBounds(340,370,80,30);
        this.jbBPTest.setBackground(Color.ORANGE);
        this.add(jbBPTest);
        this.jbBPTest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] outline = getOutline();
                double[] picture = ImageUtil.getInstance().getGrayMatrixFromPanel(canvas,outline);
                for(int lengthh=0;lengthh < picture.length;lengthh++){
                    if(picture[lengthh] >=0.5)
                        picture[lengthh] = 0;
                    else
                        picture[lengthh] = 1;
                }
                if(Bpnet.getTrain()) {
                    double[] answer = Bpnet.test(picture);
                    double max = answer[0];
                    int idx = -1;
                    for (int i = 1; i < 10; i++) {
                        if (answer[i] > max) {
                            max = answer[i];
                            idx = i;
                        }
                    }
                    if (idx == -1) {
                        JOptionPane.showMessageDialog(null, "I can not recognize this number");
                    } else {
                        JOptionPane.showMessageDialog(null, "I guess this number is:" + idx);
                    }
                }
                else
                    JOptionPane.showMessageDialog(null,"You have not trained the net");
            }
        });

        //数据测试CNN网络
        this.jbCNNData = new JButton();
        this.jbCNNData.setText("Data CNN train");
        this.jbCNNData.setBounds(40,410,160,30);
        this.jbCNNData.setBackground(Color.pink);
        this.add(jbCNNData);
        this.jbCNNData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //待完善
                JOptionPane.showMessageDialog(null,"Without a CNNnet");
            }
        });

        //CNN手写数据训练
        this.jbCNNTrain = new JButton();
        this.jbCNNTrain.setText("train");
        this.jbCNNTrain.setBounds(240,410,80,30);
        this.jbCNNTrain.setBackground(Color.pink);
        this.add(jbCNNTrain);
        this.jbCNNTrain.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //待完善
                JOptionPane.showMessageDialog(null,"Without a CNNnet");
            }
        });

        //CNN手写数据训练
        this.jbCNNTest = new JButton();
        this.jbCNNTest.setText("test");
        this.jbCNNTest.setBounds(340,410,80,30);
        this.jbCNNTest.setBackground(Color.pink);
        this.add(jbCNNTest);
        this.jbCNNTest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //待完善
                JOptionPane.showMessageDialog(null,"Without a CNNnet");
            }
        });

        this.setVisible(true);

    }

    public boolean train(List<ImageModel> text){
        this.Bptrain = new BPtrain(text,Bpnet,800);
        Bptrain.writeTrain();
        return true;
    }

    public int[] getOutline(){
        double[] grayMatrix = ImageUtil.getInstance().getGrayMatrixFromPanel(canvas, null);
        int[] binaryArray = ImageUtil.getInstance().transGrayToBinaryValue(grayMatrix);
        int minRow = Integer.MAX_VALUE;
        int maxRow = Integer.MIN_VALUE;
        int minCol = Integer.MAX_VALUE;
        int maxCol = Integer.MIN_VALUE;
        for(int i=0;i<binaryArray.length;i++){
            int row = i/28;
            int col = i%28;
            if(binaryArray[i] == 1){
                if(minRow > row){
                    minRow = row;
                }
                if(maxRow < row){
                    maxRow = row;
                }
                if(minCol > col){
                    minCol = col;
                }
                if(maxCol < col){
                    maxCol = col;
                }
            }
        }
        int len = Math.max((maxCol-minCol+1)*10, (maxRow-minRow+1)*10);
        canvas.setOutLine(minCol*10, minRow*10, len, len);

        return new int[]{minCol*10, minRow*10, len, len};
    }

    public String saveJPanel(int[] outline){
        Dimension imageSize = this.canvas.getSize();
        BufferedImage image = new BufferedImage(imageSize.width,imageSize.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        this.canvas.paint(graphics);
        graphics.dispose();
        try {
            //cut
            if(outline[0] + outline[2] > canvas.getWidth()){
                outline[2] = canvas.getWidth()-outline[0];
            }
            if(outline[1] + outline[3] > canvas.getHeight()){
                outline[3] = canvas.getHeight()-outline[1];
            }
            image = image.getSubimage(outline[0],outline[1],outline[2],outline[3]);
            //resize
            Image smallImage = image.getScaledInstance(Constant.Width, Constant.Height, Image.SCALE_SMOOTH);
            BufferedImage bSmallImage = new BufferedImage(Constant.Width,Constant.Height,BufferedImage.TYPE_INT_RGB);
            Graphics graphics1 = bSmallImage.getGraphics();
            graphics1.drawImage(smallImage, 0, 0, null);
            graphics1.dispose();

            String fileName = String.format("%s/%d_%s.jpg",Constant.trainFolder,Constant.digit,java.util.UUID.randomUUID());
            ImageIO.write(bSmallImage, "jpg", new File(fileName));
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
