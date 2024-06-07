package org.vexillum;

import javafx.scene.paint.*;
import javafx.scene.image.*;

public class Masker {
    public static WritableImage standardCushion(boolean isSmall, String designPath) {
        Image cushion;
        if (isSmall) cushion = new Image("org/Assets/Cushions/SmallRegular.png");
        else cushion = new Image("org/Assets/Cushions/LargeRegular.png");

        return cushionMask(cushion, designPath);
    }

    public static WritableImage longCushion(boolean isSmall, String designPath) {
        Image cushion;
        if (isSmall) cushion = new Image("org/Assets/Cushions/SmallLong.png");
        else cushion = new Image("org/Assets/Cushions/LargeLong.png");

        return cushionMask(cushion, designPath);
    }

    private static WritableImage cushionMask(Image cushion, String designPath) {
        Image sizingImage = new Image(designPath);
        double newHeight = Math.max(sizingImage.getHeight(), cushion.getHeight());
        double percentageIncrease = newHeight / sizingImage.getHeight();
        double newWidth = Math.max(sizingImage.getWidth() * percentageIncrease, cushion.getWidth());
        Image designResize = new Image(designPath, newWidth, newHeight, false, true);

        double heightCompare = cushion.getHeight() - newHeight;
        int offSetY = Math.signum(heightCompare) == 1 ? (int) (heightCompare / 2) : 0;

        double widthCompare = cushion.getWidth() - newWidth;
        int offSetX = Math.signum(widthCompare) == -1 ? (int) (widthCompare / -2) : 0;

        WritableImage combinedImage = new WritableImage((int) cushion.getWidth(), (int) cushion.getHeight());
        PixelReader cushionReader = cushion.getPixelReader();
        PixelReader designReader = designResize.getPixelReader();
        PixelWriter writer = combinedImage.getPixelWriter();

        for (int y = 0; y < combinedImage.getHeight(); y++) {
            for (int x = 0; x < combinedImage.getWidth(); x++) {
                Color cushionPixel = cushionReader.getColor(x, y);
                Color designPixel = designReader.getColor(x + offSetX, y + offSetY);

                Color newPixel;
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
