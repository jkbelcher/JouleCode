import heronarts.lx.LX;
import heronarts.lx.blend.LXBlend.FunctionalBlend;
import heronarts.lx.color.LXColor;

public class JTestBlend extends FunctionalBlend {
    public JTestBlend(LX lx) {
        super(lx, LXColor::add);
      }
    
    @Override
    public void onActive() {
        System.out.println("JTestBlend active");        
    }
    
    @Override
    public void onInactive() {
        System.out.println("JTestBlend inactive");        
    }
    
}
