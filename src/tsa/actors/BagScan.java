package tsa.actors;

import tsa.messages.ScanBag;
import tsa.messages.ScanBagResults;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class BagScan extends UntypedActor {
	
	private final int number;
	private final ActorRef security;
	
	public BagScan(int number, ActorRef security) {
		this.number = number;
		this.security = security;
		
		this.getContext().setId("BagScan-" + Integer.toString(this.number));
	}

	@Override
	public void onReceive(Object message) {
		if (message instanceof ScanBag) {
			// Baggage randomly fails inspection with a probability of 20%.
			boolean passed = (Math.random() < 0.8);
			ActorRef passenger = ((ScanBag) message).passenger;
			
			if (passed) {
				System.out.println(passenger.getId() + ": Passed BagScan-" + number);
			} else {
				System.out.println(passenger.getId() + ": Failed BagScan-" + number);
			}

			ScanBagResults resultsMessage = 
					new ScanBagResults(passenger, passed);
			security.tell(resultsMessage);
		}
	}

}
