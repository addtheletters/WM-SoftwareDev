package generation;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;

import falstad.Constants;
import generation.Order.Builder;

/**
 * Contains JUnit tests which verify that a maze Builder produces valid mazes when used with a MazeFactory.
 * Tests check that deterministic factories are consistent and nondeterministic ones are inconsistent.
 * 
 * Specifically, this class runs tests on the default maze generator, Builder.DFS, but it could easily be adapted to test others.
 * 
 * @author Ben Zhang
 *
 */
public class MazeFactoryTest {
	/**
	 * The skill level with which to place all orders.
	 * For high numbers, will cause all tests to take significant time.
	 */
	public static final int ORDER_SKILL = 3;
	
	/**
	 * The highest skill level to be tested when checking that varying skill levels are handled. 
	 * For high numbers, will cause the varying-skill test to take significant time.
	 */
	public static final int MAX_SKILL = 6;
	
	/**
	 * A factory initialized in setup to be deterministic.
	 */
	private MazeFactory deterministicFactory;
	
	/**
	 * A factory initialized in setup to be non-deterministic.
	 */
	private MazeFactory randomFactory;
	
	/**
	 * Initialize factories as preparation before all tests.
	 */
	@Before
	public void setUp(){
		deterministicFactory = new MazeFactory(true);
		randomFactory = new MazeFactory();	
	}
	
	/**
	 * Constructs a TestOrder object using static constants and the parameters given.
	 * @param builder The builder the order will specify
	 * @param perfect true to specify a perfect (no holes or extra rooms) order, false otherwise.
	 * @param skill The skill level
	 * @return A TestOrder object holding the specified values
	 */
	public static TestOrder constructTestOrder(Builder builder, boolean perfect, int skill){
		return new TestOrder(skill, builder, perfect);
	}
	
	/**
	 * Shortcut for {@link MazeFactoryTest#constructTestOrder(Builder, boolean, int)} with skill determined by constant.
	 * @param builder The builder the order will specify
	 * @param perfect true to specify a perfect (no holes or extra rooms) order, false otherwise.
	 * @return A TestOrder object holding the specified values
	 */
	public static TestOrder constructTestOrder(Builder builder, boolean perfect){
		return new TestOrder(ORDER_SKILL, builder, perfect);
	}
	
	/**
	 * Checks if all tiles in the maze configuration have been labeled with a valid distance from the exit.
	 * @param mazeConfig configuration of the maze
	 * @return true if all tiles have a valid distance; false otherwise
	 */
	private static boolean allDistancesExist(MazeConfiguration mazeConfig){
		int oneCount = 0;
		int[][] allDists = mazeConfig.getMazedists().getDists();
		for(int i = 0; i < allDists.length; i++){
			for(int j = 0; j < allDists[0].length; j++){
				if( allDists[i][j] == 1 ){
					oneCount += 1;
				}
				if( allDists[i][j] <= 0){
					return false;
				}
			}
		}
		return oneCount == 1;
	}
	
	/**
	 * Displays the distances to the exit of all maze cells in a neat grid.
	 * @param mazeConfig configuration of the maze
	 */
	public static void prettyPrintDistances(MazeConfiguration mazeConfig){
		mazeConfig.getMazedists().prettyPrint();
	}
	
	/**
	 * Checks if a solution for a maze is specified from the provided position by following
	 * {@link MazeConfiguration#getNeighborCloserToExit(int, int)}.
	 * @param mazeConfig configuration of the maze
	 * @param position array of length 2 holding the (x,y) of the start position
	 * @return true if the solution can be verified; false if it fails
	 */
	public static boolean isSolveableFrom(MazeConfiguration mazeConfig, int[] start){
		int[] position = { start[0], start[1] }; // position follows where we are as we try to move from start to the exit
		int distance = mazeConfig.getDistanceToExit(position[0], position[1]);
		while(distance > 1){
//			System.out.println("distance is " + distance);
//			System.out.println("position is " + position[0] + "," + position[1]);
			if(mazeConfig.getMazecells().isExitPosition(position[0], position[1])){
				System.err.println("should not have reached exit yet! investigate?");
				return true;
			}
			int[] closerNeighbor = mazeConfig.getNeighborCloserToExit(position[0], position[1]);
			position[0] = closerNeighbor[0];
			position[1] = closerNeighbor[1];
			int redistance = mazeConfig.getDistanceToExit(position[0], position[1]);
//			if((redistance - distance ) != -1){
//				System.out.println("Distance difference was " + (redistance-distance));
//			}
			//distance--;
			distance = redistance; // update our distance to the exit
		}
		//System.out.println("reached the end");
		return mazeConfig.getMazecells().isExitPosition(position[0], position[1]);
	}
	
