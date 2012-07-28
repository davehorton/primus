package org.sipdev.commons.mutables ;

public class MutableShort {
 public MutableShort(short sh) {
    s = sh;
 }
 MutableShort(){}
 
 public short s;
 public void setShort(short p_num) {s = p_num; } 
 public short getShort(){ return s; }
 public short shortValue(){ return s; }
}
