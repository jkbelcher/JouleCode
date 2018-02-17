import java.util.ArrayList;
import java.util.List;

import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.CompoundParameter;

public class GemEdgePattern extends JoulePattern {

	public final CompoundParameter flipsPerSec = 
			new CompoundParameter("FlipPerSec", 150, 2, 300)
			.setDescription("Gem edge color changes per second");
	public final CompoundParameter secBetweenColors = 
			new CompoundParameter("HoldTime", 2, 0, 5)
			.setDescription("How many seconds to hold a color before starting to flip.");
	
	
	private List<GemEdgeColorPair> edges;
	private List<GemEdgeColorPair> edgesToChange;
	
	int currentColor;
	int nextColor;
	
	public GemEdgePattern(LX lx) {
		super(lx);
		
		this.addParameter(flipsPerSec);
		this.addParameter(secBetweenColors);
	}
	
	void initialize() {
		edges = new ArrayList<GemEdgeColorPair>();
		currentColor = getRandomColor();
		nextColor = getRandomColor();
		
		for (Cluster cluster : this.model.clusters) {
			for (Gem g : cluster.gems) {
				for (GemEdge ge : g.edges) {
					this.edges.add(new GemEdgeColorPair(ge, currentColor));
				}
			}
		}
		
		startNextColor();
	}
	
	void startNextColor() {
		edgesToChange = new ArrayList<GemEdgeColorPair>(this.edges);
		
		nextColor = getRandomColor();
		
		float secBetweenColors = this.secBetweenColors.getValuef();
		this.nextFlipTime = this.runMs + (secBetweenColors*1000f);
	}
	
	public void onActive() {
		this.initialize();
	}

	double nextFlipTime=0;
	
	@Override
	protected void run(double deltaMs) {

		// Time to switch to a new color?
		if (this.runMs > nextFlipTime) {
			// Flip/Change an edge color
			GemEdgeColorPair edge = getRandomRemainingEdge();
			edge.color = nextColor;
			edgesToChange.remove(edge);
			
			if (this.edgesToChange.size() == 0) {
				startNextColor();
				// it will set next flip time
			} else {
				// Set next flip time
				float flipsPerSec = this.flipsPerSec.getValuef();
				float msUntilNextFlip = 1000f / flipsPerSec;
				this.nextFlipTime = this.runMs + msUntilNextFlip;
			}
		}
		
		for (GemEdgeColorPair edge : this.edges) {
			this.setColor(edge.edge, edge.color);
		}

	}
	
	GemEdgeColorPair getNextEdgeToFlip() {
		return getRandomRemainingEdge();
	}
	
	GemEdgeColorPair getRandomEdge() {
		int iEdge = (int) (Math.random()*edges.size());
		iEdge %= edges.size();
		return this.edges.get(iEdge);
	}
	
	GemEdgeColorPair getRandomRemainingEdge() {
		int iEdge = (int) (Math.random()*edgesToChange.size());
		iEdge %= edges.size();
		
		return this.edgesToChange.get(iEdge);
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

