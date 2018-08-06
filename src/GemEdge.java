import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import heronarts.lx.model.LXPoint;

public class GemEdge extends LXAbstractFixtureMapped {

    public final int id;
    public float x;
    public float y;
    public float z;
    public float xn;
    public float yn;
    public float zn;
    protected GemEdgeDirection directionLoaded;
    protected GemEdgeDirection directionMapped;

    protected List<LXPoint> pointsReverseLoaded;

    public GemEdge(int id) {
        this.id = id;
    }

    public int getNumPoints() {
        return this.points.size();
    }

    protected void onLoadComplete(GemEdgeDirection loadedDirection, GemEdgeDirection mappedDirection) {
        this.directionLoaded = loadedDirection;

        // Create an array of the points in the other direction
        this.pointsReverseLoaded = new ArrayList<LXPoint>();
        for (int iPoint = this.points.size() - 1; iPoint >= 0; iPoint--) {
            this.pointsReverseLoaded.add(this.getPoint(iPoint));
        }

        this.directionMapped = mappedDirection;
    }

    public List<LXPoint> getPoints(GemEdgeDirection direction) {
        if (direction == this.directionLoaded) {
            return this.points;
        } else {
            // Note: currently, an invalid GemEdgeDirection will simply return
            // the reverseLoaded order.
            return this.pointsReverseLoaded;
        }
    }

    public LXPoint getPoint(int index, GemEdgeDirection direction) {
        if (direction == this.directionLoaded) {
            return this.points.get(index);
        } else {
            return this.pointsReverseLoaded.get(index);
        }
    }

    public GemEdgeDirection getDirectionAntiGravity() {
        if (this.directionLoaded == GemEdgeDirection.DOWNUP || this.directionLoaded == GemEdgeDirection.UPDOWN) {
            return GemEdgeDirection.DOWNUP;
        } else {
            return GemEdgeDirection.LEFTRIGHT;
        }
    }
    
    public GemEdgeDirection getDirectionRandom() {
        return (Math.random() < 0.5) ? this.getDirectionAntiGravity() : getOppositeDirection(this.getDirectionAntiGravity());
    }

    // Override this to return the points in order as they are on the physical strip
    // The key value indexes are relative to the start of the fixture. A parent fixture
    // can modify keys but must keep the values the same as they are globally unique.
    @Override
    public AbstractMap<Integer, LXPoint> getPointsMapped() {
        final TreeMap<Integer, LXPoint> mappedPoints = new TreeMap<Integer, LXPoint>();
        int i = 0;
        for (LXPoint point : this.getPoints(this.directionMapped)) {
            mappedPoints.put(i++, point);
        }

        return mappedPoints;
    }

    public void computeNormals() {
        // Initialize x,y,z for edge. These can be overwritten from the outside if desired.
        LXPoint midPoint = getPoint(this.points.size() / 2, this.directionLoaded);

        this.x = midPoint.x;
        this.y = midPoint.y;
        this.z = midPoint.z;
        this.xn = midPoint.xn;
        this.yn = midPoint.yn;
        this.zn = midPoint.zn;
    }

    public static GemEdgeDirection getOppositeDirection(GemEdgeDirection direction) {
        switch (direction) {
        case UPDOWN:
            return GemEdgeDirection.DOWNUP;
        case DOWNUP:
            return GemEdgeDirection.UPDOWN;
        case LEFTRIGHT:
            return GemEdgeDirection.RIGHTLEFT;
        case RIGHTLEFT:
            return GemEdgeDirection.LEFTRIGHT;
        default:
            break;
        }
        return direction;
    }

}
