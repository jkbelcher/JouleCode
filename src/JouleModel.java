import java.lang.Math;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.io.CsvMapReader;

import heronarts.lx.model.LXFixture;
import heronarts.lx.model.LXModel;
import heronarts.lx.transform.LXTransform;

/**
 * This model represents the entire Joule art car.
 * It contains lists of the logical lighted components on the car.
 */
public class JouleModel extends LXModel {

    // For CSV files:
    static public final String subSeparator = ";";

    public final List<BeagleboneController> controllers;
    public final List<Cluster> clusters;
    public final List<GemType> gemTypes;
    public final List<Gem> gems;

    public Boolean isInitialized = false;

    public JouleModel(LXFixture[] allFixtures, List<BeagleboneController> controllers, List<Cluster> clusters,
            List<GemType> gemTypes, List<Gem> gems) {
        super(allFixtures);

        this.controllers = controllers;
        this.clusters = clusters;
        this.gemTypes = gemTypes;
        this.gems = gems;
    }

    public void computeNormalsJoule() {
        for (Gem gem : this.gems) {
            gem.computeNormalsJoule();
        }
    }

    public static JouleModel LoadConfigurationFromFile() throws Exception {
        final List<BeagleboneController> controllers = new ArrayList<BeagleboneController>();
        final List<Cluster> clusters = new ArrayList<Cluster>();
        final List<GemType> gemTypes = new ArrayList<GemType>();
        final List<Gem> gems = new ArrayList<Gem>();

        final TreeMap<Integer, BeagleboneController> controllersDict = new TreeMap<Integer, BeagleboneController>();
        final HashMap<String, Cluster> clustersDict = new HashMap<String, Cluster>();
        final HashMap<String, GemType> gemTypeDict = new HashMap<String, GemType>();

        // Establish positional transform.
        // Positions of clusters are relative to car. Positions of gems are relative to clusters.
        // This allows for convenient repositioning of clusters on the car.
        LXTransform transform = new LXTransform();

        // Gem Types
        List<GemTypeParameters> gtP = ReadGemTypesFromFile("./config/gemTypes.csv");
        for (GemTypeParameters p : gtP) {
            GemType newGemType = new GemType(p);
            gemTypes.add(newGemType);
            gemTypeDict.put(p.gemType, newGemType);
        }

        // Controllers
        List<ControllerParameters> cP = ReadControllersFromFile("./config/controllers.csv");
        for (ControllerParameters p : cP) {
            BeagleboneController newController = new BeagleboneController(p);
            controllers.add(newController);
            controllersDict.put(p.id, newController);
        }

        // Read Gems from file but save them to create with their appropriate
        // cluster, for transform convenience.
        TreeMap<String, List<GemParameters>> gP = ReadGemsFromFile("./config/gems.csv");

        // Clusters
        List<ClusterParameters> clP = ReadClustersFromFile("./config/clusters.csv");
        for (ClusterParameters p : clP) {
            Cluster newCluster = new Cluster(p);
            clusters.add(newCluster);
            clustersDict.put(p.name, newCluster);

            // Add to Controller
            controllersDict.get(p.controllerID).AddCluster(newCluster);

            // Load Gems for this cluster
            transform.push();
            transform.translate(newCluster.params.x, newCluster.params.y, newCluster.params.z);
            transform.rotateY(Math.toRadians(newCluster.params.yRotation));

            for (GemParameters gParams : gP.get(newCluster.params.name)) {
                GemType gemType = gemTypeDict.get(gParams.gemType);
                Gem newGem;
                switch (gParams.gemType.toLowerCase()) {
                case "sign":
                    // Use sign class
                    newGem = new SignGem(gParams, gemType, transform);
                    break;
                case "alpha":
                case "beta":
                case "charlie":
                case "gogo":
                default:
                    newGem = new OriginalGem(gParams, gemType, transform);
                    break;                        
                }

                newGem.controller = controllersDict.get(p.controllerID);
                gems.add(newGem);
                newCluster.AddGem(newGem);
            }

            transform.pop();
        }

        List<LXFixture> _fixtures = new ArrayList<LXFixture>(gems);

        return new JouleModel(_fixtures.toArray(new LXFixture[_fixtures.size()]), controllers, clusters, gemTypes,
                gems);
    }

