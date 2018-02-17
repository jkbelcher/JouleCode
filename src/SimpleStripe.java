import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.LXModulator;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.parameter.CompoundParameter;
import processing.core.PApplet;

// Very simple pattern, just has one parameter and one LFO
public class SimpleStripe extends LXPattern {
  
	private PApplet applet;
	
  public final CompoundParameter period = (CompoundParameter)
    new CompoundParameter("Period", 1000, 500, 10000)
    .setExponent(2)
    .setDescription("Period of oscillation of the stripe");
    
    public final CompoundParameter size = (CompoundParameter)
    new CompoundParameter("Size", 3*JouleCode.FEET, 1*JouleCode.FEET, 10*JouleCode.FEET)
    .setDescription("Size of the stripe");
  
  private final LXModulator xPos = startModulator(new SinLFO(model.xMin, model.xMax, period)); 
  
  public SimpleStripe(PApplet tApplet, LX lx) {
    super(lx);
    
    this.applet = tApplet;
    
    // Parameters automatically appear in UI and are saved in project file
    addParameter("period", this.period);
    addParameter("size", this.size);
  }
  
  public void run(double deltaMs) {
    float xPos = this.xPos.getValuef();
    float falloff = 100 / this.size.getValuef();
    for (LXPoint p : model.points) {
      // Render each point based on its distance from a moving target position in the x axis 
      colors[p.index] = palette.getColor(p, PApplet.max(0, 100 - falloff * PApplet.abs(p.x - xPos)));
    }
  }
}
  
