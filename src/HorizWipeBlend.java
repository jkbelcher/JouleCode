import java.util.ArrayList;
import java.util.List;

import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;

public class HorizWipeBlend extends FixedFadeBlend {

    public final CompoundParameter tilt = new CompoundParameter("Tilt", 0.6, -.99, .99)
            .setDescription("Normalized tilt of the wipe");

    public HorizWipeBlend(LX lx) {
        super(lx);

        addParameter(tilt);
    }

    @Override
    public void onActive() {
        randomizeParameter(this.tilt);
        
        super.onActive();
    }

    protected List<PointTransition> buildPointTransitions() {
        float tilt = this.tilt.getValuef();
        double fade = this.fade.getValue();

        ArrayList<PointTransition> transitions = new ArrayList<PointTransition>();
        for (LXPoint p : this.model.getPoints()) {
            double amt;
            if (tilt >= 0) {
                amt = 1 - ((p.xn + (p.yn * tilt)) / (1 + tilt));
            } else {
                amt = 1 - ((p.xn + ((1 - p.yn) * Math.abs(tilt))) / (1 + Math.abs(tilt)));
            }
            PointTransition pt = buildFixedFadePointTransition(p, amt, fade);
            transitions.add(pt);
        }
        return transitions;
    }
    
}
