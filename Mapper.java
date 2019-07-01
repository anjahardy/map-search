import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;

/**
 * This is the main class for the mapping program. It extends the GUI abstract
 * class and implements all the methods necessary, as well as having a main
 * function.
 * 
 * @author tony
 */
public class Mapper extends GUI {
	public static final Color NODE_COLOUR = new Color(77, 113, 255);
	public static final Color SEGMENT_COLOUR = new Color(130, 130, 130);
	public static final Color HIGHLIGHT_COLOUR = new Color(255, 219, 77);

	// these two constants define the size of the node squares at different zoom
	// levels; the equation used is node size = NODE_INTERCEPT + NODE_GRADIENT *
	// log(scale)
	public static final int NODE_INTERCEPT = 1;
	public static final double NODE_GRADIENT = 0.8;

	// defines how much you move per button press, and is dependent on scale.
	public static final double MOVE_AMOUNT = 100;
	// defines how much you zoom in/out per button press, and the maximum and
	// minimum zoom levels.
	public static final double ZOOM_FACTOR = 1.3;
	public static final double MIN_ZOOM = 1, MAX_ZOOM = 200;

	// how far away from a node you can click before it isn't counted.
	public static final double MAX_CLICKED_DISTANCE = 0.15;

	// these two define the 'view' of the program, ie. where you're looking and
	// how zoomed in you are.
	private Location origin;
	private double scale;

	ArrayList <Node> points = new ArrayList<>(Arrays.asList(null, null));

	AStar a;

	Node chosen = null;

	//public Float infinity = Float.POSITIVE_INFINITY;

	HashSet<Node>articulationPointsList = new HashSet<>();

	// our data structures.
	private Graph graph;
	private Trie trie;

	Node root = null;

	@Override
	protected void redraw(Graphics g) {
		if (graph != null)
			graph.draw(g, getDrawingAreaDimension(), origin, scale);
	}

	@Override
	protected void onClick(MouseEvent e) {

		/**Node from = graph.getNode(34060); // needs to be populated by finding a node based on the click
		Node to = graph.getNode(18117); // same

		AStar a = new AStar(from, to);

		List<Node> path = a.quickestPath();

		System.out.println(((List) path).toString());**/

		// ------------------------

		Location clicked = Location.newFromPoint(e.getPoint(), origin, scale);//find the closest node.
		//find the closest node
		double bestDist = Double.MAX_VALUE;
		Node closest = null;

		for (Node node : graph.nodes.values()) {
		 	double distance = clicked.distance(node.location);
		 	if (distance < bestDist) {
		 		bestDist = distance;
		 		closest = node;
		 	}
		 }

		if(chosen != null){
			a = new AStar(closest, chosen);
			a.quickestPath();
			printText();
			redraw();
			closest = null;
			chosen = null;
		}
		this.chosen = closest;

		// if it's close enough, highlight it and show some information.
		if (clicked.distance(closest.location) < MAX_CLICKED_DISTANCE) {
			graph.setHighlight(closest);
			getTextOutputArea().setText(closest.toString());
		}

		/**if(chosen != null){
			a = new AStar(closest, chosen);
			a.quickestPath();
		}
		this.chosen = closest;**/

		/**if(chosen != null) {
			a = new AStar(closest, chosen);
			a.quickestPath();
			redraw();
			closest = null;
			chosen = null;
		}
		this.chosen = closest;**/

		/**if(points.get(0)==null){
			points.add(0, closest);
		}
		else if(points.get(1)==null) {
			points.add(1, closest);
		}
		else{
			a = new AStar(points.get(0), points.get(1));
			a.quickestPath();
			redraw();
			points.set(0, null);
			points.set(1, null);
		}**/

		/**points.add(closest);
		if (points.size()==2){
			a = new AStar (points.get(0), points.get(1));
			a.quickestPath();
			redraw();
			points.clear();
		}*/

		/**if(points.size() != 2){
			points.add(closest);
		}
		else {
			a = new AStar (points.get(0), points.get(1));
			a.quickestPath();
			points.clear();
		}

		if(chosen != null){
			a = new AStar(closest, chosen);
			a.quickestPath();
		}
		this.chosen = closest;**/

		/**if(points.get(0) == null){
			points.set(0, getNode(Location.newFromPoint(e.getPoint(), origin, scale)));

		}else if(points.get(1) == null){
			points.set(1, getNode(Location.newFromPoint(e.getPoint(), origin, scale)));

		}else{
			// find the path
			a = new AStar(points.get(0), points.get(1));
			a.quickestPath();

			// get the path - return nodes
			//List<Segment> path = a.quickestPath();

			//System.out.println(path);
			points.set(0, null);
			points.set(1, null);
		}
		System.out.println(points);**/
	}

