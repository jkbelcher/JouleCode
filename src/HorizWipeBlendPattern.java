import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;

public class HorizWipeBlendPattern extends JouleBlendPattern {

	public final CompoundParameter tilt = 
			new CompoundParameter("Tilt", 0.6, -.99, .99)
			.setDescription("Normalized tilt of the wipe");
	
	public HorizWipeBlendPattern(LX lx) {
		super(lx);
		
		addParameter(tilt);
	}

	float yLean = 0.6f;
	
	public void setRandomParameters() {
		randomizeParameter(this.tilt);
	}
	
	public void onActive()
	{
		this.setRandomParameters();
		
		// Hold consistent tilt for one pass
		this.yLean = this.tilt.getValuef();
	}
	
	@Override
	protected void run(double deltaMs) {
		// Use currentMs instead of runMs!!

		for (LXPoint p : this.model.getPoints()) {
			int color = isPointOldFrame(p) ? LXColor.BLACK : LXColor.WHITE;
			colors[p.index] = color;
		}
	}
	
	Boolean isPointOldFrame(LXPoint p) {
		if (yLean >= 0) {
			return (p.xn + (p.yn * yLean)) < (this.percentRemaining * (1+yLean));
		} else {
			return (p.xn + ((1-p.yn) * Math.abs(yLean))) < (this.percentRemaining * (1+Math.abs(yLean)));
		}
	}

}
