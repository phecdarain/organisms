package organisms.g05;


import java.util.*;
import java.io.*;
import java.awt.Color;

import organisms.*;

public final class TestPlayer implements Player {

	static final String _CNAME = "Militiaman";
	static final Color _CColor = new Color(1.0f, 0.67f, 0.67f);
	private int state;
	private Random rand;
	private OrganismsGame game;
	int age = 0;

	private final int FINDINGFARM = 0;
	private final int FOODWEST = 1;
	private final int FOODEAST = 2;
	private final int FOODSOUTH = 3;
	private final int FOODNORTH = 4;
	// the direction means where the food is according to the original food finder
	private final int FIRSTCHILDNORTH = 5;
	private final int FIRSTCHILDSOUTH = 6;
	private final int FIRSTCHILDWEST = 7;
	private final int FIRSTCHILDEAST = 8;

	private final int SECONDCHILDNORTH = 9;
	private final int SECONDCHILDSOUTH = 10;
	private final int SECONDCHILDWEST = 11;
	private final int SECONDCHILDEAST = 12;

	private final int THIRDCHILDEAST = 13;
	private final int THIRDCHILDWEST = 14;
	private final int THIRDCHILDSOUTH = 15;
	private final int THIRDCHILDNORTH = 16;

	// the direction means the organism step dir to the center and eat it
	private final int EATINGEAST = 17;
	private final int EATINGWEST = 18;
	private final int EATINGSOUTH = 19;
	private final int EATINGNORTH = 20;

	private final int PRODUCEDONEEAST = 21;
	private final int PRODUCEDONEWEST = 22;
	private final int PRODUCEDONESOUTH = 23;
	private final int PRODUCEDONENORTH = 24;

	private final int TESTINGFOOD = 25;
	private final int FOODREADY = 26;

	private boolean enoughFood = false;


	private int operationalSteps = 0;

	private int firstChildState = -1;
	private int secondChildState = -1;

	private double hungerRate = 1.5;



	/*
	 * This method is called when the Organism is created.
	 * The key is the value that is passed to this organism by its parent (not used here)
	 */
	public void register(OrganismsGame game, int key) throws Exception
	{
		rand = new Random();
		if (key < 0)
			state = FINDINGFARM;
		else state = key;
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
	int lastDir = -1;

	private Move findingFarmMove0(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception{

		int foodDir = Utils.findFood(foodpresent);
		if (foodDir < 0){
			if (lastDir >= 0){
				if (neighbors[Utils.dirMap[lastDir]] < 0)
					return new Move(Utils.dirMap[lastDir]);
			}
			int esDir = Utils.findEmptySpace(neighbors);
			if (esDir < 0)
				return new Move(STAYPUT);
			else {
				lastDir = esDir;
				return new Move(Utils.dirMap[esDir]);
			}

		}else{

			switch (Utils.dirMap[foodDir]){
			case WEST:
				if (neighbors[SOUTH] >= 0 || neighbors[NORTH] >= 0){
					return new Move(WEST);
				}

				state = PRODUCEDONEWEST;
				firstChildState = FIRSTCHILDWEST;
				secondChildState = SECONDCHILDWEST;
				break;
			case EAST: 
				if (neighbors[SOUTH] >= 0 || neighbors[NORTH] >= 0){
					return new Move(EAST);
				}
				state = PRODUCEDONEEAST;
				firstChildState = FIRSTCHILDEAST;
				secondChildState = SECONDCHILDEAST;

				break;
			case SOUTH:
				if (neighbors[EAST] >= 0 || neighbors[WEST] >= 0){
					return new Move(SOUTH);
				}

				state = PRODUCEDONESOUTH;
				firstChildState = FIRSTCHILDSOUTH;
				secondChildState = SECONDCHILDSOUTH;
				break;
			case NORTH: 
				if (neighbors[EAST] >= 0 || neighbors[WEST] >= 0){
					return new Move(NORTH);
				}
				state = PRODUCEDONENORTH;
				firstChildState = FIRSTCHILDNORTH;
				secondChildState = SECONDCHILDNORTH;
				break;
			}
			int produceDir = Utils.turnRight(foodDir);
			return new Move(REPRODUCE, Utils.dirMap[produceDir], firstChildState);
		}


	}
	private Move findingFarmMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception{

		int foodDir = Utils.findFood(foodpresent);
		if (foodDir < 0){
			if (lastDir >= 0){
				if (neighbors[Utils.dirMap[lastDir]] < 0)
					return new Move(Utils.dirMap[lastDir]);
			}
			int esDir = Utils.findEmptySpace(neighbors);
			if (esDir < 0)
				return new Move(STAYPUT);
			else {
				lastDir = esDir;
				return new Move(Utils.dirMap[esDir]);
			}

		}else{
			state = TESTINGFOOD;
			lastDir = foodDir;
			return new Move(Utils.dirMap[foodDir]);
		}
	}	


	public Move foodNorthMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		if (enoughFood){
			hungerRate = game.M()/2/game.v();
			if (energyleft > 0.8 * game.M()){
				int dir = Utils.findEmptySpace(neighbors);
				if (dir >= 0){
					enoughFood = false;
					return new Move(REPRODUCE,Utils.dirMap[dir],FINDINGFARM);
				}
			}
		}
		if (energyleft >  hungerRate * game.v())
			return new Move(STAYPUT);
		else{
			state = EATINGNORTH;
			return new Move(NORTH);
		}

	}
	public Move foodSouthMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		if (enoughFood){
			hungerRate = game.M()/2/game.v();
			if (energyleft > 0.8 * game.M()){

				int dir = Utils.findEmptySpace(neighbors);
				if (dir >= 0){
					enoughFood = false;
					return new Move(REPRODUCE,Utils.dirMap[dir],FINDINGFARM);
				}
			}
		}

		if (energyleft >  hungerRate * game.v())
			return new Move(STAYPUT);
		else{
			state = EATINGSOUTH;
			return new Move(SOUTH);
		}

	}

