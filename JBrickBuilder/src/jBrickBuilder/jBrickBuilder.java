/*
	Copyright 2014-2017 Mario Pascucci <mpascucci@gmail.com>
	This file is part of JBrickBuilder

	JBrickBuilder is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	JBrickBuilder is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with JBrickBuilder.  If not, see <http://www.gnu.org/licenses/>.

*/


package jBrickBuilder;



import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.prefs.BackingStoreException;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;
import javax.media.opengl.GLException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.stream.XMLStreamException;





//import jbrickconnedit.MySettings;
import bricksnspace.appsettings.AppSettings;
import bricksnspace.appsettings.AppUIResolution;
import bricksnspace.appsettings.AppVersion;
import bricksnspace.appsettings.OptionsDialog;
import bricksnspace.busydialog.BusyDialog;
import bricksnspace.dbconnector.DBConnector;
import bricksnspace.ldeditor.EditChangeListener;
import bricksnspace.ldeditor.LDEditor;
import bricksnspace.ldpovray.LDPovRenderer;
import bricksnspace.ldraw3d.LDRenderedPart;
import bricksnspace.ldraw3d.LDrawGLDisplay;
import bricksnspace.ldraw3d.ProgressUpdater;
import bricksnspace.ldrawlib.ConnectionPoint;
import bricksnspace.ldrawlib.ImportLDrawProjectTask;
import bricksnspace.ldrawlib.LDFlexPart;
import bricksnspace.ldrawlib.LDLibManageDlg;
import bricksnspace.ldrawlib.LDPrimitive;
import bricksnspace.ldrawlib.LDrawColor;
import bricksnspace.ldrawlib.LDrawDBImportTask;
import bricksnspace.ldrawlib.LDrawLib;
import bricksnspace.ldrawlib.LDrawPart;





/**
 * Simple LDraw CAD using OpenGL
 * 
 * @author Mario Pascucci
 *
 */
