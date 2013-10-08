package wrm.log;

import java.util.Arrays;
import java.util.List;

public enum Priority {
	DEBUG,
	INFO,
	WARN,
	ERROR,
	FATAL;
	
	
	
	  public boolean isEqOrMoreSevereThan(Priority other){
		 List<Priority> list = Arrays.asList(values());
		 return list.indexOf(this) >= list.indexOf(other);
	  }
}
