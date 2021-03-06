import java.util.ArrayList;
import java.util.List;

import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.BooleanParameter.Mode;

public class HorizWipeBlend extends NormalizedScopeBlend {

    public final CompoundParameter tilt = new CompoundParameter("Tilt", 0.6, -.99, .99)
            .setDescription("Normalized tilt of the wipe");

    public final BooleanParameter isReverse = 
            new BooleanParameter("Reverse", true)
            .setDescription("When ENABLED, run effect in opposite direction")
            .setMode(Mode.TOGGLE);

    public HorizWipeBlend(LX lx) {
        super(lx);

        addParameter(tilt);
    }

    @Override
    public void setRandomParameters() {
        randomizeParameter(this.tilt);    
        randomizeParameter(this.isReverse);
        super.setRandomParameters();
    }

    @Override
    protected List<PointTransition> buildPointTransitions() {
        float tilt = this.tilt.getValuef();
        double fade = this.fade.getValue();
        boolean isReverse = this.isReverse.getValueb();

        ArrayList<PointTransition> transitions = new ArrayList<PointTransition>();
        for (NormalizedPoint p : this.scope.getPointsNormalized()) {
            double amt;
            if (tilt >= 0) {
                amt = 1 - ((p.xn + (p.yn * tilt)) / (1 + tilt));
            } else {
                amt = 1 - ((p.xn + ((1 - p.yn) * Math.abs(tilt))) / (1 + Math.abs(tilt)));
            }
            if (isReverse)
                amt = 1 - amt;
            PointTransition pt = buildFixedFadePointTransition(p.p, amt, fade);
            transitions.add(pt);
        }
        return transitions;
    }
    
}
