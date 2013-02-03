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
			
			//Increment index for next passenger. 
			jailIndex++; 
		}
		
		
	}
}
