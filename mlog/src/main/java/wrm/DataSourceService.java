package wrm;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;


@Component
public class DataSourceService {

	@Resource(name = "sources")
	private Properties sources;

	Map<String, List<DataSource>> dataSources = new HashMap();

	@PostConstruct
	private void init() {
		for(Entry<Object, Object> p : sources.entrySet())
		{
			String[] key = p.getKey().toString().split("\\.");
			String[] path = p.getValue().toString().split(":");
			DataSource ds = new DataSource(path[0], path[1]);
			if (dataSources.get(key[0])== null)
				dataSources.put(key[0], new LinkedList<DataSource>());
			
			dataSources.get(key[0]).add(ds);
		}
		
	}
	
	
	public Collection<String> getConfigs(){
		Set<String> keySet = dataSources.keySet();
		List<String> result = new LinkedList<>(keySet);
		Collections.sort(result);
		return result;
	}
	
	public List<DataSource> getDataSources(String config){
		return dataSources.get(config);
	}
	
}
