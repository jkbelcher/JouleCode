import java.util.ArrayList;
import java.util.List;

import heronarts.lx.LX;

public class EdgeWipeBlend extends FixedFadeBlend {

    public EdgeWipeBlend(LX lx) {
        super(lx);
    }

    @Override
    protected List<PointTransition> buildPointTransitions() {
        double fade = this.fade.getValue();

        ArrayList<PointTransition> transitions = new ArrayList<PointTransition>();

        for (Gem gem : this.model.gems) {
            for (GemEdge edge : gem.edges) {
                GemEdgeDirection edgeDirection = edge.getDirectionRandom();
                double percentPerPixel = 1 / ((double)edge.getNumPoints());
                for (int i = 0; i < edge.getNumPoints(); i++) {
                    double amtEnd = percentPerPixel * (i+1);
                    PointTransition pt = buildFixedFadePointTransition(edge.getPoint(i, edgeDirection), amtEnd, fade);
                    transitions.add(pt);
                }
            }
        }
        return transitions;
    }
}
