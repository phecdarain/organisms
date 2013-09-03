//***********************************************************
//*
//* File:           TournamentResults.java
//* Author:         Srikant Krishna
//* Contact:        srikant@cs.columbia.edu
//* Update:         9.14.2002
//*
//* Description:    Tournament results analysis class
//*                 responsible for computing statistics
//*                 and generating a table model.
//*
//***********************************************************

package organisms.ui;

import java.util.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.text.NumberFormat;

public final class TournamentResults implements IFCTournamentResults {

    ArrayList       _listeners;
    Class           _cellclass;
    final String    _CNULL_STRING = "[Null]";
    IFCGameRecord[] _games;
    String[][]      _table;
    NumberFormat    _nf;
    PlayerEntry[]   _entries;
    double[]        _versusopp;
    double[]        _oppname;
    static final String _CNAME      = "Tournament Results";
    static final int _CNUM_GAMES    = 0;
    static final int _CAVG_RANK     = 1;
    static final int _CCUM_RANK     = 2;
    static final int _CNUM_WINS     = 3;
    static final int _CNUM_TIES     = 4;
    static final int _CNUM_LOSSES   = 5;
    static final int _CCUM_SCORE    = 6;
    static final int _CAVG_SCORE    = 7;
    static final int[] _CVALUE_COLUMNS = { _CNUM_GAMES, _CAVG_RANK, _CCUM_RANK, _CNUM_WINS, _CNUM_TIES, _CNUM_LOSSES, _CCUM_SCORE, _CAVG_SCORE };
    static final String[] _CNAMES = { "Player", "NumGames", "AvgRank", "CumRank", "Wins", "Ties", "Losses", "CumScore", "AvgScore" };

    public TournamentResults(IFCGameRecord[] __games) throws Exception {
        HashMap hash;
        Class[] players;
        ClassScorePair[] pairs;
        double[] ranks;
        PlayerEntry pe;
        double[] scores;
        double avg=0;
        int[] winners;
        int k;
        int _MAXI;
        int _MAXJ;
        int _MAXK;

        _listeners = new ArrayList();
        _cellclass = new String().getClass();
        _games = __games;

        hash = new HashMap();
        _MAXI = _games.length;
        for (int i=0; i < _MAXI; i++) {
            players = _games[i].players();
            scores = _games[i].scores();
            winners = Util.maxIndex(scores);
            _MAXJ = players.length;

            pairs = new ClassScorePair[_MAXJ];
            avg = 0;
            for (int j=0; j < _MAXJ; j++) {
                avg += scores[j];
                pairs[j] = new ClassScorePair(players[j], scores[j]);
            }
            avg = (_MAXJ > 0) ? (avg / (double) _MAXJ) : 0;
            ranks = Util.ranks(pairs);

            for (int j=0; j < _MAXJ; j++) {
                pe = (PlayerEntry) hash.get(players[j]);
                if (pe == null) {
                    pe = new PlayerEntry(players[j]);;
                    hash.put(players[j], pe);
                }
                pe.set(_CCUM_SCORE, pe.get(_CCUM_SCORE) + scores[j]);
                pe.set(_CNUM_GAMES, pe.get(_CNUM_GAMES) + 1);
                for (k=0; k < _MAXJ; k++) {
                    if  (players[j].equals(pairs[k].playerClass())) {
                        pe.set(_CCUM_RANK, pe.get(_CCUM_RANK) + ranks[k]);
                        break;
                    }
                }
                if (k == _MAXJ) {
                    throw new Exception("Error:  Player Class Rank Not Found: " + players[j]);
                }
                _MAXK = winners.length;
                if (_MAXK == 1) {
                    if (winners[0] == j) {
                        pe.set(_CNUM_WINS, pe.get(_CNUM_WINS) + 1);;
                    } else {
                        pe.set(_CNUM_LOSSES, pe.get(_CNUM_LOSSES) + 1);
                    }
                } else {
                    for (k=0; k < _MAXK; k++) {
                        if (winners[k] == j) {
                            pe.set(_CNUM_TIES, pe.get(_CNUM_TIES) + 1);
                            break;
                        }
                    }
                    if (k == _MAXK) {
                        pe.set(_CNUM_LOSSES, pe.get(_CNUM_LOSSES) + 1);
                        //pe.set(_CEPSILON, pe.get(_CEPSILON) + (scores[j] - avg));
                    }
                }
            }
        }
        _entries = (PlayerEntry[]) hash.values().toArray(new PlayerEntry[0]);
        _MAXI = _entries.length;
        for (int i=0; i < _MAXI; i++) {
            _MAXJ = (int) _entries[i].get(_CNUM_GAMES);
//            _entries[i].set(_CDELTA, (_MAXJ != 0) ? (_entries[i].get(_CDELTA) / _MAXJ) : 0);
//            _entries[i].set(_CEPSILON, (_MAXJ != 0) ? (_entries[i].get(_CEPSILON) / _MAXJ) : 0);
            _entries[i].set(_CAVG_RANK, (_MAXJ != 0) ? (_entries[i].get(_CCUM_RANK) / _MAXJ) : 0);
            _entries[i].set(_CAVG_SCORE, (_MAXJ != 0) ? (_entries[i].get(_CCUM_SCORE) / _MAXJ) : 0);
        }
        _nf = NumberFormat.getInstance();
        _nf.setMinimumFractionDigits(3);
        _nf.setMaximumFractionDigits(3);
        _nf.setMinimumIntegerDigits(1);
        Arrays.sort(_entries, new PlayerEntrySorter(_CAVG_RANK));
        generateTable();
    }

