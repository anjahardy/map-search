import java.util.*;
import java.util.ArrayList;


public class AStar {

    private Node start;
    private Node end;

    private AStarNode current;
    private AStarNode previous;
    private boolean reachedEnd = false;
    public ArrayList<Segment>path;

    public AStar(Node start, Node end) {
        this.start = start;
        this.end = end;
    }

    public double getEstimatedCost(Node node) {
        double estimatedCost = node.h(end);
        return estimatedCost;
    }

    public AStarNode initial() {
        double estimatedCost = getEstimatedCost(start);
        AStarNode current = new AStarNode(start, null, 0, estimatedCost);
        this.current = current;
        return current;
    }

    /**public double segmentCost(Collection<Segment> segments, int adjcentID) {
        for (Segment s : segments) {
            int endNodeID = s.getEnd().getNodeID();
            if (endNodeID == adjcentID) {
                return s.getLength();
            }
        }
        return 0;
    }**/

    /**public int compare(AStarNode n1, AStarNode n2) {
        double n1c = n1.getEstimatedCost();
        double n2c = n2.getEstimatedCost();
        if (n1c > n2c) {
            return 1;
        } else if (n1c < n2c) {
            return -1;
        } else {
            return 0;
        }
    }**/

    public Map<Node, AStarNode> quickestPath() {
       HashMap<Node, AStarNode> visited = new HashMap<>();//store visited nodes
        /**PriorityQueue<AStarNode> fringe = new PriorityQueue<>(99999999, new Comparator<AStarNode>() {
            @Override
            public int compare(AStarNode n1, AStarNode n2) {
                double n1c = n1.getEstimatedCost();
                double n2c = n2.getEstimatedCost();
                if (n1c > n2c) {
                    return -1;
                } else if (n1c < n2c) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });**///create the fringe with comparator
        PriorityQueue<AStarNode> fringe = new PriorityQueue<>();//create the fringe with comparator stored in a*node constructor
        initial();//calling the method that makes the start node into an a*node
        fringe.add(current);//adding the starting node(which is now an a*node) to the fringe to start the search
        while (fringe.size() != 0 && reachedEnd != true) {//initializing a while loop 'while the fringe is not empty'
            AStarNode polled = fringe.poll();//removing the a*node from the fringe
            this.current = polled;
            if (!visited.containsValue(current)) {//initializing if condition 'if the a*node has not already been visited'
                visited.put(current.getCurrentNode(), current);//add a*node to visited with original node as key
                this.previous = current;//save polled node to global variable previous
                if (current.getCurrentNode().equals(end)) {//initializing if condition 'if the node is the end node'
                    this.reachedEnd = true;//set reachedEnd to true
                    break;//break
                }
                //adding neighbours to fringe
                ArrayList<Node> adjacentNodes = current.getCurrentNode().getAdjacent();//arraylist to store adjacent nodes
                for(int i = 0; i < adjacentNodes.size(); i++){//initializing for loop 'for each of the adjacent nodes
                    Node adjacent = adjacentNodes.get(i);//select an adjacent node
                    int currentID = current.getCurrentNode().getNodeID();//store the ID of the current node in a variable
                    int adjacentID = adjacent.getNodeID();//store the ID of the selected adjacent node in a variable
                    Collection<Segment>adjacentSegments = adjacent.getSegments();//collection to store all the segments in/out of the selected adjacent node
                    for(Segment s : adjacentSegments){//initializing for loop 'for each segment in the collection of adjacent segments'
                            if((s.getStart().getNodeID() == currentID && s.getEnd().getNodeID() == adjacentID) || (s.getEnd().getNodeID() == currentID && s.getStart().getNodeID() == adjacentID)){
                                //initializing if condition 'if the end nodes of the segment match the current and selected nodes'
                                double costSoFar = s.getLength() + current.getCostSoFar();//set the cost so far to be the length of the segment + the current cost do far
                                double estimatedCost = adjacent.h(end);//figure out the new heuristic from the selected adjacent node
                                AStarNode adjacentAsAStar = new AStarNode(adjacent, current, costSoFar, estimatedCost);//create a new a*node with the selected adjacent node as the current node, current node as previous node, and new cost so far and heuristic
                                fringe.add(adjacentAsAStar);//add new node to the fringe
                        }
                    }
                }


                /**for (Node adjacent : current.getCurrentNode().getAdjacent()) {//iterating through each node in the list of adjacent nodes for that a*node
                    int currentID = current.getCurrentNode().getNodeID();
                    int adjacentID = adjacent.getNodeID();//finding the node ID of that particular adjacent node
                    Collection<Segment> adjacentSegments = adjacent.getSegments(); //finding the list of segments involving that particular node
                    for (Segment s : adjacentSegments) {//initializing for loop 'for each segment involving that particular node
                        if(!visited.containsKey(s.getEnd())) {
                            if (s.getStart().getNodeID() == currentID && s.getEnd().getNodeID() == adjacentID) {//checking that it is outgoing segment from the node we polled
                                double costSoFar = s.getLength() + current.getCostSoFar();//finding the cost so far by adding the length of that segment to the cost of the path so far
                                double estimatedCost = adjacent.h(end);//finding the estimated cost to the end of the path
                                AStarNode adjacentAsAStar = new AStarNode(adjacent, current, costSoFar, estimatedCost);//making a new a*node out of the adjacent node
                                fringe.add(adjacentAsAStar);//adding the new a*node to the fringe
                            }
                        }
                    }
                }**/
            }
        }
        printPath(current, start);
        return visited; // get path:
    }


