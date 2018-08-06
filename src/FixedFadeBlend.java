import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;

public abstract class FixedFadeBlend extends PrecalculatedBlend {

    public final CompoundParameter fade = new CompoundParameter("Fade", 0.1, 0, .2)
            .setDescription("Width of the transition's cross-fade");

    public FixedFadeBlend(LX lx) {
        super(lx);

        addParameter(fade);
    }

    @Override
    public void onActive() {
        randomizeParameter(this.fade);
        
        super.onActive();
    }

    protected PointTransition buildFixedFadePointTransition(LXPoint p, double amt, double lenAmt) {
        double endAmt = ((1.-lenAmt) * amt) + lenAmt;
        return new PointTransition(p, endAmt - lenAmt, lenAmt, endAmt);
    }
}
