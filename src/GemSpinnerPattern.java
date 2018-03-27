import java.util.ArrayList;
import java.util.List;

import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;
import heronarts.lx.transform.LXProjection;

// Incomplete, started but unfinished for BM2017.
public class GemSpinnerPattern extends JoulePattern {

    List<SpinningGem> spinners;

    public GemSpinnerPattern(LX lx) {
        super(lx);

        initialize();
    }

    void initialize() {
        this.spinners = new ArrayList<SpinningGem>();

        for (Gem gem : this.model.gems) {
            SpinningGem sg = new SpinningGem();
            sg.setGem(gem);
            this.spinners.add(sg);
        }
    }

    @Override
    protected void run(double deltaMs) {
        // TODO Auto-generated method stub
    }

    public class SpinningGem {
        public Gem gem;
        public LXProjection projection;
        public float hueOffset;
        public List<SpinPoint> spinPoints;

        public void setGem(Gem gem) {
            this.gem = gem;

            this.spinPoints = new ArrayList<SpinPoint>();

            LXPoint z = this.gem.getPoint(0);
            float minX = z.x;
            float maxX = z.x;
            float minZ = z.z;
            float maxZ = z.z;

            for (LXPoint p : this.gem.getPoints()) {
                minX = Math.min(minX, p.x);
                minX = Math.min(minX, p.x);
                minX = Math.min(minX, p.x);
                minX = Math.min(minX, p.x);
                minX = Math.min(minX, p.x);
                minX = Math.min(minX, p.x);
            }

        }
    }

    public class SpinPoint {
        public LXPoint point;
        public float baseHue;
        public float hue;
    }
}
