import heronarts.lx.LX;
import heronarts.lx.blend.LXBlend;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.BoundedParameter;
import heronarts.lx.parameter.DiscreteParameter;

public abstract class RandomizableLXBlend extends LXBlend {
    
    protected RandomizableLXBlend(LX lx) {
        super(lx);
    }

    public /* abstract */ void setRandomParameters() {        
    }
    
    public void onActive()
    {
        this.setRandomParameters();
    }
    
    public static void randomizeParameter(BoundedParameter parameter) {
        randomizeParameter(parameter, parameter.range.min, parameter.range.max);
    }

    public static void randomizeParameter(BoundedParameter parameter, double min, double max) {
        float newValue = (float) ((Math.random()*(max-min))+min);
        parameter.setValue(newValue);
    }
    
    public static void randomizeParameter(DiscreteParameter parameter) {
        int newValue = (int) (Math.random() * ((float)(parameter.getRange()))+parameter.getMinValue());
        parameter.setValue(newValue);
    }

    public static void randomizeParameter(BooleanParameter parameter) {
        boolean newValue = Math.random() >= 0.5 ? true : false;
        parameter.setValue(newValue);
    }

}
