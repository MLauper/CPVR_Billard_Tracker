import ij.ImagePlus;
import ij.gui.NewImage;
import ij.process.ImageProcessor;

public class HueImage extends Image{

    public HueImage(ImageProcessor imageProcessor) {
        super(imageProcessor);
    }

    public ImagePlus getHueImage(){
        ImagePlus imgHue = NewImage.createByteImage("Hue", originalPictureWidth / 2, originalPictureHeight / 2, 1, NewImage.FILL_BLACK);
        ImageProcessor ipHue = imgHue.getProcessor();
        byte[] pixHue = (byte[]) ipHue.getPixels();

        return imgHue;
    }
}
