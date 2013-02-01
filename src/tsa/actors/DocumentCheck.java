package tsa.actors;

import tsa.messages.ArrivedAtDocCheck;
import tsa.messages.FailedDocCheck;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class DocumentCheck extends UntypedActor {

	@Override
	public void onReceive(Object message) {
		if (message instanceof ArrivedAtDocCheck) {
			ActorRef passenger = ((ArrivedAtDocCheck) message).passenger;
			
			// XXX: Passengers are randomly turned away for document problems at
			//      a probability of 20%.
			if (Math.random() < 0.2) {
				FailedDocCheck failedMessage = new FailedDocCheck();
				passenger.tell(failedMessage);
				System.out.println("Passenger failed doc check");
			} else {
				System.out.println("Passenger passed doc check");
			}
		}
	}

}
