public class ArticulationPointElement {
    private Node node;
    private int depth;
    private Node parent;

    public ArticulationPointElement(Node node, int depth, Node parent) {
        this.node = node;
        this.depth = depth;
        this.parent = parent;
    }

    public Node getNode() {
        return node;
    }

    public int getDepth() {
        return depth;
    }

    public Node getParent() {
        return parent;
    }
}
