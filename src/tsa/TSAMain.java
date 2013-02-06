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
		
		// XXX: Force close required to end program.
		// TODO: Need to make sure termination is happening correctly. 
		//terminateAllActors(passengers, documentCheck, queues);
	}
	
	//To Terminate the actors 
	//Current order of termination 
	//Passengers -> DocumentCheck -> Queue -> BagScan & BodyScan -> Security -> Jail
	private static void terminateAllActors(ActorRef[] passengers, ActorRef documentCheck, ActorRef[] queues) { 
		
		ActorTerminate terminateSelf = new ActorTerminate(); 
		
		System.out.println("Terminating Actors");
		
		System.out.println("Terminating All Passenger Actors");
		for (int i = 0; i < NUMBER_OF_PASSENGERS; i++ ) { 
			passengers[i].tell(terminateSelf);
		}
		
		System.out.println("Terminating Document Check");
		documentCheck.tell(terminateSelf);
		
		for (int i = 0; i < NUMBER_OF_QUEUES; i++) { 
			System.out.println("Terminating All Actors in Queue " + i);
			queues[i].tell(terminateSelf);
		}
		
	}

}