	@Override
	protected void onSearch() {
		if (trie == null)
			return;

		// get the search query and run it through the trie.
		String query = getSearchBox().getText();
		Collection<Road> selected = trie.get(query);

		// figure out if any of our selected roads exactly matches the search
		// query. if so, as per the specification, we should only highlight
		// exact matches. there may be (and are) many exact matches, however, so
		// we have to do this carefully.
		boolean exactMatch = false;
		for (Road road : selected)
			if (road.name.equals(query))
				exactMatch = true;

		// make a set of all the roads that match exactly, and make this our new
		// selected set.
		if (exactMatch) {
			Collection<Road> exactMatches = new HashSet<>();
			for (Road road : selected)
				if (road.name.equals(query))
					exactMatches.add(road);
			selected = exactMatches;
		}

		// set the highlighted roads.
		graph.setHighlight(selected);

		// now build the string for display. we filter out duplicates by putting
		// it through a set first, and then combine it.
		Collection<String> names = new HashSet<>();
		for (Road road : selected)
			names.add(road.name);
		String str = "";
		for (String name : names)
			str += name + "; ";

		if (str.length() != 0)
			str = str.substring(0, str.length() - 2);
		getTextOutputArea().setText(str);
	}

	public Node getNode(Location loc){

		for(Node n : graph.nodes.values()){
			if(n.getLocation().isClose(loc, 0.1)){
				return n;
			}

		}

		return null; // no node was found
	}

	@Override
	protected void onMove(Move m) {
		if (m == GUI.Move.NORTH) {
			origin = origin.moveBy(0, MOVE_AMOUNT / scale);
		} else if (m == GUI.Move.SOUTH) {
			origin = origin.moveBy(0, -MOVE_AMOUNT / scale);
		} else if (m == GUI.Move.EAST) {
			origin = origin.moveBy(MOVE_AMOUNT / scale, 0);
		} else if (m == GUI.Move.WEST) {
			origin = origin.moveBy(-MOVE_AMOUNT / scale, 0);
		} else if (m == GUI.Move.ZOOM_IN) {
			if (scale < MAX_ZOOM) {
				// yes, this does allow you to go slightly over/under the
				// max/min scale, but it means that we always zoom exactly to
				// the centre.
				scaleOrigin(true);
				scale *= ZOOM_FACTOR;
			}
		} else if (m == GUI.Move.ZOOM_OUT) {
			if (scale > MIN_ZOOM) {
				scaleOrigin(false);
				scale /= ZOOM_FACTOR;
			}
		}
	}

	@Override
	protected void onLoad(File nodes, File roads, File segments, File polygons) {
		graph = new Graph(nodes, roads, segments, polygons);
		trie = new Trie(graph.roads.values());
		origin = new Location(-250, 250); // close enough
		scale = 1;
	}

