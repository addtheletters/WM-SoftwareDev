package edu.wm.cs.cs301.slidingpuzzle;
/**
 * A puzzle state represents the current arrangement of tiles on a sliding puzzle.
 * The puzzle board is a square, e.g. a 4x4 board. 
 * The number of empty slots is at least 1 and at most 3.
 * The encoding assumes that an empty slot (or blank tile) is represented with a zero
 * number. The state of the puzzle is the arrangement of tiles on the board.
 * 
 * The PuzzleStateInterface provides four move operations: up, down, left and right, by 
 * fixing a naming convention with an enum type Operation. 
 * An implementing class encapsulates the knowledge about the rules of the game, 
 * i.e. which move is possible in which state.
 * A move operation moves a tile from one position to an adjacent empty position.
 * It is also possible to simply flip two tiles which are not necessarily adjacent.
 * The flip operation is used to support dragging a tile over multiple empty slots 
 * to a particular position.
 * 
 * A puzzle state can be used as a pure data container:
 * a) to store a tuple (state1,operation,state2) with the notion 
 * of a parent and operation. A particular state would store state2 as its state, state1 as 
 * its parent and operation as its operation. Get methods allow to retrieve this
 * information.
 * b) to store the length of a path from the initial state to the state
 * represented by the current puzzle state. This is a straightforward by-product of the 
 * computation of moves and successor puzzle states as the path length of the 
 * successor is just an increment by one. 
 * 
 * The parent is stored to support a shuffle function and an automatic (auto mode) function.
 * The shuffle function allows to create a random puzzle that needs a 
 * particular number of steps to get back to the solution.
 * The auto mode function provides a single step towards the solution and 
 * is useful to implement at GUI function that guides a user towards the solution.
 *  
 * Note that for a game, one would create the initial state and then
 * work from that state with move, flip or shuffle operations to obtain
 * new states.
 * 
 * Set-methods are absent for a good reason as they are not necessary
 * and their absence guarantees that the internal connection between states
 * with the parent relationship can not be invalidated by external calls
 * to overwrite value settings.
 * 
 * The intended usage is for a 4x4 board with 1, 2, or 3 empty slots.
 * 
 * @author Peter Kemper
 *
 */
public interface PuzzleState {
	/**
	 * The puzzle allows for four moves whose names are fixed with an enum type 
	 * Operation. Note that MOVERIGHT means that a tile moves to the right and
	 * the slot to its right need to be empty for this to be possible. 
	 */
	public enum Operation{ MOVERIGHT, MOVELEFT, MOVEUP, MOVEDOWN } ;
	/**
	 * Sets or resets PuzzleState object to the initial (final) state of a game
	 * with the given dimension and number of empty slots.
	 * The initial state is the same as the final state. All tiles are ordered
	 * from top-left to bottom-right, empty slots are at the bottom-right.
	 * Parent and operation are set to null.
	 * @param dimension is the number of tiles per row (or column)
	 * @param numberOfEmptySlots is a number between 1 and 3
	 */
	public void setToInitialState(int dimension, int numberOfEmptySlots) ;
	/**
	 * Get the value of the tile at the given row and column position. 
	 * An empty slot is represented as 0. 
	 * The row, column index starts at 0. The top-left position is (0,0). 
	 * @param row is the row index, range 0,1,..., dimension-1
	 * @param column is the column index, range 0,1,..., dimension-1
	 * @return value for the tile at the given position, range 0,1,...,dimension^2-1
	 */
	public int getValue(int row, int column);
	/**
	 * Get the parent state which is the state from which the current state was reached.
	 * The parent state is useful if one wants to represent
	 * that a puzzle state A moves to puzzle state B with a particular move operation.
	 * For this example B.getParent() returns A. Obtaining A is useful to navigate back on
	 * a path from a current state to a initial state where all tiles are sorted.
	 * The initial state does not have a parent.
	 * @return a previously stored parent state or null if the state is the initial state.
	 */
	public PuzzleState getParent() ;

	/**
	 * Retrieves the operation by which this state is reached.
	 * The operation is set when applying move or flip operations.
	 * The initial state has no operation. 
	 * @return previously stored Operation or null if there is none.
	 */
	public Operation getOperation() ;

	/**
	 * Get length of the path from initial state to the current state.
	 * A path is a sequence of states that result from a series of move operations
	 * that are sequentially applied to the initial state to get to the current state.
	 * The initial state has a path length of 0, states that are reached with one move 
	 * have path length of one and so forth.
	 * A flip operation is a GUI short cut and is internally represented 
	 * with a sequence of move operations.
	 * @return the number of move operations from initial state to this state, 
	 * the returned value is greater or equal 0
	 */
	public int getPathLength() ;

	/**
	 * Checks if it is possible to move a tile from the given position in the given direction.
	 * If it is, the method returns a new instance of PuzzleState where the parent state is set
	 * to this object, i.e. the current state before the move, the operation is set to op and 
	 * the state is set to the state that results from the move operation. The operation also
	 * sets the distance from the initial state for the returned state.
	 * If the move is not possible, the method returns null.
	 * @param row is an index in the range 0,1,..., dimension-1
	 * @param column is an index in the range 0,1,..., dimension-1
	 * @param op gives an operation such as move left or move up.
	 * @return new PuzzleState for legal move. For illegal move operation it returns null.
	 */
	public PuzzleState move(int row, int column, Operation op);
	/**
	 * A flip is a short cut for a possible series of move operations. 
	 * In the GUI, it results from a drag operations across multiple empty slots.
	 * Check if it is possible to move a tile from the start position to the end position.
	 * If start and end are not adjacent, there must be empty positions in between to make 
	 * a sequence of moves possible from start to end.
	 * If this is possible, the method returns a new instance of PuzzleState where  
	 * the state is set to the state that results from the sequence of move operations.
	 * A number of intermediate PuzzleStates is created as needed to represent the 
	 * sequence of move operations such that one can work backwards from the resulting
	 * state towards the starting state. 
	 * If the move is not possible, the method returns null.
	 * Starting position has coordinates (startRow,startColumn).
	 * Ending position has coordinates (endRow,endColumn).
	 * @param startRow is an index in the range 0,1,..., dimension-1
	 * @param startColumn is an index in the range 0,1,..., dimension-1
	 * @param endRow is an index in the range 0,1,..., dimension-1
	 * @param endColumn is an index in the range 0,1,..., dimension-1
	 * @return new PuzzleState for legal sequence of moves, otherwise null.
	 */
	public PuzzleState flip(int startRow, int startColumn, int endRow, int endColumn);
	/**
	 * Creates a random path from this state to the newly generated returned state
	 * that has the given length. The states are connected with a number of intermediate
	 * PuzzleStates to represent the path such that one can reverse from the resulting
	 * state backwards to the initial state in a step-by-step manner with the help 
	 * of the getParent() method. For state on the sequence, the getPathLength() method
	 * gives the number of steps away from the initial state.
	 * A path that contains cycles should be avoided as this is boring for the game.
	 * The path is generated with the help of a random number generator to make it 
	 * irregular and non-deterministic.
	 * @param pathLength is a value greater or equal 0, denotes the number of steps from this state to 
	 * the returned state
	 * @return state that is reachable with the given number of moves
	 */
	public PuzzleState shuffleBoard(int pathLength);
	/**
	 * Tells if the current state has an empty slot at the given position with 
	 * coordinates (row, column).
	 * @param row is an index in the range 0,1,..., dimension-1
	 * @param column is an index in the range 0,1,..., dimension-1
	 * @return true if this is an empty slot, false otherwise
	 */
	public boolean isEmpty(int row, int column);
}
