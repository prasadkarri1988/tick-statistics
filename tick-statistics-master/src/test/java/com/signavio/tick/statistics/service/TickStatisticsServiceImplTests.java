package com.signavio.tick.statistics.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.signavio.tick.statistics.api.model.Tick;

@SpringBootTest
public class TickStatisticsServiceImplTests {

	private static final Logger logger = LoggerFactory.getLogger(TickStatisticsServiceImplTests.class);

	@Autowired
	TickStatisticsService tickStatisticsServiceImpl;

	@Value("${tick.sliding.time.interval}")
	private int tickValidity;

	@Value("${statistics.sliding.time.interval}")
	private int statisticsValidity;

	Calendar calender;

	@BeforeEach
	public void setup() throws Exception {
		calender = Calendar.getInstance();
	}

	@Test
	public void test_validTimeStamp() throws Exception {
		Date requestDate = calender.getTime();
		int randamNumber = (int) (Math.random() * 60);
		logger.debug("test_createTick_validTimeStamp randamNumber  {}", randamNumber);
		calender.add(Calendar.SECOND, randamNumber);
		Date currentDate = calender.getTime();
		Boolean validity = tickStatisticsServiceImpl.isTimeStampValidity(currentDate.getTime(), requestDate.getTime(),
				tickValidity);
		assertEquals(Boolean.TRUE, validity);
	}

	@Test
	public void test_nonValidTimeStamp() throws Exception {
		Date requestDate = calender.getTime();
		int randamNumber = tickValidity + (int) (Math.random() * 60);
		logger.debug("test_createdTick_nonValidTimeStamp randamNumber  {}", randamNumber);
		calender.add(Calendar.SECOND, randamNumber);
		Date currentDate = calender.getTime();
		Boolean validity = tickStatisticsServiceImpl.isTimeStampValidity(currentDate.getTime(), requestDate.getTime(),
				tickValidity);
		assertEquals(Boolean.FALSE, validity);
	}
	
	@Test
	public void test_addTick() throws Exception {
		Tick tick = new Tick("IBM.N", 143.86, calender.getTime().getTime());
		tick = tickStatisticsServiceImpl.addTick(tick);
		Map<String, List<Tick>> tickMap = tickStatisticsServiceImpl.getAllTicks();
		List<Tick> tickList = tickMap.get("IBM.N");
		assertEquals(1,tickList.size());
	}
	
	
}
