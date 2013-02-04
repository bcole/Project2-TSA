package tsa.actors;

import tsa.messages.ActorTerminate;
import tsa.messages.ArrivedAtDocCheck;
import tsa.messages.ArrivedAtQueue;
import tsa.messages.FailedDocCheck;
import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;

public class DocumentCheck extends UntypedActor {
	
	private int nextQueueIndex = 0;

	private final ActorRef[] queues;
	
	public DocumentCheck(ActorRef[] queues) {
		this.queues = queues;
	}

	@Override
	public void onReceive(Object message) {
		if (message instanceof ArrivedAtDocCheck) {
			ActorRef passenger = ((ArrivedAtDocCheck) message).passenger;
			
			// XXX: Passengers are randomly turned away for document problems at
			//      a probability of 20%.
			if (Math.random() < 0.2) {
				System.out.println(passenger.getId() + ": Failed DocumentCheck");
				FailedDocCheck failedMessage = new FailedDocCheck();
				passenger.tell(failedMessage);
			} else {
				System.out.println(passenger.getId() + ": Passed DocumentCheck");
				ArrivedAtQueue arrivedMessage = new ArrivedAtQueue(passenger);
				queues[nextQueueIndex].tell(arrivedMessage);
				
				// Set index for next queue.
				if (nextQueueIndex == queues.length - 1) {
					nextQueueIndex = 0;
				} else {
					nextQueueIndex++;
				}
			}
		}
		
		// Message to terminate and actor terminates itself. 
		if (message instanceof ActorTerminate) { 
			this.getContext().tell(Actors.poisonPill());
		}
	}
}
