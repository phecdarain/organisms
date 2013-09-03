//***********************************************************
//*
//* File:           Organisms2.java
//* Author:         Abhinav Kamra
//* Contact:        kamra-at-cs.columbia.edu
//* Update:         10/16/2003
//*
//* Description:    Game model for Project 4, CS4444
//*                 Fall 2013
//*
//*
//***********************************************************


package organisms;

import organisms.ui.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.TableModel;

import organisms.ui.Configuration;
import organisms.ui.GUI;
import organisms.ui.IFCConfiguration;
import organisms.ui.IFCGameRecord;
import organisms.ui.IFCModel;
import organisms.ui.IFCTournament;
import organisms.ui.IFCUI;
import organisms.ui.ParseValue;
import organisms.ui.SlotPanel;
import organisms.ui.Util;

import java.text.NumberFormat;

// for graphing
import java.awt.geom.*;


public final class OrganismsGame implements Constants, IFCModel {

    Vector roundList;  // all the rounds and what players looked like on them
    GraphDrawer graph;
    boolean shouldGraph;
    
    private static final boolean fightingAllowed = false;
    private static int[][] food;

    private Class[]                 _classlist;
    private Class[]                 _playerlist;
    private int                     _currRound;
    private int                     _maxrounds;
    private int                     _numplayers;
    private int                     _state;
    private transient IFCUI         _ui;
    private IFCConfiguration        _config;
    private static Random           _random;
    private JTextField              _input;
    private ControlPanel            _control;
    private ViewPanel               _view;
    private boolean                 _registered;
    private static final int        _CMOVING                = 0;
    private static final int        _CWAITING               = 1;
    private static final int        _CFINISHED              = 2;
    private static final int[]      _CSTATES                = { _CMOVING, _CWAITING, _CFINISHED };
    private static final String     _CNAME                  = "CIS 700 - Fall 2013 - Organisms 1.0";
    private static final String     _CPROPERTIES_FILE       = "gamemodel.properties";
    private static final int        _CMIN_ROUNDS            = 1;
    private static final int        _CMAX_ROUNDS            = 500000;
    private static final int        _CMIN_PLAYERS           = 1;
    private static final int        _CMAX_PLAYERS           = 100;
    private static final int        _CMIN_X		    = 1;
    private static final int        _CMAX_X		    = 1000;
    private static final int        _CMIN_Y		    = 1;
    private static final int        _CMAX_Y		    = 1000;
    private static final int        _CMIN_v		    = 1;
    private static final int        _CMAX_v		    = 1000;
    private static final int        _CMIN_u		    = 1;
    private static final int        _CMAX_u		    = 1000;
    private static final int        _CMIN_M		    = 1;
    private static final int        _CMAX_M		    = 1000;
    private static final int        _CMIN_K		    = 1;
    private static final int        _CMAX_K		    = 1000;
    private static final double     _CMIN_p		    = 0.0;
    private static final double     _CMAX_p		    = 1.0;
    private static final double     _CMIN_q		    = 0.0;
    private static final double     _CMAX_q		    = 1.0;
    
    private HashMap<Class, FightResults> fightResults;
    
    class FightResults {
    	int attackWin, attackLoss, defendWin, defendLoss;
    }

    class PlayerEntry
    {
	    Class _playerclass;
	    String _name;
	    Color _color;
	    int _population;
	    int _totalenergy;
	    boolean isExtinct = false;

	    PlayerEntry(Class __class, String __name, Color __color)
	    {
		    _population = 1;
		    _playerclass = __class;
		    _name = __name;
		    _color = __color;
		    _totalenergy = 0;
	    }
	    double score()
	    {
		    return (_population <= 0)?(0.0):(_totalenergy / _population);
	    }
	    String name()
	    {
		    return _name;
	    }
	    Color color()
	    {
		    return _color;
	    }
	    Class playerclass()
	    {
		    return _playerclass;
	    }
	    int population()
	    {
		    return _population;
	    }
	    int totalenergy()
	    {
		    return _totalenergy;
	    }

	    void AddPop(int increment)
	    {
		    _population += increment;
	    }
	    void AddEnergy(int add)
	    {
		    _totalenergy += add;
	    }
    }

 class Cell
 {
	 int foodvalue;
	 int playertype;
	 public PlayerWrapper pw;
	 boolean movedone;
 }

    private int population;	// Total number of amoebae on the grid
    
    private int init_energy;	// Initial Energy of an Organism
    private int X;	// Grid Width
    private int Y;	// Grid Height
    private int M;	// Maximum energy per organism
    private int K;	// Maximum Food per cell
    private int s;	// Enery consumed in staying put
    private int v;	// Energy consumed in moving / reproducing
    private int u;	// Energy per unit food
    private double p;	// Food Generation probability
    private double q;	// Food Doubling probability
    private Cell[][] cells;	// Grid
    private PlayerEntry[] OrigPlayers;	// Player Classes

    //********************************************
    //*
    //* Constructors
    //*
    //********************************************
    public OrganismsGame() throws Exception {
	roundList = new Vector();
	shouldGraph = false;
	create(createDefaultConfiguration());
    }

    public OrganismsGame(IFCConfiguration __config) throws Exception {
        create(__config);
    }

    public OrganismsGame(IFCTournament __tournament) throws Exception {
        run(__tournament);
    }
    
    //********************************************
    //*
    //* Constructor Delegates
    //*
    //********************************************
    public void run(IFCTournament __tournament) throws Exception {

	    IFCGameRecord[] games;
	    double starttime;
	
	    _random = new Random();
	    
	    _maxrounds = _config.numRounds();
	    init_energy= _config.InitEnergy();
	    X = _config.GridX();
	    Y = _config.GridY();
	    M = _config.M();
	    K = _config.K();
	    v = _config.v();
	    u = _config.u();
	    p = _config.p();
	    q = _config.q();
	    s = 1;

	    cells = new Cell[X][];
	    for(int i=0;i < X;i++)
	    {
		    cells[i] = new Cell[Y];
		    for(int j=0;j < Y;j++)
			    cells[i][j] = new Cell();
	    }
	    

	    games = __tournament.games();
	    if (games == null) {
		    throw new Exception("Error:  Null game record list");
	    }

	    starttime = System.currentTimeMillis()/60000.0;
	    for(int game=0;game < games.length;game++)
	    {
    	System.err.println("[Game "+ game +" of " + games.length + "]: Starting at " + (System.currentTimeMillis()/60000.0 - starttime) + " minutes from start");
	    population = 0;
		    _playerlist = games[game].players();
		    _numplayers = _playerlist.length;
		    if(X*Y < _numplayers)
			    throw new Exception("More players than Space on Grid");
		    for(int i=0;i < X;i++)
		    {
			    for(int j=0;j < Y;j++)
			    {
				    cells[i][j].foodvalue = 0;
				    cells[i][j].pw = null;
			    }
		    }
		    OrigPlayers = new PlayerEntry[_numplayers];
		    for(int i=0;i < _numplayers;i++)
		    {
			    int x, y;
			    while(true)
			    {
				    x = _random.nextInt(X);
				    y = _random.nextInt(Y);
				    if(cells[x][y].pw == null)
					    break;
			    }
			    population++;
			    cells[x][y].pw = new PlayerWrapper(_playerlist[i]);
			    cells[x][y].pw.register(this, -1);
			    cells[x][y].foodvalue = 0;;
			    cells[x][y].playertype = i;;
			    OrigPlayers[i] = new PlayerEntry(_playerlist[i], cells[x][y].pw.name(), cells[x][y].pw.color());
			    ChangeEnergy(x, y, init_energy);
		    }

		    _currRound = 0;
		    _state = _CMOVING;
		    
		    while (step()) { }
		    games[game].setScores(finalScores());
	    }
        
    } // end -- run

    double[] finalScores()
    {
	    double[] RET = new double[OrigPlayers.length];
	    for(int i=0;i < OrigPlayers.length;i++)
		    RET[i] = OrigPlayers[i].score();
	    return RET;
    }

