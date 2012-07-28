package com.beachdog.primusCore;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.sipdev.framework.Framework;
import org.sipdev.framework.Log;

import com.beachdog.primusCore.model.Subscriber;

public class AppTest {

	@Ignore
	@Test
	public void verifyHibernateMappings() {
		Framework framework = null ;
		Log log = null ;
		try {
			framework = Framework.getInstance() ;
			log = framework.getLogger() ;
			SessionFactory sessionFactory = (SessionFactory) framework.getResource("sessionFactory"); 
			Session session = sessionFactory.openSession() ;
			
			/* make sure we can retrieve all of the pojos from the database */
			List<Subscriber> listSub = session.createQuery("from Subscriber").list() ;
			System.out.println("Retrieved " + listSub.size() + " Subscribers from test database") ;
			
					
			session.close() ;
			
			
		} catch( Exception e ) {
			e.printStackTrace() ;
			org.junit.Assert.fail() ;
		}
	}

}
