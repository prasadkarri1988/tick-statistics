package com.signavio.tick.statistics.api;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.signavio.tick.statistics.api.model.Tick;
import com.signavio.tick.statistics.exception.ErrorResponse;
import com.signavio.tick.statistics.exception.TickException;
import com.signavio.tick.statistics.model.StatisticsResponse;
import com.signavio.tick.statistics.service.TickStatisticsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(value = "/api")
@Api(value = "The main use case for that API is to provide real-time price\r\n" + 
		"statistics from the last 60 seconds (sliding time interval).")
public class TickStatisticsController {

	private static final Logger logger = LoggerFactory.getLogger(TickStatisticsController.class);
	
	@Autowired
	TickStatisticsService tickStatisticsServiceImpl;

	@Value("${tick.sliding.time.interval}")
	private Long tickValidity;
	
	@Value("${statistics.sliding.time.interval}")
	private Integer statisticsValidity;

	@PostMapping(value = "/ticks", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = {@ApiResponse(code = 204, message = "No Content"),
		      @ApiResponse(code = 201, message = "Created")})
	public ResponseEntity<?> createTicks(@Valid @RequestBody Tick tick) {

		if (!tickStatisticsServiceImpl.isTimeStampValidity(new Date().getTime(), tick.getTimestamp(), tickValidity)) {
			logger.error("Validation failed for timestamp received {}", tick.getTimestamp());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		tickStatisticsServiceImpl.addTick(tick);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	

	@GetMapping(value = "/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<StatisticsResponse> getOverallStatistics() {
	    return ResponseEntity.ok(tickStatisticsServiceImpl.getStatistics(null,statisticsValidity));
	  }
	
	@GetMapping(value = "/statistics/{instrument_identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<StatisticsResponse> getStatistics(@PathVariable(value = "instrument_identifier") String instrumentIdentifier) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, statisticsValidity);
		Long thresholdTimeStamp = cal.getTime().getTime();
	    logger.info("Fetching overall statistics for thresholdTimeStamp {}",thresholdTimeStamp);
	    return ResponseEntity.ok(tickStatisticsServiceImpl.getStatistics(instrumentIdentifier,statisticsValidity));
	  }
	
	@ExceptionHandler(TickException.class)
	public ResponseEntity<ErrorResponse> exceptionHandler(TickException ex) {
		logger.error("Exception ApplicationParseException :: {} ", ex.getMessage());
		ErrorResponse error = new ErrorResponse();
		error.setErrorCode(Integer.valueOf(1001));
		error.setErrorDesc(ex.getMessage());
		error.setTimestamp(new Date());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
		logger.error("Exception MethodArgumentNotValidException :: {} ", ex.getMessage());
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return errors;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(HttpMessageNotReadableException ex) {
		logger.error("Exception HttpMessageNotReadableException :: {} ", ex.getMessage());
		ErrorResponse error = new ErrorResponse();
		error.setErrorCode(Integer.valueOf(1002));
		error.setErrorDesc(ex.getMessage());
		error.setTimestamp(new Date());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
}
