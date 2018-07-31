import java.util.HashMap;
import java.util.TreeMap;

import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;

public class RaindropPattern extends JoulePattern {

    public RaindropPattern(LX lx) {
        super(lx);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void run(double deltaMs) {
        // TODO Auto-generated method stub

    }
    
    protected class Raindrop {
        double startMs;
        double endMs;
        float size;
        int color;
        
        TreeMap<LXPoint, Double> points = new TreeMap<LXPoint, Double>();    
        
        float StretchRatio; //>0, <1000
    }
    
    //protected class 

}
