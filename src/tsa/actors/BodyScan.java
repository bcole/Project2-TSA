package tsa.actors;
import tsa.messages.ActorTerminate;
import tsa.messages.ScanBody;
import tsa.messages.ScanBodyResults;
import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;


public class BodyScan extends UntypedActor {
	
	private final int number;
	private final ActorRef security;
	
	public BodyScan(int number, ActorRef security) {
		this.number = number;
		this.security = security;
		
		this.getContext().setId("BodyScan-" + Integer.toString(this.number));
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof ScanBody) {
			// Passengers randomly fail inspection with a probability of 20%.
			boolean passed = (Math.random() < 0.8);
			ActorRef passenger = ((ScanBody) message).passenger;
			
			if (passed) {
				System.out.println(passenger.getId() + ": Passed BodyScan-" + number);
			} else {
				System.out.println(passenger.getId() + ": Failed BodyScan-" + number);
			}
	
			ScanBodyResults resultsMessage = 
					new ScanBodyResults(passenger, passed);
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
