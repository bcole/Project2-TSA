package tsa;

import tsa.actors.BagScan;
import tsa.actors.BodyScan;
import tsa.actors.DocumentCheck;
import tsa.actors.Passenger;
import tsa.actors.Queue;
import tsa.actors.Security;
import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;

public class TSAMain {
	
	public static final int NUMBER_OF_PASSENGERS = 5;
	public static final int NUMBER_OF_QUEUES = 3;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final ActorRef[] queues = new ActorRef[NUMBER_OF_QUEUES];
		
		// XXX: Perhaps some object group for each line would be good.  For now
		//      constructor arguments will be used.
		for (int i = 0; i < NUMBER_OF_QUEUES; i++) {
			final ActorRef security = Actors.actorOf(Security.class);
			
			final ActorRef bagScan = Actors.actorOf(new UntypedActorFactory() {
				public UntypedActor create() {
					return new BagScan(security);
				}
			});
			
			final ActorRef bodyScan = Actors.actorOf(new UntypedActorFactory() {
				public UntypedActor create() {
					return new BodyScan(security);
				}
			});
			
			queues[i] = Actors.actorOf(new UntypedActorFactory() {
				public UntypedActor create() {
					return new Queue(bagScan, bodyScan);
				}
			});
			
			// XXX: Start time shouldn't matter here, correct?
			security.start();
			bagScan.start();
			bodyScan.start();
			queues[i].start();
		}
		
		final ActorRef documentCheck = Actors.actorOf(new UntypedActorFactory() {
            public UntypedActor create() {
                return new DocumentCheck(queues);
            }
        });
		documentCheck.start();
		
		
		// Passengers enter the system from a main driver program.
		ActorRef[] passengers = new ActorRef[NUMBER_OF_PASSENGERS];
		for (int i = 0; i < NUMBER_OF_PASSENGERS; i++) {
			final int number = i;
			
			passengers[i] = Actors.actorOf(new UntypedActorFactory() {
                public UntypedActor create() {
                    return new Passenger(number, documentCheck);
                }
            });
		}
		
		for (ActorRef passenger: passengers) {
			passenger.start();
		}
		
		// XXX: Force close required to end program.
	}

}
