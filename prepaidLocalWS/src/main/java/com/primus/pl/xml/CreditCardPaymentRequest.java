package com.primus.pl.xml;

public class CreditCardPaymentRequest {

	private Requestor requestor ;
	private Subscriber subscriber ;
	private CreditCard card ;
	
	public Requestor getRequestor() {
		return requestor;
	}
	public void setRequestor(Requestor requestor) {
		this.requestor = requestor;
	}
	public Subscriber getSubscriber() {
		return subscriber;
	}
	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}
	public CreditCard getCard() {
		return card;
	}
	public void setCard(CreditCard card) {
		this.card = card;
	}
	
}
