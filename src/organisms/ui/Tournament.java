//***********************************************************
//*
//* File:           Tournament.java
//* Author:         Abhinav Kamra
//* Contact:        kamra-at-cs.columbia.edu
//* Update:         9.13.2003
//*
//* Description:    Tournament configuration and results
//*                 storage class.
//*
//***********************************************************

package organisms.ui;

public final class Tournament implements IFCTournament {
    
    IFCGameRecord[] _games;

    public void setGames(IFCGameRecord[] __games) throws Exception {
        int _MAX = __games.length;
        _games = new IFCGameRecord[_MAX];
        
        System.arraycopy(__games, 0, _games, 0, _MAX);
    }
    
    public IFCGameRecord[] games() throws Exception {
        int _MAX = _games.length;
        IFCGameRecord[] RET = new IFCGameRecord[_MAX];

        System.arraycopy(_games, 0, RET, 0, _MAX);
        return RET;
    }
}
