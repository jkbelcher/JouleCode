import java.util.List;

import heronarts.lx.LX;
import heronarts.lx.blend.DissolveBlend;
import heronarts.lx.blend.LXBlend;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;

public abstract class PrecalculatedBlend extends JouleBlend {

    private List<PointTransition> pointTransitions;
    
    private final LXBlend subBlendFunction;

    public PrecalculatedBlend(LX lx) {
        super(lx);
        this.subBlendFunction = new DissolveBlend(lx);
    }
    
    @Override
    public void onActive() {
        this.initialize();
    }
    
    protected final void initialize() {
        this.pointTransitions = this.buildPointTransitions();
    }
    
    protected abstract List<PointTransition> buildPointTransitions();

    @Override
    public void lerp(int[] from, int[] to, double amt, int[] output) {
        for (PointTransition pt : this.pointTransitions) {
            if (amt < pt.startAmt) {
                output[pt.p.index] = from[pt.p.index];                
            } else {
                if (amt < pt.endAmt) {
                    double crossFadeAmt = (amt-pt.startAmt) / pt.lenAmt;
                    output[pt.p.index] = lerpSinglePoint(from[pt.p.index], to[pt.p.index], crossFadeAmt);
                } else {
                    output[pt.p.index] = to[pt.p.index];
                }
            }
        }
    }
    
    public int lerpSinglePoint(int from, int to, double amt) {
        int dst, src;
        double alpha;
        if (amt <= 0.5) {
          dst = from;
          src = to;
          alpha = amt * 2.;
        } else {
          dst = to;
          src = from;
          alpha = (1-amt) * 2.;
        }
        return LXColor.add(dst, src, alpha);
    }

    protected class PointTransition {
        public final LXPoint p;
        public double startAmt;
        public double lenAmt;
        public double endAmt;      //Value from 0-1
        
        public PointTransition(LXPoint p) {
            this(p, 1, 0, 1);
        }

        public PointTransition(LXPoint p, double amt) {
            this(p,amt,0,amt);
        }
        
        public PointTransition(LXPoint p, double startAmt, double lenAmt, double endAmt) {
            this.p = p;
            this.startAmt = startAmt;
            this.lenAmt = lenAmt;
            this.endAmt = endAmt;
        }
    }
}
