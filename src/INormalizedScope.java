import java.util.List;

public interface INormalizedScope {

    public List<NormalizedPoint> getPointsNormalized();
    
    public NormalScope getNormalScope();
    
    public int countChildScopes();
    
    public List<INormalizedScope> getChildScope(int index);
    
    // Helper function    
    public static class NormalizedScopeUtils {        
        public static List<INormalizedScope> getChildScopeRandom(INormalizedScope scope) {
            int countChildScopes = scope.countChildScopes();
            if (countChildScopes == 0)
                return null;
            else
                return scope.getChildScope((int) (Math.random() * ((float)countChildScopes)));
        }        
    }
    
}
