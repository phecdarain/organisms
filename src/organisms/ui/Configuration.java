//***********************************************************
//*
//* File:           Configuration.java
//* Author:         Abhinav Kamra
//* Contact:        kamra-at-cs.columbia.edu
//* Update:         9.5.2003
//*
//* Description:    Configuration object for Project 1, CS4444
//*                 Fall 2003
//*
//*
//***********************************************************

package organisms.ui;
import java.util.*;

public final class Configuration implements IFCConfiguration {

    int[] _numroundsbounds;
    int[] _numplayersbounds;
    int[] _numXbounds;
    int[] _numYbounds;
    int[] _numubounds;
    int[] _numvbounds;
    int[] _numMbounds;
    int[] _numKbounds;
    
    double[] _numpbounds;
    double[] _numqbounds;

    
    int _init_energy;
    int _numrounds;
    int X, Y, u, v, M, K;
    double p, q;
    Class[] _classlist;
    Class[] _playerlist;
    String _logfile;
    
    
    public void setInitEnergy(int _init) throws Exception {
        _init_energy = _init;
    }

    public int InitEnergy() throws Exception {
        return _init_energy;
    }
    
    public void setNumRounds(int __numrounds) throws Exception {
        _numrounds = __numrounds;
    }

    public int numRounds() throws Exception {
        return _numrounds;
    }
    
    public void setNumRoundsBounds(int __min, int __max) throws Exception {
        _numroundsbounds = new int[2];
        _numroundsbounds[0] = __min;
        _numroundsbounds[1] = __max;
    }
    
    public int numRoundsMin() throws Exception {
        return _numroundsbounds[0];
    }

    public int numRoundsMax() throws Exception {
        return _numroundsbounds[1];
    }

    public int numPlayers() throws Exception {
        return _playerlist.length;
    }
    
    public void setNumPlayersBounds(int __min, int __max) throws Exception {
        _numplayersbounds = new int[2];
        _numplayersbounds[0] = __min;
        _numplayersbounds[1] = __max;
    }
    
    public int numPlayersMin() throws Exception {
        return _numplayersbounds[0];
    }

    public int numPlayersMax() throws Exception {
        return _numplayersbounds[1];
    }
    
    public void setGridX(int _x) throws Exception {
        X = _x;
    }
    
    public int GridX() throws Exception {
        return X;
    }

    public void setXBounds(int __min, int __max) throws Exception {
        _numXbounds = new int[2];
        _numXbounds[0] = __min;
        _numXbounds[1] = __max;
    }
    
    public void setGridY(int _y) throws Exception {
        Y = _y;
    }
    
    public int GridY() throws Exception {
        return Y;
    }

    public void setYBounds(int __min, int __max) throws Exception {
        _numYbounds = new int[2];
        _numYbounds[0] = __min;
        _numYbounds[1] = __max;
    }
    
    public void setv(int _v) throws Exception {
        v = _v;
    }
    
    public int v() throws Exception {
        return v;
    }

    public void setvBounds(int __min, int __max) throws Exception {
        _numvbounds = new int[2];
        _numvbounds[0] = __min;
        _numvbounds[1] = __max;
    }
    
    public void setu(int _u) throws Exception {
        u = _u;
    }
    
    public int u() throws Exception {
        return u;
    }

    public void setuBounds(int __min, int __max) throws Exception {
        _numubounds = new int[2];
        _numubounds[0] = __min;
        _numubounds[1] = __max;
    }
    
    public void setM(int _m) throws Exception {
        M = _m;
    }
    
    public int M() throws Exception {
        return M;
    }

    public void setMBounds(int __min, int __max) throws Exception {
        _numMbounds = new int[2];
        _numMbounds[0] = __min;
        _numMbounds[1] = __max;
    }
    
    public void setK(int _k) throws Exception {
        K = _k;
    }
    
    public int K() throws Exception {
        return K;
    }

    public void setKBounds(int __min, int __max) throws Exception {
        _numKbounds = new int[2];
        _numKbounds[0] = __min;
        _numKbounds[1] = __max;
    }
    
    public void setp(double _p) throws Exception {
        p = _p;
    }
    
    public double p() throws Exception {
        return p;
    }

    public void setpBounds(double __min, double __max) throws Exception {
        _numpbounds = new double[2];
        _numpbounds[0] = __min;
        _numpbounds[1] = __max;
    }
    
    public void setq(double _q) throws Exception {
        q = _q;
    }
    
    public double q() throws Exception {
        return q;
    }

    public void setqBounds(double __min, double __max) throws Exception {
        _numqbounds = new double[2];
        _numqbounds[0] = __min;
        _numqbounds[1] = __max;
    }
    
    public void setClassList(Class[] __list) throws Exception {
        int _MAX = __list.length;
        
        _classlist = new Class[_MAX];
        System.arraycopy(__list, 0, _classlist, 0, _MAX);    
    }    
    
    public Class[] classList() throws Exception {
        int _MAX = _classlist.length;
        Class[] RET = new Class[_MAX];
        
        System.arraycopy(_classlist, 0, RET, 0, _MAX);
        return RET;    
    }
    
    public Class getClass(int __pos) throws Exception {
        return _classlist[__pos];
    }
    
    public void setClass(int __pos, Class __class) throws Exception {
        _classlist[__pos] = __class;
    }
    
    public void setPlayerList(Class[] __list) throws Exception {
        int _MAX = __list.length;
        
        if (_MAX < _numplayersbounds[0] || _MAX > _numplayersbounds[1]) {
            throw new Exception("Error:  Number of Players Out of Range: "+_MAX);
        }
        _playerlist = new Class[_MAX];
        System.arraycopy(__list, 0, _playerlist, 0, _MAX);
    } 
    
    public Class[] playerList() throws Exception {
        int _MAX = _playerlist.length;
        Class[] RET = new Class[_MAX];
        
        System.arraycopy(_playerlist, 0, RET, 0, _MAX);
        return RET;
    }

    public Class[] RandomplayerList() throws Exception {
        int _MAX = _playerlist.length;
        Class[] RET = new Class[_MAX];
	ArrayList ar = new ArrayList(_MAX);
	Random rand = new Random();

	for(int i=0;i < _MAX;i++)
		ar.add(new Integer(i));

	for(int i=0;i < _MAX;i++)
	{
		int index = ((Integer)ar.remove(rand.nextInt(ar.size()))).intValue();
		RET[i] = _playerlist[index];
	}
        
        return RET;
    }

    public Class player(int __pos) throws Exception {
        return _playerlist[__pos];
    }

    public void setPlayer(int __pos, Class __class) throws Exception {
        _playerlist[__pos] = __class;
    }
    
    public String logFile() throws Exception {
        return _logfile;
    }

    public void setLogFile(String __logfile) throws Exception {
        _logfile = __logfile;
    }
}
