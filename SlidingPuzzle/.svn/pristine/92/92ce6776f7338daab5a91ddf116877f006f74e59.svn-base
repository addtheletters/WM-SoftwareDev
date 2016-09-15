/**
 * 
 */
package edu.wm.cs.cs301.slidingpuzzle;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.wm.cs.cs301.slidingpuzzle.PuzzleState.Operation;

/**
 * Junit test cases for SimplePuzzleState implementation of PuzzleState interface.
 * Class contains a number of black box tests.
 * Test scenario: 4x4 board with 1 empty slot
 * Sequence of moves: down, right, right and then reversed as left, left, up 
 * such that cycle back to starting state is closed.
 * 
 * @author Peter Kemper
 *
 */
public class SimplePuzzleStateTest {

	// Sequence of states and operations of the test scenario
	PuzzleState[] testSequence;
	Operation[] testOps;
	
	/**
	 * Junit calls this method automatically each time before executing a method tagged as a test.
	 * It is used here to set up a particular test scenario of a 4x4 board with 1 empty slot
	 * and a sequence of 6 moves that goes a ways from the initial state and then returns.
	 * This gives room to test individual moves, equality of states.
	 * 
	 * Note: it would be sufficient to execute this code just once as the testSequence
	 * and testOps arrays and their objects are not modified by the tests.
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// create a test sequence that starts from the initial state
		// performs 3 steps
		// performs 3 steps in reverse of previous 3 steps 
		// returns to initial state, game over
		int empties = 1;
		int dim = 4;
		testSequence = new PuzzleState[7];
		testOps = new Operation[7];
		testSequence[0] = new SimplePuzzleState();
		testSequence[0].setToInitialState(dim, empties); 
		// 4x4 board with 1 empty slot on last position (3,3)
		// move (2,3)->(3,3), empty (2,3)
		performOneStepForTestScenario(1, Operation.MOVEDOWN, dim-2, dim-1);
		// move (2,2)->(2,3), empty (2,2)
		performOneStepForTestScenario(2, Operation.MOVERIGHT, dim-2, dim-2);
		// move (2,1)->(2,2), empty (2,1)
		performOneStepForTestScenario(3, Operation.MOVERIGHT, dim-2, dim-3);
		// let's reverse this little path back to the starting state
		// move (2,2)->(2,1), empty (2,2)
		performOneStepForTestScenario(4, Operation.MOVELEFT, dim-2, dim-2);
		// move (2,3)->(2,2), empty (2,3)
		performOneStepForTestScenario(5, Operation.MOVELEFT, dim-2, dim-1);
		// move (3,3)->(2,2), empty (3,3)
		performOneStepForTestScenario(6, Operation.MOVEUP, dim-1, dim-1);
	}

	/**
	 * Helper method to perform and store a step of the test scenario in the setup method
	 * @param position
	 * @param op
	 * @param from
	 * @param to
	 */
	private void performOneStepForTestScenario(int position, Operation op, int from, int to) {
		testSequence[position] = testSequence[position-1].move(from, to, op);
		assertTrue(null != testSequence[position]);
		testOps[position] = op;
	}

