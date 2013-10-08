package wrm.log;

import lombok.Data;

@Data
public class LogEvent {
	String date;
	Priority priority;
	String data;
	String host;
	
}
