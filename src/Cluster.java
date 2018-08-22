import java.util.ArrayList;
import java.util.List;

import heronarts.lx.model.LXAbstractFixture;
import heronarts.lx.model.LXPoint;
import processing.core.PApplet;

public class Cluster extends LXAbstractFixture implements INormalizedScope {

    public final ClusterParameters params;
    public final List<Gem> gems = new ArrayList<Gem>();
    public final List<GemEdge> edges = new ArrayList<GemEdge>();
    public final List<GemEdge> continuousEdges = new ArrayList<GemEdge>();

    public Cluster(ClusterParameters params) {
        this.params = params;
        PApplet.println(" Cluster", this.params.name);
    }

    public void AddGem(Gem gem) {
        this.gems.add(gem);
        this.edges.addAll(gem.edges);
        this.continuousEdges.addAll(gem.edges);
        this.addPoints(gem);
    }
    
    // INormalizedScope
    
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
    
    public int countChildScopes() {
        return 3;
    }
    
    public List<INormalizedScope> getChildScope(int index) {
        switch (index) {
        case 0:
            return new ArrayList<INormalizedScope>(this.continuousEdges);
        case 1:
            return new ArrayList<INormalizedScope>(this.edges);
        case 2:
            return new ArrayList<INormalizedScope>(this.gems);
        default:
            throw new IllegalArgumentException("An invalid child scope was requested: " + this.getClass() + " " + index);                
        }
    }
    
    // end INormalizedScope

}
