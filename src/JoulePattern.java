import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.BoundedParameter;
import heronarts.lx.parameter.DiscreteParameter;

//This class exists to make the JouleModel available to patterns so they can run model-specific animations.
//This prevents each pattern from needing to cast LXModel to JouleModel.

public abstract class JoulePattern extends RandomizableLXPattern {

    protected final JouleModel model;
    
    protected JoulePattern(LX lx) {
        super(lx);
        this.model=(JouleModel)lx.model;
    }
}
