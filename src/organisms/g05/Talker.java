package organisms.g05;

import java.util.*;
import java.io.*;
import java.awt.Color;

import organisms.*;
import organisms.g05.*;

public final class Talker implements Player {

  // Knobs
  // estimated board size - used for population and p estimation
  public static final int m = 5; 
  public static final int n = 5;
  public static final double initialEstimatedp = 0.01;
  public static final double initialEstimatedq = 0.02;
  public static final double allowFoodToGrowFactor = 0.5;
  public static final int minimumMovesFactor = 4;
  public static final double maxFoodGrowMovesFactor = 0.1; 


  // internal state
  public static final int LOOKING_FOR_FOOD = 0;
  public static final int EATING_FOOD = 1;
  public static final int WAITING_FOR_FOOD_TO_GROW = 2;

  // external state masks
  public static final int MOVE_WEST = 0x01;


	static final String _CNAME = "Chit-Chat";
	static final Color _CColor = new Color(0.0f, 0.50f, 0.00f);
	private int state;
  private int internalState;
	private Random rand;
	private OrganismsGame game;

  // Known consts
  private int s, v, u, M, K;

  // estimated values
  private double p, q;

  // total moves
  private int moves;

  // used while waiting for food to grow
  private int foodDirection;
  private int currentWaitMove;
  private int waitMoves;
  private int foodLevel;


	/*
	 * This method is called when the Organism is created.
	 * The key is the value that is passed to this organism by its parent (not used here)
	 */
	public void register(OrganismsGame game, int key) throws Exception
	{
		rand = new Random();
		this.game = game;
    s = game.s();
		v = game.v();
    u = game.u();
		M = game.M();
    K = game.K();
    moves = 0;

    // TODO, inherit from parent
    p = initialEstimatedp;
    q = initialEstimatedq;

    // TODO
    state = 0;
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
	  moves++;	


    // Step 0: Check whether we need to move from this block
    if (neighbors[EAST] == MOVE_WEST || state == MOVE_WEST) {
      //game.println ("attempt to move..");
      int direction =  chooseMoveDirection(neighbors);
      m = new Move (direction);  
      if (direction != STAYPUT) {
        internalState = LOOKING_FOR_FOOD;
      } else {
        // transmit state
        state = MOVE_WEST;
      }
      return m;
    }

    // Step 1: Check whether we are eating food
    if (foodleft > 0 && internalState != WAITING_FOR_FOOD_TO_GROW) {
      //game.println ("foodleft: " + foodleft);
      foodLevel = foodleft;
      if ((foodleft*u > (M - energyleft) || energyleft < minimumEnergy()) && energyleft <= (M-u)) {
        //game.println ("Filling tank!");
        internalState = EATING_FOOD;
        // fill tank!
        m = new Move(STAYPUT);
      } else {
        double gainKeepEating = payoffToEatFood() - costToEatFood();
        double gainStopEating = payoffToStopEatingFood(foodleft) - costToStopEatingFood();

        //game.println ("Gain keep eating: " + gainKeepEating);
        //game.println ("Gain stop eating: " + gainStopEating);

        if (gainKeepEating > gainStopEating) {
          //game.println ("Keep eating!");
          internalState = EATING_FOOD;
          m = new Move(STAYPUT);
        } else {
          int direction = chooseMoveDirection(neighbors);
          // check whether we are trapped
          if (direction == STAYPUT) {
            state = MOVE_WEST;
          }

          waitMoves = estimateMovesToFoodLevel(allowFoodToGrowFactor);
          //game.println ("Estimated wait: " + waitMoves);
          if (waitMoves > (maxFoodGrowMovesFactor*M)/s) {
            direction = STAYPUT;
          }
          if (direction == STAYPUT) {
            //game.println ("Keep eating, too many moves!");
            internalState = EATING_FOOD;
          } else {
            //game.println ("Yield farm.");
            internalState = WAITING_FOR_FOOD_TO_GROW;
            currentWaitMove = 0;
            foodDirection = findOppositeDirection(direction);
          } 
          m = new Move(direction);
        }
      }
      return m;
    }

    // Step 2: Check whether we are waiting for food to grow
    if (internalState == WAITING_FOR_FOOD_TO_GROW) {
      // check whether the food still exist, otherwise fall through and search for more food
      if (foodpresent[foodDirection]) {
        currentWaitMove++;
        if ((currentWaitMove >= waitMoves || energyleft < minimumEnergy()) && energyleft <= (M-u)) {
          //game.println ("Wait over, eat!");
          internalState = EATING_FOOD;
          m = new Move(foodDirection);
        } else {
          //game.println ("Waiting for food to grow.");
          // TODO: for now, reproduce. need a better equation (cost/payoff) for reproduction logic
          if (energyleft > (M-u) && foodLevel*u > (energyleft+v)) {
            //game.println ("Reproduce.");
            // reproduce onto free tile
            m = new Move (REPRODUCE, foodDirection, 0);
            foodLevel = foodLevel - (energyleft+v);
          } else {
            m = new Move(STAYPUT);
          }
        }
        return m;
      }
    }

    // if food is around, eat it. otherwise, move.
    for (int i = 1; i < foodpresent.length; i++) {
      if (foodpresent[i] && neighbors[i] != 0) {
        if (energyleft < (M - u)) {
          m = new Move(i); 
        } else {
          //game.println ("Found food. Waiting.");
          m = new Move(STAYPUT);
        }
        return m;
      }
    }

    // move logic
    internalState = LOOKING_FOR_FOOD;
    double gainStayNoFood = payoffToStayNoFood() - costToStayNoFood();
    double gainMoveNoFood = payoffToMoveNoFood() - costToMoveNoFood();

    //game.println ("Gain Stay No Food: " + gainStayNoFood);
    //game.println ("Gain Move No Food: " + gainMoveNoFood);

    if (gainStayNoFood > gainMoveNoFood) {
      //game.println ("STAYPUT!");
      m = new Move(STAYPUT);
    } else {
      //game.println ("Move!");
      m = new Move(chooseMoveDirection(neighbors));
    }
		return m;
	}



