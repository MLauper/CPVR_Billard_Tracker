import ij.ImagePlus;
import ij.gui.NewImage;
import ij.process.ImageProcessor;

import java.awt.*;

public class BayeredImage extends Image {

    private ImagePlus halfSizeRGB;
    private ImagePlus fullSizeRGB;
    int[] halfSizePixRGB;
    int[] fullSizePixRGB;

    public BayeredImage(ImageProcessor imageProcessor) {
        super(imageProcessor);
    }

    public ImagePlus getRGBImage(DebayerSize debayerSize) {
        if (debayerSize == DebayerSize.HALF_SIZE){
            return createOrReturnHalfSizeRGB();
        }
        else {
            return createOrReturnFullSizeRGB();
        }
    }

    public ImagePlus getRGBImage(){
        return getRGBImage(DebayerSize.HALF_SIZE);
    }

    private ImagePlus createOrReturnFullSizeRGB() {
        return null;
    }

    private ImagePlus createOrReturnHalfSizeRGB() {
        if (halfSizeRGB != null){
            return halfSizeRGB;
        }
        buildHalfSizeRGB();
        return halfSizeRGB;
    }

    private void buildHalfSizeRGB() {
        initializeHalfSizeRGB();
        traverseBayeredPatternHalfSizeRGB();
    }

    private void initializeHalfSizeRGB() {
        halfSizeRGB = NewImage.createRGBImage("RGBDeBayered", (int)Math.floor(originalPictureWidth / 2), (int)Math.floor(originalPictureHeight / 2), 1, NewImage.FILL_BLACK);
        ImageProcessor ipRGB = halfSizeRGB.getProcessor();
        halfSizePixRGB = (int[]) ipRGB.getPixels();
    }

    private void traverseBayeredPatternHalfSizeRGB(){
        // TODO: Remove magic offset for bayered Pattern
        int originalPositionX = 0;
        int originalPositionY = 1;
        Point currentPoint;
        int absolutePosition;
        int imageHeight = halfSizeRGB.getHeight()-2;
        int imageWidth = halfSizeRGB.getWidth()-2;

        for (int newX = 0; originalPositionX < originalPictureHeight-1; newX++){
            for (int newY = 0; originalPositionY < originalPictureWidth-1; newY++){
                Point newPosition = new Point(newX,newY);
                int newAbsoultPosition = getAbsolutPixelPosition(newPosition, originalPictureHeight / 2, originalPictureWidth / 2);
                halfSizePixRGB[newAbsoultPosition] = getAverageRGB(new Point(originalPositionX,originalPositionY));
                originalPositionY += 2;
            }
            originalPositionY = 0;
            originalPositionX += 2;
        }
    }

    private int getAverageRGB(Point topLeftPixel){
        if(hasRightPixel(topLeftPixel) == false || hasBottomPixel(topLeftPixel) == false){
            return 0;
        }

        Point topRightPixel = this.getRightPixel(topLeftPixel);
        Point bottomLeftPixel = this.getBottomPixel(topLeftPixel);
        Point bottomRightPixel = this.getBottomRightPixel(topLeftPixel);

        byte greenPixel1 = this.getPixelColor(topLeftPixel);
        byte greenPixel2 = this.getPixelColor(bottomRightPixel);
        byte greenPixel = (byte) (((int)greenPixel1 + (int)greenPixel2) / 2);
        byte bluePixel = this.getPixelColor(topRightPixel);
        byte redPixel = this.getPixelColor(bottomLeftPixel);

        int pixelColor = ((redPixel & 0xff)<<16)+((greenPixel & 0xff)<<8) + (bluePixel & 0xff);

        return (pixelColor);
    }

    public byte getPixelColor(Point point){
        int absolutePosition = getAbsolutPixelPosition(point);
        return originalPixel[absolutePosition];
    }

}
