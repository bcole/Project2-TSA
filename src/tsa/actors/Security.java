package tsa.actors;

import java.util.HashMap;
import java.util.Map;

import tsa.messages.LogMessage;
import tsa.messages.ScanBagResults;
import tsa.messages.ScanBodyResults;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Security extends UntypedActor {
	
	// XXX: Does this need to be thread safe if it is contained in this Actor?
	private Map<ActorRef, ScanResults> resultsMap 
			= new HashMap<ActorRef, ScanResults>();

	@Override
	public void onReceive(Object message) {
		// XXX: Do we need to respond immediately if a scan fails, or wait?
		
		if (message instanceof ScanBagResults) {
			ScanBagResults resultsMessage = (ScanBagResults) message;
			ActorRef passenger = resultsMessage.passenger;
			boolean passed = resultsMessage.passed;
			
			if (!resultsMap.containsKey(passenger)) {
				// No scan results received.
				ScanResults scanResults = new ScanResults();
				scanResults.setScanBagResultsPassed(passed);
				resultsMap.put(passenger, scanResults);
			} else {
				// Other scan result already received.
				resultsMap.get(passenger).setScanBagResultsPassed(passed);
				respondToScanResults(passenger);
			}
		} else if (message instanceof ScanBodyResults) {
			ScanBodyResults resultsMessage = (ScanBodyResults) message;
			ActorRef passenger = resultsMessage.passenger;
			boolean passed = resultsMessage.passed;
			
			if (!resultsMap.containsKey(passenger)) {
				// No scan results received.
				ScanResults scanResults = new ScanResults();
				scanResults.setScanBodyResultsPassed(passed);
				resultsMap.put(passenger, scanResults);
			} else {
				// Other scan result already received.
				resultsMap.get(passenger).setScanBodyResultsPassed(passed);
				respondToScanResults(passenger);
			}
		}
	}
	
	private void respondToScanResults(ActorRef passenger) {
		ScanResults scanResults = resultsMap.remove(passenger);
		
		if (scanResults.getScanBagResultsPassed()) {
			passenger.tell(new LogMessage("Passed bag scan."));
		} else {
			passenger.tell(new LogMessage("Failed bag scan."));
		}
		
		if (scanResults.getScanBodyResultsPassed()) {
			passenger.tell(new LogMessage("Passed body scan."));
		} else {
			passenger.tell(new LogMessage("Failed body scan."));
		}
		
		if (scanResults.getScanBagResultsPassed()) {
			passenger.tell(new LogMessage("Passed security."));
		} else {
			passenger.tell(new LogMessage("Failed security."));
		}
	}
	
	/**
	 * Helper class to store bag scan and body scan results together for
	 * associating with a Passenger as a single object.
	 */
	private class ScanResults {
		private boolean scanBagResultsPassed;
		private boolean scanBodyResultsPassed;
		
		public boolean getScanBagResultsPassed() {
			return scanBagResultsPassed;
		}
		
		public boolean getScanBodyResultsPassed() {
			return scanBodyResultsPassed;
		}
		
		public void setScanBagResultsPassed(boolean passed) {
			scanBagResultsPassed = passed;
		}

		public void setScanBodyResultsPassed(boolean passed) {
			scanBodyResultsPassed = passed;
		}
	}

}
