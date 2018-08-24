package net.cc.orbit;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

import net.cc.universe.Mass;
import net.cc.universe.Planet;

public class Orbit3D extends Orbit2D {
	public static void main(String[] args) throws Exception {
		Mass focus = new Mass(1.0, 1.0);
		Mass particle = new Mass(1.0, 1.0);
		double perhelion = 76.0917;
		double aphelion = 936.0;
		double perhelionArgument = 311.29;
		double orbitalInclination = 11.92;
		double ascendingNode = 144.546;
		int steps = 3600;
		Orbit3D orbit = new Orbit3D(focus, particle, perhelion, aphelion, perhelionArgument, orbitalInclination, ascendingNode, steps);
		AnalysisLauncher.open(new AbstractAnalysis() {
			@Override
		    public void init(){
		        float x;
		        float y;
		        float z;
		        
		        Coord3d[] points = new Coord3d[Planet.STEPS * 3]; 
		        org.jzy3d.colors.Color[] c = new org.jzy3d.colors.Color[Planet.STEPS * 3];
		        org.jzy3d.colors.Color[] cPlanet = {
		        		org.jzy3d.colors.Color.MAGENTA,
		        		org.jzy3d.colors.Color.YELLOW,
		        		org.jzy3d.colors.Color.GREEN,
		        		org.jzy3d.colors.Color.RED,
		        		org.jzy3d.colors.Color.random(),
		        		org.jzy3d.colors.Color.random(),
		        		org.jzy3d.colors.Color.BLUE,
		        		org.jzy3d.colors.Color.CYAN,
		        		org.jzy3d.colors.Color.random()
		        }; 
		        
		        for (int j = 0; j < 3; j++) {
		        	Planet p = Planet.SOLAR_SYSTEM[j];
		        	org.jzy3d.colors.Color cp = cPlanet[j];
		        	Orbit3D orbit = p.getOrbit();
			        for(int i = 0; i < orbit.getSteps(); i++){
			            x = (float)orbit.getParticleX()[i];
			            y = (float)orbit.getParticleY()[i];
			            z = (float)orbit.getParticleZ()[i];
			            System.out.println(x + "\t" + y + "\t" + z);
			            points[Planet.STEPS * j + i] = new Coord3d(x, y, z);
			            c[Planet.STEPS * j + i] = cp;
			            System.out.println(cp);
			            System.out.println(i*j + "\t" + Planet.STEPS * Planet.SOLAR_SYSTEM.length);
			        }
		        }
		        
		        Scatter scatter = new Scatter(points, c);
		        chart = AWTChartComponentFactory.chart(Quality.Advanced, "awt");
		        chart.getScene().add(scatter);
		        chart.setViewMode(ViewPositionMode.FREE);
		        
		    }
		});
	}
	
	private final static int SIG_FIGS = 5;
	
	private final static double ACCURACY = Math.pow(10, SIG_FIGS * -1);

	private static final double SAMPLING_MAX = 30;
	
	private double orbitalInclination, ascendingNode;
	
	protected final double[] velocityZ;
	
	protected final double[] particleZ;
	
	protected final double[] particleMeanZ;
	
	protected double centerZ;

	public Orbit3D(Mass focus, Mass particle, double perhelion,
			double aphelion, double perhelionArgument, double orbitalInclination, 
			double ascendingNode, int steps) {
		super(focus, particle, perhelion, aphelion, perhelionArgument, steps, false);
		this.particleZ = new double[steps];
		this.particleMeanZ = new double[steps];
		this.velocityZ = new double[steps];
		this.orbitalInclination = orbitalInclination;
		this.ascendingNode = ascendingNode;
		translatePositions();
	}

	public Orbit3D(Orbit2D orbit, double orbitalInclination, double ascendingNode) {
		super(orbit.getFocus(), orbit.getParticle(), orbit.getPerhelion(), 
				orbit.getAphelion(), orbit.getPerhelionArgument(), orbit.getSteps(),
				false);
		this.particleZ = new double[steps];
		this.particleMeanZ = new double[steps];
		this.velocityZ = new double[steps];
		this.orbitalInclination = orbitalInclination;
		this.ascendingNode = ascendingNode;
		translatePositions();
	}
	
