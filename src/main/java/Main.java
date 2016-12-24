/**
 * Created by dokgo on 18.12.16.
 */
public class Main {
    public static void main(String[] args) {
        System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        SplayTree tree = new SplayTree();
        if (args.length == 3 && args[0].equals("console")) {
            String in = args[1];
            String out = args[2];

            tree.readFromFile(in);
            tree.writeToFile(out);

            System.out.printf("file %s created\n    ", out);

        } else tree.display();

    }

}

