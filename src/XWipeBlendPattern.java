import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;

public class XWipeBlendPattern extends JouleBlendPattern {

	public XWipeBlendPattern(LX lx) {
		super(lx);
	}
	
	@Override
	protected void run(double deltaMs) {
		// Use currentMs instead of runMs!!

		for (LXPoint p : this.model.getPoints()) {
			int color = p.xn < this.percentRemaining ? LXColor.BLACK : LXColor.WHITE;
			colors[p.index] = color;
		}
	}

}
