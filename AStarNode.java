public class AStarNode implements Comparable<AStarNode> {

    private Node currentNode;
    private AStarNode previousNode;
    private double costSoFar;
    private double estimatedCost;

    public AStarNode(Node currentNode, AStarNode previousNode, double costSoFar, double estimatedCost) {
        this.currentNode = currentNode;
        this.previousNode = previousNode;
        this.costSoFar = costSoFar;
        this.estimatedCost = estimatedCost;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public AStarNode getPreviousNode() {
        return previousNode;
    }

    public double getCostSoFar() {
        return costSoFar;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public int compareTo(AStarNode n) {
        if (getEstimatedCost() < n.getEstimatedCost()) {
            return -1;
        } else if (getEstimatedCost() > n.getEstimatedCost()) {
            return 1;
        } else {
            return 0;
        }
    }
}
