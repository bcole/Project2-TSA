package tsa.actors;

import tsa.messages.ActorTerminate;
import tsa.messages.ArrivedAtDocCheck;
import tsa.messages.FailedDocCheck;
import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;


public class Passenger extends UntypedActor {
	
	private final int number;
	private final ActorRef documentCheck;
	
	// XXX: Should the DocumentCheck be a constructor argument or sent in a
	//      start message?
	public Passenger(int number, ActorRef documentCheck) {
		this.number = number;
		this.documentCheck = documentCheck;
		this.getContext().setId("Passenger-" + Integer.toString(this.number));
	}
	
	@Override
	public void onReceive(Object message) {
		if (message instanceof FailedDocCheck) {
			System.out.println(this.getContext().getId() + 
					": Failed document check.");
			
			// XXX: Using poison pill prevents above log message from printing.
			//this.getContext().tell(Actors.poisonPill());
		}
		
		//Message to terminate and actor terminates itself. 
		if (message instanceof ActorTerminate) { 
			
			this.getContext().tell(Actors.poisonPill());
		}
	}
	
	@Override
	public void preStart() {
		ArrivedAtDocCheck message = new ArrivedAtDocCheck(this.getContext());
		documentCheck.tell(message);
	}
	
	@Override
	public String toString() {
		return ("Passenger-" + number);
	}
	
}
