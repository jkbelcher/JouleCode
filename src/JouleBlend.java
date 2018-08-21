import heronarts.lx.LX;
import heronarts.lx.blend.LXBlend;
import heronarts.lx.parameter.BoundedParameter;
import heronarts.lx.parameter.DiscreteParameter;

public abstract class JouleBlend extends LXBlend {

    protected final JouleModel model;
    
    public JouleBlend(LX lx) {
        super(lx);
        this.model=(JouleModel)lx.model;
    }

    @Override
    public void lerp(int[] from, int[] to, double amt, int[] output) {
        // Overridden to prevent accidental infinite recursion
        // Joule blends should override this
    }
    
    @Override
    public final void blend(int[] dst, int[] src, double alpha, int[] output) {
        // Prioritize lerp for Joule,
        // assuming custom blends are mainly asymmetrical.
        this.lerp(dst, src, alpha / 2.0, output);
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

}