    private static CellProcessor[] getControllerCsvProcessors() {
        return new CellProcessor[] { new UniqueHashCode(), // id (must be unique)
                new NotNull(), // ipAddress
                new ParseInt(), // numberOfChannels
                new ParseInt() // LEDsPerChannel
        };
    }

    protected static List<ControllerParameters> ReadControllersFromFile(String filename) throws Exception {

        final ArrayList<ControllerParameters> results = new ArrayList<ControllerParameters>();

        ICsvMapReader mapReader = null;
        try {
            mapReader = new CsvMapReader(new FileReader(filename), CsvPreference.STANDARD_PREFERENCE);

            // The header columns are used as the keys to the Map
            final String[] header = mapReader.getHeader(true);
            final CellProcessor[] processors = getControllerCsvProcessors();

            Map<String, Object> c;
            while ((c = mapReader.read(header, processors)) != null) {
                // System.out.println(String.format("lineNo=%s, rowNo=%s, map=%s", mapReader.getLineNumber(), mapReader.getRowNumber(), c));

                ControllerParameters p = new ControllerParameters();
                p.id = Integer.parseInt(c.get("id").toString());
                p.ipAddress = c.get("ipAddress").toString();
                p.numberOfChannels = Integer.parseInt(c.get("numberOfChannels").toString());
                p.LEDsPerChannel = Integer.parseInt(c.get("LEDsPerChannel").toString());

                results.add(p);
            }
        } finally {
            if (mapReader != null) {
                mapReader.close();
            }
        }

        return results;
    }

    private static CellProcessor[] getClusterCsvProcessors() {
        return new CellProcessor[] { new ParseInt(), // controllerID
                new NotNull(), // name
                new ParseInt(), // x
                new ParseInt(), // y
                new ParseInt(), // z
                new ParseInt(), // yRotation
        };
    }

    protected static List<ClusterParameters> ReadClustersFromFile(String filename) throws Exception {

        final ArrayList<ClusterParameters> results = new ArrayList<ClusterParameters>();

        ICsvMapReader mapReader = null;
        try {
            mapReader = new CsvMapReader(new FileReader(filename), CsvPreference.STANDARD_PREFERENCE);

            // The header columns are used as the keys to the Map
            final String[] header = mapReader.getHeader(true);
            final CellProcessor[] processors = getClusterCsvProcessors();

            Map<String, Object> c;
            while ((c = mapReader.read(header, processors)) != null) {
                // System.out.println(String.format("lineNo=%s, rowNo=%s, map=%s", mapReader.getLineNumber(), mapReader.getRowNumber(), c));

                ClusterParameters p = new ClusterParameters();
                p.controllerID = Integer.parseInt(c.get("controllerID").toString());
                p.name = c.get("name").toString();
                p.x = Integer.parseInt(c.get("x").toString());
                p.y = Integer.parseInt(c.get("y").toString());
                p.z = Integer.parseInt(c.get("z").toString());
                p.yRotation = Integer.parseInt(c.get("yRotation").toString());

                results.add(p);
            }
        } finally {
            if (mapReader != null) {
                mapReader.close();
            }
        }

        return results;
    }

    private static CellProcessor[] getGemTypeCsvProcessors() {
        return new CellProcessor[] { new NotNull(), // gemType
                new ParseDouble(), // topSquare
                new ParseDouble(), // bottomSquare
                new ParseDouble(), // heightSquares
        };
    }

