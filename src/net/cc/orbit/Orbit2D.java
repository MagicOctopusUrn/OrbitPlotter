package net.cc.orbit;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import net.cc.universe.Mass;

public class Orbit2D {
	private final static int SIG_FIGS = 5;
	
	private final static double ACCURACY = Math.pow(10, SIG_FIGS * -1);

	private static final double SAMPLING_MAX = 30;
	
	protected final Mass focus;
	
	protected final Mass particle;
	
	protected double eccentricy, semiMajor;
	
	protected double aphelion, perhelion;
	
	protected final int steps;
	
	protected final int[] time;
	
	protected final double[] meanAnomaly;
	
	protected final double[] trueAnomaly;
	
	protected final double[] eccentricAnomaly;
	
	protected final double[] velocityX;
	
	protected final double[] velocityY;
	
	protected final double[] particleX;
	
	protected final double[] particleY;

	protected final double[] particleMeanX;

	protected final double[] particleMeanY;
	
	protected double centerX, centerY, perhelionArgument;

	public Orbit2D(Mass focus, Mass particle, double perhelion, double aphelion,
			double perhelionArgument, int steps, boolean transform) {
		super();
		this.focus = focus;
		this.particle = particle;
		this.aphelion = aphelion;
		this.perhelion = perhelion;
		this.steps = steps;
		this.time = new int[steps];
		this.meanAnomaly = new double[steps];
		this.eccentricAnomaly = new double[steps];
		this.trueAnomaly = new double[steps];
		this.velocityX = new double[steps];
		this.velocityY = new double[steps];
		this.particleX = new double[steps];
		this.particleY = new double[steps];
		this.particleMeanX = new double[steps];
		this.particleMeanY = new double[steps];
		this.eccentricy = (aphelion - perhelion) / (aphelion + perhelion);
		this.perhelionArgument = perhelionArgument;
		generateTime();
		generateMeanAnomaly();
		generateEccentricAnomaly();
		generateTrueAnomaly();
		generatePositionX();
		generatePositionY();
		generateMeanPositionX();
		generateMeanPositionY();
		generateVelocity();
		generateCenter();
		if (transform)
			translatePositions();
	}

