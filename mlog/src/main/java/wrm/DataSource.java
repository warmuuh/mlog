package wrm;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class DataSource {
	String host;
	String filePattern;
}
