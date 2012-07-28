package org.sipdev.framework;

import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

/*  The PCSCommonsPoolableDataSource is a concrete implementation of the PCSPoolableDataSource interface for use
 *  in the Pactolus Applications Framework.  This class uses the Jakarta Commons DBCP pooling framework as it's underlying
 *  pooling mechanism.
 */
public class CommonsPoolableDataSource implements PoolableDataSource, DataSource {
  

  private PoolableConnectionFactory poolableConnectionFactory;
  private ConnectionFactory connectionFactory;
  private ObjectPool connectionPool;
  private PoolingDriver driver;
  private String poolName = null;
  private String driverName = null;
  private String host = null;
  private String username = null;
  private String password = null;
  private int port = 0;
  private String connectionURL = null;
  private String validationQuery = null;    // The SQL query that will be used to validate connections from this pool before returning them to the caller. If specified, this query MUST be an SQL SELECT statement that returns at least one row.
  private Log logger = null;
  private boolean autoCommitFlag = true; // defaultAutoCommitFlag
  private boolean readOnly = false; // defaultReadOnly flag
    
  
  
  //  Specific settings to Commons DBCP
  private int initialSize = 0;              //  The initial number of connections that are created when the pool is started. 
  private int maxActive = 9;            //   The maximum number of active connections that can be allocated from this pool at the same time, or negative for no limit.
  private int maxIdle = 9;                  //  The maximum number of active connections that can remain idle in the pool, without extra ones being released, or zero for no limit.
  private int minIdle =0;                 //     The minimum number of active connections that can remain idle in the pool, without extra ones being created, or zero to create none.
  private int maxWait  = -1;            //   The maximum number of milliseconds that the pool will wait (when there are no available connections) for a connection to be returned before throwing an exception, or -1 to wait indefinitely.
  
  private boolean testOnBorrow = true;  //  The indication of whether objects will be validated before being borrowed from the pool. If the object fails to validate, it will be dropped from the pool, and we will attempt to borrow another.
  private boolean testOnReturn = false; //  The indication of whether objects will be validated before being returned to the pool. 
  private boolean testWhileIdle = false;  //  The indication of whether objects will be validated by the idle object evictor (if any). If an object fails to validate, it will be dropped from the pool. 
  private int timeBetweenEvictionRunsMillis = -1;  //  The number of milliseconds to sleep between runs of the idle object evictor thread. When non-positive, no idle object evictor thread will be run.
  private int numTestsPerEvictionRun = 3;  //  The number of milliseconds to sleep between runs of the idle object evictor thread. When non-positive, no idle object evictor thread will be run.
  private int minEvictableIdleTimeMillis = 1000 * 60 * 30; //  The minimum amount of time an object may sit idle in the pool before it is eligable for eviction by the idle object evictor (if any).
  private boolean removeAbandoned = true;
  private int removeAbandonedTimeout = 300;
  private boolean logAbandoned = true;
  
  private final static String  loggingIdentifier = "org.sipdev.framework.commonspoolabledatasource";
  
  
  public void createPool() throws FrameworkException {
	 logger.log(Log.DEBUG,"Creating DB pool",loggingIdentifier);
    loadDriver();
    setupDriver();
  } // createPool
  
  public void close() {
	  
  }
  
  private void loadDriver() throws FrameworkException {
	 try {
      Class.forName("org.apache.commons.dbcp.PoolingDriver");
    } catch (ClassNotFoundException cnfe) {
      logger.log(Log.ERROR,"Could not load class org.apache.commons.dbcp.PoolingDriver",loggingIdentifier);
      throw new FrameworkException("Error loading class");
    } // try - catch
    try {
      Class.forName(this.getDriverName());
    } catch (ClassNotFoundException cnfe) {
      logger.log(Log.ERROR,"Could not load class: " + driverName,loggingIdentifier);
      throw new FrameworkException("Error loading class");
    } // try - catch
  } // loadDriver
  
