import ij.ImagePlus;
import ij.gui.NewImage;
import ij.plugin.PNG_Writer;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

public class Billard_Tracker implements PlugInFilter
{
    @Override
    public int setup(String arg, ImagePlus imp)
    {
        return DOES_8G;
    }

    private BayeredImage bayeredImage;

    @Override
    public void run(ImageProcessor imageProcessor) {

        BayeredImage bayeredImage = new BayeredImage(imageProcessor);

        ImagePlus halfSizeRGBImage = bayeredImage.getRGBImage();
        halfSizeRGBImage.show();
        halfSizeRGBImage.updateAndDraw();

        ImagePlus fullSizeRGBImage = bayeredImage.getRGBImage(Image.DebayerSize.FULL_SIZE);
        fullSizeRGBImage.show();
        fullSizeRGBImage.updateAndDraw();

        ImagePlus hueImage = bayeredImage.getHueImage();
        hueImage.show();
        hueImage.updateAndDraw();

        ImagePlus saturationImage = bayeredImage.getSaturationImage();
        saturationImage.show();
        saturationImage.updateAndDraw();

        ImagePlus brightnessImage = bayeredImage.getBrightnessImage();
        brightnessImage.show();
        brightnessImage.updateAndDraw();

        PNG_Writer png = new PNG_Writer();
        try {
            png.writeImage(halfSizeRGBImage, "./data_out/Billard1024x544x3.png", 0);
            png.writeImage(fullSizeRGBImage, "./data_out/Billard2048x1088x3.png", 0);
            png.writeImage(hueImage, "./data_out/Billard2048x1088_Hue.png", 0);
            png.writeImage(saturationImage, "./data_out/Billard2048x1088_Saturation.png", 0);
            png.writeImage(brightnessImage, "./data_out/Billard2048x1088_Brightness.png", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
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