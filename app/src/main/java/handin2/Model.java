package handin2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.geometry.Point2D;

public class Model {
    List<String> lines = new ArrayList<String>();

    public Model(String filename) {
        File f = new File(filename);
        try {
            try (Scanner s = new Scanner(f)) {
                while (s.hasNext()) {
                    lines.add(s.nextLine());
                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void add(Point2D lastmodel, Point2D newmodel) {
        String s = "LINES".concat(" ").concat(lastmodel.getX() + "").concat(" ")
                .concat(lastmodel.getY() + "").concat(" ")
                .concat(newmodel.getX() + "").concat(" ")
                .concat(newmodel.getY() + "");
        lines.add(s);

    }

}