  private  void setupDriver()  throws FrameworkException {
	// Build properties 
    Properties props = new Properties();
    props.setProperty("initialSize",String.valueOf(initialSize));
    props.setProperty("maxActive",String.valueOf(maxActive));
    props.setProperty("maxIdle",String.valueOf(maxIdle));
    props.setProperty("minIdle",String.valueOf(minIdle));
    props.setProperty("maxWait",String.valueOf(maxWait));
    if(validationQuery != null) {
      props.setProperty("validationQuery",validationQuery);
    }
    props.setProperty("testOnBorrow",String.valueOf(testOnBorrow));
    props.setProperty("testOnReturn",String.valueOf(testOnReturn));
    props.setProperty("testWhileIdle",String.valueOf(testWhileIdle));
    props.setProperty("timeBetweenEvictionRunsMillis",String.valueOf(timeBetweenEvictionRunsMillis));
    props.setProperty("numTestsPerEvictionRun",String.valueOf(numTestsPerEvictionRun));
    props.setProperty("minEvictableIdleTimeMillis",String.valueOf(minEvictableIdleTimeMillis));
    props.setProperty("logAbandonded",String.valueOf(logAbandoned));
    props.setProperty("removeAbandonedTimeout",String.valueOf(removeAbandonedTimeout));
    props.setProperty("removeAbandoned", String.valueOf(removeAbandoned));
    if(username != null) {
      props.setProperty("username",this.username);
      props.setProperty("user",this.username);
    }
    if(password != null) {
      props.setProperty("password",this.password);
    }
    
    GenericObjectPool.Config objectPoolConfig = new GenericObjectPool.Config();
    objectPoolConfig.maxActive = maxActive;
    objectPoolConfig.maxIdle = maxIdle;
    objectPoolConfig.minIdle = minIdle;
    objectPoolConfig.maxWait = maxWait;
    objectPoolConfig.testOnBorrow = testOnBorrow;
    objectPoolConfig.testOnReturn = testOnReturn;
    objectPoolConfig.testWhileIdle = testWhileIdle;
    objectPoolConfig.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    objectPoolConfig.numTestsPerEvictionRun = numTestsPerEvictionRun;
    objectPoolConfig.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    objectPoolConfig.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_FAIL;
    
    connectionFactory = new DriverManagerConnectionFactory(connectionURL,props);
    connectionPool = new GenericObjectPool(null,objectPoolConfig);
    poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,connectionPool,null,validationQuery != null?validationQuery:null,this.readOnly,this.autoCommitFlag);
    connectionPool.setFactory(poolableConnectionFactory);
    
    try {
      driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
    } catch (SQLException exc) {
      logger.log(Log.ERROR,"Error getting pooling driver manager:  jdbc.apache.commons.dbcp",exc,loggingIdentifier);
      throw new FrameworkException("Error in setupDriver");
    } // try - catch
    
