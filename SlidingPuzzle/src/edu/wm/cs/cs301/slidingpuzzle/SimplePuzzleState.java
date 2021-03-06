package edu.wm.cs.cs301.slidingpuzzle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.awt.Point;

/**
 * A simple implementation of PuzzleState.
 * 
 * @author Ben Zhang
 * 
 */
public class SimplePuzzleState implements PuzzleState, Comparable<SimplePuzzleState> {

	private int[][] current; // data of current state represented by this object
	private Operation lastOperation; // the last operation performed 
	private ArrayDeque<PuzzleState> history; // tracking previous states
	// front is initial, end is last move
	
	/* 
	 * Constructors
	 */
	/**
	 * Constructs a SimplePuzzleState with null internals.
	 */
	public SimplePuzzleState() {
		current = null;
		lastOperation = null;
		history = null;
	}
	
	/**
	 * Constructs and initializes a SimplePuzzleState using the provided dimensions and number of empty slots.
	 * @param dim the number of rows per tile / column
	 * @param emptySlots the number of slots to leave empty
	 */
	public SimplePuzzleState(int dim, int emptySlots){
		setToInitialState(dim, emptySlots);
	}
	
	/**
	 * Constructs a SimplePuzzleState descendant from a previous state
	 * @param last the previous state
	 * @param tiles the new status of the puzzle board
	 * @param op the operation which changed the last state to the new status
	 */
	public SimplePuzzleState(SimplePuzzleState last, int[][] tiles, Operation op){
		this.current = tiles;
		this.lastOperation = op;
		if(last != null){
			this.history = new ArrayDeque<PuzzleState>(last.getHistory());
			this.history.addLast(last);
		}
		else{
			this.history = new ArrayDeque<PuzzleState>();
		}
		//System.out.println(this.history.size());
	}
	
	
	/*
	 * Static methods to organize / avoid repetition of code for internal calculation 
	 */
	/**
	 * Returns the number of filled tiles in the puzzle, 
	 * given its dimensions and number of empty slots.
	 * @param dim the number of rows / columns
	 * @param emptySlots the number of slots left empty
	 * @return the number of filled tiles in the puzzle
	 */
	private static int numTiles(int dim, int emptySlots){
		return (dim * dim) - emptySlots;
	}
	
	/**
	 * Returns an altered version of the original matrix with the values at (row1, col1) and (row2, col2) swapped.
	 * Used when creating new PuzzleStates in move().
	 * @param original matrix of the original values
	 * @param row1 row of first value being swapped
	 * @param col1 column of first value being swapped
	 * @param row2 row of second value being swapped
	 * @param col2 column of second value being swapped
	 * @return altered matrix with the swap completed
	 */
	private static int[][] swap(int[][] original, int row1, int col1, int row2, int col2){
		//System.out.println("Swapping " + row1 + "," + col1 + " with " + row2 + "," + col2 );
		int[][] altered = new int[original.length][original[0].length];
		for(int i = 0; i < original.length; i++){
			for(int j = 0; j < original[0].length; j++){
				if(i == row1 && j == col1){
					//System.out.println("found 1");
					altered[i][j] = original[row2][col2];
				}
				else if (i == row2 && j == col2){
					//System.out.println("found 2");
					altered[i][j] = original[row1][col1];
				}
				else{
					//System.out.println("copy");
					altered[i][j] = original[i][j];
				}
			}
		}
		return altered;
	}
	
	/**
	 * Returns the difference in row a piece would experience if shifted by the operation.
	 * @param op the operation
	 * @return the amount by which the operation shifts vertically
	 */
	private static int getRowShift(Operation op){
		switch (op) {
			case MOVEDOWN:
				return 1;
			case MOVEUP:
				return -1;
			default:
				return 0;
		}
	}
	
