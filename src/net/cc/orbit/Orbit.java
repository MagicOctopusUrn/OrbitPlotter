package net.cc.orbit;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import net.cc.universe.Mass;

public class Orbit {
	public static void main(String[] args) {
		int steps = 360;
		Mass earth = new Mass(1.0,3.9078);
		Mass sun = new Mass(333055.0,1.0);
		Orbit orbit = new Orbit(earth, sun, 2.0, 10.0, steps);
		DecimalFormat df = new DecimalFormat("#.######");
		System.out.println(orbit);
		for (int i = 0; i < steps; i++) {
			System.out.print(df.format(orbit.getTime()[i]));
			System.out.print("\t");
			System.out.print(df.format(orbit.getMeanAnomaly()[i]));
			System.out.print("\t");
			System.out.print(df.format(orbit.getEccentricAnomaly()[i]));
			System.out.print("\t");
			System.out.print(df.format(orbit.getTrueAnomaly()[i]));
			System.out.print("\t");
			System.out.print(df.format(orbit.getParticleX()[i]));
			System.out.print("\t");
			System.out.print(df.format(orbit.getParticleY()[i]));
			System.out.println();
		}
	}
	
	private final static int SIG_FIGS = 5;
	
	private final static double ACCURACY = Math.pow(10, SIG_FIGS * -1);

	private static final double SAMPLING_MAX = 30;
	
	private final Mass focus;
	
	private final Mass particle;
	
	private double eccentricy, semiMajor;
	
	private double apoapsis, perapsis;
	
	private final int steps;
	
	private final int[] time;
	
	private final double[] meanAnomaly;
	
	private final double[] trueAnomaly;
	
	private final double[] eccentricAnomaly;
	
	private final double[] velocity;
	
	private final double[] particleX;
	
	private final double[] particleY;

	private final double[] particleMeanX;

	private final double[] particleMeanY;

	public Orbit(Mass focus, Mass particle, double perapsis, double apoapsis, int steps) {
		super();
		this.focus = focus;
		this.particle = particle;
		this.apoapsis = apoapsis;
		this.perapsis = perapsis;
		this.steps = steps;
		this.time = new int[steps];
		this.meanAnomaly = new double[steps];
		this.eccentricAnomaly = new double[steps];
		this.trueAnomaly = new double[steps];
		this.velocity = new double[steps];
		this.particleX = new double[steps];
		this.particleY = new double[steps];
		this.particleMeanX = new double[steps];
		this.particleMeanY = new double[steps];
		this.eccentricy = (apoapsis - perapsis) / (apoapsis + perapsis);
		generateTime();
		generateMeanAnomaly();
		generateEccentricAnomaly();
		generateTrueAnomaly();
		generatePositionX();
		generatePositionY();
		generateMeanPositionX();
		generateMeanPositionY();
		generateVelocity();
	}

	private void generateTime() {
		for (int i = 0; i < steps; i++) {
			this.time[i] = i;
		}
	}

	private void generateMeanAnomaly() {
		for (int i = 0; i < steps; i++) {
			this.meanAnomaly[i] = (360.0/steps)*((double)i);
		}
	}

	private void generateEccentricAnomaly() {
		for (int i = 0; i < steps; i++) {
			double pi = Math.PI;
			double m = this.meanAnomaly[i];
			
			m /= 360.0;
			
			m = 2.0*pi*(m-Math.floor(m));
			
			double E, F, K = pi/180.0;
			
			if (this.eccentricy < 0.8) {
				E = m;
			} else {
				E = pi;
			}
			
			F = E - this.eccentricy * Math.sin(m) - m;
			
			int j = 0;
			while ((Math.abs(F) > Orbit.ACCURACY) && j < Orbit.SAMPLING_MAX) {
				E = E - F / (1.0 - this.eccentricy * Math.cos(E));
				F = E - this.eccentricy * Math.sin(E) - m;
				j++;
			}
			
			E = E / K;
			
			this.eccentricAnomaly[i] = Math.round(E / Orbit.ACCURACY) * Orbit.ACCURACY;
		}
	}

	private void generateTrueAnomaly() {
		for (int i = 0; i < steps; i++) {
			double K = Math.PI / 180.0;
			
			double E = this.eccentricAnomaly[i] * K;
			
			double F = Math.sqrt(1.0 - (this.eccentricy * this.eccentricy));
			
			double P = Math.atan2(F * Math.sin(E), Math.cos(E) - this.eccentricy) / K;
			
			this.trueAnomaly[i] = Math.round(P / Orbit.ACCURACY) * Orbit.ACCURACY;
		}
	}

	private void generatePositionX() {
		for (int i = 0; i < steps; i++) {
			double K = Math.PI / 180.0;
			
			double T = this.eccentricAnomaly[i] * K;
			
			double A = this.perapsis / (1 - this.eccentricy);
			
			double C = Math.cos(T);
			
			this.particleX[i] = A * (C - this.eccentricy);
		}
	}

	private void generatePositionY() {
		for (int i = 0; i < steps; i++) {
			double K = Math.PI / 180.0;
			
			double T = this.eccentricAnomaly[i] * K;
			
			double A = this.perapsis / (1 - this.eccentricy);
			
			double F = Math.sqrt(1.0 - this.eccentricy * this.eccentricy);
			
			double S = Math.sin(T);
			
			this.particleY[i] = A * F * S;
		}
	}

	private void generateMeanPositionX() {
		for (int i = 0; i < steps; i++) {
			double K = Math.PI / 180.0;
			
			double T = this.eccentricAnomaly[i] * K - 180.0;
			
			double A = this.perapsis / (1 - this.eccentricy);
			
			double C = Math.cos(T);
			
			this.particleMeanX[i] = A * (C - this.eccentricy);
		}
	}

	private void generateMeanPositionY() {
		for (int i = 0; i < steps; i++) {
			double K = Math.PI / 180.0;
			
			double T = this.eccentricAnomaly[i] * K - 180.0;
			
			double A = this.perapsis / (1 - this.eccentricy);
			
			double F = Math.sqrt(1.0 - this.eccentricy * this.eccentricy);
			
			double S = Math.sin(T);
			
			this.particleMeanY[i] = A * F * S;
		}
	}

	private void generateVelocity() {

	}

	public int[] getTime() {
		return time;
	}

	public double[] getMeanAnomaly() {
		return meanAnomaly;
	}

	public double[] getTrueAnomaly() {
		return trueAnomaly;
	}

	public double[] getEccentricAnomaly() {
		return eccentricAnomaly;
	}

	public double[] getParticleX() {
		return particleX;
	}

	public double[] getParticleY() {
		return particleY;
	}

	public double[] getParticleMeanX() {
		return particleMeanX;
	}

	public double[] getParticleMeanY() {
		return particleMeanY;
	}

	@Override
	public String toString() {
		return "Orbit [eccentricy=" + eccentricy + ", apoapsis=" + apoapsis + ", perapsis=" + perapsis + ", steps="
				+ steps + "]";
	}
}
