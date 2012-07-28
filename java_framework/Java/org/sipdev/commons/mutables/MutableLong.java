package org.sipdev.commons.mutables ;

public class MutableLong {
 public MutableLong(long p_l) {
    l = p_l;
 }
 public MutableLong(){}
 public long l;
 public void setLong(long p_f) { l = p_f; }
  public long getLong(){ return l; }
 public long longValue(){ return l; }    

}