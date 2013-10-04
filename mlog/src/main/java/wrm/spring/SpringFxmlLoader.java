package wrm.spring;

import java.io.IOException;
import java.io.InputStream;

import javafx.fxml.FXMLLoader;
import javafx.util.Callback;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringFxmlLoader implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	public Object load(String url) {
		try (InputStream fxmlStream = SpringFxmlLoader.class
				.getResourceAsStream(url)) {
			System.err.println(SpringFxmlLoader.class.getResourceAsStream(url));
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(SpringFxmlLoader.class.getResource(url));
			
			
			loader.setControllerFactory(new Callback<Class<?>, Object>() {
				@Override
				public Object call(Class<?> clazz) {
					return applicationContext.getBean(clazz);
				}
				
			});
			
			
			
			loader.setClassLoader(new ClassLoader() {
				@Override
				public Class<?> loadClass(String name)
						throws ClassNotFoundException {
					if (applicationContext.containsBean(name))
						return applicationContext.getBean(name).getClass();
					
					return super.loadClass(name);
				}
			});
			
			
			return loader.load(fxmlStream);
		} catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
				this.applicationContext = applicationContext;
		
	}
}