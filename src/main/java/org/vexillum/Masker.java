package org.vexillum;

import javafx.scene.paint.*;
import javafx.scene.image.*;

/**
 * Class containing static functions used to create the cushion designs in the runtime as needed
 */
public class Masker {
    /**
     * Returns a square cushion design with the provided design overlaid on the top
     * @param isSmall if true returns a design with 100px width, if false it's 1000px
     * @param designPath path for the design to be overlaid
     * @return a square cushion with the relevant design at either 100px or 1000px width
     */
    public static WritableImage standardCushion(boolean isSmall, String designPath) {
        Image cushion;
        if (isSmall) cushion = new Image("org/Assets/Cushions/SmallRegular.png");
        else cushion = new Image("org/Assets/Cushions/LargeRegular.png");

        return cushionMask(cushion, designPath);
    }

    /**
     * Returns a rectangular cushion design with the provided design overlaid on the top
     * @param isSmall if true returns a design with 100px width, if false it's 1000px
     * @param designPath path for the design to be overlaid
     * @return a rectangular cushion with the relevant design at either 100px or 1000px width
     */
    public static WritableImage longCushion(boolean isSmall, String designPath) {
        Image cushion;
        if (isSmall) cushion = new Image("org/Assets/Cushions/SmallLong.png");
        else cushion = new Image("org/Assets/Cushions/LargeLong.png");

        return cushionMask(cushion, designPath);
    }

    /**
     * The helper function only used inside this class to overlay the image onto the cushion
     * @param cushion the design of the cushion to use (square or rectangular)
     * @param designPath the path to the design inside the file directory
     * @return either a square or rectangular cushion with the relevant design, at either 100px or 1000px width
     */
    private static WritableImage cushionMask(Image cushion, String designPath) {
        //Since the mask is not going to line up with the height or width of the design, the design must be changed
        Image sizingImage = new Image(designPath);
        double newHeight = Math.max(sizingImage.getHeight(), cushion.getHeight());
        //Percentage increase is how much the design was stretched up by to match the mask size, as a decimal
        double percentageIncrease = newHeight / sizingImage.getHeight();
        //For the width Math.max is used, as generally you want to keep proportional sizing increase by multiplying by
        //the increase percent, but if that doesn't reach the width of the cushion mask then you have to use that instead
        double newWidth = Math.max(sizingImage.getWidth() * percentageIncrease, cushion.getWidth());
        //This is the design actually resized, from now on this is used over sizingImage
        Image designResize = new Image(designPath, newWidth, newHeight, false, true);

        //These next 4 variables are used to centre the design onto the centre of the mask, as otherwise they wouldn't
        //overlap as expected
        double heightCompare = cushion.getHeight() - newHeight;
        int offSetY = Math.signum(heightCompare) == 1 ? (int) (heightCompare / 2) : 0;

        double widthCompare = cushion.getWidth() - newWidth;
        //The inverse is needed here as centring the width is only needed when smaller than the old one
        int offSetX = Math.signum(widthCompare) == -1 ? (int) (widthCompare / -2) : 0;

        //combinedImage is the variable ultimately returned, and the one that will be slowly created below
        WritableImage combinedImage = new WritableImage((int) cushion.getWidth(), (int) cushion.getHeight());
        PixelReader cushionReader = cushion.getPixelReader();
        PixelReader designReader = designResize.getPixelReader();
        PixelWriter writer = combinedImage.getPixelWriter();

        for (int y = 0; y < combinedImage.getHeight(); y++) {
            for (int x = 0; x < combinedImage.getWidth(); x++) {
                Color cushionPixel = cushionReader.getColor(x, y);
                //Ensuring the offset is applied when getting the relevant location on the design
                Color designPixel = designReader.getColor(x + offSetX, y + offSetY);

                Color newPixel;
                //This conditional statement ensures that parts of the design that are black tones or not fully opaque
                //are not overlaid by the design, since they form the border
                if (cushionPixel.getOpacity() < 1 || (
                        cushionPixel.getRed() < 0.05 &&
                                cushionPixel.getGreen() < 0.05 &&
                                cushionPixel.getBlue() < 0.05)) {
                    newPixel = cushionPixel;
                }
                else newPixel = designPixel;

                writer.setColor(x, y, newPixel);
            }
        }

        return combinedImage;
    }
}
