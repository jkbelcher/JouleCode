import java.util.ArrayList;
import java.util.List;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.LXColor;

// This class was used during development to find issues with the model.
@LXCategory(LXCategory.TEST)
public class GemEdgeColorPattern extends JoulePattern {

	private final List<GemEdgeColorPair> edges;
	
	public GemEdgeColorPattern(LX lx) {
		super(lx);
		
		edges = new ArrayList<GemEdgeColorPair>();
		
		int hue = 0;
		int hueOffset = 20;
		
		for (Gem g : this.model.gems) {
			hue = 0;
			for (GemEdge ge : g.edges) {
				int c = LXColor.hsb(hue, 100, 100);
				/*int c = LXColor.RED;
				if (ge.id==1) { 
					c = LXColor.BLUE;
				} else if (ge.id==2) {
					c = LXColor.WHITE;
				}
				*/
				this.edges.add(new GemEdgeColorPair(ge, c));
				hue += hueOffset;
				hue %= 360;
			}
		}
	}

	@Override
	protected void run(double deltaMs) {
		for (GemEdgeColorPair edge : this.edges) {
			this.setColor(edge.edge, edge.color);
		}
	}
	
	private class GemEdgeColorPair {
		
		public final GemEdge edge;
		public int color;
		
		public GemEdgeColorPair(GemEdge e, int c) {
			this.edge = e;
			this.color = c;
		}
	}
	
}
