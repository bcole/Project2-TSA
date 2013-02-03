package tsa.actors;

import java.util.concurrent.LinkedBlockingQueue;

import tsa.messages.ActorTerminate;
import tsa.messages.ArrivedAtQueue;
import tsa.messages.ScanBag;
import tsa.messages.ScanBody;
import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;

public class Queue extends UntypedActor {
	
	private final ActorRef bagScan;
	private final ActorRef bodyScan;
	
	private final java.util.Queue<ActorRef> passengerQueue;
	
	public Queue(ActorRef bagScan, ActorRef bodyScan) {
		this.bagScan = bagScan;
		this.bodyScan = bodyScan;
		
		// XXX: Does this need to be a thread-safe queue?
		passengerQueue = new LinkedBlockingQueue<ActorRef>();
	}

	@Override
	public void onReceive(Object message) {
		if (message instanceof ArrivedAtQueue) {
			ActorRef passenger = ((ArrivedAtQueue) message).passenger;

			ScanBag scanBagMessage = new ScanBag(passenger);
			ScanBody scanBodyMessage = new ScanBody(passenger);
			
			bagScan.tell(scanBagMessage);
			bodyScan.tell(scanBodyMessage);
		}
		
		//Message to terminate and actor terminates itself. 
		if (message instanceof ActorTerminate) { 
			
			this.getContext().tell(Actors.poisonPill());
		}
	}

}