	private void generateCenter() {
		double K = Math.PI / 180.0;
		double A = this.perhelion / (1 - this.eccentricy);
		double T = 90 * K;
		double C = Math.cos(T);
		this.centerX = A * (C - this.eccentricy);
		this.centerY = 0;
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

	/**
	 * http://www.jgiesen.de/kepler/kepler.html
	 */
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
			while ((Math.abs(F) > Orbit2D.ACCURACY) && j < Orbit2D.SAMPLING_MAX) {
				E = E - F / (1.0 - this.eccentricy * Math.cos(E));
				F = E - this.eccentricy * Math.sin(E) - m;
				j++;
			}
			
			E = E / K;
			
			this.eccentricAnomaly[i] = Math.round(E / Orbit2D.ACCURACY) * Orbit2D.ACCURACY;
		}
	}

	/**
	 * http://www.jgiesen.de/kepler/kepler.html
	 */
	private void generateTrueAnomaly() {
		for (int i = 0; i < steps; i++) {
			double K = Math.PI / 180.0;
			
			double E = this.eccentricAnomaly[i] * K;
			
			double F = Math.sqrt(1.0 - (this.eccentricy * this.eccentricy));
			
			double P = Math.atan2(F * Math.sin(E), Math.cos(E) - this.eccentricy) / K;
			
			this.trueAnomaly[i] = Math.round(P / Orbit2D.ACCURACY) * Orbit2D.ACCURACY;
		}
	}

	/**
	 * http://www.jgiesen.de/kepler/kepler.html
	 */
	private void generatePositionX() {
		for (int i = 0; i < steps; i++) {
			double K = Math.PI / 180.0;
			
			double A = this.perhelion / (1 - this.eccentricy);
			
			double T = this.trueAnomaly[i] * K;
			
			double C = Math.cos(T);
			
			this.particleX[i] = A * (C - this.eccentricy);
		}
	}
	
	/**
	 * https://en.wikipedia.org/wiki/Transformation_matrix
	 */
	private void translatePositions() {
		double K = Math.PI / 180.0;
		double pa = this.perhelionArgument * K;
		for (int i = 0; i < steps; i++) {
			double x = this.particleX[i];
			double y = this.particleY[i];
			double xMean = this.particleMeanX[i];
			double yMean = this.particleMeanY[i];
			double xVelocity = this.velocityX[i];
			double yVelocity = this.velocityY[i];
			this.particleX[i] = x * Math.cos(pa) + y * Math.sin(pa);
			this.particleY[i] = y * Math.cos(pa) - x * Math.sin(pa);
			this.particleMeanX[i] = xMean * Math.cos(pa) + yMean * Math.sin(pa);
			this.particleMeanY[i] = yMean * Math.cos(pa) - xMean * Math.sin(pa);
			this.velocityX[i] = xVelocity * Math.cos(pa) + yVelocity * Math.sin(pa);
			this.velocityY[i] = yVelocity * Math.cos(pa) - xVelocity * Math.sin(pa);
		}
		this.centerX = this.centerX * Math.cos(pa) + this.centerY * Math.sin(pa);
		this.centerY = this.centerY * Math.cos(pa) - this.centerX * Math.sin(pa);
	}

	private void generatePositionY() {
		for (int i = 0; i < steps; i++) {
			double K = Math.PI / 180.0;
			
			double T = this.trueAnomaly[i] * K;
			
			double A = this.perhelion / (1 - this.eccentricy);
			
			double F = Math.sqrt(1.0 - this.eccentricy * this.eccentricy);
			
			double S = Math.sin(T);
			
			this.particleY[i] = A * F * S;
		}
	}

	private void generateMeanPositionX() {
		for (int i = 0; i < steps; i++) {
			double K = Math.PI / 180.0;
			
			double A = this.perhelion / (1 - this.eccentricy);
			
			double T = this.meanAnomaly[i] * K;
			
			double C = Math.cos(T);
			
			this.particleMeanX[i] = A * (C - this.eccentricy);
		}
	}

	private void generateMeanPositionY() {
		for (int i = 0; i < steps; i++) {
			double K = Math.PI / 180.0;
			
			double A = this.perhelion / (1 - this.eccentricy);
			
			double T = this.meanAnomaly[i] * K;
			
			double S = Math.sin(T);
			
			this.particleMeanY[i] = A * S ;
		}
	}

	private void generateVelocity() {
		for (int i = 0; i < steps; i++) {
			double K = Math.PI / 180.0;
			
			double A = this.perhelion / (1 - this.eccentricy);
			
			double M = this.meanAnomaly[i] * K;
			
			double C = Math.cos(M);
			
			double S = Math.sin(M);
			
			if (this.meanAnomaly[i] != 90 && this.meanAnomaly[i] != 270)
				this.velocityX[i] = Math.signum(C) * Math.sqrt(A / (Math.abs(C) * A * (1.0 - this.eccentricy * this.eccentricy)));

			if (this.meanAnomaly[i] != 0 && this.meanAnomaly[i] != 180)
				this.velocityY[i] = Math.signum(S) * Math.sqrt(A / (Math.abs(S) * A * (1.0 - this.eccentricy * this.eccentricy)));
		}
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

	public double[] getVelocityX() {
		return velocityX;
	}

	public double[] getVelocityY() {
		return velocityY;
	}

	public double getEccentricy() {
		return eccentricy;
	}

	public void setEccentricy(double eccentricy) {
		this.eccentricy = eccentricy;
	}

	public double getSemiMajor() {
		return semiMajor;
	}

	public void setSemiMajor(double semiMajor) {
		this.semiMajor = semiMajor;
	}

	public double getAphelion() {
		return aphelion;
	}

	public void setAphelion(double aphelion) {
		this.aphelion = aphelion;
	}

	public double getPerhelion() {
		return perhelion;
	}

	public void setPerhelion(double perhelion) {
		this.perhelion = perhelion;
	}

	public int getSteps() {
		return steps;
	}

	public double getCenterX() {
		return centerX;
	}

	public double getCenterY() {
		return centerY;
	}
	
	public double getPerhelionArgument() {
		return perhelionArgument;
	}

	public Mass getFocus() {
		return focus;
	}

	public Mass getParticle() {
		return particle;
	}

	@Override
	public String toString() {
		return "Orbit [eccentricy=" + eccentricy + ", aphelion=" + aphelion + ", perhelion=" + perhelion + ", steps="
				+ steps + ", perhelionArgument=" + perhelionArgument + "]";
	}
}