	/**
	 * Returns the difference in column a piece would experience if shifted by the operation.
	 * @param op the operations
	 * @return the amount by which the operation shifts horizontally
	 */
	private static int getColShift(Operation op){
		switch (op) {
			case MOVELEFT:
				return -1;
			case MOVERIGHT:
				return 1;
			default:
				return 0;
		}
	}
	
	
	/**
	 * Utility class to make shuffling easier
	 * @author Ben
	 */
	private class Move{
		Point pos;
		Operation op;
		public Move(Point p, Operation o){
			this.pos = p;
			this.op = o;
		}
		public int hashCode(){
			return (int)(Math.pow(2, pos.hashCode()) * Math.pow(3, op.hashCode()));
		}
		public boolean equals(Object obj){
			if(obj instanceof Move){
				return this.pos.equals(((Move) obj).pos) && this.op.equals(((Move) obj).op);
			}
			return false;
		}
	}
	
	
	@Override
	public void setToInitialState(int dimension, int numberOfEmptySlots) {
		history = new ArrayDeque<PuzzleState>();
		lastOperation = null;
		
		current = new int[dimension][dimension];
		
		int nslots = dimension * dimension;
		int ntiles = numTiles(dimension, numberOfEmptySlots);
		int filledTiles = 0;
		while(filledTiles < nslots){
			if(filledTiles < ntiles){
				current[filledTiles / dimension][ filledTiles % dimension ] = filledTiles+1;
			}
			else{
				current[filledTiles / dimension][ filledTiles % dimension ] = 0;
			}
			filledTiles ++;
		}
	}
	
	@Override
	public int getValue(int row, int column) {
		return current[row][column];
	}
	
	@Override
	public PuzzleState getParent(){
		return history.peekLast();
	}

	@Override
	public Operation getOperation() {
		return lastOperation;
	}

	@Override
	public int getPathLength() {
		return history.size();
	}
	
	/**
	 * Gets the history of the puzzle (the states it has held in order to reach the current state)
	 * @return deque containing the history, with the front holding the initial state and the end holding the last state
	 */
	public ArrayDeque<PuzzleState> getHistory(){
		return history;
	}
	
	/**
	 * Checks if a (row,col) position is on the puzzle grid.
	 * @param row of the position to check
	 * @param col column of the position to check
	 * @return true if the position is on the grid, false otherwise
	 */
	private boolean positionValid(int row, int col){
		return (row >= 0 && col >= 0) && (row < current.length && col < current[0].length);
	}
	
	@Override
	public boolean isEmpty(int row, int column) {
		return current[row][column] == 0;
	}
	
	/**
	 * Count the number of empty spaces in the current grid. This should stay constant.
	 * @return the number of empty spaces 
	 */
	private int countEmpty(){
		int count = 0;
		for(int i = 0; i < current.length; i++){
			for(int j = 0; j < current.length; j++){
				if(isEmpty(i, j))
					count ++;
			}
		}
		return count;
	}
	
	/**
	 * Checks if a (row,col) position is on the grid and empty.
	 * @param row of the position to check
	 * @param col column of the position to check
	 * @return true if the position can be moved to, false otherwise
	 */
	private boolean canMoveTo(int row, int col){
		return positionValid(row,col) && isEmpty(row, col);
	}
	
	/**
	 * Checks if a (row,col) position is on the grid and holds a tile.
	 * @param row of the position to check
	 * @param col column of the position to check
	 * @return true if the position can be moved from, false otherwise
	 */
	private boolean canMoveFrom(int row, int col){
		return positionValid(row, col) && !isEmpty(row, col);
	}
	
	/**
	 * Finds all operations that are valid to perform on a tile at (row, col).
	 * @param row of the position to check
	 * @param col column of the position to check
	 * @return list of valid operations
	 */
	private List<Operation> findOpsFrom(int row, int col){
		ArrayList<Operation> candidates = new ArrayList<Operation>();
		if(!positionValid(row, col)) return candidates; // no valid moves if start position is off the grid
		if(canMoveTo(row + 1, col)) candidates.add(Operation.MOVEDOWN);
		if(canMoveTo(row - 1, col)) candidates.add(Operation.MOVEUP);
		if(canMoveTo(row, col + 1)) candidates.add(Operation.MOVERIGHT);
		if(canMoveTo(row, col - 1)) candidates.add(Operation.MOVELEFT);
		return candidates;
	}
	
