package com.signavio.tick.statistics.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.signavio.tick.statistics.api.model.Statistics;
import com.signavio.tick.statistics.api.model.Tick;
import com.signavio.tick.statistics.model.StatisticsResponse;

@Service
public interface TickStatisticsService {
	
	Tick addTick(Tick tick);
	StatisticsResponse getStatistics(String instrument, Integer statisticsValidity);
	Statistics buildStatistics(List<Tick>  tickOverallList);
	Map<String, List<Tick>> getAllTicks();
	boolean isTimeStampValidity(long currentTimeStamp, long requestedTimeStamp, long  validity);

}
