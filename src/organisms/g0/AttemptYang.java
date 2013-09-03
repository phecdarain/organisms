package organisms.g0;

import java.awt.Color;
import java.util.Random;

import organisms.Move;
import organisms.OrganismsGame;
import organisms.Player;

public class AttemptYang implements Player {

	static final String _CNAME = "Little Yang";
	static final Color _CColor = new Color(0f, 0f, 1.0f);
	private int state;
	private OrganismsGame game;


	/*
	 * This method is called when the Organism is created.
	 * The key is the value that is passed to this organism by its parent (not used here)
	 */
	public void register(OrganismsGame game, int key) throws Exception
	{
		state = 0;
		this.game = game;

	}

	/*
	 * Return the name to be displayed in the simulator.
	 */
	public String name() throws Exception {
		return _CNAME;
	}

	/*
	 * Return the color to be displayed in the simulator.
	 */
	public Color color() throws Exception {
		return _CColor;
	}

	/*
	 * Not, uh, really sure what this is...
	 */
	public boolean interactive() throws Exception {
		return false;
	}

	/*
	 * This is the state to be displayed to other nearby organisms
	 */
	public int externalState() throws Exception {
		return state;
	}

	/*
	 * This is called by the simulator to determine how this Organism should move.
	 * foodpresent is a four-element array that indicates whether any food is in adjacent squares
	 * neighbors is a four-element array that holds the externalState for any organism in an adjacent square
	 * foodleft is how much food is left on the current square
	 * energyleft is this organism's remaining energy
	 */
	public Move move(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {

		Move m = null; // placeholder for return value

		if (0.9 * game.M() <= energyleft){
			return new Move(REPRODUCE, EAST, 0);

		}

		if (foodpresent[EAST])
			return new Move(EAST);

		if (foodpresent[WEST])
			return new Move(WEST);

		if (foodpresent[NORTH])
			return new Move(NORTH);

		if (foodpresent[SOUTH])
			return  new Move(SOUTH);

		
		if (neighbors[SOUTH] >= 0)
			return  new Move(NORTH);
		
		if (neighbors[WEST] >= 0)
			return new Move(EAST);

		if (neighbors[EAST] >= 0)
			return new Move(WEST);
		
		if (neighbors[NORTH] >= 0)
			return new Move(SOUTH);

		
		
		m = new Move(STAYPUT);	


		/*		
		int direction = rand.nextInt(6);

		switch (direction) {
		case 0: m = new Move(STAYPUT); break;
		case 1: m = new Move(WEST); break;
		case 2: m = new Move(EAST); break;
		case 3: m = new Move(NORTH); break;
		case 4: m = new Move(SOUTH); break;
		case 5:	direction = rand.nextInt(4);
		// if this organism will reproduce:
		// the second argument to the constructor is the direction to which the offspring should be born
		// the third argument is the initial value for that organism's state variable (passed to its register function)
		if (direction == 0) m = new Move(REPRODUCE, WEST, state);
		else if (direction == 1) m = new Move(REPRODUCE, EAST, state);
		else if (direction == 2) m = new Move(REPRODUCE, NORTH, state);
		else m = new Move(REPRODUCE, SOUTH, state);
		}
		 */
		return m;
	}

}
