import java.util.ArrayList;

import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;

public class GemEdgeBlend extends JouleBlend {

    private final ArrayList<GemEdgeTime> edges = new ArrayList<GemEdgeTime>();

    public GemEdgeBlend(LX lx) {
        super(lx);
    }

    @Override
    public void onActive() {
        this.initialize();
    }
    
    @Override
    public void lerp(int[] from, int[] to, double amt, int[] output) {
        for (GemEdgeTime edge : this.edges) {
            if (amt > edge.timing)
            {
                for (LXPoint p : edge.edge.getPoints()) {
                    output[p.index] = to[p.index];
                }
            }
            else
            {
                for (LXPoint p : edge.edge.getPoints()) {
                    output[p.index] = from[p.index];
                }
            }
        }
    }

    void initialize() {
        ArrayList<GemEdgeTime> edgesToSetup = new ArrayList<GemEdgeTime>();

        for (Cluster cluster : this.model.clusters) {
            for (Gem g : cluster.gems) {
                for (GemEdge ge : g.edges) {
                    edgesToSetup.add(new GemEdgeTime(ge));
                }
            }
        }

        edges.clear();
        float percentBetweenFlips = 1.0f / ((float) edgesToSetup.size());
        float currentPercent = 0f;
        while (edgesToSetup.size() > 0) {
            //Choose random edge
            int iEdge = (int) (Math.random() * edgesToSetup.size());
            GemEdgeTime edge = edgesToSetup.get(iEdge);
            edgesToSetup.remove(iEdge);
            edge.timing = currentPercent;
            
            this.edges.add(edge);
            
            currentPercent += percentBetweenFlips;
        }
    }
    
    private class GemEdgeTime {

        public final GemEdge edge;
        public double timing;

        public GemEdgeTime(GemEdge e) {
            this.edge = e;
            this.timing = 0;
        }
    }    

}