    void create(IFCConfiguration __config) throws Exception
    {
    	System.out.println("==============================");
    	
	    _random = new Random();
	    _config = __config;

	    fightResults = new HashMap<Class, FightResults>();
	    
	    
	    
	    population = 0;
	    _maxrounds = _config.numRounds();
	    init_energy= _config.InitEnergy();
	    X = _config.GridX();
	    Y = _config.GridY();
	    M = _config.M();
	    K = _config.K();
	    v = _config.v();
	    u = _config.u();
	    p = _config.p();
	    q = _config.q();
	    s = 1;

	    _classlist = _config.classList();
	    _playerlist = _config.playerList();
	    _numplayers = _playerlist.length;
	    
	    if(X*Y < _numplayers)
		    throw new Exception("More players than Space on Grid");
	    cells = new Cell[X][];
	    for(int i=0;i < X;i++)
	    {
		    cells[i] = new Cell[Y];
		    for(int j=0;j < Y;j++)
			    cells[i][j] = new Cell();
	    }
	    //System.out.println("Initializing food: X=" + X + " Y=" + Y);
	    food = new int[X][];
	    for (int i = 0; i < X; i++) {
	    	food[i] = new int[Y];
	    	for (int j = 0; j < Y; j++)
	    		food[i][j] = 0;
	    }


	    OrigPlayers = new PlayerEntry[_numplayers];
	    for(int i=0;i < _numplayers;i++)
	    {
		    int x, y;
		    while(true)
		    {
			    x = _random.nextInt(X);
			    y = _random.nextInt(Y);
			    if(cells[x][y].pw == null)
				    break;
		    }
		    population++;
		    cells[x][y].pw = new PlayerWrapper(_playerlist[i]);
		    cells[x][y].pw.register(this, -1);
		    cells[x][y].foodvalue = 0;;
		    cells[x][y].playertype = i;;
		    OrigPlayers[i] = new PlayerEntry(_playerlist[i], cells[x][y].pw.name(), cells[x][y].pw.color());
		    ChangeEnergy(x, y, init_energy);
		    fightResults.put(OrigPlayers[i]._playerclass, new FightResults());
	    }

	    _currRound = 0;
	    _state = _CMOVING;
	    //_history = new ArrayList();
        
        _control = new ControlPanel();
        _view = new ViewPanel();
    }


    //********************************************
    //*
    //* Initial Configuration
    //*
    //********************************************
    public IFCConfiguration createDefaultConfiguration() throws Exception {
        IFCConfiguration RET = new Configuration();
        String[] toks;
        Class[] classes;
        Class[] players;
        int _MAX;
        Properties properties;
        Random random = new Random();
        ParseValue pv;

        RET.setNumRoundsBounds(_CMIN_ROUNDS, _CMAX_ROUNDS);
        RET.setNumPlayersBounds(_CMIN_PLAYERS, _CMAX_PLAYERS);
        RET.setXBounds(_CMIN_X, _CMAX_X);
        RET.setYBounds(_CMIN_Y, _CMAX_Y);
        RET.setvBounds(_CMIN_v, _CMAX_v);
        RET.setuBounds(_CMIN_u, _CMAX_u);
        RET.setMBounds(_CMIN_M, _CMAX_M);
        RET.setKBounds(_CMIN_K, _CMAX_K);

        properties = Util.gatherProperties(_CPROPERTIES_FILE);
        RET.setLogFile(properties.getProperty("LOG_FILE").trim());

        pv = ParseValue.parseIntegerValue(properties.getProperty("INIT_ENERGY").trim(), _CMIN_M, _CMAX_M);
        if (!pv.isValid()) {
            throw new Exception("Properties parameter out of range, Init Energy");
        }
        RET.setInitEnergy(((Integer) pv.value()).intValue());

        pv = ParseValue.parseIntegerValue(properties.getProperty("MAX_ROUNDS").trim(), _CMIN_ROUNDS, _CMAX_ROUNDS);
        if (!pv.isValid()) {
            throw new Exception("Properties parameter out of range, Number of Rounds");
        }
        RET.setNumRounds(((Integer) pv.value()).intValue());

        pv = ParseValue.parseIntegerValue(properties.getProperty("X").trim(), _CMIN_X, _CMAX_X);
        if (!pv.isValid()) {
            throw new Exception("Properties parameter out of range, X");
        }
        RET.setGridX(((Integer) pv.value()).intValue());

        pv = ParseValue.parseIntegerValue(properties.getProperty("Y").trim(), _CMIN_Y, _CMAX_Y);
        if (!pv.isValid()) {
            throw new Exception("Properties parameter out of range, Y");
        }
        RET.setGridY(((Integer) pv.value()).intValue());

        pv = ParseValue.parseIntegerValue(properties.getProperty("v").trim(), _CMIN_v, _CMAX_v);
        if (!pv.isValid()) {
            throw new Exception("Properties parameter out of range, v");
        }
        RET.setv(((Integer) pv.value()).intValue());

        pv = ParseValue.parseIntegerValue(properties.getProperty("u").trim(), _CMIN_u, _CMAX_u);
        if (!pv.isValid()) {
            throw new Exception("Properties parameter out of range, u");
        }
        RET.setu(((Integer) pv.value()).intValue());

        pv = ParseValue.parseIntegerValue(properties.getProperty("M").trim(), _CMIN_M, _CMAX_M);
        if (!pv.isValid()) {
            throw new Exception("Properties parameter out of range, M");
        }
        RET.setM(((Integer) pv.value()).intValue());

        pv = ParseValue.parseIntegerValue(properties.getProperty("K").trim(), _CMIN_K, _CMAX_K);
        if (!pv.isValid()) {
            throw new Exception("Properties parameter out of range, K");
        }
        RET.setK(((Integer) pv.value()).intValue());

        pv = ParseValue.parseDoubleValue(properties.getProperty("p").trim(), _CMIN_p, _CMAX_p);
        if (!pv.isValid()) {
            throw new Exception("Properties parameter out of range, p");
        }
        RET.setp(((Double) pv.value()).doubleValue());

        pv = ParseValue.parseDoubleValue(properties.getProperty("q").trim(), _CMIN_q, _CMAX_q);
        if (!pv.isValid()) {
            throw new Exception("Properties parameter out of range, q");
        }
        RET.setq(((Double) pv.value()).doubleValue());

        toks = Util.split(",\t\n ", properties.getProperty("CLASS_LIST").trim());
        _MAX = toks.length;
        classes = new Class[_MAX];
        for (int i=0; i < _MAX; i++) {
            classes[i] = Class.forName(toks[i]);
        }
        RET.setClassList(classes);

        toks = Util.split(",\t\n ", properties.getProperty("PLAYER_LIST").trim());
        _MAX = toks.length;
        if (_MAX < _CMIN_PLAYERS || _MAX > _CMAX_PLAYERS) {
            throw new Exception("Properties parameter out of range, Number of Players (Player List)");
        }
        players = new Class[_MAX];
        for (int i=0; i < _MAX; i++) {
            players[i] = Class.forName(toks[i]);
        }
        RET.setPlayerList(players);

        return RET;
    }


    private int numPlayers()
    {
	    return _numplayers;
    }

    private int currRound()
    {
	    return _currRound;
    }

    private int maxRounds()
    {
	    return _maxrounds;
    }

    //********************************************
    //*
    //* Exposed Methods
    //*
    //********************************************

    public int s()
    {
	    return s;
    }

    public int v()
    {
	    return v;
    }

    public int u()
    {
	    return u;
    }

    public int M()
    {
	    return M;
    }

    public int K()
    {
	    return K;
    }

    public void print(String __str) throws Exception {
        if (_registered) {
              _ui.print(__str);
        }
    }

    public void println(String __str) throws Exception {
        if (_registered) {
              _ui.println(__str);
        }
    }

    public void println() throws Exception {
        if (_registered) {
              _ui.println();
        }
    }

    //********************************************
    //*
    //* IFCModel
    //*
    //********************************************
    public void register(IFCUI __ui) throws Exception {
	roundList = new Vector();
        _ui = __ui;
        _ui.register(this);
        _registered = true;

//        create(createDefaultConfiguration());

        println("[Player Configuration]: ");
        for (int i=0; i < _numplayers; i++) {
            println("\t[Player" + i + "]: " + OrigPlayers[i].name());
        }
        refreshGame();
    }

    public String name() throws Exception {
        return _CNAME;
    }

    public JPanel exportControlPanel() throws Exception {
        return _control;
    }

    public JPanel exportViewPanel() throws Exception {
        return _view;
    }