public class jBrickBuilder implements ActionListener, 
			ProgressUpdater, ColorChangeListener, EditChangeListener 
{

	
	private final String appName = "JBrickBuilder";
	//private final String VERSIONURL = "http://jbrickbuilder.sourceforge.net/VERSION";
	private final String colorXml = "colors.xml";
	private final String LDRURL = "http://www.ldraw.org/library/updates/complete.zip";
	private final String commentString = "0 // Created with JBrickBuilder";
	private final String commentUrl= "0 // http://sourceforge.net/projects/jbrickbuilder/";

	// LDraw Library
	private LDrawLib ldr;
	
	// 3D OpenGL display
	private LDrawGLDisplay gldisplay;

	// 3D model editing and rendering
	private LDEditor modelEdit;

	// save and load
	private boolean firstSave = true;
	private Map<String,File> loadedFiles = new HashMap<String, File>();
	
	private JToggleButton perspective;
	private JFrame frame;
	private JFileChooser modelFile;
	private JFileChooser subPartFile;
	private SmartFileChooser imgFile;
	private SmartFileChooser modelSave;
	private SmartFileChooser modelSaveMpd;
	private SmartFileChooser povFile;
	private JToggleButton light;
	private JToggleButton polygon;
	private JToggleButton wires;
	private JButton rotleft;
	private JButton rotright;
	private JButton rotup;
	private JButton rotdown;
	private JButton zoomin;
	private JButton zoomreset;
	private JButton zoomout;
	private JButton panleft;
	private JButton panright;
	private JButton pandown;
	private JButton panup;
	private JButton panreset;
	private JButton saveShot;
	private JButton explodeSubModel;
	private JButton hideParts;
	private JButton unHideAll;
	private JLabel status;
	private JButton newModel;
	private JButton saveModel;
	private JButton loadModel;
	private ImageIcon icnReady;
	private ImageIcon icnBusy;
	private ImageIcon icnError;
	private JButton gridUp;
	private JButton gridDown;
	private JButton gridLeft;
	private JButton gridRight;
	private JButton setSnap;
	private JButton setGrid;
	private JButton alignGrid;
	private JButton resetGrid;
	private JButton gridNear;
	private JButton gridFar;
	private JButton gridXZ;
	private JButton gridXY;
	private JButton gridYZ;
	private JButton addBrick;
	private JButton delBrick;
	private JButton colorBrick;
	private JToggleButton autoConnect;
	private JButton testBrick;
	private LDrawColorDialog colorDialog;
	private JToggleButton enableGrid;
	private ImageIcon[] icnImg = new ImageIcon[4];
	private JMenuItem mntmLdrParts;
	private JMenuItem mntmAbout;
	private JMenuItem mntmExit;
	private ColorToolbar colorTool;
	private LDrawPartChooser partChooser;
	private JToggleButton repeatBrick;
	private File currentFile;
	private final String imageFolder;
	private JMenuItem mntmOptions;
	private JPanel topToolbars;
	private JToolBar modelTools;
	private JButton modelData;
	private JButton setAngleStep;
	private JPopupMenu angleStep;
	private JMenuItem angleStep1;
	private JMenuItem angleStep5;
	private JMenuItem angleStep10;
	private JMenuItem angleStep15;
	private JMenuItem angleStep18;
	private JRadioButtonMenuItem angleStep22;
	private JMenuItem angleStep33;
	private JMenuItem angleStep45;
	private JMenuItem angleStep90;
	private JToggleButton enableAxis;
	private JButton loadSubPart;
	private JButton addSubPart;
	private JButton saveModelAs;
	private PartMRUChooser partMRUChooser;
	private JButton addMruBrick;
	private JButton dupBrick;
	private JButton exportAsMpd;
	private JToggleButton smoothPolygon;
	private JMenuItem mntmLdrGet;
	private JMenuItem mntmLdrConn;
	private JButton selById;
	private JButton selByColor;
	private JButton saveBlock;
	private JButton resetPointer;
	private JButton rotateBrick;
	private JPanel toolPanel;
	private JToggleButton enableSnap;
	private JButton editUndo;
	private JButton editRedo;
	private JButton editCopy;
	private JButton editCut;
	private JButton editPaste;
	private JButton resetEdit;
	private ImageIcon normalSaveIcon;
	//private JButton flexBrick;
	private ImageIcon modifiedSaveIcon;
	private JButton nextStep;
	private JButton prevStep;
	private JButton lastStep;
	private JTextField currStep;
	private JButton stepBrick;
	private JButton firstStep;
	private JButton editSubModel;
	private DBConnector dbc;
	private JMenuItem mntmLibs;
	private JButton povExport;
	
	
	/*
	 * TODO: placing brick -> remove/disable occupied connection->not necessary at the moment
	 * TODO: better part browsing with part miniature
	 * TODO: align part to other part (to avoid rotation wrong alignment 3010 rotated + 3062b)
	 * done: part moving after place.
	 * 		- create a new part with all selected part inside
	 * 		- part are relative to a calculated origin (something like a "center of gravity"
	 * 		- all rotation and moving are done on pseudo-part
	 * 		- after placing part, pseudo part is exploded.
	 */
	
	
	
	jBrickBuilder() {
		
		AppVersion.setMyVersion("0.5.3");

		// logging system init
		try {
			FileHandler logFile = new FileHandler(appName+"-%g.log",0,3);
			logFile.setLevel(Level.ALL);
			logFile.setFormatter(new SimpleFormatter());
			Logger.getGlobal().addHandler(logFile);
			Logger.getGlobal().log(Level.INFO, "Logger started");
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		try {
			dbc = new DBConnector("./jbb", "jbbuser", "");
		} catch (ClassNotFoundException | SQLException e2) {
			JOptionPane.showMessageDialog(null, 
					"Unable to establish a connection with database.\n"
					+"You can't use program without it.\n"
					+ "Program now exits.",
					"Database error", JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[MAIN] Unable to establish a connection with database.",e2);
			System.exit(1);
		}
		

		
		//AppUIResolution.setLimits(1600, 1000);
		imageFolder = AppUIResolution.getImgDir();
		
		// images for busy animation
		icnImg[0] = new ImageIcon(this.getClass().getResource("images/f0.png"));
		icnImg[1] = new ImageIcon(this.getClass().getResource("images/f1.png"));
		icnImg[2] = new ImageIcon(this.getClass().getResource("images/f2.png"));
		icnImg[3] = new ImageIcon(this.getClass().getResource("images/f3.png"));
		// images for busy/ready icon
		icnReady = new ImageIcon(this.getClass().getResource(imageFolder+"ready.png"));
		icnBusy = new ImageIcon(this.getClass().getResource(imageFolder+"busy.png"));
		icnError = new ImageIcon(this.getClass().getResource(imageFolder+"error.png"));

		ldr = null;
		// defines application preferences
		initPrefs();
//		AppSettings.openPreferences(this);
//		// types and description for preferences
//		AppSettings.addPref(MySettings.LDRAWDIR, "Path to your LDraw library folder", AppSettings.FOLDER);
//		AppSettings.addPref(MySettings.LDRAWZIP, "Path to your LDraw library zip file", AppSettings.FILE);
//		AppSettings.addPref(MySettings.FOLDERLIB, "Use installed LDraw library, instead of zipped", AppSettings.BOOLEAN);
//		AppSettings.addPref(MySettings.ALLCOLORS, "Use extended color palette", AppSettings.BOOLEAN);
//		AppSettings.addPref(MySettings.GRIDSIZE,"Grid size in LDU",AppSettings.FLOAT);
//		AppSettings.addPref(MySettings.SNAPSIZE, "Snap size in LDU",AppSettings.FLOAT);
//		AppSettings.addPref(MySettings.AUTHOR,"Author name and e-mail",AppSettings.STRING);
//		AppSettings.addPref(MySettings.LOWDETAILS, "Use low-details parts", AppSettings.BOOLEAN);
//		AppSettings.addPref(MySettings.PERSPECTIVE, "Use perspective view", AppSettings.BOOLEAN);
//		AppSettings.addPref(MySettings.ANTIALIAS, "Use antialias (req. restart)", AppSettings.BOOLEAN);
//		AppSettings.addPref(MySettings.LDDROTMODE, "Use LDD rotation mode (req. restart)", AppSettings.BOOLEAN);
//		AppSettings.addPref(MySettings.LICENSE,"License text for new models",AppSettings.STRING);
//		// Private settings
//		AppSettings.addPrivatePref(MySettings.LIBLIST, "LDraw lib list", AppSettings.STRING);

		
		if (!AppSettings.isConfigured() || AppSettings.get(MySettings.LIBOFFICIAL).equals("")) {
			// this is a first run or an upgrade
			firstRun();
//			JOptionPane.showMessageDialog(null,
//					"Thank you for using "+appName+"!\n" +
//					"This is first run.\n"+
//					"Program needs some other files to properly work:\n" +
//					"- LDraw official library.\n" +
//					"- Connection database\n"+
//					"I'll ask you some question...",
//					"Welcome!", JOptionPane.INFORMATION_MESSAGE, 
//					new ImageIcon(this.getClass().getResource("images/icon-about.png")));
//			int res = JOptionPane.showConfirmDialog(null, 
//					"Do you already have LDraw library?\n"
//					+ "You can use your installed library (as a folder) or\n"
//					+ "original zipped file with LDraw official library.\n"
//					+ "Click 'No' if you don't have any LDraw library\n"
//					+ "Program will download latest LDraw library.",
//					"LDraw library choice", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
//			if (res == JOptionPane.YES_OPTION) {
//				JFileChooser jfc = new JFileChooser(".");
//				FileFilter ff = new FileNameExtensionFilter("LDraw Library ZIP", "zip");
//				jfc.setFileFilter(ff);
//				jfc.setDialogTitle("Select LDraw library (ZIP or folder)");
//				jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//				while (true) {
//					res = jfc.showOpenDialog(null);
//					if (res == JFileChooser.APPROVE_OPTION) {
//						if (jfc.getSelectedFile().getName().toLowerCase().endsWith(".zip")) {
//							// a zip library is selected
//							ZipFile z;
//							try {
//								z = new ZipFile(jfc.getSelectedFile());
//							} catch (IOException e) {
//								z = null;
//							}
//							if (z == null || z.getEntry("ldraw/p/stud.dat") == null) {
//								JOptionPane.showMessageDialog(null,
//										"File "+jfc.getSelectedFile().getName()+"\n" +
//										"Isn't a LDraw ZIP file library.\n"+
//										"Try again.",
//										"Unknown file", JOptionPane.ERROR_MESSAGE);
//							}
//							else {
//								AppSettings.putBool(MySettings.FOLDERLIB, false);
//								AppSettings.put(MySettings.LDRAWZIP, jfc.getSelectedFile().getPath());
//								break;
//							}
//						}
//						else {
//							File l = new File(jfc.getSelectedFile().getParent(),"parts");
//							if (l.exists() && l.isDirectory()) {
//								// found library path
//								AppSettings.putBool(MySettings.FOLDERLIB, true);
//								AppSettings.put(MySettings.LDRAWDIR, jfc.getSelectedFile().getParent());
//								break;
//							}
//							else {
//								l = new File(jfc.getSelectedFile(), "parts");
//								if (l.exists() && l.isDirectory()) {
//									// found library path
//									AppSettings.putBool(MySettings.FOLDERLIB, true);
//									AppSettings.put(MySettings.LDRAWDIR, jfc.getSelectedFile().getPath());
//									break;
//								}			
//								else {
//									JOptionPane.showMessageDialog(null,
//										"Folder "+jfc.getSelectedFile().getName()+"\n" +
//										"isn't a LDraw installed library.\n"+
//										"Try again.",
//										"Unknown folder", JOptionPane.ERROR_MESSAGE);
//								}
//							}
//						}
//					}
//					else {
//						res = JOptionPane.showConfirmDialog(null, 
//								"Do you want to exit program?\n",
//								"Confirm", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
//						if (res == JOptionPane.YES_OPTION) {
//							System.exit(0);
//						}
//					}
//				}
//			}
//			else {
//				// download library
//				boolean found = false;
//				AppSettings.put(MySettings.LDRAWZIP,"complete.zip");
//				for (int tries=0;tries<3;tries++) {
//					doLDrawLibDownload();
//					File l = new File(AppSettings.get(MySettings.LDRAWZIP));
//					if (l.isFile() && l.canRead()) {
//						ZipFile z;
//						try {
//							z = new ZipFile(l);
//						} catch (IOException e) {
//							z = null;
//						}
//						if (z == null || z.getEntry("ldraw/p/stud.dat") == null) {
//							JOptionPane.showMessageDialog(null,
//									"Downloaded file is corrupted.\n" +
//									"Try to download again.",
//									"Broken file", JOptionPane.ERROR_MESSAGE);
//						}
//						else {
//							found = true;
//							AppSettings.putBool(MySettings.FOLDERLIB, false);
//							AppSettings.put(MySettings.LDRAWZIP, l.getPath());
//							break;
//						}
//					}
//				}
//				if (!found) {
//					// problems downloading library, exit
//					JOptionPane.showMessageDialog(null,
//							"Unable to download LDraw library.\n" +
//							"Cannot continue, exiting...",
//							"Giving up", JOptionPane.ERROR_MESSAGE);
//					System.exit(1);
//				}
//			}
//			// here we have all data to continue
//			try {
//				AppSettings.savePreferences();
//			} catch (IOException e) {
//				JOptionPane.showMessageDialog(null, 
//						"Cannot save preferences.\n"
//						+ "Problem is:\n"
//						+ e.getLocalizedMessage(),
//						"Preferences error", JOptionPane.ERROR_MESSAGE);
//				e.printStackTrace();
//			} catch (BackingStoreException e) {
//				JOptionPane.showMessageDialog(null, 
//						"Cannot save preferences.\n"
//						+ "Problem is:\n"
//						+ e.getLocalizedMessage(),
//						"Preferences error", JOptionPane.ERROR_MESSAGE);
//				e.printStackTrace();
//			}
			// now checks for connections library
			try {
				ConnectionPoint.init();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null,
						"Program needs brick connections database.\n"
						+"Looks like this is first run, let's get file.\n"
						+ "Click OK to start...",
						"Connections", JOptionPane.INFORMATION_MESSAGE);
				ConnectionPoint.release();
				boolean found = false;
				for (int tries=0;tries<3;tries++) {
					doConnDownload();
					File l = new File(ConnectionPoint.CONNZIP);
					if (l.isFile() && l.canRead()) {
						ZipFile z;
						try {
							z = new ZipFile(l);
						} catch (IOException e) {
							z = null;
						}
						if (z == null || z.getEntry("VERSION") == null) {
							JOptionPane.showMessageDialog(null,
									"Downloaded file is corrupted.\n" +
									"Try to download again...",
									"Broken file", JOptionPane.ERROR_MESSAGE);
						}
						else {
							found = true;
							break;
						}
					}
				}
				if (!found) {
					// problems downloading library, exit
					JOptionPane.showMessageDialog(null,
							"Unable to download connection database.\n" +
							"Cannot continue, exiting...",
							"Giving up", JOptionPane.ERROR_MESSAGE);
					System.exit(1);
				}
			}
		}
		
		// loading library
		try {
			ldr = loadLibs();
		}
		catch (IOException | SQLException exc) {
			JOptionPane.showMessageDialog(null, 
					"Unable to read your LDraw library.\n"
					+ "Please edit your library list\n "
					+ "and restart program.\n"
					+ "Original error was:\n"+
							exc.getLocalizedMessage(), 
					"Library Error", JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"LDraw Library error", exc);
			LDLibManageDlg dlg = new LDLibManageDlg(frame, "Edit/add libraries to use", true, ldr);
			dlg.setVisible(true);
		}
//		if (AppSettings.getBool(MySettings.FOLDERLIB)) {
//			try {
//				ldr = new LDrawLib(
//						new File(AppSettings.get(MySettings.LDRAWDIR)),
//						null);
//				LDrawPart.setLdrlib(ldr);
//			}
//			catch (IOException exc) {
//				JOptionPane.showMessageDialog(null, 
//						"Unable to read your Ldraw library folder.\n"
//						+ "If you moved your library please update your preferences\n "
//						+ "and restart program to specify a new position.\n"
//						+ "Original error was:\n"+
//								exc.getLocalizedMessage(), 
//						"Library Error", JOptionPane.ERROR_MESSAGE);
//				ldr = null;
//			}
//		}
//		else {
//			try {
//				ldr = new LDrawLib(AppSettings.get(MySettings.LDRAWZIP),null);
//				LDrawPart.setLdrlib(ldr);
//			} catch (IOException e) {
//				JOptionPane.showMessageDialog(null,
//						"Unable to use LDraw library ZIP file.\n"
//								+ "If you moved your library please update your preferences\n "
//								+ "and restart program to specify a new position.\n"
//								+ "Original error was:\n"+
//										e.getLocalizedMessage(), 
//						"Library error", JOptionPane.ERROR_MESSAGE);
//				ldr = null;
//			}
//		}

		// updates checks
		// main program
//		if (AppVersion.myVersion() < CheckUpdates.getUrlVersion(VERSIONURL)) {
//			JOptionPane.showMessageDialog(null, 
//					"There is a new version of JBrickBuilder.\n"
//					+ "Please update",
//					"Program", JOptionPane.INFORMATION_MESSAGE);
//		}
//		try {
//			ldrdb = new LDrawLibDB(dbc);
//			ldrdb = new LDrawDB("jbb","jbbuser","");
//		} catch (SQLException e1) {
//			JOptionPane.showMessageDialog(null, 
//					"Unable to create parts database.\n"
//					+"You can't use program without it.\n"
//					+ "Program now exits.",
//					"Database error", JOptionPane.ERROR_MESSAGE);
//			Logger.getGlobal().log(Level.SEVERE,"Parts database error", e1);
//			System.exit(1);
//		}
		try {
			ConnectionPoint.init();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					"Unable to open connections database.\n"
					+ "Error is: " + e.getLocalizedMessage()
					+ "Program can be used without connections\n"
					+ "but it is more difficult to use.",
					"Connection error", JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"Connection database init error", e);
		}
		if (CheckUpdates.getZipVersion(ConnectionPoint.CONNZIP) < 
				CheckUpdates.getUrlVersion(ConnectionPoint.CONNVERSION)) {
			JOptionPane.showMessageDialog(null, 
					"There is a new version of Connection database.\n"
					+ "Please update connections",
					"Connections", JOptionPane.INFORMATION_MESSAGE);
		}
		frame = new JFrame(appName);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setPreferredSize(new Dimension(970, 700));
		frame.setIconImage(new ImageIcon(this.getClass().getResource("images/icon-big.png")).getImage());
		// from http://stackoverflow.com/questions/7613577/java-how-do-i-prevent-windowclosing-from-actually-closing-the-window
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				closeApp();
			}
		});
		modelSave = new SmartFileChooser(".",".ldr");
		modelSave.setDialogTitle("Save LDR model");
		modelSave.setDialogType(JFileChooser.SAVE_DIALOG);
		FileNameExtensionFilter ff = new FileNameExtensionFilter("LDraw models", "dat", "ldr", "mpd", "l3b","lcd");
		modelSaveMpd = new SmartFileChooser(".",".mpd");
		modelSaveMpd.setDialogTitle("Export model as MPD");
		modelSaveMpd.setDialogType(JFileChooser.SAVE_DIALOG);
		modelFile = new JFileChooser(".");
		modelFile.setFileFilter(ff);
		modelFile.setDialogTitle("Select LDraw model to render");
		subPartFile = new JFileChooser(".");
		subPartFile.setFileFilter(ff);
		subPartFile.setDialogTitle("Select LDraw subpart/submodel load");
		imgFile = new SmartFileChooser(".",".png");
		imgFile.setDialogTitle("Save screenshot");
		imgFile.setDialogType(JFileChooser.SAVE_DIALOG);
		povFile = new SmartFileChooser(".",".pov");
		povFile.setDialogTitle("Export model to PovRay scene file");
		povFile.setDialogType(JFileChooser.SAVE_DIALOG);
		
		LDrawGLDisplay.setAntialias(AppSettings.getBool(MySettings.ANTIALIAS));
		try {
			gldisplay = new LDrawGLDisplay();
//			dh = new DrawHelpers(gldisplay);
		}
		catch (GLException e) {
			JOptionPane.showMessageDialog(null, 
					"There is a problem with your graphic card:\n"+
							e.getLocalizedMessage(), 
					"3D Init Error", JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"OpenGL error", e);			
			System.exit(1);
		}

		if (AppSettings.getBool(MySettings.LDDROTMODE)) {
			gldisplay.setRotMode(LDrawGLDisplay.ROT_LDD);
		}
		else {
			gldisplay.setRotMode(LDrawGLDisplay.ROT_STD);
		}
		
		frame.getContentPane().add(gldisplay.getCanvas(),BorderLayout.CENTER);
		
		try {
			colorDialog = new LDrawColorDialog(frame,"Select color",true,this.getClass().getResourceAsStream("data/"+colorXml));
		} catch (FileNotFoundException | XMLStreamException e) {
			Logger.getGlobal().log(Level.SEVERE,"Color definition error", e);
			System.exit(1);
		}
		
		
		// setting menu
		// main menu
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		

		JMenu mnUpdateCatalogs = new JMenu("Catalogs");
		menuBar.add(mnUpdateCatalogs);

		mntmLdrGet = new JMenuItem("Download LDraw library");
		mntmLdrGet.addActionListener(this);
		mnUpdateCatalogs.add(mntmLdrGet);

		mntmLdrConn = new JMenuItem("Download connections database");
		mntmLdrConn.addActionListener(this);
		mnUpdateCatalogs.add(mntmLdrConn);
		
		mnUpdateCatalogs.addSeparator();
		
		mntmLdrParts = new JMenuItem("Refresh parts database");
		mntmLdrParts.addActionListener(this);
		mnUpdateCatalogs.add(mntmLdrParts);
		
		// last menu in top right 
		JMenu mnProgram = new JMenu("Program");
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(mnProgram);
		
		mntmOptions = new JMenuItem("Options...");
		mntmOptions.addActionListener(this);
		mnProgram.add(mntmOptions);

		mntmLibs = new JMenuItem("LDraw libraries...");
		mntmLibs.addActionListener(this);
		mnProgram.add(mntmLibs);

		mnProgram.add(new JSeparator());

		mntmAbout = new JMenuItem("About...");
		mntmAbout.addActionListener(this);
		mnProgram.add(mntmAbout);
		mnProgram.add(new JSeparator());

		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(this);
		mnProgram.add(mntmExit);
		

		topToolbars = new JPanel();
		topToolbars.setLayout(new FlowLayout(FlowLayout.LEFT));

		// toolbars
		
		modelTools = new JToolBar("Model editing");
		newModel = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"model-new.png")));
		newModel.setToolTipText("New model");
		newModel.addActionListener(this);
		modelTools.add(newModel);
		
		loadModel = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"model-open.png")));
		loadModel.setToolTipText("Load LDraw model");
		loadModel.addActionListener(this);
		modelTools.add(loadModel);
		
		modelData = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"brick-edit.png")));
		modelData.setToolTipText("Set model info");
		modelData.addActionListener(this);
		modelTools.add(modelData);
		
		normalSaveIcon = new ImageIcon(this.getClass().getResource(imageFolder+"model-save.png"));
		modifiedSaveIcon = new ImageIcon(this.getClass().getResource(imageFolder+"model-save-mod.png"));
		
		saveModel = new JButton(normalSaveIcon);
		saveModel.setToolTipText("Save model (LDR)");
		saveModel.addActionListener(this);
		//saveModel.setEnabled(false);
		modelTools.add(saveModel);
		
		saveModelAs = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"model-save-as.png")));
		saveModelAs.setToolTipText("Save model with name");
		saveModelAs.addActionListener(this);
		modelTools.add(saveModelAs);
		
		exportAsMpd = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"model-save-mpd.png")));
		exportAsMpd.setToolTipText("Export as MPD format");
		exportAsMpd.addActionListener(this);
		modelTools.add(exportAsMpd);
		
		modelTools.addSeparator();
		
		loadSubPart = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"load-block.png")));
		loadSubPart.setToolTipText("Load subpart/block from file");
		loadSubPart.addActionListener(this);
		modelTools.add(loadSubPart);
		
		addSubPart = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"sel-block.png")));
		addSubPart.setToolTipText("Add a custom part/block");
		addSubPart.addActionListener(this);
		modelTools.add(addSubPart);
		
		saveBlock = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"sel-save.png")));
		saveBlock.setToolTipText("Save selected as block");
		saveBlock.addActionListener(this);
		modelTools.add(saveBlock);
		
		explodeSubModel = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"ungroup.png")));
		explodeSubModel.setToolTipText("Ungroup selected sub-model");
		explodeSubModel.addActionListener(this);
		modelTools.add(explodeSubModel);
		
		editSubModel = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"brick-edit.png")));
		editSubModel.setToolTipText("Edit selected sub-model");
		editSubModel.addActionListener(this);
		modelTools.add(editSubModel);
		
		modelTools.addSeparator();
		
		editUndo = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"undo.png")));
		editUndo.setToolTipText("Undo last edit");
		editUndo.addActionListener(this);
		editUndo.setEnabled(false);
		modelTools.add(editUndo);
		
		editRedo = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"redo.png")));
		editRedo.setToolTipText("Redo last edit");
		editRedo.addActionListener(this);
		editRedo.setEnabled(false);
		modelTools.add(editRedo);
		
		modelTools.addSeparator();

		editCopy = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"copy.png")));
		editCopy.setToolTipText("Copy selected to clipboard");
		editCopy.addActionListener(this);
		editCopy.setEnabled(false);
		modelTools.add(editCopy);
		
		editCut = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"cut.png")));
		editCut.setToolTipText("Move selected to clipboard");
		editCut.addActionListener(this);
		editCut.setEnabled(false);
		modelTools.add(editCut);
		
		editPaste = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"paste.png")));
		editPaste.setToolTipText("Paste clipboard");
		editPaste.addActionListener(this);
		editPaste.setEnabled(false);
		modelTools.add(editPaste);
		
		modelTools.addSeparator();
		
		addBrick = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"brick-add.png")));
		addBrick.setToolTipText("Add brick");
		addBrick.addActionListener(this);
		modelTools.add(addBrick);
		
		addMruBrick = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"brick-mru.png")));
		addMruBrick.setToolTipText("Select from recently used");
		addMruBrick.addActionListener(this);
		modelTools.add(addMruBrick);
		
		dupBrick = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"brick-dup.png")));
		dupBrick.setToolTipText("Duplicate brick");
		dupBrick.addActionListener(this);
		modelTools.add(dupBrick);
		
		rotateBrick = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"conn-rotate.png")));
		rotateBrick.setToolTipText("Rotate tool");
		rotateBrick.addActionListener(this);
		modelTools.add(rotateBrick);
		
		delBrick = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"brick-del.png")));
		delBrick.setToolTipText("Delete brick");
		delBrick.addActionListener(this);
		modelTools.add(delBrick);
		
		colorBrick = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"brick-color.png")));
		colorBrick.setToolTipText("Change brick color");
		colorBrick.addActionListener(this);
		modelTools.add(colorBrick);
		
		stepBrick = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"brick-step.png")));
		stepBrick.setToolTipText("Step editor/player");
		stepBrick.addActionListener(this);
		modelTools.add(stepBrick);
		
		resetEdit = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"edit-reset.png")));
		resetEdit.setToolTipText("Reset editor");
		resetEdit.addActionListener(this);
		modelTools.add(resetEdit);
		
		
		topToolbars.add(modelTools);

		JPanel rightToolbar = new JPanel();
		frame.getContentPane().add(rightToolbar,BorderLayout.EAST);
		
		colorTool = new ColorToolbar(colorDialog, JToolBar.VERTICAL, 16);
		colorTool.setColorChangeListener(this);
		rightToolbar.add(colorTool);

		
		frame.getContentPane().add(topToolbars,BorderLayout.NORTH);
		
		// left toolbar
		JPanel leftToolbars = new JPanel();
		
		JToolBar drawHelpers = new JToolBar("Tools",JToolBar.VERTICAL);
		drawHelpers.setLayout(new GridLayout(0, 2));

		gridUp = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"grid-up.png")));
		gridUp.setToolTipText("Move grid -Y");
		gridUp.addActionListener(this);
		drawHelpers.add(gridUp);
		
		gridDown = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"grid-down.png")));
		gridDown.setToolTipText("Move grid +Y");
		gridDown.addActionListener(this);
		drawHelpers.add(gridDown);
		
		gridLeft = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"grid-left.png")));
		gridLeft.setToolTipText("Move grid -X");
		gridLeft.addActionListener(this);
		drawHelpers.add(gridLeft);

		gridRight = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"grid-right.png")));
		gridRight.setToolTipText("Move grid +X");
		gridRight.addActionListener(this);
		drawHelpers.add(gridRight);
		
		gridNear = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"grid-near.png")));
		gridNear.setToolTipText("Move grid -Z");
		gridNear.addActionListener(this);
		drawHelpers.add(gridNear);

		gridFar = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"grid-far.png")));
		gridFar.setToolTipText("Move grid +Z");
		gridFar.addActionListener(this);
		drawHelpers.add(gridFar);
		
		gridXZ = new JButton("XZ");
		gridXZ.setToolTipText("Align grid to plane XZ");
		gridXZ.addActionListener(this);
		drawHelpers.add(gridXZ);

		gridXY = new JButton("XY");
		gridXY.setToolTipText("Align grid to plane XY");
		gridXY.addActionListener(this);
		drawHelpers.add(gridXY);

		gridYZ = new JButton("YZ");
		gridYZ.setToolTipText("Align grid to plane YZ");
		gridYZ.addActionListener(this);
		drawHelpers.add(gridYZ);

		alignGrid = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"grid-align-part.png")));
		alignGrid.setToolTipText("Align grid to selected element");
		alignGrid.addActionListener(this);
		drawHelpers.add(alignGrid);
		
		resetGrid = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"grid-reset.png")));
		resetGrid.setToolTipText("Reset grid to origin");
		resetGrid.addActionListener(this);
		drawHelpers.add(resetGrid);

		resetPointer = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"pointer-reset.png")));
		resetPointer.setToolTipText("Reset pointer rotation");
		resetPointer.addActionListener(this);
		drawHelpers.add(resetPointer);

		enableGrid = new JToggleButton(new ImageIcon(this.getClass().getResource(imageFolder+"grid.png")));
		enableGrid.setToolTipText("Grid ON");
		enableGrid.setSelected(true);
		enableGrid.addActionListener(this);
		drawHelpers.add(enableGrid);
		
		enableSnap = new JToggleButton(new ImageIcon(this.getClass().getResource(imageFolder+"snap.png")));
		enableSnap.setToolTipText("Snap ON");
		enableSnap.setSelected(true);
		enableSnap.addActionListener(this);
		drawHelpers.add(enableSnap);
		
		enableAxis = new JToggleButton(new ImageIcon(this.getClass().getResource(imageFolder+"axis.png")));
		enableAxis.setToolTipText("Axis ON");
		enableAxis.setSelected(true);
		enableAxis.addActionListener(this);
		drawHelpers.add(enableAxis);
		
		setSnap = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"snap-size.png")));
		setSnap.setToolTipText("Set snap size");
		setSnap.addActionListener(this);
		drawHelpers.add(setSnap);
		
		setGrid = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"grid-size.png")));
		setGrid.setToolTipText("Set grid spacing");
		setGrid.addActionListener(this);
		drawHelpers.add(setGrid);

		setAngleStep = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"brick-rotate.png")));
		setAngleStep.setToolTipText("Set rotation step");
		setAngleStep.addActionListener(this);
		drawHelpers.add(setAngleStep);
		
		autoConnect = new JToggleButton(new ImageIcon(this.getClass().getResource(imageFolder+"brick-autoconnect.png")), true);
		autoConnect.setToolTipText("Use autoconnect");
		autoConnect.addActionListener(this);
		drawHelpers.add(autoConnect);
		
		repeatBrick = new JToggleButton(new ImageIcon(this.getClass().getResource(imageFolder+"brick-repeat.png")), true);
		repeatBrick.setToolTipText("Repeat brick");
		repeatBrick.addActionListener(this);
		drawHelpers.add(repeatBrick);
		
		hideParts = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"hide.png")));
		hideParts.setToolTipText("Hide selected");
		hideParts.addActionListener(this);
		drawHelpers.add(hideParts);
		
		unHideAll = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"show.png")));
		unHideAll.setToolTipText("Unhide all");
		unHideAll.addActionListener(this);
		drawHelpers.add(unHideAll);
		
		saveShot = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"camera.png")));
		saveShot.setToolTipText("Take a screenshot");
		saveShot.addActionListener(this);
		drawHelpers.add(saveShot);
		
		povExport = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"povray.png")));
		povExport.setToolTipText("PovRay exporter (experimental!)");
		povExport.addActionListener(this);
		drawHelpers.add(povExport);
		
