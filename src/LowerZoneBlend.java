import heronarts.lx.LX;

public class LowerZoneBlend extends ZoneBlend {

    public LowerZoneBlend(LX lx) {
        super(lx);
        
        super.zone.setValue(JouleZone.Lower);
    }

}