    public JButton[] exportTools() throws Exception {
        return _control.exportTools();
    }
    
    public JMenu exportMenu() throws Exception {
        return null;
    }

    public IFCConfiguration exportConfiguration() throws Exception {
        return _config;
    }
    
    
    private void refreshGame() throws Exception {
        if (_registered) {
            _ui.refresh();
        }
    } 
    //********************************************
    //*
    //* Private Methods
    //*
    //********************************************


    private void resetGame() throws Exception {
        if (_registered) {
            _ui.reset();
        }
    }


    boolean WithProb(double p)
    {
	    double tmp = _random.nextDouble();
	    return ((tmp <= p) ? true : false );
    }

    int min(int a, int b)
    {
	    return (a < b)?a:b;
    }

    int max(int a, int b)
    {
	    return (a > b)?a:b;
    }

    void preProcessGrid()
    {
	    for(int x=0;x < X;x++)
	    {
		    for(int y=0;y < Y;y++)
		    {
			    cells[x][y].movedone = false;
	    			// Generate Food
			    if(cells[x][y].pw == null)
			    {
				    if(cells[x][y].foodvalue == 0)
				    { // Generate with prob p
					    cells[x][y].foodvalue = WithProb(p) ? 1 : 0;
				    }
				    else
				    { // Double with prob q
					    int newfood = 0;
					    for(int i=0;i < cells[x][y]. foodvalue;i++)
						    newfood += WithProb(q) ? 1 : 0;
					    cells[x][y].foodvalue = min(K, cells[x][y].foodvalue + newfood);
				    }
			    }
			    else
			    { // Feed the Organisms
				    if(cells[x][y].foodvalue > 0)
				    {
				    	int currE = cells[x][y].pw.energy();
				    	if(currE + u <= M)
					{
						ChangeEnergy(x, y, currE + u);
						cells[x][y].foodvalue--;
					}
				    }
			    }
		    }
	    }
    }

    boolean[] GetFoodState(int x, int y)
    {
	    boolean[] foodpresent = new boolean[DIRECTIONS.length];
	    for(int i=0;i < DIRECTIONS.length;i++)
	    {
		    int x1 = (X + x + _CXTrans[i]) % X;
		    int y1 = (Y + y + _CYTrans[i]) % Y;
		    foodpresent[i] = (cells[x1][y1].foodvalue == 0)?(false):(true);
	    }
	    return foodpresent;
    }

    int[] GetEnemyState(int x, int y)
    {
	    int[] enemypresent = new int[DIRECTIONS.length];
	    for(int i=0;i < DIRECTIONS.length;i++)
	    {
		    int x1 = (X + x + _CXTrans[i]) % X;
		    int y1 = (Y + y + _CYTrans[i]) % Y;
		    enemypresent[i] = (cells[x1][y1].pw == null)?(-1):(cells[x1][y1].pw.extState());
	    }
	    return enemypresent;
    }

    void ChangeEnergy(int x, int y, int finalE)
    {
	    int orig = cells[x][y].pw.energy();
	    OrigPlayers[ cells[x][y].playertype ].AddEnergy(finalE - orig);
	    cells[x][y].pw.setEnergy(finalE);
    }

    void KillAmoeba(int x, int y) throws Exception
    {
	    int origenergy = cells[x][y].pw.energy();
	    ChangeEnergy(x, y, 0);
	    OrigPlayers[ cells[x][y].playertype ].AddPop(-1);
	    population--;
	    cells[x][y].pw = null;
	    //println("\tKilling Amoeba at cell " + x + ", " + y);
    }

    void ShiftAmoeba(int x, int y, int x1, int y1)
    {
	    cells[x1][y1].playertype = cells[x][y].playertype;
	    cells[x1][y1].pw = cells[x][y].pw;
	    cells[x][y].pw = null;
    }

    void NewAmoeba(int x, int y, int ptype, int init_energy, int key) throws Exception
    {
	    cells[x][y].playertype = ptype;
	    cells[x][y].pw = new PlayerWrapper(OrigPlayers[ptype].playerclass());
	    cells[x][y].pw.register(this, key);
	    ChangeEnergy(x, y, init_energy);
	    OrigPlayers[ptype].AddPop(1);
	    population++;
    }

    void processMove(int x, int y, Move move) throws Exception
    {
	    int currE;
	    int x1, y1;
	    if(move == null)
		    move = new Move(STAYPUT);
	    switch(move.type())
	    {
		    case STAYPUT:
			    currE = cells[x][y].pw.energy();
			    if(currE - s <= 0)
				    KillAmoeba(x, y);
			    else
			    {
			    	ChangeEnergy(x, y, currE - s);
				cells[x][y].movedone = true;
			    }
			    break;
		    case WEST:
		    case EAST:
		    case NORTH:
		    case SOUTH:
			    x1 = (X + x + _CXTrans[move.type()]) % X;
			    y1 = (Y + y + _CYTrans[move.type()]) % Y;
			    if(cells[x1][y1].pw != null) // Cell is not Empty
			    {
			    	
			    	if (fightingAllowed) {
				    	/*
				    	 * If a player attempts to move onto a cell occupied by another player... it's on!
				    	 */
				    	System.out.println("FIGHT!");
				    	
				    	// check that they're not from the same species?
				    	PlayerWrapper attacker = cells[x][y].pw;
				    	PlayerWrapper defender = cells[x1][y1].pw;
				    	
				    	Class attackerClass = attacker._class;
				    	Class defenderClass = defender._class;
				    	//System.out.println("attacker=" + attackerClass + "; defender=" + defenderClass);
				    	if (attackerClass.equals(defenderClass)) {
				    		//System.out.println("Cannibalism!");
				    		move = new Move(STAYPUT);
				    		processMove(x, y, move);
				    		break;
				    	}
				    	
				    	// get the energy level for each player
					    int attackerEnergy = attacker.energy();
					    int defenderEnergy = defender.energy();
				    	//System.out.println("attacker=" + attackerEnergy + "; defender=" + defenderEnergy);
				    	
				    	// determine winner and loser
				    	int totalEnergy = attackerEnergy + defenderEnergy;
				    	
				    	int random = (int)(Math.random() * totalEnergy);
				    	//System.out.println("random=" + random);
				    				    	
				    	if (random < attackerEnergy) {
				    		// attacker wins
					    	//System.out.println("attacker=" + attackerClass + " defeated defender=" + defenderClass);
					    	//System.out.println("attacker wins");
				    		FightResults fr = fightResults.get(attackerClass);
				    		fr.attackWin++;
				    		fr = fightResults.get(defenderClass);
				    		fr.defendLoss++;
				    		KillAmoeba(x1,y1);
				    		cells[x1][y1].pw = null;
				    		processMove(x,y,move); // now when we do the move, the spot will be empty
				    	}
				    	else {
				    		// defender wins
					    	//System.out.println("defender=" + defenderClass + " defeated attacker=" + attackerClass);
					    	//System.out.println("defender wins");
				    		FightResults fr = fightResults.get(defenderClass);
				    		fr.defendWin++;
				    		fr = fightResults.get(attackerClass);
				    		fr.attackLoss++;
				    		KillAmoeba(x,y);
				    	}
			    	}
			    	else
			    	{
			    		move = new Move(STAYPUT);
			    		processMove(x, y, move);
			    	}
			    }
			    else
			    {
				    currE = cells[x][y].pw.energy();
				    if(currE - v <= 0)
					    KillAmoeba(x, y);
				    else
				    {
					    ChangeEnergy(x, y, currE - v);
					    ShiftAmoeba(x, y, x1, y1);
					    cells[x1][y1].movedone = true;
				    }
			    }
			    break;
		    case REPRODUCE:
			    currE = cells[x][y].pw.energy();
			    if(currE - v <= 1)
				    KillAmoeba(x, y);
			    else
			    {
				    switch(move.childpos())
				    {
					    case WEST:
					    case EAST:
					    case NORTH:
					    case SOUTH:
						    x1 = (X + x + _CXTrans[move.childpos()]) % X;
						    y1 = (Y + y + _CYTrans[move.childpos()]) % Y;
						    if(cells[x1][y1].pw != null)
						    {
						    	move.setType(STAYPUT);
						    	processMove(x, y, move);
						    }
						    else
						    {
							    currE -= v;
							    ChangeEnergy(x, y, currE/2);
							    NewAmoeba(x1, y1, cells[x][y].playertype, currE - (currE / 2), move.key());
							    cells[x][y].movedone = true;
							    cells[x1][y1].movedone = true;
						    }
						    break;
					    default:
						    move.setType(STAYPUT);
						    processMove(x, y, move);
						    break;
				    }
			    }
			    break;
		    default:
			    move.setType(STAYPUT);
			    processMove(x, y, move);
			    break;
	    }
    }

