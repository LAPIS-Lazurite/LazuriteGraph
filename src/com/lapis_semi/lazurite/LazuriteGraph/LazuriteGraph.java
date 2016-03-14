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
import java.util.ArrayList;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.jfree.ui.RefineryUtilities;

import gnu.io.CommPortIdentifier;


public class LazuriteGraph extends JFrame implements ActionListener {
	static ComChart chart;

	JTabbedPane SettingFrame;
	GraphSetting graphSetting;
	ComSetting tabCom;
	SubghzSetting tabSubGHz;
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
		tabSubGHz = new SubghzSetting();

		SettingFrame = new JTabbedPane();
		SettingFrame.addTab("COM",tabCom );
		SettingFrame.addTab("SubGHz", tabSubGHz);
		SettingFrame.setBounds(0, 0, 600, 200);

		graphSetting = new GraphSetting();

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
		graphSetting.setParam();
		tabCom.setParam();
		tabSubGHz.setParam();
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
		graphSetting.getParam();
		tabCom.getParam();
		Param.selectedTabIndex = SettingFrame.getSelectedIndex();
		if(tabSubGHz.getParam(true) == true) {
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
	}

	static void GraphClose() {
		chart.close();
		graphStart.setEnabled(false);
	}

	//
	private void initParam() {
		for (int i = 0; i < 4; i++) {
			Param.GraphEnb[i] = false;
			Param.GraphAxisText[i] = "";
			Param.LineNum[i] = 1;
		}
		Param.GraphTitle = "";
		Param.GraphEnb[0] = true;
		Param.comPort = "";
		Param.comBaud = 0;
		Param.subghzBaud =100;
		Param.subghzChannel=36;
		Param.subghzPanid = "0xABCD";
		Param.subghzPwr = 20;
		Param.subghzStrTxaddr = "0";

	}

