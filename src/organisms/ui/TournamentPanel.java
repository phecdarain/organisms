//***********************************************************
//*
//* File:           TournamentPanel.java
//* Author:         Abhinav Kamra
//* Contact:        kamra-at-cs.columbia.edu
//* Update:         9.13.2003
//*
//* Description:    Visual Tournament setup and analysis 
//*                 class.
//*
//***********************************************************

package organisms.ui;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.TableModel;
import java.text.NumberFormat;


public final class TournamentPanel extends JPanel {

    JTabbedPane         _tabs;
    ConfigurationPanel  _configure;
    AnalysisPanel       _analysis;
    IFCTournament       _tournament;
    IFCModel            _model;
    IFCUI               _ui;
    Random              _random;

    Hashtable		_hashtable;

    static final int    _CWIDTH = 600;
    static final int    _CHEIGHT = 600;
    static final int    _CMIN_GAMES = 1;
    static final int    _CMAX_GAMES = 10000;


    public TournamentPanel() throws Exception {
        super();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
                                                     BorderFactory.createLoweredBevelBorder()));
        setPreferredSize(new Dimension(_CWIDTH, _CHEIGHT));
        setMinimumSize(new Dimension(_CWIDTH, _CHEIGHT));

        _configure = new ConfigurationPanel();
        _tabs = new JTabbedPane();
        _tabs.addTab("Configure", _configure);
        _tabs.addTab("Analysis", new JPanel());
        add(_tabs);
        _random = new Random();
    }

    public JButton[] exportTools() {
        return _configure.exportTools();
    }

    public JMenu exportMenu() {
        return null;
    }

    public void register(IFCUI __ui, IFCModel __model) throws Exception {
        _ui = __ui;
        _model = (IFCModel) __model.getClass().newInstance();
        _configure.register(_model.exportConfiguration());;
        repaint();
    }

    public JPanel exportViewPanel() throws Exception {
        return this;
    }
    
    private final class ConfigurationPanel extends JPanel implements ActionListener, ListSelectionListener {

        JScrollPane      _scrplayers;
        JScrollPane      _scrclasses;
        JList            _players;
        JList            _classes;
        JTextField       _numplayers;
        JTextField       _numgames;
        JButton          _run;
        IFCConfiguration _config;
        final ImageIcon  _CRUN_ICON             = new ImageIcon("Images/alum_run.gif");
        final int        _CSLOT_WIDTH           = _CWIDTH;
        final int        _CSLOT_HEIGHT          = 100;
        final int        _CTEXT_WIDTH           = _CWIDTH;
        final int        _CTEXT_HEIGHT          = 40;
        final int        _CRADIO_HEIGHT         = 20;
        final int        _CDEFAULT_NUM_GAMES    = 10;
        final Font       _CCONFIG_FONT          = new Font("Courier", Font.BOLD, 16);
        final Font       _CLIST_FONT            = new Font("Courier", Font.BOLD, 10);
        final String     _CSPACER               = "  ";
        
        public ConfigurationPanel() throws Exception {
            super();
            setLayout(new GridLayout(2, 2));
            setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
                                                     BorderFactory.createLoweredBevelBorder()));
            setPreferredSize(new Dimension(_CWIDTH, _CHEIGHT));
            setMinimumSize(new Dimension(_CWIDTH, _CHEIGHT));

            _run = new JButton(_CRUN_ICON);
            _run.addActionListener(this);
        }

        public void register(IFCConfiguration __config) throws Exception {
            SlotPanel slot;
            JPanel box;
            JLabel label;
            ButtonGroup bg;

            removeAll();
            _config = __config;
            slot = new SlotPanel(_CSLOT_WIDTH, _CSLOT_HEIGHT);
            slot.setVertical();
            _classes = new JList(_config.classList());
            _classes.setFont(_CLIST_FONT);
            _classes.addListSelectionListener(this);
            _scrclasses = new JScrollPane(_classes);
            label = new JLabel("Available Players");
            label.setFont(_CCONFIG_FONT);
            slot.add(label, _scrclasses);
            add(slot);

            slot = new SlotPanel(_CSLOT_WIDTH, _CSLOT_HEIGHT);
            slot.setVertical();
            _players = new JList(_config.playerList());
            _players.setFont(_CLIST_FONT);
            _players.addListSelectionListener(this);
            _scrplayers = new JScrollPane(_players);
            label = new JLabel("Contestants");
            label.setFont(_CCONFIG_FONT);
            slot.add(label, _scrplayers);
            add(slot);;

            box = new JPanel();
            box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
            slot = new SlotPanel(_CTEXT_WIDTH, _CTEXT_HEIGHT);
            box.add(slot);

            slot = new SlotPanel(_CTEXT_WIDTH, _CTEXT_HEIGHT);
            _numplayers = new JTextField(Integer.toString(_config.numPlayers()));
            _numplayers.setFont(_CCONFIG_FONT);
            label = new JLabel("PlayersPerGame: ");
            label.setFont(_CCONFIG_FONT);
            slot.add(label, _numplayers);
            box.add(slot);

            slot = new SlotPanel(_CTEXT_WIDTH, _CTEXT_HEIGHT);
            _numgames = new JTextField(Integer.toString(_CDEFAULT_NUM_GAMES));
            _numgames.setFont(_CCONFIG_FONT);
            label = new JLabel("NumGames:       ");
            label.setFont(_CCONFIG_FONT);
            slot.add(label, _numgames);
            box.add(slot);
            add(box);
            
/*
            slot = new SlotPanel(_CTEXT_WIDTH, _CTEXT_HEIGHT);
            _numhiddencardsfield = new JTextField(Integer.toString(_CDEFAULT_NUM_HIDDEN));
            _numhiddencardsfield.setFont(_CCONFIG_FONT);
            label = new JLabel("NumHiddenCards: ");
            label.setFont(_CCONFIG_FONT);
            slot.add(label, _numhiddencardsfield);
            box.add(slot);
            add(box);

            slot = new SlotPanel(_CTEXT_WIDTH, _CTEXT_HEIGHT);
            _cardsperplayerfield = new JTextField(Integer.toString(_CDEFAULT_NUM_CPP));
            _cardsperplayerfield.setFont(_CCONFIG_FONT);
            label = new JLabel("CardsPerPlayer: ");
            label.setFont(_CCONFIG_FONT);
            slot.add(label, _cardsperplayerfield);
            box.add(slot);
            add(box);
            slot = new SlotPanel(_CTEXT_WIDTH, _CTEXT_HEIGHT);
            _numroundsfield = new JTextField(Integer.toString(_CDEFAULT_MAXROUNDS));
            _numroundsfield.setFont(_CCONFIG_FONT);
            label = new JLabel("MaxRounds:      ");
            label.setFont(_CCONFIG_FONT);
            slot.add(label, _numroundsfield);
            box.add(slot);
            add(box);
*/
            
	    /*
            box = new JPanel();
            box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
            slot = new SlotPanel(_CTEXT_WIDTH, _CTEXT_HEIGHT);
            box.add(slot);
            slot = new SlotPanel(_CTEXT_WIDTH, _CTEXT_HEIGHT);
            label = new JLabel("Tournament Type");
            label.setFont(_CCONFIG_FONT);
            slot.add(new JLabel(_CSPACER), label);
            box.add(slot);
            slot = new SlotPanel(_CTEXT_WIDTH, _CRADIO_HEIGHT);
            _roundrobin = new JRadioButton("Round Robin");
            _roundrobin.setFont(_CLIST_FONT);
            _roundrobin.addActionListener(this);
            _roundrobin.setSelected(true);
            slot.add(new JLabel(_CSPACER), _roundrobin);
            box.add(slot);
            slot = new SlotPanel(_CTEXT_WIDTH, _CRADIO_HEIGHT);
            _multiplayer = new JRadioButton("Multiplayer");
            _multiplayer.setFont(_CLIST_FONT);
            _multiplayer.addActionListener(this);
            slot.add(new JLabel(_CSPACER), _multiplayer);
            box.add(slot);
            slot = new SlotPanel(_CTEXT_WIDTH, _CRADIO_HEIGHT);
            _multisample = new JRadioButton("Multisample");
            _multisample.setFont(_CLIST_FONT);
            _multisample.addActionListener(this);
            slot.add(new JLabel(_CSPACER), _multisample);
            box.add(slot);
            bg = new ButtonGroup();
            bg.add(_roundrobin);
            bg.add(_multiplayer);
            bg.add(_multisample);
            add(box);
	    */
        }

        public void valueChanged(ListSelectionEvent __event) {
            try {
                Object source = __event.getSource();
                ListModel listmodel;
                Vector contents;
                Class selection;
                int val;
                int _MAX;
                
                if (source == _classes) {
                    listmodel = _classes.getModel();
                    selection = (Class) listmodel.getElementAt(_classes.getSelectedIndex());

                    contents = new Vector();
                    listmodel = _players.getModel();
                    _MAX = listmodel.getSize();
                    for (int i=0; i < _MAX; i++) {
                        contents.add(listmodel.getElementAt(i));
                    }
                    if (!contents.contains(selection)) {
                        contents.add(selection);
                        _players.setListData(contents);
                        _numplayers.setText(Integer.toString(contents.size()));
                        repaint();
                    }
                }

                if (source == _players) {
                    val = _players.getSelectedIndex();
                    contents = new Vector();
                    listmodel = _players.getModel();
                    _MAX = listmodel.getSize();
                    for (int i=0; i < _MAX; i++) {
                        if (i != val) {
                            contents.add(listmodel.getElementAt(i));
                        }
                    }
                    _players.setListData(contents);
                    _numplayers.setText(Integer.toString(contents.size()));
                    repaint();
                }

            } catch (Exception EXC) {
                System.out.println(EXC.getMessage());
                EXC.printStackTrace();
            }
        }

        public void actionPerformed(ActionEvent __event) {
            try {
                Object source = __event.getSource();
                IFCGameRecord[] games=null;
                IFCTournamentResults[] results;
                IFCTournament tournament;
                ListModel listmodel;
                ParseValue pv;
                ArrayList alist;
                Class[] clist;
                Class[] plist;
                int _MAXI;
                int _MAXJ;
                int _MAXK;

                if (source == _run) {

			    // START -- KAMRA Tournament
                    
                        listmodel = _players.getModel();
                        _MAXI = listmodel.getSize();
                        if (_MAXI < 1) {
                            throw new Exception("Error:  Invalid Number of Players Specified");
                        }
                        clist = new Class[_MAXI];
                        for (int i=0; i < _MAXI; i++) {
                            clist[i] = (Class) listmodel.getElementAt(i);
                        }

                        pv = ParseValue.parseIntegerValue(_numplayers.getText(), _config.numPlayersMin(), _config.numPlayersMax());
                        if (!pv.isValid()) {
                            throw new Exception("Error:  Number of Players Out of Range");
                        }
                        _MAXJ = ((Integer) pv.value()).intValue();
                        if (_MAXI < _MAXJ) {
                            throw new Exception("Error:  Number of Players per Game Is Larger Than Contestant List Size");
                        }

                        pv = ParseValue.parseIntegerValue(_numgames.getText(), _CMIN_GAMES, _CMAX_GAMES);
                        if (!pv.isValid()) {
                            throw new Exception("Error:  Number of Games Out of Range");
                        }
                        _MAXK = ((Integer) pv.value()).intValue();

			/*
                        pv = ParseValue.parseIntegerValue(_numhiddencardsfield.getText(), _CMIN_HIDDEN, _CMAX_HIDDEN);
                        if (!pv.isValid()) {
                            throw new Exception("Error:  Number of Cards Out of Range");
                        }
                        numhiddencards = ((Integer) pv.value()).intValue();
                        
                        pv = ParseValue.parseIntegerValue(_cardsperplayerfield.getText(), _config.CardsPerPlayerMin(), _config.CardsPerPlayerMax());
                        if (!pv.isValid()) {
                            throw new Exception("Error:  Cards Per Player Out of Range");
                        }
                        cardsperplayer = ((Integer) pv.value()).intValue();
                        
                        pv = ParseValue.parseIntegerValue(_numroundsfield.getText(), _config.numRoundsMin(), _config.numRoundsMax());
                        if (!pv.isValid()) {
                            throw new Exception("Error:  Number of Rounds Out of Range");
                        }
                        maxrounds = ((Integer) pv.value()).intValue();
			*/

                        plist = new Class[_MAXJ];
                        alist = new ArrayList();
                        games = new GameRecord[_MAXK];
			for(int k=0;k < _MAXK;k++)
			{
				alist.clear();
				for(int i=0;i < _MAXI;i++)
					alist.add(new Integer(i));
				for(int j=0;j < _MAXJ;j++)
				{
					int index = ((Integer)alist.remove(_random.nextInt(alist.size()))).intValue();
					plist[j] = clist[index];
				}
				games[k] = new GameRecord();
				games[k].setPlayers(plist);
			}
			// END -- KAMRA Tournament

                    tournament = new Tournament();
                    tournament.setGames(games);
                    _model.run(tournament);
                    _tabs.remove(1);
                    results = new IFCTournamentResults[1];
                    results[0] = new TournamentResults(games);
                    //results[1] = new PlayerSpecificResults(games);
		    results[0].Print();
		    //results[1].Print();
                    _analysis = new AnalysisPanel(results);
                    _tabs.addTab("Analysis", _analysis);
                    _tabs.setSelectedComponent(_analysis);
                    _ui.maximizeViewSize();
                    repaint();
                }
            } catch (Exception EXC) {
                System.out.println(EXC.getMessage());
                EXC.printStackTrace();
            }
        }

        public JButton[] exportTools() {
            JButton[] RET = new JButton[1];
            RET[0] = _run;
            return RET;
        }
        
        double choose(int __c, int __n) {
            return  (factorial(__c) / (factorial(__n) * factorial(__c - __n)));
        }
        
        double factorial(int __n) {
            double RET=1;

            for (int i=2; i <= __n; i++) {
                RET *= i;
            }
            return RET;
        }
    }

    private final class AnalysisPanel extends JPanel {

        JTabbedPane             _tabs;
        IFCTournamentResults[]  _results;
        final Font              _CHEADER_FONT = new Font("Courier", Font.BOLD, 14);
        final Font              _CCELL_FONT = new Font("Courier", Font.BOLD, 12);

        public AnalysisPanel(IFCTournamentResults[] __results) throws Exception {
            super();
            
            JTable table;
            JScrollPane scr;
            int _MAX;

            setLayout(new BorderLayout());
            setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
                                                     BorderFactory.createLoweredBevelBorder()));
            setPreferredSize(new Dimension(_CWIDTH, _CHEIGHT));
            setMinimumSize(new Dimension(_CWIDTH, _CHEIGHT));

            _tabs = new JTabbedPane();
            _results = __results;
            _MAX = _results.length;
            for (int i=0; i < _MAX; i++) {
                table = new JTable();
                table.setFont(_CCELL_FONT);
                table.getTableHeader().setFont(_CHEADER_FONT);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                table.setModel(_results[i]);
                table.getColumnModel().addColumnModelListener(_results[i]);
                scr = new JScrollPane(table);
                _tabs.add(_results[i].name(), scr);
            }
            add(_tabs);
        }
    }
}

