import java.util.List;

public interface INormalized {

    public List<NormalizedPoint> getPointsNormalized();
    
    public NormalScope getNormalScope();
    
    public boolean hasChildScopes();
    
}
