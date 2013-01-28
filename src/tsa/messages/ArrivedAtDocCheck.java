package tsa.messages;

import akka.actor.ActorRef;

public class ArrivedAtDocCheck {

	public final ActorRef passenger; 
	
	public ArrivedAtDocCheck(ActorRef passenger) {
		this.passenger = passenger;
	}
}
