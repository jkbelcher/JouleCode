import java.util.ArrayList;
import java.util.List;

import heronarts.lx.LX;
import heronarts.lx.blend.DissolveBlend;
import heronarts.lx.blend.LXBlend;

public class SmartTransitionBlend extends JouleBlend {

    private final List<LXBlend> blends = new ArrayList<LXBlend>();
    private int iActiveBlend = -1;
    private LXBlend activeBlend;
    public Boolean isRandom = true;
    
    public SmartTransitionBlend(LX lx) {
        super(lx);
        
        this.addBlends(new LXBlend[]{
                new HorizWipeBlend(lx),
                new GemEdgeBlend(lx),
                new SphereBlend(lx),
                new OneGemBlend(lx),
                new CarouselBlend(lx)
                });
                //new DissolveBlend(lx),
    }
    
    @Override
    public void dispose() {
        iActiveBlend = -1;
        activeBlend = null;
        for (LXBlend blend : blends) {
            blend.dispose();
        }
        blends.clear();
        super.dispose();        
    }
    
    public SmartTransitionBlend addBlend(LXBlend blend) {
        this.blends.add(blend);
        return this;        
    }
    
    public SmartTransitionBlend addBlends(LXBlend[] newBlends) {
        for (LXBlend blend : newBlends) {
            this.blends.add(blend);
        }
        return this;
    }
    
    @Override
    public void onActive() {
        if (blends.size() == 0)
            throw new IllegalStateException("SmartTransitionBlend must have child blends in order to run.");
            
        nextBlend();
    }
    
    @Override
    public void onInactive() {
        // We inactivate the blend right before activating a new one        
    }
    
    protected void nextBlend() {
        if (blends.size() == 1) {
            if (activeBlend == null) {
                iActiveBlend = 0;
                activeBlend = blends.get(0);
                activeBlend.onActive();
            }
            return;                
        }

        if (activeBlend != null)
            activeBlend.onInactive();
        
        if (isRandom) {
            int nextIndex = iActiveBlend;
            while (nextIndex == iActiveBlend) {            
                nextIndex = (int) (Math.random() * blends.size());
                nextIndex %= blends.size(); // necessary?
            }

            iActiveBlend = nextIndex;
        } else {
            int nextIndex = iActiveBlend + 1;
            if (nextIndex > (this.blends.size() - 1))
                nextIndex = 0;

            iActiveBlend = nextIndex;
        }

        activeBlend = this.blends.get(iActiveBlend);
        activeBlend.onActive();
    }

    @Override
    public void lerp(int[] dst, int[] src, double amt, int[] output) {
        activeBlend.lerp(dst, src, amt, output);
    }

}
