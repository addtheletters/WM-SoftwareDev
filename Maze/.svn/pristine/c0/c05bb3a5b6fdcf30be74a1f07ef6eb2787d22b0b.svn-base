package generation;

/**
 * Stub testing class for validating the function of factories and maze builders.
 * 
 * @author Ben Zhang
 *
 */
public class TestOrder implements Order {
	
	/**
	 * Debug constant to enable or disable printing from this class.
	 */
	private static final boolean SUPPRESS_PRINT = true;

	public int skill;
	public Builder builder;
	public boolean perfect;
	
	private boolean delivered;
	
	private MazeConfiguration result;
	
	public TestOrder(int skill, Builder builder, boolean perfect){
		this.skill = skill;
		this.builder = builder;
		this.perfect = perfect;
		this.delivered = false;
	}
	
	@Override
	public int getSkillLevel() {
		return skill;
	}

	@Override
	public Builder getBuilder() {
		return builder;
	}

	@Override
	public boolean isPerfect() {
		return perfect;
	}
	
	/**
	 * Returns the delivered resulting maze configuration of this order.
	 * @return the resulting maze from a successfully delivered order. If the order has yet to be delivered or failed, returns null.
	 */
	public MazeConfiguration getResult(){
		if(!delivered){
			if(!SUPPRESS_PRINT)
				System.err.println("TestOrder: Attempted to get result of order which has not been delivered.");
		}
		return result;
	}
	
	/**
	 * Returns status of this order's delivery.
	 * @return true after the order has been successfully delivered; false otherwise.
	 */
	public boolean isDelivered(){
		return delivered;
	}

	@Override
	public void deliver(MazeConfiguration mazeConfig) {
		this.result = mazeConfig;
		this.delivered = true;
		if(!SUPPRESS_PRINT){
			System.out.println("TestOrder: Delivered. Maze cells are as follows.");
			System.out.println(this.result.getMazecells());
		}
	}

	@Override
	public void updateProgress(int percentage) {
		if(!SUPPRESS_PRINT)
			System.out.println("TestOrder: Generation progress: " + percentage + "%"); // goes over 100% sometimes?
	}

}
