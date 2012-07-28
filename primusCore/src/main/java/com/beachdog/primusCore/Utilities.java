package com.beachdog.primusCore;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;

public class Utilities {
    /*
     * This method parses the prepaid serial number passed in into 
     * the lot_control_number and lot_seq_number.  The last 8 digits are
     * assumed to be the lot_seq_number, remaining digits are the lot_control_number.
     **/
    public static void parseSubSerialNumber( String p_sSerialNumber,
            StringBuffer p_sLotControlNumber,
            StringBuffer p_sLotSeqNumber) throws Exception {

        p_sLotControlNumber.setLength(0);
        p_sLotSeqNumber.setLength(0) ;

        if (p_sSerialNumber.length() < 11) {
            throw new Exception("insufficient digits in serial number <" + p_sSerialNumber + ">");
        } else {
        	p_sLotSeqNumber.append(p_sSerialNumber.substring(p_sSerialNumber.length() - 8));
            p_sLotControlNumber.append(p_sSerialNumber.substring(0, p_sSerialNumber.length() - 8));
        }
    }
    
    public static Timestamp getCurrentTimestamp() {
    	return new java.sql.Timestamp((new java.util.Date()).getTime()) ;
    }
    
	protected static String getHost() {
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