//		testBrick = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"brick-info.png")));
//		testBrick.setToolTipText("New feature testing (danger!)");
//		testBrick.addActionListener(this);
//		drawHelpers.add(testBrick);
		
		
		leftToolbars.add(drawHelpers);
		
		frame.getContentPane().add(leftToolbars, BorderLayout.WEST);

		// popup menus
		angleStep = new JPopupMenu("Rotation step");
		angleStep.setLightWeightPopupEnabled(false);

		angleStep1 = new JRadioButtonMenuItem("1 degree");
		angleStep1.addActionListener(this);
		angleStep.add(angleStep1);
		
		angleStep5 = new JRadioButtonMenuItem("5 degree");
		angleStep5.addActionListener(this);
		angleStep.add(angleStep5);
		
		angleStep10 = new JRadioButtonMenuItem("10 degree");
		angleStep10.addActionListener(this);
		angleStep.add(angleStep10);
		
		angleStep15 = new JRadioButtonMenuItem("15 degree");
		angleStep15.addActionListener(this);
		angleStep.add(angleStep15);
		
		angleStep18 = new JRadioButtonMenuItem("18 degree");
		angleStep18.addActionListener(this);
		angleStep.add(angleStep18);
		
		angleStep22 = new JRadioButtonMenuItem("22.5 degree");
		angleStep22.addActionListener(this);
		angleStep.add(angleStep22);
		
		angleStep33 = new JRadioButtonMenuItem("33 degree");
		angleStep33.addActionListener(this);
		angleStep.add(angleStep33);
		
		angleStep45 = new JRadioButtonMenuItem("45 degree");
		angleStep45.addActionListener(this);
		angleStep.add(angleStep45);
		
		angleStep90 = new JRadioButtonMenuItem("90 degree",true);
		angleStep90.addActionListener(this);
		angleStep.add(angleStep90);

		ButtonGroup bg = new ButtonGroup();
		bg.add(angleStep1);
		bg.add(angleStep5);
		bg.add(angleStep10);
		bg.add(angleStep15);
		bg.add(angleStep18);
		bg.add(angleStep22);
		bg.add(angleStep33);
		bg.add(angleStep45);
		bg.add(angleStep90);
		
		// view controls
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createLoweredBevelBorder());
		panel.setLayout(new GridBagLayout());
		//panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.ipady = 2;
		gbc.ipadx = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		
		JToolBar rotPanel = new JToolBar("Rotate Pan Zoom");
		rotPanel.setBorder(BorderFactory.createTitledBorder("Rotate, pan and zoom model"));
		rotPanel.setLayout(new GridLayout(2, 6));

		gbc.gridx = 0;
		gbc.gridy = 0;

		panel.add(rotPanel,gbc);
		
		panreset = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"view-restore-2.png")));
		panreset.setToolTipText("Reset view");
		panreset.addActionListener(this);
		rotPanel.add(panreset);
		
		rotup = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"rotate-up.png")));
		rotup.setToolTipText("Rotate up");
		rotup.addActionListener(this);
		rotPanel.add(rotup);

		zoomreset = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"zoom-fit-best.png")));
		zoomreset.setToolTipText("Reset zoom");
		zoomreset.addActionListener(this);
		rotPanel.add(zoomreset);
		
		zoomin = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"zoom-in.png")));
		zoomin.setToolTipText("Zoom in");
		zoomin.addActionListener(this);
		rotPanel.add(zoomin);
		
		panup = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"pan-up.png")));
		panup.setToolTipText("Move up");
		panup.addActionListener(this);
		rotPanel.add(panup);

		zoomout = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"zoom-out.png")));
		zoomout.setToolTipText("Zoom out");
		zoomout.addActionListener(this);
		rotPanel.add(zoomout);

		rotleft = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"rotate-left.png")));
		rotleft.setToolTipText("Rotate left");
		rotleft.addActionListener(this);
		rotPanel.add(rotleft);
		
		rotdown = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"rotate-down.png")));
		rotdown.setToolTipText("Rotate down");
		rotdown.addActionListener(this);
		rotPanel.add(rotdown);

		rotright = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"rotate-right.png")));
		rotright.setToolTipText("Rotate right");
		rotright.addActionListener(this);
		rotPanel.add(rotright);

		panleft = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"pan-left.png")));
		panleft.setToolTipText("Move left");
		panleft.addActionListener(this);
		rotPanel.add(panleft);
		
		pandown = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"pan-down.png")));
		pandown.setToolTipText("Move down");
		pandown.addActionListener(this);
		rotPanel.add(pandown);

		panright = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"pan-right.png")));
		panright.setToolTipText("Move right");
		panright.addActionListener(this);
		rotPanel.add(panright);

		gbc.gridx++;
		gbc.gridy = 0;
		JToolBar optionPanel = new JToolBar("3D options");
		optionPanel.setBorder(BorderFactory.createTitledBorder("3D options"));
		optionPanel.setLayout(new GridLayout(2, 2));
		panel.add(optionPanel,gbc);

		if (AppSettings.getBool(MySettings.PERSPECTIVE)) {
			perspective = new JToggleButton(new ImageIcon(this.getClass().getResource(imageFolder+"perspective.png")));
			perspective.setToolTipText("Perspective projection");
		}
		else {
			perspective = new JToggleButton(new ImageIcon(this.getClass().getResource(imageFolder+"brick-filled.png")));
			perspective.setToolTipText("Orthogonal projection");
		}
		perspective.setSelected(AppSettings.getBool(MySettings.PERSPECTIVE));
		gldisplay.setPerspective(perspective.isSelected());
		perspective.addActionListener(this);
		optionPanel.add(perspective);
		
		light = new JToggleButton(new ImageIcon(this.getClass().getResource(imageFolder+"light-on.png")));
		light.setToolTipText("Use light");
		light. setSelected(true);
		light.addActionListener(this);
		optionPanel.add(light);
		
		polygon = new JToggleButton(new ImageIcon(this.getClass().getResource(imageFolder+"brick-filled.png")));
		polygon.setToolTipText("Polygon fill");
		polygon.setSelected(true);
		polygon.addActionListener(this);
		optionPanel.add(polygon);
		
		wires = new JToggleButton(new ImageIcon(this.getClass().getResource(imageFolder+"brick-wire.png")));
		wires.setToolTipText("Display edges");
		wires.setSelected(true);
		wires.addActionListener(this);
		optionPanel.add(wires);

		if (!AppSettings.getBool(MySettings.LOWDETAILS)) {
			smoothPolygon = new JToggleButton(new ImageIcon(this.getClass().getResource(imageFolder+"smooth.png")));
			smoothPolygon.setToolTipText("Standard details");
			LDrawLib.resetStdRes();
		}
		else {
			smoothPolygon = new JToggleButton(new ImageIcon(this.getClass().getResource(imageFolder+"rough.png")));
			smoothPolygon.setToolTipText("Lo-res details");
			LDrawLib.forceLoRes();
		}
		smoothPolygon.setSelected(!AppSettings.getBool(MySettings.LOWDETAILS));
		smoothPolygon.addActionListener(this);
		optionPanel.add(smoothPolygon);

		// selection tool panel
		gbc.gridx++;
		gbc.gridy = 0;
		JToolBar selectPanel = new JToolBar("Select");
		selectPanel.setBorder(BorderFactory.createTitledBorder("Select"));
		selectPanel.setLayout(new GridLayout(2, 2));
		panel.add(selectPanel,gbc);

		selById = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"select-id.png")));
		selById.setToolTipText("Select by part ID");
		selById.addActionListener(this);
		selectPanel.add(selById);
		
		selByColor = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"select-color.png")));
		selByColor.setToolTipText("Select by color");
		selByColor.addActionListener(this);
		selectPanel.add(selByColor);
		
		// step tool panel
		gbc.gridx++;
		gbc.gridy = 0;
		JToolBar stepPanel = new JToolBar("Step");
		stepPanel.setBorder(BorderFactory.createTitledBorder("Step"));
		stepPanel.setLayout(new GridLayout(2, 2));
		panel.add(stepPanel,gbc);

		prevStep = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"step-back.png")));
		prevStep.setToolTipText("Previous step");
		prevStep.addActionListener(this);
		stepPanel.add(prevStep);

		nextStep = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"step-next.png")));
		nextStep.setToolTipText("Next step");
		nextStep.addActionListener(this);
		stepPanel.add(nextStep);		
		
		currStep = new JTextField("1");
		currStep.addActionListener(this);
		stepPanel.add(currStep);
		
		firstStep = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"step-first.png")));
		firstStep.setToolTipText("First step");
		firstStep.addActionListener(this);
		stepPanel.add(firstStep);
		
		lastStep = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"step-last.png")));
		lastStep.setToolTipText("Last step");
		lastStep.addActionListener(this);
		stepPanel.add(lastStep);
		
		
		// tool option panel
		gbc.gridx++;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		toolPanel = new JPanel();
		toolPanel.setBorder(BorderFactory.createTitledBorder("Tool option"));
		JLabel emptyTool = new JLabel(" -Tool options here- ");
		emptyTool.setEnabled(false);
		toolPanel.add(emptyTool);
		panel.add(toolPanel, gbc);
		
		gbc.gridx++;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 2;
		panel.add(Box.createHorizontalGlue(),gbc);
		
		gbc.gridx++;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 2;
		panel.add(Box.createHorizontalGlue(),gbc);
		
		gbc.gridwidth = gbc.gridx+1;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		status = new JLabel("Ready", icnReady, SwingConstants.LEFT);
		panel.add(status, gbc);
		
		partChooser = new LDrawPartChooser(frame, "Search for part",true,ldr);

		partMRUChooser = new PartMRUChooser(frame,80);
		
		LDRenderedPart.disableAuxLines();
		LDRenderedPart.useBoundingSelect();
		LDrawPart currentModel = LDrawPart.newEmptyPart();
		if (AppSettings.get(MySettings.AUTHOR) == "") {
			AppSettings.put(MySettings.AUTHOR, System.getProperty("user.name"));
		}
		currentModel.setAuthor(AppSettings.get(MySettings.AUTHOR));
		currentModel.addPart(LDPrimitive.newComment(commentString));
		currentModel.addPart(LDPrimitive.newComment(commentUrl));
		currentModel.initStep();
		modelEdit = LDEditor.newLDModelEditor(currentModel,gldisplay);
		modelEdit.registerChangeListener(this);
		float gridStep = AppSettings.getFloat(MySettings.GRIDSIZE);
		if (gridStep == 0) {
			gridStep = 20;
		}
		float snap = AppSettings.getFloat(MySettings.SNAPSIZE);
		if (snap == 0) {
			snap = 4;
		}
		modelEdit.setGridSize(gridStep);
		modelEdit.setSnap(snap);
		

		frame.getContentPane().add(panel,BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
	}

	
	
	// app init tasks
	private void initPrefs() {
		// defines application preferences
		AppSettings.openPreferences(this);
		// types and description for preferences
//		AppSettings.addPref(MySettings.LDRAWDIR, "Path to your LDraw library folder", AppSettings.FOLDER);
//		AppSettings.addPref(MySettings.FOLDERLIB, "Use installed LDraw library, instead of zipped", AppSettings.BOOLEAN);
		AppSettings.addPref(MySettings.ALLCOLORS, "Use extended color palette (req. restart)", AppSettings.BOOLEAN);
		AppSettings.defBool(MySettings.ALLCOLORS, true);
		AppSettings.addPref(MySettings.GRIDSIZE,"Grid size in LDU",AppSettings.FLOAT);
		AppSettings.defFloat(MySettings.GRIDSIZE, 20.0f);
		AppSettings.addPref(MySettings.SNAPSIZE, "Snap size in LDU",AppSettings.FLOAT);
		AppSettings.defFloat(MySettings.SNAPSIZE, 4.0f);
		AppSettings.addPref(MySettings.AUTHOR,"Author name and e-mail",AppSettings.STRING);
		AppSettings.addPref(MySettings.LOWDETAILS, "Use low-details parts", AppSettings.BOOLEAN);
		AppSettings.defBool(MySettings.LOWDETAILS, false);
		AppSettings.addPref(MySettings.PERSPECTIVE, "Use perspective view", AppSettings.BOOLEAN);
		AppSettings.defBool(MySettings.PERSPECTIVE, false);
		AppSettings.addPref(MySettings.ANTIALIAS, "Use antialias (req. restart)", AppSettings.BOOLEAN);
		AppSettings.defBool(MySettings.ANTIALIAS, true);
		AppSettings.addPref(MySettings.LDDROTMODE, "Use LDD rotation mode (req. restart)", AppSettings.BOOLEAN);
		AppSettings.defBool(MySettings.LDDROTMODE, true);
		AppSettings.addPref(MySettings.LICENSE,"License text for new models",AppSettings.STRING);
		// Private settings
		AppSettings.addPrivatePref(MySettings.LIBLIST, "LDraw lib list", AppSettings.STRING);
		AppSettings.addPrivatePref(MySettings.LDRAWZIP, "Path local LDraw library zip file", AppSettings.FILE);
		AppSettings.defString(MySettings.LDRAWZIP, "complete.zip");
		AppSettings.addPrivatePref(MySettings.LIBOFFICIAL, "LDraw official library", AppSettings.STRING);
	}
	
	
	/**
	 * do some fist run tasks: setup libraries or download it
	 */
	private void firstRun() {
		
		JOptionPane.showMessageDialog(null,
				"Thank you for using "+appName+"!\n" +
				"This is first run.\n"+
				"Program needs some other files to properly work:\n" +
				"- LDraw official library.\n" +
				"- Connection database\n"+
				"I'll ask you some question...",
				"Welcome!", JOptionPane.INFORMATION_MESSAGE, 
				new ImageIcon(this.getClass().getResource("images/icon-about.png")));
		int res = JOptionPane.showConfirmDialog(null, 
				"Do you already have LDraw library?\n"
				+ "You can use your installed library (as a folder) or\n"
				+ "original zipped file with LDraw official library.\n"
				+ "Click 'No' if you don't have any LDraw library\n"
				+ "Program will download latest LDraw library.",
				"LDraw library choice", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
		if (res == JOptionPane.YES_OPTION) {
			JFileChooser jfc = new JFileChooser(".");
			FileFilter ff = new FileNameExtensionFilter("LDraw Library ZIP", "zip");
			jfc.setFileFilter(ff);
			jfc.setDialogTitle("Select Official LDraw library (ZIP or folder)");
			jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			while (true) {
				res = jfc.showOpenDialog(null);
				if (res == JFileChooser.APPROVE_OPTION) {
					try {							
						ldr = new LDrawLib(jfc.getSelectedFile().getPath(),dbc);
						if (!ldr.isOfficial(LDrawLib.OFFICIALINDEX)) {
							JOptionPane.showMessageDialog(null,
									"File/folder "+jfc.getSelectedFile().getName()+"\n" +
									"isn't an official LDraw library.\n"+
									"Try again.",
									"Unknown folder/file", JOptionPane.ERROR_MESSAGE);
							continue;
						}
						break;
					} catch (IOException | SQLException e) {
						JOptionPane.showMessageDialog(null,
								"Unable to use library "+jfc.getSelectedFile().getName()+"\n" +
								"Cause: "+e.getLocalizedMessage()+"\n"+
								"Try again.",
								"Library error", JOptionPane.ERROR_MESSAGE);
						Logger.getGlobal().log(Level.WARNING,"[firstRun] Error selecting library: "+jfc.getSelectedFile(),e);
					}
				}
				else {
					res = JOptionPane.showConfirmDialog(null, 
							"Do you want to exit program?\n",
							"Confirm", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
					if (res == JOptionPane.YES_OPTION) {
						System.exit(0);
					}
				}
			}
		}
		else {
			// download library
			boolean found = false;
			for (int tries=0;tries<3;tries++) {
				doLDrawLibDownload();
				try {
					ldr = new LDrawLib(AppSettings.get(MySettings.LDRAWZIP),dbc);
					if (!ldr.isOfficial(LDrawLib.OFFICIALINDEX)) {
						JOptionPane.showMessageDialog(null,
								"Downloaded file is corrupted.\n" +
								"Try to download again.",
								"Broken file", JOptionPane.ERROR_MESSAGE);
						continue;
					}
					found = true;
					break;
				} catch (IOException | SQLException e) {
					JOptionPane.showMessageDialog(null,
							"Unable to use downloaded file\n" +
							"Cause: "+e.getLocalizedMessage()+"\n"+
							"Try again.",
							"Library error", JOptionPane.ERROR_MESSAGE);
					Logger.getGlobal().log(Level.WARNING,"[firstRun] Unable to use downloaded library",e);
				}
			}
			if (!found) {
				// problems downloading library, exit
				JOptionPane.showMessageDialog(null,
						"Unable to download LDraw library.\n" +
						"Cannot continue, exiting...",
						"Giving up", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		}
		doPartDBUpdate();
		LDLibManageDlg dlg = new LDLibManageDlg(frame, "Edit/add other libraries to use", true, ldr);
		dlg.setVisible(true);
//		if (ldr.getOfficialLibrary() < 0) {
//			// no official library defined!
//			JOptionPane.showMessageDialog(frame, "Official LDraw library not in list.\nYou may have weird/unstable results.", 
//					"Check your libraries", JOptionPane.WARNING_MESSAGE);
//		}
		saveLibs(ldr);
		// here we have all data to continue
		try {
			AppSettings.savePreferences();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, 
					"Cannot save preferences.\n"
					+ "Problem is:\n"
					+ e.getLocalizedMessage(),
					"Preferences error", JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"Preferences write error", e);
		} catch (BackingStoreException e) {
			JOptionPane.showMessageDialog(null, 
					"Cannot save preferences.\n"
					+ "Problem is:\n"
					+ e.getLocalizedMessage(),
					"Preferences error", JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"Preferences write error", e);
		}
	}

	
	
	
	
	// Actions handler
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == perspective) {
			if (perspective.isSelected()) {
				perspective.setIcon(new ImageIcon(this.getClass().getResource(imageFolder+"perspective.png")));
				perspective.setToolTipText("Perspective projection");
			}
			else {
				perspective.setIcon(new ImageIcon(this.getClass().getResource(imageFolder+"brick-filled.png")));
				perspective.setToolTipText("Orthogonal projection");
			}
			AppSettings.putBool(MySettings.PERSPECTIVE, perspective.isSelected());
			gldisplay.setPerspective(perspective.isSelected());
		}
		else if (e.getSource() == newModel) {
			if (canProceedDiscard()) {
				LDrawPart currentModel = LDrawPart.newEmptyPart();
				if (AppSettings.get(MySettings.AUTHOR) == "") {
					AppSettings.put(MySettings.AUTHOR, System.getProperty("user.name"));
				}
				currentModel.setAuthor(AppSettings.get(MySettings.AUTHOR));
				currentModel.addPart(LDPrimitive.newComment(commentString));
				currentModel.addPart(LDPrimitive.newComment(commentUrl));
				currentModel.initStep();
				modelEdit.closeEditor();
				modelEdit = LDEditor.newLDModelEditor(currentModel,gldisplay);
				modelEdit.registerChangeListener(this);
				Thread t = modelEdit.getRenderTask(this);
				t.start();
				frame.setTitle(appName+" - "+currentModel.getLdrawId());
				firstSave = true;
				currStep.setText("1");
			}
		}
		else if (e.getSource() == loadModel) {
			if (canProceedDiscard()) {
				int res = modelFile.showOpenDialog(frame);
				if (res != JFileChooser.APPROVE_OPTION)
					return;
				BusyDialog busyDialog = new BusyDialog(frame,"Reading project",true,icnImg);
				busyDialog.setMsg("Loading model...");
				ImportLDrawProjectTask ldrproject = new ImportLDrawProjectTask(modelFile.getSelectedFile());
				busyDialog.setTask(ldrproject);
				busyDialog.startTask();
				// after completing task return here
				busyDialog.dispose();
				//System.out.println(ldrproject.getModel());
				try {
					ldrproject.get(10, TimeUnit.MILLISECONDS);
					if (ldrproject.isWarnings()) {
						JOptionPane.showMessageDialog(frame, "There are some errors/missing parts. See logs for details", 
								"Project import messages", JOptionPane.WARNING_MESSAGE);
					}
				}
				catch (ExecutionException ex) {
					Logger.getGlobal().log(Level.SEVERE,"Execution interrupted", ex);
				} catch (InterruptedException e1) {
					Logger.getGlobal().log(Level.SEVERE,"Execution interrupted", e);
				} catch (TimeoutException e1) {
					Logger.getGlobal().log(Level.SEVERE,"Execution interrupted", e);
				}
				modelEdit.closeEditor();
				if (ldrproject.isMpd()) {
					// import and change MPD to LDR name
					firstSave = true;
					modelSave.setSelectedFile(
							new File(modelFile.getCurrentDirectory(),ldrproject.getModel().getLdrawId()));
				}
				else {
					currentFile = modelFile.getSelectedFile();
					loadedFiles.put(ldrproject.getModel().getLdrawId(), currentFile);
					firstSave = false;
				}
				modelEdit = LDEditor.newLDModelEditor(ldrproject.getModel(),gldisplay);
				modelEdit.registerChangeListener(this);
				Thread t = modelEdit.getRenderTask(this);
				t.start();
				frame.setEnabled(false);
				frame.setTitle(appName+" - "+modelEdit.getLdrawid());
				currStep.setText(Integer.toString(modelEdit.goLastStep()));
			}
		}
		else if (e.getSource() == saveModel) {
			if (firstSave) {
				int res = modelSave.showSaveDialog(frame);
				if (res != JFileChooser.APPROVE_OPTION) {
					return;
				}
				currentFile = modelSave.getSelectedFile();
			}
			else {
				currentFile = loadedFiles.get(modelEdit.getLdrawid());				
			}
			try {
				modelEdit.setPartName(currentFile.getName());
				modelEdit.saveAsLdr(currentFile);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(frame, "Unable to write model file:\n"+e1.getLocalizedMessage(), 
						"Save error", JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"Model save error", e1);
				return;
			}
			if (firstSave) {
				modelEdit.registerCustomPart(currentFile.getName());
				loadedFiles.put(modelEdit.getLdrawid(), currentFile);
				LDEditor.savedPart(modelEdit.getLdrawid());
			}
			frame.setTitle(appName+" - "+modelEdit.getLdrawid());
			modelEdit.markSave();
			firstSave = false;
		}
		else if (e.getSource() == saveModelAs) {
			
			int res = modelSave.showSaveDialog(frame);
			if (res != JFileChooser.APPROVE_OPTION) {
				return;
			}
			currentFile = modelSave.getSelectedFile();
			try {
				modelEdit.setPartName(currentFile.getName());
				modelEdit.saveAsLdr(currentFile);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(frame, "Unable to write model file:\n"+e1.getLocalizedMessage(), 
						"Save error", JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"Model save error", e1);
				return;
			}
			//LDrawPart.delCustomPart(currentModel.getLdrawid());
			modelEdit.registerCustomPart(currentFile.getName());
			loadedFiles.put(modelEdit.getLdrawid(), currentFile);
			frame.setTitle(appName+" - "+modelEdit.getLdrawid());
			//continueEdit();
			modelEdit.markSave();
			firstSave = false;
		}
		else if (e.getSource() == loadSubPart) {
			int res = subPartFile.showOpenDialog(frame);
			if (res != JFileChooser.APPROVE_OPTION)
				return;
			BusyDialog busyDialog = new BusyDialog(frame,"Reading model",true,icnImg);
			busyDialog.setMsg("Loading model...");
			ImportLDrawProjectTask ldrproject = new ImportLDrawProjectTask(subPartFile.getSelectedFile());
			busyDialog.setTask(ldrproject);
			busyDialog.startTask();
			// after completing task return here
			busyDialog.dispose();
			try {
				ldrproject.get(10, TimeUnit.MILLISECONDS);
				if (ldrproject.isWarnings()) {
					JOptionPane.showMessageDialog(frame, "There are some errors/missing parts. See logs for details", 
							"Model import messages", JOptionPane.WARNING_MESSAGE);
				}
			}
			catch (ExecutionException ex) {
				Logger.getGlobal().log(Level.SEVERE,"Execution interrupted", ex);
			} catch (InterruptedException e1) {
				Logger.getGlobal().log(Level.SEVERE,"Execution interrupted", e1);
			} catch (TimeoutException e1) {
				Logger.getGlobal().log(Level.SEVERE,"Execution interrupted", e1);
			}
			loadedFiles.put(ldrproject.getModel().getLdrawId(), subPartFile.getSelectedFile());
			modelEdit.startAction(LDEditor.ADDPLUGIN, ldrproject.getModel().getLdrawId(),
					colorTool.getSelected(),false);
		}
		else if (e.getSource() == addSubPart) {
			LDrawBlockChooser dlg = new LDrawBlockChooser(frame, "Select block/custom part", 
					LDEditor.getUnsavedParts(),true);
			dlg.setVisible(true);
			if (dlg.getSelected() != null) {
				if (dlg.getResponse() == JOptionPane.OK_OPTION) {
					if (dlg.getSelected().getLdrawId().equals(modelEdit.getLdrawid())) {
						JOptionPane.showMessageDialog(frame, 
								"Cannot add current editing part.\n"+
								"This will create a recursive reference.",
								"Recursion not allowed",JOptionPane.ERROR_MESSAGE);
					}
					else {
						modelEdit.startAction(LDEditor.ADDPLUGIN, dlg.getSelected().getLdrawId(),
								colorTool.getSelected(),false);
						//modelEdit.startAddPart(dlg.getSelected().getLdrawid(),
								//colorTool.getSelected());
					}
				}
				else if (dlg.getResponse() == -42) {
					// edit model
					if (!dlg.getSelected().getLdrawId().equals(modelEdit.getLdrawid()) && canProceedDiscard()) {
						LDrawPart currentModel = LDrawPart.getCustomPart(dlg.getSelected().getLdrawId());
						//System.out.println(currentModel);
						firstSave = false;
						if (! loadedFiles.containsKey(currentModel.getLdrawId())) {
							loadedFiles.put(currentModel.getLdrawId(), 
									new File(modelSave.getCurrentDirectory(),currentModel.getLdrawId()));
							firstSave = true;
						}
						currentFile = loadedFiles.get(currentModel.getLdrawId());
						modelSave.setSelectedFile(currentFile);
						modelEdit.closeEditor();
						modelEdit = LDEditor.newLDModelEditor(currentModel,gldisplay);
						modelEdit.registerChangeListener(this);
						Thread t = modelEdit.getRenderTask(this);
						t.start();
						//ch.addAllConnections(currentModel);
						frame.setTitle(appName+" - "+modelEdit.getLdrawid());
						currStep.setText(Integer.toString(modelEdit.goLastStep()));
					}
				}
			}
		}
		else if (e.getSource() == modelData) {
			ModelDataDialog dlg = new ModelDataDialog(frame, "Set model information", true, modelEdit);
			dlg.setVisible(true);
		}
		else if (e.getSource() == light) {
			if (light.isSelected()) {
				light.setToolTipText("Use light");
				light.setIcon(new ImageIcon(this.getClass().getResource(imageFolder+"light-on.png")));
			}
			else {
				light.setToolTipText("Flat lighting");
				light.setIcon(new ImageIcon(this.getClass().getResource(imageFolder+"light-off.png")));
			}
			gldisplay.setLighting(light.isSelected());
		}
		else if (e.getSource() == polygon) {
			if (polygon.isSelected()) {
				polygon.setToolTipText("Polygon filled");
				polygon.setIcon(new ImageIcon(this.getClass().getResource(imageFolder+"brick-filled.png")));
			}
			else {
				polygon.setToolTipText("Polygon hidden");
				polygon.setIcon(new ImageIcon(this.getClass().getResource(imageFolder+"brick-wire.png")));				
			}
			gldisplay.setPolygon(polygon.isSelected());
		}
		else if (e.getSource() == wires) {
			if (wires.isSelected()) {
				wires.setToolTipText("Display edges");
				wires.setIcon(new ImageIcon(this.getClass().getResource(imageFolder+"brick-wire.png")));
			}
			else {
				wires.setToolTipText("Edges hidden");
				wires.setIcon(new ImageIcon(this.getClass().getResource(imageFolder+"brick-wire-off.png")));
			}
			gldisplay.setWireframe(wires.isSelected());
		}
		else if (e.getSource() == smoothPolygon) {
			if (smoothPolygon.isSelected()) {
				smoothPolygon.setToolTipText("Standard details");
				smoothPolygon.setIcon(new ImageIcon(this.getClass().getResource(imageFolder+"smooth.png")));
				LDrawLib.resetStdRes();
			}
			else {
				smoothPolygon.setToolTipText("Lo-res details");
				smoothPolygon.setIcon(new ImageIcon(this.getClass().getResource(imageFolder+"rough.png")));
				LDrawLib.forceLoRes();
			}
			LDrawPart.clearCache();
			Thread t = modelEdit.getRenderTask(this);
			t.start();
			AppSettings.putBool(MySettings.LOWDETAILS, !smoothPolygon.isSelected());
		}
		else if (e.getSource() == panleft) {
			gldisplay.setOffsetx(10f);
		}
		else if (e.getSource() == panright) {
			gldisplay.setOffsetx(-10f);
		}
		else if (e.getSource() == panup) {
			gldisplay.setOffsety(10f);
		}
		else if (e.getSource() == pandown) {
			gldisplay.setOffsety(-10f);
		}
		else if (e.getSource() == delBrick) {
			modelEdit.startAction(LDEditor.DELPLUGIN);
			//doDeleteBrick();
		}
		else if (e.getSource() == addBrick) {
			doAddBrick();
		}
		else if (e.getSource() == colorBrick) {
			modelEdit.startAction(LDEditor.RECOLORPLUGIN,colorTool.getSelected());
		}
		
		else if (e.getSource() == addMruBrick) {
			partMRUChooser.showLocation(addMruBrick);
			partMRUChooser.setVisible(true);
			if (partMRUChooser.getResponse() == JOptionPane.OK_OPTION) {
				String part = partMRUChooser.getSelected();
				if (LDrawPart.isLdrPart(part)) {
					modelEdit.startAction(LDEditor.ADDPLUGIN,part, colorTool.getSelected(),false);
				}
			}
		}
		else if (e.getSource() == dupBrick) {
			modelEdit.startAction(LDEditor.DUPPLUGIN);
		}
		else if (e.getSource() == rotateBrick) {
			modelEdit.startAction(LDEditor.ROTATEPLUGIN, toolPanel);
		}
		else if (e.getSource() == alignGrid) {
			modelEdit.alignGridToSelectedPart();
		}
		else if (e.getSource() == gridXY) {
			modelEdit.alignXY();
		}
		else if (e.getSource() == gridXZ) {
			modelEdit.alignXZ();
		}
		else if (e.getSource() == gridYZ) {
			modelEdit.alignYZ();
		}
		else if (e.getSource() == gridDown) {
			modelEdit.upY();
		}
		else if (e.getSource() == gridUp) {
			modelEdit.downY();
		}
		else if (e.getSource() == gridRight) {
			modelEdit.upX();
		}
		else if (e.getSource() == gridLeft) {
			modelEdit.downX();
		}
		else if (e.getSource() == gridNear) {
			modelEdit.downZ();
		}
		else if (e.getSource() == gridFar) {
			modelEdit.upZ();
		}
		else if (e.getSource() == zoomin) {
			gldisplay.setZoomFactor(0.7f);
		}
		else if (e.getSource() == zoomout) {
			gldisplay.setZoomFactor(1.3f);
		}
		else if (e.getSource() == zoomreset) {
			gldisplay.resetZoom();
		}
		else if (e.getSource() == rotup) {
			gldisplay.rotateX(5);
		}
		else if (e.getSource() == rotdown) {
			gldisplay.rotateX(-5);
		}
		else if (e.getSource() == rotleft) {
			gldisplay.rotateY(-5);
		}
		else if (e.getSource() == rotright) {
			gldisplay.rotateY(5);
		}
		else if (e.getSource() == testBrick) {
			// XXX test action
		}
		else if (e.getSource() == povExport) {
			// povray experimental code
			int res = povFile.showSaveDialog(frame);
			if (res != JFileChooser.APPROVE_OPTION) {
				return;
			}
			try {
				LDPovRenderer r = LDPovRenderer.getRenderer(povFile.getSelectedFile());
				r.setPerspective(gldisplay.getZoomFactor());
				r.setViewMatrix(gldisplay.getViewMatrix());
				r.startRender();
				r.addModel(modelEdit.getPrimitives());
			} catch (UnsupportedEncodingException e1) {
				//  Auto-generated catch block
				e1.printStackTrace();
			} catch (FileNotFoundException e1) {
				//  Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// Auto-generated catch block
				e1.printStackTrace();
			}

			//System.out.println(currentModel.getPrimitives());
			//LDrawPart.listcache();
			//ch.dumpConnections();
//			ch.getVectors().dumpStruct();
//			for (Gadget3D g: ch.getVectors().getBoundingBoxes()) {
//				gldisplay.addGadget(g);
//			}   // D
			//System.out.println(undo.toString());
			//System.out.println(LDEditor.getUnsavedParts());
		}
//		else if (e.getSource() == flexBrick) {
//			modelEdit.startAction(LDEditor.FLEXPLUGIN);
//		}
		else if (e.getSource() == setGrid) {
			String g = JOptionPane.showInputDialog(frame, "Set grid size (LDU)", 
					String.format(Locale.US,"%s", modelEdit.getGridSize()));
			if (g == null)
				return;
			try {
				float n = Float.parseFloat(g);
				if (n < 0.1 || n > 1000) 
					return;
				modelEdit.setGridSize(n);
				AppSettings.putFloat(MySettings.GRIDSIZE, modelEdit.getGridSize());
			}
			catch (NumberFormatException ex) {
				return;
			}
		}
		else if (e.getSource() == resetGrid) {
			modelEdit.resetGrid();
		}
		else if (e.getSource() == resetPointer) {
			modelEdit.resetPointerMatrix();
		}
		else if (e.getSource() == setSnap) {
			String g = JOptionPane.showInputDialog(frame, "Set snap size (LDU)", 
					String.format(Locale.US,"%s", modelEdit.getSnap()));
			if (g == null)
				return;
			try {
				float n = Float.parseFloat(g);
				if (n < 0.1 || n > 100) 
					return;
				modelEdit.setSnap(n);
				AppSettings.putFloat(MySettings.SNAPSIZE, modelEdit.getSnap());
			}
			catch (NumberFormatException ex) {
				return;
			}
		}
		else if (e.getSource() == setAngleStep) {
			angleStep.show((Component)e.getSource(), 10, 10);
		}
		else if (e.getSource() == panreset) {
			modelEdit.resetView();
		}
		else if (e.getSource() == mntmLdrGet) {
			doLDrawLibDownload();
			JOptionPane.showMessageDialog(frame, 
					"You must restart program to use new library\n",
					"New library",JOptionPane.INFORMATION_MESSAGE);
		}
		else if (e.getSource() == mntmLdrParts) {
			doPartDBUpdate();
		}
		else if (e.getSource() == mntmLdrConn) {
			if (canProceedDiscard()) {
				doConnDownload();
				try {
					ConnectionPoint.init();
					JOptionPane.showMessageDialog(frame, 
							"You need to restart program to use new\n"
							+ "connection database.\n"
							+ "Exit now...",
							"Connection updated",JOptionPane.INFORMATION_MESSAGE);
					closeApp();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(frame, 
							"Unable to use new connections database\n"
							+ "Please retry",
							"Connections problems",JOptionPane.ERROR_MESSAGE);
					Logger.getGlobal().log(Level.SEVERE,"Connection database init error", e1);
				}
			}
		}
		else if (e.getSource() == mntmAbout) {
			AboutDialog dlg = new AboutDialog(frame, appName, 
					new ImageIcon(this.getClass().getResource(imageFolder+"icon-big.png")));
			dlg.setVisible(true);
		}
		else if (e.getSource() == hideParts) {
			modelEdit.hideSelected();
			resetPartInfo();
		}
		else if (e.getSource() == unHideAll) {
			modelEdit.showAll();
		}
		else if (e.getSource() == saveBlock) {
			modelEdit.saveSelectedAsBlock();
			resetPartInfo();
		}
		else if (e.getSource() == editUndo) {
			modelEdit.undoLastEdit();
		}
		else if (e.getSource() == editRedo) {
			modelEdit.redoLastEdit();
		}
		else if (e.getSource() == explodeSubModel) {
			modelEdit.explodeSelected();
		}
		else if (e.getSource() == enableGrid) {
			if (enableGrid.isSelected()) {
				enableGrid.setToolTipText("Grid ON");
			}
			else {
				enableGrid.setToolTipText("Grid OFF");
			}
			modelEdit.enableGrid(enableGrid.isSelected());
		}
		else if (e.getSource() == enableSnap) {
			if (enableSnap.isSelected()) {
				enableSnap.setToolTipText("Snap ON");
			}
			else {
				enableSnap.setToolTipText("Snap OFF");
			}
			LDEditor.setSnapping(enableSnap.isSelected());
		}
		else if (e.getSource() == enableAxis) {
			if (enableAxis.isSelected()) {
				enableAxis.setToolTipText("Axis ON");
			}
			else {
				enableAxis.setToolTipText("Axis OFF");
			}
			modelEdit.enableAxis(enableAxis.isSelected());
		}
		else if (e.getSource() == saveShot) {
			int res = imgFile.showSaveDialog(frame);
			if (res != JFileChooser.APPROVE_OPTION)
				return;
			try {
				BufferedImage img = gldisplay.getScreenShot();
				ImageIO.write(img, "PNG",imgFile.getSelectedFile());
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(frame, 
						"Unable to write screenshot image:\n"+e1.getLocalizedMessage(),
						"Write error",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"Image write error", e1);
			}
		}
		else if (e.getSource() == resetEdit) {
			modelEdit.resetCurrentAction();
		}
		else if (e.getSource() == editCopy) {
			modelEdit.doCutCopy(false);
		}
		else if (e.getSource() == editCut) {
			modelEdit.doCutCopy(true);
		}
		else if (e.getSource() == editPaste) {
			modelEdit.doPaste();
		}
		else if (e.getSource() == angleStep1) {
			LDEditor.setRotateStep((float) (1*Math.PI/180));
		}
		else if (e.getSource() == angleStep5) {
			LDEditor.setRotateStep((float) (5*Math.PI/180));
		}
		else if (e.getSource() == angleStep10) {
			LDEditor.setRotateStep((float) (10*Math.PI/180));
		}
		else if (e.getSource() == angleStep15) {
			LDEditor.setRotateStep((float) (15*Math.PI/180));
		}
		else if (e.getSource() == angleStep18) {
			LDEditor.setRotateStep((float) (18*Math.PI/180));
		}
		else if (e.getSource() == angleStep22) {
			LDEditor.setRotateStep((float) (22.5*Math.PI/180));
		}
		else if (e.getSource() == angleStep33) {
			LDEditor.setRotateStep((float) (33*Math.PI/180));
		}
		else if (e.getSource() == angleStep45) {
			LDEditor.setRotateStep((float) (Math.PI/4));
		}
		else if (e.getSource() == angleStep90) {
			LDEditor.setRotateStep((float) (Math.PI/2));
		}
		else if (e.getSource() == autoConnect) {
			LDEditor.setAutoconnect(autoConnect.isSelected());
		}
		else if (e.getSource() == repeatBrick) {
			LDEditor.setRepeatBrick(repeatBrick.isSelected());
		}
		else if (e.getSource() == exportAsMpd) {
			int res = modelSaveMpd.showSaveDialog(frame);
			if (res != JFileChooser.APPROVE_OPTION) {
				return;
			}
			try {
				modelEdit.saveAsMpd(modelSaveMpd.getSelectedFile());
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(frame, "Unable to write MPD model file:\n"+e1.getLocalizedMessage(), 
						"Save error", JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"Model write error", e1);
				return;
			}
		}
		// selection
		else if (e.getSource() == selById) {
			modelEdit.selectByPartId();
		}
		else if (e.getSource() == selByColor) {
			modelEdit.selectByColor();
		}
		else if (e.getSource() == nextStep) {
			currStep.setText(Integer.toString(modelEdit.nextStep()));
		}
		else if (e.getSource() == prevStep) {
			currStep.setText(Integer.toString(modelEdit.prevStep()));
		}
		else if (e.getSource() == lastStep) {
			currStep.setText(Integer.toString(modelEdit.goLastStep()));
		}
		else if (e.getSource() == firstStep) {
			currStep.setText(Integer.toString(modelEdit.goFirstStep()));
		}
		else if (e.getSource() == stepBrick) {
			modelEdit.startAction(LDEditor.STEPPLUGIN, toolPanel);
		}
		else if (e.getSource() == editSubModel) {
			if (modelEdit.getSelected().size() == 1) {
				LDPrimitive p = modelEdit.getPart(modelEdit.getSelected().iterator().next());
				if (LDrawPart.existsCustomPart(p.getLdrawId()) && canProceedDiscard()) {
					LDrawPart currentModel = LDrawPart.getCustomPart(p.getLdrawId());
					//System.out.println(currentModel);
					firstSave = false;
					if (! loadedFiles.containsKey(currentModel.getLdrawId())) {
						loadedFiles.put(currentModel.getLdrawId(), 
								new File(modelSave.getCurrentDirectory(),currentModel.getLdrawId()));
						firstSave = true;
					}
					currentFile = loadedFiles.get(currentModel.getLdrawId());
					modelSave.setSelectedFile(currentFile);
					modelEdit.closeEditor();
					modelEdit = LDEditor.newLDModelEditor(currentModel,gldisplay);
					modelEdit.registerChangeListener(this);
					Thread t = modelEdit.getRenderTask(this);
					t.start();
					//ch.addAllConnections(currentModel);
					frame.setTitle(appName+" - "+modelEdit.getLdrawid());
					currStep.setText(Integer.toString(modelEdit.goLastStep()));
				}
			}
		}
		else if (e.getSource() == mntmOptions) {
			OptionsDialog dlg = new OptionsDialog(frame, "Program options", true);
			dlg.setVisible(true);
			if (dlg.getResponse() == JOptionPane.OK_OPTION) {
				// save new preferences
				savePrefs();
			}
		}
		else if (e.getSource() == mntmLibs) {
			LDLibManageDlg dlg = new LDLibManageDlg(frame, "Manage LDraw libraries", true, ldr);
			dlg.setVisible(true);
			saveLibs(ldr);
		}
		else if (e.getSource() == mntmExit) {
			closeApp();
		}
		saveModel.setIcon(modelEdit.isModified()?modifiedSaveIcon:normalSaveIcon);
		editUndo.setEnabled(modelEdit.isUndoAvailable());
		editRedo.setEnabled(modelEdit.isRedoAvailable());
		gldisplay.getCanvas().requestFocusInWindow();
	}


	
	/////////////////////////////
	//
	//   Utility functions
	//
	/////////////////////////////
	
	
	private void doAddBrick() {
		
		partChooser.setLocationRelativeTo(frame);
		partChooser.setVisible(true);
		if (partChooser.getResponse() != JOptionPane.OK_OPTION) 
			return;
		if (partChooser.getSelected() == null)
			return;
		String part = partChooser.getSelected().getLdrawId();
		if (LDrawPart.isLdrPart(part)) {
			if (partChooser.isFlexPart() && LDFlexPart.getFlexPart(part) != null) {
				// it is a flex part
				modelEdit.startAction(LDEditor.FLEXPLUGIN,toolPanel,part, colorTool.getSelected(),modelEdit.getLdrawid());
			}
			else {
				modelEdit.startAction(LDEditor.ADDPLUGIN,part, colorTool.getSelected(),false);
				partMRUChooser.addElement(part, partChooser.getSelected().getDescription(), 
						partChooser.getSelImage().getScaledInstance(75, 75, Image.SCALE_FAST));
			}
		}
		else {
			JOptionPane.showMessageDialog(frame, 
					"No part with code:\n"+part,
					"Ldraw part error",JOptionPane.ERROR_MESSAGE);
		}
	}
	

	
	private void resetPartInfo() {
		status.setText("-");
	}

	
	
	
	private void closeApp() {

		if (canProceedDiscard() && discardUnsavedParts()) {
			savePrefs();
			frame.dispose();
			System.exit(0);
		}
	}
	
	
	
	
	private void savePrefs() {
		
		try {
			AppSettings.savePreferences();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, 
					"Cannot save preferences.\n"
					+ "Problem is:\n"
					+ e.getLocalizedMessage(),
					"Preferences error", JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"Preferences write error", e);
		} catch (BackingStoreException e) {
			JOptionPane.showMessageDialog(null, 
					"Cannot save preferences.\n"
					+ "Problem is:\n"
					+ e.getLocalizedMessage(),
					"Preferences error", JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"Preferences write error", e);
		}
	}
	
	
	

	public void saveLibs(LDrawLib l) {
		
		String preflib = "";
		
		AppSettings.put(MySettings.LIBOFFICIAL, l.getPath(LDrawLib.OFFICIALINDEX));
		for (int i=1;i<l.count();i++) {
			if (i > 1) 
				preflib += "|";	// adds a separator
			preflib += l.getPath(i)+"|"+(l.isEnabled(i)?"t":"f"); 
		}
		AppSettings.put(MySettings.LIBLIST, preflib);
	}


	
	
	public LDrawLib loadLibs() throws IOException, SQLException {
		
		LDrawLib l = new LDrawLib(AppSettings.get(MySettings.LIBOFFICIAL),dbc);
		String preflib = AppSettings.get(MySettings.LIBLIST);
		String[] libs = preflib.split("\\|");
		int n = 0;
		while (n+1 < libs.length) {
			// add library and set if enabled (string == "t" is for "true")
			l.addLDLib(libs[n],libs[n+1].equals("t"));
			n += 2;
		}
		return l;
	}
	



	

	/**
	 * return true if can proceed losing changes
	 * @return
	 */
	private boolean canProceedDiscard() {
		
		if (modelEdit.isModified()) {
			int res = JOptionPane.showConfirmDialog(frame, 
					"Current model modified.\nDiscarding changes, are you sure?", 
					"Confirm unsaved", JOptionPane.YES_NO_OPTION);
			return (res == JOptionPane.YES_OPTION);			
		}
		return true;
	}
	
	
	
	private boolean discardUnsavedParts() {
		
		if (LDEditor.getUnsavedParts().size() > 0) {
			int res = JOptionPane.showConfirmDialog(frame, 
					"One or more generated parts unsaved.\nClosing program will lose parts, are you sure?", 
					"Lose unsaved", JOptionPane.YES_NO_OPTION);
			return (res == JOptionPane.YES_OPTION);			
		}
		return true;
	}
	
	
	
