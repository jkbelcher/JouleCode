import java.util.ArrayList;
import java.util.List;

import heronarts.lx.LX;

public class AddNormalizedBlend extends NormalizedScopeBlend {

    public AddNormalizedBlend(LX lx) {
        super(lx);
    }

    protected List<PointTransition> buildPointTransitions() {
        ArrayList<PointTransition> transitions = new ArrayList<PointTransition>();
        for (NormalizedPoint p : this.scope.getPointsNormalized()) {
            PointTransition pt = new PointTransition(p.p, 0, 1, 1);
            transitions.add(pt);
        }
        return transitions;
    }

}
