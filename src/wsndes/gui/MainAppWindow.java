package wsndes.gui;


import java.awt.BasicStroke;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import java.awt.Dimension;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import java.awt.SystemColor;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.InputEvent;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;


public class MainAppWindow implements ActionListener, ItemListener, MouseListener, MouseMotionListener, ChangeListener, KeyListener{
	
	public enum LinkPresentation{
		SIMPLE,
		TRAFFIC
	}
	
	public enum Action{
		NONE,
		ADDING,
		DRAGGING,
		RESIZING,
		SELECTION
	}
	public JFrame frame;
	public LandField landfield;
	public JScrollPane scrollPane;
	public JLabel dSizeLable;
	public JSlider dfSlider;
	public int map[][] = new int[1000][1000];
	public int dfRadius;
	public ArrayList<Integer> regions[][] = (ArrayList<Integer>[][])Array.newInstance(ArrayList.class, 20, 20);
	public Action curAction = Action.NONE;
	public static int idCounter = 1;
	public boolean drawLayout = false;
	public List<Mote> motes;
	public List<Mote> sinks;
	public List<Link> links;
	public Map<Integer, Mote> moteid;
	public Mote slcMote = null;
	public Color cMote = new Color( 0, 0, 0);
	public Color cReach = new Color(100, 205, 0, 50);
	public Color cMoteS = new Color(255, 64, 64);
	public Color cMoteSD = new Color(200, 64, 64);
	public Color cText = new Color(50, 50, 50);
	public Color cEdge = new Color(80, 80, 80);
	public Color cLink = new Color(255, 0, 255);
	public Color cInterSinkPath = new Color(255, 255, 0);
	public Color cSinkSelected = new Color(0, 0, 255);
	public Color cSinkPath = new Color(255, 0, 0);
	public Color cReachS = new Color(0, 0, 255, 70);
	public Color cCross = Color.RED;
	public Color bckgrnd = new Color(255, 255, 255);
	public Stroke thinEdge = new BasicStroke(1);
	public Stroke mediumEdge = new BasicStroke(2);
	public Stroke thickEdge = new BasicStroke(3);
	
	public Point oldLoc;
	public Point slcTpLeft;
	public Point slcBtmRgt;
	
	public JLabel pointerLoc;
	public JLabel moteID;
	public JLabel moteRadius;
	public JComboBox comboBox;
	public JCheckBox chckbxCons,chckbxLinks, chckbxRanges, chckbxSinkOverlay, chckbxPaths;
	
	//Test seed is 90876875
	public final Random rgen = new Random();
	
	public final JFileChooser fc = new JFileChooser();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainAppWindow window = new MainAppWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainAppWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		for(int i = 0; i <20; i++){
			for(int j = 0; j <20; j++){
				regions[i][j] = new ArrayList<Integer>();
			}
		}
		
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		FileFilter simpleFilter = new FileNameExtensionFilter(".topo, .tpl", "*.topo", "*.tpl");
		FileFilter trafficFilter = new FileNameExtensionFilter(".tfc", "*.tfc");
		fc.addChoosableFileFilter(simpleFilter);
		fc.addChoosableFileFilter(trafficFilter);
		
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 900, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		
		
		scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(500, 400));
		scrollPane.setSize(new Dimension(500, 400));
		scrollPane.setBounds(12, 13, 670, 519);
		frame.getContentPane().add(scrollPane);
		
		//landfield = new JPanel();
		landfield = new LandField(this);
		landfield.setBackground(SystemColor.inactiveCaptionText);
		landfield.setPreferredSize(new Dimension(1000, 1000));
		landfield.setSize(new Dimension(1000, 1000));
		landfield.addMouseListener(this);
		landfield.addMouseMotionListener(this);
		frame.addKeyListener(this);
		frame.requestFocus();
		
		
		scrollPane.setViewportView(landfield);
		
		JButton btnAddNode = new JButton("");
		//btnAddNode.setIcon(new ImageIcon("C:\\Users\\alireza\\workspace\\TossimTopology\\resources\\Addnode.png"));
		btnAddNode.setIcon(new ImageIcon(getClass().getResource( "/images/Addnode.png")));
		btnAddNode.setBounds(826, 13, 40, 40);
		btnAddNode.setActionCommand("addNode");
		btnAddNode.addActionListener(this);
		frame.getContentPane().add(btnAddNode);
		
		JPanel InfoPanel = new JPanel();
		InfoPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		InfoPanel.setBounds(707, 415, 175, 117);
		frame.getContentPane().add(InfoPanel);
		InfoPanel.setLayout(null);
		
		JLabel lblPosition = new JLabel("Position:");
		lblPosition.setBounds(12, 13, 49, 16);
		InfoPanel.add(lblPosition);
		
		JLabel lblMote = new JLabel("Mote:");
		lblMote.setBounds(12, 42, 56, 16);
		InfoPanel.add(lblMote);
		
		pointerLoc = new JLabel("");
		pointerLoc.setBounds(68, 30, 95, 16);
		InfoPanel.add(pointerLoc);
		
		moteID = new JLabel("");
		moteID.setBounds(68, 59, 95, 16);
		InfoPanel.add(moteID);
		
		moteRadius = new JLabel("");
		moteRadius.setBounds(68, 88, 95, 16);
		InfoPanel.add(moteRadius);
		
		dSizeLable = new JLabel("");
		dSizeLable.setBounds(837, 279, 17, 16);
		frame.getContentPane().add(dSizeLable);
		
		dfSlider = new JSlider();
		dfSlider.setPaintLabels(true);
		dfSlider.setSnapToTicks(true);
		dfSlider.setMinimum(10);
		dfSlider.setMaximum(100);
		dfSlider.setOrientation(SwingConstants.VERTICAL);
		dfSlider.setBounds(828, 114, 35, 161);
		dfSlider.addChangeListener(this);
		frame.getContentPane().add(dfSlider);
		dfSlider.setValue(70);
		
		JLabel lblRadius = new JLabel("Radius");
		lblRadius.setBounds(826, 98, 56, 16);
		frame.getContentPane().add(lblRadius);
		
		chckbxCons = new JCheckBox("Connections");
		chckbxCons.setBounds(690, 14, 111, 25);
		chckbxCons.setSelected(true);
		chckbxCons.addItemListener(this);
		frame.getContentPane().add(chckbxCons);
		
		chckbxLinks = new JCheckBox("Links");
		chckbxLinks.setBounds(690, 44, 111, 25);
		chckbxLinks.setSelected(true);
		chckbxLinks.addItemListener(this);
		frame.getContentPane().add(chckbxLinks);
		
		comboBox = new JComboBox();
		comboBox.setBounds(694, 78, 107, 24);
		comboBox.addItem("Simple");
		comboBox.addItem("Traffics");
		comboBox.setSelectedIndex(0);
		comboBox.setActionCommand("linkPrs");
		comboBox.addActionListener(this);
		frame.getContentPane().add(comboBox);
		
		chckbxRanges = new JCheckBox("Ranges");
		chckbxRanges.setBounds(690, 114, 111, 25);
		chckbxRanges.setSelected(true);
		chckbxRanges.addItemListener(this);
		frame.getContentPane().add(chckbxRanges);
		
		chckbxSinkOverlay = new JCheckBox("Sink overlay");
		chckbxSinkOverlay.setBounds(690, 144, 111, 25);
		chckbxSinkOverlay.setSelected(true);
		chckbxSinkOverlay.addItemListener(this);
		frame.getContentPane().add(chckbxSinkOverlay);
		
		chckbxPaths = new JCheckBox("Paths");
		chckbxPaths.setBounds(690, 174, 111, 25);
		chckbxPaths.setSelected(true);
		chckbxPaths.addItemListener(this);
		frame.getContentPane().add(chckbxPaths);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenu mnTools = new JMenu("Tools");
		menuBar.add(mnTools);
		
		JMenu mnTopoGen = new JMenu("TopoGen");
		mnTools.add(mnTopoGen);
		
		JMenu mnRandom = new JMenu("Random");
		mnTopoGen.add(mnRandom);
		
		JMenuItem mntmSave = new JMenuItem("Save", KeyEvent.VK_S);
		mntmSave.setActionCommand("saveMenu");
		mntmSave.addActionListener(this);
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		mnFile.add(mntmSave);
		
		JMenuItem mntmReset = new JMenuItem("Reset", KeyEvent.VK_R);
		mntmReset.setActionCommand("resetMenu");
		mntmReset.addActionListener(this);
		mntmReset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		mnFile.add(mntmReset);
		
		JMenuItem mntmOpen = new JMenuItem("Open", KeyEvent.VK_O);
		mntmOpen.setActionCommand("openMenu");
		mntmOpen.addActionListener(this);
		mntmOpen.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		mnFile.add(mntmOpen);
		
		JMenuItem mntmGrid = new JMenuItem("Grid", KeyEvent.VK_G);
		mntmGrid.setActionCommand("gridMenu");
		mntmGrid.addActionListener(this);
		mntmGrid.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_G, ActionEvent.CTRL_MASK));
		mnTopoGen.add(mntmGrid);
		
		
		
		JMenuItem mntmNaive = new JMenuItem("Naive", KeyEvent.VK_N);
		mntmNaive.setActionCommand("naiveMenu");
		mntmNaive.addActionListener(this);
		mntmNaive.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		mnRandom.add(mntmNaive);
		
		JMenuItem mntmUniform = new JMenuItem("Uniform");
		mntmUniform.setActionCommand("trueMenu");
		mntmUniform.addActionListener(this);
		mntmUniform.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
		mnRandom.add(mntmUniform);
		
		
		
		motes = new ArrayList<Mote>();
		sinks = new ArrayList<Mote>();
		links = new ArrayList<Link>();
		moteid = new HashMap<Integer, Mote>();
	}
	
	
	void drawArrowHead(Graphics2D g, Point tip, Point tail)  
    {  
		double phi = Math.toRadians(30);  
        int barb = 15;
		g.setPaint(cText);  
        double dy = tip.y - tail.y;  
        double dx = tip.x - tail.x;  
        double theta = Math.atan2(dy, dx);  
        //System.out.println("theta = " + Math.toDegrees(theta));  
        double x, y, rho = theta + phi;  
        for(int j = 0; j < 2; j++)  
        {  
            x = tip.x - barb * Math.cos(rho);  
            y = tip.y - barb * Math.sin(rho);  
            g.draw(new Line2D.Double(tip.x, tip.y, x, y));  
            rho = theta - phi;  
        }  
    }
	
	void drawCross(Graphics2D g2, Point center){
		int xLeft = center.x - 10;
		int xRight = center.x + 10;
		int yUp = center.y - 10;
		int yDown = center.y + 10;
		g2.setColor(cCross);
		g2.drawLine(xLeft, yDown, xRight, yUp);
		g2.drawLine(xLeft, yUp, xRight, yDown);
	}

	
	private int getMoteAt(int x, int y) {
		return map[x][y];
	}
	
	private boolean ocupiedBy(int x, int y, int id){
		return map[x][y] == id;
	}

	private boolean isLocationAvailable(int x, int y) {
		boolean ret = true;
		int xs = x - 5 <0?0: x - 5;
		int xf = x+5>landfield.getWidth() - 1?landfield.getWidth() - 1:x + 4;
		int ys = y - 5 <0?0: y - 5;
		int yf = y+5>landfield.getHeight() - 1?landfield.getHeight() - 1:y + 4;
		for(int i = xs; i <= xf; i++){
			for(int j = ys; j <= yf; j++){
				ret &= (map[x][y]==0);
			}
		}
		return ret;
	}
	
	private void fillMap(int x, int y, int id){
		int xs = x - 5 <0?0: x - 5;
		int xf = x+5>landfield.getWidth() - 1?landfield.getWidth() - 1:x + 4;
		int ys = y - 5 <0?0: y - 5;
		int yf = y+5>landfield.getHeight() - 1?landfield.getHeight() - 1:y + 4;
		for(int i = xs; i <= xf; i++){
			for(int j = ys; j <= yf; j++){
				map[i][j] = id;
			}
		}
	}
	
	private void clearMap(int x, int y){
		int xs = x - 5 <0?0: x - 5;
		int xf = x+5>landfield.getWidth() - 1?landfield.getWidth() - 1:x + 4;
		int ys = y - 5 <0?0: y - 5;
		int yf = y+5>landfield.getHeight() - 1?landfield.getHeight() - 1:y + 4;
		for(int i = xs; i <= xf; i++){
			for(int j = ys; j <= yf; j++){
				map[i][j] = 0;
			}
		}
	}
	
	
	
	private void reset(){
		motes.clear();
		moteid.clear();
		sinks.clear();
		links.clear();
		for(int i = 0; i < 1000; i++){
			for(int j = 0; j < 1000; j++){
				map[i][j] = 0;
			}
		}
		for(int i = 0; i < regions.length; i++){
			for(int j = 0; j < regions[0].length; j++){
				regions[i][j].clear();
			}
		}
		slcMote = null;
		idCounter = 1;
		landfield.repaint();
	}
	
	
	private void saveTopology(BufferedWriter topo){
		try {
			topo.write("n " + motes.size()+ "\n");
			for(int i = 0; i < motes.size(); i++){
				topo.write("moteid " + motes.get(i).getId() + "\n");
			}
			
			for(int i = 0; i < motes.size(); i++){
				motes.get(i).saveOutNeighbours(topo);
			}
			
			topo.flush();
			topo.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void saveTraffic(BufferedWriter topo){
		try {
			for(Mote m : motes){
				topo.write("node " + m.getId() + " " + m.location.x + " " + m.location.y + " " + m.isSink  + " " + m.radius+ "\n");
			}
			
			for(Link l:links){
				if(l.traffic > 0.01)	
					topo.write("gain " + l.from.id + " " + l.to.id + " " + l.traffic + "\n");
			}
			
			topo.flush();
			topo.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loadTopology(BufferedReader br) {
		reset();
		String line;
		try {
			while((line = br.readLine()) != null) {
				StringTokenizer stk = new StringTokenizer(line, " ");
				if(!stk.hasMoreTokens())
					continue;
				String type = stk.nextToken();
	        	if(type.equalsIgnoreCase("node")){
	        		int id = Integer.parseInt(stk.nextToken());
	        		int x = 2*Integer.parseInt(stk.nextToken());
	        		int y = 2*Integer.parseInt(stk.nextToken());
	        		boolean s = Boolean.parseBoolean(stk.nextToken());
	        		int r = 2*Integer.parseInt(stk.nextToken());
	        		Mote m = new Mote(x, y, r, s);
	        		if(s){
	        			sinks.add(m);
	        		}
	        		
					motes.add(m);
					moteid.put(Integer.valueOf(m.getId()), m);
					fillMap(x, y, m.getId());
					m.maintainConsistency();
	        	}
			}
			setUpNetwork();
			drawLayout = true;
			landfield.repaint();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void createGrid(int row, int col) {
		reset();
		
		int crnrX = dfRadius;
		int crnrY = dfRadius;
		
		for(int i = 0; i < row; i++){
			for(int j = 0; j < col; j++){
				int curX = crnrX + i * dfRadius;
				int curY = crnrY + j * dfRadius;
				Mote m = new Mote(curX, curY, dfRadius);
				motes.add(m);
				moteid.put(Integer.valueOf(m.getId()), m);
				fillMap(curX, curY, m.getId());
				m.maintainConsistency();
			}
		}
		setUpNetwork();
		landfield.repaint();
	}
	
	private void createNaive(int num){
		reset();
		
		int initX = 500;//rgen.nextInt(1000);
		int initY = 500;//rgen.nextInt(1000);
		
		int rds = getRanRadius();
		
		Mote m = new Mote(initX, initY, rds);
		motes.add(m);
		moteid.put(Integer.valueOf(m.getId()), m);
		fillMap(initX, initY, m.getId());
		m.maintainConsistency();
		
		int numOfMotes = 1;
		
		while(numOfMotes < num){
			int baseIndex = rgen.nextInt(motes.size());
			Mote baseMote =motes.get(baseIndex);
			Point p = getNewNeighbour(baseMote);
			if(pointAcceptable(p, baseMote)){
				rds = getRanRadius();
				m = new Mote(p.x, p.y, rds);
				motes.add(m);
				moteid.put(Integer.valueOf(m.getId()), m);
				fillMap(p.x, p.y, m.getId());
				m.maintainConsistency();
				numOfMotes++;
			}
		}
		setUpNetwork();
		landfield.repaint();
	} 
	
	private void createTrueUniformRandom(int width, int height, int num){
		reset();
		int numOfMotes = 1;
		int xoff = 30;
		int yoff = 30;
		
		while(numOfMotes <= num){
			int x = rgen.nextInt(width) + xoff;
			int y = rgen.nextInt(height) + yoff;
			if(isLocationAvailable(x, y)){
				Mote m = new Mote(x, y, dfRadius);
				motes.add(m);
				moteid.put(Integer.valueOf(m.getId()), m);
				fillMap(x, y, m.getId());
				m.maintainConsistency();
				numOfMotes++;
			}
		}
		setUpNetwork();
		landfield.repaint();
	}
	
	private boolean pointAcceptable(Point p, Mote m){
		if(p.equals(m.getLocation()))
			return false;
		
		if(p.x >= 1000 || p.x < 0 || p.y >= 1000 || p.y < 0)
			return false;
		
		return true;
	}
	
	private Point getNewNeighbour(Mote m){
		Point bP = m.getLocation();
		int brds = m.getRadius();
		
		int rnd1 = rgen.nextInt(2000);
		double angle = ((rnd1 * 1.0)/2000.0)* 2 * Math.PI;
		
		int rnd2 = rgen.nextInt(2000);
		int rds = (int)(((rnd2 * 1.0)/2000.0)* brds);
		
		int nx = (int)(rds * Math.cos(angle));
		int ny = (int)(rds * Math.sin(angle));
		
		return new Point(nx + bP.x, ny + bP.y);
	}
	
	private int getRanRadius(){
		double r = rgen.nextGaussian();
		r = r * 0.1 * dfRadius;
		
		r += dfRadius;
		
		return (int)r;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if(action.equalsIgnoreCase("addNode")){
			curAction = Action.ADDING;
		}else if(action.equalsIgnoreCase("openMenu")){
			int returnVal = fc.showOpenDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File topoFile = fc.getSelectedFile();
				try {
					BufferedReader br = new BufferedReader(new FileReader(topoFile));
					loadTopology(br);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		}else if(action.equalsIgnoreCase("saveMenu")){
			int returnVal = fc.showSaveDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File topoFile = fc.getSelectedFile();
	            if(topoFile.getName().length()== 0){
	            	JOptionPane.showMessageDialog(frame, "You must choose a name for your file.");
	            	return;
	            }
	            //This is where a real application would open the file.
	            
	            try {
					
					FileFilter ff = fc.getFileFilter();
					if(ff.getDescription().contains("tpl")){
						File f = new File(topoFile.getAbsolutePath() + ".tpl");
						BufferedWriter outFile = new BufferedWriter( new FileWriter( f ) );
						saveTopology(outFile);
					}else if((ff.getDescription().contains("tfc"))){
						File f = new File(topoFile.getAbsolutePath() + ".tfc");
						BufferedWriter outFile = new BufferedWriter( new FileWriter( f ) );
						saveTraffic(outFile);
					}
					
					return;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        } 
		}else if(action.equalsIgnoreCase("gridMenu")){
			GridOptionsDiag customDialog = new GridOptionsDiag(frame, "geisel");
	        customDialog.pack();
	        customDialog.setVisible(true);
	        
	        if(customDialog.isValid()){
	        	int row = customDialog.getRow();
	        	int col = customDialog.getCol();
	        	createGrid(row, col);
	        }
		}else if(action.equalsIgnoreCase("naiveMenu")){
			NaiveOptionsDiag customDialog = new NaiveOptionsDiag(frame, "geisel");
			customDialog.pack();
	        customDialog.setVisible(true);
	        
	        if(customDialog.isValid()){
	        	int num = customDialog.getNum();
	        	createNaive(num);
	        }
		}else if(action.equalsIgnoreCase("trueMenu")){
			TrueUniformRandomDiag customDialog = new TrueUniformRandomDiag(frame, "geisel");
			customDialog.pack();
	        customDialog.setVisible(true);
	        
	        if(customDialog.isValid()){
	        	int w = customDialog.getRegionWidth();
	        	int h = customDialog.getRegionHeight();
	        	int num = customDialog.getNum();
	        	createTrueUniformRandom(w, h, num);
	        }
		}else if(action.equalsIgnoreCase("resetMenu")){
			reset();
		}else if(action.equalsIgnoreCase("linkPrs")){
			switch(comboBox.getSelectedIndex()){
			case 0:
				landfield.setLinkMode(LinkPresentation.SIMPLE);
				break;
			case 1:
				landfield.setLinkMode(LinkPresentation.TRAFFIC);
				break;
			
			}
			landfield.repaint();
		}
		
	}
	
	

	@Override
	public void mouseClicked(MouseEvent e) {
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		pointerLoc.setText("");
		moteID.setText("");
		moteRadius.setText("");
	}

	@Override
	public void mousePressed(MouseEvent e) {
		drawLayout = false;
		frame.requestFocus();
		int x = e.getX();
		int y = e.getY();
		switch(curAction){
		case NONE:
			if(slcMote!=null){
				if(slcMote.getDragging()){
					curAction = Action.DRAGGING;
					oldLoc = new Point(slcMote.getLocation());
					clearMap(oldLoc.x, oldLoc.y);
					break;
				}else if(slcMote.getResize()){
					curAction = Action.RESIZING;
					break;
				}
				
			}
			int id = getMoteAt(x, y);
			if(id>0){
				Mote m = moteid.get(Integer.valueOf(id));
				m.setSelect(true);
				if(slcMote != null)
					slcMote.setSelect(false);
				slcMote = m;
				curAction = Action.DRAGGING;
			}else{
				if(slcMote != null)
					slcMote.setSelect(false);
				slcMote = null;
				curAction = Action.SELECTION;
				slcTpLeft = new Point(x, y);
				slcBtmRgt = new Point(x, y);
			}
			break;
		case ADDING:
			if(isLocationAvailable(x, y)){
				Mote m = new Mote(x, y, dfRadius);
				motes.add(m);
				moteid.put(Integer.valueOf(m.getId()), m);
				fillMap(x, y, m.getId());
				curAction = Action.NONE;
				if(slcMote != null)
					slcMote.setSelect(false);
				slcMote = m;
				slcMote.maintainConsistency();
				curAction = Action.DRAGGING;
			}else{
				JOptionPane.showMessageDialog(frame,
					    "This location is already occupied",
					    "Error",
					    JOptionPane.WARNING_MESSAGE);
			}
			break;
		}
		landfield.repaint();
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		switch(curAction){
		case DRAGGING:
			if(isLocationAvailable(x, y) || ocupiedBy(x, y,slcMote.getId())){
				slcMote.setLocation(x, y);
				fillMap(x, y, slcMote.getId());
				slcMote.maintainConsistency();
			}else{
				if(oldLoc != null){
					slcMote.setLocation(oldLoc.x, oldLoc.y);
					fillMap(oldLoc.x, oldLoc.y, slcMote.getId());
				}
				slcMote.maintainConsistency();
				JOptionPane.showMessageDialog(frame,
					    "This location is already occupied",
					    "Error",
					    JOptionPane.WARNING_MESSAGE);
			}
			break;
		}
		oldLoc =null;
		slcTpLeft = null;
		slcBtmRgt = null;
		curAction = Action.NONE;
		drawLayout = true;
		setUpNetwork();
		landfield.repaint();
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		pointerLoc.setText("(" +x+ ", " +y+")");
		int cell = map[x][y];
		if(cell>0){
			moteID.setText("ID= " + cell);
			moteRadius.setText("Radius= " + moteid.get(cell).radius);
		}		else{
			moteID.setText("");
			moteRadius.setText("");
		}
		if(slcMote != null){
			int r = slcMote.getRadius();
			int d = (int)slcMote.getLocation().distance(e.getX(), e.getY());
			boolean redraw = false;
			if(d < 5){
				if(!slcMote.getDragging()){
					slcMote.setDragging(true);
					redraw = true;
				}
			}else if(Math.abs(r - d) <=2){
				if(slcMote.getDragging()){
					slcMote.setDragging(false);
					redraw = true;
				}
				
				if(!slcMote.getResize()){
					slcMote.setResize(true);
					redraw = true;
				}
			}else{
				if(slcMote.getDragging()){
					slcMote.setDragging(false);
					redraw = true;
				}
				
				if(slcMote.getResize()){
					slcMote.setResize(false);
					redraw = true;
				}
			}
			if(redraw)
				landfield.repaint();
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		switch(curAction){
		case DRAGGING:
			slcMote.setLocation(x, y);
			slcMote.maintainConsistency();
			landfield.repaint();
			break;
		case RESIZING:
			int r = (int)slcMote.getLocation().distance(x, y);
			if(r > 8){
				slcMote.setRadius(r);
				slcMote.maintainConsistency();
				landfield.repaint();
			}
			break;
		case SELECTION:
			slcBtmRgt.x = x;
			slcBtmRgt.y = y;
			landfield.repaint();
			break;
		}
		
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if(e.getSource() == dfSlider){
			dfRadius = dfSlider.getValue();
			dSizeLable.setText("" + dfRadius);
		}
		
	}
	

	@Override
	public void keyPressed(KeyEvent e) {
		
		if(e.getKeyChar() == 'x'){
			if(slcMote != null){
				if(slcMote.isSink){
					slcMote.setSink(false);
					sinks.remove(slcMote);
				}else{
					slcMote.setSink(true);
					sinks.add(slcMote);
				}
				setUpNetwork();
			}
			
		}else if(e.getKeyCode() ==KeyEvent.VK_DELETE){
			if(slcMote != null){
				Integer id = new Integer(slcMote.id);
				motes.remove(slcMote);
				sinks.remove(slcMote);
				moteid.remove(id);
				for(Mote in:slcMote.inNeighbours){
					in.outNeighbours.remove(slcMote);
				}
				for(Mote in:slcMote.outNeighbours){
					in.inNeighbours.remove(slcMote);
				}
				clearMap(slcMote.location.x,slcMote.location.y);
				for(int i = 0; i < regions.length; i++){
					for(int j = 0; j < regions[0].length; j++){
						
						regions[i][j].remove(id);
					}
				}
				computeTrafics();
				slcMote = null;
			}
		}
		landfield.repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getSource();
		if(source == chckbxCons){
			landfield.connectionLayerOn = chckbxCons.isSelected();
		}else if(source == chckbxLinks){
			landfield.linkLayerOn = chckbxLinks.isSelected();
			comboBox.setEnabled(chckbxLinks.isSelected());
		}else if(source ==  chckbxRanges){
			landfield.rangeLayerOn = chckbxRanges.isSelected();
		}else if(source ==  chckbxSinkOverlay){
			landfield.sinkOverlayLayerOn = chckbxSinkOverlay.isSelected();
		}else if(source ==  chckbxPaths){
			landfield.pathLayerOn = chckbxPaths.isSelected();
		}
		
		landfield.repaint();
	}
	
	public void setUpNetwork(){
		discardLinks();
		createLinks();
		computeTrafics();
	}
	
	public void computeTrafics(){
		for(Mote m: motes){
			m.pathToSink = null;
			m.sinkMap.clear();
			if(!m.isSink){
				Map<Mote, Mote> tree =  spanRouteTree(m);
				List<Mote> pathToClosestSink = null;
				for(Mote s:sinks){
					List<Mote> pathToSink = getShortestPath(m, s, tree);
					if(pathToSink.size() < 2)
						continue;
					
					if(pathToClosestSink == null || pathToSink.size() < pathToClosestSink.size()){
						pathToClosestSink = pathToSink;
					}
				}
				if(pathToClosestSink != null)
					addFlow(pathToClosestSink, 0.05, true);
			}else{
				Map<Mote, Mote> tree =  spanRouteTree(m);
				for(Mote s:sinks){
					List<Mote> pathToAnotherSink = getShortestPath(m, s, tree);
					if(pathToAnotherSink.size() < 2)
						continue;
					addFlow(pathToAnotherSink, 0.0, true);
				}
			}
		}
		
		for(Mote s:sinks){
			s.imposeInterSinkTraffic();
		}
		
	}
	
	
	
	public Map<Mote, Mote> spanRouteTree(Mote s){
		Map<Mote, Mote> tree = new HashMap<Mote, Mote>();
		List<Mote> visited = new ArrayList<Mote>();
		List<Mote> discovered = new ArrayList<Mote>();
		discovered.add(s);
		visited.add(s);
		
		do{
			Mote cur = discovered.remove(0);
			for(Mote n:cur.outNeighbours){
				if(!visited.contains(n)){
					tree.put(n, cur);
					visited.add(n);
					discovered.add(n);
				}
			}
		}while(!discovered.isEmpty());
		return tree;
	}
	
	public List<Mote> getShortestPath(Mote s, Mote d, Map<Mote, Mote> tree){
		List<Mote> chain  = new ArrayList<Mote>();
		chain.add(d);
		Mote curMote = d;
		do{
			curMote = tree.get(curMote);
			if(curMote == null)
				break;
			chain.add(curMote);
		}while(!curMote.equals(s));
		return chain;
	}
	
	public void createLinks(){
		for(Mote m:motes){
			for(Mote n:m.outNeighbours){
				links.add(new Link(m,n));
			}
		}
	}
	
	public void discardLinks(){
		links.clear();
	}
	
	public void addFlow(List<Mote> chain, double tfc,  boolean register){
		List<Link> path = null;
		
		if(register){
			path = new ArrayList<Link>();
		}
		Mote source = null;
		for(int i = 0; i < chain.size() - 1; i++){
			Mote t = chain.get(i);
			Mote f = chain.get(i + 1);
			for(Link l : links){
				if(l.from.equals(f) && l.to.equals(t)){
					l.addTraffic(tfc);
					if(register){
						path.add(l);
					}
				}
			}
			source = f;
			
		}
		
		if(register){
			source.registerSinkPath(path, chain.get(0));
		}
	}
	
	public class Link{
		public Mote from;
		public Mote to;
		public double traffic;
		
		public Link(Mote f, Mote t){
			from = f;
			to = t;
		}
		
		public void addTraffic(double t){
			traffic = Math.min(1.0, traffic + t);
		}
		
		@Override
		public boolean equals(Object obj){
			if(!(obj instanceof Link))
				return false;
			Link l = (Link)obj;
			if(!l.from.equals(this.from))
				return false;
			if(!l.to.equals(this.to))
				return false;
			
			return true;
		}
	}
	
	public class Mote{
		int radius, id;
		Point location;
		List<Point> rgns;
		List<Link> pathToSink;
		Map<Mote,List<Link>> sinkMap;
		boolean isSelected = false;
		boolean isResizing = false;
		boolean isDragging = false;
		List<Mote> outNeighbours;
		List<Mote> inNeighbours;
		Integer idObj;
		boolean isSink = false;
		public Mote(int x,int y, int r){
			id = idCounter++;
			idObj = new Integer(id);
			rgns = new ArrayList<Point>();
			outNeighbours = new ArrayList<Mote>();
			inNeighbours = new ArrayList<Mote>();
			location = new Point(x, y);
			radius = r;
			sinkMap = new HashMap<Mote,List<Link>>();
		}

		

		public void imposeInterSinkTraffic() {
				if(!isSink)
					return;
				
				double traffic = 0.0;
				for(Link l:links){
					if(l.to.equals(this)){
						traffic += l.traffic;
					}
				}
				
				for(List<Link> p:sinkMap.values()){
					for(Link l:p){
						l.addTraffic(traffic);
					}
				}
		}



		public Mote(int x,int y){
			id = idCounter++;
			idObj = new Integer(id);
			rgns = new ArrayList<Point>();
			outNeighbours = new ArrayList<Mote>();
			inNeighbours = new ArrayList<Mote>();
			location = new Point(x, y);
			radius = 50;
			sinkMap = new HashMap<Mote,List<Link>>();
		}
		
		public Mote(int x, int y, int r, boolean s) {
			isSink = s;
			id = idCounter++;
			idObj = new Integer(id);
			rgns = new ArrayList<Point>();
			outNeighbours = new ArrayList<Mote>();
			inNeighbours = new ArrayList<Mote>();
			location = new Point(x, y);
			radius = r;
			sinkMap = new HashMap<Mote,List<Link>>();
		}



		public void saveOutNeighbours(BufferedWriter topo) {
			try {
				for(int i = 0; i < outNeighbours.size(); i++){
					Mote onb = outNeighbours.get(i);
					int dist = (int)location.distance(onb.location.x, onb.location.y);
					topo.write("gain " + this.getId() + " " + onb.getId() + " -" + dist + "\n");
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void setDragging(boolean b) {
			isDragging = b;
		}
		
		public boolean getDragging(){
			return isDragging;
		}
		
		public void setResize(boolean b) {
			isResizing = b;
		}
		
		public boolean getResize(){
			return isResizing;
		}
		
		public int getRadius(){
			return radius;
		}
		
		public void setRadius(int r){
			radius = r;
		}
		
		public void setLocation(int x,int y){
			location.setLocation(x, y);
		}
		
		
		public Point getLocation(){
			return location;
		}
		
		public void setSelect(boolean s){
			isSelected = s;
		}
		
		public boolean getSelect(){
			return isSelected;
		}
		
		public boolean getSink(){
			return isSink;
		}
		
		public void setSink(boolean s){
			isSink = s;
/*			if(isSink){
				pathToSink = null;
			}else{
				sinkMap.clear();
			}*/
		}
		
		public int getId(){
			return id;
		}
		
		@Override
		public boolean equals(Object other){
			if(!(other instanceof Mote)){
				return false;
			}
			if(((Mote)other).id != id){
				return false;
			}
			return true;
		}
		
		void removeOutNeighbour(Mote n){
			outNeighbours.remove(n);
		}
		
		void addOutNeighbour(Mote n){
			outNeighbours.add(n);
		}
		
		void removeInNeighbour(Mote n){
			inNeighbours.remove(n);
		}
		
		void addInNeighbour(Mote n){
			inNeighbours.add(n);
		}
		
		List<Mote> getOutNeighbours(){
			return outNeighbours;
		}
		
		void updateRegions(){
			Rectangle reg = new Rectangle(0,0, 50, 50);
			Rectangle bounds = new Rectangle(location.x,location.y, 2 * radius + 1, 2 * radius + 1);
			Iterator<Point> it = rgns.iterator();
			while(it.hasNext()){
				Point rCorner = it.next();
				reg.x = rCorner.x * 50;
				reg.y = rCorner.y * 50;
				if(!reg.intersects(bounds)){
					regions[rCorner.x][rCorner.y].remove(idObj);
					it.remove();
				}
			}
			
			int xs = (location.x - radius) < 0 ? 0:(location.x - radius)/50;
			int xf = (location.x + radius) > landfield.getWidth() ? 19:(location.x + radius)/50;
			int ys = (location.y - radius) < 0 ? 0:(location.y - radius)/50;
			int yf = (location.y + radius) > landfield.getHeight() ? 19:(location.y + radius)/50;
			
			Point rCorner = new Point(0, 0);
			
			for(int i = xs; i <= xf; i++){
				for(int j = ys; j <= yf; j++){
					rCorner.x =  i;
					rCorner.y = j;
					if(!rgns.contains(rCorner)){
						rgns.add(new Point(rCorner));
						regions[i][j].add(idObj);
					}
				}
			}
			
		}
		
		List<Mote> getPossibleInNeighbourse(){
			List<Mote> pnMotes = new ArrayList<Mote>();
			for(Integer i: regions[location.x/50][location.y/50]){
				pnMotes.add(moteid.get(i));
			}
			return pnMotes;
		}
		
		List<Mote> getPossibleOutNeighbourse(){
			List<Integer> pnTags = new ArrayList<Integer>();
			List<Mote> pnMotes = new ArrayList<Mote>();
			int xs = location.x - radius <0?0: location.x - radius;
			int xf = location.x + radius>landfield.getWidth() - 1?landfield.getWidth() - 1:location.x + radius - 1;
			int ys = location.y - radius <0?0: location.y - radius;
			int yf = location.y + radius>landfield.getHeight() - 1?landfield.getHeight() - 1:location.y + radius - 1;
			for(int i = xs; i <= xf; i++){
				for(int j = ys; j <= yf; j++){
					if(map[i][j] != id && map[i][j] != 0 && !pnTags.contains(Integer.valueOf(map[i][j]))){
						pnTags.add(Integer.valueOf(map[i][j]));
						pnMotes.add(moteid.get(Integer.valueOf(map[i][j])));
					}
				}
			}
			return pnMotes;
		}
		
		void maintainConsistency(){
			updateRegions();
			
			Iterator<Mote> ito = outNeighbours.iterator();
			
			while(ito.hasNext()){
				Mote onb = ito.next();
				int dist = (int)location.distance(onb.location.x, onb.location.y);
				if(dist > radius){
					ito.remove();
					onb.removeInNeighbour(this);
				}
			}
			
			List<Mote> pons = getPossibleOutNeighbourse();
			for(Mote pn: pons){
				int dist = (int)location.distance(pn.location.x, pn.location.y);
				if(dist <= radius && !outNeighbours.contains(pn)){
					addOutNeighbour(pn);
					pn.addInNeighbour(this);
				}
			}
			
			Iterator<Mote> iti = inNeighbours.iterator();
			while(iti.hasNext()){
				Mote inb = iti.next();
				if(!inb.checkout(this)){
					iti.remove();
				}
			}
			
			List<Mote> pins = getPossibleInNeighbourse();
			for(Mote pn: pins){
				if(pn.id == id)
					continue;
				if(pn.checkout(this) && !inNeighbours.contains(pn)){
					addInNeighbour(pn);
				}
			}
		}
		
		public void registerSinkPath(List<Link> path, Mote sink){
			if(isSink){
				sinkMap.put(sink, path);
			}else{
				pathToSink = path;
			}
		}
		
		private boolean checkout(Mote mote) {
			if(radius < location.distance(mote.location.x, mote.location.y)){
				removeOutNeighbour(mote);
				return false;
			}else if(!outNeighbours.contains(mote)){
				addOutNeighbour(mote);
			}
			return true;
		}

		@Override
		public String toString(){
			return "[" +id + ", " + radius+ ", (" + location.x+", " +location.y+ ")]";
		}
	}

	
}
