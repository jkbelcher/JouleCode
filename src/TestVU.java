import java.util.ArrayList;
import java.util.List;

import heronarts.lx.LX;
import heronarts.lx.audio.GraphicMeter;
import heronarts.lx.color.LXColor;

public class TestVU extends JoulePattern {

    GraphicMeter meter;
    List<EdgeBandPair> pairs;

    public TestVU(LX lx) {
        super(lx);

        this.meter = lx.engine.audio.meter;
        this.initialize();
    }

    private void initialize() {
        this.pairs = new ArrayList<EdgeBandPair>();

        int numBands = this.meter.getNumBands() - 4;
        int minBand = 2;
        float degreesPerBand = 360 / (numBands - minBand);

        int iBand = 6;

        for (Gem gem : model.gems) {
            for (GemEdge edge : gem.gravityMappedEdges) {
                int color = LXColor.hsb(degreesPerBand * iBand - minBand, 100, 100);
                this.pairs.add(new EdgeBandPair(edge, edge.getDirectionAntiGravity(), iBand, color));
                iBand++;
                if (iBand == numBands) {
                    iBand = 2;
                }
            }
        }
    }

    @Override
    protected void run(double deltaMs) {
        this.clearColors();

        for (EdgeBandPair eb : this.pairs) {
            float percent = this.meter.getBandf(eb.iBand);
            // float percent = this.meter.getSquaref(eb.iBand);
            float litPixelsf = ((float) eb.edge.getNumPoints()) * percent;
            float percentLastPixel = litPixelsf - ((int) litPixelsf);
            int litPixels = (int) (((float) eb.edge.getNumPoints()) * percent);
            for (int iPixel = 0; iPixel < litPixels; iPixel++) {
                colors[eb.edge.getPoint(iPixel, eb.direction).index] = eb.color;
            }
            if (litPixels < eb.edge.getNumPoints()) {
                colors[eb.edge.getPoint(litPixels, eb.direction).index] = LXColor.scaleBrightness(eb.color,
                        percentLastPixel);
            }
        }
    }

    private class EdgeBandPair {
        public GemEdge edge;
        public GemEdgeDirection direction;
        public int iBand;
        public int color;

        public EdgeBandPair(GemEdge edge, GemEdgeDirection direction, int iBand, int color) {
            this.edge = edge;
            this.direction = direction;
            this.iBand = iBand;
            this.color = color;
        }
    }

}
