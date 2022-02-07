package util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatter implements Formatter<Date> {

	@Autowired
	private MessageSource messageSource;

	@Override
	public Date parse(String s, Locale locale) throws ParseException {
		return new SimpleDateFormat(messageSource.getMessage("date_format_pattern", new Object[]{}, locale)).parse(s);
	}

	@Override
	public String print(Date date, Locale locale) {
		return new SimpleDateFormat(messageSource.getMessage("date_format_pattern", new Object[]{}, locale)).format(date);
	}

}