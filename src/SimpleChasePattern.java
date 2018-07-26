import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.lx.parameter.BooleanParameter.Mode;

@LXCategory(LXCategory.OTHER)
public class SimpleChasePattern extends JoulePattern {

    static final long TICKS_PER_SEC = 1000;

    public final CompoundParameter pixelMovesPerSec = 
            new CompoundParameter("Speed", 30, 10, 80)
            .setDescription("Pixel moves per second");
    
    public final DiscreteParameter totalPixelLength = 
            new DiscreteParameter("LenTotal", 60, 30, 60)
            .setDescription("Total pixel length");
    
    public final DiscreteParameter countFgPixels = 
            new DiscreteParameter("LenForeground", 1, 1, 30)
            .setDescription("Number of foreground pixels");

    public final CompoundParameter hueBg = 
            new CompoundParameter("Hue Background", LXColor.h(LXColor.GREEN), 0, 360)
            .setDescription("Color hue of nucleus");
    
    public final CompoundParameter hueFg = 
            new CompoundParameter("Hue Foreground", LXColor.h(LXColor.BLUE), 0, 360)
            .setDescription("Color hue of nucleus");
    
    public final CompoundParameter brightnessBg = 
            new CompoundParameter("Background brightness", 0.3, 0, 1)
            .setDescription("Background brightness, as a percent of full");

    public final BooleanParameter randomize = 
            new BooleanParameter("Randomize")
            .setDescription("Randomize the parameters")
            .setMode(Mode.MOMENTARY);

    private int pos = 0;
    double nextMoveTime;

    public SimpleChasePattern(LX lx) {
        super(lx);

        addParameter(pixelMovesPerSec);
        addParameter(totalPixelLength);
        addParameter(countFgPixels);
        addParameter(hueBg);
        addParameter(hueFg);
        addParameter(brightnessBg);
        addParameter(randomize);

        this.randomize.addListener(new LXParameterListener() {
            public void onParameterChanged(LXParameter p) {
                setRandomParameters();
            }
        });
    }

    public void setRandomParameters() {
        randomizeParameter(this.pixelMovesPerSec);
        randomizeParameter(this.totalPixelLength);
        randomizeParameter(this.countFgPixels);
        randomizeParameter(this.hueBg);
        randomizeParameter(this.hueFg);
        this.pos = 0;
    }

    public void onActive() {
        this.setRandomParameters();
        this.nextMoveTime = this.runMs + (TICKS_PER_SEC / this.pixelMovesPerSec.getValuef());
    }

    @Override
    protected void run(double deltaMs) {

        this.clearColors();

        int totalPixelLength = this.totalPixelLength.getValuei();
        int countFgPixels = this.countFgPixels.getValuei();
        float brightnessBg = this.brightnessBg.getValuef();

        // Shift the position if it's time.
        if (this.runMs > this.nextMoveTime) {
            pos++;
            pos %= totalPixelLength;

            this.nextMoveTime = this.runMs + (TICKS_PER_SEC / this.pixelMovesPerSec.getValuef());
        }

        int fgColor = LXColor.hsb(this.hueFg.getValuef(), 100, 100);
        int bgColor = LXColor.hsb(this.hueBg.getValuef(), 100, 100 * brightnessBg);

        // Write appropriate color to each pixel
        for (int i = 0; i < this.model.points.length; i++) {
            if ((i + pos) % totalPixelLength < countFgPixels) {
                // Foreground color
                colors[this.model.points[i].index] = fgColor;
            } else {
                // Background color
                colors[this.model.points[i].index] = bgColor;
            }
        }
    }

}
