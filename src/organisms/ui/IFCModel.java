//***********************************************************
//*
//* File:           IFCModel.java
//* Author:         Abhinav Kamra
//* Contact:        kamra-at-cs.columbia.edu
//* Update:         9.5.2003
//*
//* Description:    Model interface for all projects.
//*                 Rather than separate the model from
//*                 the view, the views are delegated
//*                 by the game models.
//*
//***********************************************************

package organisms.ui;

import java.io.Serializable;
import javax.swing.*;

public interface IFCModel extends Serializable {
    
    public JPanel           exportViewPanel()               throws Exception;
    public JPanel           exportControlPanel()            throws Exception;
    public IFCConfiguration exportConfiguration()           throws Exception;
    public JButton[]        exportTools()                   throws Exception;   
    public JMenu            exportMenu()                    throws Exception;
    public void             register(IFCUI __UI)            throws Exception;
    public String           name()                          throws Exception;
    public void             run(IFCTournament __tournament) throws Exception;
}
