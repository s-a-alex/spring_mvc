package util;

import config.DataServiceConfig;
import dao.SingerService;
import entity.Singer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SuppressWarnings("unchecked")
public class SingerDaoTest {
	private static Logger logger = LoggerFactory.getLogger(SingerDaoTest.class);

	private GenericApplicationContext ctx;
	private SingerService singerService;

	@Before
	public void setUp() {
		ctx = new AnnotationConfigApplicationContext(DataServiceConfig.class);
		singerService=ctx.getBean(SingerService.class);
		assertNotNull(singerService);
	}

	@Test
	public void testFindAll() {
		logger.info("testFindAll");
		List<Singer> singers = singerService.findAll();
		assertEquals(14, singers.size());
		listSingers(singers);
		logger.info("\n\n\n");
	}

	private static void listSingers(List<Singer> singers) {
		logger.info(" ---- Listing singers:");
		for (Singer singer : singers) {
			logger.info(singer.toString());
		}
	}

	@After
	public void tearDown() {
		ctx.close();
	}
}