	/**
	 * This method does the nasty logic of making sure we always zoom into/out
	 * of the centre of the screen. It assumes that scale has just been updated
	 * to be either scale * ZOOM_FACTOR (zooming in) or scale / ZOOM_FACTOR
	 * (zooming out). The passed boolean should correspond to this, ie. be true
	 * if the scale was just increased.
	 */
	private void scaleOrigin(boolean zoomIn) {
		Dimension area = getDrawingAreaDimension();
		double zoom = zoomIn ? 1 / ZOOM_FACTOR : ZOOM_FACTOR;

		int dx = (int) ((area.width - (area.width * zoom)) / 2);
		int dy = (int) ((area.height - (area.height * zoom)) / 2);

		origin = Location.newFromPoint(new Point(dx, dy), origin, scale);
	}

	public static void main(String[] args) {
		new Mapper();
	}

	public void printText(){
		double totalDist = 0;
		String roadName = "";
		double length = 0;
		for (Segment seg : a.path){
			roadName = seg.getRoad().getName();
			length = seg.getLength();
			totalDist += seg.getLength();
			getTextOutputArea().append(roadName + ": " + length + "km -> ");
		}
		getTextOutputArea().append("Total Distance : " + totalDist + "km");
	}

	protected void articulationPoints() {
		root = graph.getNode(10015);
		root.depth = 0;
		int numSubTrees = 0;
		for(Node n : root.getAdjacent()){
			if(n.depth == -1){
				iterArtPts(n, 1, root);
				numSubTrees++;
			}
			if(numSubTrees>1){
				articulationPointsList.add(root);
			}
		}
		for(Node node : articulationPointsList){
			graph.setHighlight(node);
			redraw();
		}
	}

	protected void iterArtPts(Node firstNode, int depth, Node parent){
		Stack<ArticulationPointElement> stack = new Stack<>();//create a stack of articulation point elements
		ArticulationPointElement initial = new ArticulationPointElement(firstNode, depth, parent);//create an initial element
		stack.push(initial);//push the initial element onto the stack
		while(stack.empty()==false){//initialize a while loop 'while the stack is not empty'
			ArticulationPointElement peekedElement = stack.peek();//peek at the top element and store in a variable
			Node n = peekedElement.getNode();//get the node in the element and assign to a variable
			if(n.depth==-1){//initialize if condition 'if the depth is infinity (choose an impossible depth)'
				n.depth = depth;//the node depth = depth
				n.reachback = depth;//the node reachback = depth
				for(Node child : n.getAdjacent()){//initialize for loop 'for all the children of the node
					if(child != peekedElement.getParent()){//initialize if condition 'if child is not equal to the parent of the node
						n.children.add(child);//ass child to the children of the node (a field created in the node class
					}
				}
			}
			else if(n.children.isEmpty() == false){//initialize else if condition 'else if there are children of the node
				for(Node chosenChild : n.children){//choose a child from children
					if(chosenChild.depth != -1){//initialize if condition 'if the depth of the chosen child is not infinity
						n.reachback = Math.min(chosenChild.depth, n.reachback);//set the reachback of the node to be the minimum out of the depth of the chosen child or the current node reachback
					}
					else{//initialize else condition
						ArticulationPointElement chosenChildForStack = new ArticulationPointElement(chosenChild, depth+1, n);//make the chosen child into a new articulation point element
						stack.push(chosenChildForStack);//push the new element onto the stack
					}
					n.children.remove(chosenChild);//remove the chosen child from the children of the node
				}
			}
			else{//initialize else condition
				if(n != firstNode){//initialize if condition 'if node does not equal the first node passed into the method
					n.parent.reachback = Math.min(n.reachback, n.parent.reachback);//the reachback of the parent of the node can be set to the minimum of the reachback of the node or the reachback of the parent of the node
					if(n.reachback >= n.parent.depth){//initialize if condition 'if the reachback of the node is greater than or equal to the depth of the parent of the node'
						articulationPointsList.add(n.parent);//add the parent of the node to the list of articulation points
					}
				}
			}
			stack.pop();//pop that element from the stack
		}
	}
}

// code for COMP261 assignments