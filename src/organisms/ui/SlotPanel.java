//***********************************************************
//*
//* File:           SlotPanel.java
//* Author:         Abhinav Kamra
//* Contact:        kamra-at-cs.columbia.edu
//* Update:         9.5.2003
//*
//* Description:    Sized panels.
//*
//*
//*
//***********************************************************

package organisms.ui;

import java.awt.*;
import javax.swing.*;

public final class SlotPanel extends JPanel {
    Dimension _dim;
    boolean   _vertical;
    int       _width;
    int       _height;
    
    public SlotPanel(int __width, int __height) {
        _width = __width;
        _height = __height;   
        _dim = new Dimension(_width, _height);
        setLayout(new BorderLayout());
        setPreferredSize(_dim);
    }                    
        
    private void add(JComponent __comp) { }
        
    public void add(JComponent __comp1, JComponent __comp2) {
        if (!_vertical) {
            add(__comp1, BorderLayout.WEST);
            add(__comp2, BorderLayout.CENTER);
        } else {
            add(__comp1, BorderLayout.NORTH);
            add(__comp2, BorderLayout.CENTER);
        }
    }
    
    public void setHorizontal() {
        _vertical = false;
    }
    
    public void setVertical() {
        _vertical = true;
    }
        
    public Dimension getPreferredSize() {
        return _dim;
    }
        
    public Dimension getMaximumSize() {
        return _dim;
    }
        
    public Dimension getMinimumSize() {
        return _dim;
    }
}
