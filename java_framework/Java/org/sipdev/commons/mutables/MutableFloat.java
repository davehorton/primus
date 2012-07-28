package org.sipdev.commons.mutables ;

public class MutableFloat {
 public MutableFloat(float p_f) {
    f = p_f;
 }
 public MutableFloat(){}
 public float f;
 public void setFloat(float p_f) {f = p_f; } 
 public float getFloat(){ return f; } 
 public float floatValue(){ return f; }
}
