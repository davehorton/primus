package com.beachdog.primusCallflow;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.sipdev.framework.Framework;
import org.sipdev.framework.Log;


public class AppTest {

	@Ignore
	@Test
	public void verifySomething() {
		Framework framework = null ;
		Log log = null ;
		try {
			framework = Framework.getInstance() ;
			log = framework.getLogger() ;
			SessionFactory sessionFactory = (SessionFactory) framework.getResource("sessionFactory"); 
			Session session = sessionFactory.openSession() ;
			
			session.close() ;			
			
		} catch( Exception e ) {
			e.printStackTrace() ;
			org.junit.Assert.fail() ;
		}
	}

}
