package cz.upol.mobyg.utils;

import javafx.scene.image.PixelReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Medel on 15/07/2015.
 */
public final class ImageUtils {

    private ImageUtils() {
    }

    public static void saveImage(PixelReader pixelReader, String name) {
        BufferedImage image1 = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
        javafx.scene.paint.Color c;
        for (int i = 0; i < 480; i++) {
            for (int j = 0; j < 640; j++) {
                c = pixelReader.getColor(j, i);
                int r = (int) (c.getRed() * 255);
                int g = (int) (c.getGreen() * 255);
                int b = (int) (c.getBlue() * 255);

                int rgb = r;
                rgb = (rgb << 8) + g;
                rgb = (rgb << 8) + b;
                image1.setRGB(j, i, rgb);
            }
        }
        File outputfile = new File(name);
        try {
            ImageIO.write(image1, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
