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
$Id: PCSPoolableDataSource.java 598 2006-12-22 21:28:45Z erobbins $
 */

package org.sipdev.framework;

import java.sql.Connection;
import java.sql.SQLException;


/**
 *  This interface describes the method used by the Pactolus PCS Framework for managing pooled database connections.
 *  
 *  The framework comes with a default pooling mechanism using the Apache Jakarta Commons DBCP pooling mechanism.
 *  If you wish to implement your own pooling mechanism then you must implement this interface and define your class within the
 *  beans definition file for the framework.
 *  
 * @author Ed Robbins
 *
 */
public interface PoolableDataSource {
  public void setPoolName(String poolName);
  public void setDriverName(String driverName);
  public void setHost(String host);
  public void setUsername(String username);
  public void setPassword(String password);
  public void setPort(int port);
  public void setConnectionURL(String url);
  public void setValidationQuery(String sqlString);
  public void setLogger(Log logger);
  public void createPool() throws FrameworkException;
  public Connection getConnection() throws SQLException;
  public void printStats();
} // interface PCSPoolableDataSource
/* $Id: PCSPoolableDataSource.java 598 2006-12-22 21:28:45Z erobbins $ */