import java.util.AbstractMap;
import java.util.Map;
import java.util.TreeMap;

import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.output.LXDatagram;
import heronarts.lx.output.OPCConstants;

public class LEDScapeDatagram extends LXDatagram implements OPCConstants {

    static final int OFFSET_R = 0;
    static final int OFFSET_G = 1;
    static final int OFFSET_B = 2;

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
    public LEDScapeDatagram(int NumberOfChannels, int LEDsPerChannel, AbstractMap<Integer, Integer> indicesMapped) {
        super(HEADER_LEN + (BYTES_PER_PIXEL * NumberOfChannels * LEDsPerChannel));

        this.NumberOfChannels = NumberOfChannels;
        this.LEDsPerChannel = LEDsPerChannel;

        // Convert key/value map of LXPoints to a key/value map of indices.
        this.pointIndices = indicesMapped;

        int dataLength = BYTES_PER_PIXEL * this.NumberOfChannels * this.LEDsPerChannel;
        // this.buffer = new byte[HEADER_LEN + dataLength];
        this.buffer[INDEX_CHANNEL] = 0;
        this.buffer[INDEX_COMMAND] = COMMAND_SET_PIXEL_COLORS;
        this.buffer[INDEX_DATA_LEN_MSB] = (byte) (dataLength >>> 8);
        // PApplet.println(this.packetData[INDEX_DATA_LEN_MSB],
        // (this.NumberOfChannels * this.LEDsPerChannel)*3/256);
        this.buffer[INDEX_DATA_LEN_LSB] = (byte) (dataLength & 0xFF);
        // this.packetData[INDEX_DATA_LEN_LSB] = (byte)((this.NumberOfChannels *
        // this.LEDsPerChannel)*3%256);
        // PApplet.println(this.packetData[INDEX_DATA_LEN_LSB],
        // (this.NumberOfChannels * this.LEDsPerChannel)*3%256, dataLength &
        // 0xFF, ((this.NumberOfChannels * this.LEDsPerChannel)*3%256) & 0xFF);
        this.InitializePacketDataWithBackgroundColor();

        // PApplet.println("LEDScape buffer length:",this.buffer.length);
    }

    public LEDScapeDatagram(int NumberOfChannels, int LEDsPerChannel, LXAbstractFixtureMapped fixture) {
        this(NumberOfChannels, LEDsPerChannel, ConvertFixtureToMappedIndices(fixture));
    }

    public LEDScapeDatagram setChannel(byte channel) {
        this.buffer[INDEX_CHANNEL] = channel;
        return this;
    }

    public byte getChannel() {
        return this.buffer[INDEX_CHANNEL];
    }

    @Override
    public void onSend(int[] colors) {
        for (Map.Entry<Integer, Integer> entry : pointIndices.entrySet()) {
            int dataOffset = INDEX_DATA + (entry.getKey() * BYTES_PER_PIXEL);
            int c = colors[entry.getValue()];
            this.buffer[dataOffset + OFFSET_R] = (byte) (0xFF & (c >> 16));
            this.buffer[dataOffset + OFFSET_G] = (byte) (0xFF & (c >> 8));
            this.buffer[dataOffset + OFFSET_B] = (byte) (0xFF & c);
        }
    }

    protected void InitializePacketDataWithBackgroundColor() {
        for (int i = 0; i < this.pointIndices.size(); ++i) {
            int dataOffset = INDEX_DATA + i * BYTES_PER_PIXEL;
            this.buffer[dataOffset + OFFSET_R] = (byte) (0xFF & (this.backgroundColor >> 16));
            this.buffer[dataOffset + OFFSET_G] = (byte) (0xFF & (this.backgroundColor >> 8));
            this.buffer[dataOffset + OFFSET_B] = (byte) (0xFF & this.backgroundColor);
        }
    }

    public LEDScapeDatagram setBackgroundColor(int newBackgroundColor) {
        this.backgroundColor = newBackgroundColor;
        this.InitializePacketDataWithBackgroundColor();
        return this;
    }

}
