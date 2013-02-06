package tsa.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class BagTimer extends UntypedActor{
	
	private Long wakeUpAt;
	
	
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof TimeBagMessage){
			wakeUpAt = message.wakeUpAt;
			ActorRef bagScan = message.bagScan;
			long timeLeft = wakeUpAt - System.currentTimeMillis();
			if(timeLeft>0){
				Thread.sleep(timeLeft);
				
			}
		}
		
	}
	

	

}
