//***********************************************************
//*
//* File:           IFCUI.java
//* Author:         Abhinav Kamra
//* Contact:        kamra-at-cs.columbia.edu
//* Update:         9.14.2003
//*
//* Description:    User-interface interface for all of
//*                 CS4444, Fall 2003 projects.  Minimalist
//*                 to permit pure console implementations.
//*
//***********************************************************

package organisms.ui;

public interface IFCUI {

    public void   register(IFCModel __model)             throws Exception;
    public void   refresh()                              throws Exception;
    public void   reset()                                throws Exception;
    public void   maximizeViewSize()                     throws Exception;
    public void   resetViewSize()                        throws Exception;
    public void   configure(IFCConfiguration __config)   throws Exception;
    public void   persist(Class __class, Object __obj)   throws Exception;
    public Object persist(Class __class)                 throws Exception;
    public void   print(String __str)                    throws Exception;
    public void   println(String __str)                  throws Exception;
    public void   println()                              throws Exception;
}
