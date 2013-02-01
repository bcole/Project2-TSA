package tsa.messages;

import akka.actor.ActorRef;

public class ScanBody {

	public final ActorRef passenger;
	
	public ScanBody(ActorRef passenger) {
		this.passenger = passenger;
	}
}
