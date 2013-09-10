package organisms.g05;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;

import organisms.Move;
import organisms.OrganismsGame;
import organisms.Player;

public class AttemptSurvivorYang2 implements Player {

	static final String _CNAME = "The Survivor";
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

		
		int v = game.v();
		int M = game.M();


		if (energyleft/2 + foodleft * game.u() > M){
			if (neighbors[NORTH] < 0)
					m = new Move(REPRODUCE, NORTH, state);
			else if (neighbors[SOUTH] < 0)
					m = new Move(REPRODUCE, SOUTH, state);
			else m = new Move(STAYPUT);
		}
		else if ((energyleft < 4*v) || (neighbors[WEST]!=-1) || energyleft > 490) m = new Move(WEST);
		else m = new Move(STAYPUT);

		return m;
		
	}


}
