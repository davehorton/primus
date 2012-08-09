package com.beachdog.primusCore;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

public class Utilities {

	public static class M6Credential {
		public String address ;
		public String password ;
		public String username ;
	}
	
	protected static Logger logger = Logger.getLogger(Utilities.class) ;

	
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
    
	public static String getLocalHost() {
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
	
	public static boolean sendMail(String emailRecipients, String emailServerAddress, String subject, String body, File attachment)  {
		Properties props = new Properties();
		
		props.setProperty("mail.transport.protocol", "smtp"); 
		props.setProperty("mail.smtp.host", emailServerAddress); 
		props.setProperty("mail.smtp.auth", "false") ;
		
		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(true) ;
		MimeMessage message = new MimeMessage(session);
		
		try {
			message.setSubject(subject);
			
			Address from = new InternetAddress("noreply@primustel.ca");
			message.setFrom(from) ;
			
			String[] people = emailRecipients.split(",");
			for( String s : people ) {
				Address to = new InternetAddress(s) ;
				message.addRecipient(Message.RecipientType.TO, to) ;
			}
			
			if( null != attachment ) {
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setText(body);
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart);
				messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(attachment.getAbsolutePath());
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(attachment.getName());
				multipart.addBodyPart(messageBodyPart);
				message.setContent(multipart);
			}
			else {
				message.setContent(body, "text/plain");				
			}
			
			message.saveChanges(); 
			Transport transport = session.getTransport("smtp");
			transport.connect(emailServerAddress, null, null);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			
		} catch (MessagingException e) {
			logger.error("Error sending mail message") ;
			logger.error( e ) ;
			return false ;
		}	
		return true ;
	}
	
	public static M6Credential getM6Credential( Config cfg, String phoneNumber ) {
		M6Credential c = new M6Credential() ;
		
		if( 0 == phoneNumber.indexOf("416") || 0 == phoneNumber.indexOf("647") ) {
			c.address = cfg.getM6Address() ;
			c.username = cfg.getM6User() ;
			c.password = cfg.getM6Password() ;
		}
		else {
			c.address = cfg.getM6Address2() ;
			c.username = cfg.getM6User2() ;
			c.password = cfg.getM6Password2() ;			
		}
		
		return c ;
	}
}
