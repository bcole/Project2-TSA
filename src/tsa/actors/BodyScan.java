package tsa.actors;
import tsa.messages.ScanBody;
import tsa.messages.ScanBodyResults;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;


public class BodyScan extends UntypedActor {
	
	private final ActorRef security;
	
	public BodyScan(ActorRef security) {
		this.security = security;
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof ScanBody) {
			// Passengers randomly fail inspection with a probability of 20%.
			boolean passed = (Math.random() < 0.8);
			ActorRef passenger = ((ScanBody) message).passenger;
	
			ScanBodyResults resultsMessage = 
					new ScanBodyResults(passenger, passed);
			security.tell(resultsMessage);
		} 
	}

}
