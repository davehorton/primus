package org.sipdev.commons.mutables ;

public class MutableDouble {
 public MutableDouble(double p_d) {
    d = p_d;
 }
 public MutableDouble(){}
 public double d;
 public void setDouble(double p_d) {d = p_d; } 
 public double getDouble(){ return d; } 
 public double doubleValue(){ return d; }
}
