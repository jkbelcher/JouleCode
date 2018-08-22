import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.CompoundParameter;

@LXCategory(LXCategory.OTHER)
public class VertRainbowShiftPattern extends JoulePattern {

    public final CompoundParameter hueRange = 
            new CompoundParameter("hueRange", 128, 5, 400).setDescription("hueRange");
    
    public final CompoundParameter speed = 
            new CompoundParameter("Speed", .14, .03, 0.5)
            .setDescription("Speed in full range shifts per second");

    float huePos = 0;

    public VertRainbowShiftPattern(LX lx) {
        super(lx);

        addParameter(hueRange);
        addParameter(speed);
    }

    @Override
    public void setRandomParameters() {
        randomizeParameter(this.hueRange);
        randomizeParameter(this.speed, this.speed.range.min, 0.25);
    }
    
    @Override
    protected void run(double deltaMs) {

        float hueRange = this.hueRange.getValuef();

        // Calc current position
        float speed = this.speed.getValuef();
        float degreesMoved = hueRange * speed * (((float) deltaMs) / 1000f);

        huePos += degreesMoved;
        huePos %= 360;

        for (Gem gem : this.model.gems) {
            for (NormalizedPoint np : gem.getPointsNormalized()) {
                float hue = ((np.yn * hueRange) - huePos) % 360;
                colors[np.p.index] = LXColor.hsb(hue, 100, 100);                
            }
        }
    }

}
