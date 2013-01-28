package tsa.actors;
import akka.actor.UntypedActor;


public class BodyScan extends UntypedActor{
	private boolean occupied = false;
	private Passenger currentPassanger;
	
	
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof ScanBody){
			if(occupied) //tell queue actor that body scan is occupied
		}else{
			//set current passenger
			
			//do check for pass/fail
			
			//pass message to securityActor with pass fail 
		}
		
	}

}
