import ij.process.ImageProcessor;

import java.awt.*;

public class Image {

    public Image() {
    }

    public enum DebayerSize {
        HALF_SIZE, FULL_SIZE
    }

    protected enum PixelType {
        RED, GREEN_TOPRED, GREEN_TOPBLUE, BLUE
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
        if (point.getX() == this.originalPictureHeight-1){
            return false;
        }
        return true;
    }

    protected Point getTopPixel(Point point){
        if (!hasTopPixel(point)){
            return null;
        }
        return new Point((int)point.getX()-1, (int)point.getY());
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

    protected Point getLeftPixel(Point point) {
        if (!hasLeftPixel(point)){
            return null;
        }
        return new Point((int)point.getX(), (int)point.getY()-1);
    }

    protected Point getBottomLeftPixel(Point point) {
        if (!hasBottomPixel(point)){
            return null;
        }
        Point bottomPixel = getBottomPixel(point);
        if (!hasLeftPixel(bottomPixel)){
            return null;
        }
        return getLeftPixel(bottomPixel);
    }

    protected Point getBottomRightPixel(Point point) {
        if (!hasBottomPixel(point)){
            return null;
        }
        Point bottomPixel = getBottomPixel(point);
        if (!hasRightPixel(bottomPixel)){
            return null;
        }
        return getRightPixel(bottomPixel);
    }

    protected Point getTopLeftPixel(Point point) {
        if (!hasTopPixel(point)){
            return null;
        }
        Point topPixel = getTopPixel(point);
        if (!hasLeftPixel(topPixel)){
            return null;
        }
        return getLeftPixel(topPixel);
    }

    protected Point getTopRightPixel(Point point) {
        if (!hasTopPixel(point)){
            return null;
        }
        Point topPixel = getTopPixel(point);
        if (!hasRightPixel(topPixel)){
            return null;
        }
        return getRightPixel(topPixel);
    }

    public boolean hasLeftPixel (Point point){
        if(point.getY() == 0){
            return false;
        }
        return true;
    }
}
