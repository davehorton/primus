package org.sipdev.commons.mutables ;

public class MutableBoolean {
 public MutableBoolean(String str) {
    if (str.equals("true") || str.equals("TRUE")) {
        bool=true; 
        } else {
            bool = false ;
        }
 }
 public MutableBoolean(boolean b ) {bool = b;}
 public MutableBoolean(){}
 public boolean bool;
 public void setBoolean(boolean p_bool) {bool = p_bool; }
 public boolean getBoolean(){ return bool; }
 public boolean booleanValue() { return bool; }
  
 public String toString(){
        if (bool) {
        return("true"); 
        } else {
            return("false") ;
        }
 }
 public void setBoolean(String str) {
    if (str.equals("true") || str.equals("TRUE")) {
        bool=true; 
        } else {
            bool = false ;
        }
 }
 
 public void getBoolean(String str) {
    if (str.equals("true") || str.equals("TRUE")) {
        bool=true; 
        } else {
            bool = false ;
        }
 }
   
}
