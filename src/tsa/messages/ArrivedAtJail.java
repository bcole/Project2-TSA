package tsa.messages;

import akka.actor.ActorRef;

public class ArrivedAtJail {

	public final ActorRef passenger;
	
	public ArrivedAtJail(ActorRef passenger) {
		this.passenger = passenger;
	}
}