	/**
	 * Junit calls this method automatically each time after executing a method tagged as a test.
	 * Not used here.
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.SimplePuzzleState#hashCode()}.
	 * The SimplePuzzleState class must implement (overwrite) the equals method.
	 * If one does so, it is recommended good practice to also overwrite the 
	 * hashCode method to have a consistent class design.
	 */
	@Test
	public void testHashCode() {
		// states that are equal must have the same hash code
		// first and last state in the test sequence are equal
		assertTrue(testSequence[0].equals(testSequence[6]));
		assertTrue(testSequence[0].hashCode() == testSequence[6].hashCode());
		// second and second to last state are equal and they are not the initial state
		assertTrue(testSequence[1].equals(testSequence[5]));
		assertTrue(testSequence[1].hashCode() == testSequence[5].hashCode());
		// third and third to last state are equal and they are not the initial state
		assertTrue(testSequence[2].equals(testSequence[4]));
		assertTrue(testSequence[2].hashCode() == testSequence[4].hashCode());
	}

	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.SimplePuzzleState#setToInitialState(int, int)}.
	 * The initial state for a given dimension and number of empty slots should list
	 * tiles with numbers in increasing order and have empty slots at the very end.
	 * Empty slots are encoded with zero values.
	 */
	@Test
	public void testSetToInitialState() {
		// create state with initial state
		PuzzleState ps1 = new SimplePuzzleState();
		int empties = 1;
		int dim = 4;
		int len = dim*dim - empties;
		ps1.setToInitialState(dim, empties); // 4x4 board with 1 empty slot
		for (int r=0; r < dim; r++) {
			for (int c=0; c < dim; c++) {
				if (r*dim+c < len) {
					assertEquals(r*dim+c+1, ps1.getValue(r, c));
				}
				else {
					// empties at end positions
					assertEquals(0, ps1.getValue(r, c));
				}
			}
		}
		// create 2nd state of similar dimension and number of empty slots
		PuzzleState ps2 = new SimplePuzzleState();
		ps2.setToInitialState(dim, empties+1); // 4x4 board with 2 empty slots
		assertFalse(ps1.equals(ps2));
		ps2.setToInitialState(dim+1, empties); // 5x5 board with 1 empty slot
		assertFalse(ps1.equals(ps2));
		ps2.setToInitialState(dim, empties); // 4x4 board with 1 empty slot
		assertTrue(ps1.equals(ps2));
		// move empty slot on last position, 
		// up and down are inverse and lead to same state
		assertTrue(ps2.isEmpty(dim-1, dim-1));
		PuzzleState ps3 = ps2.move(dim-2, dim-1, Operation.MOVEDOWN);
		assertFalse(ps2.equals(ps3));
		ps3 = ps3.move(dim-1, dim-1, Operation.MOVEUP);
		assertTrue(ps2.equals(ps3));
	}

	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.SimplePuzzleState#getOperation()}.
	 * We check if the stored operation for each state matches with
	 * the operation that was used.
	 */
	@Test
	public void testGetOperation() {
		// test with an explicit comparison with individual operations
		assertEquals(testSequence[0].getOperation(),null);
		// move down (2,3)->(3,3), empty (2,3)
		assertEquals(testSequence[1].getOperation(),Operation.MOVEDOWN);
		// move right (2,2)->(2,3), empty (2,2)
		assertTrue(testSequence[2].getOperation() == Operation.MOVERIGHT);
		// move right (2,1)->(2,2), empty (2,1)
		assertTrue(testSequence[3].getOperation() == Operation.MOVERIGHT);
		// let's reverse this little path back to the starting state
		// move left (2,2)->(2,1), empty (2,2)
		assertTrue(testSequence[4].getOperation() == Operation.MOVELEFT);
		// move left (2,3)->(2,2), empty (2,3)
		assertTrue(testSequence[5].getOperation() == Operation.MOVELEFT);
		// move up (3,3)->(2,2), empty (3,3)
		assertTrue(testSequence[6].getOperation() == Operation.MOVEUP);
		// same test but with the help of the testOps array
		for (int i = 0; i <= 6; i++) {
			assertEquals("failed in iteration " + i, testSequence[i].getOperation(), testOps[i]);
		}
	}

	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.SimplePuzzleState#getParent()}.
	 * We check if the stored parent for each state matches with its predecessor'
	 * in the test sequence.
	 */
	@Test
	public void testGetParent() {
		// the initial state does not have a parent or predecessor
		assertEquals(testSequence[0].getParent(),null);
		// since we stored all states in the sequence, 
		// for each state the parent state is the previous element in the array
		for (int i = 1; i <= 6; i++) {
			assertEquals("failed it iteration " + i, testSequence[i].getParent(),testSequence[i-1]);
		}
	}

	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.SimplePuzzleState#getPathLength()}.
	 * We check if the distance from the initial state increases when the 
	 * sequence moves away from the initial state and if it decreases when
	 * the sequence reverses direction and gets back to the initial state.
	 */
	@Test
	public void testGetDistance() {
		// create board and move empty slot to middle position
		// 4x4 board with 1 empty slot on last position (3,3)
		assertEquals(0,testSequence[0].getPathLength());
		// move down (2,3)->(3,3), empty (2,3)
		assertEquals(1,testSequence[1].getPathLength());
		// move right (2,2)->(2,3), empty (2,2)
		assertEquals(2,testSequence[2].getPathLength());
		// move right (2,1)->(2,2), empty (2,1)
		assertEquals(3,testSequence[3].getPathLength());
		// let's reverse this little path back to the starting state
		// move left (2,2)->(2,1), empty (2,2)
		assertEquals(4,testSequence[4].getPathLength());
		// move left (2,3)->(2,2), empty (2,3)
		assertEquals(5,testSequence[5].getPathLength());
		// move up (3,3)->(2,2), empty (3,3)
		assertEquals(6,testSequence[6].getPathLength());
	}

	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.SimplePuzzleState#shuffleBoard(int)}.
	 * The shuffle procedure performs a random walk of a given length.
	 * It works correctly if we can use it to perform a series inverse move operations
	 * that leads back to the initial state.
	 */
	@Test
	public void testShuffleBoard() {
		// set up game
		PuzzleState init = new SimplePuzzleState();
		init.setToInitialState(4, 1);
		int length = 10;
		PuzzleState current = init.shuffleBoard(length);
		PuzzleState backtracker = current;
		int[] pos = null;
		Operation op = null;
		// the current state and the backtracker will be different objects
		// but equal throughout 2 sequences of operations
		// the current state moves forward with a sequence of operations
		// that reverses how we got to that state and ends at the initial state
		// the backtracker tracks back to the initial state via the parent states
		// the backtracker guides the current state on which operations to perform.
		assertEquals(current,backtracker);
		// loop needs to make progress on 3 entities: length, current, backtracker
		for (; length > 0; length--) {
			// the backtracker's distance gets shorter
			assertEquals("failed at length " + length, length, backtracker.getPathLength());
			// determine inverse operation
			op = getInverseOperation(backtracker);
			assertNotNull("failed at length " + length, op);
			// determine which tile to move, 
			// the target state has an empty slot on the position we want
			// update backtracker to target state, 
			// loop progress: backtracker
			backtracker = backtracker.getParent();
			// get position for current state from target state
			pos = findEmptySlot(backtracker);
			assertNotNull("failed at length " + length, pos);
			// move to previous state
			// note: move always moves forward, 
			// so direction is forward and distance increases
			// loop progress: current state
			current = current.move(pos[0], pos[1], op);
			// compare parent state and newly reached state
			assertEquals(current,backtracker);
			//loop progress: length decremented
		}
		// at length 0:
		// current and backtracker must be at the initial state
		assertEquals(init,current);
		assertEquals(backtracker,init);
		assertEquals(0,backtracker.getPathLength());
		assertNotEquals(0,current.getPathLength()); // current moves forward
	}

