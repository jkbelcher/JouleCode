import java.lang.Math;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import heronarts.lx.model.LXPoint;
import heronarts.lx.transform.LXTransform;
import processing.core.PApplet;


public class Gem extends LXAbstractFixtureMapped {

	public class GemEdgeDirectionPair {
		GemEdgeDirection fromEdgeDirection;
		GemEdgeDirection toEdgeDirection;
		
		public GemEdgeDirectionPair (GemEdgeDirection fromEdgeDirection, GemEdgeDirection toEdgeDirection) {
			this.fromEdgeDirection = fromEdgeDirection;
			this.toEdgeDirection = toEdgeDirection;
		}
	}
	
	public final GemParameters params;
	public final GemType gemType;
	public final List<GemEdge> edges;
	public final AbstractMap<Integer, GemEdge> edgesByPosition;
	public BeagleboneController controller;
	
	public final List<GemEdge> gravityMappedEdges;	//Represents full vertical edges, and one edges for the horizontals all the way around
	
	protected LXTransform AdjustTransformForEdge(LXTransform transform, int edgePosition) {
		// Rotate transform so the edge can be drawn on X-axis
		// Need to use GemType and GemSizes (loaded from file) to rotate to correct positions.
		// **Probably need an offset column for each side to offset any blank space to start of LED strip.
		float t = this.gemType.topSquare();
		float a = (float)Math.toDegrees(Math.asin(0.5*(this.gemType.topSquare()-this.gemType.bottomSquare())/(this.gemType.heightSquares())));
		
		switch (edgePosition) {
			case 1:
				transform.rotateY(Math.toRadians(-45));
				transform.rotateZ(Math.toRadians(-90+a));
				break;
			case 2:
				transform.translate(t, 0, 0);
				transform.rotateY(Math.toRadians(-135));
				transform.rotateZ(Math.toRadians(-90+a));
				break;
			case 3:
				transform.translate(t, 0, t);
				transform.rotateY(Math.toRadians(135));
				transform.rotateZ(Math.toRadians(-90+a));
				break;
			case 4:
				transform.translate(0, 0, t);
				transform.rotateY(Math.toRadians(45));
				transform.rotateZ(Math.toRadians(-90+a));
				break;				
			case 5:
				//Already aligned on X-axis
				break;
			case 6:
				transform.translate(t, 0, 0);
				transform.rotateY(Math.toRadians(-90));
				break;
			case 7:
				transform.translate(t, 0, t);
				transform.rotateY(Math.toRadians(180));
				break;
			case 8:
				transform.translate(0, 0, t);
				transform.rotateY(Math.toRadians(90));
				break;				
			case 9:				
				transform.rotateY(Math.toRadians(-45));
				transform.rotateZ(Math.toRadians(45));
				break;				
			case 10: 
				transform.translate(t, 0, 0);
				transform.rotateY(Math.toRadians(-90-45));
				transform.rotateZ(Math.toRadians(45));				
				break;
			case 11:
				transform.translate(t, 0, t);
				transform.rotateY(Math.toRadians(180-45));
				transform.rotateZ(Math.toRadians(45));
				break;
			case 12:
				transform.translate(0, 0, t);
				transform.rotateY(Math.toRadians(90-45));
				transform.rotateZ(Math.toRadians(45));
				break;
			case 13:
				break;
			case 14:
				break;
			case 15:
				break;
			case 16:
				break;
			default:
				break;
		}
		return transform;
	}
	
