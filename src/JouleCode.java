import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.output.LXDatagramOutput;
import heronarts.p3lx.LXStudio;
import processing.core.PApplet;
import processing.event.KeyEvent;

//Keyboard shortcuts:
// Ctrl + Q = Quit
// Ctrl + H = Help Mode On (turns background color to red to reveal unmapped pixels)
// Ctrl + J = Help Mode Off

public class JouleCode extends PApplet {

    // Let's work in inches
    public final static float INCHES = 1;
    public final static float FEET = 12 * INCHES;

    public static JouleCode applet;

    // Top-level, we have a model and an LXStudio instance
    JouleModel model;
    LXStudio lx;

    public static final List<LEDScapeDatagram> datagrams = new ArrayList<LEDScapeDatagram>();

    public static void main(String[] args) {
        // PApplet.main("JouleCode");
        // PApplet.main(new String[] { "--present", "JouleCode" });
        PApplet.main(new String[] { "--present", JouleCode.class.getName() });
        // PApplet.main(new String[] { "--present", JouleCode.class.getName(),
        // "--display=2"});
    }

    public void settings() {
        size(displayWidth, displayHeight, P3D);
        pixelDensity(displayDensity());
        /*
         * if (frame != null) { frame.setResizable(true); }
         */
    }

    public void setup() {
        JouleCode.applet = this;

        // Monitor key events for Ctrl+Q = Quit, Ctrl+H = Help, etc.
        registerMethod("keyEvent", this);

        // Create the model, which describes where our light points are
        println("Loading config from file...");
        try {
            model = JouleModel.LoadConfigurationFromFile();
            PApplet.println("Loaded", model.controllers.size() + " controllers,", model.clusters.size() + " clusters,",
                    model.gems.size() + " gems,", "and " + model.points.length + " pixels.");
        } catch (Exception e) {
            PApplet.println("Failure while loading Joule configuration from file.");
            e.printStackTrace();
            exit();
        }
        println("...finished loading config.");

        // Create the P3LX engine
        // Third parameter=true starts in Multi-threaded mode
        lx = new LXStudio(this, model, true) {
            @Override
            protected void initialize(LXStudio lx, LXStudio.UI ui) {
                // Add custom LXComponents or LXOutput objects to the engine here, before the UI is constructed

                lx.registerPattern(SimpleChasePattern.class);
                lx.registerPattern(GemEdgePattern.class);
                lx.registerPattern(SpinnerPattern.class);
                lx.registerPattern(VUMeter.class);
                lx.registerPattern(BubblesPattern.class);
                lx.registerPattern(VertRainbowShiftPattern.class);

                // Cast the model to access model-specific properties from within this overridden initialize() function.
                JouleModel m = (JouleModel) model;

                try {
                    // Foreach controller
                    for (BeagleboneController controller : m.controllers) {
                        LEDScapeDatagram datagram;
                        datagram = (LEDScapeDatagram) new LEDScapeDatagram(controller.params.numberOfChannels,
                                controller.params.LEDsPerChannel, controller).setAddress(controller.params.ipAddress)
                                        .setPort(7890);
                        JouleCode.datagrams.add(datagram);
                    }

                    // DEPRICATED: TCP output
                    // LEDScapeOutput output = new LEDScapeOutput(lx, "192.168.111.211", 7890, controller.params.numberOfChannels, controller.params.LEDsPerChannel, controller);

                    // Create a UDP LXDatagramOutput to own these packets
                    LXDatagramOutput output = new LXDatagramOutput(lx);
                    for (LEDScapeDatagram dg : datagrams) {
                        output.addDatagram(dg);
                    }

                    this.addOutput(output); // Comment out for dev

                } catch (UnknownHostException e) {
                    println("Unknown Host Exception while constructing UDP output: " + e);
                    e.printStackTrace();
                } catch (SocketException e) {
                    println("Socket Excpetion while constructing UDP output: " + e);
                    e.printStackTrace();
                }
            }

            @Override
            protected void onUIReady(LXStudio lx, LXStudio.UI ui) {
                // The UI is now ready, can add custom UI components if desired
                // UIWalls is from example project. Leaving in for now because
                // it prevents an error.
                ui.preview.addComponent(new UIWalls(model));
                ui.leftPane.engine.setVisible(true);

            }
        };

        // Use multi-threading for network output
        // lx.engine.output.mode.setValue(LXOutput.Mode.RAW);
        lx.engine.isNetworkMultithreaded.setValue(true);
        lx.engine.framesPerSecond.setValue(100);

        // Some components, like GemEdges, want to compute normals after all
        // components are loaded.
        model.computeNormalsJoule();

        // Fancy transitions for BM2017 were implemented using a hack.
        // This was necessary because the LX blend collections were static final.
        // In this setup there were three channels: Patterns, Transitions, and the Blender.

        // For development, initialize to desired pattern.
        lx.engine.getChannel(0)
                //.addPattern(new GemEdgeOrderAssistPattern(lx))   //Testing
                //.addPattern(new EdgeChannelPattern(lx))          //Testing
                //.addPattern(new GemEdgeColorPattern(lx))         //Testing
                //.addPattern(new SolidColorJoulePattern(lx))      //Testing, simple
                .addPattern(new VertRainbowShiftPattern(lx))
                .addPattern(new VUMeter(lx))
                .addPattern(new GemEdgePattern(lx))
                .addPattern(new SimpleChasePattern(lx))
                .addPattern(new SpinnerPattern(lx))
                .addPattern(new BubblesPattern(lx))
                .focusedPattern.setValue(1);
        lx.engine.getChannel(0).goNext();

        lx.engine.audio.enabled.setValue(true);
        lx.engine.audio.meter.gain.setValue(18);

        // ===Add fancy blending features===
        lx.engine.getChannel(0).enabled.setValue(false);
        lx.engine.getChannel(0).fader.setValue(0);

        // Channel of patterns that are the blends
        lx.engine.addChannel(new LXPattern[] { 
                new EdgeWipeBlendPattern(lx), 
                new OneGemBlendPattern(lx),
                new HorizWipeBlendPattern(lx), 
                new SphereBlendPattern(lx), 
                new OneGemBlendPattern(lx) })
                .label.setValue("FancyBlends");
        lx.engine.getChannel(1).enabled.setValue(false);
        lx.engine.getChannel(1).fader.setValue(0);

        // Channel & Pattern that does the blending
        BlenderPlusPattern blender = new BlenderPlusPattern(lx)
                .setTargetChannel(lx.engine.getChannel(0), lx.engine.getChannel(1));
        lx.engine.addChannel(new LXPattern[] { blender })
                .label.setValue("BlenderPlusChannel");
        blender.initialize();
        lx.engine.getChannel(2).fader.setValue(1);
        lx.engine.getChannel(2).enabled.setValue(true);
    }

