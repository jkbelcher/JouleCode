import heronarts.lx.LX;
import heronarts.lx.parameter.BoundedFunctionalParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.parameter.FunctionalParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.lx.parameter.LXParameter.Polarity;
import heronarts.lx.parameter.BoundedParameter.Range;

public class AudioBridgePattern extends JoulePattern {

    // User selects two channels to target
    public final DiscreteParameter aChannel = new DiscreteParameter("AChan", 1, 1, 4+1);
    public final DiscreteParameter bChannel = new DiscreteParameter("BChan", 2, 1, 4+1);
    public final CompoundParameter eqRangeMax = (CompoundParameter) new CompoundParameter("EQmax", 0.5)
            .setDescription("Maximum value for the equalizer knobs. Adjust per DJ.")
            .addListener(new LXParameterListener() {
                public void onParameterChanged(LXParameter p) {
                    eqRange = new Range(0, eqRangeMax.getValue());
                }
                });
    Range eqRange = new Range(0, 0.5);
    
    // Raw values mapped with midi
    public final CompoundParameter low1 = new CompoundParameter("low1");
    public final CompoundParameter low2 = new CompoundParameter("low2");
    public final CompoundParameter low3 = new CompoundParameter("low3");
    public final CompoundParameter low4 = new CompoundParameter("low4");
    public final CompoundParameter mid1 = new CompoundParameter("mid1");
    public final CompoundParameter mid2 = new CompoundParameter("mid2");
    public final CompoundParameter mid3 = new CompoundParameter("mid3");
    public final CompoundParameter mid4 = new CompoundParameter("mid4");
    public final CompoundParameter high1 = new CompoundParameter("high1");
    public final CompoundParameter high2 = new CompoundParameter("high2");
    public final CompoundParameter high3 = new CompoundParameter("high3");
    public final CompoundParameter high4 = new CompoundParameter("high4");
    
    public final CompoundParameter crossfader = new CompoundParameter("crossFader");
    
    public final CompoundParameter fade1 = new CompoundParameter("fade1");
    public final CompoundParameter fade2 = new CompoundParameter("fade2");
    public final CompoundParameter fade3 = new CompoundParameter("fade3");
    public final CompoundParameter fade4 = new CompoundParameter("fade4");
    public final CompoundParameter fadeMaster = new CompoundParameter("fadeMaster");
    public final CompoundParameter boothMonitor = new CompoundParameter("boothMonitor");    
    
    public final CompoundParameter colorParameter = new CompoundParameter("colorParam");
    public final CompoundParameter colorParamRangeMax = (CompoundParameter) new CompoundParameter("CPmax", 0.6)
            .setDescription("Color parameter knob sensitivity. Adjust per DJ.")
            .addListener(new LXParameterListener() {
                public void onParameterChanged(LXParameter p) {
                    colorParameterRange = new Range(0, colorParamRangeMax.getValue());
                }
                });
    Range colorParameterRange = new Range(0, 0.6);
    public final CompoundParameter color1 = (CompoundParameter)new CompoundParameter("color1").setPolarity(Polarity.BIPOLAR);
    public final CompoundParameter color2 = (CompoundParameter)new CompoundParameter("color2").setPolarity(Polarity.BIPOLAR);
    public final CompoundParameter color3 = (CompoundParameter)new CompoundParameter("color3").setPolarity(Polarity.BIPOLAR);
    public final CompoundParameter color4 = (CompoundParameter)new CompoundParameter("color4").setPolarity(Polarity.BIPOLAR);
    
    // Raw values routed to A/B values, to be mapped or used by other functional parameters
    public final BoundedFunctionalParameter lowA = new BoundedFunctionalParameter("LowA") {
        public double computeValue() { switch (aChannel.getValuei()) { 
        case 1: return eqRange.getNormalized(low1.getValue());
        case 2: return eqRange.getNormalized(low2.getValue()); 
        case 3: return eqRange.getNormalized(low3.getValue()); 
        case 4: return eqRange.getNormalized(low4.getValue()); default: return 0; } }
    };
    public final BoundedFunctionalParameter lowB = new BoundedFunctionalParameter("LowB") {
        public double computeValue() { switch (bChannel.getValuei()) { 
        case 1: return eqRange.getNormalized(low1.getValue());
        case 2: return eqRange.getNormalized(low2.getValue()); 
        case 3: return eqRange.getNormalized(low3.getValue()); 
        case 4: return eqRange.getNormalized(low4.getValue()); default: return 0; } }
    };

