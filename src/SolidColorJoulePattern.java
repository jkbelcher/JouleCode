import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;

@LXCategory(LXCategory.TEST)
public class SolidColorJoulePattern extends JoulePattern {

    public final CompoundParameter hue = 
            new CompoundParameter("Hue", LXColor.h(LXColor.RED), 0, 360)
            .setDescription("Hue");
    public final CompoundParameter brightness = 
            new CompoundParameter("Brightness", 100, 0, 100)
            .setDescription("Brightness");

    public SolidColorJoulePattern(LX lx) {
        super(lx);

        addParameter(hue);
        addParameter(brightness);
    }

    @Override
    public void setRandomParameters() {
        randomizeParameter(this.hue);
        randomizeParameter(this.brightness);
    }
    
    @Override
    protected void run(double deltaMs) {

        this.clearColors();

        float hue = this.hue.getValuef();
        float brightness = this.brightness.getValuef();

        int color = LXColor.hsb(hue, 100, brightness);

        for (LXPoint p : this.model.points) {
            colors[p.index] = color;
        }
    }

}
