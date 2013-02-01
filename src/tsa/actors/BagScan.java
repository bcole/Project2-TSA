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
			// XXX: No simulation of being occupied.
			
			// Baggage randomly fails inspection with a probability of 20%.
			boolean passed = (Math.random() < 0.2);

			ScanBagResults resultsMessage = new ScanBagResults(passed);
			security.tell(resultsMessage);
		}
	}

}
