import org.graphstream.algorithm.DynamicAlgorithm;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.IOException;
import java.util.Random;

/**
 * Created by dokgo on 18.12.16.
 */
public class Main {
    public static void main(String[] args) {
        System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        SplayTree tree = new SplayTree();
        try {
            tree.read(tree.graph, "/home/dokgo/Documents/graph.dgs");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(tree);
        //tree.display();

/*        Random random = new Random();
        *//*for (int j = 0; j < 20; j++) {
            int i = random.nextInt(10 + 1);
            tree.insert(i);
            sleep();
        }*//*
        sleep();
        tree.insert(4);
        sleep();
        tree.insert(5);
        sleep();
        tree.insert(2);
        sleep();
        tree.insert(0);
        sleep();
        tree.insert(3);
        sleep();
        tree.contains(4);
        System.out.println(tree);
        sleep();
        tree.insert(87);
        sleep();
        tree.contains(4);

        try {
            tree.save("/home/dokgo/Documents/graph.");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*boolean flag = true;
        while (true){
            tree.setEdges(flag);
            flag = !flag;
        }*/

      /*  Graph g = new SingleGraph("d");

        // Creation of the graph

        ApparitionAlgorithm da = new ApparitionAlgorithm() ;

        da.init(g);
        g.display();

        int i = 9;
        while( i > 0 ){
            g.addNode(Integer.toString(i));
            i--;
            sleep();
        }*/


    }

    static void sleep() {
        try {
            Thread.sleep(900);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

