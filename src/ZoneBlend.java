import java.util.ArrayList;

import heronarts.lx.LX;
import heronarts.lx.blend.AddBlend;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.EnumParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;

public class ZoneBlend extends JouleBlend {

    public enum JouleZone {
        Lower,
        Middle,
        Upper,
        Charlie,
        GoGo,
        Sign
      }

    @SuppressWarnings("unchecked")
    public final EnumParameter<JouleZone> zone = (EnumParameter<JouleZone>) new EnumParameter<JouleZone>("Zone", JouleZone.Lower)
        .setDescription("Sets the target zone for this blend")
        .addListener(new LXParameterListener() {
            public void onParameterChanged(LXParameter p) {
                buildZones();
            }
            });

    AddBlend addBlend;

    // *Can switch these to lists of LXPoints if Gem isn't precise enough.
    protected ArrayList<Gem> inZone = new ArrayList<Gem>();
    protected ArrayList<Gem> outZone = new ArrayList<Gem>();

    public ZoneBlend(LX lx) {
        super(lx);
        
        this.addBlend = new AddBlend(lx);

        addParameter(zone);
        buildZones();        
    }
    
    @Override
    public String getName() {
        // Removes "Zone" from the end of the name.  Only works with child classes.
        
        String name = super.getName();
        
        if (!name.equals("Zone") && name.endsWith("Zone")) {
            name = name.substring(0, name.length() - 4);
        }
        return name;
    }
    
    protected void buildZones() {
        inZone.clear();
        outZone.clear();

        JouleZone zone = this.zone.getEnum();

        switch (zone) {
        case Charlie:
            for (Gem gem : this.model.gems) {
                if (gem.params.gemType.equals("charlie")) {
                    inZone.add(gem);
                } else {
                    outZone.add(gem);
                }                
            }
            break;
        case GoGo:
            for (Gem gem : this.model.gems) {
                if (gem.params.gemType.equals("gogo")) {
                    inZone.add(gem);
                } else {
                    outZone.add(gem);
                }                
            }
            break;
        case Sign:
            for (Gem gem : this.model.gems) {
                if (gem.params.gemType.equals("sign")) {
                    inZone.add(gem);
                } else {
                    outZone.add(gem);
                }                
            }
            break;
        case Lower:
            for (Gem gem : this.model.gems) {
                if (gem.params.clusterName.substring(1, 2).equals("L")) {
                    inZone.add(gem);
                } else {
                    outZone.add(gem);
                }                
            }
            break;
        case Middle:
            for (Gem gem : this.model.gems) {
                if (gem.params.clusterName.substring(1, 2).equals("M")) {
                    inZone.add(gem);
                } else {
                    outZone.add(gem);
                }                
            }
            break;
        case Upper:
            for (Gem gem : this.model.gems) {
                if (gem.params.clusterName.substring(1, 2).equals("U")) {
                    inZone.add(gem);
                } else {
                    outZone.add(gem);
                }                
            }
            break;
        default:
            break;
        }
    }

    @Override
    public void lerp(int[] from, int[] to, double amt, int[] output) {                
        // This blend is designed to be a channelBlend, but at full blend it should fully mask the zone
        amt = Math.min(1, amt * 2);
        
        int[] dst, src;
        double alpha;
        if (amt <= 0.5) {
          dst = from;
          src = to;
          alpha = amt * 2.;
        } else {
          dst = to;
          src = from;
          alpha = (1-amt) * 2.;
        }

        for (Gem gem : inZone) {
            for (LXPoint p : gem.getPoints()) {
                output[p.index] = LXColor.add(dst[p.index], src[p.index], alpha);
            }
        }
        for (Gem gem : outZone) {
            for (LXPoint p : gem.getPoints()) {
                output[p.index] = from[p.index];
            }
        }
    }
    
}
