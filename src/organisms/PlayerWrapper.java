//***********************************************************
//*
//* File:           PlayerWrapper.java
//* Author:         Abhinav Kamra
//* Contact:        kamra-at-cs.columbia.edu
//* Update:         10.16.2003
//*
//* Description:    Compositional wrapper for IFCPlayer
//*                 objects.
//*
//***********************************************************

package organisms;

import java.util.*;
import java.io.*;
import java.awt.Color;

public final class PlayerWrapper implements Serializable, Player {

	int	      _energy;
	int	      _extState;
	double        _score;
	Player     _player;
	Class         _class;
	OrganismsGame    _amoeba;

	PlayerWrapper(Class __class) throws Exception {
        _class = __class;
	_energy = 0;
	}
    
    private void register_priv(OrganismsGame __amoeba, int key) throws Exception {
        try {
            _amoeba = __amoeba;
            _player.register(_amoeba, key);
	    _extState = 0;
	    externalState();
        } catch (Exception EXC) {
            System.out.println(EXC.getMessage());
            EXC.printStackTrace();
	    System.out.println("Player " + _class + " threw an Exception in register()");
        }
    }


	public void register(OrganismsGame __amoeba, int key) throws Exception {
        _player = (Player) _class.newInstance();
        register_priv(__amoeba, key);

    }

	/*
    public void register(Organisms2 __amoeba, IFCPlayer __player) throws Exception {
        _player = __player;
        _class = _player.getClass();
        register_priv(__amoeba);
    }
    */

	public String name() throws Exception {
        try {
	        return _player.name();
        } catch (Exception EXC) {
            System.out.println(EXC.getMessage());
            EXC.printStackTrace();
	    System.out.println("Player " + _class + " threw an Exception in name()");
	    return "Anonymous";
        }
    }

    public int externalState() throws Exception
    {
        try {
	        int x = _player.externalState();
		if((x >= MIN_EXTSTATE)&(x <= MAX_EXTSTATE))
			_extState = x;
		return _extState;
        } catch (Exception EXC) {
            System.out.println(EXC.getMessage());
            EXC.printStackTrace();
	    System.out.println("Player " + _class + " threw an Exception in externalState()");
        }
	return _extState;
    }

    
    public Color color() throws Exception {
    	if (_player != null) return _player.color();
    	else  return new Color(1.0f, 1.0f, 0.9f);
        
    }

    public Class playerClass() throws Exception {
        return _class;
    }

    public void gameOver() throws Exception {
        try {
            if (_player instanceof PersistentPlayer) {
                ((PersistentPlayer) _player).gameOver();
            }
        } catch (Exception EXC) {
            System.out.println(EXC.getMessage());
            EXC.printStackTrace();
	    System.out.println("Player " + _class + " threw an Exception in gameOver()");
        }
    }

    public Move move(boolean[] foodvalue, int[] enemies, int foodleft, int redundant_energy) throws Exception {
        try {
            Move RET = _player.move(foodvalue, enemies, foodleft, _energy);
            
            return RET;

        } catch (Exception EXC) {
            System.out.println(EXC.getMessage());
            EXC.printStackTrace();
	    System.out.println("Player " + _class + " threw an Exception in move()");
            return null;
        }
    }

    public boolean interactive() throws Exception {
        try {
            return _player.interactive();
        } catch (Exception EXC) {
            System.out.println(EXC.getMessage());
            EXC.printStackTrace();
	    System.out.println("Player " + _class + " threw an Exception in interactive()");
	    return false;
        }
    }

	public double score() {
	    return _score;
    }

	public int energy() {
	    return _energy;
    }

	public int extState() {
	    return _extState;
    }

    public void setScore(double __score) {
        _score = __score;
    }

    public void setEnergy(int __energy) {
        _energy = __energy;
    }

    public Player player() {
        return _player;
    }

    public String toString() {
	    try {
    		StringBuffer SB = new StringBuffer("[");

            SB.append(_player.name());
		    SB.append("]");
    		return new String(SB);
        } catch (Exception EXC) {
            return EXC.getMessage();
        }
	}
}
