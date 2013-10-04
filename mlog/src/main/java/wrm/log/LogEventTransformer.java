package wrm.log;

import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Component;

@MessageEndpoint
public class LogEventTransformer {

	
	@Transformer
    public LogEvent transformRawLogEvent(String value, @Header("host") String host){
        LogEvent logEvent = new LogEvent();
        logEvent.setData(value);
        logEvent.setHost(host);
		return logEvent;
    } 
	
}
