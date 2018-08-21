import heronarts.lx.LX;

public class SignZoneBlend extends ZoneBlend {

    public SignZoneBlend(LX lx) {
        super(lx);
        
        super.zone.setValue(JouleZone.Sign);
    }

}
