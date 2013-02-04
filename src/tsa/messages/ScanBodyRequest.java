package tsa.messages;

import akka.actor.ActorRef;

public class ScanBodyRequest {
	public final ActorRef queue;
	
	public ScanBodyRequest(ActorRef queue) {
		this.queue = queue;
	}
}
