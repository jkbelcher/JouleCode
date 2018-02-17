import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.CompoundParameter;

public class VertRainbowShiftPattern extends JoulePattern {

	private static final float RADIANS_PER_REVOLUTION = 2.0f;

	public final CompoundParameter hueRange = 
			new CompoundParameter("hueRange", 128, 5, 400)
			.setDescription("hueRange");
	public final CompoundParameter speed = 
			new CompoundParameter("Speed", .14, .001, 1.0)
			.setDescription("Speed in full range shifts per second");
	
	float huePos = 0;
	
	public VertRainbowShiftPattern(LX lx) {
		super(lx);
		
		addParameter(hueRange);
		addParameter(speed);		
	}

	@Override
	protected void run(double deltaMs) {
		
		float hueRange = this.hueRange.getValuef();
		
		//Calc current position
	    float speed = this.speed.getValuef();
	    float degreesMoved = hueRange*speed*(((float) deltaMs)/1000f);
	    
	    huePos += degreesMoved;
	    huePos %= 360;
	    
	    for (Gem gem : this.model.gems) {
	    	for (GemEdge edge : gem.gravityMappedEdges) {
	    		int numPixels = edge.getNumPoints();
	    		float numPixelsf = (float) numPixels;
	    		float degreesPerPixel = (hueRange/numPixelsf);
	    		
	    		for (int i = 0; i < numPixels; i++) {
	    			float hue = ((degreesPerPixel*((float) i))+huePos) % 360;
	    			colors[edge.getPoint(i, edge.getDirectionAntiGravity()).index] = LXColor.hsb(hue, 100, 100);
	    		}
	    		
	    	}
	    }
    }

}
