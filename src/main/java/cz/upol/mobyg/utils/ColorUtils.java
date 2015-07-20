package cz.upol.mobyg.utils;

import cz.upol.mobyg.color.ColorHSV;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Medel on 02/07/2015.
 */
public final class ColorUtils {
    private ColorUtils() {
    }

    public static List<ColorHSV> getPixelsHSV(Image imageRGB) {
        PixelReader pixelReader = imageRGB.getPixelReader();
        double imageHeight = imageRGB.getHeight();
        double imageWidth = imageRGB.getWidth();
        List<ColorHSV> result = new ArrayList<>();

        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {
                Color colorRGB = pixelReader.getColor(j, i);
                ColorHSV colorHSV = convertRGBtoHSV(colorRGB);
                result.add(colorHSV);
            }
        }
        return result;
    }

    // TODO udelat z List<ColorHSV> strukturu "imageHSV" s promennyma Data(list ColorHSV), width a height
    public static Image getPixelsRGB(List<ColorHSV> imageHSV) {
        WritableImage result = new WritableImage(640, 480);
        PixelWriter pixelWriter = result.getPixelWriter();

        for (int i = 0; i < 480; i++) {
            int x = i * 640;
            for (int j = 0; j < 640; j++) {
                ColorHSV colorHSV = imageHSV.get(x + j);
                Color colorRGB = convertHSVtoRGB(colorHSV);
                pixelWriter.setColor(j, i, colorRGB);
            }
        }

        return result;
    }

    public static ColorHSV convertRGBtoHSV(Color colorRGB) {
        int r = (int) (colorRGB.getRed() * 255);
        int g = (int) (colorRGB.getGreen() * 255);
        int b = (int) (colorRGB.getBlue() * 255);

        float[] hsvValues = java.awt.Color.RGBtoHSB(r, g, b, null);

        float h = hsvValues[0];
        float s = hsvValues[1];
        float v = hsvValues[2];

        return new ColorHSV(h, s, v);
    }

    public static Color convertHSVtoRGB(ColorHSV colorHSV) {
        double h = colorHSV.getHue();
        double s = colorHSV.getSaturation();
        double v = colorHSV.getValue();

        java.awt.Color hsv = java.awt.Color.getHSBColor((float) h, (float) s, (float) v);

        int r = hsv.getRed();
        int g = hsv.getGreen();
        int b = hsv.getBlue();

        return Color.rgb(r, g, b);
    }
}
