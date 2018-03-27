import java.util.List;

import heronarts.lx.LX;
import heronarts.lx.LXChannel;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.BoundedParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import processing.core.PApplet;

public class BlenderPlusPattern extends JoulePattern {

  public final CompoundParameter transitionTimeSecs = (CompoundParameter)
		    new CompoundParameter("Transition Time", 10, .1, 30)
		    .setDescription("Sets the duration of blending transitions between patterns");
  public final CompoundParameter autoCycleTimeSecs = (CompoundParameter)
		    new CompoundParameter("Cycle Time", 10, .1, 60)
		    .setDescription("Sets the number of seconds after which the channel cycles to the next pattern");

  
	protected LXChannel targetChannel;
	protected LXPattern p1;
	protected LXPattern p2;
	protected LXChannel transitionChannel;
	protected JouleBlendPattern transitionPattern;
	protected int iTransitionPattern=0;
	
	public BlenderPlusPattern(LX lx) {
		super(lx);

		addParameter(transitionTimeSecs);
		addParameter(autoCycleTimeSecs);
	}
	
	public BlenderPlusPattern setTargetChannel (LXChannel targetChannel, LXChannel transitionChannel) {

		this.targetChannel = targetChannel;
		this.transitionChannel = transitionChannel;
		
		return this;
	}
	
	public LXChannel getTargetChannel () {
		return this.targetChannel;
	}
	
	public BlenderPlusPattern initialize() {
		this.autoCycleTimeSecs.addListener(new LXParameterListener() {
	          public void onParameterChanged(LXParameter p) {
	        	  autoCycleChanged(p);
	          }
	        });
		
		isChanging = false;
		this.lastChangeTime = 0;
		calcNextChangeTime();
		this.p1 = this.targetChannel.patterns.get(3);
		this.p1.onActive();
		
		return this;
	}
	
	void autoCycleChanged(LXParameter p) {
		if (!isChanging) {
			// recalculate change time
			this.calcNextChangeTime();
		}
	}
	
	@Override
	public void onActive() {

	}
	
	void calcNextChangeTime() {
		this.nextChangeTime = this.lastChangeTime + (this.autoCycleTimeSecs.getValue() * 1000f);
	}

	double lastChangeTime=0;
	double nextChangeTime=0;
	Boolean isChanging = false;
	
	double transitionStartTime=1;
	double transitionLength=1;
	double transitionEndTime=2;
	
	protected void beginTransition() {
		this.isChanging = true;
		this.transitionStartTime = this.runMs;
		this.transitionLength = this.transitionTimeSecs.getValuef() * 1000f;
		this.transitionEndTime = this.transitionStartTime + this.transitionLength;
		
		this.p2 = getNextPattern();
		this.p2.onActive();	
		
		this.transitionPattern = this.getNextTransitionPattern();
		this.transitionPattern.initialize(transitionStartTime);
		this.transitionPattern.onActive();
	}
	
	protected void endTransition() {
		isChanging = false;
		this.lastChangeTime = this.runMs;
		this.calcNextChangeTime();
		
		this.p1.onInactive();
		this.p1 = p2;
		
		this.transitionPattern.onInactive();
	}
	

			
	@Override
	protected void run(double deltaMs) {
		
		// Are we in transition?
		if (isChanging) {
			// We are in transition.  Is it over?
			if (this.runMs > this.transitionEndTime) {
				// Transition is finished!
				this.endTransition();
			}
		} else {
			// Not in transition.  Is it time to start?
			if (this.runMs > this.nextChangeTime) {
				// Time to start a transition!
				this.beginTransition();
			}
		}
				
		this.p1.loop(deltaMs);
		int[] c1 = p1.getColors();
		
		this.clearColors();

		if (this.isChanging) {
			// In transition
			this.p2.loop(deltaMs);
			int[] c2 = p2.getColors();
			
			double transitionPercent = (this.runMs - this.transitionStartTime) / this.transitionLength;
			
			this.transitionPattern.setCurrentMs(this.runMs, (float) transitionPercent);
			this.transitionPattern.loop(deltaMs);
			int[] tmask = transitionPattern.getColors();

			for (int i = 0; i < colors.length; i++) {
				int f1 = LXColor.multiply(c1[i], LXColor.subtract(LXColor.WHITE, tmask[i]));   		//(c1[i], ).multiply(c1, c2)(c1[i], c2[i], transitionPercent);
				int f2 = LXColor.multiply(c2[i], tmask[i]);
				colors[i] = LXColor.add(f1, f2);
			}
		
		} else {
			
			// Not in transition
			// Display the current pattern only.
			for (int i = 0; i < colors.length; i++) {
				colors[i] = c1[i];
			}
		}
	}
	
	protected LXPattern getNextPattern() {
		List<LXPattern> patterns = this.targetChannel.getPatterns();
		int indexP1 = patterns.indexOf(p1);
		indexP1 = Math.max(indexP1, -1);
		
		int nextIndex = indexP1+1;
		if (nextIndex > (patterns.size()-1)) {
			nextIndex = 0;
		}
		
		// Treat all patterns as active.  Improve this later to cycle through active patterns.
		return patterns.get(nextIndex);		
	}
	
	protected JouleBlendPattern getNextTransitionPattern() {
		return getNextTransitionPatternRandom();		
	}
	
	protected JouleBlendPattern getNextTransitionPatternIncremental() {
		List<LXPattern> patterns = this.transitionChannel.getPatterns();
		
		int nextIndex = iTransitionPattern+1;
		if (nextIndex > (patterns.size()-1)) {
			nextIndex = 0;
		}
		
		// Treat all patterns as active.  Improve this later to cycle through only active patterns.
		this.iTransitionPattern = nextIndex;
		return (JouleBlendPattern) patterns.get(iTransitionPattern);		
	}
	
	protected JouleBlendPattern getNextTransitionPatternRandom() {
		List<LXPattern> patterns = this.transitionChannel.getPatterns();
		
		int nextIndex = (int) (Math.random()*patterns.size());
		nextIndex %= patterns.size();	//necessary?
		
		// Treat all patterns as active.  Improve this later to cycle through only active patterns.
		this.iTransitionPattern = nextIndex;
		return (JouleBlendPattern) patterns.get(iTransitionPattern);		
	}
	

}
