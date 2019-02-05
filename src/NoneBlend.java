import heronarts.lx.LX;

public class NoneBlend extends JouleBlend {

    public NoneBlend(LX lx) {
        super(lx);
    }

    @Override
    public void lerp(int[] from, int[] to, double amt, int[] output) {
        //output = from;
        for (int i = 0; i < from.length; i++) {
            output[i] = from[i];
        }
    }
}
