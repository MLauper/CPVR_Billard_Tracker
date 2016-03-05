import java.awt.Color;
import ij.ImagePlus;
import ij.gui.NewImage;
import ij.plugin.PNG_Writer;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

public class Billard_Tracker implements PlugInFilter
{
    @Override
    public int setup(String arg, ImagePlus imp)
    {   return DOES_8G;
    }

    @Override
    public void run(ImageProcessor ip1)
    {   int w1 = ip1.getWidth();
        int h1 = ip1.getHeight();
        byte[] pix1 = (byte[]) ip1.getPixels();
        
        ImagePlus imgGray = NewImage.createByteImage("GrayDeBayered", w1/2, h1/2, 1, NewImage.FILL_BLACK);
        ImageProcessor ipGray = imgGray.getProcessor();
        byte[] pixGray = (byte[]) ipGray.getPixels();
        int w2 = ipGray.getWidth();
        int h2 = ipGray.getHeight();
        
        ImagePlus imgRGB = NewImage.createRGBImage("RGBDeBayered", w1/2, h1/2, 1, NewImage.FILL_BLACK);
        ImageProcessor ipRGB = imgRGB.getProcessor();
        int[] pixRGB = (int[]) ipRGB.getPixels();
        
        long msStart = System.currentTimeMillis();
        
        ImagePlus imgHue = NewImage.createByteImage("Hue", w1/2, h1/2, 1, NewImage.FILL_BLACK);
        ImageProcessor ipHue = imgHue.getProcessor();
        byte[] pixHue = (byte[]) ipHue.getPixels();
        
        int i1 = 0, i2 = 0;

        for (int y=0; y < h2; y++) 
        {   
            for (int x=0; x<w2; x++) 
            {
                // int G1 = pix1[i1] & 0xff;
                // int B = pix[i1+1] & 0xff;
                int position = y*w2 + x;
                int origPix = (pix1[position*2] + pix1[position*2+1] + pix1[position*2+y*w1] + pix1[position*2+y*w1+1]) / 4;
                pixGray[position] = (byte) origPix;
            }

        }

        long ms = System.currentTimeMillis() - msStart;
        System.out.println(ms);
        ImageStatistics stats = ipGray.getStatistics();
        System.out.println("Mean:" + stats.mean);
        
        PNG_Writer png = new PNG_Writer();
        try
        {   png.writeImage(imgRGB , "./data_in/Billard1024x544x3.png",  0);
            png.writeImage(imgHue,  "./data_in/Billard1024x544x1H.png", 0);
            png.writeImage(imgGray, "./data_in/Billard1024x544x1B.png", 0);
            
        } catch (Exception e)
        {   e.printStackTrace();
        }
        
        imgGray.show();
        imgGray.updateAndDraw();
        //imgRGB.show();
        imgRGB.updateAndDraw();
        //imgHue.show();
        imgHue.updateAndDraw();
    }
    
    public static void main(String[] args)
    {
        Billard_Tracker plugin = new Billard_Tracker();

        ImagePlus im = new ImagePlus("./data_in/Billard2048x1088x1.png");
        im.show();
        plugin.setup("", im);
        plugin.run(im.getProcessor());
    }
}