    public final BoundedFunctionalParameter midA = new BoundedFunctionalParameter("MidA") {
        public double computeValue() { switch (aChannel.getValuei()) { 
        case 1: return eqRange.getNormalized(mid1.getValue());
        case 2: return eqRange.getNormalized(mid2.getValue()); 
        case 3: return eqRange.getNormalized(mid3.getValue()); 
        case 4: return eqRange.getNormalized(mid4.getValue()); default: return 0; } }
    };
    public final BoundedFunctionalParameter midB = new BoundedFunctionalParameter("MidB") {
        public double computeValue() { switch (bChannel.getValuei()) { 
        case 1: return eqRange.getNormalized(mid1.getValue());
        case 2: return eqRange.getNormalized(mid2.getValue()); 
        case 3: return eqRange.getNormalized(mid3.getValue()); 
        case 4: return eqRange.getNormalized(mid4.getValue()); default: return 0; } }
    };    

    public final BoundedFunctionalParameter highA = new BoundedFunctionalParameter("HighA") {
        public double computeValue() { switch (aChannel.getValuei()) { 
        case 1: return eqRange.getNormalized(high1.getValue());
        case 2: return eqRange.getNormalized(high2.getValue()); 
        case 3: return eqRange.getNormalized(high3.getValue()); 
        case 4: return eqRange.getNormalized(high4.getValue()); default: return 0; } }
    };    
    public final BoundedFunctionalParameter highB = new BoundedFunctionalParameter("HighB") {
        public double computeValue() { switch (bChannel.getValuei()) { 
        case 1: return eqRange.getNormalized(high1.getValue());
        case 2: return eqRange.getNormalized(high2.getValue()); 
        case 3: return eqRange.getNormalized(high3.getValue()); 
        case 4: return eqRange.getNormalized(high4.getValue()); default: return 0; } }
    };    

    public final BoundedFunctionalParameter fadeA = new BoundedFunctionalParameter("FadeA") {
        public double computeValue() { switch (aChannel.getValuei()) { 
        case 1: return fade1.getValue();
        case 2: return fade2.getValue(); 
        case 3: return fade3.getValue(); 
        case 4: return fade4.getValue(); default: return 0; } }
    };   
    public final BoundedFunctionalParameter fadeB = new BoundedFunctionalParameter("FadeB") {
        public double computeValue() { switch (bChannel.getValuei()) { 
        case 1: return fade1.getValue();
        case 2: return fade2.getValue(); 
        case 3: return fade3.getValue(); 
        case 4: return fade4.getValue(); default: return 0; } }
    };   
    
    public final BoundedFunctionalParameter colorA = new BoundedFunctionalParameter("ColorA") {
        public double computeValue() { switch (aChannel.getValuei()) { 
        case 1: return color1.getValue();
        case 2: return color2.getValue(); 
        case 3: return color3.getValue(); 
        case 4: return color4.getValue(); default: return 0; } }
    }; //.setPolarity(Polarity.BIPOLAR);   
    public final BoundedFunctionalParameter colorB = new BoundedFunctionalParameter("ColorB") {
        public double computeValue() { switch (bChannel.getValuei()) { 
        case 1: return color1.getValue();
        case 2: return color2.getValue(); 
        case 3: return color3.getValue(); 
        case 4: return color4.getValue(); default: return 0; } }
    };   
    
    // Calculated values    
    public final FunctionalParameter LowANet = new FunctionalParameter("LowANet") {
        public double getValue() { return lowA.getValue() * fadeA.getValue(); }
    };
    public final FunctionalParameter LowBNet = new FunctionalParameter("LowBNet") {
        public double getValue() { return lowB.getValue() * fadeB.getValue(); }
    };

