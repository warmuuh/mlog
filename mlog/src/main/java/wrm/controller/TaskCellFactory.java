package wrm.controller;

import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.util.Callback;

import org.springframework.stereotype.Component;

import wrm.log.LogEvent;
import wrm.log.Priority;

public class TaskCellFactory implements Callback<TableColumn<LogEvent,Object>, TableCell<LogEvent,Object>> {

	
	Filter filter;
	
	@Override
	public TableCell<LogEvent,Object> call(TableColumn<LogEvent,Object> p) {

		TableCell<LogEvent,Object> cell = new TableCell<LogEvent, Object>() {

			@Override
			public void updateItem(Object item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? null : getString());
				setGraphic(null);
				TableRow<LogEvent> currentRow = getTableRow();
				LogEvent curEvt = currentRow == null ? null
						: (LogEvent) currentRow.getItem();
				if (curEvt != null){
					if (!isHover() && !isSelected() && !isFocused()) {
						clearPriorityStyle();
						setPriorityStyle(curEvt.getPriority());
						if (filter != null)
							if (filter.matches(curEvt)){
								clearPriorityStyle();
								currentRow.getStyleClass().add("match");
							}
					}
				}


			}

			@Override
			public void updateSelected(boolean upd) {
				super.updateSelected(upd);
			}

			private void clearPriorityStyle() {
				ObservableList<String> styleClasses = getStyleClass();
				styleClasses.remove("priority-debug");
				styleClasses.remove("priority-warn");
				styleClasses.remove("priority-info");
				styleClasses.remove("priority-error");
				styleClasses.remove("match");
			}

			private void setPriorityStyle(Priority priority) {
				if (priority == null)
					return;
				
				switch (priority) {
				case DEBUG:
					getStyleClass().add("priority-debug");
					break;
				case INFO:
					getStyleClass().add("priority-info");
					break;
				case WARN:
					getStyleClass().add("priority-warn");
					break;
				case ERROR:
				case FATAL:
					getStyleClass().add("priority-error");
					break;
					
				}
			}

			private String getString() {
				return getItem() == null ? "" : getItem().toString();
			}
		};

		return cell;
	}
	
	public void setHighlightFilter(Filter filter){
		this.filter = filter;
	}
	
}