package BPnet;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageUtil {
    private static ImageUtil imageUtil = null;
    private int smallWidth = Constant.Width;
    private int smallHeight = Constant.Height;

    private ImageUtil(){}

    public static ImageUtil getInstance(){
        if(imageUtil == null){
            imageUtil = new ImageUtil();
        }
        return imageUtil;
    }

    //list all jpg file in train folder
    public List<String> getImageList(){
        File file = new File(Constant.trainFolder);
        List<String> fileList = new ArrayList<String>();
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for(File fileItem:files){
                if(fileItem.isFile() && fileItem.getAbsolutePath().endsWith(".jpg")){
                    fileList.add(fileItem.getAbsolutePath());
                }
            }
        }
        return fileList;
    }

    public double[] getGrayMatrixFromPanel(Canvas canvas, int[] outline){
        Dimension imageSize = canvas.getSize();
        BufferedImage image = new BufferedImage(imageSize.width,imageSize.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        canvas.paint(graphics);
        graphics.dispose();

        //cut
        if(outline != null){
            if(outline[0] + outline[2] > canvas.getWidth()){
                outline[2] = canvas.getWidth()-outline[0];
            }
            if(outline[1] + outline[3] > canvas.getHeight()){
                outline[3] = canvas.getHeight()-outline[1];
            }
            image = image.getSubimage(outline[0],outline[1],outline[2],outline[3]);
        }
        //resize to 28*28
        Image smallImage = image.getScaledInstance(smallWidth, smallHeight, Image.SCALE_SMOOTH);
        BufferedImage bSmallImage = new BufferedImage(smallWidth,smallHeight,BufferedImage.TYPE_INT_RGB);
        Graphics graphics1 = bSmallImage.getGraphics();
        graphics1.drawImage(smallImage, 0, 0, null);
        graphics1.dispose();

        //get gray value
        int[] pixes = new int[smallWidth*smallHeight];
        double[] grayMatrix = new double[smallWidth*smallHeight];
        int index = -1;
        pixes = (int[])bSmallImage.getRaster().getDataElements(0,0,smallWidth,smallHeight,pixes);
        for(int i=0;i<smallWidth;i++){
            for(int j=0;j<smallHeight;j++){
                int rgb = pixes[i*smallWidth+j];
                int r = (rgb & 0xff0000) >> 16;
                int g = (rgb & 0xff00) >> 8;
                int b = (rgb & 0xff);
                double gray = Double.valueOf(r * 299 + g * 587 + b * 114 + 500)/255000.0;

                grayMatrix[++index] = gray;
            }
        }
        return grayMatrix;
    }

    public int[] transGrayToBinaryValue(double[] input){
        int[] binaryArray = new int[input.length];
        for(int i=0;i<input.length;i++){
            if(Double.compare(0.7, input[i]) >= 0){
                binaryArray[i] = 1;
            }else{
                binaryArray[i] = 0;
            }
        }
        return binaryArray;
    }
}
