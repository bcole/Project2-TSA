//TimeBagMessage
package tsa.messages;

import akka.actor.ActorRef;

public class TimeBagMessage {
	public final long wakeUpAt;
	public final ActorRef bagScan;
	
	public TimeBagMessage(long wakeUpAt, ActorRef bagScan){
		this.wakeUpAt = wakeUpAt;
		this.bagScan = bagScan;
	}
}
