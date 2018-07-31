import heronarts.lx.LX;
import heronarts.lx.blend.LXBlend;

public abstract class JouleBlend extends LXBlend {

    protected final JouleModel model;
    
    public JouleBlend(LX lx) {
        super(lx);
        this.model=(JouleModel)lx.model;
    }

}
