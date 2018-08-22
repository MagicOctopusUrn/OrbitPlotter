package net.cc.universe;

import java.util.List;

import java.util.ArrayList;

public class Mass {
	private final static List<Mass> UNIVERSE = new ArrayList<Mass>(0);
	
	private final Double mass;
	
	private final Double density;

	public static List<Mass> getUniverse() {
		return UNIVERSE;
	}

	public Mass(Double mass, Double density) {
		super();
		this.mass = mass;
		this.density = density;
	}

	public Double getMass() {
		return mass;
	}

	public Double getDensity() {
		return density;
	}
}
