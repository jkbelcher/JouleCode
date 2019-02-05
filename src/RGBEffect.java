import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXEffect;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.CompoundParameter;

@LXCategory(LXCategory.COLOR)
public class RGBEffect extends LXEffect {

    private final CompoundParameter rValue =
            new CompoundParameter("R", 1, 0, 1)
            .setDescription("R Value");

    private final CompoundParameter gValue =
            new CompoundParameter("G", 1, 0, 1)
            .setDescription("G Value");

    private final CompoundParameter bValue =
            new CompoundParameter("B", 1, 0, 1)
            .setDescription("B Value");

    public RGBEffect(LX lx) {
        super(lx);
        
        addParameter(rValue);
        addParameter(gValue);
        addParameter(bValue);
    }

    @Override
    protected void run(double deltaMs, double amount) {
        double rValue = this.rValue.getValue();  
        double gValue = this.gValue.getValue();  
        double bValue = this.bValue.getValue();  
        for (int i = 0; i < colors.length; ++i) {
            this.colors[i] = LXColor.rgb(
            (int)(((double)LXColor.red(this.colors[i]))*rValue),
            (int)(((double)LXColor.green(this.colors[i]))*gValue),
            (int)(((double)LXColor.blue(this.colors[i]))*bValue)
          );
}
    }

}
