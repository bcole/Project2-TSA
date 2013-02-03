package tsa.messages;

import akka.actor.ActorRef;

public class ScanBag {

	public final ActorRef passenger;
	
	public ScanBag(ActorRef passenger) {
		this.passenger = passenger;
	}
}