	/**
	 * Asserts that a solution for a maze exists from every position within the maze.
	 * @param mazeConfig configuration of the maze
	 */
	public static void checkSolveableFromEverywhere(MazeConfiguration mazeConfig){
		for(int i = 0; i < mazeConfig.getWidth(); i++){
			for(int j = 0; j < mazeConfig.getHeight(); j++){
				assertTrue("Maze should be solveable from " + i + "," + j, isSolveableFrom(mazeConfig, new int[]{i, j}));
			}	
		}
	}
	
	/**
	 * Asserts that the maze is properly constructed with only a single exit.
	 * @param mazeConfig configuration of the maze
	 */
	public static void checkSingleExit(MazeConfiguration mazeConfig){
		int exits = 0;
		for(int i = 0; i < mazeConfig.getWidth(); i++){
			for(int j = 0; j < mazeConfig.getHeight(); j++){
				if(mazeConfig.getMazecells().isExitPosition(i, j))
					exits ++;
			}
		}
		assertTrue("Only a single exit should exist (" + exits + ")", exits == 1);
	}
	
	/**
	 * Picks a random position within the maze.
	 * @param mazeConfig configuration of the maze
	 * @return int array of length 2 holding (x,y) random position
	 */
	public static int[] randomMazePosition( MazeConfiguration mazeConfig ){
		int[] pos = { 	(int)(Math.random() * mazeConfig.getWidth()),
						(int)(Math.random() * mazeConfig.getHeight()) };
		return pos;
	}
	
	/**
	 * Attempts several assertions to check that the maze configuration is valid.
	 * Checks that all maze tiles have been set with a distance to the exit.
	 * Checks that the maze is solvable from a everywhere within.
	 * Checks that there is only a single exit.
	 * @param mazeConfig configuration of the maze
	 */
	public static void checkMazeValid(MazeConfiguration mazeConfig){
		//prettyPrintDistances(mazeConfig);
		assertTrue("All Maze cells should have valid distances to exit", allDistancesExist(mazeConfig));
		//assertTrue("Maze should be solvable from designed start", isSolveableFrom(mazeConfig, mazeConfig.getStartingPosition()));
		//assertTrue("Maze should be solvable from random position", isSolveableFrom(mazeConfig, randomMazePosition(mazeConfig)));
		checkSolveableFromEverywhere(mazeConfig);
		checkSingleExit(mazeConfig);
	}
	
	/**
	 * Creates an order using the provided builder and perfection setting and starts the factory working on it.
	 * @param builder The maze builder to use
	 * @param perfect true for a perfect maze; false otherwise
	 * @param factory The factory with which to place the order
	 * @param skill The skill level to use
	 * @return the order that was placed
	 */
	public static final TestOrder placeOrder(Builder builder, boolean perfect, MazeFactory factory, int skill){
		TestOrder order = constructTestOrder(builder, perfect, skill);
		boolean success = factory.order(order);
		assertTrue("Order should succeed", success);
		return order;
	}
	
	/**
	 * Shortcut for {@link MazeFactoryTest#placeOrder(Builder, boolean, MazeFactory, int)} with skill level determined by local constant.
	 * @param builder The maze builder to use
	 * @param perfect true for a perfect maze; false otherwise
	 * @param factory The factory with which to place the order
	 * @return the order that was placed
	 */
	public static final TestOrder placeOrder(Builder builder, boolean perfect, MazeFactory factory){
		return placeOrder(builder, perfect, factory, ORDER_SKILL);
	}
	