	private boolean loadGraphFile(File graphFile) {
		Properties properties = new Properties();
		try {
			InputStream inputStream = new FileInputStream(graphFile);
			properties.load(inputStream);
			inputStream.close();
			for (int i = 0; i < 4; i++) {
				Param.GraphEnb[i] = Boolean.valueOf(properties.getProperty("GraphEnb" + String.valueOf(i)));
				Param.GraphAxisText[i] = properties.getProperty("GraphAxisText" + String.valueOf(i));
				Param.LineNum[i] = Integer.parseInt(properties.getProperty("LineNum" + String.valueOf(i)));
			}
			Param.GraphTitle = properties.getProperty("GraphTitle");
			Param.comBaud = Integer.parseInt(properties.getProperty("comBaud"));
			Param.comPort = properties.getProperty("comPort");
			SettingFrame.setSelectedIndex(Integer.parseInt(properties.getProperty("selectedTab")));
			Param.subghzBaud = Integer.parseInt(properties.getProperty("subghzBaud"));
			Param.subghzStrTxaddr = properties.getProperty("subghzStrTxaddr");
			Param.subghzPwr = Integer.parseInt(properties.getProperty("subghzPwr"));
			Param.subghzPanid = properties.getProperty("subghzPanid");
			Param.subghzChannel = Integer.parseInt(properties.getProperty("subghzChannel"));
			graphSetting.setParam();
			tabCom.setParam();
			tabSubGHz.setParam();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return true;
	}

	private boolean writeGraphFile(File graphFile) {

		graphSetting.getParam();
		tabCom.getParam();
		tabSubGHz.getParam();

		Properties properties = new Properties();

		properties.setProperty("selectedTab", String.valueOf(SettingFrame.getSelectedIndex()));

		for (int i = 0; i < 4; i++) {
			properties.setProperty("GraphEnb" + String.valueOf(i), Boolean.toString(Param.GraphEnb[i]));
			properties.setProperty("GraphAxisText" + String.valueOf(i), Param.GraphAxisText[i]);
			properties.setProperty("LineNum" + String.valueOf(i), Integer.toString(Param.LineNum[i]));
		}
		properties.setProperty("GraphTitle", Param.GraphTitle);
		properties.setProperty("comBaud", Integer.toString(Param.comBaud));
		if (Param.comPort == null) {
			Param.comPort = "";
		}
		properties.setProperty("comPort", Param.comPort);
		properties.setProperty("subghzBaud", Integer.toString(Param.subghzBaud));
		properties.setProperty("subghzStrTxaddr", Param.subghzStrTxaddr);
		properties.setProperty("subghzPwr", Integer.toString(Param.subghzPwr));
		properties.setProperty("subghzPanid", Param.subghzPanid);
		properties.setProperty("subghzChannel", Integer.toString(Param.subghzChannel));

		try {
			FileOutputStream outputStream = new FileOutputStream(graphFile);
			properties.store(outputStream, "store test"); // 書き込み
			outputStream.close();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return true;
	}


	public class GraphSetting extends JPanel implements MouseListener{
		private JComboBox[] lineNum = new JComboBox[4];
		private JLabel graphTitleLabel = new JLabel();
		private JTextField graphTitleText = new JTextField();
		private JLabel[] graphLabel = new JLabel[4];
		private JCheckBox[] graphEnb = new JCheckBox[4];
		private JTextField[] graphAxisText = new JTextField[4];
		public GraphSetting() {
			setLayout(null);
			graphTitleLabel = new JLabel(Param.Label_Graph_Title);
			graphTitleLabel.setBounds(10, 30, 100, 20);
			add(graphTitleLabel);

			graphTitleText = new JTextField(Param.GraphTitle);
			graphTitleText.setBounds(120, 30, 200, 20);
			add(graphTitleText);

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
				graphLabel[i] = new JLabel("Graph" + String.valueOf(i + 1));
				graphEnb[i] = new JCheckBox();
				graphEnb[i].setSelected(Param.GraphEnb[i]);
				graphAxisText[i] = new JTextField(Param.GraphAxisText[i]);
				Integer[] j = { 1, 2, 3 };
				lineNum[i] = new JComboBox(j);
				lineNum[i].setSelectedItem(Param.LineNum[i]);

				graphLabel[i].setBounds(10, 90 + i * 30, 100, 20);
				graphEnb[i].setBounds(120, 90 + i * 30, 100, 20);
				graphAxisText[i].setBounds(220, 90 + i * 30, 200, 20);
				lineNum[i].setBounds(440, 90 + i * 30, 100, 20);

				add(graphLabel[i]);
				add(graphEnb[i]);
				add(graphAxisText[i]);
				add(lineNum[i]);
			}
			ImageIcon logo_icon = new ImageIcon(Param.File_Path_Logo);
			JLabel logo_label = new JLabel(logo_icon);
			logo_label.setBounds(350, 10,200, 40);
			logo_label.addMouseListener(this);
			add(logo_label);

		}
		public void setParam() {
			for (int i = 0; i < 4; i++) {
				graphEnb[i].setSelected(Param.GraphEnb[i]);
				graphAxisText[i].setText(Param.GraphAxisText[i]);
				lineNum[i].setSelectedItem(Param.LineNum[i]);
			}
			graphTitleText.setText(Param.GraphTitle);
		}

		public void getParam() {
			for (int i = 0; i < 4; i++) {
				Param.GraphEnb[i] = graphEnb[i].isSelected();
				Param.GraphAxisText[i] = graphAxisText[i].getText();
				Param.LineNum[i] = (Integer) lineNum[i].getSelectedItem();
			}
			Param.GraphTitle = graphTitleText.getText();
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


	public class myFileFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return name.endsWith( ".graph" );
		}
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

		public void setParam() {
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
			Param.comBaud = (Integer) comBaudList.getSelectedItem();
			Param.comPort = (String) comPortList.getSelectedItem();
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

	public class SubghzSetting extends JPanel {
		private JComboBox subghzBaud;
		private JComboBox subghzChannel;
		private JTextField subghzPanid;
		private JTextField subghzTxAddr;
		private JComboBox subghzPwr;

		public SubghzSetting(){

			setLayout(null);
			JLabel chLabel = new JLabel(Param.Label_Subghz_Channel);
			chLabel.setBounds(10,10,100,20);

			ArrayList<Integer> channel = new ArrayList<Integer>();
			for(int i=24; i<= 61 ; i++) {
				channel.add(i);
			}
			subghzChannel = new JComboBox(channel.toArray());
			subghzChannel.setSelectedItem(36);
			subghzChannel.setBounds(120, 10, 100, 20);

			JLabel baudLabel = new JLabel(Param.Label_Baud);
			baudLabel.setBounds(10,40,100,20);

			Integer[] baud = {50,100};
			subghzBaud = new JComboBox(baud);
			subghzBaud.setSelectedItem(100);
			subghzBaud.setBounds(120,40,100,20);

			JLabel panidLabel = new JLabel(Param.Label_Subghz_Panid);
			panidLabel.setBounds(10,70,100,20);

			JLabel pwrLabel = new JLabel(Param.Label_Subghz_Pwr);
			pwrLabel.setBounds(340,10,100,20);

			Integer[] pwr = {1,20};
			subghzPwr = new JComboBox(pwr);
			subghzPwr.setSelectedItem(20);
			subghzPwr.setBounds(440,10,100,20);


			subghzPanid = new JTextField("0xABCD");
			subghzPanid.setBounds(120,70,100,20);

			JLabel txaddrLabel = new JLabel(Param.Label_Subghz_Txaddr);
			txaddrLabel.setBounds(10,100,100,20);

			subghzTxAddr = new JTextField(Param.subghzStrTxaddr);
			subghzTxAddr.setBounds(120,100,300,20);

			add(baudLabel);
			add(subghzBaud);
			add(chLabel);
			add(subghzChannel);
			add(panidLabel);
			add(subghzPanid);
			add(txaddrLabel);
			add(subghzTxAddr);
			add(pwrLabel);
			add(subghzPwr);

		}

		public boolean setParam(boolean notification){
			setParam();
			return checkValue(notification);
		}

		public void setParam() {
			subghzBaud.setSelectedItem(Param.subghzBaud);
			subghzChannel.setSelectedItem(Param.subghzChannel);
			subghzPanid.setText(Param.subghzPanid);
			subghzTxAddr.setText(Param.subghzStrTxaddr);
			subghzPwr.setSelectedItem(Param.subghzPwr);
		}

		public boolean getParam(boolean notification) {
			getParam();
			return checkValue(notification);
		}
		public void getParam() {
			Param.subghzBaud = (Integer) subghzBaud.getSelectedItem();
			Param.subghzChannel = (Integer) subghzChannel.getSelectedItem();
			Param.subghzPanid = subghzPanid.getText();
			Param.subghzStrTxaddr = subghzTxAddr.getText();
			Param.subghzPwr = (Integer) subghzPwr.getSelectedItem();
		}

		private boolean checkValue(boolean notification) {
			// check value of Panid
			try {
				int panid;
				panid = Integer.decode(Param.subghzPanid);
				if((panid >= 0) && (panid <= 0xffff)) {
					Param.subghzPanidEnb = true;
				} else {
					Param.subghzPanidEnb = false;
				}
			} catch (NumberFormatException nfex) {
				Param.subghzPanidEnb = false;;
			}

			// check value of Txaddr
			int len = Param.subghzStrTxaddr.length();

			// must be 16bit address mode
			if(len <= 6) {
				try{
					Param.subghzTxaddr[3] = Integer.decode(Param.subghzStrTxaddr);
					if((Param.subghzTxaddr[3] >= 0) && (Param.subghzTxaddr[3] <= 0xffff)) {
						for(int i=0; i<=2 ;i++) {
							Param.subghzTxaddr[i] = 0;
						}
						Param.subghzTxaddrEnb = true;
					} else {
						Param.subghzTxaddrEnb = false;
					}
				} catch(NumberFormatException nfex) {
					Param.subghzTxaddrEnb = false;
				}
			// 64bit address mode
			} else if(len == 16) {
				Param.subghzTxaddrEnb = true;
				try {
					for(int i=0; i<4;i++) {
						Param.subghzTxaddr[i] = Integer.parseInt("0x"+Param.subghzStrTxaddr.substring(i,(i+1)*2));
					}
				} catch(NumberFormatException nfex) {
					Param.subghzTxaddrEnb = false;
				}
			}
			// other data format is error
			else {
				Param.subghzTxaddrEnb = false;
			}
			if((notification) && ((!Param.subghzPanidEnb) || (!Param.subghzTxaddrEnb))){
				String message = Param.subghzParamError;
				if(!Param.subghzPanidEnb) {
					message += " \""+Param.Label_Subghz_Panid+"\"";
				}
				if(!Param.subghzTxaddrEnb) {
					message += " \""+Param.Label_Subghz_Txaddr+"\"";
				}
			    JLabel label = new JLabel(message);
			    JOptionPane.showMessageDialog(this, label);
			}
			return (Param.subghzPanidEnb | Param.subghzTxaddrEnb );
		}

	}
}
