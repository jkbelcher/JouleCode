import java.util.ArrayList;
import java.util.List;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.audio.GraphicMeter;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.BoundedParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;

@LXCategory(LXCategory.OTHER)
public class VUMeter extends JoulePattern {

    /////////////////////////////////////////////////
    // Pass-through parameters for two GraphicMeters
    /**
     * Gain of the meter, in decibels
     */
    public final BoundedParameter gain = (BoundedParameter) new BoundedParameter("Gain", 0, -48, 48)
            .setDescription("Sets the gain of the meter in dB")
            .setUnits(LXParameter.Units.DECIBELS);

    /**
     * Range of the meter, in decibels.
     */
    public final BoundedParameter range = (BoundedParameter) new BoundedParameter("Range", 48, 6, 96)
            .setDescription("Sets the range of the meter in dB")
            .setUnits(LXParameter.Units.DECIBELS);

    /**
     * Meter attack time, in milliseconds
     */
    public final BoundedParameter attack = (BoundedParameter) new BoundedParameter("Attack", 10, 0, 100)
            .setDescription("Sets the attack time of the meter response")
            .setUnits(LXParameter.Units.MILLISECONDS);

    /**
     * Meter release time, in milliseconds
     */
    public final BoundedParameter release = (BoundedParameter) new BoundedParameter("Release", 100, 0, 1000)
            .setDescription("Sets the release time of the meter response")
            .setExponent(2)
            .setUnits(LXParameter.Units.MILLISECONDS);

    public final BoundedParameter releasePeaks = (BoundedParameter) new BoundedParameter("ReleasePeaks", 600, 0, 1000)
            .setDescription("Sets the release time of the peaks")
            .setExponent(2)
            .setUnits(LXParameter.Units.MILLISECONDS);

    /////////////////////////////////////////////////

    public final CompoundParameter hueShiftSpeed = new CompoundParameter("HueShift", 5, 0, 60)
            .setDescription("Hue shift speed");

    GraphicMeter meter;
    GraphicMeter meterPeaks;
    List<EdgeBandPair> pairs;

    float huePos = 0;

    public VUMeter(LX lx) {
        super(lx);

        addParameter(hueShiftSpeed);

        // For GraphicMeters
        addParameter(gain);
        addParameter(range);
        addParameter(attack);
        addParameter(release);
        addParameter(releasePeaks);

        initialize();

        this.gain.addListener(new LXParameterListener() {
            public void onParameterChanged(LXParameter p) {
                onGainChanged();
            }
        });
        this.range.addListener(new LXParameterListener() {
            public void onParameterChanged(LXParameter p) {
                onRangeChanged();
            }
        });
        this.attack.addListener(new LXParameterListener() {
            public void onParameterChanged(LXParameter p) {
                onAttackChanged();
            }
        });
        this.release.addListener(new LXParameterListener() {
            public void onParameterChanged(LXParameter p) {
                onReleaseChanged();
            }
        });
        this.releasePeaks.addListener(new LXParameterListener() {
            public void onParameterChanged(LXParameter p) {
                onReleasePeaksChanged();
            }
        });
    }

    void onGainChanged() {
        this.meter.gain.setValue(this.gain.getValue());
        this.meterPeaks.gain.setValue(this.gain.getValue());
    }

    void onRangeChanged() {
        this.meter.range.setValue(this.attack.getValue());
        this.meterPeaks.range.setValue(this.attack.getValue());
    }

    void onAttackChanged() {
        this.meter.attack.setValue(this.attack.getValue());
        this.meterPeaks.attack.setValue(this.attack.getValue());
    }

    void onReleaseChanged() {
        this.meter.release.setValue(this.release.getValue());
    }

    void onReleasePeaksChanged() {
        this.meterPeaks.release.setValue(this.releasePeaks.getValue());
    }