	/**
	 * Create an order, starts the factory working on it, waits for completion, and then validates the result.
	 * @param builder The maze builder to use
	 * @param perfect true for a perfect maze; false otherwise
	 * @param factory The factory with which to place the order
	 * @param skill The skill level to use
	 * @return the order that was placed, should be completed when returned
	 */
	public static final TestOrder placeOrderAndWait(Builder builder, boolean perfect, MazeFactory factory, int skill){
		TestOrder order = placeOrder(builder, perfect, factory, skill);
		factory.waitTillDelivered();
		assertTrue("Order should be delivered", order.isDelivered());
		checkMazeValid(order.getResult());
		return order;
	}
	
	/**
	 * Shortcut for {@link MazeFactoryTest#placeOrderAndWait(Builder, boolean, MazeFactory, int)} with skill set as the local constant.
	 * @param builder The maze builder to use
	 * @param perfect true for a perfect maze; false otherwise
	 * @param factory The factory with which to place the order
	 * @return the order that was placed, should be completed when returned
	 */
	public static final TestOrder placeOrderAndWait(Builder builder, boolean perfect, MazeFactory factory){
		return placeOrderAndWait(builder, perfect, factory, ORDER_SKILL);
	}
	
	/**
	 * Shortcut default to non-deterministic factory for {@link MazeFactoryTest#placeOrder(Builder, boolean, MazeFactory)}.
	 * @param builder The maze builder to use
	 * @param perfect true for a perfect maze; false otherwise
	 * @return the order that was placed
	 */
	public final TestOrder placeOrder(Builder builder, boolean perfect){
		return placeOrder(builder, perfect, randomFactory);
	}
	
	/**
	 * Shortcut default to non-deterministic factory for {@link MazeFactoryTest#placeOrderAndWait(Builder, boolean, MazeFactory)}.
	 * @param builder The maze builder to use
	 * @param perfect true for a perfect maze; false otherwise
	 * @return the order that was placed, should be completed when returned
	 */
	public final TestOrder placeOrderAndWait(Builder builder, boolean perfect){
		return placeOrderAndWait(builder, perfect, randomFactory);
	}
	
	/**
	 * Test that a builder handles different skill levels
	 * @param builder the builder to test
	 * @param perfect true for a perfect maze; false otherwise
	 */
	public final void testVaryingSkillLevels(Builder builder, boolean perfect){
		for(int skill = 0; skill <= MAX_SKILL; skill ++){
			TestOrder order = placeOrderAndWait(builder, perfect, deterministicFactory, skill);
			assertEquals("Width should be as constant specifies", order.getResult().getWidth(), Constants.SKILL_X[skill] );
			assertEquals("Height should be as constant specifies", order.getResult().getHeight(), Constants.SKILL_Y[skill] );
		}
	}
	
	/**
	 * Test that a builder correctly ignores a second order while busy.
	 * @param builder the builder to test
	 */
	public final void testOrderWhileBusy(Builder builder) {
		TestOrder firstOrder = placeOrder(builder, false, randomFactory);
		TestOrder orderWhileBusy = constructTestOrder(builder, false);
		boolean busySuccess = randomFactory.order(orderWhileBusy);
		assertFalse("Order should fail", busySuccess);
		randomFactory.waitTillDelivered();
		
		assertTrue("First order should be delivered", firstOrder.isDelivered());
		assertFalse("Second order should not be delivered", orderWhileBusy.isDelivered());
		checkMazeValid(firstOrder.getResult());
	}
	
	/**
	 * Test that a builder can fulfill an order correctly after one has been cancelled.
	 * @param builder the builder to test
	 */
	public final void testSmoothCancel(Builder builder){
		randomFactory.cancel(); // attempt to cancel without having placed an order; should result in no action
		TestOrder interrupted = placeOrder(builder, false, randomFactory);
		randomFactory.cancel();
		randomFactory.waitTillDelivered(); // should not need to wait at all since order is cancelled
		assertFalse("Order should not be delivered", interrupted.isDelivered());
		placeOrderAndWait(builder, false, randomFactory);
	}
	
	/** 
	 * Test that a builder generates mazes consistently when in a deterministic factory.
	 * @param builder the builder to test
	 */
	public final void testDeterministic(Builder builder){
		TestOrder firstOrder = placeOrderAndWait(builder, false, deterministicFactory);
		TestOrder secondOrder = placeOrderAndWait(builder, false, deterministicFactory);
		assertEquals("Order results should be identical", firstOrder.getResult().getMazecells(), secondOrder.getResult().getMazecells());
	}
	
