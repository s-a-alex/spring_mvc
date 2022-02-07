package init;

import config.DataServiceConfig;
import config.SecurityConfig;
import config.WebConfig;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	private static final long MAX_FILE_SIZE = 5000000;
	private static final long MAX_REQUEST_SIZE = 5000000;
	// Size threshold after which files will be written to disk
	private static final int FILE_SIZE_THRESHOLD = 0;

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[]{
				SecurityConfig.class, DataServiceConfig.class
		};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[]{
				WebConfig.class
		};
	}

	@Override
	protected String[] getServletMappings() {
		return new String[]{"/"};
	}

	@Override
	protected Filter[] getServletFilters() {
		CharacterEncodingFilter cef = new CharacterEncodingFilter();
		cef.setEncoding("UTF-8");
		cef.setForceEncoding(true);
		return new Filter[]{new HiddenHttpMethodFilter(), cef};
	}

	// В первом параметре компонента MultipartConfigElement задается место для временного хранения файлов, во втором - максимально
	// допустимый размер выгружаемого файла (в данном случае 5 Мбайт). В третьем параметре указывается длина запроса, которая в данном
	// случае также составляет 5 Мбайт. В четвертом параметре задается порог, при превышении которого файлы должны записываться на диск.
	@Override
	protected void customizeRegistration(ServletRegistration.Dynamic registration) {
		registration.setMultipartConfig(new MultipartConfigElement(null, MAX_FILE_SIZE, MAX_REQUEST_SIZE, FILE_SIZE_THRESHOLD));
	}
}