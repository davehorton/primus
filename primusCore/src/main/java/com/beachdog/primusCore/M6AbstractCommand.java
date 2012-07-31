package com.beachdog.primusCore;

import java.util.Hashtable;

import org.apache.log4j.Logger;

import com.vocaldata.AdminSOAP.DBSOAPException;
import com.vocaldata.AdminSOAP.VDAdmin;

public class M6AbstractCommand {

	protected static Logger logger =Logger.getLogger(M6AbstractCommand.class) ;

	protected String M6User ;
	protected String M6Password ;
	protected String M6Address ;
	protected Hashtable values = new Hashtable<Object,Object>() ;
	protected String sessionOid ;
	protected String localHost ;
	protected VDAdmin vdadmin ;
	protected Boolean armed ;
	
	public M6AbstractCommand( String M6Address, String M6User, String M6Password, String localHost ) throws DBSOAPException {
		this.M6Address = M6Address ;
		this.M6User = M6User ;
		this.M6Password = M6Password ;
		this.armed = false ;
		this.localHost = localHost ;

		this.vdadmin = new VDAdmin( M6Address ) ;
		sessionOid = vdadmin.authenticate(M6User, M6Password, localHost);
		armed = true; 
	}
		
	
	public String getM6User() {
		return M6User;
	}
	public void setM6User(String m6User) {
		M6User = m6User;
	}
	public String getM6Password() {
		return M6Password;
	}
	public void setM6Password(String m6Password) {
		M6Password = m6Password;
	}
	public String getM6Address() {
		return M6Address;
	}
	public void setM6Address(String m6Address) {
		M6Address = m6Address;
	}
	public Hashtable getValues() {
		return values;
	}


	public void setValues(Hashtable values) {
		this.values = values;
	}


	public String getSessionOid() {
		return sessionOid;
	}


	public void setSessionOid(String sessionOid) {
		this.sessionOid = sessionOid;
	}
	

}
