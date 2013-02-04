package tsa.actors;
import tsa.messages.ActorTerminate;
import tsa.messages.BodyScanReady;
import tsa.messages.ScanBody;
import tsa.messages.ScanBodyRequest;
import tsa.messages.ScanBodyResults;
import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;


public class BodyScan extends UntypedActor {
	
	private final int number;
	private final ActorRef security;
	
	private ActorRef currentPassenger = null;
	private ActorRef queue;
	
	public BodyScan(int number, ActorRef security) {
		this.number = number;
		this.security = security;
		
		this.getContext().setId("BodyScan-" + Integer.toString(this.number));
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof ScanBodyRequest) {
			// Set the Queue if it hasn't been set before.
			if (queue == null) {
				queue = ((ScanBodyRequest) message).queue;
			}
			
			// The body scan is ready if there is no current passenger.
			if (currentPassenger == null) {
				queue.tell(new BodyScanReady());
			}
		} else if (message instanceof ScanBody) {
			// This can only be reached after BodyScanReady is sent to Queue.
			processPassenger(((ScanBody) message).passenger);
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
	
	private void processPassenger(ActorRef passenger) {
		currentPassenger = passenger;
		
		// Passengers randomly fail inspection with a probability of 20%.
		boolean passed = (Math.random() < 0.8);
		
		
		// TODO: SLEEP HERE
		

		if (passed) {
			System.out.println(currentPassenger.getId() + 
					": Passed BodyScan-" + number);
		} else {
			System.out.println(currentPassenger.getId() + 
					": Failed BodyScan-" + number);
		}

		ScanBodyResults resultsMessage = 
				new ScanBodyResults(currentPassenger, passed);
		security.tell(resultsMessage);
		
		currentPassenger = null;
		
		// Always tell the Queue when BodyScan goes from full to empty.
		queue.tell(new BodyScanReady());
	}

}
