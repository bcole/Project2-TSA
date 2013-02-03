package tsa.actors;

import tsa.messages.ScanBag;
import tsa.messages.ScanBagResults;
import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;
import tsa.messages.ActorTerminate;

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
		
		//Message to terminate and actor terminates itself. 
		if (message instanceof ActorTerminate) { 
			
			//Try and tell the security to die. If already dead then it will 
			//throw an exception because it can't tell it to die. Catch the exception
			//and print info message. 
			try { 
				security.tell(new ActorTerminate());
			} catch (Exception excep) { 
				System.out.println("Security Actor already terminated OR there is another error.");
			}
			this.getContext().tell(Actors.poisonPill());
		}
	}

}
