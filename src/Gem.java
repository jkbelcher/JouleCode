import java.lang.Math;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import heronarts.lx.model.LXPoint;
import heronarts.lx.transform.LXTransform;
import processing.core.PApplet;

public abstract class Gem extends LXAbstractFixtureMapped implements INormalized {

    public class GemEdgeDirectionPair {
        GemEdgeDirection fromEdgeDirection;
        GemEdgeDirection toEdgeDirection;

        public GemEdgeDirectionPair(GemEdgeDirection fromEdgeDirection, GemEdgeDirection toEdgeDirection) {
            this.fromEdgeDirection = fromEdgeDirection;
            this.toEdgeDirection = toEdgeDirection;
        }
    }

    public final GemParameters params;
    public final GemType gemType;
    public final List<GemEdge> edges;
    public final AbstractMap<Integer, GemEdge> edgesByPosition;
    public BeagleboneController controller;

    public final List<GemEdge> gravityMappedEdges; // Represents full vertical edges, and one edges for the horizontals all the way around
    public final List<GemEdge> continuousEdges; // Represents full vertical edges, and one edges for the horizontals all the way around

    protected abstract LXTransform AdjustTransformForEdge(LXTransform transform, int edgePosition);

    public abstract GemEdgeDirectionPair getEdgeDirections(String gemType, int fromEdgeId, int toEdgeId) throws Exception;
        
    public Gem(GemParameters p, GemType gemType, LXTransform transform) throws Exception {
        this.params = p;
        this.gemType = gemType;
        this.edges = new ArrayList<GemEdge>();
        this.edgesByPosition = new TreeMap<Integer, GemEdge>();

        PApplet.println("     Gem", this.params.clusterName + "." + this.params.positionInCluster, "channel",
                this.params.controllerChannel);

        // Transform should arrive positioned for cluster.
        // Reposition transform to gem position (within cluster);
        transform.push();
        transform.translate(this.params.x, this.params.y, this.params.z);
        transform.rotateX(Math.toRadians(this.params.xTilt));
        transform.rotateY(Math.toRadians(this.params.yTilt));
        transform.rotateZ(Math.toRadians(this.params.zTilt));

        // 1. Create each edge and initialize points
        for (int iEdge = 0; iEdge < this.params.edgePixelCount.length; iEdge++) {

            int numPixels = this.params.edgePixelCount[iEdge];

            if (numPixels > 0) {

                // When labeling edges in our original mapping, we started with 1 instead of 0.
                int edgePosition = iEdge + 1;

                // Create edge object
                GemEdge gemEdge = new GemEdge(edgePosition);

                // Adjust transform to edge
                transform.push();
                transform = AdjustTransformForEdge(transform, edgePosition);

                // Add each pixel for this edge
                float pixelXpos = 1.25f; // starting position
                for (int pixel = 0; pixel < this.params.edgePixelCount[iEdge]; pixel++) {

                    transform.push();
                    transform.translate(pixelXpos, 0, 0);
                    pixelXpos += 1.25f; // distance between LEDs on the strip is 1.25 inches

                    LXPoint newPoint = new LXPoint(transform.x(), transform.y(), transform.z());
                    gemEdge.addPoint(newPoint);
                    this.addPoint(newPoint);

                    transform.pop();
                }

                transform.pop();

                this.edges.add(gemEdge);
                this.edgesByPosition.put(edgePosition, gemEdge);
            }
        }

        // Finished positioning within the gem
        transform.pop();

        // 2. Initialize edge directions, generate collections for opposite direction

        // Do first edge first
        int firstEdgeId = this.params.edgeOrder[0];
        int secondEdgeId = this.params.edgeOrder[1];
        GemEdgeDirection firstEdgeDirectionLoaded = getLoadedDirection(firstEdgeId);
        GemEdgeDirection firstEdgeDirectionMapped = getEdgeDirections(p.gemType, firstEdgeId, secondEdgeId).fromEdgeDirection;
        this.edgesByPosition.get(firstEdgeId).onLoadComplete(firstEdgeDirectionLoaded, firstEdgeDirectionMapped);

        // Do remaining edges by calculating from the preceding edge
        for (int i = 1; i < this.params.edgeOrder.length; i++) {
            int edgeIdFrom = this.params.edgeOrder[i - 1];
            int edgeIdTo = this.params.edgeOrder[i];
            GemEdgeDirection edgeDirectionLoaded = getLoadedDirection(edgeIdTo);
            GemEdgeDirection edgeDirectionMapped = getEdgeDirections(p.gemType, edgeIdFrom, edgeIdTo).toEdgeDirection;
            this.edgesByPosition.get(edgeIdTo).onLoadComplete(edgeDirectionLoaded, edgeDirectionMapped);
        }

        // 3. Create full gravity mapped edges. These exist as a convenience for patterns.
        this.gravityMappedEdges = new ArrayList<GemEdge>();

        for (GMSet set : this.GetGravityMappedSets()) {
            List<LXPoint> points = new ArrayList<LXPoint>();

            for (int i = 0; i < set.positions.length; i++) {
                int position = set.positions[i];

                // These are theoretical pairs. This edge position may not exist in this gem.
                if (this.edgesByPosition.containsKey(position)) {
                    GemEdge edge = this.edgesByPosition.get(position);
                    for (LXPoint edgePoint : edge.getPoints(set.direction)) {
                        points.add(edgePoint);
                    }
                }
            }

            // Did we find any points for this set? If so make a GravityMappedEdge;
            if (!points.isEmpty()) {
                GemEdge gme = new GemEdge(0);
                for (LXPoint gmePoint : points) {
                    gme.addPoint(gmePoint);
                }

                // Note: only the loaded direction will be used in GravityMappedEdges
                gme.onLoadComplete(set.direction, set.direction);

                this.gravityMappedEdges.add(gme);
            }
        }
        
        // 4. Create continuous edges, different from gravityMappedSets (different on the sign)
        // **TODO: add function for step 3 and 4
        this.continuousEdges = new ArrayList<GemEdge>();

        for (GMSet set : this.GetContinuousEdges()) {
            List<LXPoint> points = new ArrayList<LXPoint>();

            for (int i = 0; i < set.positions.length; i++) {
                int position = set.positions[i];

                // These are theoretical pairs. This edge position may not exist in this gem.
                if (this.edgesByPosition.containsKey(position)) {
                    GemEdge edge = this.edgesByPosition.get(position);
                    for (LXPoint edgePoint : edge.getPoints(set.direction)) {
                        points.add(edgePoint);
                    }
                }
            }

            // Did we find any points for this set? If so make a GravityMappedEdge;
            if (!points.isEmpty()) {
                GemEdge gme = new GemEdge(0);
                for (LXPoint gmePoint : points) {
                    gme.addPoint(gmePoint);
                }

                // Note: only the loaded direction will be used in GravityMappedEdges
                gme.onLoadComplete(set.direction, set.direction);

                this.continuousEdges.add(gme);
            }
        }
    }