	public Move foodEastMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		if (enoughFood){
			hungerRate = game.M()/2/game.v();
			if (energyleft > 0.8 * game.M()){

				int dir = Utils.findEmptySpace(neighbors);
				if (dir >= 0){
					enoughFood = false;
					return new Move(REPRODUCE,Utils.dirMap[dir],FINDINGFARM);
				}
			}
		}

		if (energyleft >  hungerRate * game.v())
			return new Move(STAYPUT);
		else{
			state = EATINGEAST;
			return new Move(EAST);
		}

	}

	public Move foodWestMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		if (enoughFood){
			hungerRate = game.M()/2/game.v();
			if (energyleft > 0.8 * game.M()){

				int dir = Utils.findEmptySpace(neighbors);
				if (dir >= 0){
					enoughFood = false;
					return new Move(REPRODUCE,Utils.dirMap[dir],FINDINGFARM);
				}
			}
		}

		if (energyleft >  1.5 * game.v())
			return new Move(STAYPUT);
		else{
			state = EATINGWEST;
			return new Move(WEST);
		}

	}

	public Move firstChildNorthMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		int Dir = NORTH;
		switch (operationalSteps){
		case 0:
			if (neighbors[Dir] < 0){
				operationalSteps++;
				return new Move(Dir);	
			}else return new Move(STAYPUT);
		case 1:
			if (neighbors[Dir] < 0){
				operationalSteps++;
				return new Move(REPRODUCE, Dir, THIRDCHILDNORTH);
			}
		default: 
			state = FOODWEST;
			return new Move(STAYPUT);
		}
	}

	public Move firstChildSouthMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		int Dir = SOUTH;
		switch (operationalSteps){
		case 0:
			if (neighbors[Dir] < 0){
				operationalSteps++;
				return new Move(Dir);	
			}else return new Move(STAYPUT);
		case 1:
			if (neighbors[Dir] < 0){
				operationalSteps++;
				return new Move(REPRODUCE, Dir, THIRDCHILDSOUTH);
			}
		default: 
			state = FOODEAST;
			return new Move(STAYPUT);
		}
	}

	public Move firstChildEastMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		int Dir = EAST;
		switch (operationalSteps){
		case 0:
			if (neighbors[Dir] < 0){
				operationalSteps++;
				return new Move(Dir);	
			}else return new Move(STAYPUT);
		case 1:
			if (neighbors[Dir] < 0){
				operationalSteps++;
				return new Move(REPRODUCE, Dir, THIRDCHILDEAST);
			}
		default: 
			state = FOODNORTH;
			return new Move(STAYPUT);
		}
	}

	public Move firstChildWestMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		int Dir = WEST;
		switch (operationalSteps){
		case 0:
			if (neighbors[Dir] < 0){
				operationalSteps++;
				return new Move(Dir);	
			}else return new Move(STAYPUT);
		case 1:
			if (neighbors[Dir] < 0){
				operationalSteps++;
				return new Move(REPRODUCE, Dir, THIRDCHILDWEST);
			}
		default: 
			state = FOODSOUTH;
			return new Move(STAYPUT);
		}
	}
	public Move secondChildSouthMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		int Dir = SOUTH;
		switch (operationalSteps){
		case 0:
			if (neighbors[Dir] < 0){
				operationalSteps++;
				return new Move(Dir);	
			}else return new Move(STAYPUT);
		default: 
			state = FOODWEST;
			return new Move(STAYPUT);
		}
	}
	public Move secondChildNorthMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		int Dir = NORTH;
		switch (operationalSteps){
		case 0:
			if (neighbors[Dir] < 0){
				operationalSteps++;
				return new Move(Dir);	
			}else return new Move(STAYPUT);
		default: 
			state = FOODEAST;
			return new Move(STAYPUT);
		}
	}
	public Move secondChildEastMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		int Dir = EAST;
		switch (operationalSteps){
		case 0:
			if (neighbors[Dir] < 0){
				operationalSteps++;
				return new Move(Dir);	
			}else return new Move(STAYPUT);
		default: 
			state = FOODSOUTH;
			return new Move(STAYPUT);
		}
	}

	public Move secondChildWestMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		int Dir = WEST;
		switch (operationalSteps){
		case 0:
			if (neighbors[Dir] < 0){
				operationalSteps++;
				return new Move(Dir);	
			}else return new Move(STAYPUT);
		default: 
			state = FOODNORTH;
			return new Move(STAYPUT);
		}
	}
	public Move thirdChildSouthMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		int Dir = Utils.dirMap[Utils.turnLeft(Utils.localSOUTH)];
		switch (operationalSteps){
		case 0:
			if (neighbors[Dir] < 0){
				operationalSteps++;
				return new Move(Dir);	
			}else return new Move(STAYPUT);
		default: 
			state = FOODNORTH;
			return new Move(STAYPUT);
		}
	}

	public Move thirdChildNorthMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		int Dir = Utils.dirMap[Utils.turnLeft(Utils.localNORTH)];
		switch (operationalSteps){
		case 0:
			if (neighbors[Dir] < 0){
				operationalSteps++;
				return new Move(Dir);	
			}else return new Move(STAYPUT);
		default: 
			state = FOODSOUTH;
			return new Move(STAYPUT);
		}
	}

	public Move thirdChildEastMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		int Dir = Utils.dirMap[Utils.turnLeft(Utils.localEAST)];
		switch (operationalSteps){
		case 0:
			if (neighbors[Dir] < 0){
				operationalSteps++;
				return new Move(Dir);	
			}else return new Move(STAYPUT);
		default: 
			state = FOODWEST;
			return new Move(STAYPUT);
		}
	}

	public Move thirdChildWestMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		int Dir = Utils.dirMap[Utils.turnLeft(Utils.localWEST)];
		switch (operationalSteps){
		case 0:
			if (neighbors[Dir] < 0){
				operationalSteps++;
				return new Move(Dir);	
			}else return new Move(STAYPUT);
		default: 
			state = FOODEAST;
			return new Move(STAYPUT);
		}
	}

	public Move eatingEastMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		if (foodleft > 0.8 * game.K()){
			enoughFood = true;
		}

		if (neighbors[WEST] < 0){
			state = FOODEAST;
			return new Move(WEST);
		}
		return new Move(STAYPUT);
	}

	public Move eatingWestMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {

		if (foodleft > 0.8 * game.K()){
			enoughFood = true;
		}

		if (neighbors[EAST] < 0){
			state = FOODWEST;
			return new Move(EAST);
		}
		return new Move(STAYPUT);
	}
	public Move eatingNorthMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {

		if (foodleft > 0.8 * game.K()){
			enoughFood = true;
		}

		if (neighbors[SOUTH] < 0){
			state = FOODNORTH;
			return new Move(SOUTH);
		}
		return new Move(STAYPUT);
	}
	public Move eatingSouthMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {

		if (foodleft > 0.8 * game.K()){
			enoughFood = true;
		}

		if (neighbors[NORTH] < 0){
			state = FOODSOUTH;
			return new Move(NORTH);
		}
		return new Move(STAYPUT);
	}
	public Move producedOneEastMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		if (neighbors[NORTH] < 0){
			state = FOODEAST;
			return new Move(REPRODUCE, NORTH,secondChildState);
		}
		return new Move(STAYPUT);
	}

	public Move producedOneWestMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		if (neighbors[SOUTH] < 0){
			state = FOODWEST;
			return new Move(REPRODUCE, NORTH, secondChildState);
		}
		return new Move(STAYPUT);
	}

	public Move producedOneNorthMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		if (neighbors[WEST] < 0){
			state = FOODNORTH;
			return new Move(REPRODUCE, WEST, secondChildState);
		}
		return new Move(STAYPUT);
	}
	public Move producedOneSouthMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		if (neighbors[EAST] < 0){
			state = FOODSOUTH;
			return new Move(REPRODUCE, SOUTH, secondChildState);
		}
		return new Move(STAYPUT);
	}


	public Move testingFoodMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		if (foodleft <= 1 || neighbors[WEST] >= 0 || neighbors[EAST] >= 0 ||neighbors[SOUTH] >= 0 ||neighbors[NORTH] >= 0 ){
			if (foodleft * game.u() + energyleft > game.M()){
				return new Move(REPRODUCE, Utils.dirMap[Utils.turnRight(lastDir)], FINDINGFARM);
			}else {
				if (foodleft == 0){
					if (!foodpresent[Utils.dirMap[lastDir]])
						state = FINDINGFARM;
					return new Move(Utils.dirMap[lastDir]);
				}
				return new Move(STAYPUT);
			}
		}

		state = FOODREADY;
		//TODO hacking here
		return new Move(WEST);

	}
	public Move foodReadyMove(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		state = PRODUCEDONEEAST;
		firstChildState = FIRSTCHILDEAST;
		secondChildState = SECONDCHILDEAST;
		return new Move(REPRODUCE, SOUTH, firstChildState);
	}

	public Move move(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {
		Move m = null; // placeholder for return value
		age++;

		switch(state){
		case FINDINGFARM: 
			return findingFarmMove(foodpresent, neighbors, foodleft, energyleft);
		case FOODNORTH:		
			return foodNorthMove(foodpresent, neighbors, foodleft, energyleft);
		case FOODSOUTH:		
			return foodSouthMove(foodpresent, neighbors, foodleft, energyleft);
		case FOODEAST:		
			return foodEastMove(foodpresent, neighbors, foodleft, energyleft);
		case FOODWEST:		
			return foodWestMove(foodpresent, neighbors, foodleft, energyleft);
		case FIRSTCHILDNORTH:
			return firstChildNorthMove(foodpresent, neighbors, foodleft, energyleft);
		case FIRSTCHILDSOUTH:
			return firstChildSouthMove(foodpresent, neighbors, foodleft, energyleft);
		case FIRSTCHILDEAST:
			return firstChildEastMove(foodpresent, neighbors, foodleft, energyleft);
		case FIRSTCHILDWEST:
			return firstChildWestMove(foodpresent, neighbors, foodleft, energyleft);
		case SECONDCHILDSOUTH:
			return secondChildSouthMove(foodpresent, neighbors, foodleft, energyleft);
		case SECONDCHILDNORTH:
			return secondChildNorthMove(foodpresent, neighbors, foodleft, energyleft);
		case SECONDCHILDWEST:
			return secondChildWestMove(foodpresent, neighbors, foodleft, energyleft);
		case SECONDCHILDEAST:
			return secondChildEastMove(foodpresent, neighbors, foodleft, energyleft);
		case THIRDCHILDSOUTH:
			return thirdChildSouthMove(foodpresent, neighbors, foodleft, energyleft);
		case THIRDCHILDNORTH:
			return thirdChildNorthMove(foodpresent, neighbors, foodleft, energyleft);
		case THIRDCHILDEAST:
			return thirdChildEastMove(foodpresent, neighbors, foodleft, energyleft);
		case THIRDCHILDWEST:
			return thirdChildWestMove(foodpresent, neighbors, foodleft, energyleft);
		case EATINGEAST:
			return eatingEastMove(foodpresent, neighbors, foodleft, energyleft);
		case EATINGWEST:
			return eatingWestMove(foodpresent, neighbors, foodleft, energyleft);
		case EATINGNORTH:
			return eatingNorthMove(foodpresent, neighbors, foodleft, energyleft);
		case EATINGSOUTH:
			return eatingSouthMove(foodpresent, neighbors, foodleft, energyleft);
		case PRODUCEDONEEAST:
			return producedOneEastMove(foodpresent, neighbors, foodleft, energyleft);
		case PRODUCEDONEWEST:
			return producedOneWestMove(foodpresent, neighbors, foodleft, energyleft);
		case PRODUCEDONENORTH:
			return producedOneNorthMove(foodpresent, neighbors, foodleft, energyleft);
		case PRODUCEDONESOUTH:
			return producedOneSouthMove(foodpresent, neighbors, foodleft, energyleft);
		case TESTINGFOOD:
			return testingFoodMove(foodpresent, neighbors, foodleft, energyleft);
		case FOODREADY:
			return foodReadyMove(foodpresent, neighbors, foodleft, energyleft);

		}
		System.out.println(this + ": UNIDENTIDIED STATE:" + state);
		return new Move(STAYPUT);



	}

}
