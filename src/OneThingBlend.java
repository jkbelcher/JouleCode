import java.util.ArrayList;
import java.util.List;

import heronarts.lx.LX;
import heronarts.lx.parameter.BoundedParameter.Range;

public class OneThingBlend extends NormalizedScopeBlend {

    private final List<NormalizedScopeBlend> blends = new ArrayList<NormalizedScopeBlend>();

    public OneThingBlend(LX lx) {
        super(lx);
        
        this.addBlends(new NormalizedScopeBlend[]{
                //new HorizWipeBlend(lx),
                //new SphereBlend(lx),
                //new AddNormalizedBlend(lx),
                new ClockBlend(lx)
                });
    }
    
    public OneThingBlend addBlends(NormalizedScopeBlend[] newBlends) {
        for (NormalizedScopeBlend blend : newBlends) {
            this.blends.add(blend);
        }
        return this;
    }
    
    boolean hasRecurse = false;
    NormalizedScopeBlend childBlend = null;
    
    @Override
    public void setRandomParameters() {
        initializeOneThingBlend();        
        super.setRandomParameters();
    }
    
    private void initializeOneThingBlend() {
        if (childBlend != null) {
            childBlend.onInactive();            
        }
        // Child blend is one of a set of blends
        childBlend = blends.get((int) (Math.random() * (float) blends.size()));
        childBlend.onActive();            
    }

    @Override
    protected List<PointTransition> buildPointTransitions() {
        ArrayList<PointTransition> transitions = new ArrayList<PointTransition>();

        double fade = this.fade.getValue();
        
        List<INormalizedScope> things = INormalizedScope.NormalizedScopeUtils.getChildScopeRandom(this.model);

        for (INormalizedScope thing : things) {
            // Space each thing randomly through the transition
            double startAmt = (float) (Math.random() * (1 - fade));
            double endAmt = startAmt + fade;
            
            // Calculate the blend on the thing
            childBlend.setScope(thing);
            List<PointTransition> subTransitions = childBlend.buildPointTransitions();

            // Adjust the blend timing to fit within the scope
            // Because blend timing is normalized, we can use a range to covert out of it
            Range r = new Range(startAmt, endAmt);
            for (PointTransition pt : subTransitions) {
                pt.startAmt = r.normalizedToValue(pt.startAmt);
                pt.endAmt = r.normalizedToValue(pt.endAmt);
                pt.lenAmt = pt.endAmt - pt.startAmt;
                transitions.add(pt);
            }
        }
        
        return transitions;
    }

}
