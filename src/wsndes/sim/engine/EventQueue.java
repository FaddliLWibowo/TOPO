package wsndes.sim.engine;

import java.util.ArrayList;
import java.util.List;

public class EventQueue {
	List<TimedEvent> Q;
	
	private static EventQueue eq;
	
	private EventQueue(){
		Q = new ArrayList<TimedEvent>();
	}
	
	public static EventQueue getInstance(){
		if(eq == null)
			eq = new EventQueue();
		return eq;
	}
}
