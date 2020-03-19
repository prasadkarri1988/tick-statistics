package com.signavio.tick.statistics.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.signavio.tick.statistics.api.model.Tick;
import com.signavio.tick.statistics.service.TickStatisticsService;

@SpringBootTest
@AutoConfigureMockMvc
public class TickStatisticsControllerTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;
	
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
	
	@AfterEach
	public void clear() throws Exception {
		if(tickStatisticsServiceImpl.getAllTicks()!=null && tickStatisticsServiceImpl.getAllTicks().size()>0)
			tickStatisticsServiceImpl.getAllTicks().clear();
	}
	
	@Test
	public void test_createTick_notValidTimeStamp_return_noContent() throws Exception {
		int randamNumber = statisticsValidity + (int)(Math.random()*60*(-1));
		calender.add(Calendar.SECOND, randamNumber);
		mockMvc.perform(post("/api/ticks").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(new Tick("IBM.N", 143.82, calender.getTime().getTime()))))
				.andExpect(status().isNoContent()).andReturn();
	}
	
	@Test
	public void test_createTick_instrument_null_then_constrainViolation() throws Exception {
		int randamNumber = statisticsValidity + (int)(Math.random()*60);
		calender.add(Calendar.SECOND, randamNumber);
		mockMvc.perform(post("/api/ticks").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(new Tick(null, 143.82, calender.getTime().getTime()))))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.instrument").value("instrument must not be null or empty")).andReturn();
	}
	
	@Test
	public void test_createTick_price_negitive_then_constrainViolation() throws Exception {
		int randamNumber = statisticsValidity + (int)(Math.random()*60);
		calender.add(Calendar.SECOND, randamNumber);
		mockMvc.perform(post("/api/ticks").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(new Tick("IBM.N", -143.82, calender.getTime().getTime()))))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.price").value("price must not be negitive or zero")).andReturn();
	}
	
	@Test
	public void test_createTick_price_null_then_constrainViolation() throws Exception {
		int randamNumber = statisticsValidity + (int)(Math.random()*60);
		calender.add(Calendar.SECOND, randamNumber);
		mockMvc.perform(post("/api/ticks").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(new Tick("IBM.N", null, calender.getTime().getTime()))))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.price").value("price cannot be null")).andReturn();
	}
	
	@Test
	public void test_createTick_timestamp_negitive_then_constrainViolation() throws Exception {
		int randamNumber = statisticsValidity + (int)(Math.random()*60);
		calender.add(Calendar.SECOND, randamNumber);
		mockMvc.perform(post("/api/ticks").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(new Tick("IBM.N", 143.82, -calender.getTime().getTime()))))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.timestamp").value("timestamp should be positive number")).andReturn();
	}
	
	@Test
	public void test_createTick_timestamp_null_then_constrainViolation() throws Exception {
		int randamNumber = statisticsValidity + (int)(Math.random()*60);
		calender.add(Calendar.SECOND, randamNumber);
		mockMvc.perform(post("/api/ticks").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(new Tick("IBM.N", 143.82, null))))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.timestamp").value("timestamp cannot be null")).andReturn();
	}
	
	@Test
	public void test_getOverallStatistics_with_no_data() throws Exception {
		mockMvc.perform(get("/api/statistics"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.avg").value("0.0"))
				.andExpect(jsonPath("$.max").value("0.0"))
				.andExpect(jsonPath("$.min").value("0.0"))
				.andExpect(jsonPath("$.count").value("0")).andReturn();
	}
	
	@Test
	public void test_getInstrumentStatistics_with_no_data() throws Exception {
		mockMvc.perform(get("/api/statistics/IBM.N"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.avg").value("0.0"))
				.andExpect(jsonPath("$.max").value("0.0"))
				.andExpect(jsonPath("$.min").value("0.0"))
				.andExpect(jsonPath("$.count").value("0")).andReturn();
	}
	
	@Test
	public void test_getOverallStatistics_validitytime_single() throws Exception {
		int randamNumber = tickValidity + (int)(Math.random()*60*(-1));
		calender.add(Calendar.SECOND, randamNumber);
		mockMvc.perform(post("/api/ticks").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(new Tick("IBM.N", 143.82, calender.getTime().getTime()))))
				.andExpect(status().isCreated()).andReturn();
		mockMvc.perform(get("/api/statistics"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.avg").value("143.82"))
				.andExpect(jsonPath("$.max").value("143.82"))
				.andExpect(jsonPath("$.min").value("143.82"))
				.andExpect(jsonPath("$.count").value("1")).andReturn();
	}
	
	
	@Test
	public void test_getOverallStatistics_validitytime_miltiple() throws Exception {
		mockMvc.perform(post("/api/ticks").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(new Tick("IBM.N", 140.00, calender.getTime().getTime()))))
				.andExpect(status().isCreated()).andReturn();
		mockMvc.perform(post("/api/ticks").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(new Tick("IBM.M", 150.00, calender.getTime().getTime()))))
				.andExpect(status().isCreated()).andReturn();
		mockMvc.perform(get("/api/statistics"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.avg").value("145.0"))
				.andExpect(jsonPath("$.max").value("150.0"))
				.andExpect(jsonPath("$.min").value("140.0"))
				.andExpect(jsonPath("$.count").value("2")).andReturn();
	}

	@Test
	public void test_getOverallStatistics_validtime_and_invalidtime() throws Exception {
		mockMvc.perform(post("/api/ticks").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(new Tick("IBM.N", 140.00, calender.getTime().getTime()))))
				.andExpect(status().isCreated()).andReturn();
		int randamNumber = tickValidity + (int) (Math.random() * 60);
		calender.add(Calendar.SECOND, -randamNumber);
		Date pastDate = calender.getTime();
		Tick tick = new Tick("IBM.N", 143.86, pastDate.getTime());
		tick = tickStatisticsServiceImpl.addTick(tick);
		mockMvc.perform(get("/api/statistics"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.avg").value("140.0"))
			.andExpect(jsonPath("$.max").value("140.0"))
			.andExpect(jsonPath("$.min").value("140.0"))
			.andExpect(jsonPath("$.count").value("1")).andReturn();
	}
	
	@Test
	public void test_getInstrumentStatistics_validtime_single() throws Exception {
		mockMvc.perform(post("/api/ticks").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(new Tick("IBM.M", 150.00, calender.getTime().getTime()))))
				.andExpect(status().isCreated()).andReturn();
		mockMvc.perform(post("/api/ticks").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(new Tick("IBM.N", 140.00, calender.getTime().getTime()))))
				.andExpect(status().isCreated()).andReturn();
		mockMvc.perform(get("/api/statistics/IBM.N"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.avg").value("140.0"))
		.andExpect(jsonPath("$.max").value("140.0"))
		.andExpect(jsonPath("$.min").value("140.0"))
		.andExpect(jsonPath("$.count").value("1")).andReturn();
	}
	
	@Test
	public void test_getInstrumentStatistics_validtime_multiple() throws Exception {
		mockMvc.perform(post("/api/ticks").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(new Tick("IBM.N", 150.00, calender.getTime().getTime()))))
				.andExpect(status().isCreated()).andReturn();
		mockMvc.perform(post("/api/ticks").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(new Tick("IBM.N", 140.00, calender.getTime().getTime()))))
				.andExpect(status().isCreated()).andReturn();
		mockMvc.perform(get("/api/statistics/IBM.N"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.avg").value("145.0"))
		.andExpect(jsonPath("$.max").value("150.0"))
		.andExpect(jsonPath("$.min").value("140.0"))
		.andExpect(jsonPath("$.count").value("2")).andReturn();
	}
	
	@Test
	public void test_getInstrumentStatistics_validtime_multiple_invalidtime() throws Exception {
		mockMvc.perform(post("/api/ticks").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(new Tick("IBM.N", 150.00, calender.getTime().getTime()))))
				.andExpect(status().isCreated()).andReturn();
		mockMvc.perform(post("/api/ticks").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(new Tick("IBM.N", 140.00, calender.getTime().getTime()))))
				.andExpect(status().isCreated()).andReturn();
		int randamNumber = tickValidity + (int) (Math.random() * 60);
		calender.add(Calendar.SECOND, -randamNumber);
		Date pastDate = calender.getTime();
		Tick tick = new Tick("IBM.N", 143.86, pastDate.getTime());
		tick = tickStatisticsServiceImpl.addTick(tick);
		mockMvc.perform(get("/api/statistics/IBM.N"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.avg").value("145.0"))
		.andExpect(jsonPath("$.max").value("150.0"))
		.andExpect(jsonPath("$.min").value("140.0"))
		.andExpect(jsonPath("$.count").value("2")).andReturn();
	}
}
