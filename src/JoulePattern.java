import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.BoundedParameter;
import heronarts.lx.parameter.DiscreteParameter;

//This class exists to make the JouleModel available to patterns so they can run model-specific animations.
//This prevents each pattern from needing to cast LXModel to JouleModel.

public abstract class JoulePattern extends LXPattern {

    protected final JouleModel model;
    
    protected JoulePattern(LX lx) {
        super(lx);
        this.model=(JouleModel)lx.model;
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
    
    public int getRandomColor() {
        return this.getRandomColor(100);
    }
    
    public int getRandomColor(float brightness) {
        return LXColor.hsb(Math.random() * 360.0, 100, brightness);
    }	
}
