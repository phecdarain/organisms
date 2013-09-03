//***********************************************************
//*
//* File:           Movejava
//* Author:         Abhinav Kamra
//* Contact:        kamra-at-cs.columbia.edu
//* Update:         11.9.2003
//*
//* Description:    Basic Move object representing individaul
//*                 transformations of a polygon for Project
//*                 4, CS4444 Fall 2003.
//*
//***********************************************************

package organisms;

import java.io.Serializable;

public class Move implements Constants, Serializable {

	int _type;
	int _childpos;
	int _key;


    public Move(int __type) throws Exception {
        _type = __type;
    }

    public Move(int __type, int __childpos, int __key) throws Exception {
        _type = __type;
        _childpos = __childpos;
	_key = __key;
    }

    public int type() throws Exception {
        return _type;
    }

    public void setType(int t) throws Exception {
        _type = t;
    }

    public int childpos() throws Exception {
        return _childpos;
    }

    public int key() throws Exception {
        return _key;
    }

    public String toString() {
        try {
            StringBuffer SB = new StringBuffer();

	    switch(type())
	    {
		    case STAYPUT:
			    return new String("Stay Put");
		    case WEST:
			    return new String("Moving West");
		    case EAST:
			    return new String("Moving East");
		    case NORTH:
			    return new String("Moving North");
		    case SOUTH:
			    return new String("Moving South");
		    case REPRODUCE:
			    return new String("Reproducing");
	    }
	    return _CERROR_STRING;

        } catch (Exception EXC) {
            return _CERROR_STRING;
        }
    }
}
