//***********************************************************
//*
//* File:           IFCPersistent.java
//* Author:         Abhinav Kamra
//* Contact:        kamra-at-cs.columbia.edu
//* Update:         10.16.2003
//*
//* Description:    Persistent player interface for players
//*                 that need to be maintained across games.
//*                 Furthermore, these players also need
//*                 to be notified of the final result of
//*                 each game.
//*
//***********************************************************

package organisms;

public interface PersistentPlayer extends Player {

    public void gameOver()                            throws Exception;
}
