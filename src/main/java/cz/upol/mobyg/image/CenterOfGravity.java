package cz.upol.mobyg.image;

import cz.upol.mobyg.color.ColorHSV;
import javafx.scene.paint.Color;

/**
 * Created by Medel on 02/07/2015.
 */
public class CenterOfGravity {


    public Color[][] getCenterOfGravity(Color[][] rgbArray) {
        int height = rgbArray[0].length;
        int width = rgbArray.length;

        Color[][] result = new Color[width][height];

        int redAccumX = 0;
        int redAccumY = 0;
        int redAccumN = 0;

        int greenAccumX = 0;
        int greenAccumY = 0;
        int greenAccumN = 0;

        int blueAccumX = 0;
        int blueAccumY = 0;
        int blueAccumN = 0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

                if ((int) (rgbArray[j][i].getRed() * 255) == 255) {
                    redAccumX += j;
                    redAccumY += i;
                    redAccumN++;
                }
                if ((int) (rgbArray[j][i].getGreen() * 255) == 255) {
                    greenAccumX += j;
                    greenAccumY += i;
                    greenAccumN++;
                }
                if ((int) (rgbArray[j][i].getBlue() * 255) == 255) {
                    blueAccumX += j;
                    blueAccumY += i;
                    blueAccumN++;
                }
                result[j][i] = Color.rgb(0, 0, 0);
            }
        }
        if (redAccumN != 0)
            result[redAccumX / redAccumN][redAccumY / redAccumN] = Color.rgb(255, 0, 0);
        if (greenAccumN != 0)
            result[greenAccumX / greenAccumN][greenAccumY / greenAccumN] = Color.rgb(0, 255, 0);
        if (blueAccumN != 0)
            result[blueAccumX / blueAccumN][blueAccumY / blueAccumN] = Color.rgb(0, 0, 255);

        return result;
    }

    public Color[][] erosion(Color[][] rgbArray) {
        int height = rgbArray[0].length;
        int width = rgbArray.length;

        Color[][] result = new Color[width][height];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                result[j][i] = erodeOnePixel(rgbArray, j, i);
            }
        }

        return result;
    }

    private Color erodeOnePixel(Color[][] rgbArray, int x, int y) {

        double red = 255;
        double green = 255;
        double blue = 255;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int testX = x - 1 + i;
                int textY = y - 1 + j;

                if (isInArray(rgbArray, testX, textY)) {
                    red = Math.min(red, rgbArray[testX][textY].getRed() * 255);
                    green = Math.min(green, rgbArray[testX][textY].getGreen() * 255);
                    blue = Math.min(blue, rgbArray[testX][textY].getBlue() * 255);
                }
            }
        }
        return Color.rgb((int) red, (int) green, (int) blue);
    }

    private boolean isInArray(Color[][] rgbArray, int x, int y) {
        int height = rgbArray[0].length;
        int width = rgbArray.length;

        return (x >= 0) && (x < width) && (y >= 0) && (y < height);
    }

    public Color[][] filterPoints(ColorHSV[][] hsvArray, ColorHSV redHSV, ColorHSV greenHSV, ColorHSV blueHSV) {
        Color[][] result = new Color[hsvArray.length][hsvArray[0].length];
        //List<ColorHSV> result = new ArrayList<>();
        double h, s, v;
        int redHue = (int) (redHSV.getHue() * 360);
        int redSat = (int) (redHSV.getSaturation() * 360) - 40;
        int redVal = (int) (redHSV.getValue() * 360) - 40;

        int greenHue = (int) (greenHSV.getHue() * 360);
        int greenSat = (int) (greenHSV.getSaturation() * 360) - 40;
        int greenVal = (int) (greenHSV.getValue() * 360) - 40;

        int blueHue = (int) (blueHSV.getHue() * 360);
        int blueSat = (int) (blueHSV.getSaturation() * 360) - 40;
        int blueVal = (int) (blueHSV.getValue() * 360) - 40;

        for (int i = 0; i < hsvArray[0].length; i++) {
            for (int j = 0; j < hsvArray.length; j++) {
                ColorHSV hsv = hsvArray[j][i];

                h = hsv.getHue() * 360;
                s = hsv.getSaturation() * 360;
                v = hsv.getValue() * 360;

                if ((isColorInInterval(redHue, 15, h)) && (s > redSat) && (v > redVal))
                    result[j][i] = Color.rgb(255, 0, 0);
                else if ((isColorInInterval(greenHue, 15, h)) && (s > greenSat) && (v > greenVal))
                    result[j][i] = Color.rgb(0, 255, 0);
                else if ((isColorInInterval(blueHue, 15, h)) && (s > blueSat) && (v > blueVal))
                    result[j][i] = Color.rgb(0, 0, 255);
                else
                    result[j][i] = Color.rgb(0, 0, 0);
            }
        }
        return result;
    }

    private boolean isColorInInterval(int center, int range, double color) {
        double distance;

        double firstDistance = Math.abs(center - color);
        double secondDistance = 360 - firstDistance;

        distance = Math.min(firstDistance, secondDistance);

        return distance <= range;
    }
}
