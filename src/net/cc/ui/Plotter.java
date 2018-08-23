package net.cc.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Size2D;

import net.cc.orbit.Orbit;
import net.cc.universe.Mass;

public class Plotter extends JFrame implements ChangeListener, ActionListener, KeyListener, MouseWheelListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1896699599011793322L;

	private final ChartPanel orbitChartPanel;

	private final ChartPanel anomalyChartPanel;

	private final JTextField minAU, maxAU, steps;

	private final JSlider apoapsis, perapsis, time, perapsisArgument;

	private final JButton update;

	private final JTextArea console;

	public Plotter() {
		super("Orbital Plotter");
		this.setSize(600, 1000);

		JPanel southPanel = new JPanel(new BorderLayout());

		JPanel controlPanel = new JPanel(new GridLayout(1, 5));
		this.minAU = new JTextField("1.0");
		this.maxAU = new JTextField("100.0");
		this.steps = new JTextField("200");
		this.update = new JButton("Update");
		controlPanel.add(this.minAU);
		controlPanel.add(this.maxAU);
		controlPanel.add(this.steps);
		this.update.addKeyListener(this);
		this.update.addActionListener(this);
		controlPanel.add(this.update);

		JPanel sliderPanel = new JPanel(new GridLayout(1, 3));

		this.perapsis = new JSlider(0, 100);
		this.perapsis.addChangeListener(this);
		this.perapsis.setMinorTickSpacing(5);
		this.perapsis.setMajorTickSpacing(10);
		this.perapsis.setPaintTicks(true);
		sliderPanel.add(this.perapsis);

		this.apoapsis = new JSlider(0, 100);
		this.apoapsis.addChangeListener(this);
		this.apoapsis.setMinorTickSpacing(5);
		this.apoapsis.setMajorTickSpacing(10);
		this.apoapsis.setPaintTicks(true);
		sliderPanel.add(this.apoapsis);

		this.time = new JSlider(0, Integer.parseInt(this.steps.getText()) - 1, 0);
		this.time.addChangeListener(this);
		this.time.setMinorTickSpacing(5);
		this.time.setMajorTickSpacing(10);
		this.time.setPaintTicks(true);
		sliderPanel.add(this.time);

		this.perapsisArgument = new JSlider(0, 360);
		this.perapsisArgument.addChangeListener(this);
		this.perapsisArgument.setMinorTickSpacing(30);
		this.perapsisArgument.setMajorTickSpacing(90);
		this.perapsisArgument.setPaintTicks(true);
		sliderPanel.add(this.perapsisArgument);

		console = new JTextArea(10, 10);
		JScrollPane consoleScroll = new JScrollPane(console);
		consoleScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		consoleScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		this.orbitChartPanel = new ChartPanel(null);
		this.orbitChartPanel.addMouseWheelListener(this);
		this.orbitChartPanel.setPreferredSize(new Dimension(500, 500));
		this.add(this.orbitChartPanel, BorderLayout.NORTH);

		this.anomalyChartPanel = new ChartPanel(null);
		this.anomalyChartPanel.addMouseWheelListener(this);
		this.anomalyChartPanel.setPreferredSize(new Dimension(500, 250));
		this.add(this.anomalyChartPanel, BorderLayout.CENTER);

		southPanel.add(controlPanel, BorderLayout.NORTH);
		southPanel.add(sliderPanel, BorderLayout.CENTER);
		southPanel.add(consoleScroll, BorderLayout.SOUTH);
		this.add(southPanel, BorderLayout.SOUTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		this.setVisible(true);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Plotter().setVisible(true);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource().equals(this.update)) {
				int steps = Integer.parseInt(this.steps.getText());
				this.time.setMaximum(steps - 1);
				double minAU = Double.parseDouble(this.minAU.getText());
				double maxAU = Math.max(minAU + 1, Double.parseDouble(this.maxAU.getText()));
				this.perapsis.setMinimum((int) minAU);
				this.perapsis.setMaximum((int) Math.max(minAU, maxAU));
				this.perapsis.setMinorTickSpacing(5);
				this.perapsis.setMajorTickSpacing(10);
				this.perapsis.setPaintTicks(true);
				this.apoapsis.setMinimum((int) minAU);
				this.apoapsis.setMaximum((int) Math.max(minAU, maxAU));
				this.apoapsis.setMinorTickSpacing(5);
				this.apoapsis.setMajorTickSpacing(10);
				this.apoapsis.setPaintTicks(true);
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getScrollType() != MouseWheelEvent.WHEEL_UNIT_SCROLL)
			return;
		if (e.getWheelRotation() < 0)
			increaseZoom((JComponent) e.getComponent(), true);
		else
			decreaseZoom((JComponent) e.getComponent(), true);
	}

	public synchronized void increaseZoom(JComponent chart, boolean saveAction) {
		ChartPanel ch = (ChartPanel) chart;
		zoomChartAxis(ch, true);
	}

	public synchronized void decreaseZoom(JComponent chart, boolean saveAction) {
		ChartPanel ch = (ChartPanel) chart;
		zoomChartAxis(ch, false);
	}

	private void zoomChartAxis(ChartPanel chartP, boolean increase) {
		int width = chartP.getMaximumDrawWidth() - chartP.getMinimumDrawWidth();
		int height = chartP.getMaximumDrawHeight() - chartP.getMinimumDrawWidth();
		if (increase) {
			chartP.zoomInBoth(width / 2, height / 2);
		} else {
			chartP.zoomOutBoth(width / 2, height / 2);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		try {
			console.setText("");
			double perapsis = (double) (this.perapsis.getValue());
			double apoapsis = (double) (this.apoapsis.getValue());
			double perapsisArgument = (double)this.perapsisArgument.getValue();
			this.apoapsis.setMinimum((int)perapsis);
			int time = this.time.getValue();
			int steps = Integer.parseInt(this.steps.getText());
			Orbit orbit = new Orbit(new Mass(1.0, 1.0), new Mass(1.0, 1.0), perapsis, apoapsis, perapsisArgument, steps);
			DecimalFormat df = new DecimalFormat("#.######");
			for (int i = 0; i < steps; i++) {
				console.append(df.format(orbit.getTime()[i]));
				console.append("\t");
				console.append(df.format(orbit.getMeanAnomaly()[i]));
				console.append("\t");
				console.append(df.format(orbit.getEccentricAnomaly()[i]));
				console.append("\t");
				console.append(df.format(orbit.getTrueAnomaly()[i]));
				console.append("\t");
				console.append(df.format(orbit.getParticleX()[i]));
				console.append("\t");
				console.append(df.format(orbit.getParticleY()[i]));
				console.append("\t");
				console.append(df.format(orbit.getParticleMeanX()[i]));
				console.append("\t");
				console.append(df.format(orbit.getParticleMeanY()[i]));
				console.append("\n");
			}
			console.append(orbit.toString() + "\n");

			this.orbitChartPanel.setChart(getOrbitChart(steps, orbit, (int)time));

			this.anomalyChartPanel.setChart(getAnomalyChart(steps, orbit));
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public JFreeChart getAnomalyChart(int steps, Orbit orbit) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries meanSeries = new XYSeries("Mean Anomaly");
		XYSeries eccentricSeries = new XYSeries("Eccentric Anomaly");
		XYSeries trueSeries = new XYSeries("True Anomaly");

		for (int i = 1; i < steps; i++) {
			meanSeries.add(orbit.getTime()[i], orbit.getMeanAnomaly()[i]);
		}
		for (int i = 1; i < steps; i++) {
			eccentricSeries.add(orbit.getTime()[i], orbit.getEccentricAnomaly()[i]);
		}
		for (int i = 1; i < steps; i++) {
			if (orbit.getTrueAnomaly()[i] > 0)
				trueSeries.add(orbit.getTime()[i], orbit.getTrueAnomaly()[i]);
			else
				trueSeries.add(orbit.getTime()[i], 360 + orbit.getTrueAnomaly()[i]);
		}

		dataset.addSeries(meanSeries);
		dataset.addSeries(eccentricSeries);
		dataset.addSeries(trueSeries);

		String chartTitle = "Anomaly Plot";
		String xAxisLabel = "T (In units of Time)";
		String yAxisLabel = "Anomaly (In Degrees)";

		JFreeChart chart = ChartFactory.createScatterPlot(chartTitle, xAxisLabel, yAxisLabel, (XYDataset) dataset);

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

		// sets paint color for each series
		renderer.setSeriesShape(0, new Ellipse2D.Double(-0.5, -0.5, 1, 1));
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesStroke(0, new BasicStroke(1.0f));
		renderer.setSeriesShape(1, new Ellipse2D.Double(-0.5, -0.5, 1, 1));
		renderer.setSeriesPaint(1, Color.GREEN);
		renderer.setSeriesStroke(1, new BasicStroke(1.0f));
		renderer.setSeriesShape(2, new Ellipse2D.Double(-0.5, -0.5, 1, 1));
		renderer.setSeriesPaint(2, Color.YELLOW);
		renderer.setSeriesStroke(2, new BasicStroke(1.0f));

		chart.getXYPlot().setRenderer(renderer);

		return chart;
	}

	public JFreeChart getOrbitChart(int steps, Orbit orbit, int t) {
		XYSeriesCollection dataset = new XYSeriesCollection();

		XYSeries orbitSeries = new XYSeries("Orbit", false);
		for (int i = 0; i < steps; i++) {
			orbitSeries.add(orbit.getParticleX()[i], orbit.getParticleY()[i]);
		}
		orbitSeries.add(orbit.getParticleX()[0], orbit.getParticleY()[0]);

		dataset.addSeries(orbitSeries);

		XYSeries eccentricOrbitSeries = new XYSeries("Mean Orbit", false);
		for (int i = 0; i < steps; i++) {
			eccentricOrbitSeries.add(orbit.getParticleMeanX()[i], orbit.getParticleMeanY()[i]);
		}
		eccentricOrbitSeries.add(orbit.getParticleMeanX()[0], orbit.getParticleMeanY()[0]);

		dataset.addSeries(eccentricOrbitSeries);

		XYSeries focusSeries = new XYSeries("Focus");

		focusSeries.add(0.0, 0.0);

		dataset.addSeries(focusSeries);
		
		/*
		XYSeries angleSeries = new XYSeries("Measurement at T", false);

		angleSeries.add(orbit.getCenterX(), orbit.getCenterY());
		angleSeries.add(orbit.getParticleMeanX()[t], orbit.getParticleMeanY()[t]);
		angleSeries.add(orbit.getCenterX(), orbit.getCenterY());
		angleSeries.add(orbit.getPerapsis(), 0);
		angleSeries.add(-1 * orbit.getApoapsis(), 0);
		angleSeries.add(0, 0);
		angleSeries.add(orbit.getParticleX()[t], orbit.getParticleY()[t]);

		dataset.addSeries(angleSeries);
		*/
		
		String chartTitle = "Orbit Chart (X,Y)";
		String xAxisLabel = "X (In AU)";
		String yAxisLabel = "Y (In AU)";

		JFreeChart chart = ChartFactory.createScatterPlot(chartTitle, xAxisLabel, yAxisLabel, (XYDataset) dataset);

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

		// sets paint color for each series
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesShape(0, new Ellipse2D.Double(-1, -1, 2, 2));

		renderer.setSeriesPaint(1, Color.YELLOW);
		renderer.setSeriesShape(1, new Ellipse2D.Double(-1, -1, 2, 2));

		// sets thickness for series (using strokes)
		renderer.setSeriesFillPaint(2, Color.GREEN);
		renderer.setSeriesStroke(2, new BasicStroke(1.0f));
		renderer.setSeriesShape(2, new Ellipse2D.Double(-5, -5, 10, 10));

		// sets thickness for series (using strokes)
		for (int i = 3; i < 10; i++) {
			renderer.setSeriesFillPaint(i, Color.BLACK);
			renderer.setSeriesStroke(i, new BasicStroke(1.0f));
			renderer.setSeriesShape(i, new Ellipse2D.Double(-1, -1, 2, 2));
		}

		chart.getXYPlot().setRenderer(renderer);

		NumberAxis nax = (NumberAxis) chart.getXYPlot().getDomainAxis();
		NumberAxis nay = (NumberAxis) chart.getXYPlot().getRangeAxis();
		Range xrange = nax.getRange();
		Range yrange = nay.getRange();
		double xlen = Math.round(xrange.getLength());
		double ylen = Math.round(yrange.getLength());
		if (xlen < ylen)
			nax.setRangeAboutValue(xrange.getCentralValue(), ylen);
		else
			nay.setRangeAboutValue(yrange.getCentralValue(), xlen);

		return chart;
	}
}
