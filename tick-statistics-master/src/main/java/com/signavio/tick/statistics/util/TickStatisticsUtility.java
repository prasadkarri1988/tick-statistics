package com.signavio.tick.statistics.util;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TickStatisticsUtility {
	
	private static final Logger logger = LoggerFactory.getLogger(TickStatisticsUtility.class);
	
	private TickStatisticsUtility() {
	}

	public static Boolean isValidTimeStamp(Long currentTimeStamp, Long requestTimeStamp, Long validitySecond) {
		logger.debug("validating request timestamp :: {} ", requestTimeStamp);
		logger.debug("current timestamp :: {} ", currentTimeStamp);
		Long timeDifferenceInSeconds = TimeUnit.MILLISECONDS.toSeconds(currentTimeStamp-requestTimeStamp);
		logger.debug("timeDifferenceInMilliAecond :: {} ", timeDifferenceInSeconds);
		return timeDifferenceInSeconds.compareTo(validitySecond) < 0;
	}
	
	public static Long getThresholdTimeStamp(Integer slidingTimeinterval) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, slidingTimeinterval);
		logger.debug("thresholdTimeStamp {}",cal);
		return cal.getTime().getTime();
	}
}
