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

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * The <code>PCSLog4jLogger</code> is a concrete implementation of the <code>PCSLog</code> interface.  
 * There are five (5) levels of logging available:
 * 
 * <ul>
 * <li>DEBUG
 * <li>INFO
 * <li>WARN
 * <li>ERROR
 * <li>FATAL
 * </ul>
 * 
 * <p>These 5 levels directly relate to the named levels within the log4j logger and as such, this class is an adapter
 * to the log4j logger class.  By default it will configure itself using the log4j.properties file located in:
 * 
 *  /opt/pcs/config/log4j.properties
 * 
 *  A different file/locations can be specified by calling the setLog4jProperties method and passing in the appropriate value.
 *  
 /* $Id: PCSLog4jLogger.java 959 2008-06-04 14:52:33Z erobbins $ 
 *
 */


public class Log4jLogger implements Log {

  private final String FQN = "org.sipdev";
  private String log4jProperties = (ClassLoader.getSystemResource("log4j.properties") == null)? "":ClassLoader.getSystemResource("log4j.properties").toExternalForm(); 
  
  public Log4jLogger() {
    
  } 
  
  /**
   * Allows complete decoupling from spring.  Spring will call this method after the object is realized and all setters
   * defined in the beans.xml file have been called.
   * 
   *
   */
  public void init() {
	  if( log4jProperties.length() > 5 && 0 == log4jProperties.indexOf("file:/") ) {
		  /* Strip file:/ from the string and set  the thread sleep time to 30 seconds */
		  PropertyConfigurator.configureAndWatch(log4jProperties.substring(5),30);			
	  }																													
  } // init
  
  /**
   * @param int The level at which the message will be logged.
   * @parm The message to log
   */
  public void log(int level, Object message) {
    log(level,message,FQN);
  }

  /**
   * @param int The level at which the message will be logged.
   * @parm The message to log
   * @param The exception to log
   */
  public void log(int level, Object message, Throwable th) {
    log(level,message,th,FQN);
  }

  /**
   * @param The level at which the message will be logged.
   * @param The message to log
   * @param The identifier used to denote which logger to use.  See log4j.properties file
   */
  public void log(int level, Object message, String identifier) {
    switch(level) {
    case Log.DEBUG:
      Logger.getLogger(identifier).debug(message);
      break;
    case Log.INFO:
      Logger.getLogger(identifier).info(message);
      break;
    case Log.WARN:
      Logger.getLogger(identifier).warn(message);
      break;
    case Log.ERROR:
      Logger.getLogger(identifier).error(message);
      break;
    case Log.FATAL:
      Logger.getLogger(identifier).fatal(message);
      break;
    default:
      Logger.getLogger(identifier).debug(message);
    
    } // switch
  } // log(level,message,identifier)

  /**
   * @param The level at which the message will be logged.
   * @param The message to log
   * @param The exception to log
   * @param The identifier used to denote which logger to use.  See log4j.properties file
   */
  public void log(int level, Object message, Throwable th, String identifier) {
   switch(level) {
     case Log.DEBUG:
       Logger.getLogger(identifier).debug(message,th);
       break;
     case Log.INFO:
       Logger.getLogger(identifier).info(message,th);
       break;
     case Log.WARN:
       Logger.getLogger(identifier).warn(message,th);
       break;
     case Log.ERROR:
       Logger.getLogger(identifier).error(message,th);
       break;
     case Log.FATAL:
       Logger.getLogger(identifier).fatal(message,th);
       break;
     default:
       Logger.getLogger(identifier).debug(message,th);
         
   } // switch
    
  } // log(level,message,throwable,identifier)

  /**
   * 
   * @return A string representing the path to the configured log4j.properties file
   */
  public String getLog4jProperties() {
    return log4jProperties;
  }

  /**
   * 
   * @param log4jProperties 
   */
  public void setLog4jProperties(String log4jProperties) {
    this.log4jProperties = log4jProperties;
  }

public void debug(Object message, Throwable th, String identifier) {
	this.log(DEBUG,message,th,identifier);
}

public void debug(Object message, String identifier) {
	this.log(DEBUG,message,identifier);
}

public void debug(Object message) {
	this.log(DEBUG,message);
}

public void debug(Object message, Throwable th) {
	this.log(DEBUG,message,th);
}

public void error(Object message, Throwable th, String identifier) {
	this.log(ERROR,message,th,identifier);
}

public void error(Object message, String identifier) {
	this.log(ERROR,message,identifier);
}

public void error(Object message) {
	this.log(ERROR,message);
}

public void error(Object message, Throwable th) {
	this.log(ERROR,message,th);
}

public void fatal(Object message, Throwable th, String identifier) {
	this.log(FATAL,message,th,identifier);
	
}

public void fatal(Object message, String identifier) {
	this.log(FATAL,message,identifier);
	
}

public void fatal(Object message) {
	this.log(FATAL,message);
	
}

public void fatal(Object message, Throwable th) {
	this.log(FATAL,message,th);
	
}

public void info(Object message, Throwable th, String identifier) {
	this.log(INFO,message,th,identifier);
	
}

public void info(Object message, String identifier) {
this.log(INFO,message,identifier);
}

public void info(Object message) {
this.log(INFO,message);
}

public void info(Object message, Throwable th) {
this.log(INFO,message,th);
}

public void warn(Object message, Throwable th, String identifier) {
	this.log(WARN,message,th,identifier);
}

public void warn(Object message, String identifier) {
	this.log(WARN,message,identifier);
}

public void warn(Object message) {
this.log(WARN,message);
}

public void warn(Object message, Throwable th) {
this.log(WARN,message,th);
}

} // PCSLog4jLogger
