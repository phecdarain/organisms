package organisms.g5;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;

import organisms.Move;
import organisms.OrganismsGame;
import organisms.Player;

public class Group5Survivor implements Player {

	static final String _CNAME = "The Survivor";
	static final Color _CColor = new Color(0.0f, 0.0f, 1f);
	protected int state;
	protected OrganismsGame game;
	
	boolean surviving = false;
	private int age = 0;
	private int lastDir = 0;


	/*
	 * This method is called when the Organism is created.
	 * The key is the value that is passed to this organism by its parent (not used here)
	 */
	public void register(OrganismsGame game, int key) throws Exception
	{
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

	/*
	 * This is called by the simulator to determine how this Organism should move.
	 * foodpresent is a four-element array that indicates whether any food is in adjacent squares
	 * neighbors is a four-element array that holds the externalState for any organism in an adjacent square
	 * foodleft is how much food is left on the current square
	 * energyleft is this organism's remaining energy
	 */

	int[] growthCnt = {0, 0, 0, 0}; 

	protected final Move survivingMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		age ++;
		Move m = null;
		// count growth around you
		for (int i = 0; i != 4; i++){
			int dir = Utils.dirMap[i];
			if (neighbors[dir] >= 0) {
				growthCnt[i] = 0;
				surviving = false;
			}
			if (foodpresent[dir]) growthCnt[i] = growthCnt[i] + 1;
		}


		if (4 * (game.v() + .0)/game.s() >= 3 * age + 4)
			return new Move(STAYPUT);

		if (foodleft > 0 && energyleft + game.u() <= game.M())
			return new Move(STAYPUT);
		
		if (foodleft > 0){
			if (foodleft * game.u() + energyleft > 2 * game.M()){
				surviving = false;
				return new Move(STAYPUT);
			}
			else{
				for (int i = 0; i != 4; i++)
					growthCnt[i] = 0;
				
				return new Move(WEST);

			}
		}



		int longest = 0;
		for (int i = 1; i != 4; i++){
			if (growthCnt[i] > growthCnt[longest])
				longest = i;
		}

		if (growthCnt[longest] == 0){
			if (energyleft <= 2 * game.v())
				return new Move(STAYPUT);
			int Dir = Utils.dirMap[lastDir];
			if (neighbors[Dir] < 0){
				m = new Move(Dir);
			}
			else{
				int otherDir;
				if (Dir == WEST)
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
				m =  new Move(Utils.dirMap[longest]);
			}
		}
		for (int i = 0; i != 4; i++)
			growthCnt[i] = 0;
		return m;


	}
	public Move move(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		
		if (surviving){
			return survivingMove(foodpresent,neighbors, foodleft, energyleft);
		}
		
		Move m = null; // placeholder for return value
		
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