    public void generateTable() throws Exception {
        int _MAXI = _entries.length;
        int _MAXJ = _CVALUE_COLUMNS.length;
        String[] toks;

        _table = new String[_MAXI][_CNAMES.length];
        _MAXJ = _CVALUE_COLUMNS.length;
        for (int i=0; i < _MAXI; i++) {
            toks = Util.split(".", _entries[i].toString());
            _table[i][0] = toks[toks.length-1];
            for (int j=0; j < _MAXJ; j++) {
                _table[i][j+1] = _nf.format(_entries[i].get(j));
            }
        }
    }
    
    public String name() throws Exception {
        return _CNAME;
    }

	public void Print() throws Exception
	{
		int x = getRowCount();
		int y = getColumnCount();
		
		//System.out.println("[" + _games[0].numRobots() + " Robots, " + (_games[0].players()).length + " Players, n = " + _games[0].size() + "]");
		for(int i=0;i < y;i++)
			System.out.print(_CNAMES[i] + "\t");
		System.out.println();
		for(int i=0;i < x;i++)
		{
//			System.out.print(_names[i] + "\t");
			for(int j=0;j < y;j++)
			{
				System.out.print((String)getValueAt(i, j) + "\t");
			}
			System.out.println();
		}
		System.out.println();
	}

    public void addTableModelListener(TableModelListener __listener) {
        if (!_listeners.contains(__listener)) {
            _listeners.add(__listener);
        }
    }

    public void removeTableModelListener(TableModelListener __listener) {
        _listeners.remove(__listener);
    }

    public Class getColumnClass(int __col) {
        return _cellclass;
    }

    public int getColumnCount() {
        return _CVALUE_COLUMNS.length + 1;
    }

    public String getColumnName(int __col) {
        return _CNAMES[__col];
    }

    public int getRowCount() {
        if (_table == null) {
            return 0;
        }
        return _table.length;
    }

    public Object getValueAt(int __row, int __col) {
        return _table[__row][__col];
    }

    public boolean isCellEditable(int __row, int __col) {
        return false;
    }

    public void setValueAt(Object __obj, int __row, int __col)
    { }
    
    public void columnAdded(TableColumnModelEvent __event) 
    { }
    
    public void columnMarginChanged(ChangeEvent __event)
    { }

    public void columnMoved(TableColumnModelEvent __event)
    { }
    
    public void columnRemoved(TableColumnModelEvent __event)
    { }
    
    public void columnSelectionChanged(ListSelectionEvent __event) {
        try {
            int index = __event.getFirstIndex();
        
            if (index == 0) {
                Arrays.sort(_entries);
                generateTable();
                return;
            }
            Arrays.sort(_entries, new PlayerEntrySorter(index-1));
            generateTable();
        } catch (Exception EXC) {
            System.out.println(EXC.getMessage());
            EXC.printStackTrace();
        }
    }

    private class PlayerEntry implements Comparable {
        Class _player;
        String _name;
        double[] _values;

        public PlayerEntry(Class __player) throws Exception {
            _player = __player;
            _name = _player.toString();
            _values = new double[_CVALUE_COLUMNS.length];
        }
        
        public void set(int __pos, double __val) throws Exception {
            _values[__pos] = __val;
        }

        public double get(int __pos) throws Exception {
            return _values[__pos];
        }
        
        public int compareTo(Object __obj) {
            return toString().compareTo(((PlayerEntry) __obj).toString());
        }

        public boolean equals(Object __obj) {
            if (!(__obj instanceof PlayerEntry)) {
                return false;
            }
            return ((PlayerEntry) __obj).equals(this);
        }

        public String toString() {
            return _name;
        }
    }
    
    private class ClassScorePair implements Comparable {
    
        Class _class;
        double _score;
        
        public ClassScorePair(Class __class, double __score) throws Exception {
            _class = __class;
            _score = __score;
        }
        
        public int compareTo(Object __obj) {
            try {
                double score = ((ClassScorePair) __obj)._score;

                return (_score > score ? -1 : (_score < score ? 1 : 0));
            } catch (Exception EXC) {
                System.out.println(EXC.getMessage());
                EXC.printStackTrace();
                return 0;
            }
        }

        public Class playerClass() throws Exception {
            return _class;
        }

        public double score() throws Exception {
            return score();
        }
    }
    
    private class PlayerEntrySorter implements Comparator {
        
        int _sortindex;
    
        public PlayerEntrySorter(int __sortindex) {
            _sortindex = __sortindex;
        }

        public int compare(Object __obj1, Object __obj2) {
            try {
                double val1 = ((PlayerEntry) __obj1).get(_sortindex);
                double val2 = ((PlayerEntry) __obj2).get(_sortindex);

                return (val1 < val2 ? -1 : (val1 > val2 ? 1 : 0));
            } catch (Exception EXC) {
                System.out.println(EXC.getMessage());
                EXC.printStackTrace();
                return 0;
            }
         }
    }
}
