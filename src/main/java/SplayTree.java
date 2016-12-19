import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.graph.implementations.SingleNode;
import org.graphstream.stream.SinkAdapter;
import org.graphstream.stream.file.*;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dokgo on 18.12.16.
 */
public class SplayTree extends SinkAdapter {

    class Node extends SingleNode {
        Integer key;
        Node left, right;

        Node(Integer key) {
            super(graph, Integer.toString(key));
            this.key = key;
            String id = Integer.toString(key);
            org.graphstream.graph.Node node = graph.addNode(id);
            node.addAttribute("ui.label", id);
        }

        @Override
        public String toString() {
            return Integer.toString(key);
        }
    }

    View view;
    Graph graph;
    private Node root;   // root of the BST
    String s = "";
    double X;
    double Y;
    double D;
    boolean removed = false;


    public SplayTree() {
        graph = new SingleGraph("Tree");
        graph.addAttribute("ui.stylesheet", "node { fill-color: green; size: 25px;} " +
                "node#null {fill-color:white;} " +
                "node.marked {fill-color:red;}");

        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
    }

    public Node getRoot() {
        return root;
    }

    public boolean contains(Integer key) {
        boolean res = get(key) != null;
        setPositions();
        return res;
    }

    // return value associated with the given key
    // if no such value, return null
    public Node get(Integer key) {
        root = splay(root, key);
        int cmp = key.compareTo(root.key);
        if (cmp == 0) return root;
        else return null;
    }

    public void insert(Integer key) {
        // splay key to root
        if (root == null) {
            org.graphstream.graph.Node node = graph.addNode("null");
            node.setAttribute("xy", X, Y - 1);
            root = new Node(key);
            setPositions();
            return;
        } else {
            if (!removed) {
                graph.removeNode("null");
                removed = true;
            }
        }

        root = splay(root, key);

        int cmp = key.compareTo(root.key);

        // Insert new node at root
        if (cmp < 0) {
            Node n = new Node(key);
            n.left = root.left;
            n.right = root;
            root.left = null;
            root = n;
        }

        // Insert new node at root
        else if (cmp > 0) {
            Node n = new Node(key);
            n.right = root.right;
            n.left = root;
            root.right = null;
            root = n;
        }

        // It was a duplicate key. Simply replace the value
        else {
            root.key = key;
        }

        setPositions();
    }


    public void remove(Integer key) {
        if (root == null) return; // empty tree

        root = splay(root, key);

        int cmp = key.compareTo(root.key);

        if (cmp == 0) {
            if (root.left == null) {
                root = root.right;
            } else {
                Node x = root.right;
                root = root.left;
                splay(root, key);
                root.right = x;
            }
        }
        graph.removeNode(Integer.toString(key));
        setPositions();
        // else: it wasn't in the tree to remove
    }

    //h is a local root
    private Node splay(Node h, Integer key) {
        if (h == null) return null;

        int cmp1 = key.compareTo(h.key);

        if (cmp1 < 0) {
            // key not in tree, so we're done
            if (h.left == null) {
                return h;
            }
            int cmp2 = key.compareTo(h.left.key);
            if (cmp2 < 0) {
                h.left.left = splay(h.left.left, key);
                h = rotateRight(h);
            } else if (cmp2 > 0) {
                h.left.right = splay(h.left.right, key);
                if (h.left.right != null)
                    h.left = rotateLeft(h.left);
            }

            if (h.left == null) return h;
            else return rotateRight(h);
        } else if (cmp1 > 0) {
            // key not in tree, so we're done
            if (h.right == null) {
                return h;
            }

            int cmp2 = key.compareTo(h.right.key);
            if (cmp2 < 0) {
                h.right.left = splay(h.right.left, key);
                if (h.right.left != null)
                    h.right = rotateRight(h.right);
            } else if (cmp2 > 0) {
                h.right.right = splay(h.right.right, key);
                h = rotateLeft(h);
            }

            if (h.right == null) return h;
            else return rotateLeft(h);
        } else return h;
    }

    // height of tree (1-node tree has height 0)
    public int height() {
        return height(root);
    }

    private int height(Node x) {
        if (x == null) return -1;
        return 1 + Math.max(height(x.left), height(x.right));
    }


    public int size() {
        return size(root);
    }

    private int size(Node x) {
        if (x == null) return 0;
        else return 1 + size(x.left) + size(x.right);
    }

    // right rotate
    private Node rotateRight(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        return x;
    }

    // left rotate
    private Node rotateLeft(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        return x;
    }

    @Override
    public String toString() {
        s = "";
        add(root);
        return s;
    }

    public void add(Node root) {
        if (root != null) {
            add(root.left);
            s += " " + root;
            add(root.right);
        }
    }

    void setPositions() {
        setPositions(root, X, Y, 0.50000);
        removeAllEdges();
        setEdges(true);
    }

    void setPositions(Node node, double x, double y, double z) {
        if (node != null) {
            setPositions(node.left, x - (x * z), y - D,z + z*node.getDegree());
            setPositions(node.right, X + (x - (x * z)), y - D,  z);

            graph.getNode(node.toString()).setAttribute("xy", x, y);
        }
    }

    void setEdges(boolean flag) {
        setEdges(root, flag);
    }

    void removeAllEdges(){
        List<String> list = new LinkedList<String>();
        for (Edge edge: graph.getEachEdge()) {
            list.add(edge.getId());
        }

        for (String s : list){
            //System.out.println(graph.getEdge(s));
            graph.removeEdge(s);
        }
    }

    void setEdges(Node node, boolean flag) {
        if (node != null) {
            setEdges(node.left, flag);
            String a = node.toString();
            String b = node.left != null ? node.left.toString() : null;
            String n = a + b;
            if (b != null) {
                graph.removeEdge(n);
                graph.addEdge(n, a, b);
            }
            setEdges(node.right, flag);
            a = node.toString();
            b = node.right != null ?  node.right.toString() : null;
            n = a + b;
            if (b != null) {
                graph.removeEdge(n);
                graph.addEdge(n, a, b);
            }
        }
    }

    public void display() {
        Viewer viewer = graph.display(false);
        viewer.disableAutoLayout();
        view = viewer.getDefaultView();

        X = view.getWidth() / 2;
        Y = view.getHeight() / 2;
        D = Y * 0.5;

       //view.setViewCenter(X, X, Y * 2);
        view.setViewPercent(10);
        view.resetView();
        graph.addSink(this);
    }

    public void save(String path) throws IOException {
        FileSinkDGS fg = new FileSinkDGS();
        FileSinkDOT fs = new FileSinkDOT();
        FileSinkGML fl = new FileSinkGML();
        fs.writeAll(graph, path + "dot");
        fg.writeAll(graph, path + "dgs");
        fl.writeAll(graph, path + "gml");
    }

    public void read(Graph graph, String path) throws IOException {
        FileSource fs = new FileSourceDGS();
        fs.addSink(graph);

        try {
            fs.begin(path);

            while (fs.nextEvents()) {
                System.out.println("added");

            }
        } catch( IOException e) {
	        e.printStackTrace();
        }

        try {
            fs.end();
        } catch( IOException e) {
            e.printStackTrace();
        } finally {
            graph.display();
            fs.removeSink(graph);
        }
    }

    @Override
    public void nodeAdded(String sourceId, long timeId, String nodeId) {
        insert(Integer.parseInt(nodeId));
        System.out.println(nodeId);
    }
}
