package tsa.actors;

import tsa.messages.ScanBagResults;
import tsa.messages.ScanBodyResults;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Security extends UntypedActor {

	private boolean scanBagResultsPassed;
	private boolean scanBagResultsReceived;
	private boolean scanBodyResultsPassed;
	private boolean scanBodyResultsReceived;
	
	private ActorRef currentPassenger;

	@Override
	public void onReceive(Object message) {
		// XXX: No queue implemented.  This doesn't work.

		if (message instanceof ScanBagResults) {
			scanBagResultsReceived = true;
			scanBagResultsPassed = ((ScanBagResults) message).passed;
			
			// XXX: Do we need to respond immediately if a scan fails, or wait?
			if (scanBodyResultsReceived) {
				respondToScanResults();
			}
		} else if (message instanceof ScanBodyResults) {
			scanBodyResultsReceived = true;
			scanBodyResultsPassed = ((ScanBodyResults) message).passed;
			
			if (scanBagResultsReceived) {
				respondToScanResults();
			}
		}
	}
	
	private void respondToScanResults() {
		if (scanBagResultsPassed && scanBodyResultsPassed) {
			System.out.println("Passenger passed tests");
		} else {
			System.out.println("Passenger failed tests");
		}
	}

}