/////////////////////////////////////
////
////   Downloads and updates
////
/////////////////////////////////////
	
	private void doLDrawLibDownload() {
		
		BusyDialog busyDialog = new BusyDialog(null,"Download LDraw library",true,icnImg);
		busyDialog.setMsg("Downloading library...");
		GetFileFromURL task;
		try {
			task = new GetFileFromURL(new URL(LDRURL),new File(AppSettings.get(MySettings.LDRAWZIP)),busyDialog);
		} catch (MalformedURLException e) {
			Logger.getGlobal().log(Level.SEVERE,"Invalid LDraw library download URL", e);
			return;
		}
		busyDialog.setTask(task);
		busyDialog.startTask();
		// after completing task return here
		busyDialog.dispose();
		try {
			task.get(10, TimeUnit.MILLISECONDS);
			busyDialog = new BusyDialog(frame,"Update part database",true,icnImg);
		}
		catch (ExecutionException | InterruptedException | TimeoutException ex) {
			Logger.getGlobal().log(Level.SEVERE,"Execution interrupted", ex);
			JOptionPane.showMessageDialog(frame, "Something goes wrong, program may not work with LDraw library.\nTry to download again.\n"+ex.getLocalizedMessage(), 
					"Ldraw part update", JOptionPane.ERROR_MESSAGE);
		}
		try {
			busyDialog.setMsg("Reading part from library...");
			LDrawDBImportTask taskdb = new LDrawDBImportTask(ldr, LDrawLib.OFFICIALINDEX);
			busyDialog.setTask(taskdb);
			busyDialog.startTask();
			// after completing task return here
			busyDialog.dispose();
			int i = taskdb.get(10, TimeUnit.MILLISECONDS);
			JOptionPane.showMessageDialog(frame, "Imported "+i+" LDraw parts.", 
						"Ldraw part database", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (ExecutionException | InterruptedException | TimeoutException ex) {
			Logger.getGlobal().log(Level.SEVERE,"Execution interrupted", ex);
			JOptionPane.showMessageDialog(frame, "Something goes wrong, program may not work:\n"+ex.getLocalizedMessage(), 
					"Ldraw part update", JOptionPane.ERROR_MESSAGE);
		}

	}
	
	
	private void doConnDownload() {
		
		BusyDialog busyDialog = new BusyDialog(null,"Download connections DB",true,icnImg);
		busyDialog.setMsg("Downloading zip file...");
		GetFileFromURL task;
		try {
			task = new GetFileFromURL(new URL(ConnectionPoint.CONNURL),new File(ConnectionPoint.CONNZIP),busyDialog);
		} catch (MalformedURLException e) {
			Logger.getGlobal().log(Level.SEVERE,"Invalid LDraw library download URL", e);
			return;
		}
		busyDialog.setTask(task);
		busyDialog.startTask();
		// after completing task return here
		busyDialog.dispose();
		try {
			task.get(10, TimeUnit.MILLISECONDS);
			JOptionPane.showMessageDialog(null, "Connections DB successfully downloaded.", 
						"Download Connections", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (ExecutionException ex) {
			Logger.getGlobal().log(Level.SEVERE,"Execution interrupted", ex);
		} catch (InterruptedException e1) {
			Logger.getGlobal().log(Level.SEVERE,"Execution interrupted", e1);
		} catch (TimeoutException e1) {
			Logger.getGlobal().log(Level.SEVERE,"Execution interrupted", e1);
		}

	}
	
	
	private void doPartDBUpdate() {
		
		BusyDialog busyDialog = new BusyDialog(frame,"Update part database",true,icnImg);
		busyDialog.setMsg("Reading part from library...");
		LDrawDBImportTask task = new LDrawDBImportTask(ldr, LDrawLib.OFFICIALINDEX);
		busyDialog.setTask(task);
		busyDialog.startTask();
		// after completing task return here
		busyDialog.dispose();
		try {
			int i = task.get(10, TimeUnit.MILLISECONDS);
			JOptionPane.showMessageDialog(frame, "Imported "+i+" LDraw parts.", 
						"Ldraw part database", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (ExecutionException ex) {
			Logger.getGlobal().log(Level.SEVERE,"Execution interrupted", ex);
		} catch (InterruptedException e1) {
			Logger.getGlobal().log(Level.SEVERE,"Execution interrupted", e1);
		} catch (TimeoutException e1) {
			Logger.getGlobal().log(Level.SEVERE,"Execution interrupted", e1);
		}

	}

	

	
	////////////////////
	//
	//   status icon
	//
	////////////////////
	
	
	@Override
	public void updateDone(int done, int total) {
		// ignored
		
	}



	@Override
	public void updateRemaining(int todo, int total) {
		// ignored
		
	}



	@Override
	public void updateDoing() {
		// ignored
		
	}



	@Override
	public void updateStart() {
		
		status.setIcon(icnBusy);
		status.setText("Busy");
	}



	@Override
	public void updateComplete() {

		//gldisplay.placeModel(renderedModel);
		status.setIcon(icnReady);
		status.setText("Ready");
		//dh.enablePointer();
		frame.setEnabled(true);
	}



	@Override
	public void updateIncomplete() {
		
		//gldisplay.placeModel(renderedModel);
		status.setIcon(icnError);
		status.setText("Error");
		//dh.enablePointer();
		frame.setEnabled(true);
	}





	
	/**
	 * Main
	 */
	public static void main(String[] args) {
		
		// to get jar path
		//System.out.println(ClassLoader.getSystemClassLoader().getResource(".").getPath());
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
		            // Set cross-platform Java L&F (also called "Metal")
			        UIManager.setLookAndFeel(
			            UIManager.getCrossPlatformLookAndFeelClassName());
			        jBrickBuilder app = new jBrickBuilder();
					app.frame.setVisible(true);
				} catch (Exception e) {
					Logger.getGlobal().log(Level.SEVERE,"Execution exception", e);
				}
			}
		});
		
	}



	@Override
	public void colorChanged(int newColor) {
		
		modelEdit.currentColorChanged(newColor);
		gldisplay.getCanvas().requestFocusInWindow();
	}



	@Override
	public void undoAvailableNotification(boolean available) {

		editUndo.setEnabled(available);
	}



	@Override
	public void redoAvailableNotification(boolean available) {

		editRedo.setEnabled(available);
	}



	@Override
	public void modifiedNotification(boolean modified) {

		saveModel.setIcon(modelEdit.isModified()?modifiedSaveIcon:normalSaveIcon);
		//saveModel.setEnabled(modified);
	}



	@Override
	public void selectedPartChanged(LDPrimitive p) {
		if (p != null) {
			status.setText(
				"LDraw: " + p.getLdrawId() + 
						" - " + p.getDescription() +
						" Color: " +  Integer.toString(p.getColorIndex()) +
						" - " + LDrawColor.getById(p.getColorIndex()).getName() +
						" ID: " + Integer.toString(p.getId()) + 
						" - " + (p.getType() + 
								" s: "+p.getStep()) 
				);
		}
		else {
			status.setText("-");
		}

	}
	
	
	
	@Override
	public void selectedConnChanged(ConnectionPoint p) {}



	@Override
	public void cutCopyAvailable(boolean available) {
		
		editCopy.setEnabled(available);
		editCut.setEnabled(available);
	}



	@Override
	public void pasteAvailable(boolean available) {
		
		editPaste.setEnabled(available);
	}

	
}