  public int chooseMoveDirection (int[] neighbors) {
    if (neighbors[WEST] == -1)
      return WEST;
    else if (neighbors[NORTH] == -1)
      return NORTH;
    else if (neighbors[EAST] == -1)
      return EAST;
    else if (neighbors[SOUTH] == -1)
      return SOUTH;
    else
      return STAYPUT;
  }

  public int findOppositeDirection (int direction) {
    if (direction == WEST)
      return EAST;
    else if (direction == NORTH)
      return SOUTH;
    else if (direction == EAST)
      return WEST;
    else 
      return NORTH;
  }

  /*
   * Minimum energy: panic mode, because survival is the first priority!
   */

  public int minimumEnergy () {
    return minimumMovesFactor*v;
  }

  /*
   *  Cost to stay when no food around
   */

  public double costToStayNoFood () {
    double cost = s;// + (m*p*3)*u;
    return cost;
  }

  /*
   *  Payoff to stay when no food around
   */

  public double payoffToStayNoFood () {
    double payoff = (4*p*u);
    return payoff;
  }

  /*
   *  Cost to move when no food around
   */

  public double costToMoveNoFood () {
    double cost = v;// + (4*p*u);
    return cost;
  }

  /*
   *  Payoff to move when no food around
   */

  public double payoffToMoveNoFood () {
    int mv;
    if (moves < m)
    {
      mv = moves;
    } 
    else
    {
      mv = m;
    }
    double payoff = mv * p * 3 * u;
    return payoff;
  }

  /*
   *  Cost to eat food
   */

  public double costToEatFood () {
    double cost = s;
    return cost;
  }

  /*
   *  Payoff to eat food
   */

  public double payoffToEatFood () {
    double payoff = u;
    return payoff;
  }


  /*
   *  Cost to stop eating food
   */

  public double costToStopEatingFood () {
    // find estimated moves to double the food
    double estimatedMoves = estimateMovesToFoodLevel(allowFoodToGrowFactor);
    double cost = 2*v + (estimatedMoves * s);
    return cost;
  }

  public int estimateMovesToFoodLevel (double factor) {
    return (int) (factor/q);
  }

  /*
   *  Payoff to eat food
   */

  public double payoffToStopEatingFood (int food) {
    double payoff = allowFoodToGrowFactor*food*u;
    return payoff;
  }

}

