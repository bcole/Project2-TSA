package tsa.actors;
import java.util.Random;

import tsa.messages.ActorTerminate;
import tsa.messages.BodyScanReady;
import tsa.messages.ScanBody;
import tsa.messages.ScanBodyRequest;
import tsa.messages.ScanBodyResults;
import akka.actor.ActorInitializationException;
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
		System.out.println(this.getContext().getId() + ": Scanner turned on for the day.");
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
				try{
					queue.tell(new BodyScanReady());
				} catch(ActorInitializationException e){}	// Queue has ended, program is terminating.
			}
		} else if (message instanceof ScanBody) {
			// This can only be reached after BodyScanReady is sent to Queue.
			processPassenger(((ScanBody) message).passenger);
		}
		
		//Message to terminate and actor terminates itself. 
		if (message instanceof ActorTerminate) { 
			
			//Before it terminates itself check that there is no current passenger. 
			//If there are no passengers then terminate the actor. 
			if (currentPassenger == null) { 
			
				//Try and tell the security to die. If already dead then it will 
				//throw an exception because it can't tell it to die. Catch the exception
				//and print info message. 
				try { 
					security.tell(new ActorTerminate());
				} catch (Exception excep) { 
					System.out.println("Security Actor already terminated OR there is another error.");
				}
				
				this.getContext().tell(Actors.poisonPill());
			} else {	// Try again.
				this.getContext().tell(message);
			}
		}
	}
	
	private void processPassenger(ActorRef passenger) {
		currentPassenger = passenger;
		
		// Passengers randomly fail inspection with a probability of 20%.
		boolean passed = (Math.random() < 0.8);
		
		
		// TODO: SLEEP HERE
		
		Random random = new Random(); 
		long sleepTime = Long.valueOf(random.nextInt(8000)); //sleep for random amount of time. 
		
		try {
			System.out.println(this.getContext().getId() + ": scanning " + currentPassenger.getId() + "...");
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
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
		
		try{
			// Always tell the Queue when BodyScan goes from full to empty.
			queue.tell(new BodyScanReady());
		} catch(ActorInitializationException e){}	// Queue has ended, program is terminating.
	}

}