    //********************************************
    //*
    //* State Transition
    //*
    //********************************************

    private boolean step() throws Exception {

        StringBuffer SB = new StringBuffer();
	Move _move = null;
	int extstate = 0;

	// ------------------------------------------------------
	// keep track of board state this round
//	System.out.println("step called; round="+_currRound);
	{
	    int totalFoodAvailable = 0;

	    // cound up food
	    for (int i = 0; i < _config.GridX(); i++)
	    {
		for (int j = 0; j < _config.GridY(); j++)
		{
		    totalFoodAvailable+=cells[i][j].foodvalue;
		}
	    }

	    Round round = new Round(OrigPlayers.length,
				    _currRound,
				    totalFoodAvailable*u);

	    for (int i = 0; i < OrigPlayers.length; i++)
	    {
		round.addPlayerData(i,
				    OrigPlayers[i]._totalenergy,
				    OrigPlayers[i]._population);
	    }
	    roundList.addElement(round); // add to our round list
	}

//	System.out.println("round history:");
// 	for (int i = 0; i < roundList.size(); i++)
// 	{
// 	    Round round = (Round)roundList.elementAt(i);
// 	    System.out.println("round: "+ round.number+
// 		", food energy available: "+round.foodEnergyAvailable);
	    
// 	    for (int j = 0; j < OrigPlayers.length; j++)
// 	    {
// 		System.out.println("player: "+j+", name="+
// 				   OrigPlayers[j]._name+
// 				   ", population="+
// 				   round.players[j].count+
// 				   ", energy="+
// 				   round.players[j].energy);
// 	    }
// 	}
	/*
	// update the int array with all the food
	for (int i = 0; i < _config.GridX(); i++) {
		for (int j = 0; j < _config.GridY(); j++) {
			food[i][j] = cells[i][j].foodvalue;
			//System.out.print(food[i][j]+"-");
		}
		//System.out.println();
	}
	*/

	
	if (shouldGraph && graph!=null)
	    graph.repaint();

        switch (_state) {
            case _CWAITING: {
                println("Please Make A Move, ");
                return false;
            }

            case _CMOVING:  {
				    if(population <= 0)
				    {
					    _state = _CFINISHED;
					    break;
				    }
				    preProcessGrid();
//				    println("------ Round " + _currRound + " ------");
				    for(int y=0;y < Y;y++)
				    {
					    for(int x=0;x < X;x++)
					    {
						    if(cells[x][y].pw == null)
							    continue;
						    if(cells[x][y].movedone)
							    continue;
						    if(!cells[x][y].pw.interactive())
						    {
							    _move = cells[x][y].pw.move(GetFoodState(x, y), GetEnemyState(x, y), cells[x][y].foodvalue, 0);
							    extstate = cells[x][y].pw.externalState();
							    processMove(x, y, _move);
						    }
						    else
						    { // Interactive Player
						    }
						    /*
						    SB = new StringBuffer();
						    SB.append("Player " + cells[x][y].playertype + " at[" + (x+1) + ", " + (y+1));
						    SB.append("]: ");
						    if(_move != null)
							    SB.append(_move.toString());
						    println(new String(SB));
						    */
					    }
				    }
				    
				    
				    for (int i = 0; i < OrigPlayers.length; i++)
				    {
						if (OrigPlayers[i]._population == 0 && OrigPlayers[i].isExtinct == false) {
							System.out.println(OrigPlayers[i]._playerclass + " extinct at round " + _currRound);
							OrigPlayers[i].isExtinct = true;
						}
				    }

		_currRound++;

		if(_currRound >= maxRounds())
		    _state = _CFINISHED;
                break;
            }

        }
        
        
        
        
        /*
        // print out the fight results
        System.out.println("---------------------------------------");
        for (PlayerEntry pe : OrigPlayers) {
        	Class c = pe._playerclass;
        	FightResults fr = fightResults.get(c);
        	System.out.println(c.getName() + ": Attacking: " + fr.attackWin + "-" + fr.attackLoss + "; Defending: " + fr.defendWin + "-" + fr.defendLoss);
        }
        */
        
        return (_state != _CFINISHED);
    }
    
void printBoard()
{
	System.out.println("######################### " + currRound() + " ################################");
	for(int j=0;j < Y;j++)
	{
		for(int i=0;i < X;i++)
		{
			System.out.print(cells[i][j].pw + ", ");
		}
		System.out.println();
	}
}

    //********************************************
    //*
    //* View Panel
    //*
    //********************************************
    private final class ViewPanel extends JPanel implements Serializable {
        final int           _CWIDTH                         = 600;
        final int           _CHEIGHT                        = 600;
	final int	    _MARGIN			    = 30;
        double              _ratio;
        final Font          _CVIEW_FONT                     = new Font("Courier", Font.BOLD, 35);
        final Font          _CAMOEBA_FONT                   = new Font("Courier", Font.BOLD, (max(X, Y) >= 50) ? (max(X, Y) >= 60) ? 8 : 9 : 10);
        final int           _CHOFFSET                       = _CAMOEBA_FONT.getSize() / 3;
        final int           _CVOFFSET                       = _CAMOEBA_FONT.getSize();
        final Color         _CBLACK                         = new Color(0.0f, 0.0f, 0.0f);
        final Color         _CBACKGROUND_COLOR              = new Color(1.0f, 1.0f, 1.0f);
        final int           _COUTLINE_THICKNESS             = 2;

