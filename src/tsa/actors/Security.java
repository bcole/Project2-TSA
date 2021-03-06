package tsa.actors;

import java.util.HashMap;
import java.util.Map;

import tsa.messages.ActorTerminate;
import tsa.messages.ArrivedAtJail;
import tsa.messages.ScanBagResults;
import tsa.messages.ScanBodyResults;
import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;

public class Security extends UntypedActor {
	
	private final int number;
	
	private Map<ActorRef, ScanResults> resultsMap 
			= new HashMap<ActorRef, ScanResults>();
	
	private final ActorRef jail;
	
	private int closed;
	
	// Constructor. 
	public Security(int number, ActorRef jail) {
		this.number = number;
		this.jail = jail; 
		closed = 0;
		this.getContext().setId("Security-"+number);
	}

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
		} 
		else if (message instanceof ScanBodyResults) {
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
		//Message to terminate and actor terminates itself. 
		if (message instanceof ActorTerminate) { 
			closed++;
			if(closed == 2){
				System.out.println("Terminating " + this.getContext().getId());
				try {
					jail.tell(new ActorTerminate());
				} catch (Exception excep) { 
					System.out.println("Jail Actor already terminated OR there is another error.");
				}
				this.getContext().tell(Actors.poisonPill());
			}
		}
	}
	
	private void respondToScanResults(ActorRef passenger) {
		ScanResults scanResults = resultsMap.remove(passenger);
		
		if (scanResults.getScanBagResultsPassed() && scanResults.getScanBodyResultsPassed()) {
			System.out.println(passenger.getId() + ": Passed Security-" + number);
			passenger.tell(new ActorTerminate());	// We don't need the passenger anymore.
		} else {
			System.out.println(passenger.getId() + ": Failed Security-" + number);
			jail.tell(new ArrivedAtJail(passenger));
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
