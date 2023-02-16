package handin2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.geometry.Point2D;

public class Model {
    List<Line> lines = new ArrayList<Line>();

    public Model(String filename) throws FileNotFoundException {
        File f = new File(filename);
        try (Scanner s = new Scanner(f)) {
            while (s.hasNext()) {
                lines.add(new Line(s.nextLine()));
            }
        }
    }

    public void add(Point2D p1, Point2D p2) {
        lines.add(new Line(p1, p2));
    }
}
