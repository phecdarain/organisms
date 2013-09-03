//***********************************************************
//*
//* File:           IFCGameRecord.java
//* Author:         Abhinav Kamra
//* Contact:        kamra-at-cs.columbia.edu
//* Update:         9.13.2003
//*
//* Description:    Interface for individual game entries
//*                 used to specify and store tournament
//*                 games.
//*
//***********************************************************

package organisms.ui;

import java.io.Serializable;

public interface IFCGameRecord extends Serializable { 

    public void     setPlayers(Class[] __players)               throws Exception;
    public Class[]  players()                                   throws Exception;
    public void     setScores(double[] __scores)                throws Exception;
    public double[] scores()                                    throws Exception;
    public void     setBatchComplete(boolean __batchcomplete)   throws Exception;
    public boolean  batchComplete()                             throws Exception;
}
