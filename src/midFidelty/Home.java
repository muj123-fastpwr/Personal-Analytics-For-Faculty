package midFidelty;


import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JLayeredPane;
import javax.swing.JSplitPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;


import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.util.Date;
import java.util.Calendar;

import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Color;

import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.UIManager;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import java.awt.SystemColor;
import java.awt.Font;
import java.awt.Canvas;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ActionEvent;


/*
 * @author: Mujtaba ali
 */

public class Home {

	private JFrame frmPerAnal;
	private static Connection cn,c;
	private Window w1;
	private static Window w2;
	private JTextArea activeWindow;
	private int width, height;
	private JTabbedPane tabbedPane, tabbedPane2;
	private JSplitPane splitPane;
	private JPanel piePanel, barPanel, ganttPanel;
	private JFreeChart pieChart, barChart,ganttChart;
	private ChartPanel chGanttPanel, chPiePanel, chBarPanel;
	private JButton winRefresh1, winRefresh2;
	private JLabel lblDate;
	private JSpinner daySpinner;
	private JLabel homeLogo;
	private JLabel lblActiveWindow;
	private JButton start, stop;
	private JLabel startlbl, stoplbl;
	
	public static void main(String[] args) throws InterruptedException, HeadlessException, SQLException, IOException, ClassNotFoundException {
					
					Home window = new Home();
					window.frmPerAnal.setVisible(true);
					
		
					w2.focusedWindow(cn,c);
					
				
	}
	
	
	public Home() throws InterruptedException, ClassNotFoundException, IOException, HeadlessException, SQLException{
		width = 759;
		height = 500;
		initializeFrame();
		
		cn = connectDB();
		w2 = new Window(cn);
		w1 = new Window();
		w1.openWindows();
		System.out.println("first Connection : "+cn);
		c = connectDB2();
		System.out.println("second Connection : "+c);
		initializeMenu();
		initializePanes();
		
	}
	
	
		
	
	private void initializeFrame() {
		frmPerAnal = new JFrame();
		frmPerAnal.getContentPane().setBackground(SystemColor.menu);
		frmPerAnal.setTitle("Personal Analytics");
		frmPerAnal.setIconImage(Toolkit.getDefaultToolkit().getImage("personal.png"));
		frmPerAnal.setBounds(100, 100, 750, 500);
		frmPerAnal.addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent e){
				width = frmPerAnal.getWidth();
				height = frmPerAnal.getHeight();
				tabbedPane.setBounds(10, 20, width-35, height-90);
				splitPane.setBounds(10, 43, width-55, height-165);
				tabbedPane2.setBounds(10, 45, width-55, height-165);
				homeLogo.setBounds((width/2)-(128/2)-10, (height/2)-(128/2)-20, 128, 128);
				start.setBounds((width/2)-130, 50, 105, 50);
				stop.setBounds((width/2), 50, 105, 50);
				startlbl.setBounds((width/2)-130, 105, 120, 30);
				stoplbl.setBounds((width/2), 105, 120, 30);
				
				piePanel.setBounds(0, 0, width-120, height-170);
				chPiePanel.setPreferredSize(new Dimension(width-170, height-185));
				ganttPanel.setBounds(0, 0, width-120, height-170);
				chBarPanel.setPreferredSize(new Dimension(width-160, height-185));
				barPanel.setBounds(0, 0, width-120, height-170);
				chGanttPanel.setPreferredSize(new Dimension(width-160, height-185));
				
				
				winRefresh1.setBounds(width-75, 5, 30, 30);
				winRefresh2.setBounds(width-75, 5, 30, 30);
				daySpinner.setBounds(width-250, 14, 141, 20);
				lblDate.setBounds(width-280, 17, 25, 14);
				lblActiveWindow.setBounds(width/2, 18, 105, 14);
				
				frmPerAnal.repaint();
			}
	
		});
		frmPerAnal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	
	private Connection connectDB(){
		Connector c = new Connector();
		return c.connectDataBase();
	}
	
	private Connection connectDB2(){
		Connector c = new Connector();
		return c.connectDataBase2();
	}
	
	
	private void initializeMenu(){	
		JMenuBar menuBar = new JMenuBar();
		frmPerAnal.setJMenuBar(menuBar);
		
		JMenu mnAction = new JMenu("Action");
		menuBar.add(mnAction);
		
		JMenu mnGenrateGraph = new JMenu("Genrate Graph");
		mnAction.add(mnGenrateGraph);
		
		JMenuItem mntmBarChart = new JMenuItem("Bar Chart");
		mnGenrateGraph.add(mntmBarChart);
		
		JMenuItem mntmPieChart = new JMenuItem("Pie Chart");
		mnGenrateGraph.add(mntmPieChart);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("List");
		mnGenrateGraph.add(mntmNewMenuItem);
		
		JMenuItem mntmImport = new JMenuItem("Import");
		mnAction.add(mntmImport);
		
		JMenuItem mntmExport = new JMenuItem("Export");
		mnAction.add(mntmExport);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0); //Close program
				frmPerAnal.dispose(); //Close window
				frmPerAnal.setVisible(false);
				frmPerAnal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
		
		mnAction.add(mntmExit);
		
		
		JMenu mnSetting = new JMenu("Setting");
		menuBar.add(mnSetting);
		
		JMenuItem mntmDisplay = new JMenuItem("Display");
		mnSetting.add(mntmDisplay);
		
		JMenuItem mntmLogs = new JMenuItem("Logs");
		mnSetting.add(mntmLogs);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenu mnAbout = new JMenu("About");
		mnHelp.add(mnAbout);
		
		JMenuItem mntmApplication = new JMenuItem("Application");
		mnAbout.add(mntmApplication);
		
		JMenuItem mntmGraph = new JMenuItem("Graph");
		mnAbout.add(mntmGraph);
		
		JMenuItem mntmDevelopers = new JMenuItem("Developers");
		mnAbout.add(mntmDevelopers);
		
		JMenuItem mntmTips = new JMenuItem("Tips");
		mnHelp.add(mntmTips);
		frmPerAnal.getContentPane().setLayout(null);
	}
	
	
	
	private void initializePanes() throws SQLException{
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 21, 724, 419);
		frmPerAnal.getContentPane().add(tabbedPane);
		
		initializeHomePane(tabbedPane);
		initializeOpenWindowPane(tabbedPane);
		initializeReportPane(tabbedPane);
	}
	
	
	
	public void initializeHomePane(JTabbedPane tabbedPane){
		JPanel homePanel = new JPanel();
		homePanel.setBackground(UIManager.getColor("InternalFrame.activeTitleGradient"));
		tabbedPane.addTab("Home", null, homePanel, null);
		homePanel.setLayout(null);
		
		
		homeLogo = new JLabel("");
		Image img = new ImageIcon(this.getClass().getResource("/personal.png")).getImage();
		//System.out.println(img.getHeight(homeLogo));
		homeLogo.setIcon(new ImageIcon(img));
		homeLogo.setBounds(305, 146, 128, 128);
		homePanel.add(homeLogo);
		
		start = new JButton("start");
		start.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(w2.startStop)
					return;
				else{
					
						w2.startStop = true;
						
						startlbl.setText("Monitoring started..");
						stoplbl.setText("");
						frmPerAnal.repaint();
						
					
				}
				
			
			}
		});
		
		stop = new JButton("stop");
		stop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(w2.startStop)
					w2.startStop = false;
				startlbl.setText("");
				stoplbl.setText("Monitoring stopped..");
			}
		});
		
		
		startlbl = new JLabel("Monitoring started..");
		stoplbl = new JLabel("");
		
		
		start.setBounds(300, 100, 70, 50);
		stop.setBounds(400, 100, 70, 50);
		
		
		startlbl.setBounds(300, 160, 150, 20);
		stoplbl.setBounds(400, 160, 150, 20);
		
		homePanel.add(start);
		homePanel.add(stop);
		homePanel.add(startlbl);
		homePanel.add(stoplbl);
		
	}
	
	
	
	public void initializeOpenWindowPane(JTabbedPane tabbedPane){
		JPanel openWindowPanel = new JPanel();
		openWindowPanel.setBackground(UIManager.getColor("InternalFrame.activeTitleGradient"));
		
		
		tabbedPane.addTab("Open Windows", null, openWindowPanel, null);
		openWindowPanel.setLayout(null);
		
		splitPane = new JSplitPane();
		splitPane.setBounds(10, 43, 699, 335);
		openWindowPanel.add(splitPane);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);
		
		activeWindow = new JTextArea();
		activeWindow.setEditable(false);
		activeWindow.setBackground(new Color(230, 230, 250));
		activeWindow.setForeground(Color.BLACK);
		Font myFont = new Font("Calibre", Font.BOLD, 15);
		activeWindow.setFont(myFont);
		activeWindow.setForeground(Color.BLUE);
		scrollPane.setViewportView(activeWindow);
		
		JPanel CurrentApps = new JPanel();
		CurrentApps.setBorder(UIManager.getBorder("CheckBox.border"));
		CurrentApps.setBackground(new Color(230, 230, 250));
		splitPane.setLeftComponent(CurrentApps);
		GridBagLayout gbl_CurrentApps = new GridBagLayout();
		gbl_CurrentApps.columnWidths = new int[]{89, 0};
		gbl_CurrentApps.rowHeights = new int[]{14, 14, 14, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_CurrentApps.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_CurrentApps.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		CurrentApps.setLayout(gbl_CurrentApps);
		
		// Current Apps start
		
		w1.currentApps(activeWindow, CurrentApps, gbl_CurrentApps);
		
		
			
		winRefresh1 = new JButton("");
		winRefresh1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				winRefresh1.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
		});
		winRefresh1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				w1.openWindows();
				w1.currentApps(activeWindow, CurrentApps, gbl_CurrentApps);
				
			}
		});
		//Current Apps end
		winRefresh1.setBounds(679, 5, 30, 30);
		openWindowPanel.add(winRefresh1);
		
		
		Image img2 = new ImageIcon(this.getClass().getResource("/refresh.png")).getImage();
		winRefresh1.setIcon(new ImageIcon(img2));
		
		JLabel lblCurrentApp = new JLabel("Current Apps");
		lblCurrentApp.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblCurrentApp.setBounds(10, 18, 90, 14);
		openWindowPanel.add(lblCurrentApp);
		
		lblActiveWindow = new JLabel("Active Window");
		lblActiveWindow.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblActiveWindow.setBounds(328, 18, 105, 14);
		openWindowPanel.add(lblActiveWindow);
		
		
	}

	
	
	
	public void initializeReportPane(JTabbedPane tabbedPane) throws SQLException{
		JPanel reportPanel = new JPanel();
		reportPanel.setBackground(UIManager.getColor("InternalFrame.activeTitleGradient"));
		
		reportPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		tabbedPane.addTab("Reports", null, reportPanel, null);
		tabbedPane.setEnabledAt(2, true);
		reportPanel.setLayout(null);
		
		
		tabbedPane2 = new JTabbedPane(JTabbedPane.LEFT);
		tabbedPane2.setBounds(10, 45, 699, 333);
		reportPanel.add(tabbedPane2);
		
		
		
		
		// ********** Display Chart Start*************
		ResultView resView = new ResultView();
		resView.getResult();
        resView.getGrayTime();
        resView.getTeachTime();
        resView.getResearchTime();
		
		JLayeredPane PieLayeredPane = new JLayeredPane();
		PieLayeredPane.setBackground(new Color(255, 255, 204));
		tabbedPane2.addTab("Pie Chart", null, PieLayeredPane, null);
		PieLayeredPane.setLayout(null);
		tabbedPane2.setBackgroundAt(0, new Color(255, 255, 255));
		
		piePanel = new JPanel();
		piePanel.setBounds(0, 0, 632, 328);
		PieLayeredPane.add(piePanel);
		
		pieChart = resView.generatePieChart();
		chPiePanel = new ChartPanel(pieChart);
        chPiePanel.setPreferredSize(new Dimension(610, 315));
        piePanel.add(chPiePanel);
		
		
		JLayeredPane barLayeredPane = new JLayeredPane();
		barLayeredPane.setBackground(new Color(255, 255, 204));
		barLayeredPane.setLayout(null);
		tabbedPane2.addTab("Bar Chart", null, barLayeredPane, null);
		tabbedPane2.setBackgroundAt(1, new Color(255, 255, 255));
		
		barPanel = new JPanel();
		barPanel.setBounds(0, 0, 632, 328);
		barLayeredPane.add(barPanel);

		//resView = new ResultView();
		barChart = resView.generateBarChart();
		chBarPanel = new ChartPanel(barChart);
        chBarPanel.setPreferredSize(new Dimension(615, 315));
        barPanel.add(chBarPanel);
		
        
        JLayeredPane ganttLayeredPane = new JLayeredPane();
		ganttLayeredPane.setBackground(new Color(255, 255, 204));
		tabbedPane2.addTab("Gantt Chart", null, ganttLayeredPane, null);
		tabbedPane2.setBackgroundAt(2, new Color(255, 255, 255));
		ganttLayeredPane.setLayout(null);
		
		
        ganttPanel = new JPanel();
        ganttPanel.setBounds(0, 0, 632, 328);
        ganttLayeredPane.add(ganttPanel);
        
        ganttChart = resView.generateGanttChart();
        chGanttPanel = new ChartPanel(ganttChart);
        chGanttPanel.setPreferredSize(new Dimension(615, 315));
        ganttPanel.add(chGanttPanel);
        
        
        
        
		//************ Display Chart End *****************
            
		
		
		
		
	/*	
		Canvas canvas = new Canvas();
		canvas.setBackground(new Color(230, 230, 250));
		canvas.setBounds(10, 10, 612, 308);
		ganttLayeredPane.add(canvas);
		*/
		lblDate = new JLabel("Day:");
		lblDate.setBounds(475, 17, 25, 14);
		reportPanel.add(lblDate);
		
		daySpinner = new JSpinner();
		daySpinner.setModel(new SpinnerDateModel(new Date(1479092400000L), new Date(1479092400000L), null, Calendar.DAY_OF_YEAR));
		daySpinner.setBounds(510, 14, 141, 20);
		reportPanel.add(daySpinner);
		
		winRefresh2 = new JButton("");
		winRefresh2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				resView.getResult();
		        try {
					resView.getGrayTime();
					resView.getTeachTime();
					resView.getResearchTime();
					pieChart = resView.generatePieChart();
					barChart = resView.generateBarChart();
					ganttChart = resView.generateGanttChart();
					chPiePanel.setChart(pieChart);
					chBarPanel.setChart(barChart);
					chGanttPanel.setChart(ganttChart);
					frmPerAnal.repaint();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		winRefresh2.setBounds(679, 5, 30, 30);
		reportPanel.add(winRefresh2);
		
		Image img2 = new ImageIcon(this.getClass().getResource("/refresh.png")).getImage();
		winRefresh2.setIcon(new ImageIcon(img2));
	}
}
