package wrm;

import javafx.application.Application;
import javafx.stage.Stage;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class JavaFxApplication extends Application {

	public static void main(String[] args) {
		Application.launch(args);
	}
	
	ApplicationContext context;
	private Application application;
	

	@Override
	public void init() throws Exception {
		super.init();
		context = new GenericXmlApplicationContext("/spring-configuration.xml");
		application = context.getBean(Application.class);
		application.init();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		application.start(primaryStage);
	}
}
