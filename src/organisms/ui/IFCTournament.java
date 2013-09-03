//***********************************************************
//*
//* File:           Tournament.java
//* Author:         Abhinav Kamra
//* Contact:        kamra-at-cs.columbia.edu
//* Update:         9.13.2003
//*
//* Description:    Tournament configuration and results
//*                 storage inerface.
//*
//***********************************************************

package organisms.ui;

import java.io.Serializable;
import javax.swing.*;


public interface IFCTournament extends Serializable {

    public IFCGameRecord[]  games()                           throws Exception;
    public void             setGames(IFCGameRecord[] __games) throws Exception;
}
