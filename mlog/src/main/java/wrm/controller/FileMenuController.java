package wrm.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component("menuController")
public class FileMenuController implements Initializable {

	@Override
	public void initialize(URL paramURL, ResourceBundle paramResourceBundle) {

	}

	@FXML
	public void exit() {
		Platform.exit();
	}

}