	/**
	 * Finds all moves that are valid which shift a piece onto (row, col).
	 * @param row of the position to check
	 * @param col column of the position to check
	 * @return list of valid moves
	 */
	private List<Move> findMovesTo(int row, int col){
		ArrayList<Move> candidates = new ArrayList<Move>();
		if(!positionValid(row, col)) return candidates; // no valid moves if start position is off the grid
		if(canMoveFrom(row + 1, col)) candidates.add(new Move(new Point(row+1, col), Operation.MOVEUP));
		if(canMoveFrom(row - 1, col)) candidates.add(new Move(new Point(row-1, col), Operation.MOVEDOWN));
		if(canMoveFrom(row, col + 1)) candidates.add(new Move(new Point(row, col+1), Operation.MOVELEFT));
		if(canMoveFrom(row, col - 1)) candidates.add(new Move(new Point(row, col-1), Operation.MOVERIGHT));
		return candidates;
	}
	
	/**
	 * Finds all valid moves to anywhere on the board.
	 * @return list of valid moves
	 */
	private List<Move> findAllMoves(){
		ArrayList<Move> moves = new ArrayList<Move>();
		for(int i = 0; i < current.length; i++){
			for(int j = 0; j < current[0].length; j++){
				if(isEmpty(i, j)){
					moves.addAll( findMovesTo(i,j) );
				}
			}
		}
		return moves;
	}

	@Override
	public PuzzleState move(int row, int column, Operation op) {
		if(!positionValid(row, column)){
			//System.err.println("Move position invalid (" + row + "," + column + ").");
			return null;
		}
		List<Operation> possible = findOpsFrom(row, column);
		if(possible.contains(op)){
			int[][] changed = swap(this.current, row, column, row+getRowShift(op), column+getColShift(op));
			return new SimplePuzzleState(this, changed, op);
		}
		return null;
	}
	
	// shortcut so using the Move class is easier
	private PuzzleState makeMove(Move m){
		return this.move(m.pos.x, m.pos.y, m.op);
	}

	@Override
	public PuzzleState flip(int startRow, int startColumn, int endRow, int endColumn) {
		// BFS in order to find a way from start to end
		Point start = new Point(startRow, startColumn);
		Point target = new Point(endRow, endColumn);
		
		HashMap<Point, PuzzleState> trail = new HashMap<Point, PuzzleState>();
		trail.put(start, this);
		
		ArrayDeque<Point> searchqueue = new ArrayDeque<Point>();
		searchqueue.push( start );
		while(searchqueue.size() > 0){
			Point loc = searchqueue.pop();
			if(loc.equals(target)){
				return trail.get(loc); // found!
			}
			List<Operation> choices = findOpsFrom(loc.x, loc.y);
			for(Operation choice : choices){
				Point cloc = new Point(loc.x + getRowShift(choice), loc.y + getColShift(choice) );
				if(!trail.containsKey(cloc)){
					trail.put(cloc, trail.get(loc).move(loc.x, loc.y, choice));
					searchqueue.push(cloc);
				}
			}
		}
		return null;
	}

	@Override
	public PuzzleState shuffleBoard(int pathLength) {
		PuzzleState shuffled = this;
		int movesTaken = 0;
		while(movesTaken < pathLength){
			movesTaken ++;
			List<Move> moves = ((SimplePuzzleState)shuffled).findAllMoves();
			// find all the states that our possible moves produce
			List<PuzzleState> possibleStates = new ArrayList<PuzzleState>();
			for(int i = 0; i < moves.size(); i++){
				possibleStates.add( ((SimplePuzzleState)shuffled).makeMove(moves.get(i)) );
			}
			// remove states that result in previously-attained board
			List<PuzzleState> toRemove = new ArrayList<PuzzleState>();
			for(PuzzleState ps : possibleStates){
				if(((SimplePuzzleState)shuffled).getHistory().contains(ps))
					toRemove.add(ps);
			}
			//System.out.println("Removing " + toRemove.size());
			possibleStates.removeAll(toRemove);
			// randomly choose a move if there are any possible ones left
			if(possibleStates.size() > 0)
				shuffled = possibleStates.get( (int)(Math.random() * possibleStates.size()) ); // choose a move randomly
		}
		return shuffled; // failure to shuffle means there are no possible moves. return original again?
	}
	
