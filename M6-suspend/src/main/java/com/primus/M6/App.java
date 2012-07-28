package com.primus.M6;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Date;
import java.util.Hashtable;

import com.vocaldata.AdminSOAP.DBSOAPException;
import com.vocaldata.AdminSOAP.UserKeys;
import com.vocaldata.AdminSOAP.VDAdmin;
import com.vocaldata.AdminSOAP.WizardTypeCode;

public class App {

	protected String M6Address ;
	protected String M6User ;
	protected String M6Password ;
	protected String phoneNumber ;
	protected Boolean suspend ;
	
	public static void main(String[] args) {
		App a = new App() ;
		a.run( args ) ;
	}

	protected void run( String[] args ) {

		if( !parseCommandLine( args ) ) {
			usage() ;
			return ;
		}

		String host = getHost() ;

			try {
				System.out.println("Creating VDAdmin object for remote server " + M6Address) ;
    			VDAdmin vdadmin = new VDAdmin( M6Address ) ;
    			System.out.println("authenticating user: " + M6User + ", password: " + M6Password + " with local host name " + host) ;
    			String sessionOid = vdadmin.authenticate(M6User, M6Password, host);
    			if( null == sessionOid ) {
    				System.err.println("Failure logging into M6 using username '" + M6User + "' and password '" + M6Password + "'; program will terminate");
    				return ;
    			}
    			System.out.println("Session OID is " + sessionOid) ;
    			System.out.println("Looking up subscriber for phone number: " + phoneNumber ) ;
    			String customerOid = vdadmin.getUserByExtension(sessionOid, "", phoneNumber) ;
    			if( null == customerOid ) {
           			System.err.println("subscriber lookup failed on M6 by phone number: " + phoneNumber ) ;	      
           			return ;
    			}
    			System.out.println("Subscriber OID is " + customerOid) ;
    			
    			
    			System.out.println("now " + (suspend ? "suspending" : "unsuspending") + " account..") ;
    	        
    			Hashtable values = new Hashtable();
    	        values.put( UserKeys.SUSPEND_SERVICE, suspend );
    	        vdadmin.modify(sessionOid, WizardTypeCode.USER, customerOid, values);

    	        System.out.println("account successfully modified") ;
    	        

			
			} catch (DBSOAPException dbe ) {
				System.err.println("Error attempting to suspend subscriber on M6, program will terminate") ;
				dbe.printStackTrace() ;
			}

	}
	
	protected void usage() {
		System.out.println("M6-suspend -M6User user -M6Password pass -M6Address address -phoneNumber phone") ;
	}

	protected boolean parseCommandLine( String[] args ) {
		boolean processingPhone = false ;
		boolean processingM6Address = false ;
		boolean processingM6User = false ;
		boolean processingM6Pass = false ;
		
       	for( String s : args ) {
			if( processingM6User ) {
				this.M6User = s ;
				processingM6User = false ;
			}
			else if( processingM6Pass ) {
				this.M6Password = s ;
				processingM6Pass = false ;
			}
			else if( processingPhone ) {
				this.phoneNumber = s ;
				processingPhone = false ;
			}
			else if( processingM6Address ) {
				this.M6Address = s ;
				processingM6Address = false ;
			}
			else if( s.equalsIgnoreCase("-phoneNumber")) {
				processingPhone = true ;
			}
			else if( s.equalsIgnoreCase("-M6Address")) {
				processingM6Address = true ;
			}
			else if( s.equalsIgnoreCase("-M6User")) {
				processingM6User = true ;
			}
			else if( s.equalsIgnoreCase("-M6Password")) {
				processingM6Pass = true ;
			}
			else if( s.equalsIgnoreCase("-suspend")) {
				this.suspend = true ;
			}
			else if( s.equalsIgnoreCase("-unsuspend")) {
				this.suspend = false ;
			}
			
		}
       	
       	if( null == M6User || null == M6Password || null == M6Address || null == phoneNumber || null == suspend ) {
       		System.err.println("M6 user id and password are required parameters: -M6User username -M6Password password") ;      
       		return false ;
       	}
      
		
		return true ;
	}
	
	protected String getHost() {
		String host = null ;
		try {
		    InetAddress addr = InetAddress.getLocalHost();

		    // Get IP Address
		    byte[] ipAddr = addr.getAddress();

		    // Get hostname
		    host = addr.getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace() ;
		}
		return host ;
	}
}
