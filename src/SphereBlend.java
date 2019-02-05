import java.util.ArrayList;
import java.util.List;

import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.BooleanParameter.Mode;
import heronarts.lx.transform.LXVector;

public class SphereBlend extends NormalizedScopeBlend {

    public final CompoundParameter midOrigin = new CompoundParameter("midOrigin", 0.4, 0, 1)
            .setDescription("Percent of time that sphere center is in the middle.");

    public final BooleanParameter isReverse = 
            new BooleanParameter("Reverse", true)
            .setDescription("When ENABLED, run effect in opposite direction")
            .setMode(Mode.TOGGLE);
    
    public SphereBlend(LX lx) {
        super(lx);
    }
    
    float xn, yn, zn;
    
    @Override
    public void setRandomParameters() {
        //Randomize new center point of sphere
        calcNewSphereCenter();
        randomizeParameter(this.isReverse);
        super.setRandomParameters();
    }
    
    protected List<PointTransition> buildPointTransitions() {
        double fade = this.fade.getValue();
        boolean isReverse = this.isReverse.getValueb();
        
        // Calculate maximum radius of sphere
        float sphereRadius = 0;
        for (NormalizedPoint p : this.scope.getPointsNormalized()) {
            sphereRadius = Math.max(sphereRadius, distToCenter(p));
        }

        //Calculate transitions
        ArrayList<PointTransition> transitions = new ArrayList<PointTransition>();
        for (NormalizedPoint p : this.scope.getPointsNormalized()) {
            double amt = 1 - ((sphereRadius - distToCenter(p)) / sphereRadius);
            if (isReverse)
                amt = 1 - amt;
            PointTransition pt = buildFixedFadePointTransition(p.p, amt, fade);
            transitions.add(pt);
        }
        return transitions;
    }
    
    protected void calcNewSphereCenter() {
        //Use the origin some percentage of the time
        if (Math.random() < this.midOrigin.getValue()) {
            this.xn = this.yn = this.zn = 0.5f;
        } else {
            this.xn = (float)Math.random();
            this.yn = (float)Math.random();
            this.zn = (float)Math.random();
        }
    }
    
    protected float distToCenter(NormalizedPoint point) {
        float dx = this.xn - point.xn;
        float dy = this.yn - point.yn;
        float dz = this.zn - point.zn;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /*
    protected float dist(LXVector vector, LXPoint point) {
        float dx = vector.x - point.x;
        float dy = vector.y - point.y;
        float dz = vector.z - point.z;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    */
}
