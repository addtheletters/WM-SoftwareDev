package edu.wm.cs.cs301.benzhang.amazebybenzhang.generation;

import android.graphics.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Provides an alternate method to create a maze with the given dimensions (width, height).
 * 
 * <br>
 * The focus of this class is its implementation of a variant of Kruskal's algorithm to join together cells into 'trees',
 * accumulating into a single spanning tree which contains all cells. 
 * <br>
 * The cells are nodes of the graph and tree; edges represent where it is possible to move from one cell to a neighbor.
 * An 'edge' then implies that two cells are adjacent and that the wall between them has been deleted.
 * A 'tree' is more of a set than a tree, a group of cells which have been connected, and is stored as neither a set nor a tree.
 * <br>
 * A form of union-find structure is used to represent the 'forest' of trees, utilizing a HashMap and HashSet. 
 * Each cell is initialized in its own 'tree' set; as cells are connected with 'edges' (walls between them are deleted) their 'tree' sets are union'ed together.
 * <br>
 * This implementation should respect rooms placed by the superclass methods.
 * 
 * @author Ben Zhang
 *
 */
public class MazeBuilderKruskal extends MazeBuilder {
	
	/**
	 * Default constructor creates a non-deterministic builder.
	 */
	public MazeBuilderKruskal() {
		this(false);
	}
	
	public MazeBuilderKruskal(boolean deterministic) {
		super(deterministic);
		System.out.println("MazeBuilderKruskal uses Kruskal's algorithm to generate the maze.");
	}
	
	/**
	 * This method generates pathways into the maze by using a variant of Kruskal's algorithm. Additional detail provided in class description ({@link MazeBuilderKruskal}).
	 */
	@Override
	protected void generatePathways(){
		//System.out.println("Width and height are " + width + "," + height);
		
		// Create a forest set of all points as trees/sets with one element each
		HashMap<Point, Integer> forest = createInitialForest(); // This map tracks which 'tree' set each point currently belongs to.
		HashSet<Integer> existingTrees; // Tracks which 'trees' are still in existence. Initially, contains a tree for every cell.
		// As trees are merged, cells are adopted into the 'tree' of the cells they merge with, and so the number of distinct trees drops.
		existingTrees = new HashSet<Integer>(forest.values());
		
		//int wallsDestroyed = 0; // for debug purposes
		// Merge trees until there is only 1 tree containing all cells
		while( existingTrees.size() > 1){
			//System.out.println( "existing tree count is " + existingTrees.size() );
			// grab a random tree from the ones in existence
			Integer[] indexedTrees = new Integer[existingTrees.size()];
			existingTrees.toArray(indexedTrees); // convert to an array so one can be chosen randomly by its index
			int examinedTree = indexedTrees[random.nextIntWithinInterval(0, indexedTrees.length - 1)];
	
			List<Wall> destroyableWalls = findTreeDestroyableWalls(examinedTree, forest);
			// could move destoyableWalls outside the loop, and do addAll(findSetDestroyableWalls()); // like how Prim does it
			if(destroyableWalls.size() > 0){
				//wallsDestroyed ++; // for debug purposes
				//System.out.println("There are breakable walls. Destroying one. Total " + wallsDestroyed);
				// pick one wall randomly and destroy it
				Wall selected = destroyableWalls.remove( random.nextIntWithinInterval(0, destroyableWalls.size()-1) );
				assert( cells.canGo(selected) );
				cells.deleteWall(selected);
				
				// do the union to merge the 'trees'
				joinTree( forest, examinedTree, forest.get(new Point(selected.getNeighborX(), selected.getNeighborY())) );
				// update the set tracking which trees still exist
				existingTrees = new HashSet<Integer>(forest.values());
			}
		}
		//System.out.println("Walls destroyed: " + wallsDestroyed); // for debug purposes
	}
	
	/**
	 * Creates an initial HashMap of grid points to 'tree' numbers which will track the forest.
	 * @return map of points to integers representing the forest
	 */
	protected HashMap<Point, Integer> createInitialForest(){
		HashMap<Point, Integer> forest = new HashMap<Point, Integer>();
		int treeNum = 0; // Each 'tree' gets a unique tree number > 0
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				treeNum ++;
				forest.put( new Point(i,j), treeNum );
			}
		}
		return forest;
	}
	
	/**
	 * Joins the cells in joinTree to the cells in baseTree, similar to a set union operation. See description of {@link MazeBuilderKruskal#generatePathways()}.
	 * Resulting cells all have an identifying tree integer of baseTree.
	 * @param forest defines the forest by describing which tree each cell belongs to
	 * @param baseTree the integer representing the base tree unto which the other tree will be joined
	 * @param joinTree the integer representing the tree which will be joined to the base tree
	 */
	protected void joinTree( HashMap<Point, Integer> forest, int baseTree, int joinTree ){
		for(Point p: forest.keySet()){
			if( forest.get(p) == joinTree ){
				forest.put(p, baseTree);
			}
		}
	}
	
	/**
	 * Finds all maze walls bordering a tree which could be destroyed to link with another tree.
	 * @param tree the integer representing the tree to be examined
	 * @param forest defines the forest by describing which tree each cell belongs to
	 * @return list of all valid walls neighboring any of the tree's cells
	 */
	protected List<Wall> findTreeDestroyableWalls( int tree, HashMap<Point, Integer> forest ){
		List<Wall> neighborWalls = new ArrayList<Wall>();
		for(Point p : forest.keySet()){ 
			if(forest.get(p) == tree){ // for all points in the tree
				for(CardinalDirection cd : CardinalDirection.values()){
					addNeighborIfValid( neighborWalls, p, cd, forest );
				}
			}
		}
		return neighborWalls;
	}
	
	/**
	 * Adds a wall specified by the cell and direction to the collection if it belongs to a different tree from the one provided.
	 * @param group the collection which valid walls will be added to
	 * @param cell the central cell
	 * @param dir the direction in which to attempt to find a valid wall
	 * @param forest defines the forest by describing which tree each cell belongs to
	 */
	protected void addNeighborIfValid( Collection<Wall> group, Point cell, CardinalDirection dir, HashMap<Point, Integer> forest ){
		Point neighbor = new Point(cell.x + dir.getDirection()[0] , cell.y + dir.getDirection()[1]);
		if( !pointInCellGrid(neighbor) ){
			//System.out.println("'Neighbor' is not in grid: " + neighbor.x + "," + neighbor.y);
			return;
		}
		if( forest.get(neighbor) != forest.get(cell) ){
			Wall neighborWall = new Wall(cell.x, cell.y, dir) ;
			if( cells.canGo(neighborWall) ){
				group.add( neighborWall );
			}
			else{
				//System.out.println("cells determined that neighbor wall cannot be destroyed. " + neighborWall );
			}
		}
		else{
			//System.out.println("Neighbor is in same tree already.");
		}
	}
	
	/**
	 * Checks that a point is within the maze's grid of cells.
	 * @param p the point to check
	 * @return true if the point is within the space of the grid; false otherwise
	 */
	protected boolean pointInCellGrid( Point p ){
		return (p.x >= 0) && (p.y >= 0) && (p.x < width) && (p.y < height);
	}
	
}