    protected abstract List<GMSet> GetGravityMappedSets();
    
    protected abstract List<GMSet> GetContinuousEdges();
    
    protected abstract GemEdgeDirection getLoadedDirection(int edgePosition) throws Exception;

    public String getGemName() {
        return this.params.clusterName + "." + this.params.positionInCluster;
    }

    /*
     * //Original working method, before pulling points from edges
     * 
     * @Override
     * public AbstractMap<Integer, LXPoint> getPointsMapped() 
     * final TreeMap<Integer, LXPoint> mappedPoints = new TreeMap<Integer, LXPoint>();
     * int i=0;
     * for (int edge : this.params.edgeOrder) { 
     *      for (LXPoint edgePoint : this.PointsByEdge.get(edge-1)) {
     *          mappedPoints.put(i++, edgePoint);
     *      }
     * }     * 
     * return mappedPoints;
     * }
     */
    
    public void computeNormalsJoule() {
        for (GemEdge ge : this.edges) {
            ge.computeNormals();
        }
        for (GemEdge gme : this.gravityMappedEdges) {
            gme.computeNormals();
        }
        for (GemEdge gme : this.continuousEdges) {
            gme.computeNormals();
        }
        this.computeNormalized();
    }

    // Normalized
    
    NormalScope normalScope = null;
    
    protected final List<NormalizedPoint> normalizedPoints = new ArrayList<NormalizedPoint>();
    
    protected void computeNormalized() {
        this.normalScope = new NormalScope(this);        
        for (LXPoint p : this.getPoints()) {
            this.normalizedPoints.add(new NormalizedPoint(p, this.normalScope));
        }
    }
    
    public NormalScope getNormalScope() {
        return this.normalScope;
    }
    
    public List<NormalizedPoint> getPointsNormalized() {
        return this.normalizedPoints;
    }
    
    public boolean hasChildScopes() {
        return false;
    }       

    
    public class GMSet {
        public int[] positions;
        public GemEdgeDirection direction;

        public GMSet(int[] positions, GemEdgeDirection direction) {
            this.positions = positions;
            this.direction = direction;
        }
    }

}
