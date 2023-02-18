package handin2;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipInputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import javafx.geometry.Point2D;

public class Model implements Serializable {
    /*
     * create different lists of the things we want to showcase in the map
     */
    List<Line> lines = new ArrayList<Line>();
    List<Way> ways = new ArrayList<Way>();

    double minlat, maxlat, minlon, maxlon;

    static Model load(String filename) throws FileNotFoundException, IOException, ClassNotFoundException, XMLStreamException, FactoryConfigurationError {
        //Loads the object file created for fast execution of program
        if (filename.endsWith(".obj")) {
            try (var in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
                return (Model) in.readObject();
            }
        }
        return new Model(filename);
    }
    

    public Model(String filename) throws XMLStreamException, FactoryConfigurationError, IOException {
        if (filename.endsWith(".osm.zip")) {
            parseZIP(filename);
        } else if (filename.endsWith(".osm")) {
            parseOSM(filename);
        } else {
            parseTXT(filename);
        }
        save(filename+".obj");
        /*
         * Once we're done parsing the file, we save whatever we found interesting as an object
         * which we can because we're stated that the elements within are serializable. 
         * This will dramatically decrease the loading time of the program.
         */
    }

    void save(String filename) throws FileNotFoundException, IOException {
        try (var out = new ObjectOutputStream(new FileOutputStream(filename))) {
            //Object outputstreams are slow, maybe consider alternative
            out.writeObject(this); //Writes this current model to disk
            /*
             * Every object reached by this needs to implement the empty interface(tagging interface) "serializable" which informs java
             * that we indeed have thought about this, and we think the relevant class is serializable. 
             */
        }
    }

    private void parseZIP(String filename) throws IOException, XMLStreamException, FactoryConfigurationError {
        var input = new ZipInputStream(new FileInputStream(filename));
        //.getNextEntry() Fetches the next file in the .zip folder
        input.getNextEntry();
        parseOSM(input);
    }

    private void parseOSM(String filename) throws FileNotFoundException, XMLStreamException, FactoryConfigurationError {
        //This method runs if the .osm file doesn'
        parseOSM(new FileInputStream(filename));
    }

    private void parseOSM(InputStream inputStream) throws FileNotFoundException, XMLStreamException, FactoryConfigurationError {
        /* This function either gets an inputstream from the parseZip method or from the other parseOSM method
         * You either have to create an event reader or a stream reader, event reader is SAX and stream reader is STAX
         * In this case we're working with STAX-parsing.
         */
        var input = XMLInputFactory.newInstance().createXMLStreamReader(new InputStreamReader(inputStream));
        //id2node is meant to save the nodes(Coordinates) so we can pull them when we need them
        var id2node = new HashMap<Long, Node>();
        var way = new ArrayList<Node>();
        var coast = false;
        while (input.hasNext()) {
            var tagKind = input.next(); // .next() returns an integer correspondint to a StreamConstant that indicates which tagkind is at hand
            if (tagKind == XMLStreamConstants.START_ELEMENT) {
                //The local name indicates which element is being parsed, examples of which is seen in the if/else statements.
                var name = input.getLocalName(); 
                
                if (name == "bounds") {
                    //"bounds" holds the coordinates that forms the 4 outermost values in every direction of the given map being parsed  
                    minlat = Double.parseDouble(input.getAttributeValue(null, "minlat"));
                    maxlat = Double.parseDouble(input.getAttributeValue(null, "maxlat"));
                    minlon = Double.parseDouble(input.getAttributeValue(null, "minlon"));
                    maxlon = Double.parseDouble(input.getAttributeValue(null, "maxlon"));

                } else if (name == "node") { //Should use equals instead of ==, as this is a special case for java. Also doesn't work on tags.
                    //The element "node" is a coordinate with a position in terms of longitude and latitude
                    var id = Long.parseLong(input.getAttributeValue(null, "id"));
                    // TODO change doubles to float
                    var lat = Double.parseDouble(input.getAttributeValue(null, "lat"));
                    var lon = Double.parseDouble(input.getAttributeValue(null, "lon"));
                    id2node.put(id, new Node(lat, lon));

                } else if (name == "way") {
                    way.clear(); //Clears whatever what was in that list before, so it's ready to be filled with new information
                    coast = false;

                } else if (name == "tag") {
                    /*
                    Format in XML file: <tag k="type" v="coastline"/>
                    We want to check if the current tag is a coastline, we will set coast to true which later will affect how the element is stored
                    */
                    var v = input.getAttributeValue(null, "v");
                    if (v.equals("coastline")) {
                        coast = true;
                    }
                } else if (name == "nd") {
                    var ref = Long.parseLong(input.getAttributeValue(null, "ref"));
                    var node = id2node.get(ref);
                    way.add(node);
                } 
                /*
                    TODO make another if-else statement to parse building elements, and make a special case for ITU <3
                 */
            } else if (tagKind == XMLStreamConstants.END_ELEMENT) {
                var name = input.getLocalName();
                //At the end of the element we store any relevant information we want to use from that element
                if (name == "way" && coast) {
                    //TODO if the element is a way and a coast it should be added to a "coast" arrayList<way>
                  ways.add(new Way(way));
                } 
                /*
                    TODO Add and else if statement to add normal ways to a different arrayList<Way>
                    Make sure that this arrayList also gets drawn over in View method
                 */ 
            }
        }
    }

    private void parseTXT(String filename) throws FileNotFoundException {
        File f = new File(filename);
        try (Scanner s = new Scanner(f)) {
            while (s.hasNext()) {
                lines.add(new Line(s.nextLine()));
            }
        }
    }

    public void add(Point2D p1, Point2D p2) {
        //Adds a line from point 1 to point 2
        lines.add(new Line(p1, p2));
    }
}
