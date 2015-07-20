package cz.upol.mobyg.color;

/**
 * Created by Medel on 02/07/2015.
 */
public class ColorHSV {
    private double hue;
    private double saturation;
    private double value;

    public ColorHSV(double hue, double saturation, double value) {
        this.hue = hue;
        this.saturation = saturation;
        this.value = value;
    }

    public void setAll(double hue, double saturation, double value) {
        this.hue = hue;
        this.saturation = saturation;
        this.value = value;
    }

    public double getHue() {
        return hue;
    }

    public void setHue(double hue) {
        this.hue = hue;
    }

    public double getSaturation() {
        return saturation;
    }

    public void setSaturation(double saturation) {
        this.saturation = saturation;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
