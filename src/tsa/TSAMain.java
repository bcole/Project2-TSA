package tsa;

import tsa.actors.DocumentCheck;
import tsa.actors.Passenger;
import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;

public class TSAMain {
	
	public static final int PASSENGER_COUNT = 3;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// XXX: At the beginning of the day, the system will initialize and turn
		//      on all of the scanners. - What is meant by "turn on"?
		final ActorRef documentCheck = Actors.actorOf(DocumentCheck.class);
		documentCheck.start();
		
		// Passengers enter the system from a main driver program.
		ActorRef[] passengers = new ActorRef[PASSENGER_COUNT];
		for (int i = 0; i < PASSENGER_COUNT; i++) {
			passengers[i] = Actors.actorOf(new UntypedActorFactory() {
                public UntypedActor create() {
                    return new Passenger(documentCheck);
                }
            });
		}
		
		for (ActorRef passenger: passengers) {
			passenger.start();
		}
		
		// XXX: Force close required to end program.
	}

}
