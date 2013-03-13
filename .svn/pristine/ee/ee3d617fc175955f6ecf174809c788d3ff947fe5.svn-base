package wsndes.sim.engine;

import java.sql.Timestamp;

public class TimedEvent implements Comparable{
	
	final Timestamp t;
	private TimedEvent(Timestamp t){
		this.t= t;
	}
	
	public static <T extends TimedEvent> T createInstance(){
		return (T) new TimedEvent(SimRuntime.now());
	}
	
	@Override
	public int compareTo(Object arg0) {
		if(arg0 == null)
			return 1;
		if(!(arg0 instanceof TimedEvent))
			return 1;
		TimedEvent other = (TimedEvent)arg0;
		this.t.compareTo(other.t);
		return 0;
	}
	
}