	/**
	 * Test to make sure random factory indeed produces inconsistent results.
	 * It is possible but extremely unlikely that this will fail even if a random factory is being used.
	 * @param builder the builder to test
	 */
	public final void testNonDeterministic(Builder builder){
		TestOrder firstOrder = placeOrderAndWait(builder, false, randomFactory);
		TestOrder secondOrder = placeOrderAndWait(builder, false, randomFactory);
		assertNotEquals("Order results should not be identical", firstOrder.getResult().getMazecells(), secondOrder.getResult().getMazecells());
	}
	
	
	
	//
	//	DFS tests
	//
	
	/**
	 * Test to check that DFS creates a successful maze for both a 'perfect' (no rooms) and 'imperfect' (rooms) specification.
	 */
	@Test
	public final void testPerfectImperfectDFS(){
		placeOrderAndWait(Builder.DFS, true);
		placeOrderAndWait(Builder.DFS, false);
	}
	
	/**
	 * Test that the DFS builder handles varying skill levels. This test can take a while.
	 */
	@Test
	public final void testVaryingSkillDFS(){
		testVaryingSkillLevels(Builder.DFS, false);
	}
	
	/**
	 * Test that DFS builder correctly ignores an order while busy using {@link MazeFactoryTest#testOrderWhileBusy(Builder)}.
	 */
	@Test
	public final void testOrderWhileBusyDFS(){
		testOrderWhileBusy(Builder.DFS);
	}
	
	/**
	 * Test that DFS can fulfill an order correctly after one has been cancelled using {@link MazeFactoryTest#testSmoothCancel(Builder)}.
	 */
	@Test
	public final void testSmoothCancelDFS(){
		testSmoothCancel(Builder.DFS);
	}
	
	/** 
	 * Test that DFS generates mazes consistently when used in a deterministic factory using {@link MazeFactoryTest#testDeterministic(Builder)}.
	 */
	@Test
	public final void testDeterministicDFS(){
		testDeterministic(Builder.DFS);
	}
	
	/**
	 * Test that DFS with non-deterministic factory generates inconsistent mazes using {@link MazeFactoryTest#testNonDeterministic(Builder)}.
	 */
	@Test
	public final void testNonDeterministicDFS(){
		testNonDeterministic(Builder.DFS);
	}
	
	
	//
	//	Prim tests
	//
	
	/**
	 * Test to check that Prim creates a successful maze for both a 'perfect' (no rooms) and 'imperfect' (rooms) specification.
	 */
	@Test
	public final void testPerfectImperfectPrim(){
		placeOrderAndWait(Builder.Prim, true);
		placeOrderAndWait(Builder.Prim, false);
	}
	
	/**
	 * Test that the Prim builder handles varying skill levels. This test can take a while.
	 */
	@Test
	public final void testVaryingSkillPrim(){
		testVaryingSkillLevels(Builder.Prim, false);
	}
	
	/**
	 * Test that Prim builder correctly ignores an order while busy using {@link MazeFactoryTest#testOrderWhileBusy(Builder)}.
	 */
	@Test
	public final void testOrderWhileBusyPrim(){
		testOrderWhileBusy(Builder.Prim);
	}
	
	/**
	 * Test that Prim can fulfill an order correctly after one has been cancelled using {@link MazeFactoryTest#testSmoothCancel(Builder)}.
	 */
	@Test
	public final void testSmoothCancelPrim(){
		testSmoothCancel(Builder.Prim);
	}
	
	/** 
	 * Test that Prim generates mazes consistently when used in a deterministic factory using {@link MazeFactoryTest#testDeterministic(Builder)}.
	 */
	@Test
	public final void testDeterministicPrim(){
		testDeterministic(Builder.Prim);
	}
	
	/**
	 * Test that Prim with non-deterministic factory generates inconsistent mazes using {@link MazeFactoryTest#testNonDeterministic(Builder)}.
	 */
	@Test
	public final void testNonDeterministicPrim(){
		testNonDeterministic(Builder.Prim);
	}
}