    public List<Segment> printPath(AStarNode current, Node start) {
        //String roadName = "";
        //double length =0;
        ArrayList<Segment>path = new ArrayList<>();
        AStarNode currentNode = current;
        while(!currentNode.getCurrentNode().equals(start)){
            int currentID = currentNode.getCurrentNode().getNodeID();
            int previousID = currentNode.getPreviousNode().getCurrentNode().getNodeID();
            Collection<Segment> segments = currentNode.getCurrentNode().getSegments();
            for(Segment s : segments) {
                if((s.getStart().getNodeID() == currentID && s.getEnd().getNodeID() == previousID) || (s.getStart().getNodeID() == previousID &&s.getEnd().getNodeID() == currentID)){
                    path.add(s);
                    s.highlighted = true;
                    currentNode = currentNode.getPreviousNode();
                }
            }
        }
        Collections.reverse(path);
        this.path = path;
        return path;
    }

    public ArrayList<Segment> getPath() {
        return this.path;
    }
}

    /**public List<Segment> printPath(AStarNode current, Node start) {
        ArrayList<Segment>path = new ArrayList<>();
        Node pathCurrent = current.getCurrentNode();
        Node pathPrevious = current.getPreviousNode().getCurrentNode();
        while(!pathCurrent.equals(start)){

            int pathCurrentID = pathCurrent.getNodeID();
            int pathPreviousID = pathPrevious.getNodeID();
            Collection<Segment> segments = pathCurrent.getSegments();
            for(Segment s : segments) {
                if((s.getStart().getNodeID() == pathCurrentID && s.getEnd().getNodeID() == pathPreviousID) || (s.getStart().getNodeID() == pathPreviousID && s.getEnd().getNodeID() == pathCurrentID)){
                    path.add(s);
                    s.highlighted = true;
                    pathCurrent = pathPrevious;
                }
            }

        }
        Collections.reverse(path);
        for(Segment s : path){
            String roadName = s.getRoad().getName();
            double length = s.getLength();
            //System.out.println(roadName + length);
        }
        return path;
    }**/


    /**public List<Segment> printPath(AStarNode current, Node start) {
        ArrayList<Segment>path = new ArrayList<>();
        Node pathCurrent = current.getCurrentNode();
        //Node pathPrevious = current.getPreviousNode().getCurrentNode();
        while(!pathCurrent.getCurrentNode().equals(start)){
            int currentID = current.getCurrentNode().getNodeID();
            int previousID = current.getPreviousNode().getCurrentNode().getNodeID();
            Collection<Segment> segments = current.getCurrentNode().getSegments();
            for(Segment s : segments) {
                if((s.getStart().getNodeID() == currentID && s.getEnd().getNodeID() == previousID) || (s.getStart().getNodeID() == previousID && s.getEnd().getNodeID() == currentID)){
                    path.add(s);
                    s.highlighted = true;
                }
            }

        }
        Collections.reverse(path);
        for(Segment s : path){
            String roadName = s.getRoad().getName();
            double length = s.getLength();
            //System.out.println(roadName + length);
        }
        return path;
    }**/



    /**public List<Node> printPath(HashMap<Node, AStarNode> visited){
        for (Map.Entry<Node, AStarNode> entry : visited.entrySet()) {
            entry.getKey().getNodeID()
        }
    }**/


    /**public List<Node> quickestPath() {
        HashMap<Node, AStarNode> visited = new HashMap<>();
        PriorityQueue<AStarNode> fringe = new PriorityQueue<>();
        //HashMap<Node, Double> path = new HashMap<Node, Double>();
        initial();
        fringe.add(current);
        while (fringe.size() != 0) {
            AStarNode next = fringe.poll();
            this.current = next;
            if (!visited.containsValue(current)) {
                visited.put(current.getCurrentNode(), current);
                this.previous = current;
            }
            if (current.getCurrentNode().equals(end)) {
                //reachedEnd = true;
                break;
            }
            // adding neighbours to fringe
            for (Node adjcent : current.getCurrentNode().getAdjcent()) {
                int adjcentID = adjcent.getNodeID();
                Collection<Segment> adjcentSegments = adjcent.getSegments(); // get only outgoing
                for (Segment s : adjcentSegments) {
                    if (s.getEnd().getNodeID() == adjcentID) { // actually node id @ end of segment
                        double costSoFar = segmentCost(adjcentSegments, adjcentID) + current.getCostSoFar();
                        double estimatedCost = adjcent.h(end);
                        //this.previous = current;
                        AStarNode newCurrent = new AStarNode(adjcent, previous, costSoFar, estimatedCost);
                        fringe.add(newCurrent);

                    }
                }
            }
        }

        return null;
    }**/



