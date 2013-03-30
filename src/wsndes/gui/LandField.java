package wsndes.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.List;

import wsndes.gui.MainAppWindow.Link;
import wsndes.gui.MainAppWindow.Mote;

import javax.swing.Action;
import javax.swing.JPanel;



public class LandField extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3480220979536559883L;
	MainAppWindow main;
	
	/**
	 * Create the panel.
	 */
	public LandField(MainAppWindow m) {
		main = m;
	}
	
	void drawArrowHead(Graphics2D g, Point tip, Point tail)  
    {  
		double phi = Math.toRadians(30);  
        int barb = 12;
		g.setPaint(main.cText);  
        double dy = tip.y - tail.y;  
        double dx = tip.x - tail.x;  
        double theta = Math.atan2(dy, dx);    
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
		g2.setColor(main.cCross);
		g2.drawLine(xLeft, yDown, xRight, yUp);
		g2.drawLine(xLeft, yUp, xRight, yDown);
	}
	
	public void paintComponent(Graphics gfx){
		Graphics2D g =  (Graphics2D)gfx;
		Color c = g.getColor();
		g.setStroke(main.sEdge);
		g.setColor(main.bckgrnd);
		Rectangle rec = this.getVisibleRect();
		g.clearRect(rec.x , rec.y , rec.width , rec.height );
		g.fillRect(rec.x, rec.y, rec.width, rec.height);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(main.cEdge);

		for(Mote m: main.motes){
			if(m != main.slcMote){
				int r = m.getRadius();
				Point p = m.getLocation();
				g.setColor(main.cReach);
				g.fillOval(p.x - r , p.y - r , 2 * r + 1, 2 * r + 1);
			}
		}
		
		if(main.slcMote != null){
			int r = main.slcMote.getRadius();
			Point p = main.slcMote.getLocation();
			g.setColor(main.cReachS);
			g.fillOval((int)p.getX() - r , (int)p.getY() - r , 2 * r + 1, 2 * r + 1);
		}
		
		
		for(Mote m: main.motes){
			Point p = m.getLocation();
			List<Mote> ns = m.getOutNeighbours();
			for(Mote n: ns){
				g.setColor(main.cEdge);
				g.drawLine(p.x, p.y, n.location.x, n.location.y);
				drawArrowHead(g, n.location,p);
			}
		}
		
		if(main.drawLayout){
			g.setColor(main.cLink);
			for(Link l: main.links){
				if(l.traffic == 0)
					continue;
				Point f = l.from.location;
				Point t = l.to.location;
				g.drawLine(f.x, f.y, t.x, t.y);
			}
		}
		
		
		for(Mote m: main.motes){
			if(m != main.slcMote){
				Point p = m.getLocation();
				g.setColor(main.cMote);
				g.fillOval(p.x - 5, p.y - 5, 11, 11);
				if(m.getSink()){
					main.drawCross(g,p);
				}
				g.setColor(main.cText);
				g.drawString("id: " +m.getId(), p.x - 5, p.y + 20);
				g.drawString("r: " +m.getRadius(), p.x - 5, p.y + 30);
			}
		}
		
		if(main.slcMote != null){
			if(main.slcMote.getDragging()){
				g.setColor(main.cMoteSD);
			}else{
				g.setColor(main.cMoteS);
			}
			int r = main.slcMote.getRadius();
			Point p = main.slcMote.getLocation();
			g.fillOval((int)p.getX() - 5, (int)p.getY() - 5, 11, 11);
			if(main.slcMote.isSink){
				drawCross(g,p);
			}
			
			if(main.slcMote.getResize()){
				g.setColor(main.cMoteS);
				g.drawOval((int)p.getX() - r , (int)p.getY() - r  , 2 * r + 1, 2 * r + 1);
			}
			
			g.setColor(main.cText);
			g.drawString("id: " +main.slcMote.getId(), p.x - 5, p.y + 20);
			g.drawString("r: " +main.slcMote.getRadius(), p.x - 5, p.y + 30);
		}
		
		if(main.curAction == MainAppWindow.Action.SELECTION){
			float dash1[] = {10.0f};
		    BasicStroke dashed =
		        new BasicStroke(1.0f,
		                        BasicStroke.CAP_BUTT,
		                        BasicStroke.JOIN_MITER,
		                        10.0f, dash1, 0.0f);
		    Stroke oldStrk = g.getStroke();
		    g.setStroke(dashed);
		    int width = main.slcBtmRgt.x - main.slcTpLeft.x;
		    int height = main.slcBtmRgt.y - main.slcTpLeft.y;
		    g.drawRect(main.slcTpLeft.x, main.slcTpLeft.y, width, height);
		    g.setStroke(oldStrk);
		}
		
		g.setColor(c);
	}
	
	public void refreshView(){
		repaint();
	}
}
