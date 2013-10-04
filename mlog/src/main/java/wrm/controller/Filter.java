package wrm.controller;

import java.util.regex.Pattern;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import wrm.log.LogEvent;

public final class Filter implements
		ListChangeListener<LogEvent> {

	private Pattern filterExpression;

	private ObservableList<LogEvent> filterData;
	
	public Filter(String text, ObservableList<LogEvent> filterData) {
		this.filterData = filterData;
		filterExpression = Pattern.compile(text);
	}

	@Override
	public void onChanged(javafx.collections.ListChangeListener.Change<? extends LogEvent> evt) {
		if (evt.wasAdded())
			for(LogEvent le : evt.getAddedSubList())
				if (matches(le))
					filterData.add(le);
	}

	public boolean matches(LogEvent le) {
		return filterExpression.matcher(le.getData()).find();
	}
}