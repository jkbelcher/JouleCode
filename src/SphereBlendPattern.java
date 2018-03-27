import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.transform.LXVector;

public class SphereBlendPattern extends JouleBlendPattern {

    float sphereRadius = 0;

    public SphereBlendPattern(LX lx) {
        super(lx);
        initialize();
    }

    public void initialize() {
        // Calculate maximum radius of sphere
        // Only do this once
        sphereRadius = 0;
        for (LXPoint p : this.model.getPoints()) {
            sphereRadius = Math.max(sphereRadius, distToOrigin(p));
        }
    }

    protected float dist(LXVector vector, LXPoint point) {
        float dx = vector.x - point.x;
        float dy = vector.y - point.y;
        float dz = vector.z - point.z;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    protected float distToOrigin(LXPoint point) {
        float dx = 0.5f - point.xn;
        float dy = 0.5f - point.yn;
        float dz = 0.5f - point.zn;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    protected void run(double deltaMs) {
        // Use currentMs instead of runMs!!

        for (LXPoint p : this.model.getPoints()) {
            int color = isPointOldFrame(p) ? LXColor.BLACK : LXColor.WHITE;
            colors[p.index] = color;
        }
    }

    Boolean isPointOldFrame(LXPoint p) {
        float distToOrigin = this.distToOrigin(p);
        return (this.sphereRadius - distToOrigin) / this.sphereRadius < this.percentRemaining;
    }

}
