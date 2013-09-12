package organisms.g05;

import java.util.*;
import java.io.*;
import java.awt.Color;

import organisms.*;

public final class GreedyBabyMaker implements Player {

	static final String _CNAME = "Random Player";
	static final Color _CColor = new Color(1.0f, 0.67f, 0.67f);
	private int state;
	private Random rand;
	private OrganismsGame game;


	/*
	 * This method is called when the Organism is created.
	 * The key is the value that is passed to this organism by its parent (not used here)
	 */
	public void register(OrganismsGame game, int key) throws Exception
	{
		rand = new Random();
		state = key;
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
	public Move getFood(boolean[] foodpresent) throws Exception {
		//Get food if adjacent, otherwise, chill.
		Move mGet = null;
		if (foodpresent[WEST]) mGet = new Move(WEST);
		else if (foodpresent[NORTH]) mGet = new Move(NORTH);
		else if (foodpresent[EAST]) mGet = new Move(EAST);
		else if (foodpresent[SOUTH]) mGet = new Move(SOUTH);
		else mGet = new Move(STAYPUT);
		return mGet;
	}
	
	public Move move(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		int randNum = rand.nextInt(4);
		randNum++;
		Move stay = new Move(STAYPUT);
		

		if (state==-1) state = 1;
		
		Move m = null; // placeholder for return value
		
		if (energyleft > 170) m = new Move(REPRODUCE, randNum, randNum);
		else m = getFood(foodpresent);
		
		if ((energyleft > 20) && (m == stay)) m = new Move(state);

		return m;
	}

}
