package tsa.actors;

import tsa.messages.ArrivedAtDocCheck;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;


public class Passenger extends UntypedActor {
	
	private final ActorRef documentCheck;
	
	// XXX: Should the DocumentCheck be a constructor argument or sent in a
	//      start message?
	public Passenger(ActorRef documentCheck) {
		this.documentCheck = documentCheck;
	}
	
	@Override
	public void onReceive(Object object) {
		
	}
	
	@Override
	public void preStart() {
		ArrivedAtDocCheck message = new ArrivedAtDocCheck(getContext());
		documentCheck.tell(message);
	}
}
