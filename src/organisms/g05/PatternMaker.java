package organisms.g05;


import java.util.*;
import java.io.*;
import java.awt.Color;

import organisms.*;

public final class PatternMaker implements Player {

	static final String _CNAME = "Random Player";
	static final Color _CColor = new Color(1.0f, 0.67f, 0.67f);
	private int state;
	private Random rand;
	private OrganismsGame game;

	//my constants
	
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
	public Move move(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		Move m = null; // placeholder for return value
		
		int state1, state2;
		int neighborsFilled = neighbors[NORTH]+neighbors[EAST]+neighbors[WEST]+neighbors[SOUTH];
		game.println("state :" + state);
		game.println("neighborsFilled :" + neighborsFilled);
		if (state == -1) {
			state1 = 500;
			state2 = 0;
		} else {
			state1 = state-state%100;
			state2 = state%100+1;
		}
		
		if (state2 == 32) {
			if (state1 == 700) state1 = 0;
			state1+=100;
			state2 = 0;
		}
		
		switch (state1) {
		case 0:
			if (state2 == 0) m = new Move(WEST);
			else if (state2 == 4) m = new Move(EAST); 
			else if (neighborsFilled < -1){
				switch (rand.nextInt(3)) {
				case 0 : m = new Move(REPRODUCE, EAST, 100+state2); break;
				case 1 : m = new Move(REPRODUCE, NORTH, 400+state2); break;
				case 2 : m = new Move(REPRODUCE, SOUTH, 300+state2); break;
				default: m = new Move(STAYPUT);
				}
			} else m = new Move(STAYPUT);
			break;
		case 100:
			if (state2 == 7) m = new Move(EAST);
			else if (state2 == 11) m = new Move(WEST);
			else if (neighborsFilled < -1) {
				switch (rand.nextInt(3)) {
				case 0 : m = new Move(REPRODUCE, SOUTH, 200+state2); break;
				case 1 : m = new Move(REPRODUCE, WEST, 0+state2); break;
				case 2 : m = new Move(REPRODUCE, NORTH, 300+state2); break;
				default: m = new Move(STAYPUT);
				} 
			} else m = new Move(STAYPUT);
			break;
		case 200: 
			if (state2 == 14) m = new Move(SOUTH);
			else if (state2 == 18) m = new Move(NORTH);
			else if (neighborsFilled < -1) m = new Move(REPRODUCE, WEST, 300+state2); 
			else m = new Move(STAYPUT);
			break;
		case 300: 
			if (state2 == 21) m = new Move(WEST);
			else if (state2 == 25) m = new Move(EAST);
			else if (neighborsFilled < -1) m = new Move(REPRODUCE, SOUTH, 100+state2); 
			else m = new Move(STAYPUT);
			break;
		case 400: 
			if (state2 == 28) m = new Move(EAST);
			else if (state2 == 32) m = new Move(WEST);
			else if (neighborsFilled < -1) m = new Move(REPRODUCE, WEST, 200+state2); 
			else m = new Move(STAYPUT);
			break;
		default:m = new Move(STAYPUT);
		}
		
		state = state1+state2;
		return m;
	}

}
