package config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.ui.context.support.ResourceBundleThemeSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
import org.springframework.web.servlet.theme.CookieThemeResolver;
import org.springframework.web.servlet.theme.ThemeChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesView;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import util.DateFormatter;

import java.util.Locale;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"web"})
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebConfig implements WebMvcConfigurer, ApplicationContextAware {

	private ApplicationContext applicationContext;

	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	//Активизирует обработчик
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	// В методе addResourceHandlers определяется местоположение файлов статических ресурсов. В дескрипторе разметки атрибут
	// location определяет папки для хранения статических ресурсов. Так, местоположение ресурса / обозначает корневую папку
	// для веб-приложения, а именно /src/main/webapp. А по пути /resources/** к обработчикам ресурсов определяется URL для
	// сопоставления со статическими ресурсами. Например, модуль Spring MVC извлечет файл standard.css из папки src/main/webapp/styles
	// по следующему URL: http: //localhost:8080/resources/styles/standard.css.
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/").setCachePeriod(31556926);
	}

	//Определяет простые автоматизированные контроллеры, предварительно сконфигурированные с ответным кодом состояния и/или представлением
	//для воспроизведения тела ответа. Такие представления не содержат логику контроллера и служат для воспроизведения приветственной страницы,
	//выполнения простых видов переадресации веб-сайтов по URL, возврата кода состояния 4О4 и прочих действий.
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("index");
		registry.addViewController("/login").setViewName("login");
	}

//	//Объявляет распознаватель представлений, выполняющий сопоставление символических имен представлений с шаблонами *.html	по пути /WEB-INF/views.
//	@Bean
//	InternalResourceViewResolver viewResolver() {
//		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
//		resolver.setPrefix("/WEB-INF/templates/");
//		resolver.setSuffix(".html");
//		return resolver;
//	}

	// Настройки Thymeleaf
	@Bean
	public SpringResourceTemplateResolver templateResolver(){
		// SpringResourceTemplateResolver automatically integrates with Spring's own resource resolution infrastructure, which is highly recommended.
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setApplicationContext(this.applicationContext);
		templateResolver.setPrefix("/WEB-INF/templates/");
		templateResolver.setSuffix(".html");
		// HTML is the default value, added here for the sake of clarity.
		templateResolver.setTemplateMode(TemplateMode.HTML);
		// Template cache is true by default. Set to false if you want templates to be automatically updated when modified.
		templateResolver.setCacheable(true);
		templateResolver.setCharacterEncoding("UTF-8");
		return templateResolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine(){
		// SpringTemplateEngine automatically applies SpringStandardDialect and enables Spring's own MessageSource message resolution mechanisms.
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		// Enabling the SpringEL compiler with Spring 4.2.4 or newer can
		// speed up execution in most scenarios, but might be incompatible
		// with specific cases when expressions in one template are reused
		// across different data types, so this flag is "false" by default
		// for safer backwards compatibility.
		templateEngine.setEnableSpringELCompiler(false);
		templateEngine.addDialect(new SpringSecurityDialect());
		return templateEngine;
	}

	@Bean
	public ThymeleafViewResolver viewResolver(){
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(templateEngine());
		viewResolver.setCharacterEncoding("UTF-8");
		return viewResolver;
	}
	// Настройки Thymeleaf

	@Override
	public void addFormatters(FormatterRegistry formatterRegistry) {
		formatterRegistry.addFormatter(dateFormatter());
	}

	@Bean
	public DateFormatter dateFormatter() {
		return new DateFormatter();
	}

	// Регистрация перехватчиков
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
//		registry.addInterceptor(themeChangeInterceptor());
//		registry.addInterceptor(webChangeInterceptor());
	}

	// В классе LocaleChangeinterceptor из модуля Spring MVC определяется перехватчик всех запросов к сервлету диспетчера типа DispatcherServlet.
	// Этот перехватчик поддерживает смену региональных настроек с помощью настраиваемого параметра запроса. В конфигурации этого перехватчика
	// определяется параметр URL под именем lang для смены региональных настроек веб-приложения. Например, http://localhost:8080/singers?lang=zh_HK
	@Bean
	LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName("lang");
		return interceptor;
	}

	// Класс ReloadableResourceBundleMessageSource реализует интерфейс MessageSource, который служит для загрузки сообщений из определенных файлов,
	// предназначенных для интернационализации. fallbackToSystemLocale предписывает модулю Spring MVC вернуться к региональным настройкам системы,
	// в которой работает данное вебприложение, если специальный комплект ресурсов для клиентских региональных настроек не обнаружен.
	@Bean
	ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("WEB-INF/i18n/messages", "WEB-INF/i18n/application");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setFallbackToSystemLocale(false);
		return messageSource;
	}

	// В классе CookieLocaleResolver поддерживается хранение и извлечение региональных настроек из сооkiе-файла пользовательского браузера.
	@Bean
	CookieLocaleResolver localeResolver() {
		CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
		cookieLocaleResolver.setDefaultLocale(Locale.ENGLISH);
		cookieLocaleResolver.setCookieMaxAge(3600);
		cookieLocaleResolver.setCookieName("locale");
		return cookieLocaleResolver;
	}
//
//	@Bean
//	ThemeChangeInterceptor themeChangeInterceptor() {
//		return new ThemeChangeInterceptor();
//	}
//
//	// ResourceBundleThemeSource отвечает за загрузку комплекта ресурсов для активной темы. Так, если активная тема называется
//	// standard, то этот компонент будет искать файл свойств standard.properties как комплект ресурсов для данной темы.
//	@Bean
//	ResourceBundleThemeSource themeSource() {
//		return new ResourceBundleThemeSource();
//	}
//
//	// CookieThemeResolver распознает активную тему для пользователей. В частности, свойство DefaultThemeName определяет используемую
//	// по умолчанию тему standard. Для сохранения темы специально для пользователя в классе CookieThemeResolver применяются сооkiе-файлы.
//	// Имеется также класс SessionThemeResolver, обеспечивающий сохранение атрибута темы в пользовательском сеансе.
//	@Bean
//	CookieThemeResolver themeResolver() {
//		CookieThemeResolver cookieThemeResolver = new CookieThemeResolver();
//		cookieThemeResolver.setDefaultThemeName("standard");
//		cookieThemeResolver.setCookieMaxAge(3600);
//		cookieThemeResolver.setCookieName("theme");
//		return cookieThemeResolver;
//	}

	@Bean
	public Validator validator() {
		final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setValidationMessageSource(messageSource());
		return validator;
	}

	@Override
	public Validator getValidator() {
		return validator();
	}

	@Bean
	StandardServletMultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}

//	@Bean
//	WebContentInterceptor webChangeInterceptor() {
//		WebContentInterceptor webContentInterceptor = new WebContentInterceptor();
//		webContentInterceptor.setCacheSeconds(0);
//		webContentInterceptor.setSupportedMethods("GET", "POST", "PUT", "DELETE");
//		return webContentInterceptor;
//	}
}
