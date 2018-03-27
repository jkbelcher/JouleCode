import processing.core.PApplet;

public class GemType {

    public final GemTypeParameters params;

    public float topSquare() { return this.params.topSquare; }
    public float bottomSquare() { return this.params.bottomSquare; }
    public float heightSquares() { return this.params.heightSquares; }

    public GemType(GemTypeParameters p) {
        this.params = p;

        PApplet.println("GemType", p.gemType + ": " + p.topSquare + ", " + p.bottomSquare + ", " + p.heightSquares);
    }

}
