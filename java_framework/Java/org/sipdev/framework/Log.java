/*
 * Copyright (c) Pactolus Communications Software, 2006

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, 
 publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the 
 Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.sipdev.framework;

/**
 * 
 * This interface represents the logging interface within the sipdev.org Application Framework.  Any logger
 * designed for this framework must implement this interface.
 *
 */
public interface Log {
  
  final static int DEBUG = 0;
  final static int INFO = 1;
  final static int WARN = 2;
  final static int ERROR = 3;
  final static int FATAL = 4;
  
  
  public void log(int level,Object message);
  public void log(int level,Object message,Throwable th);
  public void log(int level,Object message,String identifier);
  public void log(int level,Object message,Throwable th,String identifier);
  public void info(Object message, Throwable th,String identifier);
  public void info(Object message,String identifier);
  public void info(Object message);
  public void info(Object message, Throwable th);
  public void debug(Object message, Throwable th,String identifier);
  public void debug(Object message,String identifier);
  public void debug(Object message);
  public void debug(Object message,Throwable th);
  public void warn(Object message, Throwable th,String identifier);
  public void warn(Object message,String identifier);
  public void warn(Object message);
  public void warn(Object message, Throwable th);
  public void error(Object message, Throwable th,String identifier);
  public void error(Object message,String identifier);
  public void error(Object message);
  public void error(Object message,Throwable th);
  public void fatal(Object message, Throwable th,String identifier);
  public void fatal(Object message,String identifier);
  public void fatal(Object message);
  public void fatal(Object message, Throwable th);
  
} // PCSLog
/* $Id: PCSLog.java 618 2006-12-26 23:19:23Z erobbins $ */