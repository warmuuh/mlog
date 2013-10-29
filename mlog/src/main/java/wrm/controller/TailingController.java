package wrm.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import wrm.DataSource;
import wrm.DataSourceService;
import wrm.log.LogEvent;
import wrm.log.LogLevelFilter;
import wrm.log.Priority;
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
			((Stage)table.getScene().getWindow()).toFront();
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
	private TextArea details;
	@FXML
	private ChoiceBox<String> configurations;
	@FXML
	private CheckBox highlightOnly;

	@FXML
	private ChoiceBox<String> loglevelfilter;
	
	
	@Autowired
	DataSourceService sources;
	
	@Autowired
	SshStreamSource ssh;
	

	@Autowired
	LogLevelFilter logFilter;
	
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
		
		loglevelfilter.getSelectionModel().select(0);
		loglevelfilter.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> obValue,
					String old, String newValue) {
				logFilter.setFilterLevel(Priority.valueOf(newValue));
			}
		});
		
		table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); 
		table.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<LogEvent>(){

			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends LogEvent> evt) {
				if (evt.getList().size() > 0)
					details.setText(evt.getList().get(0).getData());
			}});
	}
	

	public void clear() {
		masterData.clear();
	}

	
	public void startTailing() {
		String selectedConfig = configurations.getSelectionModel().getSelectedItem();

		try {
			List<DataSource> dataSources = sources.getDataSources(selectedConfig);
			ssh.start(dataSources, grepExpression.getText());
		} catch (JSchException | IOException e) {
			e.printStackTrace();
		}
	}

	public void stopTailing() {
		ssh.stop();
	}
	
	public void filter(){
		if (filter != null)
			filterOff();
		else
			filterOn();
	}

	
	public void highlight(){
		if (filter != null)
			highlightOff();
		else
			highlightOn();

	}
	

	private void highlightOn() {
		filter = new Filter(searchExpression.getText(), null);
		for(TableColumn<LogEvent, ?> c : table.getColumns())
			if (c.getCellFactory().getClass().equals(TaskCellFactory.class)){
				TaskCellFactory taskCellFactory = TaskCellFactory.class.cast(c.getCellFactory());
				taskCellFactory.setHighlightFilter(filter);
			}
		
		refreshTable();
				
				
	}


	private void refreshTable() {
		table.setItems(null);
		table.layout();
		table.setItems(masterData);
	}


	private void highlightOff() {
		filter = null;
		for(TableColumn<LogEvent, ?> c : table.getColumns())
			if (c.getCellFactory().getClass().equals(TaskCellFactory.class)){
				TaskCellFactory taskCellFactory = TaskCellFactory.class.cast(c.getCellFactory());
				taskCellFactory.setHighlightFilter(null);
			}
		refreshTable();
	}


	private void filterOn() {
		
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
		filter = null;
	}
	
	
}