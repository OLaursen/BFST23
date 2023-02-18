package handin2;

import java.io.Serializable;
import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;

public class Way implements Serializable {
    /*
     * TODO Change floats to double
     */
    double[] coords;

    public Way(ArrayList<Node> way) {
        /*
         * Because the coordinates comes in order, the order of the array will be x1,y1,x2,y2,x3... 
         */
        coords = new double[way.size() * 2];

        //TODO Check if you can use for-each loop instead
        for (int i = 0 ; i < way.size() ; ++i) {
            var node = way.get(i);
            coords[2 * i] = 0.56 * node.lon;
            //0.56 is the approximate factor that makes it possible to go from a sphere plane to flate plane in proximity to Denmark
            coords[2 * i + 1] = -node.lat;
            //We need to use the negative value of the latitude, because otherwise the map will turn out to be the mirror image(Spejlvendt)
        }
    }

    public void draw(GraphicsContext gc) {
        gc.beginPath();
        gc.moveTo(coords[0], coords[1]); //Starting point/coordinates of the way
        for (int i = 2 ; i < coords.length ; i += 2) {
            /*
            The forloop starts right after the starting point, coords[2].
            .lineTo(x1, y1) "moves" the way through alle the node coordinates. 
            */
            gc.lineTo(coords[i], coords[i+1]);
        }
        gc.stroke();
        /*
         * TODO when polygons need to be filled, we can reuse this code to draw, but instead of .stroke() we can use .fill(). 
         * remember to add special cases and choose the right colours
         * 
         */
    }
    
}