    public final FunctionalParameter MidANet = new FunctionalParameter("MidANet") {
        public double getValue() { return midA.getValue() * fadeA.getValue(); }
    };
    public final FunctionalParameter MidBNet = new FunctionalParameter("MidBNet") {
        public double getValue() { return midB.getValue() * fadeB.getValue(); }
    };

    public final FunctionalParameter HighANet = new FunctionalParameter("HighANet") {
        public double getValue() { return highA.getValue() * fadeA.getValue(); }
    };
    public final FunctionalParameter HighBNet = new FunctionalParameter("HighBNet") {
        public double getValue() { return highB.getValue() * fadeB.getValue(); }
    };

    
    public final BoundedFunctionalParameter LevelANet = new BoundedFunctionalParameter("LevelANet") {
        public double computeValue() {return Math.max(Math.max(lowA.getValue(), midA.getValue()), highA.getValue()) * fadeA.getValue(); }
    };
    public final BoundedFunctionalParameter LevelBNet = new BoundedFunctionalParameter("LevelBNet") {
        public double computeValue() { return Math.max(Math.max(lowB.getValue(), midB.getValue()), highB.getValue()) * fadeB.getValue(); }
    };
  
    public final BoundedFunctionalParameter ColorANet = new BoundedFunctionalParameter("ColorANet") {
        public double computeValue() {
            double value = colorA.getValue();
            return colorParameterRange.getNormalized((value < .5 ? (.5-value) * 2 : (value-.5) * 2) * colorParameter.getValue());
        }
    };
    public final BoundedFunctionalParameter ColorBNet = new BoundedFunctionalParameter("ColorBNet") {
        public double computeValue() {
            double value = colorB.getValue();
            return colorParameterRange.getNormalized((value < .5 ? (.5-value) * 2 : (value-.5) * 2) * colorParameter.getValue());
        }
    };
    public final BoundedFunctionalParameter XFLevel = new BoundedFunctionalParameter("XFLevel") {
        protected double lastValue = 0;
        
        public double computeValue() {            
            double levA = LevelANet.getValue();
            double levB = LevelBNet.getValue();
            if (levA != 0 || levB != 0) {
                lastValue = levA > levB ? levB / (levA + levB) : 1 - (levA / (levA + levB));
                
            }
            return lastValue;
        }
    }.setDescription("Crossfader position calculated using net levels of A vs B");

    public AudioBridgePattern(LX lx) {
        super(lx);
        
        addParameter(this.aChannel);
        addParameter(this.bChannel);
        addParameter(this.eqRangeMax);
        addParameter(this.colorParamRangeMax);
        
        // Raw values
        addParameter(this.low1);
        addParameter(this.low2);
        addParameter(this.low3);
        addParameter(this.low4);        
        addParameter(this.mid1);
        addParameter(this.mid2);
        addParameter(this.mid3);
        addParameter(this.mid4);
        addParameter(this.high1);
        addParameter(this.high2);
        addParameter(this.high3);
        addParameter(this.high4);
        addParameter(this.fade1);
        addParameter(this.fade2);
        addParameter(this.fade3);
        addParameter(this.fade4);        
        addParameter(this.fadeMaster);
        addParameter(this.crossfader);
        addParameter(this.boothMonitor);
        addParameter(this.colorParameter);
        addParameter(this.color1);
        addParameter(this.color2);
        addParameter(this.color3);
        addParameter(this.color4);
        
        // Raw values mapped to A/B
        addParameter(this.lowA);
        addParameter(this.lowB);
        addParameter(this.midA);
        addParameter(this.midB);
        addParameter(this.highA);
        addParameter(this.highB);
        addParameter(this.fadeA);
        addParameter(this.fadeB);
        addParameter(this.colorA);
        addParameter(this.colorB);

        // Calculated values
        addParameter(this.LowANet);
        addParameter(this.LowBNet);
        addParameter(this.MidANet);
        addParameter(this.MidBNet);
        addParameter(this.HighANet);
        addParameter(this.HighBNet);        
        addParameter(this.LevelANet);
        addParameter(this.LevelBNet);
        addParameter(this.ColorANet);
        addParameter(this.ColorBNet);
        addParameter(this.XFLevel);
    }

    @Override
    protected void run(double deltaMs) {
    }

}
