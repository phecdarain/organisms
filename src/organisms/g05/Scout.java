package organisms.g05;

import java.util.*;
import java.io.*;
import java.awt.Color;

import organisms.*;

public final class Scout implements Player {

	private final int historySize = 10;
	
	static final String _CNAME = "Scout";
	static final Color _CColor = new Color(1.0f, 0f, 0f);
	private int state;
	private Random rand;
	private OrganismsGame game;
	private int age = 0;
	private final int BARE = 0;
	private final int LITTLE = 1;
	private final int MORE = 2;
	private final int RICH = 3;

	private final int ageBar = 100;
	
	private int lastPos = -1;

	private int estimate = BARE;
	private final int[] dirMap = {NORTH,WEST,SOUTH,EAST};
	//private final int[] rvsMap = {SOUTH,EAST,NORTH,WEST};

	private int oldness = 0;

	private class view{
		view(int saw, int found){
			spaceSaw = saw;
			foodFound = found;
		}
		int spaceSaw;
		int foodFound;
	}
	private LinkedList<view> history = new LinkedList<view>();
	
	
	
	/*	
	 * This method is called when the Organism is created.
	 * The key is the value that is passed to this organism by its parent (not used here)
	 */
	public void register(OrganismsGame game, int key) throws Exception
	{
		
		rand = new Random();
		if (key < 0)
			state = 0;
		else state = key;
		processState();
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

	private void updateState(){
		state = (estimate << 2) | oldness ; 
	}
	
	private int extractOldness(int state){
		return state & 3;
	}
	
	private int extractEstimate(int state){
		return (state & (3 << 2)) >> 2;
	}
	
	private void processState(){
		oldness = extractOldness(state);
		estimate = extractEstimate(state);
	}

	private void getEstimate(){
		int saw = 0;
		int found = 0;
		for (view v:history){
			saw += v.spaceSaw;
			found += v.foodFound;
		}
		if (saw == 0)
			estimate = BARE;
		else{
			double div = (found + 0.0) / saw * 4;
			estimate = (int) div;
			if(estimate >= 4)
				estimate = RICH;
		}
	}
	
	private void updateHistory(int saw, int found){
		if (history.size() >= historySize)
			history.removeLast();
		history.addFirst(new view(saw, found));
	}
	
	private void printHistory(){
		System.out.println("*********** " + this + "***********");
		for (view v:history)
			System.out.print(v.foodFound + "/" + v.spaceSaw+ " ");
		System.out.println();
	}
	
	private int findEmptySpace(int[] neighbors){
		for (int i = 0; i != 4; i++){
			int dir = dirMap[i];
			if (dir == lastPos || neighbors[dir] >= 0)
				continue;
			return dir;
		}
		return -1;
	}
	
	private int findSpaceWithFood(boolean[] foodpresent){
		for (int i = 0; i != 4; i++){
			int dir = dirMap[i];
			if (dir == lastPos || !foodpresent[dir])
				continue;
			return dir;
		}
		return -1;
	}
	
	private int getReverseDir(int dir){
		switch (dir){
		case SOUTH: return NORTH;
		case NORTH: return SOUTH;
		case EAST: return WEST;
		case WEST: return EAST;
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
	
	public Move move(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {

		Move m = null; // placeholder for return value
		age ++;
		if (age > ageBar){
			age = 0;
			if (oldness != 3)
				oldness ++;
		}

		// collect information
		int found = 0, saw = 0;
		for (int i = 0; i != 4; i++){
			int dir = dirMap[i];
			if (neighbors[dir] >= 0){
				saw += 3;
				found += extractEstimate(neighbors[dir]);
				continue;
			}
			if (foodpresent[dir]) found ++;
			saw ++;
		}
		updateHistory(saw, found);
		getEstimate();
		updateState();
		
		//printHistory();
		
		// action

		// reproduce 
		double factor = (4.0 - estimate) / 4 *.8;
		if (factor * game.M() < energyleft){
			int dir  = findEmptySpace(neighbors);
			if (dir >= 0)
				m = new	Move(REPRODUCE, dir,state);
			else
				m = new Move(STAYPUT);
			lastPos = -1;
			return m;
		}
		
		if (foodleft > 0){
			lastPos = -1;
			return new Move(STAYPUT);
		}
		//move according to estimate
		int coin = rand.nextInt(3);
		int dir = findSpaceWithFood(foodpresent);
		if (dir < 0)
			dir = findEmptySpace(neighbors);
		if (dir < 0){
			lastPos = -1;
			return new Move(STAYPUT);
		}
		if (coin < estimate){
			lastPos = getReverseDir(dir);
			return new Move(dir);
		}		
		lastPos = -1;
		return new Move(STAYPUT);
	}

}
