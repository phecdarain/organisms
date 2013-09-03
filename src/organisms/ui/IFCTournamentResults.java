//***********************************************************
//*
//* File:           IFCTournamentResults.java
//* Author:         Abhinav Kamra
//* Contact:        kamra-at-cs.columbia.edu
//* Update:         9.15.2003
//*
//* Description:    Tournament Results interface.
//*
//***********************************************************

package organisms.ui;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.TableModel;

public interface IFCTournamentResults extends TableModel, TableColumnModelListener
{ 
    public String   name()      throws Exception;
    public void Print()      throws Exception;

}
