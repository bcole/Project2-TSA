package tsa.actors;

import tsa.messages.ScanBag;
import tsa.messages.ScanBagResults;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class BagScan extends UntypedActor {
	
	private final ActorRef security;
	
	public BagScan(ActorRef security) {
		this.security = security;
	}

	@Override
	public void onReceive(Object message) {
		if (message instanceof ScanBag) {
			// Baggage randomly fails inspection with a probability of 20%.
			boolean passed = (Math.random() < 0.8);
			ActorRef passenger = ((ScanBag) message).passenger;

			ScanBagResults resultsMessage = 
					new ScanBagResults(passenger, passed);
			security.tell(resultsMessage);
		}
	}

}
