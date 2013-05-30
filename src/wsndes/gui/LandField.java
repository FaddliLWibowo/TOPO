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
import java.awt.geom.QuadCurve2D;
import java.util.List;

import wsndes.gui.MainAppWindow.Link;
import wsndes.gui.MainAppWindow.LinkPresentation;
import wsndes.gui.MainAppWindow.Mote;

import javax.swing.Action;
import javax.swing.JPanel;



public class LandField extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3480220979536559883L;
	MainAppWindow main;
	LinkPresentation lp = LinkPresentation.SIMPLE;
	public boolean linkLayerOn = true;
	public boolean rangeLayerOn = true;
	public boolean connectionLayerOn = true;
	public boolean pathLayerOn = true;
	public boolean sinkOverlayLayerOn = true;
	
	/**
	 * Create the panel.
	 */
	public LandField(MainAppWindow m) {
		main = m;
	}
	
	
	void drawArrowHead(Graphics2D g2, double tipX, double tipY, double tailX, double tailY)  
    {  
		double phi = Math.toRadians(30);  
        int barb = 10;
        Color old_c = g2.getColor();
		g2.setColor(new Color(50, 50, 50));  
        double dy = tipY - tailY;  
        double dx = tipX - tailX;  
        double theta = Math.atan2(dy, dx);   
        double x, y, rho = theta + phi;  
        for(int j = 0; j < 2; j++)  
        {  
            x = tipX - barb * Math.cos(rho);  
            y = tipY - barb * Math.sin(rho);  
            g2.draw(new Line2D.Double(tipX, tipY , x, y));  
            rho = theta - phi;  
        }  
        g2.setColor(old_c); 
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
		super.paintComponents(gfx);
		refreshView(gfx);
	}
	
	public void refreshView(Graphics gfx){
		Graphics2D g =  (Graphics2D)gfx;
		Color c = g.getColor();
		g.setStroke(main.mediumEdge);
		g.setColor(main.bckgrnd);
		
		Rectangle rec = this.getVisibleRect();
		g.clearRect(rec.x , rec.y , rec.width , rec.height );
		g.fillRect(rec.x, rec.y, rec.width, rec.height);
		g.setColor(Color.BLACK);
		g.drawRect(30, 30, 530, 530);
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(main.cEdge);
		
		
		if(rangeLayerOn ){
			for(Mote m: main.motes){
				if(m != main.slcMote){
					int r = m.getRadius();
					Point p = m.getLocation();
					g.setColor(main.cReach);
					g.fillOval(p.x - r , p.y - r , 2 * r + 1, 2 * r + 1);
				}
			}
		}
		
		
		if(main.slcMote != null && rangeLayerOn){
			int r = main.slcMote.getRadius();
			Point p = main.slcMote.getLocation();
			g.setColor(main.cReachS);
			g.fillOval((int)p.getX() - r , (int)p.getY() - r , 2 * r + 1, 2 * r + 1);
		}
		
		if(connectionLayerOn ){
			for(Mote m: main.motes){
				Point p = m.getLocation();
				List<Mote> ns = m.getOutNeighbours();
				g.setColor(main.cEdge);
				g.setStroke(main.thinEdge);
				for(Mote n: ns){
					g.drawLine(p.x, p.y, n.location.x, n.location.y);
					drawArrowHead(g, n.location,p);
				}
				g.setStroke(main.mediumEdge);
			}
		}
		
		
		if(main.drawLayout){
			if(linkLayerOn){
				if(lp == LinkPresentation.SIMPLE){
					g.setColor(main.cLink);
					for(Link l: main.links){
						if(l.traffic == 0)
							continue;
						Point f = l.from.location;
						Point t = l.to.location;
						g.drawLine(f.x, f.y, t.x, t.y);
					}
				}else if(lp == LinkPresentation.TRAFFIC){
					g.setColor(Color.RED);
					for(Link l: main.links){
						if(l.traffic == 0)
							continue;
						Point s = l.from.location;
						Point d = l.to.location;
						double dx = d.x - s.x;
	            		double dy = d.y - s.y;
	            		double xm = (s.x + d.x)/2.0;
	            		double ym = (s.y + d.y)/2.0;
	            		double ortx = -(dy )/5.0;
	            		double orty = (dx )/5.0;
	            		double ctrx = xm + ortx;
	            		double ctry = ym + orty;
	            		
	            		double arrHeadX = xm + ortx * 0.5;
	            		double arrHeadY = ym + orty * 0.5;
	            		double arrTailX = s.x + ortx * 0.5;
	            		double arrTailY = s.y + orty * 0.5;
	            		
	            		double tfc = l.traffic;
	            		
	            		int adjtfc = (int)(tfc * 510);
	            		int red = 0;
	            		int green = 0;
	            		
	            		if(adjtfc < 256){
	            			green = 255;
	            			red = adjtfc;
	            		}else{
	            			red = 255;
	            			green = 255 - (adjtfc%255);
	            		}
	            		g.setColor(new Color(red, green, 0));
	            		QuadCurve2D q = new QuadCurve2D.Double();
	            		q.setCurve(s.x, s.y, ctrx, ctry, d.x, d.y);
	            		g.draw(q);
	            		drawArrowHead(g, arrHeadX, arrHeadY , arrTailX, arrTailY);
					}
				}
			}
			
			
			if(sinkOverlayLayerOn ){
				g.setStroke(main.thickEdge);
				g.setColor(main.cInterSinkPath);
				for(Mote m: main.sinks){
					if(m == main.slcMote)
						continue;
					
					for(List<Link> p : m.sinkMap.values()){
						for(Link l :p){
							Point f = l.from.location;
							Point t = l.to.location;
							g.drawLine(f.x, f.y, t.x, t.y);
						}
					}
				}
				
			}
			
			if(main.slcMote != null && !main.slcMote.isSink && main.slcMote.pathToSink != null && pathLayerOn){
				g.setStroke(main.thickEdge);
				g.setColor(main.cSinkPath);
				for(Link l :main.slcMote.pathToSink){
					Point f = l.from.location;
					Point t = l.to.location;
					g.drawLine(f.x, f.y, t.x, t.y);
				}
			}else if(main.slcMote != null && main.slcMote.isSink && pathLayerOn && sinkOverlayLayerOn){
				g.setStroke(main.thickEdge);
				g.setColor(main.cSinkSelected);
				for(List<Link> p : main.slcMote.sinkMap.values()){
					for(Link l :p){
						Point f = l.from.location;
						Point t = l.to.location;
						g.drawLine(f.x, f.y, t.x, t.y);
					}
				}
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

	
	public void setLinkMode(LinkPresentation lp) {
		this.lp = lp;
	}
}
