//***********************************************************
//*
//* File:           GUI.java
//* Author:         Abhinav Kamra
//* Contact:        kamra-at-cs.columbia.edu
//* Update:         9.5.2003
//*
//* Description:    IFCUI implementation.  All the CS4444
//*                 Fall 2003 projects will use this
//*                 component.
//*
//***********************************************************

package organisms.ui;

import javax.swing.*;

import organisms.*;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;

public final class GUI implements IFCUI, ActionListener {
	
    /* added global var to hold Organisms2 class instance */
    public static OrganismsGame _amoeba;
    
    JFrame           _frame;
    JSplitPane       _splith;
    JSplitPane       _splitv;
    JTabbedPane      _display;
    JMenuItem        _save;
    JMenuItem        _load;    
    JButton          _saveb;
    JButton          _loadb;
    JToolBar         _toolbar;
    JPanel           _view;
    JPanel           _control;
    JPanel           _tview;
    MessagePanel     _message;
    //TournamentPanel  _tournament;
    IFCModel         _model;
    HashMap          _persistence;
    File             _objectfile;
    IFCConfiguration _config;
    double           _hviewloc;

    static final String     _CMODEL_NAME          = "Organisms2.Organisms2";
    static final ImageIcon  _CSAVE_ICON           = new ImageIcon("Images/wood_save.gif");
    static final ImageIcon  _CLOAD_ICON           = new ImageIcon("Images/wood_load.gif");
    static final String     _CDEFAULT_OBJECT_FILE = "gamemodel.state";
    static final double     _CHORIZONTAL_VIEW_SIZE = .6;

    public GUI() throws Exception {
        reset();
    }

    public void register(IFCModel __model) throws Exception {
        if (_frame != null) {
            register_cont(__model);
        } else {
            register_init(__model);
        }
        //_tournament.register(this, __model);
        _message.register(this, __model);
        println("[" + new Date(System.currentTimeMillis()) + "]: Initialization");
        refresh();
    }
        
    void register_init(IFCModel __model) throws Exception {
        JButton[] buttons;
        int _MAX;

        _model = __model; 
        _persistence = new HashMap();               
        _objectfile = new File(_CDEFAULT_OBJECT_FILE); 
        _message = new MessagePanel();
        //_tournament = new TournamentPanel();
        _frame = new JFrame();

        _toolbar = new JToolBar();
        buttons = _model.exportTools();
        if (buttons != null) {
            _MAX = buttons.length;
            for (int i=0; i < _MAX; i++) {
                _toolbar.add(buttons[i]);
            }
        }
        /*
        buttons = _message.exportTools();
        if (buttons != null) {
            _MAX = buttons.length;
            for (int i=0; i < _MAX; i++) {
                _toolbar.add(buttons[i]);
            }
        }
        */
        /*
        _saveb = new JButton(_CSAVE_ICON);
        _saveb.addActionListener(this);
        _toolbar.add(_saveb);
        _loadb = new JButton(_CLOAD_ICON);
        _loadb.addActionListener(this);
        _toolbar.add(_loadb);
        */

        /*
        buttons = _tournament.exportTools();
        if (buttons != null) {
            _MAX = buttons.length;
            for (int i=0; i < _MAX; i++) {
                _toolbar.add(buttons[i]);
            }
        }
        */

        _view = _model.exportViewPanel();
        _control = _model.exportControlPanel();
        _display = new JTabbedPane();
        _display.addTab("View", _view);
        //_display.addTab("Tournament", _tournament.exportViewPanel());

        _splitv = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        _splitv.setLeftComponent(_control);
        _splitv.setRightComponent(_message);
        _splitv.setOneTouchExpandable(true);

        _splith = new JSplitPane();
        _splith.setLeftComponent(_display);
        _splith.setRightComponent(_splitv);
        _splith.setOneTouchExpandable(true);
        resetViewSize();
                
        _frame.getContentPane().setLayout(new BorderLayout());
        _frame.setTitle(_model.name());   
        _frame.getContentPane().add(_toolbar, BorderLayout.NORTH);        
        _frame.getContentPane().add(_splith, BorderLayout.CENTER);        
        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _frame.pack();
        _frame.setVisible(true);
    }
    