    public void draw() {
        // Empty placeholder... LX handles everything for us!
        // guiScaleInt(3); //This must have been an attempt at fixing the scaling problem on retina screens in Eclipse.
    }

    public void keyEvent(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        int action = keyEvent.getAction();
        if (action == KeyEvent.RELEASE) {
            switch (keyCode) {
            // Ctrl+Q to quit
            case java.awt.event.KeyEvent.VK_Q:
                if (keyEvent.isControlDown() || keyEvent.isMetaDown()) {
                    exit();
                }
                break;
            // Ctrl+H for help mode On
            case java.awt.event.KeyEvent.VK_H:
                if (keyEvent.isControlDown() || keyEvent.isMetaDown()) {
                    setHelpModeOn();
                }
                break;
            // Ctrl+J for help mode Off
            case java.awt.event.KeyEvent.VK_J:
                if (keyEvent.isControlDown() || keyEvent.isMetaDown()) {
                    setHelpModeOff();
                }
                break;
            }
        }
    }

    void setHelpModeOn() {
        for (LEDScapeDatagram output : JouleCode.datagrams) {
            output.setBackgroundColor(LXColor.RED);
        }
        println("Help mode ON");
    }

    void setHelpModeOff() {
        for (LEDScapeDatagram output : JouleCode.datagrams) {
            output.setBackgroundColor(LXColor.BLACK);
        }
        println("Help mode OFF");
    }

}
