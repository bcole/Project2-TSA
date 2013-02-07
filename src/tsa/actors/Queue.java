package tsa.actors;

import java.util.concurrent.LinkedBlockingQueue;

import tsa.messages.ActorTerminate;
import tsa.messages.ArrivedAtQueue;
import tsa.messages.BodyScanReady;
import tsa.messages.ScanBag;
import tsa.messages.ScanBody;
import tsa.messages.ScanBodyRequest;
import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;

public class Queue extends UntypedActor {
	
	private final int number;
	private final ActorRef bagScan;
	private final ActorRef bodyScan;
	
	private final java.util.Queue<ActorRef> passengerQueue;
	
	public Queue(int number, ActorRef bagScan, ActorRef bodyScan) {
		this.number = number;
		this.bagScan = bagScan;
		this.bodyScan = bodyScan;
		
		this.getContext().setId("Queue-" + Integer.toString(this.number));
		
		// XXX: Does this need to be a thread-safe queue?
		passengerQueue = new LinkedBlockingQueue<ActorRef>();
	}

	@Override
	public void onReceive(Object message) {
		if (message instanceof ArrivedAtQueue) {
			ActorRef passenger = ((ArrivedAtQueue) message).passenger;
			passengerQueue.add(passenger);

			ScanBag scanBagMessage = new ScanBag(passenger);
			ScanBodyRequest scanBodyRequestMessage = 
					new ScanBodyRequest(this.getContext());
			
			bagScan.tell(scanBagMessage);
			bodyScan.tell(scanBodyRequestMessage);
		} else if (message instanceof BodyScanReady) {
			if (!passengerQueue.isEmpty()) {
				ActorRef passenger = passengerQueue.remove();
				ScanBody scanBodyMessage = new ScanBody(passenger);

				bodyScan.tell(scanBodyMessage);
			}
		}
		
		//Message to terminate and actor terminates itself. 
		if (message instanceof ActorTerminate) {
			// Make sure the queue is empty
			if(passengerQueue.isEmpty()){
				System.out.println("Terminating All Actors in " + this.getContext().getId());
				
				bagScan.tell(new ActorTerminate());
				bodyScan.tell(new ActorTerminate());
				
				this.getContext().tell(Actors.poisonPill());
			} else {
				// Add the message to the end of the Actor queue.
				// Basically, try again next time the queue is flushed.
				this.getContext().tell(message);
			}
		}
	}

}