    void register_cont(IFCModel __model) throws Exception {
        JButton[] buttons;
        int _MAX;

        _model = __model;                

        _toolbar.removeAll();            
        buttons = _model.exportTools();
        if (buttons != null) {
            _MAX = buttons.length;
            for (int i=0; i < _MAX; i++) {
                _toolbar.add(buttons[i]);
            }
        }
        /*
        buttons = _message.exportTools();
        if (buttons != null) {
            _MAX = buttons.length;
            for (int i=0; i < _MAX; i++) {
                _toolbar.add(buttons[i]);
            }
        }
        _toolbar.add(_saveb);
        _toolbar.add(_loadb);
        */
        /*
        buttons = _tournament.exportTools();
        if (buttons != null) {
            _MAX = buttons.length;
            for (int i=0; i < _MAX; i++) {
                _toolbar.add(buttons[i]);
            }
        }
        */
        _view = _model.exportViewPanel();
        _control = _model.exportControlPanel();

        _display.removeAll();
        _display.addTab("View", _view);
        //_display.addTab("Tournament", _tournament.exportViewPanel());

        _splitv.setLeftComponent(_control);
        _splitv.setRightComponent(_message);
        _splith.setLeftComponent(_display);
        _splith.setRightComponent(_splitv);

        _frame.getContentPane().removeAll();
        _frame.getContentPane().add(_toolbar, BorderLayout.NORTH);        
        _frame.getContentPane().add(_splith, BorderLayout.CENTER);        
        _frame.pack();
        _frame.setVisible(true);                        
    }
    
    public File selectFile(File __file) throws Exception {
        JFileChooser jfc;
        int val;
        File RET = null;
    
        jfc = new JFileChooser(__file);
        val = jfc.showOpenDialog(_frame);
        if (val == JFileChooser.APPROVE_OPTION) {
            RET = jfc.getSelectedFile();   
        }             
        return RET;    
    }
    
    public void actionPerformed(ActionEvent __event) {
        try {            
            Object OBJ = __event.getSource();
            Object read;
            File file;
            
            if (OBJ == _save || OBJ == _saveb) {
                file = selectFile(_objectfile);                
                
                if (_objectfile != null) {
                    writeObject(_objectfile);
                    _objectfile = file;
                }
                return;
            }                                        
            if (OBJ == _load || OBJ == _loadb) {
                file = selectFile(_objectfile);              
                if (file != null) {
                    read = readObject(_objectfile);
                    if (read == null) {
                        return;
                    }
                    _model = (IFCModel) read;
                    _model.register(this);
                    _objectfile = file;
                }
                return;
            }

        } catch (Exception EXC) {
            System.out.println(EXC.getMessage());
            EXC.printStackTrace();
        }
    }
    
    public void writeObject(File __file) throws Exception {
        FileOutputStream fos = new FileOutputStream(__file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        
        oos.writeObject(_model);
        oos.flush();
        oos.close();        
    }
    
    public Object readObject(File __file) throws Exception {
        FileInputStream fis = new FileInputStream(__file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object ret = ois.readObject();
        
        return ret;        
    }
    
    public void configure(IFCConfiguration __config) throws Exception {
        _config = __config;
    }
    
    public void refresh() throws Exception {
        if (_frame != null) {
            //_frame.paint(_frame.getGraphics());
            _frame.repaint();
        }
    }    

    public void reset() throws Exception {
	//        _model = (_config == null) ? new Organisms2() : new Organisms2(_config);

	if (_config == null) {
		_amoeba = new OrganismsGame();
	    _model= _amoeba; 
	}
	else {
	    _amoeba = new OrganismsGame(_config);
	    _model= _amoeba; 
	}
	_model.register(this);
	
    }

    public void print(String __str) throws Exception {
        _message.print(__str);
    }

    public void println(String __str) throws Exception {
        _message.println(__str);
    }

    public void println() throws Exception {
        _message.println();
    }
    
    public void maximizeViewSize() {
        _hviewloc = _splith.getDividerLocation();
        _splith.setDividerLocation(1.0);
    }
    
    public void resetViewSize() {
        if (_hviewloc == 0) {
            _splith.setDividerLocation(_CHORIZONTAL_VIEW_SIZE);
        } else {
            _splith.setDividerLocation(_hviewloc);
        }
    }

    public static final void main(String[] __args) {
        try {
            IFCUI ui = new GUI();

        } catch (Exception EXC) {
            System.out.println(EXC);
            EXC.printStackTrace();
        }
    }

    public void persist(Class __class, Object __obj) throws Exception {
        _persistence.put(__class, __obj);
    }
    
    public Object persist(Class __class) throws Exception {
        return _persistence.get(__class);
    }
}
