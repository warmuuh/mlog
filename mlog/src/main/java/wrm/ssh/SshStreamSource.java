package wrm.ssh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import wrm.DataSource;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@Component
@Slf4j
public class SshStreamSource {

	

	private final class LogStreamer implements Runnable {
		private MessageChannel msgChan;
		private List<ChannelExec> channels;
		
		private List<BufferedReader> readers = new ArrayList<>();

		private LogStreamer(List<ChannelExec> channels, MessageChannel messageChannel) throws IOException {
			this.channels = channels;
			msgChan = messageChannel;
			
			for(ChannelExec channel : channels){
				InputStreamReader isr = new InputStreamReader(channel.getInputStream());
				BufferedReader br = new BufferedReader(isr);	
				readers.add(br);
			}
			
		}

		@Override
		public void run() {
			
			
			while(true){
				try {
					for(int i = 0; i < channels.size(); ++i)
						handleChannelMessages(readers.get(i), channels.get(i));
					try{Thread.sleep(1000);}catch(Exception ee){}
				} catch (IOException | JSchException e) {
					e.printStackTrace();
				}
			}
		}

		private void handleChannelMessages(BufferedReader br, ChannelExec channel) throws IOException, JSchException {
			StringBuilder curLine = new StringBuilder();
			while (br.ready() && channel.isConnected()){
				String line = br.readLine();
				log.debug(">{}", line);
				if (logFormat.matcher(line).matches()) //if current line matches, send old line and record next one
				{
					if (curLine != null && curLine.length() > 0){
						msgChan.send(MessageBuilder.withPayload(curLine.toString())
							.setHeader("host", channel.getSession().getHost())
							.build());
					}
					curLine = new StringBuilder();
				}
				curLine.append(line + "\n");
			}
			if (curLine != null && curLine.length() > 0)
				msgChan.send(MessageBuilder.withPayload(curLine.toString())
						.setHeader("host", channel.getSession().getHost())
						.build());
		}
	}


	@Value("${ssh.privatekey.password}")
	String privateKeyPassword;

	@Value("${ssh.privatekey.user}")
	String user;
	@Value("${ssh.privatekey.file}")
	String privateKeyFile;
	
	
	@Value("${log.format}")
	String logFormatRegex;
	Pattern logFormat;
	
	
	
	@Autowired
	@Qualifier("rawLogEvents")
	MessageChannel messageChannel;

	private List<Session>  sessions = new LinkedList<>();

	private List<ChannelExec> channels = new LinkedList<>();

	private Thread thread;
	
	@PostConstruct
	public void init() {
		logFormat = Pattern.compile(logFormatRegex);
	}
	
	public void start(Collection<DataSource> sources, String grepExpression) throws JSchException, IOException{
		for(DataSource source : sources){
			Session session = getSshSession(source);
			sessions.add(session);
			ChannelExec channel = getExecChannel(session, source, grepExpression);
			channels.add(channel);
			
//			final InputStream stream = channel.getInputStream();
			channel.connect();
		}
		
		
		thread = new Thread(new LogStreamer(channels, messageChannel));
		thread.setDaemon(true);
		thread.start();
		
	}
	
	
	public void stop(){
		thread.stop();
		
		for(ChannelExec channel : channels)
			channel.disconnect();
		
		for(Session session : sessions)
			session.disconnect();
	}
	
	public Session getSshSession(DataSource source) throws JSchException{
		
		boolean useKeyfile = !StringUtils.isEmpty(privateKeyFile);
		
		JSch.setConfig("StrictHostKeyChecking", "no");
		JSch jsch = new JSch();
		if(useKeyfile)
			jsch.addIdentity(privateKeyFile, privateKeyPassword);
		Session session = jsch.getSession(user, source.getHost());
		session.setDaemonThread(true);
		if (!useKeyfile)
			session.setPassword(privateKeyPassword);
		session.connect();
		log.debug("Connected: {}", session.isConnected());
		return session;
	}


	private ChannelExec getExecChannel(Session session, DataSource source, String grepExpression) throws JSchException {
		ChannelExec execChan = (ChannelExec) session.openChannel("exec");
		                          
		String file = source.getFilePattern();
		String tail = "tail -f " + MessageFormat.format(file, new Date());

		String grep = "";
		if (!StringUtils.isEmpty(grepExpression))
			grep = " | grep --line-buffered " + grepExpression;
		
		String command = tail + grep;
		log.debug("Executing Command: {}", command);
		execChan.setCommand(command);
		return execChan;
	}
	
	
}
