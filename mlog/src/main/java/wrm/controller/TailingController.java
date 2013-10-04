package wrm.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import wrm.DataSource;
import wrm.DataSourceService;
import wrm.log.LogEvent;
import wrm.ssh.SshStreamSource;

import com.jcraft.jsch.JSchException;

@Component("tailingController")
public class TailingController implements Initializable {

	private final class AddEventToTableExec implements Runnable {
		private LogEvent event;
		private ObservableList<LogEvent> table2;

		public AddEventToTableExec(LogEvent event, ObservableList<LogEvent> table) {
			this.event = event;
			table2 = table;
		}

		@Override
		public void run() {
			table2.add(event);
		}
	}

	@FXML
	private Button search;
	@FXML
	private TableView<LogEvent> table;
	@FXML
	private TextField grepExpression;
	@FXML
	private TextField searchExpression;
	@FXML
	private ChoiceBox<String> configurations;

	
	@Autowired
	DataSourceService sources;
	
	@Autowired
	SshStreamSource ssh;
	private ObservableList<LogEvent> masterData;
	private ObservableList<LogEvent> filterData;
	private Filter filter;
	
	
	
	@ServiceActivator
    public void printValue(LogEvent event){
        Platform.runLater(new AddEventToTableExec(event, masterData));
        
    } 
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		configurations.getItems().addAll(sources.getConfigs());
		configurations.getSelectionModel().select(0);
		masterData = table.getItems();
	}
	

	public void startTailing() {
		String selectedConfig = configurations.getSelectionModel().getSelectedItem();

		try {
			List<DataSource> dataSources = sources.getDataSources(selectedConfig);
			ssh.start(dataSources);
		} catch (JSchException | IOException e) {
			e.printStackTrace();
		}
	}

	public void stopTailing() {
		ssh.stop();
	}
	
	public void filter(){
		if (filterData != null)
			filterOff();
		else
			fiterOn();
	}


	private void fiterOn() {
		
		filterData = FXCollections.observableArrayList();
		filter = new Filter(searchExpression.getText(), filterData);
		
		for(LogEvent e : masterData)
			if (filter.matches(e))
				filterData.add(e);
		
		table.setItems(filterData);
		masterData.addListener(filter);
		
	}

	

	private void filterOff() {
		masterData.removeListener(filter);
		table.setItems(masterData);
		filterData = null;
	}
	
	
}