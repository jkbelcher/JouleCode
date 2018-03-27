import heronarts.lx.LX;
import heronarts.lx.color.LXColor;

public class GemEdgeOrderAssistPattern extends JoulePattern {

    public GemEdgeOrderAssistPattern(LX lx) {
        super(lx);
    }

    @Override
    protected void run(double deltaMs) {
        for (Gem gem : this.model.gems) {
            for (GemEdge edge : gem.edges) {
                int edgePos = edge.id;
                int color;
                int litPixels = (edgePos % 4);
                litPixels = litPixels > 0 ? litPixels : 4;

                if (edgePos <= 4) {
                    color = LXColor.RED;
                    // litPixels = edgePos;
                } else if (edgePos <= 8) {
                    color = LXColor.GREEN;
                    // litPixels = edgePos - 4;
                } else if (edgePos <= 12) {
                    color = LXColor.BLUE;
                    // litPixels = edgePos - 8;
                } else {
                    color = LXColor.scaleBrightness(LXColor.GREEN, 0.5f);
                    // litPixels = edgePos - 12;
                }

                if (litPixels <= edge.getNumPoints()) {
                    for (int i = 0; i < litPixels; i++) {
                        colors[edge.getPoint(i, edge.getDirectionAntiGravity()).index] = color;
                    }
                } else {
                    int doublePixels = 0;
                    while (litPixels > 1) {
                        doublePixels += 1;
                        litPixels -= 2;
                    }

                    int iPixel = 0;
                    for (int i = 0; i < doublePixels; i++) {
                        colors[edge.getPoint(iPixel++, edge.getDirectionAntiGravity()).index] = LXColor.WHITE;
                    }
                    for (int i = 0; i < litPixels; i++) {
                        colors[edge.getPoint(iPixel++, edge.getDirectionAntiGravity()).index] = color;
                    }
                }
            }
        }
    }

}