    private void initialize() {

        int numMeterBands = 40;

        this.meter = new GraphicMeter(lx.engine.audio.input.mix, numMeterBands);
        this.meterPeaks = new GraphicMeter(lx.engine.audio.input.mix, numMeterBands);

        this.meter = new GraphicMeter(lx.engine.audio.input.mix, numMeterBands);
        this.meterPeaks = new GraphicMeter(lx.engine.audio.input.mix, numMeterBands);
        this.meter.gain.setValue(this.gain.getValue());
        this.meterPeaks.gain.setValue(this.gain.getValue());
        this.range.setValue(this.meter.range.getValue());
        this.meter.attack.setValue(this.attack.getValue());
        this.meterPeaks.attack.setValue(this.attack.getValue());
        this.meter.release.setValue(this.release.getValue());
        this.meterPeaks.release.setValue(this.releasePeaks.getValue());

        startModulator(this.meter);
        startModulator(this.meterPeaks);
        // meterPeaks.release.minimum -> lock to meter.release

        // Pair every gem edge to a band in the GraphicMeter
        int numBands = this.meter.getNumBands() - 5; // leave the top ones out so charlie gems have something on them
        this.pairs = new ArrayList<EdgeBandPair>();

        for (Gem gem : model.gems) {
            for (GemEdge edge : gem.gravityMappedEdges) {
                int iBand = (int) (((float) numBands) * edge.xn);
                if (iBand != 0) {
                    iBand %= numBands;
                }
                this.pairs.add(new EdgeBandPair(edge, edge.getDirectionAntiGravity(), iBand, edge.xn));
            }
        }
    }

    @Override
    protected void run(double deltaMs) {
        this.clearColors();

        float hueShiftSpeed = this.hueShiftSpeed.getValuef();
        float hueChange = hueShiftSpeed * 100 / 60.0f / 1000.0f * ((float) deltaMs);

        this.huePos += hueChange;
        this.huePos %= 360;

        // Note: can move some of the math (calc of pixels per edge) to the subclass

        for (EdgeBandPair eb : this.pairs) {
            float percent = this.meter.getBandf(eb.iBand);
            float litPixelsf = ((float) eb.edge.getNumPoints()) / 2 * percent;
            float percentLastPixel = litPixelsf - ((int) litPixelsf);
            int litPixels = (int) litPixelsf;

            float hue = ((eb.xn * 360) + this.huePos) % 360;
            int color = LXColor.hsb(hue, 100, 100);

            for (int iPixel = 0; iPixel < litPixels; iPixel++) {
                colors[eb.edge.getPoint(iPixel, eb.direction).index] = color;
                colors[eb.edge.getPoint(iPixel, eb.oppDirection).index] = color;
            }
            if (litPixels < (eb.edge.getNumPoints() / 2)) {
                colors[eb.edge.getPoint(litPixels, eb.direction).index] = LXColor.scaleBrightness(color, percentLastPixel);
            }

            // Get peaks from a meter with a slower release
            float peakPercent = this.meterPeaks.getBandf(eb.iBand);
            float iPeakPixelf = ((float) eb.edge.getNumPoints()) / 2 * peakPercent;
            int iPeakPixel = (int) iPeakPixelf;
            if (iPeakPixel < (eb.edge.getNumPoints() / 2)) {
                iPeakPixel++;
            }
            colors[eb.edge.getPoint(iPeakPixel, eb.direction).index] = LXColor.WHITE;
            colors[eb.edge.getPoint(iPeakPixel, eb.oppDirection).index] = LXColor.WHITE;
        }
    }

    private class EdgeBandPair {
        public GemEdge edge;
        public GemEdgeDirection direction;
        public GemEdgeDirection oppDirection;
        public int iBand;
        public float xn;

        public EdgeBandPair(GemEdge edge, GemEdgeDirection direction, int iBand, float xn) {
            this.edge = edge;
            this.direction = direction;
            this.oppDirection = GemEdge.getOppositeDirection(direction);
            this.iBand = iBand;
            this.xn = xn;
        }
    }

}
