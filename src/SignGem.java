import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import heronarts.lx.model.LXPoint;
import heronarts.lx.transform.LXTransform;

public class SignGem extends Gem {

    public SignGem(GemParameters p, GemType gemType, LXTransform transform) throws Exception {
        super(p, gemType, transform);
   }

    protected LXTransform AdjustTransformForEdge(LXTransform transform, int edgePosition) {
        // Rotate transform so the edge can be drawn on X-axis
        // Need to use GemType and GemSizes (loaded from file) to rotate to correct positions.
        // **Probably need an offset column for each side to offset any blank space to start of LED strip.
        float t = this.gemType.topSquare();
        float b = this.gemType.bottomSquare();
        float h = this.gemType.heightSquares();
        final float i = 6.25f;
        final float wordSpace = .75f;
        final float wordOffset = 1.75f;
        
        //float a = (float) Math.toDegrees(Math.asin(0.5 * (this.gemType.topSquare() - this.gemType.bottomSquare()) / (this.gemType.heightSquares())));

        switch (edgePosition) {
        case 1:
            transform.translate(t, 0, 0);
            transform.rotateZ(Math.toRadians(-90));
            break;
        case 2:
            // Already aligned on X-axis
            break;
        case 3:
            transform.rotateZ(Math.toRadians(-90));
            break;
        case 4:
            transform.translate(0, 0-h, 0);
            break;
        case 5:
            transform.translate(((t-b)/2)+b, 0 - (h-i)/2, 0);
            transform.rotateZ(Math.toRadians(-90));            
            break;
        case 6:
            transform.translate((t-b)/2, 0 - (h-i)/2, 0);
            break;
        case 7:
            transform.translate((t-b)/2, 0 - (h-i)/2, 0);
            transform.rotateZ(Math.toRadians(-90));            
            break;
        case 8:
            transform.translate((t - b) / 2, 0 - ((h - i) / 2 + i), 0);
            break;
        case 9:
            transform.translate(((t-b)/2)+b - wordSpace, 0 - (h-i)/2, 0 - wordOffset);
            transform.rotateZ(Math.toRadians(-90));            
            break;
        case 10:
            transform.translate((t-b)/2, 0 - (h-i)/2 - wordSpace, 0 - wordOffset);
            break;
        case 11:
            transform.translate((t-b)/2 + wordSpace, 0 - (h-i)/2, 0 - wordOffset);
            transform.rotateZ(Math.toRadians(-90));            
            break;
        case 12:
            transform.translate((t - b) / 2, wordSpace - ((h - i) / 2 + i), 0 - wordOffset);
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

    public GemEdgeDirectionPair getEdgeDirections(String gemType, int fromEdgeId, int toEdgeId) throws Exception {
        // *Some gems have jumpered edges and will transition between two non-touching edges.
        // We will have to add the map for these when we find them.
        // If there are two conflicting map directions in a jumper, we will have to add an optional override in the config file.
        
        //Special cases for GoGo gems because they have 4 channels that all feed from the top
        if (gemType.equalsIgnoreCase("gogo") && (fromEdgeId >=1 && fromEdgeId <= 4) && (toEdgeId >= 9 && toEdgeId <= 12)) {
            return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.UPDOWN);
        }
                
        switch (fromEdgeId) {
        case 1:
            switch (toEdgeId) {
            case 2:
                return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.RIGHTLEFT);
            case 4:
                return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.RIGHTLEFT);
            case 5:
                return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.DOWNUP);
            }
        case 2:
            switch (toEdgeId) {
            case 1:
                return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.UPDOWN);
            case 3:
                return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.UPDOWN);
            }
        case 3:
            switch (toEdgeId) {
            case 2:
                return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.LEFTRIGHT);
            case 4:
                return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.LEFTRIGHT);
            }
        case 4:
            switch (toEdgeId) {
            case 1:
                return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.DOWNUP);
            case 3:
                return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.DOWNUP);
            case 5:
                return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.DOWNUP);
            }
        case 5:
            switch (toEdgeId) {
            case 6:
                return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.RIGHTLEFT);
            }
        case 6:
            switch (toEdgeId) {
            case 7:
                return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.UPDOWN);
            }
        case 7:
            switch (toEdgeId) {
            case 8:
                return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.LEFTRIGHT);
            }
        case 8:
            switch (toEdgeId) {
            case 9:
                return new GemEdgeDirectionPair(GemEdgeDirection.LEFTRIGHT, GemEdgeDirection.DOWNUP);
            }
        case 9:
            switch (toEdgeId) {
            case 10:
                return new GemEdgeDirectionPair(GemEdgeDirection.DOWNUP, GemEdgeDirection.RIGHTLEFT);
            }
        case 10:
            switch (toEdgeId) {
            case 11:
                return new GemEdgeDirectionPair(GemEdgeDirection.RIGHTLEFT, GemEdgeDirection.UPDOWN);
            }
        case 11:
            switch (toEdgeId) {
            case 12:
                return new GemEdgeDirectionPair(GemEdgeDirection.UPDOWN, GemEdgeDirection.LEFTRIGHT);
            }
        }
        throw new Exception("Unknown gem edge order " + fromEdgeId + "->" + toEdgeId + " for gem " + this.getGemName()
                + "  Please add edge order in Gem.getEdgeDirections()");
    }
    
    protected List<GMSet> GetGravityMappedSets() {
        List<GMSet> sets = new ArrayList<GMSet>();
        sets.add(new GMSet(new int[] { 1 }, GemEdgeDirection.DOWNUP));
        sets.add(new GMSet(new int[] { 3 }, GemEdgeDirection.DOWNUP));
        sets.add(new GMSet(new int[] { 5 }, GemEdgeDirection.DOWNUP));
        sets.add(new GMSet(new int[] { 7 }, GemEdgeDirection.DOWNUP));
        sets.add(new GMSet(new int[] { 9 }, GemEdgeDirection.DOWNUP));
        sets.add(new GMSet(new int[] { 11 }, GemEdgeDirection.DOWNUP));
        sets.add(new GMSet(new int[] { 2 }, GemEdgeDirection.LEFTRIGHT));
        sets.add(new GMSet(new int[] { 4 }, GemEdgeDirection.LEFTRIGHT));
        sets.add(new GMSet(new int[] { 6 }, GemEdgeDirection.LEFTRIGHT));
        sets.add(new GMSet(new int[] { 8 }, GemEdgeDirection.LEFTRIGHT));
        sets.add(new GMSet(new int[] { 10 }, GemEdgeDirection.LEFTRIGHT));
        sets.add(new GMSet(new int[] { 12 }, GemEdgeDirection.LEFTRIGHT));

        return sets;
    }
    
    protected List<GMSet> GetContinuousEdges() {
        List<GMSet> sets = new ArrayList<GMSet>();
        sets.add(new GMSet(new int[] { 4, 3, 2, 1 }, GemEdgeDirection.RIGHTLEFT));
        sets.add(new GMSet(new int[] { 8, 7, 6, 5 }, GemEdgeDirection.RIGHTLEFT));
        sets.add(new GMSet(new int[] { 12, 11, 10, 9 }, GemEdgeDirection.RIGHTLEFT));

        return sets;
    }
    
    protected GemEdgeDirection getLoadedDirection(int edgePosition) throws Exception {
        // This method returns the direction that the points were added to the model.
        switch (edgePosition) {
        case 1:
        case 3:
        case 5:
        case 7:
        case 9:
        case 11:
            return GemEdgeDirection.UPDOWN;
        case 2:
        case 4:
        case 6:
        case 8:
        case 10:
        case 12:
            return GemEdgeDirection.LEFTRIGHT;
        }
        throw new Exception("Unknown edge position " + edgePosition + " for gem " + this.getGemName());
    }
    
    @Override
    public AbstractMap<Integer, LXPoint> getPointsMapped() {
        final TreeMap<Integer, LXPoint> mappedPoints = new TreeMap<Integer, LXPoint>();
        if (this.params.gemType.equalsIgnoreCase("charlie")) {
            // Charlie gems are special because they take two channels.
            int i = 0;
            for (int iEdgeOrder = 0; iEdgeOrder < 6; iEdgeOrder++) {
                int edgePosition = this.params.edgeOrder[iEdgeOrder];
                for (Map.Entry<Integer, LXPoint> entry : this.edgesByPosition.get(edgePosition).getPointsMapped()
                        .entrySet()) {
                    mappedPoints.put(i++, entry.getValue());
                }
            }
            // Second half of charlie gem is on the next channel.
            i = this.controller.params.LEDsPerChannel;
            for (int iEdgeOrder = 6; iEdgeOrder < 12; iEdgeOrder++) {
                int edgePosition = this.params.edgeOrder[iEdgeOrder];
                for (Map.Entry<Integer, LXPoint> entry : this.edgesByPosition.get(edgePosition).getPointsMapped()
                        .entrySet()) {
                    mappedPoints.put(i++, entry.getValue());
                }
            }
        } else if (this.params.gemType.equalsIgnoreCase("gogo")) {
            // GoGo gems are special because they take 4 channels.
            // First channel
            int i = 0;
            for (int iEdgeOrder = 0; iEdgeOrder < 3; iEdgeOrder++) {
                int edgePosition = this.params.edgeOrder[iEdgeOrder];
                for (Map.Entry<Integer, LXPoint> entry : this.edgesByPosition.get(edgePosition).getPointsMapped()
                        .entrySet()) {
                    mappedPoints.put(i++, entry.getValue());
                }
            }
            // Second channel
            i = this.controller.params.LEDsPerChannel;
            for (int iEdgeOrder = 3; iEdgeOrder < 6; iEdgeOrder++) {
                int edgePosition = this.params.edgeOrder[iEdgeOrder];
                for (Map.Entry<Integer, LXPoint> entry : this.edgesByPosition.get(edgePosition).getPointsMapped()
                        .entrySet()) {
                    mappedPoints.put(i++, entry.getValue());
                }
            }
            // Third channel
            i = this.controller.params.LEDsPerChannel * 2;
            for (int iEdgeOrder = 6; iEdgeOrder < 9; iEdgeOrder++) {
                int edgePosition = this.params.edgeOrder[iEdgeOrder];
                for (Map.Entry<Integer, LXPoint> entry : this.edgesByPosition.get(edgePosition).getPointsMapped()
                        .entrySet()) {
                    mappedPoints.put(i++, entry.getValue());
                }
            }
            // Fourth channel
            i = this.controller.params.LEDsPerChannel * 3;
            for (int iEdgeOrder = 9; iEdgeOrder < 12; iEdgeOrder++) {
                int edgePosition = this.params.edgeOrder[iEdgeOrder];
                for (Map.Entry<Integer, LXPoint> entry : this.edgesByPosition.get(edgePosition).getPointsMapped()
                        .entrySet()) {
                    mappedPoints.put(i++, entry.getValue());
                }
            }
        } else {
            // Normal gem mapping, i.e. not charlie or gogo gem.
            int i = 0;
            for (int edgePosition : this.params.edgeOrder) {
                for (Map.Entry<Integer, LXPoint> entry : this.edgesByPosition.get(edgePosition).getPointsMapped()
                        .entrySet()) {
                    mappedPoints.put(i++, entry.getValue());
                }
            }
        }

        return mappedPoints;
    }

}
