import heronarts.lx.LX;

// This is a special type of pattern.
// It returns a black & white frame to be used as a mask during a transition.
// It gets initialized with some extra information for calculating the transition.

public abstract class JouleBlendPattern extends JoulePattern {

	public JouleBlendPattern(LX lx) {
		super(lx);
	}

	public JouleBlendPattern initialize(double startMs, double endMs) {
		this.startMs = startMs;
		this.currentMs = startMs;
		this.endMs = endMs;

		return this;
	}
	
	protected double startMs = 0;
	protected double currentMs = 0;
	protected double endMs = 0;
	protected float percentComplete = 0f;
	protected float percentRemaining = 1f;
	
	// Called by blender before every frame calculation.
	public JouleBlendPattern setCurrentMs(double currentMs, float percentComplete) {
		this.currentMs = currentMs;
		this.percentComplete = percentComplete;
		this.percentRemaining = 1f - percentComplete;
		return this;
	}

}
