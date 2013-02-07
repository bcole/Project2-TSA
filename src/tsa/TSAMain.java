package tsa;

import tsa.actors.BagScan;
import tsa.actors.BodyScan;
import tsa.actors.DocumentCheck;
import tsa.actors.Jail;
import tsa.actors.Passenger;
import tsa.actors.Queue;
import tsa.actors.Security;
import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import tsa.messages.ActorTerminate;

public class TSAMain {
	
	public static final int NUMBER_OF_PASSENGERS = 15;
	public static final int NUMBER_OF_QUEUES = 3;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("-- Starting Simulation --");
		final ActorRef[] queues = new ActorRef[NUMBER_OF_QUEUES];
		
		final ActorRef jail = Actors.actorOf(new UntypedActorFactory () { 
			public UntypedActor create() { 
				return new Jail(NUMBER_OF_PASSENGERS,NUMBER_OF_QUEUES);
			}
		});
		
		// XXX: Perhaps some object group for each line would be good.  For now
		//      constructor arguments will be used.
		for (int i = 0; i < NUMBER_OF_QUEUES; i++) {
			final int number = i;
			
			final ActorRef security = Actors.actorOf(new UntypedActorFactory() { 
				public UntypedActor create() { 
					return new Security(number, jail);
				}
			});
			
			final ActorRef bagScan = Actors.actorOf(new UntypedActorFactory() {
				public UntypedActor create() {
					return new BagScan(number, security);
				}
			});
			
			final ActorRef bodyScan = Actors.actorOf(new UntypedActorFactory() {
				public UntypedActor create() {
					return new BodyScan(number, security);
				}
			});
			
			queues[i] = Actors.actorOf(new UntypedActorFactory() {
				public UntypedActor create() {
					return new Queue(number, bagScan, bodyScan);
				}
			});
			
			// XXX: Start time shouldn't matter here, correct?
			security.start();
			bagScan.start();
			bodyScan.start();
			queues[i].start();
			jail.start();
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
		
		// Sleep for a "day"
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Day is over, start terminating.
		System.out.println("-- End Of Day, Beginning System Shutdown --");
		documentCheck.tell(new ActorTerminate());
	}

}
