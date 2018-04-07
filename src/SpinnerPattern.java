import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.transform.LXProjection;
import heronarts.lx.transform.LXTransform;
import heronarts.lx.transform.LXVector;

public class SpinnerPattern extends JoulePattern {

    private static final float RADIANS_PER_REVOLUTION = 2.0f;

    public final CompoundParameter spinRPM = 
            new CompoundParameter("Spin", 20, 0, 120)
            .setDescription("Spin Speed");
    
    public final CompoundParameter spinRPM2 = 
            new CompoundParameter("Spin2", 0, 0, 120)
            .setDescription("Other Spin Speed");
    
    public final CompoundParameter maxRandSpin = 
            new CompoundParameter("MaxRandSpin", 20, 2, 60)
            .setDescription("Maximum random spin speed");    

    private final LXTransform transform = new LXTransform();
    private float positionSpin = 0;
    private float positionSpin2 = 0;

    public SpinnerPattern(LX lx) {
        super(lx);

        addParameter(spinRPM);
        addParameter(spinRPM2);
        addParameter(maxRandSpin);
        
        this.projection = new LXProjection(this.model);
        this.projection.translate(0-this.model.cx, 0-this.model.cy, 0-this.model.cz);
    }

    LXProjection projection;
    
    public void onActive() {
        this.setRandomParameters();
    }
    
    public void setRandomParameters() {
        randomizeParameter(this.spinRPM, 1, this.maxRandSpin.getValue());
        randomizeParameter(this.spinRPM2, 1, this.maxRandSpin.getValue());
    }

    @Override
    protected void run(double deltaMs) {

        float spinRPM = this.spinRPM.getValuef();
        float spinRPM2 = this.spinRPM2.getValuef();
        float spinChange = spinRPM * RADIANS_PER_REVOLUTION / 60.0f / 1000.0f * ((float) deltaMs);
        float spinChange2 = spinRPM2 * RADIANS_PER_REVOLUTION / 60.0f / 1000.0f * ((float) deltaMs);

        this.projection.rotateY(spinChange);
        this.projection.rotateZ(spinChange2);

        // this.positionSpin += spinChange;
        // this.positionSpin %= 360.0;
        for (LXVector v : this.projection) {

            // float theta = (float) ((LX.TWO_PI + Math.atan2(v.y, v.x)) % (LX.TWO_PI));
            float azimuth = (float) ((LX.TWO_PI + Math.atan2(v.z, v.x)) % (LX.TWO_PI));
            // float elevation = (float) ((LX.TWO_PI + Math.atan2(v.y, v.rxz)) % (LX.TWO_PI));

            float pDegrees = (float) Math.toDegrees(azimuth);
            colors[v.index] = LXColor.hsb(pDegrees, 100, 100);
        }
    }

    /*
     * LXTransform t2 = new LXTransform();
     * 
     * @Override protected void run(double deltaMs) {
     * 
     * float spinRPM = this.spinRPM.getValuef(); this.positionSpin +=
     * spinRPM*RADIANS_PER_REVOLUTION/60.0/1000.0*deltaMs; this.positionSpin %=
     * 360.0;
     * 
     * this.transform.push();
     * this.transform.rotateY(Math.toRadians(positionSpin));
     * 
     * LXMatrix matrix = transform.getMatrix();
     * 
     * for (LXPoint p : model.points) {
     * 
     * t2.push(); t2.translate(p.x, p.y, p.z); LXMatrix pointMatrix =
     * t2.getMatrix();
     * 
     * LXMatrix finalMatrix = matrix.multiply(pointMatrix); float x =
     * finalMatrix.x(); float y = finalMatrix.y();
     * 
     * 
     * float pDegrees = (float) Math.toDegrees(Math.atan(x/y)); LXVector;
     * LXProjection;
     * 
     * 
     * colors[p.index] = LXColor.hsb(pDegrees, 100, 100);
     * 
     * 
     * t2.pop(); }
     * 
     * this.transform.pop(); }
     */
}
