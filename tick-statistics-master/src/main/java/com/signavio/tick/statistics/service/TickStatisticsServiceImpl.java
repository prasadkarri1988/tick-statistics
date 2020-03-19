package com.signavio.tick.statistics.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.signavio.tick.statistics.api.model.Statistics;
import com.signavio.tick.statistics.api.model.Tick;
import com.signavio.tick.statistics.model.StatisticsResponse;
import com.signavio.tick.statistics.util.TickStatisticsUtility;

@Service
public class TickStatisticsServiceImpl implements TickStatisticsService {

	private static final Logger logger = LoggerFactory.getLogger(TickStatisticsServiceImpl.class);
	
	private final Map<String, List<Tick>> allTicks = new ConcurrentHashMap<>();
	
	@Override
	public boolean isTimeStampValidity(long currentTimeStamp, long requestedTimeStamp, long validity) {
		return TickStatisticsUtility.isValidTimeStamp(currentTimeStamp, requestedTimeStamp, validity);
	}

	@Override
	public Tick addTick(Tick tick) {
		
		List<Tick> tickList = allTicks.get(tick.getInstrument());
	    if (Objects.isNull(tickList)) {
	    	logger.debug("List is empty for instrument {} ",tick.getInstrument());
	      tickList = new ArrayList<>();
	    }
	    logger.debug("adding Tick for insrument {} ",tick.getInstrument());
	    tickList.add(tick);
	    allTicks.put(tick.getInstrument(), tickList);
	    return tick;
	}

	@Override
	public StatisticsResponse getStatistics(String instrumentInput, Integer statisticsValidity) {
		
		final Long thresholdTimeStamp = TickStatisticsUtility.getThresholdTimeStamp(statisticsValidity);
		
		List<Tick> tickOverallList = new ArrayList<>();
		allTicks.forEach((instrument, tickList)->{
			tickList = getFilteredTickList(tickList, thresholdTimeStamp, instrumentInput);
			logger.debug("adding list size of {} for instrument {}",tickList.size(),instrument);
			tickOverallList.addAll(tickList);
		});
		 
		Statistics statistics = buildStatistics(tickOverallList);
		
		long count =1;
		if(statistics.getCount().get()!=0)
			count = statistics.getCount().get();
		
		return new StatisticsResponse(statistics.getTotal().get().divide(BigDecimal.valueOf(count),2,RoundingMode.HALF_UP).doubleValue(), 
				statistics.getMax().get().doubleValue(), statistics.getMin().get().doubleValue(), statistics.getCount().get());
			
	}
	
	@Override
	public Statistics buildStatistics(List<Tick> tickOverallList) {
		Statistics statistics = new Statistics();
		if(!tickOverallList.isEmpty()) {
			logger.debug("tickOverallList size{}",tickOverallList.size());
			tickOverallList.stream().forEach(tick -> {
				BigDecimal price = BigDecimal.valueOf(tick.getPrice());
				statistics.getTotal().getAndSet(price.add(statistics.getTotal().get()));
				if(statistics.getMax().get().compareTo(price) < 0)
					statistics.getMax().getAndSet(price);
				if(statistics.getCount().get() == 0 || (statistics.getMin().get().compareTo(price) > 0))
					statistics.getMin().getAndSet(price);
				statistics.getCount().getAndIncrement();
			});
		}
		
		return statistics;
	}

	@Override
	public Map<String, List<Tick>> getAllTicks() {
		return allTicks;
	}
	
	private List<Tick> getFilteredTickList(List<Tick> tickList, Long thresholdTimeStamp, String instrumentInput){
		
		final Boolean instrumentFlag = instrumentInput!=null && !instrumentInput.isEmpty() ;
		if(Boolean.TRUE.equals(instrumentFlag) && !tickList.isEmpty())
			return tickList.stream().filter(tick -> (tick.getTimestamp() > thresholdTimeStamp 
					&& tick.getInstrument().equalsIgnoreCase(instrumentInput))).collect(Collectors.toList());
		else
			return tickList.stream().filter(tick -> tick.getTimestamp() > thresholdTimeStamp).collect(Collectors.toList());
	}
}
