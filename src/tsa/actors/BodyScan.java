package tsa.actors;
import tsa.messages.ScanBody;
import tsa.messages.ScanBodyResults;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;


public class BodyScan extends UntypedActor {
	private boolean occupied = false;
	private Passenger currentPassanger;
	
	private final ActorRef security;
	
	public BodyScan(ActorRef security) {
		this.security = security;
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		// XXX: No simulation of being occupied.
		
		if (message instanceof ScanBody) {
			// Passengers randomly fail inspection with a probability of 20%.
			boolean passed = (Math.random() < 0.2);
			ActorRef passenger = ((ScanBody) message).passenger;
	
			// XXX: Passenger has to get to Security somehow.
			ScanBodyResults resultsMessage = 
					new ScanBodyResults(passenger, passed);
			security.tell(resultsMessage);
		}

//		if(message instanceof ScanBody){
//			if(occupied) //tell queue actor that body scan is occupied
//		}else{
//			//set current passenger
//			
//			//do check for pass/fail
//			
//			//pass message to securityActor with pass fail 
//		}
		
	}

}
