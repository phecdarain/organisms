package organisms.g5;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;

import organisms.Move;
import organisms.OrganismsGame;
import organisms.Player;

//This is not a valid player, just providing general utility functions  

public class Utils implements Player {

	public final static int[] dirMap = {NORTH,WEST,SOUTH,EAST};
	public final static int code = 0x55;
	
	public final static int localNORTH = 0;
	public final static int localWEST = 1;
	public final static int localSOUTH = 2;
	public final static int localEAST = 3;
	
	public static boolean isEnemy(int num){
		if (num < 0)
			return false;
		else return num != Utils.code;
	}
	
	public static boolean enemyAround(int[] neighbors){
		for (int i = 0; i != 4; i++){
			int dir = dirMap[i];
			if (isEnemy(neighbors[dir]))
				return true;
		}
		return false;
	}
	
	public static int findEmptySpace(int[] neighbors){
		for (int i = 0; i != 4; i++){
			int dir = dirMap[i];
			if (neighbors[dir] < 0)
				return i;
		}
		return -1;
	}

	public static int numberNeighbors(int[] neighbors){
		int cnt = 0;
		for (int i = 0; i != 4; i++){
			int dir = dirMap[i];
			if (neighbors[dir] >= 0)
				cnt++;
		}
		return cnt;
	}

	public static int findFood(boolean[] foodpresent){
		for (int i = 0; i != 4; i++){
			int dir = dirMap[i];
			if (foodpresent[dir])
				return i;
		}
		return -1;
	}
	public static int getReverseDir(int dir){
		switch (dirMap[dir]){
		case SOUTH: return localNORTH;
		case NORTH: return localSOUTH;
		case EAST: return localWEST;
		case WEST: return localEAST;
		}
		return -1;
	}
	
	public static int turnLeft(int dir) {
		switch (dirMap[dir]){
		case SOUTH: return localEAST;
		case NORTH: return localWEST;
		case EAST: return localNORTH;
		case WEST: return localSOUTH;
		}
		return -1;
	}

	public static int turnRight(int dir) {
		switch (dirMap[dir]){
		case SOUTH: return localWEST;
		case NORTH: return localEAST;
		case EAST: return localSOUTH;
		case WEST: return localNORTH;
		}
		return -1;
	}
	public static int turnRightToDir(int dir) {
		return dirMap[turnRight(dir)];
	}
	
	public static int turnLeftToDir(int dir) {
		return dirMap[turnLeft(dir)];
	}
	
	public static int reverseToDir(int dir) {
		return dirMap[getReverseDir(dir)];
	}
	
	@Override
	public void register(OrganismsGame __amoeba, int key) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String name() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Color color() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean interactive() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Move move(boolean[] food, int[] enemy, int foodleft, int energyleft)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int externalState() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
}