	/**
	 * Uses the heuristic provided by {@link SimplePuzzleState#correctness()} and searches for a solution to the current puzzle state.
	 * Returns null if no solution is found.
	 * @return SimplePuzzleState representing a solved version of the puzzle, with {@link SimplePuzzleState#history} representing steps taken to solve
	 */
	public SimplePuzzleState solveAStar(){
		SimplePuzzleState target = new SimplePuzzleState(current.length, countEmpty());
		List<SimplePuzzleState> visited = new ArrayList<SimplePuzzleState>();
		PriorityQueue<SimplePuzzleState> pqueue = new PriorityQueue<SimplePuzzleState>();
		pqueue.add( new SimplePuzzleState(null, this.current, null) );
		while(pqueue.size() > 0){
			//System.out.println("Queue is of size " + pqueue.size());
			SimplePuzzleState now = pqueue.poll();
			//System.out.println("Examining state \n" + now);
			if(now.equals(target)){
				return now;
			}
			List<Move> options = now.findAllMoves();
			//List<SimplePuzzleState> possibleStates = new ArrayList<SimplePuzzleState>();
			for(Move mv : options){
				SimplePuzzleState result = (SimplePuzzleState)now.makeMove(mv);
				int ind = visited.indexOf(result);
				if( ind > -1 ){
//					SimplePuzzleState old = visited.get(ind);
//					if( result.getPathLength() < old.getPathLength() ){
//						System.out.println("Found shorter " + result.getPathLength());
//						visited.set(ind, result);
//						pqueue.add(result);
//					}
//					else{
//						continue; // skips adding to queue
//					}
				}
				else{
					visited.add(result);
					pqueue.add(result);
				}
			}
		}
		return null;
	}
	
	/**
	 * Heuristic to determine how close the current state is to being solved
	 * @return 
	 */
	private int correctness(){
		int total = 0;
		for(int i = 0; i < current.length; i++){
			for(int j = 0; j < current[0].length; j++){
				if(current[i][j] == 0){
					continue;
				}
				else{
					int srow = (current[i][j] - 1) / current.length;
					int scol = (current[i][j] - 1) % current.length;
					System.out.println("Current is " + current[i][j] + " located at " + srow + "," + scol + " which is " + (int)(Math.abs(srow - i) + Math.abs(scol - j)) + " wrong.");
					total = total - (int)(Math.abs(srow - i) + Math.abs(scol - j)); 
//					if(current[i][j] == (i * current[0].length + j + 1)){
//						total += 1;
//					}
				}
			}
		}
		return 2 * total - getPathLength(); // the path being too long probably means non-helpful moves were taken
	}
	
	@Override
	public int compareTo(SimplePuzzleState o) {
		return o.correctness() - this.correctness();
	}
	
	@Override
	public int hashCode() {
		return ("sps"+Arrays.deepToString(this.current)).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SimplePuzzleState){
			return Arrays.deepEquals(this.current, ((SimplePuzzleState) obj).current);
			//return this.hashCode() == obj.hashCode();
		}
		return false;
	}
	
	// For easier debugging
	public String toString(){
		String out =	"=SimplePuzzleState=\n";
		for(int[] a : current)
			out += Arrays.toString(a) + "\n";
		out += "hashed: " + this.hashCode() + "\n";
		out += "path length is " + this.getPathLength() + "\n";
		out +=			"===================";
		return out;
	}
	
}
