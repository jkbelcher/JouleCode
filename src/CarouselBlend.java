import java.util.ArrayList;
import java.util.List;

import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;

public class CarouselBlend extends FixedFadeBlend {

    protected CarouselOrder[] gemOrder;
    
    public CarouselBlend(LX lx) {
        this(lx, new CarouselOrder[] {
                new CarouselOrder("WL.4", 1),
                new CarouselOrder("WL.4", 2),
                new CarouselOrder("WL.4", 3),
                new CarouselOrder("WL.4", 4),
                new CarouselOrder("WL.4", 5),
                new CarouselOrder("WL.3", 1),
                new CarouselOrder("WL.3", 2),
                new CarouselOrder("WL.3", 3),
                new CarouselOrder("WL.2", 1),
                new CarouselOrder("WL.1", 1),
                new CarouselOrder("SL.1", 1),
                new CarouselOrder("SL.1", 2),
                new CarouselOrder("SL.2", 1),
                new CarouselOrder("SL.2", 2),
                new CarouselOrder("SL.2", 3),
                new CarouselOrder("SL.2", 4),
                new CarouselOrder("EL.1", 1),
                new CarouselOrder("EL.1", 2),
                new CarouselOrder("EL.1", 3),                
                new CarouselOrder("EL.2", 1),
                new CarouselOrder("EL.2", 2),
                new CarouselOrder("EL.2", 3),
                new CarouselOrder("EL.2", 4),                
                new CarouselOrder("EM.1", 1),
                new CarouselOrder("EM.1", 2),
                new CarouselOrder("EM.1", 3),
                new CarouselOrder("EL.3", 1),
                new CarouselOrder("EL.3", 2),
                new CarouselOrder("EL.3", 3),
                new CarouselOrder("EL.3", 4),
                new CarouselOrder("EL.3", 5),
                new CarouselOrder("EL.3", 6),
                new CarouselOrder("EL.3", 7),
                new CarouselOrder("EL.4", 1),
                new CarouselOrder("EL.4", 2),
                new CarouselOrder("EL.4", 3),
                new CarouselOrder("EL.4", 4),
                new CarouselOrder("EL.4", 5),
                new CarouselOrder("EM.2", 1),
                new CarouselOrder("EM.2", 2),
                new CarouselOrder("NC.2", 1),
                new CarouselOrder("NC.1", 1),
                new CarouselOrder("WU.3", 1),
                new CarouselOrder("WU.3", 2),
                new CarouselOrder("WU.3", 3),
                new CarouselOrder("WU.3", 4),
                new CarouselOrder("WU.2", 1),
                new CarouselOrder("WU.2", 2),
                new CarouselOrder("WU.2", 3),
                new CarouselOrder("WU.2", 4),
                new CarouselOrder("WU.1", 1),
                new CarouselOrder("WU.1", 2),
                new CarouselOrder("WU.1", 3),
                new CarouselOrder("SG.1", 1),
                new CarouselOrder("SG.2", 1),
                new CarouselOrder("SU.1", 1),
                new CarouselOrder("SU.1", 2),
                new CarouselOrder("SU.1", 3),
                new CarouselOrder("EU.1", 1),
                new CarouselOrder("EU.1", 2),
                new CarouselOrder("EU.1", 3),
                new CarouselOrder("EU.1", 4),
                new CarouselOrder("EU.1", 5),
                new CarouselOrder("Sign", 1),
                new CarouselOrder("EU.2", 1),
                new CarouselOrder("EU.2", 2),
                new CarouselOrder("EU.3", 1),
                new CarouselOrder("EU.3", 2),
                new CarouselOrder("EU.3", 3)
        });
    }
    
    public CarouselBlend(LX lx, CarouselOrder[] gemOrder) {
        super(lx);
        this.gemOrder = gemOrder;
    }

    @Override
    protected List<PointTransition> buildPointTransitions() {
        double fade = this.fade.getValue();
        
        double perGem = 1 / ((float)this.model.gems.size());
        
        ArrayList<PointTransition> transitions = new ArrayList<PointTransition>();
        for (int i=0; i < this.gemOrder.length; i++) {
            CarouselOrder co = gemOrder[i];
            Gem gem = getGem(co);
            double amt = Math.min(1,perGem * ((double) (i+1)));
            for (LXPoint p : gem.getPoints()) {
                PointTransition pt = buildFixedFadePointTransition(p, amt, fade);
                transitions.add(pt);
            }
        }
        return transitions;
    }
    
    protected Gem getGem(CarouselOrder co) {
        for (Cluster cluster : this.model.clusters) {
            if (cluster.params.name.equals(co.cluster)) {
                for (Gem gem : cluster.gems) {
                    if (gem.params.positionInCluster == co.positionInCluster) {
                        return gem;
                    }
                }
            }
        }        
        //throw new InvalidObjectException("Gem not found.  Adjust configuration or pattern: " + co.toString());
        System.err.println("Gem not found.  Adjust configuration or pattern: " + co.toString());
        return null;
    }
    
    static public class CarouselOrder {
        public String cluster;
        public int positionInCluster;
        
        public CarouselOrder (String cluster, int positionInCluster) {
            this.cluster = cluster;
            this.positionInCluster = positionInCluster;
        }
    }

}
