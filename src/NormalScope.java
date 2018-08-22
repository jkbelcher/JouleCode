import java.util.List;

import heronarts.lx.model.LXAbstractFixture;
import heronarts.lx.model.LXFixture;
import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;

public class NormalScope {

    /**
     * Center of the model in x space
     */
    public float cx;

    /**
     * Center of the model in y space
     */
    public float cy;

    /**
     * Center of the model in z space
     */
    public float cz;

    /**
     * Average x point
     */
    public float ax;

    /**
     * Average y point
     */
    public float ay;

    /**
     * Average z points
     */
    public float az;

    /**
     * Minimum x value
     */
    public float xMin;

    /**
     * Maximum x value
     */
    public float xMax;

    /**
     * Range of x values
     */
    public float xRange;

    /**
     * Minimum y value
     */
    public float yMin;

    /**
     * Maximum y value
     */
    public float yMax;

    /**
     * Range of y values
     */
    public float yRange;

    /**
     * Minimum z value
     */
    public float zMin;

    /**
     * Maximum z value
     */
    public float zMax;

    /**
     * Range of z values
     */
    public float zRange;

    /**
     * Smallest radius from origin
     */
    public float rMin;

    /**
     * Greatest radius from origin
     */
    public float rMax;

    /**
     * Range of radial values
     */
    public float rRange;

    public NormalScope(LXFixture target) {
        average(target);
    }
    
    /**
     * Recompute the averages
     * 
     * This method comes directly from LXModel.java in the LX framework.
     * *Possible framework change to compute averages relative to any fixture
     * or set of fixtures?
     *
     * @return this
     */
    public NormalScope average(LXFixture target) {
      float ax = 0, ay = 0, az = 0;
      float xMin = 0, xMax = 0, yMin = 0, yMax = 0, zMin = 0, zMax = 0, rMin = 0, rMax = 0;

      List<LXPoint> points = target.getPoints();
      
      boolean firstPoint = true;
      for (LXPoint p : points) {
        ax += p.x;
        ay += p.y;
        az += p.z;
        if (firstPoint) {
          xMin = xMax = p.x;
          yMin = yMax = p.y;
          zMin = zMax = p.z;
          rMin = rMax = p.r;
        } else {
          if (p.x < xMin)
            xMin = p.x;
          if (p.x > xMax)
            xMax = p.x;
          if (p.y < yMin)
            yMin = p.y;
          if (p.y > yMax)
            yMax = p.y;
          if (p.z < zMin)
            zMin = p.z;
          if (p.z > zMax)
            zMax = p.z;
          if (p.r < rMin)
            rMin = p.r;
          if (p.r > rMax)
            rMax = p.r;
        }
        firstPoint = false;
      }
      this.ax = ax / Math.max(1, points.size());
      this.ay = ay / Math.max(1, points.size());
      this.az = az / Math.max(1, points.size());
      this.xMin = xMin;
      this.xMax = xMax;
      this.xRange = xMax - xMin;
      this.yMin = yMin;
      this.yMax = yMax;
      this.yRange = yMax - yMin;
      this.zMin = zMin;
      this.zMax = zMax;
      this.zRange = zMax - zMin;
      this.rMin = rMin;
      this.rMax = rMax;
      this.rRange = rMax - rMin;
      this.cx = xMin + xRange / 2.f;
      this.cy = yMin + yRange / 2.f;
      this.cz = zMin + zRange / 2.f;

      return this;
    }


}
