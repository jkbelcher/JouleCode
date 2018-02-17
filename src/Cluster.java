import java.util.ArrayList;
import java.util.List;

import heronarts.lx.model.LXAbstractFixture;
import processing.core.PApplet;

public class Cluster extends LXAbstractFixture {

	public final ClusterParameters params;
	public final List<Gem> gems = new ArrayList<Gem>();
		
	public Cluster (ClusterParameters params) {
		this.params = params;
		PApplet.println(" Cluster",this.params.name);
	}
	
	public void AddGem (Gem gem) {
		this.gems.add(gem);
		this.addPoints(gem);
	}
	
}
