//***********************************************************
//*
//* File:           ParseValue.java
//* Author:         Abhinav Kamra
//* Contact:        kamra-at-cs.columbia.edu
//* Update:         9.5.2003
//*
//* Description:    Utility class to validate textfield
//*                 values.
//*
//***********************************************************

package organisms.ui;

public final class ParseValue {
    
    Number  _value;
    boolean _valid;
     
    public void setValue(Number __value) {         
        _value = __value;
    }
        
    public Number value() {
        return _value;
    }
        
    public void setValidity(boolean __valid) {
        _valid = __valid;
    }
        
    public boolean isValid() {
        return _valid;
    }
        
    public static ParseValue parseIntegerValue(String __str, int __min, int __max) {
        ParseValue pv = new ParseValue();
        int value;
            
        try {                
            value = Integer.parseInt(__str);
            pv.setValue(new Integer(value));
                
            if (value >= __min && value <= __max) {
                pv.setValidity(true);
            } else {
                 pv.setValidity(false);
            }
            return pv;
                
        } catch (Exception EXC) {
            pv.setValidity(false);
            return pv;
        }        
    } 
        
    public static ParseValue parseDoubleValue(String __str, double __min, double __max) {
        ParseValue pv = new ParseValue();
        double value;
            
        try {
            value = Double.parseDouble(__str);
            pv.setValue(new Double(value));

            if (value >= __min && value <= __max) {
                pv.setValidity(true);                    
            } else {
                pv.setValidity(false);
            }                                 
            return pv;                
                
        } catch (Exception EXC) {
            pv.setValidity(false);
            return pv;
        }        
    }             
}           