        //********************************************
        //*
        //* Constructor
        //*
        //********************************************            
        public ViewPanel() throws Exception {
            super();                                        
            setLayout(new BorderLayout());
            setBackground(_CBACKGROUND_COLOR);
            setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
                                                         BorderFactory.createLoweredBevelBorder()));
            setPreferredSize(new Dimension(_CWIDTH, _CHEIGHT));
            setMinimumSize(new Dimension(_CWIDTH, _CHEIGHT));
            setFont(_CVIEW_FONT);
        }       
        

        //********************************************
        //*
        //* paint() Override
        //*
        //********************************************            
        public void paintComponent(Graphics __g)
	{
            try {
                super.paintComponent(__g);
                
                int width = getWidth() - _MARGIN;
                int height = getHeight() - _MARGIN;
                _ratio = (width < height ? (double) width / (double) max(X, Y) : (double) height / (double) max(X, Y));
                __g.setFont(_CAMOEBA_FONT);
                
		__g.drawLine(_MARGIN, _MARGIN,_MARGIN, _MARGIN + (int) (Y * _ratio));
		__g.drawLine(_MARGIN, _MARGIN, _MARGIN + (int) (X * _ratio), _MARGIN);
//				printBoard();
		for(int x=0;x < X;x++)
		{
			__g.drawLine(_MARGIN + (int) ((x+1) * _ratio), _MARGIN,_MARGIN + (int) ((x+1) * _ratio), _MARGIN + (int) (Y * _ratio));
			__g.drawString(Integer.toString(x+1), _MARGIN + (int) (x * _ratio) + _CHOFFSET, _CVOFFSET);
			for(int y=0;y < Y;y++)
			{
				if(x == 0)
				{
					__g.drawLine(_MARGIN, _MARGIN + (int) ((y+1) * _ratio), _MARGIN + (int) (X * _ratio), _MARGIN + (int) ((y+1) * _ratio));
					__g.drawString(Integer.toString(y+1), _CHOFFSET, _MARGIN + (int) (y * _ratio) + _CVOFFSET);
				}
				if(cells[x][y].pw == null)
				{
					if(cells[x][y].foodvalue > 0)
						__g.drawString(Integer.toString(cells[x][y].foodvalue), _MARGIN + (int) (x * _ratio) + 2*_CHOFFSET, _MARGIN + (int) (y * _ratio) + 2*_CVOFFSET);
					continue;
				}
				else
				{
					__g.setColor(_CBLACK);
					__g.fillRect(_MARGIN + (int) (x * _ratio), _MARGIN + (int) (y * _ratio), (int) _ratio, (int) _ratio);

					try {
					//__g.setColor(OrigPlayers[cells[x][y].playertype].color());
					if(cells[x][y].pw != null)
						__g.setColor(cells[x][y].pw.color());
					__g.fillRect(_MARGIN + (int) (x * _ratio) + _COUTLINE_THICKNESS, _MARGIN + (int) (y * _ratio) + _COUTLINE_THICKNESS, (int) _ratio - _COUTLINE_THICKNESS * 2, (int) _ratio - _COUTLINE_THICKNESS * 2);
				
						__g.setColor(_CBLACK);
						if(cells[x][y].pw != null)
							__g.drawString("s" + Integer.toString(cells[x][y].pw.extState()), _MARGIN + (int) (x * _ratio) + _CHOFFSET, _MARGIN + (int) (y * _ratio) + _CVOFFSET);
						if(cells[x][y].pw != null)
							__g.drawString("e" + Integer.toString(cells[x][y].pw.energy()), _MARGIN + (int) (x * _ratio) + _CHOFFSET, _MARGIN + (int) (y * _ratio) + 2*_CVOFFSET);
					} catch(Exception EXC) {
							//System.err.println("Exception in 4");
							//System.err.println(x + ", " + y + ",pw = " + cells[x][y].pw + ", round = " + currRound());
//							printBoard();
					}
				}
			}
		}
		
            } catch (Exception EXC) {
                EXC.printStackTrace();
            }
	} // End - paintComponent
    } // End - ViewPanel Class
    
    //********************************************
    //*
    //* Control Panel
    //*
    //********************************************
    private final class ControlPanel extends JPanel implements ActionListener, ItemListener, Serializable {
        JTabbedPane  _tab;
        JPanel       _conf;
        JPanel       _info;
        final        int         _CWIDTH = 300;
        final        int         _CHEIGHT = 350;
        final        int         _CPANEL_WIDTH = _CWIDTH;
        final        int         _CPANEL_HEIGHT = 21;
        final        int         _CPLAYER_NAME_LENGTH = 20;
        final        ImageIcon   _CSTEP_ICON  = new ImageIcon("Images/marble_step.gif");
        final        ImageIcon   _C100STEPS_ICON  = new ImageIcon("Images/marble_step.gif");
        final        ImageIcon   _CSTOP_ICON  = new ImageIcon("Images/marble_stop.gif");
        final        ImageIcon   _CGRAPH_ICON  = new ImageIcon("Images/marble_graph.gif");
        final        ImageIcon   _CPLAY_ICON  = new ImageIcon("Images/marble_play.gif");
        final        ImageIcon   _CRESET_ICON = new ImageIcon("Images/marble_reset.gif");
        final        Color       _CDISABLED_FIELD_COLOR = new Color(1.0f, 1.0f, 1.0f);
        final Font   _CCONTROL_FONT  = new Font("Courier", Font.BOLD, 12);
        final Font   _CCOMBO_FONT = new Font("Courier", Font.BOLD, 10);

        JTextField   _currRoundfield;
        JTextField[] _scores;
        JTextField   _numplayersfield;
        JComboBox[]  _classes;
        JPanel       _infobox;
        JPanel       _confbox;
        JButton      _play;
        JButton      _step;
        JButton		 _100steps;
        JButton      _reset;
        JButton      _playTrials;
	JButton      _stop;
	JButton      _graph;

        JTextField   _maxroundsfield;
        JTextField   _initenergyfield;
        JTextField   _Xfield;
        JTextField   _Yfield;
        JTextField   _pfield;
        JTextField   _qfield;
        JTextField   _vfield;
        JTextField   _ufield;
        JTextField   _Mfield;
        JTextField   _Kfield;

        NumberFormat _nf;

        //********************************************
        //*
        //* Constructor
        //*
        //********************************************
        public ControlPanel() throws Exception {
            super();

            SlotPanel       slot;
            JPanel          box;
            JLabel          label;
            int             _MAX;
            StringBuffer    SB;
            String          name;

            setLayout(new BorderLayout());
            setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
                                                         BorderFactory.createLoweredBevelBorder()));
            setPreferredSize(new Dimension(_CWIDTH, _CHEIGHT));
            setMinimumSize(new Dimension(_CWIDTH, _CHEIGHT));
            setFont(_CCONTROL_FONT);

            _info = new JPanel();
            _info.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
                                                         BorderFactory.createLoweredBevelBorder()));
            _info.setLayout(new BorderLayout());
            _info.setPreferredSize(new Dimension(_CWIDTH, _CHEIGHT));
            _info.setMinimumSize(new Dimension(_CWIDTH, _CHEIGHT));
            _info.setFont(_CCONTROL_FONT);

            //_reset = new JButton(_CRESET_ICON);
            _reset = new JButton("Reset");
            _reset.addActionListener(this);
            
            _playTrials = new JButton("Play Trials");
            _playTrials.addActionListener(this);
            
            //_step = new JButton(_CSTEP_ICON);
            _step = new JButton("Step");
            _step.addActionListener(this);
            _100steps = new JButton("100 steps");//(_C100STEPS_ICON);
            _100steps.addActionListener(this);
            //_play = new JButton(_CPLAY_ICON);
            _play = new JButton("Play");
            _play.addActionListener(this);

	    //_stop = new JButton(_CSTOP_ICON);
	    _stop = new JButton("Stop");
	    _stop.addActionListener(this);

	    //_graph = new JButton(_CGRAPH_ICON);
	    _graph = new JButton("Graph");
	    _graph.addActionListener(this);

            box = new JPanel();
            box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
            slot = new SlotPanel(_CPANEL_WIDTH, _CPANEL_HEIGHT);
            _input = new JTextField();
            _input.setFont(_CCONTROL_FONT);
            _input.addActionListener(this);
            label = new JLabel("Input:              ");
            label.setFont(_CCONTROL_FONT);
            slot.add(label, _input);
            box.add(slot);

            slot = new SlotPanel(_CPANEL_WIDTH, _CPANEL_HEIGHT);
            _currRoundfield = new JTextField();
            _currRoundfield.setEditable(false);
            _currRoundfield.setFont(_CCONTROL_FONT);
            label = new JLabel("Round No.:          ");
            label.setFont(_CCONTROL_FONT);
            slot.add(label, _currRoundfield);
            box.add(slot);

            _MAX = numPlayers();
            _scores = new JTextField[_MAX];
            for (int i=0; i < _MAX; i++) {
                _scores[i] = new JTextField();
                _scores[i].setEditable(false);
                _scores[i].setFont(_CCONTROL_FONT);
                slot = new SlotPanel(_CPANEL_WIDTH, _CPANEL_HEIGHT);
                label = new JLabel(Util.adjustString(OrigPlayers[i].name(), _CPLAYER_NAME_LENGTH));
                label.setForeground(OrigPlayers[i].color());
                label.setFont(_CCONTROL_FONT);
                slot.add(label, _scores[i]);
                box.add(slot);
            }
	    
            _info.add(box, BorderLayout.CENTER);
            _infobox = box;

            _conf = new JPanel();
            _conf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
                                                         BorderFactory.createLoweredBevelBorder()));
            _conf.setLayout(new BorderLayout());
            _conf.setPreferredSize(new Dimension(_CWIDTH, _CHEIGHT));
            _conf.setMinimumSize(new Dimension(_CWIDTH, _CHEIGHT));
            _conf.setFont(_CCONTROL_FONT);
            
            box = new JPanel();
            box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));            

            slot = new SlotPanel(_CPANEL_WIDTH, _CPANEL_HEIGHT);
            _maxroundsfield = new JTextField();
            _maxroundsfield.setFont(_CCONTROL_FONT);
            _maxroundsfield.setText(Integer.toString(_config.numRounds()));
            _maxroundsfield.addActionListener(this);
            label = new JLabel("maxRounds:      ");
            label.setFont(_CCONTROL_FONT);
            slot.add(label, _maxroundsfield);
            box.add(slot);
            
            slot = new SlotPanel(_CPANEL_WIDTH, _CPANEL_HEIGHT);
            _initenergyfield = new JTextField();
            _initenergyfield.setFont(_CCONTROL_FONT);
            _initenergyfield.setText(Integer.toString(_config.InitEnergy()));
            _initenergyfield.addActionListener(this);
            label = new JLabel("Initial Energy: ");
            label.setFont(_CCONTROL_FONT);
            slot.add(label, _initenergyfield);
            box.add(slot);
            
            slot = new SlotPanel(_CPANEL_WIDTH, _CPANEL_HEIGHT);
            _Xfield = new JTextField();
            _Xfield.setFont(_CCONTROL_FONT);
            _Xfield.setText(Integer.toString(_config.GridX()));
            _Xfield.addActionListener(this);
            label = new JLabel("Grid X:         ");
            label.setFont(_CCONTROL_FONT);
            slot.add(label, _Xfield);
            box.add(slot);
            
            slot = new SlotPanel(_CPANEL_WIDTH, _CPANEL_HEIGHT);
            _Yfield = new JTextField();
            _Yfield.setFont(_CCONTROL_FONT);
            _Yfield.setText(Integer.toString(_config.GridY()));
            _Yfield.addActionListener(this);
            label = new JLabel("Grid Y:         ");
            label.setFont(_CCONTROL_FONT);
            slot.add(label, _Yfield);
            box.add(slot);
            
            slot = new SlotPanel(_CPANEL_WIDTH, _CPANEL_HEIGHT);
            _pfield = new JTextField();
            _pfield.setFont(_CCONTROL_FONT);
            _pfield.setText(Double.toString(_config.p()));
            _pfield.addActionListener(this);
            label = new JLabel("p:              ");
            label.setFont(_CCONTROL_FONT);
            slot.add(label, _pfield);
            box.add(slot);
            
            slot = new SlotPanel(_CPANEL_WIDTH, _CPANEL_HEIGHT);
            _qfield = new JTextField();
            _qfield.setFont(_CCONTROL_FONT);
            _qfield.setText(Double.toString(_config.q()));
            _qfield.addActionListener(this);
            label = new JLabel("q:              ");
            label.setFont(_CCONTROL_FONT);
            slot.add(label, _qfield);
            box.add(slot);
            
            slot = new SlotPanel(_CPANEL_WIDTH, _CPANEL_HEIGHT);
            _vfield = new JTextField();
            _vfield.setFont(_CCONTROL_FONT);
            _vfield.setText(Integer.toString(_config.v()));
            _vfield.addActionListener(this);
            label = new JLabel("v:              ");
            label.setFont(_CCONTROL_FONT);
            slot.add(label, _vfield);
            box.add(slot);
            
            slot = new SlotPanel(_CPANEL_WIDTH, _CPANEL_HEIGHT);
            _ufield = new JTextField();
            _ufield.setFont(_CCONTROL_FONT);
            _ufield.setText(Integer.toString(_config.u()));
            _ufield.addActionListener(this);
            label = new JLabel("u:              ");
            label.setFont(_CCONTROL_FONT);
            slot.add(label, _ufield);
            box.add(slot);
            
            slot = new SlotPanel(_CPANEL_WIDTH, _CPANEL_HEIGHT);
            _Mfield = new JTextField();
            _Mfield.setFont(_CCONTROL_FONT);
            _Mfield.setText(Integer.toString(_config.M()));
            _Mfield.addActionListener(this);
            label = new JLabel("M:              ");
            label.setFont(_CCONTROL_FONT);
            slot.add(label, _Mfield);
            box.add(slot);
            
            slot = new SlotPanel(_CPANEL_WIDTH, _CPANEL_HEIGHT);
            _Kfield = new JTextField();
            _Kfield.setFont(_CCONTROL_FONT);
            _Kfield.setText(Integer.toString(_config.K()));
            _Kfield.addActionListener(this);
            label = new JLabel("K:              ");
            label.setFont(_CCONTROL_FONT);
            slot.add(label, _Kfield);
            box.add(slot);
            
            slot = new SlotPanel(_CPANEL_WIDTH, _CPANEL_HEIGHT);
            _numplayersfield = new JTextField();
            _numplayersfield.setFont(_CCONTROL_FONT);
            _numplayersfield.setText(Integer.toString(_MAX));
            _numplayersfield.addActionListener(this);
            label = new JLabel("NumPlayers:     ");
            label.setFont(_CCONTROL_FONT);
            slot.add(label, _numplayersfield);
            box.add(slot);
            
            _classes = new JComboBox[_MAX];
            for (int i=0; i < _MAX; i++) {               
                _classes[i] = new JComboBox(_classlist);
                _classes[i].setSelectedItem(OrigPlayers[i].playerclass());
                _classes[i].addItemListener(this);
                _classes[i].setFont(_CCOMBO_FONT);
                slot = new SlotPanel(_CPANEL_WIDTH, _CPANEL_HEIGHT);
                label = new JLabel("["+i+"]:  ");
                label.setFont(_CCONTROL_FONT);
                slot.add(label, _classes[i]);
                box.add(slot);                                    
            }                        

            _conf.add(box, BorderLayout.CENTER);
            _confbox = box;

            _tab = new JTabbedPane();
            _tab.add("Information", _info);                  
            _tab.add("Configuration", _conf);
            add(_tab, BorderLayout.CENTER);            
            
            _nf = NumberFormat.getInstance();
            _nf.setMinimumFractionDigits(2);
            _nf.setMaximumFractionDigits(2);

        }        

        //********************************************
        //*
        //* ActionListener Interface
        //*
        //********************************************
        public void actionPerformed(ActionEvent __event) {
            Object source = __event.getSource();
            ParseValue pv = null;
            JComboBox[] tmp;
            Class[] tmpcls;
            char[] moves;
            int prev;
            int curr;
            int _MAX;
            SlotPanel slot;
            JLabel label;
            double[] scores;

            try {
            	if (source == _100steps) {
            		
                    for (int i = 0; i < 100; i++) step();
                    this.refreshControlPanel();
                    OrganismsGame.this.refreshGame();
                    return;
            	}
            	
                if (source == _step) {
                    step();
                    this.refreshControlPanel();
                    OrganismsGame.this.refreshGame();
                    return;
                }
                if (source == _play) {
                	new StopListener(this).start();
                    return;
                }
                if (source == _playTrials) {
                	String strTrials = (String) JOptionPane.showInputDialog("Enter the number of trials");
                	int trials;
                	try {
                		trials = Integer.parseInt(strTrials);
                	} catch(NumberFormatException ex) {
                		return;
                	}
                	new StopListener2(this, trials).start();
                    return;
                }
                if (source == _reset) {
                    OrganismsGame.this.resetGame();
                    return;
                }
		if (source == _stop) {
		    _state = _CFINISHED;
		}

		if (source == _graph) {
		    shouldGraph = true;
		    Color playerColors[] = new Color[OrigPlayers.length];
		    for (int i = 0; i < playerColors.length; i++)
		    {
			playerColors[i] = OrigPlayers[i]._color;
		    }
		    graph = new GraphDrawer(roundList, playerColors);
		}


                if (source == _input) {
                    if (_state == _CFINISHED) {
                        return;
                    }
		    //_moves[_playerindex] = parseMove(_input.getText());
                    return;
                }
                if (source == _maxroundsfield) {
                    pv = ParseValue.parseIntegerValue(_maxroundsfield.getText(), _CMIN_ROUNDS, _CMAX_ROUNDS);
                    if (pv.isValid()) {
                        _config.setNumRounds(((Integer) pv.value()).intValue());
                        _ui.configure(_config);
                    } else {
                        println("Invalid Input");
                    }
                }
                if (source == _initenergyfield) {
                    pv = ParseValue.parseIntegerValue(_initenergyfield.getText(), _CMIN_M, _CMAX_M);
                    if (pv.isValid()) {
                        _config.setInitEnergy(((Integer) pv.value()).intValue());
                        _ui.configure(_config);
                    } else {
                        println("Invalid Input");
                    }
                }
                if (source == _Xfield) {
                    pv = ParseValue.parseIntegerValue(_Xfield.getText(), _CMIN_X, _CMAX_X);
                    if (pv.isValid()) {
                        _config.setGridX(((Integer) pv.value()).intValue());
                        _ui.configure(_config);
                    } else {
                        println("Invalid Input");
                    }
                }
                if (source == _Yfield) {
                    pv = ParseValue.parseIntegerValue(_Yfield.getText(), _CMIN_Y, _CMAX_Y);
                    if (pv.isValid()) {
                        _config.setGridY(((Integer) pv.value()).intValue());
                        _ui.configure(_config);
                    } else {
                        println("Invalid Input");
                    }
                }
                if (source == _pfield) {
                    pv = ParseValue.parseDoubleValue(_pfield.getText(), _CMIN_p, _CMAX_p);
                    if (pv.isValid()) {
                        _config.setp(((Double) pv.value()).doubleValue());
                        _ui.configure(_config);
                    } else {
                        println("Invalid Input");
                    }
                }
                if (source == _qfield) {
                    pv = ParseValue.parseDoubleValue(_qfield.getText(), _CMIN_q, _CMAX_q);
                    if (pv.isValid()) {
                        _config.setq(((Double) pv.value()).doubleValue());
                        _ui.configure(_config);
                    } else {
                        println("Invalid Input");
                    }
                }
                if (source == _vfield) {
                    pv = ParseValue.parseIntegerValue(_vfield.getText(), _CMIN_v, _CMAX_v);
                    if (pv.isValid()) {
                        _config.setv(((Integer) pv.value()).intValue());
                        _ui.configure(_config);
                    } else {
                        println("Invalid Input");
                    }
                }
                if (source == _ufield) {
                    pv = ParseValue.parseIntegerValue(_ufield.getText(), _CMIN_u, _CMAX_u);
                    if (pv.isValid()) {
                        _config.setu(((Integer) pv.value()).intValue());
                        _ui.configure(_config);
                    } else {
                        println("Invalid Input");
                    }
                }
                if (source == _Mfield) {
                    pv = ParseValue.parseIntegerValue(_Mfield.getText(), _CMIN_M, _CMAX_M);
                    if (pv.isValid()) {
                        _config.setM(((Integer) pv.value()).intValue());
                        _ui.configure(_config);
                    } else {
                        println("Invalid Input");
                    }
                }
                if (source == _Kfield) {
                    pv = ParseValue.parseIntegerValue(_Kfield.getText(), _CMIN_K, _CMAX_K);
                    if (pv.isValid()) {
                        _config.setK(((Integer) pv.value()).intValue());
                        _ui.configure(_config);
                    } else {
                        println("Invalid Input");
                    }
                }
                if (source == _numplayersfield) {
                    pv = ParseValue.parseIntegerValue(_numplayersfield.getText(), _CMIN_PLAYERS, _CMAX_PLAYERS);
                    if (pv.isValid()) {
                        prev = _config.numPlayers();
                        curr = ((Integer) pv.value()).intValue();
                        if (prev == curr) {
                            return;
                        }
                        if (curr > prev) {
                            tmp = _classes;
                            _classes = new JComboBox[curr];
                            System.arraycopy(tmp, 0, _classes, 0, prev);
                            tmpcls = new Class[curr];
                            System.arraycopy(_config.playerList(), 0, tmpcls, 0, prev);
                            for (int i=prev; i < curr; i++) {
                                _classes[i] = new JComboBox(_classlist);
                                _classes[i].addItemListener(this);
                                _classes[i].setFont(_CCOMBO_FONT);
                                slot = new SlotPanel(_CPANEL_WIDTH, _CPANEL_HEIGHT);
                                label = new JLabel("["+i+"]:  ");
                                label.setFont(_CCONTROL_FONT);
                                slot.add(label, _classes[i]);
                                _confbox.add(slot);
                                tmpcls[i] = (Class) _classes[i].getSelectedItem();
                            }
                            _config.setPlayerList(tmpcls);
                        }
                        if (curr < prev) {
                            tmp = new JComboBox[curr];
                            System.arraycopy(_classes, 0, tmp, 0, curr);
                            tmpcls = new Class[curr];
                            System.arraycopy(_config.playerList(), 0, tmpcls, 0,  curr);
                            for (int i=curr; i < prev; i++) {
                                _confbox.remove(_confbox.getComponents().length - 1);
                            }
                            _classes = tmp;
                            _config.setPlayerList(tmpcls);
                        }
                        _ui.configure(_config);
                        repaint();
                        OrganismsGame.this.refreshGame();
                    } else {
                        println("Invalid Input");
                    }
                }                                                                                
            } catch (Exception EXC) {
                System.out.println(EXC.getMessage());
                EXC.printStackTrace();
            }
        }        
        
        //********************************************
        //*
        //* ItemListener Interface
        //*
        //********************************************            
        public void itemStateChanged(ItemEvent __event) {
            Object source = __event.getSource();
            int _MAX = _classes.length;
            
            try {
                for (int i=0; i < _MAX; i++) {
                    if (source == _classes[i]) {
                        _config.setPlayer(i, (Class) _classes[i].getSelectedItem());
                        _ui.configure(_config);
                    }                                
                }                        
            } catch (Exception EXC) {
                System.out.println(EXC.getMessage());
                EXC.printStackTrace();
            }
        }
         
        //********************************************
        //*
        //* Score Updater
        //*
        //********************************************
        public void refreshControlPanel() throws Exception {
            int _MAX = numPlayers();

            for (int i=0; i < _MAX; i++) {
		    int pop = OrigPlayers[i].population();
		    int totalE = OrigPlayers[i].totalenergy();
                _scores[i].setText(Integer.toString(pop) + ", totalE " + Integer.toString(totalE));
                //_scores[i].setText(_players[i].result());
            }
            _currRoundfield.setText(Integer.toString(currRound()));
            
            this.repaint();
        }

        
        //********************************************
        //*
        //* Action Tool Exporter
        //*
        //********************************************
        public JButton[] exportTools() {
	    /* amg2006 changed to 4 */
	    // djv changed to 5
            JButton[] ret = new JButton[7];
            ret[0] = _reset;
            ret[1] = _step;
            ret[2] = _100steps;
            ret[3] = _play;
		    ret[4] = _stop;
		    ret[5] = _graph;
		    ret[6] = _playTrials;
            return ret;
        }
    }
    class StopListener extends Thread
    {
	private ControlPanel controlPanel;	
	public StopListener(ControlPanel cp) { 
	    controlPanel=cp;
	} 
	public void run ()
	{
		try
		{
		    while (GUI._amoeba.step()) {
			GUI._amoeba.refreshGame();
			controlPanel.refreshControlPanel();
		    }		
			GUI._amoeba.refreshGame();
			controlPanel.refreshControlPanel();
		}
		catch (Exception e)
		{
		    System.out.println("unexpected exception caught in run");
	                e.printStackTrace();
		}
	}
    }

    class StopListener2 extends Thread
    {
    	private int trials = 10;
    	private int gameIndex = 1;
	private ControlPanel controlPanel;	
	private ArrayList<Round> lastRounds;
	public StopListener2(ControlPanel cp) {
		this(cp, 10);
	}
	public StopListener2(ControlPanel cp, int trials) { 
	    controlPanel=cp;
	    lastRounds = new ArrayList<Round>();
	    this.trials = trials;
	}
	private void printInfo(int gameIndex, int trials) {
		int _MAX = numPlayers();

		PlayerRoundData[] data = getLastRound().players;
		// output the results
        for (int i=0; i < _MAX; i++) {
		    int pop = data[i].count;
		    int totalE = data[i].energy;
		    String message = String.format("GAME (%d/%d) Player: %d Population: %d Energy: %d", gameIndex, trials, i, pop, totalE);
            try { getGame().println(message); } catch(Exception e) {}
        }
    }
	private void trackLastRound() {
		lastRounds.add(getLastRound());
	}
	
	private Round getLastRound() {
		return (Round) getGame().roundList.lastElement();
	}
	private OrganismsGame getGame() {
		return GUI._amoeba;
	}
	private void showSummary() {
		int _MAX = numPlayers();
		
		PlayerRoundData[] cumulativeData = new PlayerRoundData[_MAX];
		for(Round round: lastRounds) {
			PlayerRoundData[] data = round.players;
			// output the results
	        for (int i=0; i < _MAX; i++) {
			    int pop = data[i].count;
			    int totalE = data[i].energy;
			    boolean extinct = false;
			    if (pop == 0 && totalE == 0) extinct = true;
			    
			    if(cumulativeData[i] == null) {
			    	cumulativeData[i] = new PlayerRoundData(data[i].playerId, 0, 0);
			    }
			    
			    cumulativeData[i].count += data[i].count;
			    cumulativeData[i].energy += data[i].energy;
			    if(extinct) cumulativeData[i].extinctionCount++; 
	        }
		}
		
		//calculate mean
		for(int i = 0, size = lastRounds.size(); i < _MAX; i++) {
			cumulativeData[i].count = cumulativeData[i].count / size;
			cumulativeData[i].energy = cumulativeData[i].energy / size;
		}
		
		println(String.format("[SUMMARY of %d trials]", trials));
		for(PlayerRoundData data: cumulativeData) {
			println(String.format("Player: %d Mean Count: %d Mean Energy: %d Extinction: %d", data.playerId, data.count, data.energy, data.extinctionCount));
		}
	}
	
	protected void println(String message) {
		try { getGame().println(message); } catch(Exception ex) {}
	}
	
	public void run ()
	{
		try
		{
		    while (getGame().step()) {
		    	getGame().refreshGame();
		    	controlPanel.refreshControlPanel();
		    }
			getGame().refreshGame();
			controlPanel.refreshControlPanel();
			
			printInfo(gameIndex, trials);
			trackLastRound();
			gameIndex++;
			
			if(gameIndex > trials) {
				showSummary();
				return;
			}
			
			Thread.sleep(100);
			
			GUI._amoeba.resetGame();
			controlPanel.refreshControlPanel();
			run();

		}
		catch (Exception e)
		{
		    System.out.println("unexpected exception caught in run");
	                e.printStackTrace();
		}
	
    }
    
    }

} // end - class Organisms2


