import ij.ImagePlus;
import ij.gui.NewImage;
import ij.process.ImageProcessor;

import java.awt.*;

public class BayeredToRGBConverter extends Image {
    /**
     * Expects a bayered image.
     * Supports generation of a half size or full size RGB image.
     * Multiple algorithms are supported.
     */
    private ImagePlus halfSizeRGB;
    private ImagePlus fullSizeRGB;
    private ImagePlus hueImage;
    int[] halfSizePixRGB;
    int[] fullSizePixRGB;
    byte[] imagePixHue;
    private ImagePlus saturationImage;
    byte[] imagePixSaturation;
    private ImagePlus brightnessImage;
    byte[] imagePixBrightness;
    private ImagePlus redImage;
    private ImagePlus greenImage;
    private ImagePlus blueImage;
    private byte[] redImagePixels;
    private byte[] greenImagePixels;
    private byte[] blueImagePixels;

    public BayeredToRGBConverter(ImageProcessor imageProcessor) {
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
        if (fullSizeRGB != null){
            return fullSizeRGB;
        }
        buildFullSizeRGB();
        return fullSizeRGB;
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

    private void buildFullSizeRGB() {
        initializeFullSizeRGB();
        traverseBayeredPatternFullSizeRGB();
    }

    private void initializeHalfSizeRGB() {
        halfSizeRGB = NewImage.createRGBImage("HallfSizeRGBDebayered", (int)Math.floor(originalImageWidth / 2), (int)Math.floor(originalImageHeight / 2), 1, NewImage.FILL_BLACK);
        ImageProcessor ipRGB = halfSizeRGB.getProcessor();
        halfSizePixRGB = (int[]) ipRGB.getPixels();
    }

    private void initializeFullSizeRGB() {
        fullSizeRGB = NewImage.createRGBImage("FullfSizeRGBDebayered", originalImageWidth, originalImageHeight, 1, NewImage.FILL_BLACK);
        ImageProcessor ipRGB = fullSizeRGB.getProcessor();
        fullSizePixRGB = (int[]) ipRGB.getPixels();
    }

    private void traverseBayeredPatternHalfSizeRGB(){
        // TODO: Remove magic offset for bayered Pattern
        int originalPositionX = 0;
        int originalPositionY = 1;

        for (int newX = 0; originalPositionX < originalImageHeight -1; newX++){
            for (int newY = 0; originalPositionY < originalImageWidth -1; newY++){
                Point newPosition = new Point(newX,newY);
                int newAbsoultPosition = getAbsolutPixelPosition(newPosition, originalImageHeight / 2, originalImageWidth / 2);
                halfSizePixRGB[newAbsoultPosition] = getAverageRGB(new Point(originalPositionX,originalPositionY));
                originalPositionY += 2;
            }
            originalPositionY = 0;
            originalPositionX += 2;
        }
    }

    private void traverseBayeredPatternFullSizeRGB() {
        // TODO: Remove magic offset for bayered Pattern

        for (int x = 0; x < originalImageHeight -1; x++){
            for (int y = 1; y < originalImageWidth -1; y++){
                Point position = new Point(x,y);
                int absolutePosition = getAbsolutPixelPosition(position);

                PixelType pixelType = null;

                if (x%2 == 0 && y%2 == 0) pixelType = PixelType.GREEN_TOPRED;
                if (x%2 == 0 && y%2 == 1) pixelType = PixelType.BLUE;
                if (x%2 == 1 && y%2 == 0) pixelType = PixelType.RED;
                if (x%2 == 1 && y%2 == 1) pixelType = PixelType.GREEN_TOPBLUE;

                fullSizePixRGB[absolutePosition] = getFullSizeRGB(new Point(x,y),pixelType);
            }
        }
    }

    private int getAverageRGB(Point topLeftPixel){
        if(!hasRightBottomAndBottomRightPixel(topLeftPixel)){
            return 0;
        }

        Point topRightPixel = this.getRightPixel(topLeftPixel);
        Point bottomLeftPixel = this.getBottomPixel(topLeftPixel);
        Point bottomRightPixel = this.getBottomRightPixel(topLeftPixel);

        byte redPixel = this.getPixelColor(bottomLeftPixel);
        byte greenPixel1 = this.getPixelColor(topLeftPixel);
        byte greenPixel2 = this.getPixelColor(bottomRightPixel);
        byte greenPixel = (byte) (((int)greenPixel1 + (int)greenPixel2) / 2);
        byte bluePixel = this.getPixelColor(topRightPixel);

        return (convertByteRGBtoIntRGB(redPixel, greenPixel, bluePixel));
    }

    private int getFullSizeRGB(Point point, Image.PixelType pixelType) {
        byte redPixel = 0;
        byte greenPixel = 0;
        byte bluePixel = 0;

        if(isCentricPixel(point)){

            if (pixelType == PixelType.RED){
                redPixel = (byte) getPixelColor(point);
                greenPixel = (byte) ((
                        getPixelColor(getTopPixel(point)) +
                                getPixelColor(getRightPixel(point)) +
                                getPixelColor(getBottomPixel(point)) +
                                getPixelColor(getLeftPixel(point))
                ) / 4);
                bluePixel = (byte) ((
                        getPixelColor(getTopLeftPixel(point)) +
                                getPixelColor(getTopRightPixel(point)) +
                                getPixelColor(getBottomLeftPixel(point)) +
                                getPixelColor(getBottomRightPixel(point))
                ) / 4);
            }
            else if (pixelType == PixelType.GREEN_TOPRED){
                redPixel = (byte) ((
                        getPixelColor(getTopPixel(point)) +
                                getPixelColor(getBottomPixel(point))
                ) / 2);
                greenPixel = (byte) getPixelColor(point);
                bluePixel = (byte) ((
                        getPixelColor(getLeftPixel(point)) +
                                getPixelColor(getRightPixel(point))
                ) / 2);
            }
            else if (pixelType == PixelType.GREEN_TOPBLUE) {
                redPixel = (byte) ((
                        getPixelColor(getLeftPixel(point)) +
                                getPixelColor(getRightPixel(point))
                ) / 2);
                greenPixel = (byte) getPixelColor(point);
                bluePixel = (byte) ((
                        getPixelColor(getTopPixel(point)) +
                                getPixelColor(getBottomPixel(point))
                ) / 2);
            }
            else if (pixelType == PixelType.BLUE){
                redPixel = (byte) ((
                        getPixelColor(getTopLeftPixel(point)) +
                                getPixelColor(getTopRightPixel(point)) +
                                getPixelColor(getBottomLeftPixel(point)) +
                                getPixelColor(getBottomRightPixel(point))
                ) / 4);
                greenPixel = (byte) ((
                        getPixelColor(getTopPixel(point)) +
                                getPixelColor(getRightPixel(point)) +
                                getPixelColor(getBottomPixel(point)) +
                                getPixelColor(getLeftPixel(point))
                ) / 4);
                bluePixel = (byte) getPixelColor(point);
            }
        }
        return (convertByteRGBtoIntRGB(redPixel, greenPixel, bluePixel));
    }

    private boolean isCentricPixel(Point point) {
        if (hasTopPixel(point) && hasRightPixel(point) && hasBottomPixel(point) && hasLeftPixel(point)){
            Point topPixel = getTopPixel(point);
            Point bottomPixel = getBottomPixel(point);
            if (hasLeftPixel(topPixel) && hasRightPixel(topPixel) && hasLeftPixel(bottomPixel) && hasRightPixel(bottomPixel)){
                return true;
            }
        }
        return false;
    }

    private int convertByteRGBtoIntRGB(byte red, byte green, byte blue) {
        return ( (red & 0xff)<<16) + ((green & 0xff)<<8) + ((blue & 0xff) );
    }

    private boolean hasRightBottomAndBottomRightPixel(Point topLeftPixel) {
        return (hasRightPixel(topLeftPixel) && hasBottomPixel(topLeftPixel) && hasRightPixel(getBottomPixel(topLeftPixel)));
    }

    public byte getPixelColor(Point point){
        int absolutePosition = getAbsolutPixelPosition(point);
        return originalPixel[absolutePosition];
    }


    public ImagePlus getHueImage(){
        return createOrReturnHueImage();
    }

    private ImagePlus createOrReturnHueImage() {
        getRGBImage();
        if (hueImage != null){
            return hueImage;
        }
        buildHSBImages();
        return hueImage;
    }

    public ImagePlus getSaturationImage(){
        return createOrReturnSaturationImage();
    }

    private ImagePlus createOrReturnSaturationImage() {
        getRGBImage();
        if (saturationImage != null){
            return saturationImage;
        }
        buildHSBImages();
        return saturationImage;
    }

    public ImagePlus getBrightnessImage(){
        return createOrReturnBrighnessImage();
    }

    private ImagePlus createOrReturnBrighnessImage() {
        getRGBImage();
        if (brightnessImage != null){
            return brightnessImage;
        }
        buildHSBImages();
        return brightnessImage;
    }

    private void buildHSBImages() {
        inittializeHueImage();
        initializeSaturationImage();
        initializeBrightnessImage();
        convertRGBtoHSB();
    }

    private void inittializeHueImage() {
        hueImage = NewImage.createByteImage("HueImage", originalImageWidth, originalImageHeight, 1, NewImage.FILL_BLACK);
        ImageProcessor ipHue = hueImage.getProcessor();
        imagePixHue = (byte[]) ipHue.getPixels();
    }

    private void initializeSaturationImage() {
        saturationImage = NewImage.createByteImage("SaturationImage", originalImageWidth, originalImageHeight, 1, NewImage.FILL_BLACK);
        ImageProcessor ipSaturation = saturationImage.getProcessor();
        imagePixSaturation = (byte[]) ipSaturation.getPixels();
    }

    private void initializeBrightnessImage() {
        brightnessImage = NewImage.createByteImage("BrightnessImage", originalImageWidth, originalImageHeight, 1, NewImage.FILL_BLACK);
        ImageProcessor ipBrightness = brightnessImage.getProcessor();
        imagePixBrightness = (byte[]) ipBrightness.getPixels();
    }

    private void convertRGBtoHSB() {
        for (int x = 0; x < originalImageHeight -1; x++){
            for (int y = 1; y < originalImageWidth -1; y++){
                Point position = new Point(x,y);
                int absolutePosition = getAbsolutPixelPosition(position);

                int red = fullSizePixRGB[absolutePosition] >> 16;
                int green = fullSizePixRGB[absolutePosition] >> 8 & 0xff;
                int blue = fullSizePixRGB[absolutePosition] & 0xff;

                float[] hsb = Color.RGBtoHSB(red,green,blue,null);

                imagePixHue[absolutePosition] = (byte) (hsb[0]*255);
                imagePixSaturation[absolutePosition] = (byte) (hsb[1]*255);
                imagePixBrightness[absolutePosition] = (byte) (hsb[2]*255);
            }
        }
    }

    public ImagePlus getRedImage() {
        return createOrReturnImage(Channel.RED);
    }

    public ImagePlus getGreenImage() {
        return createOrReturnImage(Channel.GREEN);
    }

    public ImagePlus getBlueImage() {
        return createOrReturnImage(Channel.BLUE);
    }

    private ImagePlus createOrReturnImage(Image.Channel color){
        getRGBImage();
        if (color == Channel.RED){
            if (redImage != null){
                return redImage;
            }
            initializeSingleColorImages();
            convertRGBtoSingleColors();
            return redImage;
        }
        else if (color == Channel.GREEN){
            if (greenImage != null){
                return greenImage;
            }
            initializeSingleColorImages();
            convertRGBtoSingleColors();
            return greenImage;
        }
        else if (color == Channel.BLUE){
            if (blueImage != null){
                return blueImage;
            }
            initializeSingleColorImages();
            convertRGBtoSingleColors();
            return blueImage;
        }
        return null;
    }

    private void initializeSingleColorImages() {
        redImage = NewImage.createByteImage("RedImage", originalImageWidth, originalImageHeight, 1, NewImage.FILL_BLACK);
        ImageProcessor ipRed = redImage.getProcessor();
        redImagePixels = (byte[]) ipRed.getPixels();

        greenImage = NewImage.createByteImage("GreenImage", originalImageWidth, originalImageHeight, 1, NewImage.FILL_BLACK);
        ImageProcessor ipGreen = greenImage.getProcessor();
        greenImagePixels = (byte[]) ipGreen.getPixels();

        blueImage = NewImage.createByteImage("BlueImage", originalImageWidth, originalImageHeight, 1, NewImage.FILL_BLACK);
        ImageProcessor ipBlue = blueImage.getProcessor();
        blueImagePixels = (byte[]) ipBlue.getPixels();
    }

    private void convertRGBtoSingleColors() {
        for (int x = 0; x < originalImageHeight -1; x++){
            for (int y = 1; y < originalImageWidth -1; y++){
                Point position = new Point(x,y);
                int absolutePosition = getAbsolutPixelPosition(position);

                int red = fullSizePixRGB[absolutePosition] >> 16;
                int green = fullSizePixRGB[absolutePosition] >> 8 & 0xff;
                int blue = fullSizePixRGB[absolutePosition] & 0xff;

                redImagePixels[absolutePosition] = (byte) red;
                greenImagePixels[absolutePosition] = (byte) green;
                blueImagePixels[absolutePosition] = (byte) blue;
            }
        }
    }
}
