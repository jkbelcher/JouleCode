import java.util.ArrayList;
import java.util.List;
import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;

// Bubbles is (c)Copyright Justin Belcher 2017
public class BubblesPattern extends JoulePattern {

    public final CompoundParameter density = 
            new CompoundParameter("Density", .2, 0, 1)
            .setDescription("Density of bubbles");
    
    public final CompoundParameter minBubbleSpeed = 
            new CompoundParameter("MinSpeed", 1, 5, 100)
            .setDescription("Minimum pixel moves per second");
    
    public final CompoundParameter maxBubbleSpeed = 
            new CompoundParameter("MaxSpeed", 60, 5, 150)
            .setDescription("Maximum pixel moves per second");
    
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
        // addParameter(beat);

        this.beat.addListener(new LXParameterListener() {
            public void onParameterChanged(LXParameter p) {
                onBeatPressed(p);
            }
        });
    }

    public void onActive() {
        initialize();
    }

    private void initialize() {
        this.edges = new ArrayList<EdgeBubbleCollection>();

        float density = this.density.getValuef();

        for (Gem gem : this.model.gems) {
            for (GemEdge gme : gem.gravityMappedEdges) {
                EdgeBubbleCollection c = new EdgeBubbleCollection();
                c.edge = gme;
                c.edgeDirection = (Math.random() < 0.5) ? gme.getDirectionAntiGravity() : GemEdge.getOppositeDirection(gme.getDirectionAntiGravity());
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
        b.color = LXColor.hsb(Math.random() * 360.0, 100, 100);
        b.pos = 0;

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
                        newBubbles.add(createBubble());
                    }
                }
            }

            /* // This might work but I removed it when troubleshooting another problem.
            Iterator<Bubble> bubIterator = ebc.bubbles.iterator();
            while (bubIterator.hasNext()) {
                Bubble bubble = bubIterator.next();
                PApplet.println("bubble"); // Is it time to increment?
                if (this.runMs > bubble.nextMoveTime) {
                    PApplet.println("incrementing bubble");
                    bubble.pos++;
                    bubble.nextMoveTime = this.runMs + bubble.timePerMove;
                    if (bubble.pos > ebc.maxPos) {
                        bubIterator.remove();
                        newBubbles.add(createBubble());
                    }
                }
            }
            */

            for (Bubble expiredBubble : expiredBubbles) {
                ebc.bubbles.remove(expiredBubble);
            }
            for (Bubble newBubble : newBubbles) {
                ebc.bubbles.add(newBubble);
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