	public GemEdgeDirectionPair getEdgeDirections(int fromEdgeId, int toEdgeId) throws Exception {
		// *Some gems have jumpered edges and will transition between two non-touching edges.
		// We will have to add the map for these when we find them.
		// If there are two conflicting map directions in a jumper, we will have to add an
		// optional override in the config file.
		switch (fromEdgeId) {
		case 1:
			switch (toEdgeId) {
            case 2: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.DOWNUP); 
            case 4: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.DOWNUP); 
            case 5: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.LEFTRIGHT); 
			case 9: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.DOWNUP); 
			case 8: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.RIGHTLEFT); 
			case 13: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.LEFTRIGHT); 
			case 16: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.RIGHTLEFT);
			}
		case 2:
			switch (toEdgeId) {
            case 1: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.DOWNUP); 
            case 3: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.DOWNUP); 
			case 6: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.LEFTRIGHT); 
			case 10: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.DOWNUP); 
			case 5: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.RIGHTLEFT); 
			case 14: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.LEFTRIGHT); 
			case 13: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.RIGHTLEFT);			
			}
		case 3:
			switch (toEdgeId) {
            case 2: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.DOWNUP); 
            case 4: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.DOWNUP); 
			case 7: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.LEFTRIGHT); 
			case 11: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.DOWNUP); 
			case 6: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.RIGHTLEFT); 
			case 15: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.LEFTRIGHT); 
			case 14: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.RIGHTLEFT);			
			}
		case 4:
			switch (toEdgeId) {
            case 1: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.DOWNUP); 
            case 3: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.DOWNUP); 
            case 5: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.LEFTRIGHT); 
            case 8: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.LEFTRIGHT); 
			case 12: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.DOWNUP); 
			case 7: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.RIGHTLEFT); 
			case 16: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.LEFTRIGHT); 
			case 15: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.RIGHTLEFT);			
			}			
		case 5:
			switch (toEdgeId) {
			case 2: return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.UPDOWN);
			case 6: return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.LEFTRIGHT);
			case 10: return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.DOWNUP);
			case 9: return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.DOWNUP);
			case 8: return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.RIGHTLEFT);
			case 1: return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.UPDOWN);
			case 7: return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.RIGHTLEFT);
			}
		case 6:
			switch (toEdgeId) {
			case 3: return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.UPDOWN);
			case 7: return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.LEFTRIGHT);
			case 11: return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.DOWNUP);
			case 10: return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.DOWNUP);
			case 5: return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.RIGHTLEFT);
			case 2: return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.UPDOWN);
			}
		case 7:
			switch (toEdgeId) {
			case 4: return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.UPDOWN);
			case 8: return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.LEFTRIGHT);
			case 12: return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.DOWNUP);
			case 11: return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.DOWNUP);
			case 6: return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.RIGHTLEFT);
			case 3: return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.UPDOWN);
			}
		case 8:
			switch (toEdgeId) {
			case 1: return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.UPDOWN);
			case 5: return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.LEFTRIGHT);
			case 9: return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.DOWNUP);
			case 12: return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.DOWNUP);
			case 7: return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.RIGHTLEFT);
			case 4: return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.UPDOWN);
			}			
		case 9:
			switch (toEdgeId) {
			case 10: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.UPDOWN);
			case 11: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.UPDOWN);
			case 12: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.UPDOWN);
			case 8: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.RIGHTLEFT);
			case 1: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.UPDOWN);
			case 5: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.LEFTRIGHT);				
			}
		case 10:
			switch (toEdgeId) {
			case 11: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.UPDOWN);
			case 12: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.UPDOWN);
			case 9: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.UPDOWN);
			case 5: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.RIGHTLEFT);
			case 2: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.UPDOWN);
			case 6: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.LEFTRIGHT);				
			}
		case 11:
			switch (toEdgeId) {
			case 12: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.UPDOWN);
			case 9: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.UPDOWN);
			case 10: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.UPDOWN);
			case 6: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.RIGHTLEFT);
			case 3: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.UPDOWN);
			case 7: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.LEFTRIGHT);				
			}
		case 12:
			switch (toEdgeId) {
            case 1: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.UPDOWN);  //Charlie 1
            case 9: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.UPDOWN);
			case 10: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.UPDOWN);
			case 11: return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.UPDOWN);
			case 7: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.RIGHTLEFT);
			case 4: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.UPDOWN);
			case 8: return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.LEFTRIGHT);				
			}			
		case 13:
			switch (toEdgeId) {
			case 14: return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.LEFTRIGHT);
			case 2: return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.DOWNUP);
			case 1: return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.DOWNUP);
			case 16: return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.RIGHTLEFT);
			}
		case 14:
			switch (toEdgeId) {
			case 15: return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.LEFTRIGHT);
			case 3: return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.DOWNUP);
			case 2: return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.DOWNUP);
			case 13: return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.RIGHTLEFT);
			}
		case 15:
			switch (toEdgeId) {
			case 16: return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.LEFTRIGHT);
			case 4: return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.DOWNUP);
			case 3: return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.DOWNUP);
			case 14: return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.RIGHTLEFT);
			}
		case 16:
			switch (toEdgeId) {
			case 13: return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.LEFTRIGHT);
			case 1: return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.DOWNUP);
			case 4: return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.DOWNUP);
			case 15: return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.RIGHTLEFT);
			}
		}
		throw new Exception("Unknown gem edge order "+fromEdgeId+"->"+toEdgeId+" for gem "+this.getGemName() + "  Please add edge order in Gem.getEdgeDirections()");
	}
	
	public Gem(GemParameters p, GemType gemType, LXTransform transform) throws Exception {
		this.params = p;
		this.gemType = gemType;
		this.edges = new ArrayList<GemEdge>();
		this.edgesByPosition = new TreeMap<Integer, GemEdge>();
		
		PApplet.println("     Gem",this.params.clusterName+"."+this.params.positionInCluster,"channel",this.params.controllerChannel);
		
		// Transform should arrive positioned for cluster.
		// Reposition transform to gem position (within cluster);
		transform.push();
		transform.translate(this.params.x, this.params.y, this.params.z);
		transform.rotateX(Math.toRadians(this.params.xTilt));
		transform.rotateY(Math.toRadians(this.params.yTilt));
		transform.rotateZ(Math.toRadians(this.params.zTilt));
		
		//1. Create each edge and initialize points
		for (int iEdge=0; iEdge<this.params.edgePixelCount.length; iEdge++) {
			
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
				float pixelXpos = 1.25f;	// starting position
				for (int pixel=0; pixel<this.params.edgePixelCount[iEdge]; pixel++) {

					transform.push();
					transform.translate(pixelXpos, 0, 0);
					pixelXpos += 1.25f;		// distance between LEDs on the strip is 1.25 inches
					
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
		
		//2. Initialize edge directions, generate collections for opposite direction
		
		// Do first edge first
		int firstEdgeId = this.params.edgeOrder[0];
		int secondEdgeId = this.params.edgeOrder[1];
		GemEdgeDirection firstEdgeDirectionLoaded = getLoadedDirection(firstEdgeId);
		GemEdgeDirection firstEdgeDirectionMapped = getEdgeDirections(firstEdgeId, secondEdgeId).fromEdgeDirection;
		this.edgesByPosition.get(firstEdgeId).onLoadComplete(firstEdgeDirectionLoaded, firstEdgeDirectionMapped);
		
		// Do remaining edges by calculating from the preceding edge
		for (int i = 1; i<this.params.edgeOrder.length; i++) {
			int edgeIdFrom = this.params.edgeOrder[i-1];
			int edgeIdTo = this.params.edgeOrder[i];
			GemEdgeDirection edgeDirectionLoaded = getLoadedDirection(edgeIdTo);
			GemEdgeDirection edgeDirectionMapped = getEdgeDirections(edgeIdFrom, edgeIdTo).toEdgeDirection;
			this.edgesByPosition.get(edgeIdTo).onLoadComplete(edgeDirectionLoaded, edgeDirectionMapped);
		}
		
		//3. Create full gravity mapped edges.  These exist as a convenience for patterns.
		this.gravityMappedEdges = new ArrayList<GemEdge>();
		
		for (GMSet set : this.GetGravityMappedSets()) {
			
			List<LXPoint> points = new ArrayList<LXPoint>();
			
			for (int i=0; i<set.positions.length; i++) {
				int position = set.positions[i];
				
				// These are theoretical pairs.  This edge position may not exist in this gem.
				if (this.edgesByPosition.containsKey(position)) {
					GemEdge edge = this.edgesByPosition.get(position);
					for (LXPoint edgePoint : edge.getPoints(set.direction)) {
						points.add(edgePoint);
					}
				}
			}
			
			// Did we find any points for this set?  If so make a GravityMappedEdge;
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
		
	}
	
	private List<GMSet> GetGravityMappedSets()
	{
		List<GMSet> sets = new ArrayList<GMSet>();
		sets.add(new GMSet(new int[] {1,9}, GemEdgeDirection.DOWNUP));
		sets.add(new GMSet(new int[] {2,10}, GemEdgeDirection.DOWNUP));
		sets.add(new GMSet(new int[] {3,11}, GemEdgeDirection.DOWNUP));
		sets.add(new GMSet(new int[] {4,12}, GemEdgeDirection.DOWNUP));
		sets.add(new GMSet(new int[] {5,6,7,8}, GemEdgeDirection.LEFTRIGHT));
		
		return sets;
	}
	
	private GemEdgeDirection getLoadedDirection(int edgePosition) throws Exception {
	    //This method returns the direction that the points were added to the model.
		switch (edgePosition) {
    		case 1:
    		case 2:
    		case 3:
    		case 4:
    			return GemEdgeDirection.UPDOWN;
    		case 5:
    		case 6:
    		case 7:
    		case 8:
    			return GemEdgeDirection.LEFTRIGHT;
    		case 9:
    		case 10:
    		case 11:
    		case 12:
    			return GemEdgeDirection.DOWNUP;
    		case 13:
    		case 14:
    		case 15:
    		case 16:
    			return GemEdgeDirection.LEFTRIGHT;			
		}
		throw new Exception("Unknown edge position "+edgePosition+" for gem "+this.getGemName());
	}
	
	public String getGemName() {
		return this.params.clusterName + "." + this.params.positionInCluster;
	}

	@Override
	public AbstractMap<Integer, LXPoint> getPointsMapped()
	{
		final TreeMap<Integer, LXPoint> mappedPoints = new TreeMap<Integer, LXPoint>();
		if (this.params.gemType.equalsIgnoreCase("charlie")) {
			// Charlie gems are special because they take two channels.
			int i=0;	
			for (int iEdgeOrder = 0; iEdgeOrder < 6; iEdgeOrder++) {
				int edgePosition = this.params.edgeOrder[iEdgeOrder];
				for (Map.Entry<Integer,LXPoint> entry : this.edgesByPosition.get(edgePosition).getPointsMapped().entrySet()) {				
					mappedPoints.put(i++, entry.getValue());
				}
			}
			// Second half of charlie gem is on the next channel.
			i = this.controller.params.LEDsPerChannel;
			for (int iEdgeOrder = 6; iEdgeOrder < 12; iEdgeOrder++) {
				int edgePosition = this.params.edgeOrder[iEdgeOrder];
				for (Map.Entry<Integer,LXPoint> entry : this.edgesByPosition.get(edgePosition).getPointsMapped().entrySet()) {				
					mappedPoints.put(i++, entry.getValue());	
				}
			}
		} else {
			// Normal gem mapping, i.e. not charlie gem.
			int i=0;		
			for (int edgePosition : this.params.edgeOrder) {
				for (Map.Entry<Integer,LXPoint> entry : this.edgesByPosition.get(edgePosition).getPointsMapped().entrySet()) {				
					mappedPoints.put(i++, entry.getValue());
				}
			}
		}
		
		return mappedPoints;
	}
	
	/*
	//Original working method, before pulling points from edges
	@Override
	public AbstractMap<Integer, LXPoint> getPointsMapped()
	{
		final TreeMap<Integer, LXPoint> mappedPoints = new TreeMap<Integer, LXPoint>();
		int i=0;		
		for (int edge : this.params.edgeOrder) {
			for (LXPoint edgePoint : this.PointsByEdge.get(edge-1)) {
				mappedPoints.put(i++, edgePoint);
			}
		}
		
		return mappedPoints;
	}*/

	public class GMSet {
		public int[] positions;
		public GemEdgeDirection direction;
		
		public GMSet (int[] positions, GemEdgeDirection direction) {
			this.positions = positions;
			this.direction = direction;
		}		
	}

	public void computeNormalsJoule() {
		for (GemEdge ge : this.edges) {
			ge.computeNormals();
		}
		for (GemEdge gme : this.gravityMappedEdges) {
			gme.computeNormals();
		}
		
	}
	
}
