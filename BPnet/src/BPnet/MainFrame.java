package BPnet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;

public class MainFrame extends JFrame{
    private int width = 450;
    private int height = 450;

    private Canvas canvas = null;
    private JButton jbAdd = null;
    private JButton jbClear = null;
    private JButton jbTrain = null;
    private JButton jbTest = null;
    private BPnetwork Bpnet;
    private BPtrain Bptrain;

    public MainFrame(){
        super();
        this.setTitle("Digital Recognizer");
        this.setSize(width, height);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(300, 300);
        this.setLayout(null);


        this.canvas = new Canvas(280,280);
        this.canvas.setBounds(new Rectangle(85, 30, 280, 280));
        this.add(this.canvas);

        this.Bpnet = new BPnetwork(784,40,10,0.8,0.37,0.001);

        this.jbAdd = new JButton();
        this.jbAdd.setText("add");
        this.jbAdd.setBounds(40,360,80,30);
        this.add(jbAdd);
        this.jbAdd.addActionListener(new ActionListener() {
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

        this.jbClear = new JButton();
        this.jbClear.setText("clean");
        this.jbClear.setBounds(140,360,80,30);
        this.add(jbClear);
        this.jbClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.clear();
                Constant.digit = -1;
            }
        });

        this.jbTrain = new JButton();
        this.jbTrain.setText("train");
        this.jbTrain.setBounds(240,360,80,30);
        this.add(jbTrain);
        this.jbTrain.addActionListener(new ActionListener() {
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

        this.jbTest = new JButton();
        this.jbTest.setText("text");
        this.jbTest.setBounds(340,360,80,30);
        this.add(jbTest);
        this.jbTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] outline = getOutline();
                double[] picture = ImageUtil.getInstance().getGrayMatrixFromPanel(canvas,outline);
                for(int lengthh=0;lengthh < picture.length;lengthh++){
                    if(picture[lengthh] >=0.5)
                        picture[lengthh] = 0;
                    else
                        picture[lengthh] = 1;
                }
                double[] answer = Bpnet.test(picture);
                double max = answer[0];
                int idx = -1;
                for (int i = 1; i < 10; i++) {
                    if (answer[i] > max) {
                        max = answer[i];
                        idx = i;
                    }
                }
                if(idx == -1){
                    JOptionPane.showMessageDialog(null,"I can not recognize this number");
                }else{
                    JOptionPane.showMessageDialog(null,"I guess this number is:"+idx);
                }
            }
        });

        this.setVisible(true);

    }

    public boolean train(List<ImageModel> text){
        this.Bptrain = new BPtrain(text,Bpnet,450);
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