    driver.registerPool(poolName,connectionPool);
    
  } //setupDriver
  
  
  /**
   *  Method used to get a connection from the pool.
   *  
   *  @return <code>Connection</code>  - An available Connection from the pool.
   */
  public Connection getConnection() throws SQLException {
	logger.log(Log.DEBUG, "Retrieving connection from DriverManager.");
    Connection returnConnection = DriverManager.getConnection("jdbc:apache:commons:dbcp:" + poolName);
    returnConnection.setAutoCommit(this.autoCommitFlag);
    this.printStats();
    
    return returnConnection;
  } // getConnection
  
  /**
   * Method to print the number of active and idle connections in the pool
   */
  public void printStats() {
    logger.log(Log.DEBUG,"Stats for pool: "  + poolName,loggingIdentifier);
    logger.log(Log.DEBUG,"NumActive: " + connectionPool.getNumActive(),loggingIdentifier);
    logger.log(Log.DEBUG,"Num Idle: " + connectionPool.getNumIdle(),loggingIdentifier);
  } // printStats
  
  //  ********************************   Methods needed to Satisfy   PCSPoolableDataSource contract ******************************
  public String getConnectionURL() {
    return connectionURL;
  }
  public void setConnectionURL(String connectionURL) {
    this.connectionURL = connectionURL;
  }
  public String getDriverName() {
    return driverName;
  }
  public void setDriverName(String driverName) {
    this.driverName = driverName;
  }
  public String getHost() {
    return host;
  }
  public void setHost(String host) {
    this.host = host;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public String getPoolName() {
    return poolName;
  }
  public void setPoolName(String poolName) {
    this.poolName = poolName;
  }
  public int getPort() {
    return port;
  }
  public void setPort(int port) {
    this.port = port;
  }
  public String getUsername() {
    return username;
  }
  public void setUsername(String userName) {
    this.username = userName;
  }
  public void setLogger(Log logger) {
    this.logger = logger;
  }
  public Log getLogger() {
    return logger;
  }
  public void setValidationQuery(String validationQuery) {
    this.validationQuery = validationQuery;
  }
  /****************************  End of PCSPoolableDataSource contract *******************************/
  /********************  DBCP Commons specific properties to set   ************************/
  
  
  public int getInitialSize() {
    return initialSize;
  }
  public void setInitialSize(int initialSize) {
    this.initialSize = initialSize;
  }
  public int getMaxActive() {
    return maxActive;
  }
  public void setMaxActive(int maxActive) {
    this.maxActive = maxActive;
  }
  public int getMaxIdle() {
    return maxIdle;
  }
  public void setMaxIdle(int maxIdle) {
    this.maxIdle = maxIdle;
  }
  public int getMaxWait() {
    return maxWait;
  }
  public void setMaxWait(int maxWait) {
    this.maxWait = maxWait;
  }
  public int getMinEvictableIdleTimeMillis() {
    return minEvictableIdleTimeMillis;
  }
  public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
    this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
  }
  public int getMinIdle() {
    return minIdle;
  }
  public void setMinIdle(int minIdle) {
    this.minIdle = minIdle;
  }
  public int getNumTestsPerEvictionRun() {
    return numTestsPerEvictionRun;
  }
  public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
    this.numTestsPerEvictionRun = numTestsPerEvictionRun;
  }
  public boolean isTestOnBorrow() {
    return testOnBorrow;
  }
  public void setTestOnBorrow(boolean testOnBorrow) {
    this.testOnBorrow = testOnBorrow;
  }
  public boolean isTestOnReturn() {
    return testOnReturn;
  }
  public void setTestOnReturn(boolean testOnReturn) {
    this.testOnReturn = testOnReturn;
  }
  public boolean isTestWhileIdle() {
    return testWhileIdle;
  }
  public void setTestWhileIdle(boolean testWhileIdle) {
    this.testWhileIdle = testWhileIdle;
  }
  public int getTimeBetweenEvictionRunsMillis() {
    return timeBetweenEvictionRunsMillis;
  }
  public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
    this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
  }
  public String getValidationQuery() {
    return validationQuery;
  }
  public boolean isAutoCommitFlag() {
    return autoCommitFlag;
  }
  public void setAutoCommitFlag(boolean autoCommitFlag) {
    this.autoCommitFlag = autoCommitFlag;
  }


public boolean isLogAbandoned() {
	return logAbandoned;
}


public void setLogAbandoned(boolean logAbandoned) {
	this.logAbandoned = logAbandoned;
}


public boolean isRemoveAbandoned() {
	return removeAbandoned;
}


public void setRemoveAbandoned(boolean removeAbandoned) {
	this.removeAbandoned = removeAbandoned;
}


public int getRemoveAbandonedTimeout() {
	return removeAbandonedTimeout;
}


public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
	this.removeAbandonedTimeout = removeAbandonedTimeout;
}


public Connection getConnection(String username, String password) throws SQLException {
	return this.getConnection();
}


public PrintWriter getLogWriter() throws SQLException {
	// TODO Auto-generated method stub
	return null;
}


public int getLoginTimeout() throws SQLException {
	// TODO Auto-generated method stub
	return 0;
}


public void setLogWriter(PrintWriter out) throws SQLException {
	// TODO Auto-generated method stub
	
}


public void setLoginTimeout(int seconds) throws SQLException {
	// TODO Auto-generated method stub
	
}


public boolean isReadOnly() {
	return readOnly;
}


public void setReadOnly(boolean readOnly) {
	this.readOnly = readOnly;
}


public boolean isWrapperFor(Class<?> iface) throws SQLException {
	// TODO Auto-generated method stub
	return false;
}


public <T> T unwrap(Class<T> iface) throws SQLException {
	// TODO Auto-generated method stub
	return null;
}
  

} // PCSCommonsPoolableDataSource