	/**
	 * Find the empty slot for the given state.
	 * Relies on isEmpty() to work correctly.
	 * @param state
	 * @return
	 */
	private int[] findEmptySlot(PuzzleState state) {
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				if (state.isEmpty(r, c)) {
					int[] result = new int[2];
					result[0] = r;
					result[1] = c;
					return result;
				}
			}
		}
		return null;
	}
	/**
	 * Gets the inverse operation for the current one
	 * @param current state
	 * @return inverse operation if current state has an operation, null otherwise
	 */
	private Operation getInverseOperation(PuzzleState current) {
		Operation op = current.getOperation();
		if (null == op)
			return null;
		switch (op) {
		case MOVELEFT : 
			return Operation.MOVERIGHT;
		case MOVERIGHT : 
			return Operation.MOVELEFT;
		case MOVEUP : 
			return Operation.MOVEDOWN;
		case MOVEDOWN : 
			return Operation.MOVEUP;
		}
		return null;
	}

	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.SimplePuzzleState#equals(java.lang.Object)}.
	 * Two PuzzleStates are equal if they have the tiles arranged on the board in the same way.
	 */
	@Test
	public void testEqualsObject() {
		// try a few things with individual objects
		Object o1 = new SimplePuzzleState();
		Object o2 = null;
		// no object is equal to null, so equals must return false
		assertFalse(o1.equals(null));
		assertFalse(o1.equals(o2));
		o2 = new SimplePuzzleState();
		// here check if the default equals method has been overwritten
		// by a class specific one, 
		// the objects are different, so Object.equals() returns false
		// but the content should be the same, so SimplePuzzleState.equals() 
		// should return true
		assertTrue(o1.equals(o2));
		// Let's initialize the states to a meaningful initial state
		SimplePuzzleState tmp = (SimplePuzzleState)o1;
		tmp.setToInitialState(4, 1);
		// o1 was changed via tmp, so o1 and o2 are different now
		assertFalse(o1.equals(o2));
		tmp = (SimplePuzzleState)o2;
		tmp.setToInitialState(4, 1);
		// o2 was changed via tmp, so o1 and o2 are equal now
		assertTrue(o1.equals(o2));
		tmp.setToInitialState(4, 2);
		// o2 was changed via tmp to different number of empty slots, 
		// so o1 and o2 are different now
		assertFalse(o1.equals(o2));
		
		// Let's test states from the test sequence
		// no object is equal to null, so equals must return false
		assertFalse(testSequence[0].equals(null));
		// move down (2,3)->(3,3), empty (2,3)
		// state 0 and 1 are different
		assertFalse(testSequence[0].equals(testSequence[1]));
		// move right (2,2)->(2,3), empty (2,2)
		// state 1 and 2 are different
		assertFalse(testSequence[1].equals(testSequence[2]));
		// move right (2,1)->(2,2), empty (2,1)
		// state 2 and 3 are different
		assertFalse(testSequence[2].equals(testSequence[3]));
		// let's reverse this little path back to the starting state
		// move left (2,2)->(2,1), empty (2,2)
		// state 2 and 4 are the same
		assertTrue(testSequence[2].equals(testSequence[4]));
		// move left (2,3)->(2,2), empty (2,3)
		// state 1 and 5 are the same
		assertTrue(testSequence[1].equals(testSequence[5]));
		// move up (3,3)->(2,2), empty (3,3)
		// state 0 and 6 are the same
		assertTrue(testSequence[0].equals(testSequence[6]));
	}

	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.SimplePuzzleState#getValue(int, int)}.
	 */
	@Test
	public void testGetValue() {
		// create board and move empty slot to middle position
		// 4x4 board with 1 empty slot on last position (3,3)
		assertEquals(0,testSequence[0].getValue(3, 3));
		// move down (2,3)->(3,3), empty (2,3)
		assertEquals(0,testSequence[1].getValue(2, 3));
		assertEquals(12,testSequence[1].getValue(3, 3));
		// move right (2,2)->(2,3), empty (2,2)
		assertEquals(0,testSequence[2].getValue(2, 2));
		assertEquals(11,testSequence[2].getValue(2, 3));
		// move right (2,1)->(2,2), empty (2,1)
		assertEquals(0,testSequence[3].getValue(2, 1));
		assertEquals(10,testSequence[3].getValue(2, 2));
		// let's reverse this little path back to the starting state
		// move left (2,2)->(2,1), empty (2,2)
		assertEquals(0,testSequence[4].getValue(2, 2));
		assertEquals(10,testSequence[4].getValue(2, 1));
		// move left (2,3)->(2,2), empty (2,3)
		assertEquals(0,testSequence[5].getValue(2, 3));
		assertEquals(11,testSequence[5].getValue(2, 2));
		// move up (3,3)->(2,2), empty (3,3)
		assertEquals(0,testSequence[6].getValue(3, 3));
		assertEquals(12,testSequence[6].getValue(2, 3));
	}

	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.SimplePuzzleState#move(int, int, edu.wm.cs.cs301.slidingpuzzle.PuzzleState.Operation)}.
	 * We performed a series of move operations for the test sequence.
	 * We just need to check if the tiles on the board are set up correctly.
	 */
	@Test
	public void testMove() {
		// 1 2 3 4
		// 5 6 7 8
		// 9 10 11 12
		// 13 14 15 0
		// up, right, right
		// 1 2 3 4
		// 5 6 7 8
		// 9 0 10 11
		// 13 14 15 12
		// left, left, down
		int[] correct = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,0} ;
		checkTiles(testSequence[0],correct);
		correct[15] = 12;
		correct[11] = 0;
		checkTiles(testSequence[1],correct);
		correct[11] = 11;
		correct[10] = 0;
		checkTiles(testSequence[2],correct);
		correct[10] = 10;
		correct[9] = 0;
		checkTiles(testSequence[3],correct);
		correct[10] = 0;
		correct[9] = 10;
		checkTiles(testSequence[4],correct);
		correct[11] = 0;
		correct[10] = 11;
		checkTiles(testSequence[5],correct);
		correct[11] = 12;
		correct[15] = 0;
		checkTiles(testSequence[6],correct);
	}
	/**
	 * Helper method to check the state of a puzzle state
	 * @param ps
	 * @param correct
	 */
	private void checkTiles(PuzzleState ps, int[] correct) {
		int i = 0;
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				assertEquals("failed for entry: row " + r + ", col " + c + " index " + i , ps.getValue(r, c), correct[i]);
				i++;
			}
		}
	}
	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.SimplePuzzleState#flip(int, int, int, int)}.
	 * The flip method is used when an tile is dragged across multiple empty slots.
	 * We test the special case of 1 empty slot which is trivial and may serve as a simple smoke test.
	 * For this case flip can only do a single move and must match a move operation.
	 * We use the test scenario and check 5 states with their successor states.
	 */
	@Test
	public void testFlip() {
		// use test sequence and flip from one state to its successor state.
		PuzzleState ps;
		int startRow, startColumn, endRow, endColumn;
		int[] slot;
		for (int i = 0; i < 5; i++) {
			// current state is testSequence[i]
			// prepare position: 
			// start is non-empty tile, which is empty slot in successor state
			slot = findEmptySlot(testSequence[i+1]);
			assertNotNull("failed for state at position " + i, slot);
			startRow = slot[0];
			startColumn = slot[1];
			// end is empty slot in current state
			slot = findEmptySlot(testSequence[i]);
			assertNotNull("failed for state at position " + i, slot);
			endRow = slot[0];
			endColumn = slot[1];
			// get result from flipping 
			ps = testSequence[i].flip(startRow, startColumn, endRow, endColumn);
			// compare with successor state
			assertEquals("failed for state at position " + i, testSequence[i+1], ps);
		}
	}

	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.SimplePuzzleState#flip(int, int, int, int)}.
	 * The flip method is used when an tile is dragged across multiple empty slots.
	 * We test the special case of 3 empty slots where a requested flip is indeed possible.
	 * The series of intermediate states is unique.
	 */
	@Test
	public void testFlipForMultipleEmptySlots() {
		// set up state with 3 empty slots
		PuzzleState ps1 = new SimplePuzzleState();
		ps1.setToInitialState(4, 3);
		// 1 2 3 4
		// 5 6 7 8
		// 9 10 11 12
		// 13 0 0 0
		// move 10 to last position, keep intermediate states
		PuzzleState ps2 = ps1.move(2, 1, Operation.MOVEDOWN);
		PuzzleState ps3 = ps2.move(3, 1, Operation.MOVERIGHT);
		PuzzleState ps4 = ps3.move(3, 2, Operation.MOVERIGHT);
		// perform flip to move 10 to bottom-right most position
		// flip is equivalent to a sequence of 3 move operations
		PuzzleState test = ps1.flip(2, 1, 3, 3);
		// sequence and flip should match
		assertTrue("Comparing state " + ps4 + " with " + test, ps4.equals(test));
		// check if flip produce correct parent relationship with intermediate states
		PuzzleState parent = test.getParent();
		assertTrue("Comparing state " + ps3 + " with " + parent, ps3.equals(parent));
		parent = parent.getParent();
		assertTrue("Comparing state " + ps2 + " with " + parent, ps2.equals(parent));
		parent = parent.getParent();
		assertTrue("Comparing state " + ps1 + " with " + parent,ps1.equals(parent));
		
	}
	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.SimplePuzzleState#flip(int, int, int, int)}.
	 * The flip method is used when an tile is dragged across multiple empty slots.
	 * We test the special case of 3 empty slots where a requested flip is indeed possible.
	 * The series of intermediate states is not unique, there are 2 possible ways to proceed.
	 */
	@Test
	public void testFlipForMultipleEmptySlotsNotUnique() {
		// set up state with 3 empty slots
		PuzzleState ps1 = new SimplePuzzleState();
		ps1.setToInitialState(4, 3);
		// 1 2 3 4
		// 5 6 7 8
		// 9 10 11 12
		// 13 0 0 0
		// move 10 down, shift 11, 12 to the left
		// keep intermediate states
		PuzzleState ps2 = ps1.move(2, 1, Operation.MOVEDOWN);
		assertNotNull(ps2);
		PuzzleState ps3 = ps2.move(2, 2, Operation.MOVELEFT);
		assertNotNull(ps3);
		PuzzleState ps4 = ps3.move(2, 3, Operation.MOVELEFT);
		assertNotNull(ps4);
		// 1 2 3 4
		// 5 6 7 8
		// 9 11 12 0
		// 13 10 0 0
		// one can drag 12 to the bottom-right position in 2 ways
		// Variant 1: right, down
		// Variant 2: down, right
		// perform flip to move 12 to bottom-right most position
		PuzzleState test = ps4.flip(2, 2, 3, 3);
		assertNotNull(test);
		// sequence and flip should match
		// V1
		PuzzleState ps5V1 = ps4.move(2, 2, Operation.MOVERIGHT);
		assertNotNull(ps5V1);
		PuzzleState ps6V1 = ps5V1.move(2, 3, Operation.MOVEDOWN);
		assertNotNull(ps6V1);
		// final states agree
		assertTrue("Comparing state " + ps6V1 + " with " + test, ps6V1.equals(test));
		// V2
		PuzzleState ps5V2 = ps4.move(2, 2, Operation.MOVEDOWN);
		assertNotNull(ps5V2);
		PuzzleState ps6V2 = ps5V2.move(3, 2, Operation.MOVERIGHT);
		assertNotNull(ps6V2);
		// intermediate states differ
		assertFalse("Comparing state " + ps5V1 + " with " + ps5V1, ps5V1.equals(ps5V2));
		// final states agree
		assertTrue("Comparing state " + ps6V2 + " with " + test, ps6V2.equals(test));
		// check if flip produced correct parent relationship with intermediate states
		PuzzleState parent = test.getParent();
		assertTrue("Comparing state " + ps5V1 + " with " + parent, ps5V1.equals(parent) || ps5V2.equals(parent));
		parent = parent.getParent();
		assertTrue("Comparing state " + ps4 + " with " + parent, ps4.equals(parent));
	}
	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.SimplePuzzleState#flip(int, int, int, int)}.
	 * The flip method is used when an tile is dragged across multiple empty slots.
	 * We test the special case of 3 empty slots where a requested flip is indeed possible.
	 * There are 3 possible options, one is the solution.
	 */
	@Test
	public void testFlipForMultipleEmptySlotsCross() {
		// set up state with 3 empty slots
		PuzzleState ps1 = new SimplePuzzleState();
		ps1.setToInitialState(4, 3);
		// 1 2 3 4
		// 5 6 7 8
		// 9 10 11 12
		// 13 0 0 0
		// move 10, 12 down 
		// keep intermediate states
		PuzzleState ps2 = ps1.move(2, 1, Operation.MOVEDOWN);
		assertNotNull(ps2);
		PuzzleState ps3 = ps2.move(2, 3, Operation.MOVEDOWN);
		assertNotNull(ps3);
		// 1 2 3 4
		// 5 6 7 8
		// 9 0 11 0
		// 13 10 0 12
		// one can drag 11 to 3 possible positions but all are neighbors
		// Variant 1: flip 11 to the right
		// Variant 2: flip 11 to the left
		// Variant 3: flip 11 downwards
		PuzzleState test;
		PuzzleState ps4;
		// test Variant 1:
		test = ps3.flip(2, 2, 2, 3);
		assertNotNull(test);
		// equivalent to move right operation
		ps4 = ps3.move(2, 2, Operation.MOVERIGHT);
		assertNotNull(ps4);
		assertTrue("Comparing state " + ps4 + " with " + test, ps4.equals(test));
		assertTrue("Comparing state " + ps3 + " with " + test.getParent(), ps3.equals(test.getParent()));
		// test Variant 2:
		test = ps3.flip(2, 2, 2, 1);
		assertNotNull(test);
		// equivalent to move right operation
		ps4 = ps3.move(2, 2, Operation.MOVELEFT);
		assertNotNull(ps4);
		assertTrue("Comparing state " + ps4 + " with " + test, ps4.equals(test));
		assertTrue("Comparing state " + ps3 + " with " + test.getParent(), ps3.equals(test.getParent()));
		// test Variant 1:
		test = ps3.flip(2, 2, 3, 2);
		assertNotNull(test);
		// equivalent to move right operation
		ps4 = ps3.move(2, 2, Operation.MOVEDOWN);
		assertNotNull(ps4);
		assertTrue("Comparing state " + ps4 + " with " + test, ps4.equals(test));
		assertTrue("Comparing state " + ps3 + " with " + test.getParent(), ps3.equals(test.getParent()));
		
	}
	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.SimplePuzzleState#flip(int, int, int, int)}.
	 * The flip method is used when an tile is dragged across multiple empty slots.
	 * We test the special case of 3 empty slots where a requested flip is indeed possible.
	 * The series of intermediate states is not unique, there are 2 options to proceed, only 1 works.
	 */
	@Test
	public void testFlipForMultipleEmptySlotsDeadEnd() {
		// set up state with 3 empty slots
		PuzzleState ps1 = new SimplePuzzleState();
		ps1.setToInitialState(4, 3);
		// 1 2 3 4
		// 5 6 7 8
		// 9 10 11 12
		// 13 0 0 0
		// move 10 down 
		// keep intermediate states
		PuzzleState ps2 = ps1.move(2, 1, Operation.MOVEDOWN);
		assertNotNull(ps2);
		// 1 2 3 4
		// 5 6 7 8
		// 9 0 11 12
		// 13 10 0 0
		// one can drag 11 to the bottom-right position
		// the option to move left is a dead end
		// 
		PuzzleState test = ps2.flip(2, 2, 3, 3);
		assertNotNull(test);
		// equivalent to move down and right operation
		PuzzleState ps3 = ps2.move(2, 2, Operation.MOVEDOWN);
		assertNotNull(ps3);
		PuzzleState ps4 = ps3.move(3, 2, Operation.MOVERIGHT);
		assertNotNull(ps4);
		assertTrue("Comparing state " + ps4 + " with " + test, ps4.equals(test));
		PuzzleState parent = test.getParent();
		assertTrue("Comparing state " + ps3 + " with " + parent, ps3.equals(parent));
		parent = parent.getParent();
		assertTrue("Comparing state " + ps2 + " with " + parent, ps2.equals(parent));
	}
	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.SimplePuzzleState#flip(int, int, int, int)}.
	 * The flip method is used when an tile is dragged across multiple empty slots.
	 * We test the special case of 3 empty slots where a requested flip is indeed possible.
	 * The series of intermediate states is not unique, there are 2 options to proceed, only 1 works.
	 */
	@Test
	public void testFlipForMultipleEmptySlotsDeadEnd2() {
		// set up state with 3 empty slots
		PuzzleState ps1 = new SimplePuzzleState();
		ps1.setToInitialState(4, 3);
		// 1 2 3 4
		// 5 6 7 8
		// 9 10 11 12
		// 13 0 0 0
		// move 12 down 
		// keep intermediate states
		PuzzleState ps2 = ps1.move(2, 3, Operation.MOVEDOWN);
		assertNotNull(ps2);
		// 1 2 3 4
		// 5 6 7 8
		// 9 10 11 0
		// 13 0 0 12
		// one can drag 12 to the bottom-left position
		// the option to move up is a dead end
		// 
		PuzzleState test = ps2.flip(3, 3, 3, 1);
		assertNotNull(test);
		// equivalent to move down and right operation
		PuzzleState ps3 = ps2.move(3, 3, Operation.MOVELEFT);
		assertNotNull(ps3);
		PuzzleState ps4 = ps3.move(3, 2, Operation.MOVELEFT);
		assertNotNull(ps4);
		assertTrue("Comparing state " + ps4 + " with " + test, ps4.equals(test));
		PuzzleState parent = test.getParent();
		assertTrue("Comparing state " + ps3 + " with " + parent, ps3.equals(parent));
		parent = parent.getParent();
		assertTrue("Comparing state " + ps2 + " with " + parent, ps2.equals(parent));
	}
	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.SimplePuzzleState#isEmpty(int, int)}.
	 * We performed a series of move operations for the test sequence.
	 * We just need to check if the empty slot is on the correct position.
	 */
	@Test
	public void testIsEmptyIntInt() {
		for (int i = 0; i < 7; i++) {
			checkForEmptySlots(testSequence[i]);
		}
	}
	/**
	 * Helper method to check the state of a puzzle state
	 * @param ps
	 */
	private void checkForEmptySlots(PuzzleState ps) {
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				if (0 == ps.getValue(r, c)) {
					assertTrue("failed for entry: row " + r + ", col " + c, ps.isEmpty(r, c));
				}
				else {
					assertFalse("failed for entry: row " + r + ", col " + c, ps.isEmpty(r, c));
				}
			}
		}
	}

}
