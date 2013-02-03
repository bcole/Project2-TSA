package tsa.actors;

import akka.actor.ActorRef;
import tsa.messages.ArrivedAtJail;
import tsa.messages.GoToDetention;

public class Jail {

	private final ActorRef[] inJail; 
	private int jailIndex; 
	
	public Jail(ActorRef[] inJail) { 
		
		this.inJail = inJail;
		jailIndex = 0; 
	}
	
	private void onReceive(Object Message) { 
		
		if (Message instanceof ArrivedAtJail) {

			//Put Passenger in Jail List
			ActorRef badPassenger = ((ArrivedAtJail) Message).passenger;
			inJail[jailIndex] = badPassenger;
			System.out.println("Passenger put in jail.");
			
			//Increment index for next passenger. 
			jailIndex++; 
		}
		
		if (Message instanceof GoToDetention) { 
			
			//End of day passenger move to detention facility. 
			System.out.println("Moving " + inJail.length + " to permanent detention facility");
		}
	}
}
