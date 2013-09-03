//***********************************************************
//*
//* File:           MessagePanel.java
//* Author:         Abhinav Kamra
//* Contact:        kamra-at-cs.columbia.edu
//* Update:         9.9.2003
//*
//* Description:    Display and logging component exported
//*                 to the primary GUI backbone.
//*
//***********************************************************

package organisms.ui;

import java.io.*;
import java.util.Date;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public final class MessagePanel extends JPanel implements ActionListener, ChangeListener, Serializable {
    JTabbedPane     _tab;
    JScrollPane     _scroll;
    JTextArea       _text;
    JScrollPane     _logscroll;
    JTextArea       _logtext;
    boolean         _registered;
    final int       _CWIDTH         = 340;
    final int       _CHEIGHT        = 250;
    final Font      _CMESSAGE_FONT  = new Font("Courier", Font.BOLD, 12);
    final ImageIcon _CLOG_ICON      = new ImageIcon("Images/wood_log.gif");
    JButton         _log;        
    File            _logfile;
    boolean         _logging;        
    transient BufferedWriter  _writer;
    transient IFCUI           _ui;

    public MessagePanel() throws Exception {
        super();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
                                                     BorderFactory.createLoweredBevelBorder()));
        setPreferredSize(new Dimension(_CWIDTH, _CHEIGHT));
        setMinimumSize(new Dimension(_CWIDTH, _CHEIGHT));                                                        

        _tab = new JTabbedPane();
        _tab.addChangeListener(this);
            
        _log = new JButton(_CLOG_ICON);
        _log.addActionListener(this);
        _text = new JTextArea();
        _text.setEditable(false);
        _text.setFont(_CMESSAGE_FONT);
        _scroll = new JScrollPane(_text);
        _tab.add("Messages", _scroll);

        _logtext = new JTextArea();
        _logtext.setEditable(false);
        _logtext.setFont(_CMESSAGE_FONT);
        _logscroll = new JScrollPane(_logtext);
        _tab.add("View Log", _logscroll);                       
        add(_tab, BorderLayout.CENTER);
    }

    public void register(IFCUI __ui, IFCModel __model) throws Exception {
        _ui = __ui;
        _logfile = new File(__model.exportConfiguration().logFile());
        _registered = true;
    }
    
    public void actionPerformed(ActionEvent __event) {
        Object source = __event.getSource();

        if (source == _log) {                                        
            _logging = (_logging) ? false : true;            
        }
        _text.append("[Logging " + (_logging ? "enabled]\n" : "disabled]\n"));
    }
    
    public void stateChanged(ChangeEvent __event) {
        try {
            if (__event.getSource() == _tab && _logtext != null) {            
                _logtext.setText(readLogFile());            
            }    
        } catch (Exception EXC) {
            _logtext.setText(EXC.getMessage());
        }
    }        
        
    public void print(String __str) throws Exception {
        JScrollBar scrollbar;
        
        _text.append(__str);
        scrollbar = _scroll.getVerticalScrollBar();
        scrollbar.setValue(scrollbar.getMaximum() + scrollbar.getVisibleAmount());
                 
        if (_logging) {
            if (_writer == null) {
                _writer = new BufferedWriter(new FileWriter(_logfile.getName(), true));                    
            }                             
            _writer.write(__str, 0, __str.length());
            _writer.flush();
        }  
        if (_registered) {
            _ui.refresh();
        }
    }
    
    public void println() throws Exception {
        print("\n");
    }
    
    public void println(String __str) throws Exception {
        print(__str + "\n");
    }                
    
    public JButton[] exportTools() {
        JButton[] ret = new JButton[1];
        ret[0] = _log;
        return ret;            
    }              
    
    public String readLogFile() throws Exception {    
        BufferedReader reader = new BufferedReader(new FileReader(_logfile));
        StringBuffer SB = new StringBuffer();
        String STR;
        int count=0;

        while ((STR = reader.readLine()) != null) {
            SB.append(Integer.toString(count++));
            SB.append(":  ");
            SB.append(STR);
            SB.append("\n");
        }        
        reader.close();
        
        return new String(SB);
    }
}
