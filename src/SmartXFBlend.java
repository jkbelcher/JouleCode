import java.util.ArrayList;
import java.util.List;

import heronarts.lx.LX;
import heronarts.lx.blend.LXBlend;
import heronarts.lx.parameter.CompoundParameter;

public class SmartXFBlend extends JouleBlend {

    public final CompoundParameter secsToChange = new CompoundParameter("SecsToChange", 2, 0, 30)
            .setDescription("Number of seconds for the crossfader to be all the way on one side before a new blend is chosen.");

    private final List<LXBlend> blends = new ArrayList<LXBlend>();
    private int iActiveBlend = -1;
    private LXBlend activeBlend;
    public Boolean isRandom = false;
    
    public SmartXFBlend(LX lx) {
        super(lx);

        this.addBlends(new LXBlend[]{
                new HorizWipeBlend(lx),
                new GemEdgeBlend(lx),
                new SphereBlend(lx),
                new OneGemBlend(lx)
                });
        
        this.lastRunTime = System.currentTimeMillis() - 5;
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
    
    public SmartXFBlend addBlend(LXBlend blend) {
        this.blends.add(blend);
        return this;        
    }
    
    public SmartXFBlend addBlends(LXBlend[] newBlends) {
        for (LXBlend blend : newBlends) {
            this.blends.add(blend);
        }
        return this;
    }
    
    @Override
    public void onActive() {
        if (blends.size() == 0)
            throw new IllegalStateException("SmartXFBlend must have child blends in order to run.");
            
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
    
    long lastRunTime;
    
    public boolean isTimeToChangeBlends() {
        long millisToChange = (long) (this.secsToChange.getValue() * 1000);
        long currentTime = System.currentTimeMillis();
        boolean isTimeToChange = (currentTime - this.lastRunTime) > millisToChange;
        
        this.lastRunTime = currentTime;
        
        if (isTimeToChange)
            System.out.println("Time to change!");
        return isTimeToChange;                
    }

    @Override
    public void blend(int[] dst, int[] src, double alpha, int[] output) {
        if (this.isTimeToChangeBlends())
            nextBlend();
            
        activeBlend.blend(dst, src, alpha, output);
    }

}
