import ij.process.ImageProcessor;

import java.awt.*;

public abstract class Image {

    public enum DebayerSize {
        HALF_SIZE, FULL_SIZE
    }

    protected enum PixelType {
        RED, GREEN_TOPRED, GREEN_TOPBLUE, BLUE
    }

    protected enum Channel {
        RED, GREEN, BLUE
    }
    protected ImageProcessor imageProcessor;
    protected int originalImageWidth;
    protected int originalImageHeight;
    protected byte[] originalPixel;
    protected ImageProcessor originalImageProcessor;

    public Image(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
        originalImageProcessor = imageProcessor;
        originalImageWidth = imageProcessor.getWidth();
        originalImageHeight = imageProcessor.getHeight();
        originalPixel = (byte[]) imageProcessor.getPixels();
    }

    protected int getAbsolutPixelPosition(Point point){
        return getAbsolutPixelPosition(point, originalImageHeight, originalImageWidth);
    }


    protected int getAbsolutPixelPosition(Point point, int height, int width) {
        return (int) ((width * point.getX()) + point.getY());
    }

    protected Point getPointFromAbsoultePosition(int absolutePosition) {
        return new Point((int) Math.floor(absolutePosition/ originalImageWidth), absolutePosition % originalImageWidth);
    }

    protected boolean hasTopPixel(Point point){
        return point.getX() != 0;
    }

    protected boolean hasRightPixel(Point point){
        return point.getY() != this.originalImageWidth -1;
    }

    protected boolean hasBottomPixel(Point point){
        return point.getX() != this.originalImageHeight -1;
    }

    protected boolean hasDeltaPixel(Point origin, int deltaX, int deltaY){
        if (isInXRange((int) (origin.getX()+deltaX)) && isInYRange((int) (origin.getY()+deltaY))){
            return true;
        }
        return false;
    }

    protected boolean isInYRange(int y) {
        return y >= 0 && y <= (originalImageWidth -1);
    }

    protected boolean isInXRange(int x) {
        return x >= 0 && x <= (originalImageHeight -1);
    }

    protected Point getTopPixel(Point point){
        if (!hasTopPixel(point)){
            return null;
        }
        return new Point((int)point.getX()-1, (int)point.getY());
    }

    protected Point getRightPixel(Point point){
        if (!hasRightPixel(point)){
            return null;
        }
        return new Point((int)point.getX(), (int)point.getY() + 1);
    }

    protected Point getBottomPixel(Point point) {
        if (!hasBottomPixel(point)){
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
        return point.getY() != 0;
    }
}
