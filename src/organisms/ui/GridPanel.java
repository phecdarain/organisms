//***********************************************************
//*
//* File:           GridPanel.java
//* Author:         Abhinav Kamra
//* Contact:        kamra-at-cs.columbia.edu
//* Update:         9.5.2003
//*
//* Description:    Gridded, sized panel.
//*
//***********************************************************

package organisms.ui;

import java.awt.*;
import javax.swing.*;

public final class GridPanel extends JPanel {
    Dimension _dim;
    int       _width;
    int       _height;
    
    public GridPanel(int __width, int __height, int __cols) {         
        _width = __width;
        _height = __height;   
        _dim = new Dimension(_width, _height);
        setLayout(new GridLayout(1, __cols));
        setPreferredSize(_dim);        
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
