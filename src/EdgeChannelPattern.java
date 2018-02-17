import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;

public class EdgeChannelPattern extends JoulePattern {

	  public final DiscreteParameter channelIndex = (DiscreteParameter)
			    new DiscreteParameter("channelIndex", 0, 0, 47)
			    .setDescription("Index of the lit channel");
	  
	public EdgeChannelPattern(LX lx) {
		super(lx);
		// TODO Auto-generated constructor stub
		addParameter(channelIndex);
	}

	@Override
	protected void run(double deltaMs) {
		clearColors();
		
		int iChannel = this.channelIndex.getValuei();

		for (Gem gem : this.model.gems) {
			if (gem.params.controllerChannel == iChannel) {
				this.setColor(gem, LXColor.RED);
			}
		}

	}

}
