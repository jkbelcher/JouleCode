import java.util.ArrayList;
import java.util.List;
import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.LXColor;
import heronarts.lx.effect.StrobeEffect.Waveshape;
import heronarts.lx.modulator.LXWaveshape;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.EnumParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;

// Bubbles is (c)Copyright Justin Belcher 2017
@LXCategory(LXCategory.OTHER)
public class BubblesPattern extends JoulePattern {

    public final CompoundParameter density = 
            new CompoundParameter("Density", .2, 0.05, .45)
            .setDescription("Density of bubbles");
    
    public final CompoundParameter minBubbleSpeed = 
            new CompoundParameter("MinSpeed", 5, 3, 50)
            .setDescription("Minimum pixel moves per second");
    
    public final CompoundParameter maxBubbleSpeed = 
            new CompoundParameter("MaxSpeed", 60, 5, 75)
            .setDescription("Maximum pixel moves per second");
    
    public enum BubbleColorMode {
        RAINBOW,
        SOLID
      };

    public final EnumParameter<BubbleColorMode> colorMode =
        new EnumParameter<BubbleColorMode>("ClrMode", BubbleColorMode.RAINBOW)
        .setDescription("Color mode for new bubbles");
    
    public final BooleanParameter beat = 
            new BooleanParameter("Beat")
            .setDescription("Link beat detect to this button to release bubbles on beat.")
            .setMode(BooleanParameter.Mode.MOMENTARY);

    // .addListener(this.beatListener);
    private List<EdgeBubbleCollection> edges;

    public BubblesPattern(LX lx) {
        super(lx);

        addParameter(density);
        addParameter(minBubbleSpeed);
        addParameter(maxBubbleSpeed);
        addParameter(colorMode);
        // addParameter(beat);

        this.beat.addListener(new LXParameterListener() {
            public void onParameterChanged(LXParameter p) {
                onBeatPressed(p);
            }
        });
    }

    public void onActive() {
        this.setRandomParameters();
        this.safetyCheckParameters();
        initialize();
    }
    
    public void setRandomParameters() {
        randomizeParameter(this.density);
        randomizeParameter(this.minBubbleSpeed);
        randomizeParameter(this.maxBubbleSpeed, this.minBubbleSpeed.getValue(), this.maxBubbleSpeed.range.max);
        randomizeParameter(this.colorMode);
    }
    
    void safetyCheckParameters() {
        if (this.maxBubbleSpeed.getValuef() <= this.minBubbleSpeed.getValuef() + 1) {
            this.maxBubbleSpeed.setValue(this.minBubbleSpeed.getValue() + 5);
        }
    }
    
    int bubbleColorSolid;

    private void initialize() {
        this.edges = new ArrayList<EdgeBubbleCollection>();

        float density = this.density.getValuef();
        
        bubbleColorSolid = getRandomColor();

        for (Gem gem : this.model.gems) {
            for (GemEdge gme : gem.gravityMappedEdges) {
                EdgeBubbleCollection c = new EdgeBubbleCollection();
                c.edge = gme;
                c.edgeDirection = gme.getDirectionRandom();
                c.maxPos = gme.getNumPoints() - 1;
                int numBubbles = (int) (((float) gme.getNumPoints()) * density);
                for (int b = 0; b < numBubbles; b++) {
                    Bubble newBubble = createBubble();
                    c.bubbles.add(newBubble);
                }

                this.edges.add(c);
            }
        }
    }

    private Bubble createBubble() {
        Bubble b = new Bubble();
        
        switch (this.colorMode.getEnum()) {
            case RAINBOW: b.color = getRandomColor(); break;
            case SOLID: b.color = this.bubbleColorSolid; break;
        };
        
        b.pos = 0;

        this.safetyCheckParameters();
        float minSpeed = this.minBubbleSpeed.getValuef();
        float maxSpeed = this.maxBubbleSpeed.getValuef();
        float speedRange = Math.max(maxSpeed - minSpeed, 1);
        float pixelsPerSec = (float) ((Math.random() * speedRange) + minSpeed);

        b.timePerMove = 1000f / pixelsPerSec;
        b.nextMoveTime = this.runMs + b.timePerMove;

        return b;
    }

    Boolean beatDetected = false;

    public void onBeatPressed(LXParameter p) {
        this.beatDetected = true;
    }

    @Override
    protected void run(double deltaMs) {
        this.clearColors();

        // Foreach bubble: shift position if it's time. Create new bubble if it's beyond max position.
        for (EdgeBubbleCollection ebc : this.edges) {

            List<Bubble> expiredBubbles = new ArrayList<Bubble>();
            List<Bubble> newBubbles = new ArrayList<Bubble>();

            for (Bubble bubble : ebc.bubbles) {
                // Is it time to increment?
                if (this.runMs > bubble.nextMoveTime) {
                    bubble.pos++;
                    bubble.nextMoveTime = this.runMs + bubble.timePerMove;
                    if (bubble.pos > ebc.maxPos) {
                        expiredBubbles.add(bubble);
                        //newBubbles.add(createBubble());
                    }
                }
            }

            // Remove expired bubbles
            for (Bubble expiredBubble : expiredBubbles) {
                ebc.bubbles.remove(expiredBubble);
            }
            
            // Create new bubbles
            // *Could change this to calculate density across whole structure, and add to random group.
            int targetNumBubbles = (int)(this.density.getValuef() * (float)ebc.edge.getNumPoints());
            int numNewBubbles = targetNumBubbles - ebc.bubbles.size();
            for (int n = 0; n < numNewBubbles; n++) {
                ebc.bubbles.add(createBubble());
            }
            
            // Render every bubble
            for (Bubble bubble : ebc.bubbles) {
                colors[ebc.edge.getPoint(bubble.pos, ebc.edgeDirection).index] = bubble.color;
            }
        }
    }

    public class Bubble {
        public int pos;
        public double timePerMove;
        public int color;
        public double nextMoveTime;
    }

    public class EdgeBubbleCollection {
        public GemEdge edge;
        GemEdgeDirection edgeDirection;
        public int maxPos;
        public List<Bubble> bubbles = new ArrayList<Bubble>();
    }

}
