package tsa.actors;

import tsa.messages.TimeBagMessage;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class BagTimer extends UntypedActor{
	
	private Long wakeUpAt;
	
	
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof TimeBagMessage){
			wakeUpAt = ((TimeBagMessage)message).wakeUpAt;
			ActorRef bagScan = ((TimeBagMessage)message).bagScan;
			
			long timeLeft = wakeUpAt - System.currentTimeMillis();
			if(timeLeft>0){
				Thread.sleep(timeLeft);	
			}
			
			bagScan.tell("finished");
		}
		
	}
	

	

}
