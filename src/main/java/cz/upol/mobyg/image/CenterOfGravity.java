package cz.upol.mobyg.image;

import cz.upol.mobyg.color.ColorHSV;

import java.util.List;

/**
 * Created by Medel on 02/07/2015.
 */
public class CenterOfGravity {


    public List<ColorHSV> filterPoints(List<ColorHSV> hsvArray, ColorHSV redHSV, ColorHSV greenHSV, ColorHSV blueHSV) {
        //List<ColorHSV> result = new ArrayList<>();
        double h, s, v;
        int redHue = (int) (redHSV.getHue() * 360);
        int redSat = (int) (redHSV.getSaturation() * 360) - 15;
        int redVal = (int) (redHSV.getValue() * 360) - 15;

        int greenHue = (int) (greenHSV.getHue() * 360);
        int greenSat = (int) (greenHSV.getSaturation() * 360) - 40;
        int greenVal = (int) (greenHSV.getValue() * 360) - 40;

        int blueHue = (int) (blueHSV.getHue() * 360);
        int blueSat = (int) (blueHSV.getSaturation() * 360) - 40;
        int blueVal = (int) (blueHSV.getValue() * 360) - 40;

        for (ColorHSV hsv : hsvArray) {
            h = hsv.getHue() * 360;
            s = hsv.getSaturation() * 360;
            v = hsv.getValue() * 360;

            if ((isColorInInterval(redHue, 15, h)) && (s > redSat) && (v > redVal))
                hsv.setAll(0, 1, 1);
            else if ((isColorInInterval(greenHue, 15, h)) && (s > greenSat) && (v > greenVal))
                hsv.setAll((120 / 360.0), 1, 1);
            else if  ((isColorInInterval(blueHue, 15, h)) && (s > blueSat) && (v > blueVal))
                hsv.setAll((240 / 360.0), 1, 1);
            else
                hsv.setAll(0, 0, 0);
        }

        return hsvArray;
    }

    private boolean isColorInInterval(int center, int range, double color) {
        double distance;

        double firstDistance = Math.abs(center - color);
        double secondDistance = 360 - firstDistance;

        distance = Math.min(firstDistance, secondDistance);

        return distance <= range;
    }

}
