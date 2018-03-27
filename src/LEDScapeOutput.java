import java.util.AbstractMap;
import java.util.Map;
import java.util.TreeMap;

import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.output.LXSocketOutput;
import processing.core.PApplet;

public class LEDScapeOutput extends LXSocketOutput {

    static final int HEADER_LEN = 4;

    static final int BYTES_PER_PIXEL = 3;

    static final int INDEX_CHANNEL = 0;
    static final int INDEX_COMMAND = 1;
    static final int INDEX_DATA_LEN_MSB = 2;
    static final int INDEX_DATA_LEN_LSB = 3;
    static final int INDEX_DATA = 4;

    static final int OFFSET_R = 0;
    static final int OFFSET_G = 1;
    static final int OFFSET_B = 2;

    static final int COMMAND_SET_PIXEL_COLORS = 0;

    private final byte[] packetData;

    private final AbstractMap<Integer, Integer> pointIndices;

    public final int LEDsPerChannel;
    public final int NumberOfChannels;
    protected int backgroundColor = LXColor.BLACK;

    protected static AbstractMap<Integer, Integer> ConvertFixtureToMappedIndices(LXAbstractFixtureMapped fixture) {
        TreeMap<Integer, Integer> indicesMap = new TreeMap<Integer, Integer>();
        for (Map.Entry<Integer, LXPoint> entry : fixture.getPointsMapped().entrySet()) {
            indicesMap.put(entry.getKey(), entry.getValue().index);
        }

        return indicesMap;
    }

    // Map is <index of LED in channel, overall index of point>
    public LEDScapeOutput(LX lx, String host, int port, int NumberOfChannels, int LEDsPerChannel, AbstractMap<Integer, Integer> indicesMapped) {
        super(lx, host, port);

        this.NumberOfChannels = NumberOfChannels;
        this.LEDsPerChannel = LEDsPerChannel;

        // Convert key/value map of LXPoints to a key/value map of indices.
        this.pointIndices = indicesMapped;

        int dataLength = BYTES_PER_PIXEL * this.NumberOfChannels * this.LEDsPerChannel;
        this.packetData = new byte[HEADER_LEN + dataLength];
        this.packetData[INDEX_CHANNEL] = 0;
        this.packetData[INDEX_COMMAND] = COMMAND_SET_PIXEL_COLORS;
        this.packetData[INDEX_DATA_LEN_MSB] = (byte) (dataLength >>> 8);
        // PApplet.println(this.packetData[INDEX_DATA_LEN_MSB],
        // (this.NumberOfChannels * this.LEDsPerChannel)*3/256);
        this.packetData[INDEX_DATA_LEN_LSB] = (byte) (dataLength & 0xFF);
        // this.packetData[INDEX_DATA_LEN_LSB] = (byte)((this.NumberOfChannels *
        // this.LEDsPerChannel)*3%256);
        this.InitializePacketDataWithBackgroundColor();

        PApplet.println("packet buffer length:", this.packetData.length);
    }

    public LEDScapeOutput(LX lx, String host, int port, int NumberOfChannels, int LEDsPerChannel,
            LXAbstractFixtureMapped fixture) {
        this(lx, host, port, NumberOfChannels, LEDsPerChannel, ConvertFixtureToMappedIndices(fixture));
    }

    @Override
    protected byte[] getPacketData(int[] colors) {
        for (Map.Entry<Integer, Integer> entry : pointIndices.entrySet()) {
            int dataOffset = INDEX_DATA + (entry.getKey() * BYTES_PER_PIXEL);
            int c = colors[entry.getValue()];
            this.packetData[dataOffset + OFFSET_R] = (byte) (0xFF & (c >> 16));
            this.packetData[dataOffset + OFFSET_G] = (byte) (0xFF & (c >> 8));
            this.packetData[dataOffset + OFFSET_B] = (byte) (0xFF & c);
        }
        return this.packetData;
    }

    protected void InitializePacketDataWithBackgroundColor() {
        for (int i = 0; i < this.pointIndices.size(); ++i) {
            int dataOffset = INDEX_DATA + i * BYTES_PER_PIXEL;
            this.packetData[dataOffset + OFFSET_R] = (byte) (0xFF & (this.backgroundColor >> 16));
            this.packetData[dataOffset + OFFSET_G] = (byte) (0xFF & (this.backgroundColor >> 8));
            this.packetData[dataOffset + OFFSET_B] = (byte) (0xFF & this.backgroundColor);
        }
    }

    public LEDScapeOutput setChannel(int channel) {
        this.packetData[INDEX_CHANNEL] = (byte) channel;
        return this;
    }

    public LEDScapeOutput setBackgroundColor(int newBackgroundColor) {
        this.backgroundColor = newBackgroundColor;
        this.InitializePacketDataWithBackgroundColor();
        return this;
    }

}
