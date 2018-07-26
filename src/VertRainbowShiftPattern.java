import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.CompoundParameter;

@LXCategory(LXCategory.OTHER)
public class VertRainbowShiftPattern extends JoulePattern {

    public final CompoundParameter hueRange = 
            new CompoundParameter("hueRange", 128, 5, 400).setDescription("hueRange");
    
    public final CompoundParameter speed = 
            new CompoundParameter("Speed", .14, .001, 1.0)
            .setDescription("Speed in full range shifts per second");

    float huePos = 0;

    public VertRainbowShiftPattern(LX lx) {
        super(lx);

        addParameter(hueRange);
        addParameter(speed);
    }

    @Override
    protected void run(double deltaMs) {

        float hueRange = this.hueRange.getValuef();

        // Calc current position
        float speed = this.speed.getValuef();
        float degreesMoved = hueRange * speed * (((float) deltaMs) / 1000f);

        huePos += degreesMoved;
        huePos %= 360;

        for (Gem gem : this.model.gems) {
            for (GemEdge edge : gem.gravityMappedEdges) {
                if (edge.directionMapped == GemEdgeDirection.DOWNUP) {
                    int numPixels = edge.getNumPoints();
                    float numPixelsf = (float) numPixels;
                    float degreesPerPixel = (hueRange / numPixelsf);

                    for (int i = 0; i < numPixels; i++) {
                        float hue = ((degreesPerPixel * ((float) i)) + huePos) % 360;
                        colors[edge.getPoint(i, edge.getDirectionAntiGravity()).index] = LXColor.hsb(hue, 100, 100);
                    }
                } else {
                    // **Note: should pre-load these values and not calculate them each frame.
                    float numPixelsf;
                    float degreesPerPixel;
                    float hue;

                    if (gem.params.gemType.equals("alpha")) {
                        numPixelsf = 27;
                        degreesPerPixel = (hueRange / numPixelsf);
                        hue = ((degreesPerPixel * 21.5f) + huePos) % 360;
                    } else if (gem.params.gemType.equals("bravo")) {
                        numPixelsf = 18;
                        degreesPerPixel = (hueRange / numPixelsf);
                        hue = ((degreesPerPixel * 15.5f) + huePos) % 360;
                    } else if (gem.params.gemType.equals("charlie")) {
                        numPixelsf = 68;
                        degreesPerPixel = (hueRange / numPixelsf);
                        hue = ((degreesPerPixel * 54.5f) + huePos) % 360;
                    } else {
                        numPixelsf = 0f;
                        degreesPerPixel = 0f;
                        hue = 0f;
                    }

                    int numPixels = edge.getNumPoints();
                    for (int i = 0; i < numPixels; i++) {
                        colors[edge.getPoint(i, edge.getDirectionAntiGravity()).index] = LXColor.hsb(hue, 100, 100);
                    }
                }
            }
        }
    }

}