	/**
	 * https://en.wikipedia.org/wiki/Orbital_elements
	 */
	private void translatePositions() {
		double K = Math.PI / 180.0;
		double an = this.ascendingNode * K;      // upper omega
		double oi = this.orbitalInclination * K; // i
		double pa = this.perhelionArgument * K;  // lower omega
		double[] t = {
			Math.cos(an) * Math.cos(pa) - Math.sin(an) * Math.cos(oi) * Math.sin(pa), //x1
			Math.sin(an) * Math.cos(pa) + Math.cos(an) * Math.cos(oi) * Math.sin(pa), //x2
			Math.sin(oi) * Math.sin(pa),                                              //x3
			-Math.cos(an) * Math.sin(pa) - Math.sin(an) * Math.cos(oi) * Math.cos(pa),//y1
			-Math.sin(an) * Math.sin(pa) + Math.cos(an) * Math.cos(oi) * Math.cos(pa),//y2
			Math.sin(oi) * Math.cos(pa),                                              //y3
			Math.sin(oi) * Math.sin(an),                                              //z1
			-Math.sin(oi) * Math.cos(an),                                             //z2
			Math.cos(oi)                                                              //z3
		};
		
		for (int i = 0; i < t.length; i++)
			System.out.println(t[i]);

		double i, j, k;
		for (int s = 0; s < steps; s++) {
			i = this.particleX[s];
			j = this.particleY[s];
			k = this.particleZ[s];
			
			this.particleX[s] = i * t[0] + j * t[1] + k * t[2];
			this.particleY[s] = i * t[3] + j * t[4] + k * t[5];
			this.particleZ[s] = i * t[6] + j * t[7] + k * t[8];
			
			i = this.particleMeanX[s];
			j = this.particleMeanY[s];
			k = this.particleMeanZ[s];
			
			this.particleMeanX[s] = i * t[0] + j * t[1] + k * t[2];
			this.particleMeanY[s] = i * t[1] + j * t[2] + k * t[3];
			this.particleMeanZ[s] = i * t[1] + j * t[2] + k * t[3];
			
			i = this.velocityX[s];
			j = this.velocityY[s];
			k = this.velocityZ[s];
			
			this.velocityX[s] = i * t[0] + j * t[1] + k * t[2];
			this.velocityY[s] = i * t[3] + j * t[4] + k * t[5];
			this.velocityZ[s] = i * t[6] + j * t[7] + k * t[8];
		}
		
		/*
		Transform3D t3d = new Transform3D(transformationMatrix);
		
		for (int i = 0; i < steps; i++) {
			Point3d particlePoint = 
					new Point3d(this.particleX[i], this.particleY[i], this.particleZ[i]);
			t3d.transform(new Point3d(particlePoint));
			this.particleX[i] = particlePoint.x;
			this.particleY[i] = particlePoint.y;
			this.particleZ[i] = particlePoint.z;
			
			Point3d particleMeanPoint = 
					new Point3d(this.particleMeanX[i], this.particleMeanY[i], 
							this.particleMeanZ[i]);
			t3d.transform(new Point3d(particleMeanPoint));
			this.particleMeanX[i] = particleMeanPoint.x;
			this.particleMeanY[i] = particleMeanPoint.y;
			this.particleMeanZ[i] = particleMeanPoint.z;
			
			Point3d velocityPoint = 
					new Point3d(this.velocityX[i], this.velocityY[i], 
							this.velocityZ[i]);
			t3d.transform(new Point3d(velocityPoint));
			this.velocityX[i] = velocityPoint.x;
			this.velocityY[i] = velocityPoint.y;
			this.velocityZ[i] = velocityPoint.z;
		}
		Point3d center = new Point3d(this.centerX, this.centerY, this.centerZ);
		t3d.transform(center);
		*/
		i = this.centerX;
		j = this.centerY;
		k = this.centerZ;
		
		this.centerX = i * t[1] + j * t[2] + k * t[3];
		this.centerY = i * t[1] + j * t[2] + k * t[3];
		this.centerZ = i * t[1] + j * t[2] + k * t[3];
	}

	public double getOrbitalInclination() {
		return orbitalInclination;
	}

	public double getAscendingNode() {
		return ascendingNode;
	}

	public double[] getVelocityZ() {
		return velocityZ;
	}

	public double[] getParticleZ() {
		return particleZ;
	}

	public double[] getParticleMeanZ() {
		return particleMeanZ;
	}

	public double getCenterZ() {
		return centerZ;
	}
}
