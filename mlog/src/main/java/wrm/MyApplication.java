package wrm;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import wrm.spring.SpringFxmlLoader;

@Primary
@Component
public class MyApplication extends Application {

	@Autowired
	SpringFxmlLoader loader;

	@Override
	public void start(Stage primaryStage) throws Exception {
	 	Parent root = (Parent) loader.load("/search.xml");
		Scene scene = new Scene(root, 768, 480);
//		scene.getStylesheets().add("dark.css");
		primaryStage.setScene(scene);
		primaryStage.setTitle("JavaFX demo");
		primaryStage.show();
	}
}
