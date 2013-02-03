package tsa.messages;

import akka.actor.ActorRef;

public class ScanBagResults {
	
	public final ActorRef passenger;
	public final boolean passed;
	
	// XXX: Passenger has to get to Security somehow.
	public ScanBagResults(ActorRef passenger, boolean passed) {
		this.passenger = passenger;
		this.passed = passed;
	}

}
