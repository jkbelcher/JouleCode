import java.util.ArrayList;
import java.util.List;

import heronarts.lx.LX;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.BooleanParameter.Mode;
import heronarts.lx.parameter.BoundedParameter.Range;

public class ClockBlend extends NormalizedScopeBlend {

    public final CompoundParameter offSet = new CompoundParameter("Offset", 0, 0, 360)
            .setDescription("Number of rotational degrees to offset from zero");

    public final BooleanParameter isReverse = 
            new BooleanParameter("Reverse", true)
            .setDescription("When ENABLED, run effect in opposite direction")
            .setMode(Mode.TOGGLE);
    
    protected final Range rRange = new Range(0,360);

    public ClockBlend(LX lx) {
        super(lx);
    }
    
    @Override
    public void setRandomParameters() {
        randomizeParameter(this.offSet);
        randomizeParameter(this.isReverse);
        super.setRandomParameters();
    }

    @Override
    protected List<PointTransition> buildPointTransitions() {
        double fade = this.fade.getValue();
        double offSet = this.offSet.getValue();
        boolean isReverse = this.isReverse.getValueb();
        
        ArrayList<PointTransition> transitions = new ArrayList<PointTransition>();
        for (NormalizedPoint p : this.scope.getPointsNormalized()) {
            double amt = Math.toDegrees(p.theta);
            amt += offSet;
            while (amt >= 360.0)
                amt -= 360.0;
            amt = rRange.getNormalized(amt);
            if (isReverse)
                amt = 1 - amt;
                        
            PointTransition pt = buildFixedFadePointTransition(p.p, amt, fade);
            transitions.add(pt);
        }
        return transitions;
    }
    
}