    protected static List<GemTypeParameters> ReadGemTypesFromFile(String filename) throws Exception {
        final ArrayList<GemTypeParameters> results = new ArrayList<GemTypeParameters>();

        ICsvMapReader mapReader = null;
        try {
            mapReader = new CsvMapReader(new FileReader(filename), CsvPreference.STANDARD_PREFERENCE);

            // The header columns are used as the keys to the Map
            final String[] header = mapReader.getHeader(true);
            final CellProcessor[] processors = getGemTypeCsvProcessors();

            Map<String, Object> c;
            while ((c = mapReader.read(header, processors)) != null) {
                // System.out.println(String.format("lineNo=%s, rowNo=%s, map=%s", mapReader.getLineNumber(), mapReader.getRowNumber(), c));

                GemTypeParameters p = new GemTypeParameters();
                p.gemType = c.get("gemType").toString();
                p.topSquare = (float) Double.parseDouble(c.get("topSquare").toString());
                p.bottomSquare = (float) Double.parseDouble(c.get("bottomSquare").toString());
                p.heightSquares = (float) Double.parseDouble(c.get("heightSquares").toString());

                results.add(p);
            }
        } finally {
            if (mapReader != null) {
                mapReader.close();
            }
        }

        return results;
    }

    private static CellProcessor[] getGemCsvProcessors() {
        return new CellProcessor[] { new NotNull(), // clusterName
                new ParseInt(), // positionInCluster
                new ParseInt(), // controllerChannel
                new NotNull(), // gemType
                new ParseInt(), // x
                new ParseInt(), // y
                new ParseInt(), // z
                new ParseDouble(), // xRotate
                new ParseDouble(), // yRotate
                new ParseDouble(), // zRotate
                new NotNull(), // edgeOrder
                new NotNull(), // edgePixelCount
        };
    }

    protected static TreeMap<String, List<GemParameters>> ReadGemsFromFile(String filename) throws Exception {

        final TreeMap<String, List<GemParameters>> results = new TreeMap<String, List<GemParameters>>();

        ICsvMapReader mapReader = null;
        try {
            mapReader = new CsvMapReader(new FileReader(filename), CsvPreference.STANDARD_PREFERENCE);

            // the header columns are used as the keys to the Map
            final String[] header = mapReader.getHeader(true);
            final CellProcessor[] processors = getGemCsvProcessors();

            Map<String, Object> c;
            while ((c = mapReader.read(header, processors)) != null) {
                // System.out.println(String.format("lineNo=%s, rowNo=%s,
                // map=%s", mapReader.getLineNumber(), mapReader.getRowNumber(),
                // c));

                GemParameters p = new GemParameters();
                p.clusterName = c.get("clusterName").toString();
                p.positionInCluster = Integer.parseInt(c.get("positionInCluster").toString());
                p.controllerChannel = Integer.parseInt(c.get("controllerChannel").toString());
                p.gemType = c.get("gemType").toString();
                p.x = Integer.parseInt(c.get("x").toString());
                p.y = Integer.parseInt(c.get("y").toString());
                p.z = Integer.parseInt(c.get("z").toString());
                p.xTilt = Double.parseDouble(c.get("xRotate").toString());
                p.yTilt = Double.parseDouble(c.get("yRotate").toString());
                p.zTilt = Double.parseDouble(c.get("zRotate").toString());
                p.edgeOrder = Arrays.stream(c.get("edgeOrder").toString().split(subSeparator))
                        .mapToInt(Integer::parseInt).toArray();
                p.edgePixelCount = Arrays.stream(c.get("edgePixelCount").toString().split(subSeparator))
                        .mapToInt(Integer::parseInt).toArray();

                if (!results.containsKey(p.clusterName)) {
                    results.put(p.clusterName, new ArrayList<GemParameters>());
                }
                results.get(p.clusterName).add(p);
            }
        } finally {
            if (mapReader != null) {
                mapReader.close();
            }
        }

        return results;
    }

}