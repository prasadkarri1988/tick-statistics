package com.signavio.tick.statistics.api.model;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;


public final class Statistics {

	private AtomicReference<BigDecimal> total = new AtomicReference<>(BigDecimal.ZERO);
	private AtomicReference<BigDecimal> min = new AtomicReference<>(BigDecimal.ZERO);
	private AtomicReference<BigDecimal> max = new AtomicReference<>(BigDecimal.ZERO);
	private AtomicLong count = new AtomicLong(0);

	public Statistics() {
		super();
	}

	public Statistics(AtomicReference<BigDecimal> total, AtomicReference<BigDecimal> min, AtomicReference<BigDecimal> max,
			AtomicLong count) {
		super();
		this.total = total;
		this.min = min;
		this.max = max;
		this.count = count;
	}

	public AtomicReference<BigDecimal> getTotal() {
		return total;
	}

	public void setTotal(AtomicReference<BigDecimal> total) {
		this.total = total;
	}

	public AtomicReference<BigDecimal> getMin() {
		return min;
	}

	public void setMin(AtomicReference<BigDecimal> min) {
		this.min = min;
	}

	public AtomicReference<BigDecimal> getMax() {
		return max;
	}

	public void setMax(AtomicReference<BigDecimal> max) {
		this.max = max;
	}

	public AtomicLong getCount() {
		return count;
	}

	public void setCount(AtomicLong count) {
		this.count = count;
	}

}
