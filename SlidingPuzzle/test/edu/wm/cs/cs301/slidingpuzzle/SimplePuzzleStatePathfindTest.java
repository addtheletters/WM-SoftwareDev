package edu.wm.cs.cs301.slidingpuzzle;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SimplePuzzleStatePathfindTest {
	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.SimplePuzzleState#solveAStar()}.
	 * Depending on how the random scramble does its work, the time to reach a solution varies.
	 * Usually it should resolve within 5 seconds. Bad cases may take up to 30 seconds or longer.
	 * The resulting solution should usually have a path length of less than scrambleAmount, though
	 * because it relies on heuristics, it isn't guaranteed to.
	 */
	@Test
	public void testPathfind(){
		int scrambleAmount = 20;
		
		PuzzleState ps1 = new SimplePuzzleState(4, 3);
		PuzzleState ps1s = ps1.shuffleBoard(scrambleAmount);
		
		PuzzleState solution = ((SimplePuzzleState)ps1s).solveAStar();
		assertTrue( "Comparing state " + ps1 + " with " + solution, ps1.equals(solution) );
		int step = 0;
		System.out.println("Showing result of pathfind from scrambled state:");
		for(PuzzleState ps : ((SimplePuzzleState)solution).getHistory()){
			System.out.println("Step " + step + "==================");
			System.out.println(ps);
			step += 1;
		}
		System.out.println("Step " + step + "==================");
		System.out.println(solution);
	}
}
