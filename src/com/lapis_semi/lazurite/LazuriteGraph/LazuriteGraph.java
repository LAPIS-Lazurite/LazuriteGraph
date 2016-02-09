package com.lapis_semi.lazurite.LazuriteGraph;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.jfree.ui.RefineryUtilities;

import gnu.io.CommPortIdentifier;


public class LazuriteGraph extends JFrame implements ActionListener {
	private JLabel comGraphTitleLabel = new JLabel();
	private JTextField comGraphTitleText = new JTextField();
	private JLabel[] comGraphLabel = new JLabel[4];
	private JCheckBox[] comGraphEnb = new JCheckBox[4];
	private JTextField[] comGraphAxisText = new JTextField[4];
	static ComChart chart;

	private JComboBox[] comLineNum = new JComboBox[4];
	JTabbedPane SettingFrame;
	JPanel tabCom;
	JPanel tabSubGHz;
	static JButton graphStart;
	static String FilePath;

	Param fm;

	public static void main(String[] args) {
		LazuriteGraph graph;
//		System.out.println(System.getProperty("java.library.path"));
		if(args.length>0 ) {
			graph = new LazuriteGraph(args[0]);
		} else {
			graph = new LazuriteGraph();
		}
		graph.setVisible(true);
		graph.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// graph.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("***** end of program *****");
				LazuriteGraph.close();
			}
		});
	}

	public LazuriteGraph(String file) {
		CreateWindow();
		File graphFile = new File(file);
		loadGraphFile(graphFile);
		FilePath = graphFile.getParent();
	}

	public LazuriteGraph() {
		CreateWindow();
	}

	static public void close() {
		Param.putDefaultPath(new File("Graph.ini"), FilePath);
	}

	private void CreateWindow()
	{
		fm = new Param(new File("graph.pref"));
		FilePath = fm.getDefaultPath(new File("Graph.ini"));

		JMenuBar menubar = new JMenuBar();

		JMenu MenuFile = new JMenu("File");

		menubar.add(MenuFile);

		JMenuItem MenuNew = new JMenuItem(Param.Label_File_New);
		JMenuItem MenuOpen = new JMenuItem(Param.Label_File_Open);
		JMenuItem MenuSave = new JMenuItem(Param.Label_File_Save);
		JMenuItem MenuQuit = new JMenuItem(Param.Label_File_Quit);

		MenuFile.add(MenuNew);
		MenuFile.add(MenuOpen);
		MenuFile.add(MenuSave);
		MenuFile.add(MenuQuit);

		MenuNew.addActionListener(this);
		MenuOpen.addActionListener(this);
		MenuSave.addActionListener(this);
		MenuQuit.addActionListener(this);

		initParam();

		tabCom = new ComSetting();
		tabSubGHz = new SubGHzSetting();

		SettingFrame = new JTabbedPane();
		SettingFrame.addTab("COM", tabCom);
		SettingFrame.addTab("SubGHz", tabSubGHz);
		SettingFrame.setBounds(0, 0, 600, 200);

		GraphSetting graphSetting = new GraphSetting();

		graphStart = new JButton(Param.Label_Start);
		graphStart.addActionListener(this);

		setLayout(null);

		graphSetting.setBounds(0, 0, 600, 200);
		graphStart.setBounds(240,210,100,30);
		SettingFrame.setBounds(0,240 , 600, 200);

		add(SettingFrame);
		add(graphSetting);
		add(graphStart);

		setTitle("Setting Window");
		setJMenuBar(menubar);
		setBounds(10, 10, 600, 480);

	    ImageIcon icon = new ImageIcon(Param.App_Icon);
	    setIconImage(icon.getImage());

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if (cmd == Param.Label_File_New) {
			FileNew();
		} else if (cmd == Param.Label_File_Open) {
			FileOpen();
		} else if (cmd == Param.Label_File_Save) {
			FileSave();
		} else if (cmd == Param.Label_File_Quit) {
			System.exit(0);
		} else if (cmd == Param.Label_Start) {
			GraphOpen();
		}
	}

	// reset panel
	private void FileNew() {
		initParam();
		((ComSetting) tabCom).setParam();
	}

	// click open
	private void FileOpen() {
		// create Open File dialog
//		JFileChooser GraphFileDialog = new JFileChooser(FilePath);
		FileDialog dialog = new FileDialog(this,"File Open",FileDialog.LOAD);
		dialog.setDirectory(FilePath);
		dialog.setFile("*.graph");
		dialog.setVisible(true);
        String path = dialog.getDirectory();
        String file = dialog.getFile();
        if(file != null){
			File graphFile = new File(path+file);
			// System.out.println(GraphFileDialog.getName());
			// get paramter from *.graph
			loadGraphFile(graphFile);
			// store latest directory
			FilePath = graphFile.getParent();
		}
	}

	private void FileSave() {
		// create File dialog for save
		FileDialog dialog = new FileDialog(this,"File Open",FileDialog.SAVE);
		dialog.setDirectory(FilePath);
		dialog.setFile("*.graph");
		dialog.setVisible(true);
        String file = dialog.getDirectory()+dialog.getFile();
        int point = file.lastIndexOf(".");
        if(point > 0)  {
        	file = file.substring(0,point)+".graph";
        } else {
        	file = file+".graph";
        }
        if(file != null){
			File graphFile = new File(file);
			// write parameter
			writeGraphFile(graphFile);
			// store latest directory
			FilePath = graphFile.getParent();
		}
	}

	private void GraphOpen() {
		graphStart.setEnabled(false);
		((ComSetting) tabCom).getParam();
		chart = new ComChart(graphStart);
		chart.pack();
		RefineryUtilities.centerFrameOnScreen(chart);
		chart.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		chart.setVisible(true);
		chart.addWindowListener(new WindowAdapter() {
			public void windowClosing() {
				LazuriteGraph.GraphClose();
			}
		});
	}

	static void GraphClose() {
		chart.close();
		graphStart.setEnabled(false);
	}

	//
	private void initParam() {
		for (int i = 0; i < 4; i++) {
			Param.comGraphEnb[i] = false;
			Param.comGraphAxisText[i] = "";
			Param.comLineNum[i] = 1;
		}
		Param.comGraphTitle = "";
		Param.comGraphEnb[0] = true;
		Param.comPort = "";
		Param.comBaud = 0;
	}

	private boolean loadGraphFile(File graphFile) {
		Properties properties = new Properties();
		try {
			InputStream inputStream = new FileInputStream(graphFile);
			properties.load(inputStream);
			inputStream.close();
			for (int i = 0; i < 4; i++) {
				Param.comGraphEnb[i] = Boolean.valueOf(properties.getProperty("comGraphEnb" + String.valueOf(i)));
				Param.comGraphAxisText[i] = properties.getProperty("comGraphAxisText" + String.valueOf(i));
				Param.comLineNum[i] = Integer.parseInt(properties.getProperty("comLineNum" + String.valueOf(i)));
			}
			Param.comGraphTitle = properties.getProperty("comGraphTitle");
			Param.comBaud = Integer.parseInt(properties.getProperty("comBaud"));
			Param.comPort = properties.getProperty("comPort");
			SettingFrame.setSelectedIndex(Integer.parseInt(properties.getProperty("selectedTab")));
			((ComSetting) tabCom).setParam();

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return true;
	}

	private boolean writeGraphFile(File graphFile) {

		((ComSetting) tabCom).getParam();

		Properties properties = new Properties();

		properties.setProperty("selectedTab", String.valueOf(SettingFrame.getSelectedIndex()));

		for (int i = 0; i < 4; i++) {
			properties.setProperty("comGraphEnb" + String.valueOf(i), Boolean.toString(Param.comGraphEnb[i]));
			properties.setProperty("comGraphAxisText" + String.valueOf(i), Param.comGraphAxisText[i]);
			properties.setProperty("comLineNum" + String.valueOf(i), Integer.toString(Param.comLineNum[i]));
		}
		properties.setProperty("comGraphTitle", Param.comGraphTitle);
		properties.setProperty("comBaud", Integer.toString(Param.comBaud));
		if (Param.comPort == null) {
			Param.comPort = "";
		}
		properties.setProperty("comPort", Param.comPort);

		try {
			FileOutputStream outputStream = new FileOutputStream(graphFile);
			properties.store(outputStream, "store test"); // 書き込み
			outputStream.close();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return true;
	}

	public class ComSetting extends JPanel implements ActionListener {
		private JComboBox comPortList;
		JComboBox comBaudList;

		public ComSetting() {

			setLayout(null);

			JLabel ComLabel = new JLabel(Param.Label_Com_Port);
			ComLabel.setBounds(10, 10, 100, 20);
			add(ComLabel);

			comPortList = new JComboBox();
			updateComList();
			comPortList.setPreferredSize(new Dimension(100, 20));
			comPortList.setBounds(120, 10, 100, 20);
			add(comPortList);

			JButton ComButton = new JButton(Param.Label_Com_Updatet);
			ComButton.setBounds(240, 10, 100, 20);
			ComButton.addActionListener(this);
			add(ComButton);

			JLabel BaudLabel = new JLabel(Param.Label_Baud);
			BaudLabel.setBounds(10, 40, 100, 20);
			add(BaudLabel);

			Integer[] baud = { 9600, 115200 };
			comBaudList = new JComboBox(baud);
			comBaudList.setBounds(120, 40, 100, 20);
			add(comBaudList);

		}

		private void updateComList() {
			// CommPortIdentifier portId = null;
			Enumeration portEnum;
			portEnum = CommPortIdentifier.getPortIdentifiers();
			// ComBox.addItem("COM1");

			while (portEnum.hasMoreElements()) {
				CommPortIdentifier currentPortIdentifier = (CommPortIdentifier) portEnum.nextElement();
				comPortList.addItem(currentPortIdentifier.getName());
			}
		}

		public void setParam() {
			for (int i = 0; i < 4; i++) {
				comGraphEnb[i].setSelected(Param.comGraphEnb[i]);
				comGraphAxisText[i].setText(Param.comGraphAxisText[i]);
				comLineNum[i].setSelectedItem(Param.comLineNum[i]);
			}
			comGraphTitleText.setText(Param.comGraphTitle);
			if (Param.comBaud == 0) {
				comBaudList.setSelectedIndex(0);
			} else {
				comBaudList.setSelectedItem(Param.comBaud);
			}
			if (Param.comPort == null || Param.comPort == "") {
				comPortList.setSelectedIndex(-1);
			} else {
				comPortList.setSelectedItem(Param.comPort);
			}
		}

		public void getParam() {
			for (int i = 0; i < 4; i++) {
				Param.comGraphEnb[i] = comGraphEnb[i].isSelected();
				Param.comGraphAxisText[i] = comGraphAxisText[i].getText();
				Param.comLineNum[i] = (Integer) comLineNum[i].getSelectedItem();
			}
			Param.comBaud = (Integer) comBaudList.getSelectedItem();
			Param.comGraphTitle = comGraphTitleText.getText();
			Param.comPort = (String) comPortList.getSelectedItem();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO update com port list
			String cmd = e.getActionCommand();
			System.out.println(cmd);
			if (e.getActionCommand() == "update") {
				// System.out.println("update");
				comPortList.removeAllItems();
				updateComList();
				// add(comPortList);
			}
		}
	}

	public class GraphSetting extends JPanel implements MouseListener{
		public GraphSetting() {
			setLayout(null);
			comGraphTitleLabel = new JLabel(Param.Label_Graph_Title);
			comGraphTitleLabel.setBounds(10, 30, 100, 20);
			add(comGraphTitleLabel);

			comGraphTitleText = new JTextField(Param.comGraphTitle);
			comGraphTitleText.setBounds(120, 30, 200, 20);
			add(comGraphTitleText);

			JLabel EnbLabel = new JLabel(Param.Label_Graph_Enable);
			EnbLabel.setBounds(110, 60, 100, 20);
			add(EnbLabel);

			JLabel TitleLabel = new JLabel(Param.Label_Graph_Axis);
			TitleLabel.setBounds(220, 60, 200, 20);
			add(TitleLabel);

			JLabel LineLabel = new JLabel(Param.Label_Graph_Line);
			LineLabel.setBounds(440, 60, 100, 20);
			add(LineLabel);

			// private JLabel[] GraphLabel = new JLabel[4];
			// private JCheckBox[] GraphEnb = new JCheckBox[4];
			// private JTextField[] GraphTitle = new JTextField[4];
			// private JComboBox[] LineNum = new JComboBox[4];

			for (int i = 0; i < 4; i++) {
				comGraphLabel[i] = new JLabel("Graph" + String.valueOf(i + 1));
				comGraphEnb[i] = new JCheckBox();
				comGraphEnb[i].setSelected(Param.comGraphEnb[i]);
				comGraphAxisText[i] = new JTextField(Param.comGraphAxisText[i]);
				Integer[] j = { 1, 2, 3 };
				comLineNum[i] = new JComboBox(j);
				comLineNum[i].setSelectedItem(Param.comLineNum[i]);

				comGraphLabel[i].setBounds(10, 90 + i * 30, 100, 20);
				comGraphEnb[i].setBounds(120, 90 + i * 30, 100, 20);
				comGraphAxisText[i].setBounds(220, 90 + i * 30, 200, 20);
				comLineNum[i].setBounds(440, 90 + i * 30, 100, 20);

				add(comGraphLabel[i]);
				add(comGraphEnb[i]);
				add(comGraphAxisText[i]);
				add(comLineNum[i]);
			}
			ImageIcon logo_icon = new ImageIcon(Param.File_Path_Logo);
			JLabel logo_label = new JLabel(logo_icon);
			logo_label.setBounds(350, 10,200, 40);
			logo_label.addMouseListener(this);
			add(logo_label);

		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			Desktop desktop = Desktop.getDesktop();
			try {
				URI uri = new URI(Param.URL_Logo);
				desktop.browse(uri);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// mouse event

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// mouse event

		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// mouse event

		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// mouse event

		}
	}

	public class SubGHzSetting extends JPanel {
		public SubGHzSetting() {

		}

	}
	public class myFileFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return name.endsWith( ".graph" );
		}
	}
}
