package organisms.g05;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;

import organisms.Move;
import organisms.OrganismsGame;
import organisms.Player;

public class SurviveAndPattern implements Player {

	static final String _CNAME = "Survive And Pattern";
	static final Color _CColor = new Color(0.0f, 0.0f, 1f);
	private int state;
	private OrganismsGame game;
	private Random rand;
	private int[] dirMap = {NORTH,WEST,SOUTH,EAST};
	boolean surviving;
	private int age = 0;
	private int lastDir = WEST;


	/*
	 * This method is called when the Organism is created.
	 * The key is the value that is passed to this organism by its parent (not used here)
	 */
	public void register(OrganismsGame game, int key) throws Exception
	{
		rand = new Random();
		if (key < 0){
			surviving = true;
			state = 0;
		}else state = key;
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

	private int findEmptySpace(int[] neighbors){
		for (int i = 0; i != 4; i++){
			int dir = dirMap[i];
			if (neighbors[dir] >= 0)
				continue;
			return dir;
		}
		return -1;
	}

	/*
	 * This is called by the simulator to determine how this Organism should move.
	 * foodpresent is a four-element array that indicates whether any food is in adjacent squares
	 * neighbors is a four-element array that holds the externalState for any organism in an adjacent square
	 * foodleft is how much food is left on the current square
	 * energyleft is this organism's remaining energy
	 */
	private int getReverseDir(int dir){
		switch (dir){
		case SOUTH: return NORTH;
		case NORTH: return SOUTH;
		case EAST: return WEST;
		case WEST: return EAST;
		}
		return -1;
	}
	private int turnLeft(int dir) {
		switch (dir){
		case SOUTH: return EAST;
		case NORTH: return WEST;
		case EAST: return NORTH;
		case WEST: return SOUTH;
		}
		return -1;
	}

	int[] growthCnt = {0, 0, 0, 0}; 
		
	public Move survivingMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		Move m = null;
		if (foodleft > 0)
			return new Move(STAYPUT);

		if (4 * (game.v() + .0)/game.s() >= 3 * age + 4)
			return new Move(STAYPUT);

		int longest = 0;
		for (int i = 1; i != 4; i++){
			if (growthCnt[i] > growthCnt[longest])
				longest = i;
		}

		if (growthCnt[longest] == 0){
			int dir = lastDir;
			if (neighbors[dir] < 0){
				m = new Move(dir);
			}
			else{
				int otherDir;
				if (lastDir == WEST)
					otherDir = NORTH;
				else otherDir = WEST;
				if (neighbors[otherDir] < 0)
					m = new Move(otherDir);
				else m = new Move(STAYPUT);
			}
		}
		else {
			if (energyleft > game.v() + game.s()){
				m = new Move(STAYPUT);
				return m;
			}
			else{
				if (dirMap[longest] == EAST || dirMap[longest] == WEST)
					lastDir = NORTH;
				else lastDir = WEST;
				m =  new Move(dirMap[longest]);
			}
		}
		for (int i = 0; i != 4; i++)
			growthCnt[i] = 0;
		return m;


	}
	public Move move(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		Move m = null; // placeholder for return value
		age ++;

		
		// count growth around you
		for (int i = 0; i != 4; i++){
			int dir = dirMap[i];
			if (neighbors[dir] >= 0) {
				growthCnt[i] = 0;
				surviving = false;
				
			}
			if (foodpresent[dir]) growthCnt[i] = growthCnt[i] + 1;
		}
		if (energyleft/2 + foodleft * game.u() >= game.M()){
			surviving = false;
			int dir = findEmptySpace(neighbors);
			if (dir >= 0)
				return new Move(REPRODUCE, dir, 100);
			else return new Move(STAYPUT);
		}
		if (surviving){
			return survivingMove(foodpresent,neighbors, foodleft, energyleft);
		}

		/****************   PATTERN PART   *******************/
		//CONSTANTS
		final int Org0StartFeeding = 0;
		final int Org0StopFeeding = 4;
		final int Org1StartFeeding = 7;
		final int Org1StopFeeding = 11;
		final int Org2StartFeeding = 14;
		final int Org2StopFeeding = 18;
		final int Org3StartFeeding = 21;
		final int Org3StopFeeding = 25;
		final int Org4StartFeeding = 28;
		final int Org4StopFeeding = 31;
		
		// placeholder for return value
		
		/* role describes the relative location of the organism.  The goal is to reproduce the following array,
		 * where 'X' is a free space.  This array should reproduce across the board.
		 	0	1	X
		 	3	2	4
		 	1	X	0
		 	
		 	Organisms with a role of 000 spawn the 1, 4, or 3 organisms.
		 	Organisms with a role of 100 spawn the 0, 2, and 3 organisms.
		 	Similarly, 	200 spawns 3
		 				200 spawns 1
		 				400 spawns 2
			
			if role is 500-800, the organism is in waiting mode.
			turn describes the number of turns (mod 32)
		 */
		int role, turn;
		
		
		//Returns the number of adjacent neighbors
		int adjacentBlanks = //neighbors[NORTH]+neighbors[EAST]+neighbors[WEST]+neighbors[SOUTH];
				(neighbors[NORTH] == -1 ? 1:0 ) +
				(neighbors[SOUTH] == -1 ? 1:0 ) +
				(neighbors[EAST] == -1 ? 1:0 ) +
				(neighbors[WEST] == -1 ? 1:0 );
		
		//The default state is -1.  On the first turn, put the organism in waiting mode.  500 will cause the
		//organism to wait 96 turns; other numbers can be used to wait longer/shorter
		//Also, start turn at 0
		if (state == -1) {
			role = 500;
			turn = 0;
		} else {
			//Calculate role and turn from the state
			//increment turn
			turn = state%100;
			role = state-turn++;
		}
		
		if (turn == 32) {
			role+=100;
			if (role == 800) role = 0;
			turn = 0;
		}
		
		switch (role) {
		//Each organism spawns one or more of its neighbors.  In order to control population growth,
		//currently only organism type 
		case 0:
			//Move onto the farm to consume food on Org0StartFeeding; return to original position on Org0StopFeeding
			if (turn == Org0StartFeeding) m = new Move(WEST);
			else if (turn == Org0StopFeeding) m = new Move(EAST);
			//Only reproduce if organism is adjacent to multiple blank squares
			else if (adjacentBlanks > 2){
				switch (rand.nextInt(3)) {
				case 0 : m = new Move(REPRODUCE, EAST, 100+turn); break;
				case 1 : m = new Move(REPRODUCE, NORTH, 400+turn); break;
				case 2 : m = new Move(REPRODUCE, SOUTH, 300+turn); break;
				default: m = new Move(STAYPUT);
				}
			} else m = new Move(STAYPUT);
			break;
		case 100:
			if (turn == Org1StartFeeding) m = new Move(EAST);
			else if (turn == Org1StopFeeding) m = new Move(WEST);
			else if (adjacentBlanks > 2) {
				switch (rand.nextInt(3)) {
				case 0 : m = new Move(REPRODUCE, SOUTH, 200+turn); break;
				case 1 : m = new Move(REPRODUCE, WEST, 0+turn); break;
				case 2 : m = new Move(REPRODUCE, NORTH, 300+turn); break;
				default: m = new Move(STAYPUT);
				} 
			} else m = new Move(STAYPUT);
			break;
		case 200: 
			if (turn == Org2StartFeeding) m = new Move(SOUTH);
			else if (turn == Org2StopFeeding) m = new Move(NORTH);
			else if (adjacentBlanks > 2) m = new Move(REPRODUCE, WEST, 300+turn); 
			else m = new Move(STAYPUT);
			break;
		case 300: 
			if (turn == Org3StartFeeding) m = new Move(WEST);
			else if (turn == Org3StopFeeding) m = new Move(EAST);
			else if (adjacentBlanks > 2) m = new Move(REPRODUCE, SOUTH, 100+turn); 
			else m = new Move(STAYPUT);
			break;
		case 400: 
			if (turn == Org4StartFeeding) m = new Move(EAST);
			else if (turn == Org4StopFeeding) m = new Move(WEST);
			else if (adjacentBlanks > 2) m = new Move(REPRODUCE, WEST, 200+turn); 
			else m = new Move(STAYPUT);
			break;
		default:m = new Move(STAYPUT);
		}
		
		//Re-combine updated state
		state = role+turn;
		return m;
		
	}


}
