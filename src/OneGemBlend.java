import java.util.ArrayList;
import java.util.List;

import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;

public class OneGemBlend extends PrecalculatedBlend {

    public final CompoundParameter gemLen = 
            new CompoundParameter("GemLen", .15, .01, 1)
            .setDescription("Percent of time spent fading each gem.");

    public OneGemBlend(LX lx) {
        super(lx);

        addParameter(gemLen);
    }

    @Override
    public void onActive() {
        randomizeParameter(this.gemLen);
        
        super.onActive();
    }

    @Override
    protected List<PointTransition> buildPointTransitions() {
        ArrayList<PointTransition> transitions = new ArrayList<PointTransition>();

        double lenAmt = this.gemLen.getValue();

        for (Gem gem : this.model.gems) {
            double startAmt = (float) (Math.random() * (1 - lenAmt));
            double endAmt = startAmt + lenAmt;
            for (LXPoint p : gem.getPoints()) {
                PointTransition pt = new PointTransition(p, startAmt, lenAmt, endAmt);
                transitions.add(pt);
            }
        }
        
        return transitions;
    }
}
