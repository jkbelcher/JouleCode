import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;

public class NormalizedPoint {

    public final LXPoint p;
    public final NormalScope scope;
    
    /**
     * Radius of this point from origin in 3 dimensions
     */
    public float r;

    /**
     * Radius of this point from origin in the x-y plane
     */
    public float rxy;

    /**
     * Radius of this point from origin in the x-z plane
     */
    public float rxz;

    /**
     * angle of this point about the origin in the x-y plane
     */
    public float theta;

    /**
     * Angle of this point about the origin in the x-z plane
     * (right-handed angle of rotation about the Y-axis)
     */
    public float azimuth;

    /**
     * Angle of this point between the y-value and the x-z plane
     */
    public float elevation;

    /**
     * normalized position of point in x-space (0-1);
     */
    public float xn = 0;

    /**
     * normalized position of point in y-space (0-1);
     */
    public float yn = 0;

    /**
     * normalized position of point in z-space (0-1);
     */
    public float zn = 0;

    /**
     * normalized position of point in radial space (0-1), 0 is origin, 1 is max radius
     */
    public float rn = 0;
    
    public NormalizedPoint(LXPoint p, NormalScope scope) {
        this.p = p;
        this.scope = scope;
        normalize();
    }
    
    protected void normalize() {
        this.xn = (this.scope.xRange == 0) ? .5f : (p.x - this.scope.xMin) / this.scope.xRange;
        this.yn = (this.scope.yRange == 0) ? .5f : (p.y - this.scope.yMin) / this.scope.yRange;
        this.zn = (this.scope.zRange == 0) ? .5f : (p.z - this.scope.zMin) / this.scope.zRange;
        this.rn = (this.scope.rRange == 0) ? 0f : this.r / this.scope.rRange;
        
        // Centered values, for calculating angles relative to a centered origin in this scope        
        float xnc = this.xn * 2f - 1f;
        float ync = this.yn * 2f - 1f;
        float znc = this.zn * 2f - 1f;

        this.r = (float) Math.sqrt(xnc * xnc + ync * ync + znc * znc);
        this.rxy = (float) Math.sqrt(xnc * xnc + ync * ync);
        this.rxz = (float) Math.sqrt(xnc * xnc + znc * znc);
        this.theta = (float) ((LX.TWO_PI + Math.atan2(ync, xnc)) % (LX.TWO_PI));
        this.azimuth = (float) ((LX.TWO_PI + Math.atan2(znc, xnc)) % (LX.TWO_PI));
        this.elevation = (float) ((LX.TWO_PI + Math.atan2(ync, rxz)) % (LX.TWO_PI));
    }

}
