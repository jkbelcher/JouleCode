import heronarts.lx.LX;

public abstract class NormalizedScopeBlend extends FixedFadeBlend {

    public NormalizedScopeBlend(LX lx) {
        super(lx);
        this.scope = this.model;
    }

    protected INormalizedScope scope;
    
    public INormalizedScope getScope(NormalScope scope) {
        return this.scope;
    }
    
    public NormalizedScopeBlend setScope(INormalizedScope scope) {
        this.scope = scope;
        return this;
    }

}
