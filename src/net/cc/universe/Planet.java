package net.cc.universe;

import net.cc.orbit.Orbit2D;
import net.cc.orbit.Orbit3D;

public class Planet {
	public static int STEPS = 1000;
	
	/**
	 * https://en.wikipedia.org/wiki/Sun (These units are super fucked up)
	 * TODO
	 */
	public static final Mass SUN_MASS = new Mass(333000.0 * 5.97,1.408);
	
	/**
	 * https://nssdc.gsfc.nasa.gov/planetary/factsheet/
	 * TODO Match every unit ever to this page forever.
	 */
	public static final Planet[] SOLAR_SYSTEM = {
			new Planet(new Orbit3D(Planet.SUN_MASS, new Mass(0.330, 5427.0), 
					46.0, 69.8, 29.124, 7.0, 48.331, Planet.STEPS)), // MERCURY
			new Planet(new Orbit3D(Planet.SUN_MASS, new Mass(4.87, 5243.0),
					107.5, 108.9, 54.884, 3.4, 76.680, Planet.STEPS)), // VENUS
			new Planet(new Orbit3D(Planet.SUN_MASS, new Mass(5.97, 5514.0), 
					147.1, 152.1, 114.20783, 0.0, -11.26064, Planet.STEPS)), // EARTH
			new Planet(new Orbit3D(Planet.SUN_MASS, new Mass(0.642, 3933.0), 
					206.6, 249.2, 286.502, 1.9, 49.558, Planet.STEPS)), // MARS
			new Planet(new Orbit3D(Planet.SUN_MASS, new Mass(1898.0, 1326.0), 
					740.5, 816.6, 273.867, 1.3, 100.464, Planet.STEPS)), // JUPITER
			new Planet(new Orbit3D(Planet.SUN_MASS, new Mass(568.0, 687.0), 
					1352.6, 1514.5, 339.392, 2.5, 113.665, Planet.STEPS)), // SATURN
			new Planet(new Orbit3D(Planet.SUN_MASS, new Mass(86.8, 1271.0), 
					2741.3, 3003.6, 96.998857, 0.8, 74.006, Planet.STEPS)), // ANUS
			new Planet(new Orbit3D(Planet.SUN_MASS, new Mass(102.0, 1638.0), 
					4444.5, 4545.7, 276.336, 1.8, 131.784, Planet.STEPS)), // NEPTUNE
			new Planet(new Orbit3D(Planet.SUN_MASS, new Mass(0.0146, 2095.0), 
					4436.8, 7375.9, 113.834, 17.2, 110.299, Planet.STEPS))  // PLUTO
			// IF PLUTO WAS A PLANET BEFORE IT CAN BE A PLANET AGAIN.
	};
	
	private final Orbit3D orbit;
	
	public Planet(Orbit3D orbit3d) {
		this.orbit = orbit3d;
	}

	public static Mass getSunMass() {
		return SUN_MASS;
	}

	public static Planet[] getSolarSystem() {
		return SOLAR_SYSTEM;
	}

	public Orbit3D getOrbit() {
		return orbit;
	}
}
