package wrm.log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Component;

@MessageEndpoint
public class LogEventTransformer {

	@Value("${log.format}")
	String logFormatRegex;
	Pattern logFormat;
	
	@PostConstruct
	public void init() {
		logFormat = Pattern.compile(logFormatRegex, Pattern.DOTALL);
	}
	
	
	@Transformer
    public LogEvent transformRawLogEvent(String value, @Header("host") String host){
		Matcher matcher = logFormat.matcher(value);
		
		LogEvent logEvent = new LogEvent();
		if (matcher.matches()){
			 logEvent.setDate(matcher.group("date"));
		     String prio = matcher.group("prio");
			logEvent.setPriority(Priority.valueOf(prio));
		     logEvent.setData(matcher.group("data"));
		     logEvent.setHost(host);
		} else {
			logEvent.setData(value);
		}
		return logEvent;
    } 
	
}
