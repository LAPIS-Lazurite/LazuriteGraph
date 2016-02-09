package com.lapis_semi.lazurite.LazuriteGraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;


public class Param {
	static String Label_File_New;
	static String Label_File_Open;
	static String Label_File_Save;
	static String Label_File_Quit;
	static String Label_Com_Port;
	static String Label_Baud;
	static String Label_Graph_Enable;
	static String Label_Graph_Axis;
	static String Label_Graph_Line;
	static String Label_Com_Updatet;
	static String Label_Start;
	static String Label_Graph_Title;
	static String File_Path_Logo;
	static String URL_Logo;
	static String comGraphTitle;
	static String comGraphAxisText[] = new String[4];
	static String App_Icon;
	static int[] comLineNum = new int[4];
	static boolean comGraphEnb[] = new boolean[4];
	static int comBaud;
	static int graphMode;
	static String comPort;

	public Param(File iniFile) {
		Properties properties = new Properties();
		try {
			InputStream inputStream = new FileInputStream(iniFile);
			properties.load(inputStream);
			inputStream.close();

			Label_File_New = properties.getProperty("Label_File_New");
			Label_File_Open = properties.getProperty("Label_File_Open");
			Label_File_Save = properties.getProperty("Label_File_Save");
			Label_File_Quit = properties.getProperty("Label_File_Quit");
			Label_Com_Port = properties.getProperty("Label_Com_Port");
			Label_Baud = properties.getProperty("Label_Baud");
			Label_Graph_Enable = properties.getProperty("Label_Graph_Enable");
			Label_Graph_Axis = properties.getProperty("Label_Graph_Axis");
			Label_Graph_Line = properties.getProperty("Label_Graph_Line");
			Label_Com_Updatet = properties.getProperty("Label_Com_Updatet");
			Label_Start = properties.getProperty("Label_Start");
			Label_Graph_Title = properties.getProperty("Label_Graph_Title");
			File_Path_Logo = properties.getProperty("File_Path_Logo");
			URL_Logo = properties.getProperty("URL_Logo");
			App_Icon = properties.getProperty("App_Icon");
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

	}
	static public void putDefaultPath(File iniFile, String path) {
		Properties properties = new Properties();
		if(path == null) {
			path = "";
		}
		properties.setProperty("Default_Path", path);
		try {
			FileOutputStream outputStream = new FileOutputStream(iniFile);
			properties.store(outputStream, "store test"); // 書き込み
			outputStream.close();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	public String getDefaultPath(File iniFile) {
		String path = null;
		Properties properties = new Properties();
		try {
			InputStream inputStream = new FileInputStream(iniFile);
			properties.load(inputStream);
			inputStream.close();

			path = properties.getProperty("Default_Path");
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return path;
	}

}
