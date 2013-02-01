package tsa.messages;

import akka.actor.ActorRef;

public class ArrivedAtQueue {

	public final ActorRef passenger;
	
	public ArrivedAtQueue(ActorRef passenger) {
		this.passenger = passenger;
	}
}
