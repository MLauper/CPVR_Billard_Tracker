import ij.process.ImageProcessor;

import java.awt.*;

public class Image {

    public enum DebayerSize {
        HALF_SIZE, FULL_SIZE
    }

    protected ImageProcessor imageProcessor;
    protected int originalPictureWidth;
    protected int originalPictureHeight;
    protected byte[] originalPixel;
    protected ImageProcessor originalImageProcessor;

    public Image(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
        originalImageProcessor = imageProcessor;
        originalPictureWidth = imageProcessor.getWidth();
        originalPictureHeight = imageProcessor.getHeight();
        originalPixel = (byte[]) imageProcessor.getPixels();
    }

    public int getAbsolutPixelPosition(Point point){
        return getAbsolutPixelPosition(point,originalPictureHeight,originalPictureWidth);
    }


    protected int getAbsolutPixelPosition(Point point, int height, int width) {
        return (int) ((width * point.getX()) + point.getY());
    }

    protected Point getPointFromAbsoultePosition(int absolutePosition) {
        return new Point((int) Math.floor(absolutePosition/originalPictureWidth), absolutePosition % originalPictureWidth);
    }

    public boolean hasTopPixel (Point point){
        if (point.getX() == 0){
            return false;
        }
        return true;
    }

    public boolean hasRightPixel (Point point){
        if (point.getY() == this.originalPictureWidth){
            return false;
        }
        return true;
    }

    public boolean hasBottomPixel (Point point){
        if (point.getX() == this.originalPictureHeight){
            return false;
        }
        return true;
    }

    protected Point getRightPixel(Point point){
        if (hasRightPixel(point) == false){
            return null;
        }
        return new Point((int)point.getX(), (int)point.getY() + 1);
    }

    protected Point getBottomPixel(Point point) {
        if (hasBottomPixel(point) == false){
            return null;
        }
        return new Point((int)point.getX() + 1, (int)point.getY());
    }

    protected Point getBottomRightPixel(Point topLeftPixel) {
        if (hasBottomPixel(topLeftPixel) == false){
            return null;
        }
        Point bottomPixel = getBottomPixel(topLeftPixel);
        if (hasRightPixel(bottomPixel) == false){
            return null;
        }
        return getRightPixel(bottomPixel);
    }

    public boolean hasLeftPixel (Point point){
        if(point.getY() == 0){
            return false;
        }
        return true;
    }
}
