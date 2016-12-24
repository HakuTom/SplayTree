import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.graph.implementations.SingleNode;
import org.graphstream.stream.SinkAdapter;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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
    int time = 1000;
/*
    ProxyPipe pipe;
*/


    public SplayTree() {
        graph = new SingleGraph("Tree");
        graph.addAttribute("ui.stylesheet", "node { fill-color: green, red; size: 25px; fill-mode: dyn-plain;} " +
                "node#null {fill-color:white;} " +
                "node.marked {fill-color:red;}"
        );

        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public boolean contains(Integer key) {
        Node node = get(key);
        boolean res = node != null;
        setPositions();
        return res;
    }

    public void trav() {
        trav(root);
    }

    public void trav(Node node) {
        if (node != null) {
            trav(node.left);
            graph.getNode(node.toString()).setAttribute("ui.color", 1);
            sleep();
            trav(node.right);
        }
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
            node.setAttribute("xy", 0, 1);
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


    /*public void remove(Integer key) {
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
            graph.removeNode(Integer.toString(key));
        }

        setPositions();
        // else: it wasn't in the tree to remove
    }*/

    public boolean remove(Integer key) {
        Node parent = root;
        Node current = root;
        boolean isLeftChild = false;
        while (current.key.compareTo(key) != 0) {
            parent = current;

            if (current.key.compareTo(key) > 0) {
                isLeftChild = true;
                current = current.left;
            } else {
                isLeftChild = false;
                current = current.right;
            }
            if (current == null) {
                root = splay(root, key);
                setPositions();
                return false;
            }
        }
        // found the node
        //node has no children
        if (current.left == null && current.right == null) {
            if (current == root) {
                root = null;
            }
            if (isLeftChild) {
                parent.left = null;
            } else {
                parent.right = null;
            }
            root = splay(root, parent.key);
        }
        // has only one child
        else if (current.right == null) {
            if (current == root) {
                root = current.left;
            } else if (isLeftChild) {
                parent.left = current.left;
            } else {
                parent.right = current.left;
            }
            root = splay(root, current.left.key);
        } else if (current.left == null) {
            if (current == root) {
                root = current.right;
            } else if (isLeftChild) {
                parent.left = current.right;
            } else {
                parent.right = current.right;
            }
            root = splay(root, current.right.key);
        } else {

            //minimum element in the right sub tree
            Node successor = getSuccessor(current);
            if (current == root) {
                root = successor;
            } else if (isLeftChild) {
                parent.left = successor;
            } else {
                parent.right = successor;
            }
            successor.left = current.left;
            root = splay(root, successor.key);
        }
        graph.removeNode(Integer.toString(key));
        setPositions();
        return true;
    }

    private void setColor(Node node) {
        //graph.getNode(node.toString()).addAttribute("ui.class", "marked");
        node.addAttribute("ui.color", 0.5);
        //graph.getNode(node.toString()).addAttribute("ui.color", 1);
        org.graphstream.graph.Node node1 = graph.addNode("null");
        node1.setAttribute("xy", 0, 23);
//        pipe.pump();
        sleep();
        graph.removeNode("null");
    }

    private Node getSuccessor(Node deleteNode) {
        Node sucsr = null;
        Node sucsrParent = null;
        Node curr = deleteNode.right;
        while (curr != null) {
            sucsrParent = sucsr;
            sucsr = curr;
            curr = curr.left;
        }
        if (sucsr != deleteNode.right) {
            sucsrParent.left = sucsr.right;
            sucsr.right = deleteNode.right;
        }
        return sucsr;
    }

    //h is a local root
    public Node splay(Node h, Integer key) { //TODO: return to private
        if (h == null) return null;
//        org.graphstream.graph.Node node = graph.getNode(h.toString());


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

            if (h.left == null) {
                return h;
            } else {
                return rotateRight(h);
            }

        } else if (cmp1 > 0) {
            // key not in tree, so we're done
            if (h.right == null) {
                return h;
            }

            int cmp2 = key.compareTo(h.right.key);
            if (cmp2 < 0) {
                h.right.left = splay(h.right.left, key);
                if (h.right.left != null) {
                    h.right = rotateRight(h.right);
                }
            } else if (cmp2 > 0) {
                h.right.right = splay(h.right.right, key);
                h = rotateLeft(h);
            }

            if (h.right == null) {
                return h;
            } else {
                return rotateLeft(h);
            }

        } else {
            return h;
        }
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
        setPositions();
        return x;
    }

    // left rotate
    private Node rotateLeft(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        setPositions();
        return x;
    }

    @Override
    public String toString() {
        s = "";
        add(root);
        return s;
    }

    public void clear() {
        graph.clear();
        root = null;
    }

    public void add(Node root) {
        if (root != null) {
            add(root.left);
            s += " " + root;
            add(root.right);
        }
    }

    void setPositions() {
        setPositions(root, 0, 200, X);
        //System.out.println("coords" + graph.getNode(root.toString()).getAttribute("xy").toString());
        //view.setViewCenter(0, 0, 0);

        removeAllEdges();
        setEdges(true);
    }

    void setPositions(Node root) {
        setPositions(root, 0, 200, X);
        //System.out.println("coords" + graph.getNode(root.toString()).getAttribute("xy").toString());
        //view.setViewCenter(0, 0, 0);

        removeAllEdges();
        setEdges(true);
    }

    void setPositions(Node node, double x, double y, double z) {
        if (node != null) {
            setPositions(node.left, (x - z), y - D, z / 2);
            graph.getNode(node.toString()).setAttribute("xy", x, y);
            //System.out.printf("x: %s y: %s\n", x, y);
            setPositions(node.right, (x + z), y - D, z / 2);
        }
    }

    void setEdges(boolean flag) {
        setEdges(root, flag);
    }

    void removeAllEdges() {
        List<String> list = new LinkedList<String>();
        for (Edge edge : graph.getEachEdge()) {
            list.add(edge.getId());
        }

        for (String s : list) {
            //System.out.println(graph.getEdge(s));
            graph.removeEdge(s);
        }
    }

    void setEdges(Node node, boolean flag) {
        if (node != null) {
            setEdges(node.left, !flag);
            String a = node.toString();
            String b = node.left != null ? node.left.toString() : null;
            String n = a + b;
            if (b != null) {
                graph.removeEdge(n);
                graph.addEdge(n, a, b);
            }
            setEdges(node.right, !flag);
            a = node.toString();
            b = node.right != null ? node.right.toString() : null;
            n = a + b;
            if (b != null) {
                graph.removeEdge(n);
                graph.addEdge(n, a, b);
            }
        }
    }

    public void display() {

        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD); //graph.display(false);
        viewer.disableAutoLayout();
        view = viewer.addDefaultView(false);
        //view.setBounds(-1900, - 1080, 0, 1900, 1080, 0);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        System.out.println(view.toString());
        X = 530; //width / 2;
        Y = 231; //height / 2;
        D = Y * 0.7;

        //view.setViewCenter(0, -200, 0);
        view.setViewPercent(2.5);
        /*pipe = viewer.newViewerPipe();*/
        graph.addSink(this);
/*
        pipe.addAttributeSink(graph);
*/

        GUIForm app = new GUIForm(view, this);

    }

    public void readFromFile(String path) {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            while (line != null && !line.equals("stop")) {
                String[] op = line.split(":");
                int k = Integer.parseInt(op[1]);
                if (op[0].equals("insert")) {
                    insert(k);
                } else if (op[0].equals("delete")) {
                    remove(k);
                } else if (op[0].equals("find")) {
                    contains(k);
                }
                line = reader.readLine();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public void writeToFile(String path) {
        PrintWriter file = null;
        try {
            file = new PrintWriter(path, "UTF-8");
            file.println(this);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            file.close();
        }
    }

    void sleep() {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*@Override
    public void nodeAdded(String sourceId, long timeId, String nodeId) {
        insert(Integer.parseInt(nodeId));
        System.out.println(nodeId);
    }*/
}
