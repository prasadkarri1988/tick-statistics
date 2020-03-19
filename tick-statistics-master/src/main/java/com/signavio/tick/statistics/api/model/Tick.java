package com.signavio.tick.statistics.api.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class Tick {

	@NotEmpty(message = "instrument must not be null or empty")
	private String instrument;
	@Positive(message = "price must not be negitive or zero")
	private Double price;
	@Positive(message = "timestamp should be positive number")
	private Long timestamp;

	public Tick() {
		super();
	}

	public Tick(String instrument, Double price, Long timestamp) {
		super();
		this.instrument = instrument;
		this.price = price;
		this.timestamp = timestamp;
	}

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}

	@NotNull(message = "price cannot be null")
	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@NotNull(message = "timestamp cannot be null")
	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

}
