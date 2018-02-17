import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import processing.core.PApplet;

public class EdgeWipeBlendPattern extends JouleBlendPattern {

	public EdgeWipeBlendPattern(LX lx) {
		super(lx);
	}

	@Override
	protected void run(double deltaMs) {
		for (Gem gem : this.model.gems) {
			for (GemEdge edge: gem.edges) {
				float litPixelsf = ((float)edge.getNumPoints()) * this.percentComplete;
				float percentLastPixel = litPixelsf - ((int)litPixelsf);
				int litPixels = (int) litPixelsf;
				//PApplet.println(this.percentComplete, edge.getNumPoints(), litPixelsf, litPixels);
								
				for (int i=0; i<edge.getNumPoints(); i++) {
					int color = (i < (litPixels-1)) ? LXColor.WHITE : LXColor.BLACK;
					colors[edge.getPoint(i, edge.directionMapped).index] = color;
				}
				if (litPixels < edge.getNumPoints()) {
					colors[edge.getPoint(litPixels, edge.directionMapped).index] = LXColor.scaleBrightness(LXColor.WHITE, percentLastPixel);
				}
			}
		}
	}


}
