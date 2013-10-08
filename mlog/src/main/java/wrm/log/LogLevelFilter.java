package wrm.log;

import org.springframework.integration.annotation.Filter;
import org.springframework.integration.annotation.MessageEndpoint;

@MessageEndpoint
public class LogLevelFilter{

	Priority filterLevel = Priority.DEBUG;
	
	@Filter
	public boolean accept(LogEvent message){
		return message.getPriority().isEqOrMoreSevereThan(filterLevel);
	}

	
	
	public void setFilterLevel(Priority filterLevel) {
		this.filterLevel = filterLevel;
	}
}
