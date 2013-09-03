//***********************************************************
//*
//* File:           IFCPlayer.java
//* Author:         Abhinav Kamra
//* Contact:        kamra-at-cs.columbia.edu
//* Update:         10.16.2003
//*
//* Description:    Player interface for all Rectangle
//*                 contestants.  The interface is
//*                 intentionally minimal to accelerate
//*                 player development.
//*
//***********************************************************

package organisms;

import java.io.Serializable;
import java.awt.Color;

public interface Player extends Constants, Serializable {

    public void register(OrganismsGame __amoeba, int key) throws Exception;
    public String   name()                            			throws Exception;
    public Color    color()                           			throws Exception;
    public boolean  interactive()                     			throws Exception;
    public Move	    move(boolean[] food, int[] enemy, int foodleft, int energyleft)	throws Exception;
    public int      externalState()                   			throws Exception;
}