class Round
{
    int number;         // what number this round is
    int foodEnergyAvailable;  // how much energy (from food) is on the board
    int totalAutomata;  // how many automata are on board
    PlayerRoundData players[];  // what players looked like this round

    Round(int numPlayers, int roundNumber, int newFoodEnergyAvailable)
    {
	players = new PlayerRoundData[numPlayers];
	number = roundNumber;
	foodEnergyAvailable = newFoodEnergyAvailable;
	totalAutomata=0;
    }

    void addPlayerData(int player, int energy, int count)
    {
	players[player] = new PlayerRoundData(player, energy, count);
	totalAutomata+=count;
    }
}


class PlayerRoundData
{
   
	int playerId;
    int energy;
    int count;
    boolean extinction;
    int extinctionCount;

    PlayerRoundData(int p, int e, int c)
    {
	playerId = p;
	energy = e;
	count = c;
	extinction = false;
	extinctionCount = 0;
    }    

	public void isExtinct(boolean extinction) {
		this.extinction = extinction;
	}
    
}


class GraphDrawer extends JFrame
{
    int width = 800;
    int height = 600;
    Vector rounds;
    Color playerColors[];

    public GraphDrawer(Vector newRounds, Color newPlayerColors[])
    {
        super("Power Graphs: Automata, Energy, Available Food");
	rounds = newRounds;
	playerColors = newPlayerColors;
        getContentPane().setBackground(Color.BLACK);
        setSize(width, height);
        setVisible(true);
    }

