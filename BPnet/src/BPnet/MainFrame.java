package BPnet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame{
    private int width = 450;
    private int height = 450;
    private BPnetwork Bp = null;
    private Canvas canvas = null;

    private JButton jbTest = null;
    private JButton jbClear = null;

    public MainFrame(BPnetwork text) throws Exception{
        super();
        this.setTitle("Digital Recognizer");
        this.setSize(width, height);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(300, 300);
        this.setLayout(null);
        this.Bp = text;

        this.canvas = new Canvas(280,280);
        this.canvas.setBounds(new Rectangle(85, 30, 280, 280));
        this.add(this.canvas);

        this.jbClear = new JButton();
        this.jbClear.setText("clear");
        this.jbClear.setBounds(40, 360, 80, 30);
        this.add(jbClear);
        this.jbClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.clear();
                Constant.digit = -1;
            }
        });

        this.jbTest = new JButton();
        this.jbTest.setText("test");
        this.jbTest.setBounds(340, 360, 80, 30);
        this.add(jbTest);
        this.jbTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] outline = getOutline();
                double[] picture = ImageUtil.getInstance().getGrayMatrixFromPanel(canvas, outline);
                for(int lengthh=0;lengthh < picture.length;lengthh++){
                    if(picture[lengthh] >=0.5)
                        picture[lengthh] = 0;
                    else
                        picture[lengthh] = 1;
                }
                double[] answer = Bp.test(picture);
                double max = answer[0];
                int idx = 0;
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
}
