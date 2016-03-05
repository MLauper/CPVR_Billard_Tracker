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

    private BayeredImage bayeredImage;

    @Override
    public void run(ImageProcessor imageProcessor) {

        BayeredImage bayeredImage = new BayeredImage(imageProcessor);

        //ImagePlus imgSample = bayeredImage.getSampleImage();
        //imgSample.show();

        //ImagePlus imgGray = bayeredImage.getGrayscaleImage();

        ImagePlus imgRGB = bayeredImage.getRGBImage();

        long msStart = System.currentTimeMillis();

        //ImagePlus imgHue = bayeredImage.getHueImage();

        long ms = System.currentTimeMillis() - msStart;

        System.out.println(ms);

        //ImageStatistics stats = ipGray.getStatistics();
        //System.out.println("Mean:" + stats.mean);

        PNG_Writer png = new PNG_Writer();
        try {
            png.writeImage(imgRGB, "./data_out/Billard1024x544x3.png", 0);
            //png.writeImage(imgHue, "./data_out/Billard1024x544x1H.png", 0);
            //png.writeImage(imgGray, "./data_out/Billard1024x544x1B.png", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //imgGray.show();
        //imgGray.updateAndDraw();
        imgRGB.show();
        imgRGB.updateAndDraw();
        //imgHue.show();
        //imgHue.updateAndDraw();
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