    public void paint(Graphics g)
    {
        super.paint(g);

	// if can't paint, don't try
	if (rounds==null || rounds.size()==0)
	{
	    return;
	}

	int numPlayers = ((Round)rounds.elementAt(0)).players.length;

        int xFoodEnergy[] = new int[width+2];
        int yFoodEnergy[] = new int[width+2];

        int xTotalAutomata[] = new int[width+2];
        int yTotalAutomata[] = new int[width+2];

        int xPlayerEnergy[][] = new int[numPlayers][width+2];
        int yPlayerEnergy[][] = new int[numPlayers][width+2];

        int xPlayerCount[][] = new int[numPlayers][width+2];
        int yPlayerCount[][] = new int[numPlayers][width+2];

	// --------------------------------------------------------------
	// first, graph available food energy
	int maxFoodEnergy=0;
	int maxAutomata = 0;
		
	for (int i = 0; i < rounds.size(); i++)
	{
	    Round thisRound = (Round)rounds.elementAt(i);

	    if (thisRound.foodEnergyAvailable> maxFoodEnergy)
		maxFoodEnergy = thisRound.foodEnergyAvailable;

 	    if (thisRound.totalAutomata > maxAutomata)
 		maxAutomata = thisRound.totalAutomata;
	}

	for (int i = 0; i < width+2; i++)
	{
	    xFoodEnergy[i]=i;
	    xTotalAutomata[i]=i;
	    if (i==0 || i==width+1)
	    {
		// for edges, set to 0
		yFoodEnergy[i]=0;
		yTotalAutomata[i]=0;

		for (int j = 0; j < numPlayers; j++)
		{
		    yPlayerEnergy[j][i] = 0;
		    xPlayerEnergy[j][i] = i;

		    yPlayerCount[j][i] = 0;
		    xPlayerCount[j][i] = i;
		}
	    }
	    else
	    {
		Round thisRound = (Round)rounds.elementAt(
		    (i-1)*rounds.size()/width);

		yFoodEnergy[i]=(int)( ((float)-thisRound.foodEnergyAvailable
				   *((float)height/4))/(float)maxFoodEnergy);

		yTotalAutomata[i]=(int)( ((float)-thisRound.totalAutomata
				   *((float)height/4))/(float)maxAutomata);

		int totalPlayerEnergy = 0;  // total energy of all players
		                            // for this round

		int totalPlayerCount = 0;  // total automata count of
		                           // all players for this
		                           // round

		int previousEnergyThisRound = 0;  // how much energy
						  // we've already
						  // drawn

		int previousAutomataThisRound = 0;  // how many automata
						    // we've already
						    // drawn
		for (int j = 0; j < thisRound.players.length; j++)
		{
		    totalPlayerEnergy+=thisRound.players[j].energy;
		    totalPlayerCount+=thisRound.players[j].count;
		}

		for (int j = 0; j < thisRound.players.length; j++)
		{
		    float energy = (float)thisRound.players[j].energy;
		    float count = (float)thisRound.players[j].count;

		    xPlayerEnergy[j][i] = i;
		    yPlayerEnergy[j][i] = (int)(
			-(energy+previousEnergyThisRound)
			*((float)height/4)/(float)totalPlayerEnergy);
		    previousEnergyThisRound+=energy;

		    xPlayerCount[j][i] = i;
		    yPlayerCount[j][i] = (int)(
			-(count+previousAutomataThisRound)
			*((float)height/4)/(float)totalPlayerCount);
		    previousAutomataThisRound+=count;
		}
		

// 		System.out.println("round="+
// 				   (i-1)*rounds.size()/width+
// 				   ", energy="+
// 				   thisRound.foodEnergyAvailable);

// 		System.out.println("point: ("+
// 				   i+", "+
// 				   yPoints[i]+")");
	    }
	}
	
        // Create 2D by casting g to graphics2D
        Graphics2D g2d=(Graphics2D) g;

	Polygon p = new Polygon(xFoodEnergy, yFoodEnergy,
				xFoodEnergy.length);
	g2d.setColor(new Color(.7f, 1f, .8f));
	g2d.translate(0, height);
	g2d.fill(p);

	// --------------------------------------------------------------
	// next, graph each player energy 

	g2d.translate(0, -((float)height/3));
	for (int i = numPlayers-1;
	     i >=0; 
	     i--)
	{
//  	    for (int j = 0; j < xPlayerEnergy[i].length; j++)
//  	    {
//  		System.out.print(i+":"+xPlayerEnergy[i][j]+","+
//  				 yPlayerEnergy[i][j]+" ");
//  	    }
	    p = new Polygon(xPlayerEnergy[i], yPlayerEnergy[i],
			    xPlayerEnergy[i].length);
	    g2d.setColor(playerColors[i]);
	    g2d.fill(p);
	}


	// --------------------------------------------------------------
	// next, graph each player count

	g2d.translate(0, -((float)height/3));
	for (int i = numPlayers-1;
	     i >=0; 
	     i--)
	{
	    p = new Polygon(xPlayerCount[i], yPlayerCount[i],
			    xPlayerCount[i].length);
	    g2d.setColor(playerColors[i]);
	    g2d.fill(p);
	}

    }
}
