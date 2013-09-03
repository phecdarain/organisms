//***********************************************************
//*
//* File:           IFCConfiguration.java
//* Author:         Abhinav Kamra
//* Contact:        kamra-at-cs.columbia.edu
//* Update:         9.5.2003
//*
//* Description:    Configuration object interface.  Used to
//*                 store, specify, and retrieve
//*                 configuration parameters during
//*                 model instantiation.
//*
//***********************************************************

package organisms.ui;

import java.io.Serializable;

public interface IFCConfiguration extends Serializable {

	// Game parameters
    public void     setNumRounds(int __numrounds)               throws Exception;
    public int      numRounds()                                 throws Exception;
    public void     setNumRoundsBounds(int __min, int __max)    throws Exception;
    public int      numRoundsMin()                              throws Exception;
    public int      numRoundsMax()                              throws Exception;
    public int      numPlayers()                                throws Exception;
    public void     setNumPlayersBounds(int __min, int __max)   throws Exception;
    public int      numPlayersMin()                             throws Exception;
    public int      numPlayersMax()                             throws Exception;

    public void     setInitEnergy(int _init)               throws Exception;
    public int      InitEnergy()                                 throws Exception;
    
    public void	    setGridX(int x)				throws Exception;
    public int	    GridX()					throws Exception;
    public void	    setXBounds(int x1, int x2)			throws Exception;
    public void	    setGridY(int y)				throws Exception;
    public int	    GridY()					throws Exception;
    public void	    setYBounds(int y1, int y2)			throws Exception;

    public void	     setp(double p)				throws Exception;
    public double    p()					throws Exception;
    public void      setpBounds(double p1, double p2)		throws Exception;
    public void      setq(double q)				throws Exception;
    public double    q()					throws Exception;
    public void      setqBounds(double q1, double q2)		throws Exception;

    public void	    setv(int v)					throws Exception;
    public int	    v()						throws Exception;
    public void	    setvBounds(int v1, int v2)			throws Exception;
    public void	    setu(int u)					throws Exception;
    public int	    u()						throws Exception;
    public void	    setuBounds(int u1, int u2)			throws Exception;

    public void	    setM(int m)					throws Exception;
    public int	    M()						throws Exception;
    public void	    setMBounds(int m1, int m2)			throws Exception;
    public void	    setK(int k)					throws Exception;
    public int	    K()						throws Exception;
    public void	    setKBounds(int k1, int k2)			throws Exception;

    // Class and Players
    public void     setClassList(Class[] __list)                throws Exception;
    public Class[]  classList()                                 throws Exception;
    public Class    getClass(int __pos)                         throws Exception;
    public void     setClass(int __pos, Class __class)          throws Exception;
    public void     setPlayerList(Class[] __list)               throws Exception;
    public Class[]  playerList()                                throws Exception;
    public Class    player(int __pos)                           throws Exception;
    public void     setPlayer(int __pos, Class __class)         throws Exception;
    public String   logFile()                                   throws Exception;
    public void     setLogFile(String __str)                    throws Exception;
}
