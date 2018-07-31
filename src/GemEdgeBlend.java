import java.util.ArrayList;

import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;

public class GemEdgeBlend extends JouleBlend {

    public GemEdgeBlend(LX lx) {
        super(lx);
    }

    @Override
    public void onActive() {
        //System.out.println("GemEdgeBlend active");        
        this.initialize();
    }
    
    @Override
    public void onInactive() {
        //System.out.println("GemEdgeBlend inactive");        
    }
    
    @Override
    public void blend(int[] dst, int[] src, double alpha, int[] output) {
        double alphaHalf = alpha / 2.;
        for (GemEdgeTime edge : this.edges) {
            if (edge.timing < alphaHalf)
            {
                for (LXPoint p : edge.edge.getPoints()) {
                    output[p.index] = src[p.index];
                }
            }
            else
            {
                for (LXPoint p : edge.edge.getPoints()) {
                    output[p.index] = dst[p.index];
                }
            }
        }

    }
    
    private ArrayList<GemEdgeTime> edges;

    void initialize() {
        ArrayList<GemEdgeTime> edgesToSetup = new ArrayList<GemEdgeTime>();

        for (Cluster cluster : this.model.clusters) {
            for (Gem g : cluster.gems) {
                for (GemEdge ge : g.edges) {
                    edgesToSetup.add(new GemEdgeTime(ge));
                }
            }
        }

        edges = new ArrayList<GemEdgeTime>();
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
