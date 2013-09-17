package organisms.g5;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;

import organisms.Move;
import organisms.OrganismsGame;
import organisms.Player;

public class Group5Flood extends Group5Survivor {

	static final String _CNAME = "Flood";
	static final Color _CColor = new Color(0f, 0f, 1f);

	private int lastDir = 0;
	private int generation = 0;

	private double firstGenBar = 0.4;
	private double secondGenBar = 0.4;
	private double thirdGenBar = 0.4;
	/*
	 * This method is called when the Organism is created.
	 * The key is the value that is passed to this organism by its parent (not used here)
	 */
	public void register(OrganismsGame game, int key) throws Exception
	{
		if (key < 0){
			surviving = false;// change this to turn on surviving mode
			state = 0;
		}else {

			generation = getGeneration(key);
			state = generation;
			lastDir = getDir(key);
		}
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
		//return generation;
		return Utils.code;
	}
	
	int childBar = 1000;
	
	private int numberChild = 0;

	private int constructState(int dir, int generation){
		return (generation << 2) | dir;
	}

	private int getGeneration(int state){
		return state >> 2;
	}

	private int getDir(int state){
		return state & 3;
	}

	public Move move(boolean[] foodpresent, int[] neighbors, int foodleft, int energyleft) throws Exception {

		if (surviving){
			return survivingMove(foodpresent,neighbors, foodleft, energyleft);
		}

		double energyRatio = (foodleft * game.u() + energyleft + 0.0) / game.M();
		if (energyRatio >= 1)
			generation  = 0;
		else if (energyRatio >= secondGenBar + 0.8)
			generation = 1;
		else if (energyRatio < 0.1)
			generation = 2;

		int foodDir = Utils.findFood(foodpresent);
		
		switch (generation){
		case 0: 
			if (energyRatio >= firstGenBar){
				int childDir = (numberChild % 2 == 1)?Utils.turnLeft(lastDir):Utils.turnRight(lastDir);
				if(foodDir >= 0 || neighbors[Utils.dirMap[childDir]] < 0){
					numberChild ++;
					if (numberChild >= childBar)
						generation++;
					if (foodDir >= 0)
						return new Move(REPRODUCE, Utils.dirMap[foodDir], constructState(childDir, 1));
					else return new Move(REPRODUCE, Utils.dirMap[childDir], constructState(childDir, 1));
				}
			}
			
			if (foodleft > 0){
				if (foodDir < 0)
					return new Move(STAYPUT);
				else 
					return new Move(REPRODUCE, Utils.dirMap[foodDir], constructState(foodDir, 1));
			}
			
			if (foodDir < 0){
				if (neighbors[Utils.dirMap[lastDir]] < 0)
					return new Move(Utils.dirMap[lastDir]);
				else return new Move(STAYPUT);
			}else return new Move(Utils.dirMap[foodDir]);

		case 1:
			if (energyRatio >= secondGenBar){
				int childDir = (numberChild % 2 == 1)?Utils.turnLeft(lastDir):Utils.turnRight(lastDir);
				if(foodDir >= 0 || neighbors[Utils.dirMap[childDir]] < 0){
					numberChild ++;
					if (numberChild >= childBar)
						generation++;
					if (foodDir >= 0)
						return new Move(REPRODUCE, Utils.dirMap[foodDir], constructState(childDir, 2));
					else return new Move(REPRODUCE, Utils.dirMap[childDir], constructState(childDir, 2));
				}
			}

			if (foodleft > 0){
				if (foodDir < 0)
					return new Move(STAYPUT);
				else
					return new Move(REPRODUCE, Utils.dirMap[foodDir], constructState(foodDir, 2));
			}
			
			if (foodDir < 0){
				if (neighbors[Utils.dirMap[lastDir]] < 0)
					return new Move(Utils.dirMap[lastDir]);
				else return new Move(STAYPUT);
			}else return new Move(Utils.dirMap[foodDir]);

		case 2:

			if (energyRatio >= thirdGenBar){
				int childDir = lastDir;
				if (neighbors[childDir] < 0){
					lastDir = Utils.turnRight(lastDir);
					return new Move(REPRODUCE, Utils.dirMap[childDir], constructState(childDir, generation));
				}
			}

			if (foodleft > 0)
				return new Move(STAYPUT);

			if(Utils.enemyAround(neighbors)){
				if (foodDir < 0)
					return new Move(STAYPUT);
				else
					return new Move(Utils.dirMap[foodDir]);
				
			}

			if (foodDir < 0){
				int numNeighbors = Utils.numberNeighbors(neighbors);
				if (numNeighbors >= 2){
					int dir = Utils.findEmptySpace(neighbors);
					if (dir < 0)
						return new Move(STAYPUT);
					if (lastDir != Utils.getReverseDir(dir)){
						lastDir = dir;
						return new Move(Utils.dirMap[dir]);
					}

				}
				return new Move(STAYPUT);

			}else return new Move(Utils.dirMap[foodDir]);
		}


		return new Move(STAYPUT);

	}


}
