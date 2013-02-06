package tsa.actors;

import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;
import tsa.messages.ActorTerminate;
import tsa.messages.ArrivedAtJail;
import tsa.messages.GoToDetention;

public class Jail extends UntypedActor {

	private final ActorRef[] inJail; 
	private int jailIndex; 
	private int numQueues;
	private int queuesClosed;
	
	public Jail(int jailCapacity, int numQueues) { 
		
		this.inJail = new ActorRef[jailCapacity];
		this.numQueues = numQueues;
		queuesClosed = 0;
		jailIndex = 0; 
	}
	
	public void onReceive(Object Message) { 
		if (Message instanceof ArrivedAtJail) {
			//Put Passenger in Jail List
			ActorRef badPassenger = ((ArrivedAtJail) Message).passenger;
			inJail[jailIndex] = badPassenger;
			System.out.println("Passenger put in jail.");
			
			//Increment index for next passenger. 
			jailIndex++; 
		}
		
		if (Message instanceof GoToDetention) { 
			// End of day passenger move to detention facility. 
			System.out.println("Moving " + inJail.length + " to permanent detention facility");
		}
		
		// Message to terminate and actor terminates itself. 
		if (Message instanceof ActorTerminate) {
			queuesClosed++;
			if(queuesClosed == numQueues){			
				System.out.println("Terminating Jail");
				this.getContext().tell(Actors.poisonPill());
			}
		}
		
	}
}
