package org.sipdev.commons.mutables ;

import java.io.Serializable;

public class MutableInt implements Serializable{
 public MutableInt(int i) {
    num = i;
 }
 public MutableInt(){}
 
 public int num;
 public void setInteger(int p_num) {num = p_num; }
 public int getInteger(){ return num; }
 public void setInt(int p_num) {num = p_num; }
 public int getInt(){ return num; }
 public int intValue(){ return num; }
}
