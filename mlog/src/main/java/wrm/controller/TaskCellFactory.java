package wrm.controller;

import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

import org.springframework.stereotype.Component;

import wrm.log.LogEvent;

public class TaskCellFactory implements Callback<TableColumn<LogEvent,String>, TableCell<LogEvent,String>> {

	
	Filter filter;
	
	@Override
	public TableCell<LogEvent,String> call(TableColumn<LogEvent,String> p) {

		TableCell<LogEvent,String> cell = new TableCell<LogEvent, String>() {

			@Override
			public void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? null : getString());
				setGraphic(null);
				TableRow<LogEvent> currentRow = getTableRow();
				LogEvent curEvt = currentRow == null ? null
						: (LogEvent) currentRow.getItem();
				if (curEvt != null){
					clearPriorityStyle();
					if (!isHover() && !isSelected() && !isFocused()) {
						if (filter != null)
							if (filter.matches(curEvt))
								currentRow.getStyleClass().add("match");
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
//				switch (priority) {
//				case LOW:
//					getStyleClass().add("priorityLow");
//					break;
//				case MEDIUM:
//					getStyleClass().add("priorityMedium");
//					break;
//				case HIGH:
//					getStyleClass().add("priorityHigh");
//					break;
//				}
//				System.out.println(getStyleClass());
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