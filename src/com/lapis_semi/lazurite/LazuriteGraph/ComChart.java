package com.lapis_semi.lazurite.LazuriteGraph;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class ComChart extends JFrame implements SerialPortEventListener {
	private TimeSeriesCollection[] timeSeriesCollection = new TimeSeriesCollection[4]; // Collection
																						// of
																						// time
																						// series
																						// data
	// private XYDataset xyDataset; // dataset that will be used for the chart
	private TimeSeries[] seriesX = new TimeSeries[4]; // X series data
	private TimeSeries[] seriesY = new TimeSeries[4]; // Y series data
	private TimeSeries[] seriesZ = new TimeSeries[4]; // X series data
	JButton start;

	private BufferedReader input; // input reader
	// private OutputStream output; //output reader
	private SerialPort serialPort; // serial port object

	public ComChart(JButton start) {
		// super(title);
		this.start = start;
		initializeSerial();
		// System.out.println("TH
		// Enb"+String.valueOf(serialPort.isReceiveThresholdEnabled()));
		// System.out.println("TH
		// Thd"+String.valueOf(serialPort.getReceiveThreshold()));
		JFreeChart[] chart = new JFreeChart[4];
		XYPlot[] subplot = new XYPlot[4];
		CombinedDomainXYPlot plot = new CombinedDomainXYPlot();
		plot.setGap(10.0);

		for (int i = 0; i < 4; i++) {
			if (Param.comGraphEnb[i]) {
				timeSeriesCollection[i] = new TimeSeriesCollection();
				seriesX[i] = new TimeSeries(Param.comGraphAxisText[i] + "(X)");
				timeSeriesCollection[i].addSeries(seriesX[i]);
				if (Param.comLineNum[i] >= 2) {
					seriesY[i] = new TimeSeries(Param.comGraphAxisText[i] + "(Y)");
					timeSeriesCollection[i].addSeries(seriesY[i]);
				}
				if (Param.comLineNum[i] >= 3) {
					seriesZ[i] = new TimeSeries(Param.comGraphAxisText[i] + "(Z)");
					timeSeriesCollection[i].addSeries(seriesZ[i]);
				}
				chart[i] = ChartFactory.createTimeSeriesChart("", // title
						"Time", // x-axis label
						Param.comGraphAxisText[i], // y-axis label
						timeSeriesCollection[i], // data
						true, // create legend?
						true, // generate tooltips?
						false // generate URLs?
				);
				subplot[i] = chart[i].getXYPlot();
				plot.add(subplot[i]);
			}
		}
		JFreeChart master = new JFreeChart(Param.comGraphTitle, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
		ChartPanel panel = new ChartPanel(master, true, true, true, false, true);
		ValueAxis axis = new DateAxis("Time");
		axis.setAutoRange(true);
		axis.setFixedAutoRange(60000.0);
		master.getXYPlot().setDomainAxis(axis);
		setContentPane(panel);
		addWindowListener(new myListener());
	}

	public void actionPerformed(ActionEvent e) {
	}

	@Override
	public synchronized void serialEvent(SerialPortEvent event) {
		if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine = input.readLine();
				while(input.ready()){ inputLine = input.readLine(); }
				String[] inputValues = inputLine.split(",");

				if (inputValues[0].equals("STX") && inputValues[inputValues.length - 1].equals("ETX")) {
					int graphNum = 0;
					int dataNum = 1;
					while (graphNum < 4) {
						if (Param.comGraphEnb[graphNum]) {
							Millisecond ms = new Millisecond();
							float in_x = new Float(inputValues[dataNum]).floatValue();
							this.timeSeriesCollection[graphNum].getSeries(0).add(ms, in_x);
							dataNum++;

							if (Param.comLineNum[graphNum] >= 2) {
								float in_y = new Float(inputValues[dataNum]).floatValue();
								this.timeSeriesCollection[graphNum].getSeries(1).add(ms, in_y);
								dataNum++;
							}
							if (Param.comLineNum[graphNum] >= 3) {
								float in_z = new Float(inputValues[dataNum]).floatValue();
								this.timeSeriesCollection[graphNum].getSeries(2).add(ms, in_z);
								dataNum++;
							}
						}
						graphNum++;
					}
				}
				System.out.println(inputLine);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void initializeSerial() {
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currentPortIdentifier = (CommPortIdentifier) portEnum.nextElement();
			if (currentPortIdentifier.getName().equals(Param.comPort)) {
				portId = currentPortIdentifier;
				break;
			}
		}

		if (portId == null) {
			System.out.println("Port not found");
			return;
		}

		try {

			serialPort = (SerialPort) portId.open(this.getClass().getName(), 2000);
			serialPort.setSerialPortParams(Param.comBaud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			// output = serialPort.getOutputStream();

			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);

		} catch (Exception e) {
			System.err.println("Initialization failed : " + e.toString());
		}
	}

	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	public class myListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			close();
			start.setEnabled(true);
		}
	}

	/*
	 * public static void main(String[] args) { ComChart serialChartDemo = new
	 * ComChart("Time Series Chart Demo"); serialChartDemo.pack();
	 * RefineryUtilities.centerFrameOnScreen(serialChartDemo);
	 * serialChartDemo.setVisible(true); }
	 */
}
