package com.beachdog.primusCore;

import com.vocaldata.AdminSOAP.DBSOAPException;
import com.vocaldata.AdminSOAP.WizardTypeCode;

public class M6ModifyUserCommand extends M6AbstractCommand {
	
	private String customerOid ;
	private String phoneNumber ;

	public M6ModifyUserCommand(String M6Address, String M6User,
			String M6Password, String localHost, String phoneNumber) throws DBSOAPException {
		super(M6Address, M6User, M6Password, localHost);
		this.phoneNumber = phoneNumber ;
		
		this.customerOid = vdadmin.getUserByExtension(sessionOid, "", phoneNumber) ;
	}
	
	public void setValue( Object key, Object value ) {
		values.put(key, value) ;
	}
	
	public void execute() throws DBSOAPException {
		vdadmin.modify(sessionOid, WizardTypeCode.USER, customerOid, values);
	}

}
