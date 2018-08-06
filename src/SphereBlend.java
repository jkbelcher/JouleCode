import java.util.ArrayList;
import java.util.List;

import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.transform.LXVector;

public class SphereBlend extends FixedFadeBlend {

    public final CompoundParameter midOrigin = new CompoundParameter("midOrigin", 0.5, 0, 1)
            .setDescription("Percent of time that sphere center is in the middle.");

    public SphereBlend(LX lx) {
        super(lx);
    }
    
    float xn, yn, zn;

    protected List<PointTransition> buildPointTransitions() {
        //Randomize new center point of sphere
        calcNewSphereCenter();
        
        double fade = this.fade.getValue();
        
        // Calculate maximum radius of sphere
        float sphereRadius = 0;
        for (LXPoint p : this.model.getPoints()) {
            sphereRadius = Math.max(sphereRadius, distToCenter(p));
        }

        //Calculate transitions
        ArrayList<PointTransition> transitions = new ArrayList<PointTransition>();
        for (LXPoint p : this.model.getPoints()) {
            double amt = 1 - ((sphereRadius - distToCenter(p)) / sphereRadius);
            PointTransition pt = buildFixedFadePointTransition(p, amt, fade);
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
    
    protected float distToCenter(LXPoint point) {
        float dx = this.xn - point.xn;
        float dy = this.yn - point.yn;
        float dz = this.zn - point.zn;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    protected float dist(LXVector vector, LXPoint point) {
        float dx = vector.x - point.x;
        float dy = vector.y - point.y;
        float dz = vector.z - point.z;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
