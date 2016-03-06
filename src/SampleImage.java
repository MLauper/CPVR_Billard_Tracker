import ij.ImagePlus;
import ij.gui.NewImage;
import ij.process.ImageProcessor;

import java.awt.*;

public class SampleImage extends Image {
    public SampleImage(ImageProcessor imageProcessor) {
        super(imageProcessor);
    }

    public ImagePlus getSampleImage() {
        ImagePlus imgSample = NewImage.createByteImage("SampleImage", originalImageWidth / 2, originalImageHeight / 2, 1, NewImage.FILL_BLACK);
        ImageProcessor ipSample = imgSample.getProcessor();
        byte[] pixSample = (byte[]) ipSample.getPixels();
        int w2 = ipSample.getWidth();
        int h2 = ipSample.getHeight();

        int i1 = 0, i2 = 0;

        //   for (int y = 0; y < h2; y++) {
        //       for (int x = 0; x < w2; x++) {
        //           int position = y * w2 + x;
        //           pixSample[position] = getPixelColor(new Point(x,y));
        //       }
        //   }

        pixSample = traverseBayerPattern(pixSample);

        return imgSample;
    }

    private byte[] traverseBayerPattern(byte[] newPixels){
        int newX;
        int newY;
        int origX = 0;
        int origY = 0;

        for (newX = 0; origX < originalImageHeight; newX++){
            for (newY = 0; origY < originalImageWidth; newY++){
                Point newPosition = new Point(newX,newY);
                int newAbsoultPosition = getAbsolutPixelPosition(newPosition, originalImageHeight / 2, originalImageWidth / 2);

                newPixels[newAbsoultPosition] = getGrayscaleFromBayered(new Point(origX,origY));
                //newPixels[newAbsoultPosition] = (byte)255;

                origY += 2;
            }
            origY = 0;
            origX += 2;
        }
        return newPixels;
    }

    public ImagePlus getGrayscaleImage() {
        ImagePlus imgGray = NewImage.createByteImage("GrayDeBayered", originalImageWidth / 2, originalImageHeight / 2, 1, NewImage.FILL_BLACK);
        ImageProcessor ipGray = imgGray.getProcessor();
        byte[] pixGray = (byte[]) ipGray.getPixels();
        int w2 = ipGray.getWidth();
        int h2 = ipGray.getHeight();

        int i1 = 0, i2 = 0;

        for (int y = 0; y < h2; y++) {
            for (int x = 0; x < w2; x++) {
                // int G1 = originalPixel[i1] & 0xff;
                // int B = originalPixel[i1+1] & 0xff;
                int position = y * w2 + x;
                int origPix = (originalPixel[position * 2] + originalPixel[position * 2 + 1] + originalPixel[position * 2 + y * originalImageWidth] + originalPixel[position * 2 + y * originalImageWidth + 1]) / 4;
                pixGray[position] = (byte) origPix;
            }

        }

        return imgGray;
    }

    private byte getGrayscaleFromBayered(Point topLeftPixel){
        if(hasRightPixel(topLeftPixel) == false || hasBottomPixel(topLeftPixel) == false){
            return 0;
        }

        Point topRightPixel = this.getRightPixel(topLeftPixel);
        Point bottomLeftPixel = this.getBottomPixel(topLeftPixel);

        byte topLeftPixelGrayscale = this.getPixelColor(topLeftPixel);
        byte topRightPixelGrayscale = this.getPixelColor(topRightPixel);
        byte bottomLeftPixelGrayscale = this.getPixelColor(bottomLeftPixel);

        byte averageGrayscale = (byte) ((topLeftPixelGrayscale + topRightPixelGrayscale + bottomLeftPixelGrayscale) / 3);

        return (byte) (averageGrayscale);
    }

    public byte getPixelColor(Point point){
        int absolutePosition = getAbsolutPixelPosition(point);
        return originalPixel[absolutePosition];
    }

}
