import java.util.ArrayList;
import java.util.List;

import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;

public class OneGemBlendPattern extends JouleBlendPattern {

    public final CompoundParameter gemLen = 
            new CompoundParameter("GemLen", .15, .01, 1)
            .setDescription("Percent of time spent fading each gem.");

    public OneGemBlendPattern(LX lx) {
        super(lx);

        addParameter(gemLen);
    }

    List<OneGemBlend> blends;

    @Override
    public void onActive() {
        blends = new ArrayList<OneGemBlend>();
        float lenPercent = this.gemLen.getValuef();

        for (Gem gem : this.model.gems) {
            OneGemBlend newBlend = new OneGemBlend();
            newBlend.gem = gem;

            newBlend.lenPercent = lenPercent;
            newBlend.startPercent = (float) (Math.random() * (1 - newBlend.lenPercent));
            newBlend.endPercent = newBlend.startPercent + newBlend.lenPercent;

            this.blends.add(newBlend);
        }
    }

    @Override
    protected void run(double deltaMs) {
        // Use currentMs instead of runMs!!

        for (OneGemBlend blend : this.blends) {
            int gemColor;
            if (this.percentComplete < blend.startPercent) {
                gemColor = LXColor.BLACK;
            } else if (this.percentComplete > blend.endPercent) {
                gemColor = LXColor.WHITE;
            } else {
                // Partially transitioned gem
                gemColor = LXColor.scaleBrightness(LXColor.WHITE,
                        (this.percentComplete - blend.startPercent) / blend.lenPercent);
            }
            for (LXPoint p : blend.gem.getPoints()) {
                colors[p.index] = gemColor;
            }
        }
    }

    public class OneGemBlend {
        public Gem gem;
        public float startPercent;
        public float lenPercent;
        public float endPercent;
    }

}
