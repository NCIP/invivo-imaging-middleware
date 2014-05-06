package gov.nih.nci.ivi.camicroscope.client;

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;

public class ThumbnailSlide extends JPanel implements
        MouseListener, MouseMotionListener {


    private int MINIMUMSELECTIONBOXSIZE = 5;
    private int X, Y;

    private Viewpoint viewpoint = null;

    protected static int maxThumbSize = 300;
    static int selectionBoxSize = 0;
	  

    private int slideWidth = 0;
    private int slideHeight = 0;
    private int thumbnailWidth = 0;
    private int thumbnailHeight = 0;
    private int scaleFactor = 0;
    private int thumbScaleFactor = 0;
	  

    private Image cleanImage = null;
    private Image image = null;
    private Image unscaledImage = null;
    private Graphics imageGraphics=null;
    private boolean imageAvailable = false;
    private Color selectionBoxColor = Color.red;

	public static void main(String[] args) {
	    JFrame frame = new JFrame();
	    frame.getContentPane().add(new ThumbnailSlide());
	}


    public ThumbnailSlide() {
	    addMouseMotionListener(this);
	    addMouseListener(this);
	    setPreferredSize(new Dimension(maxThumbSize,maxThumbSize));
	    setVisible(true);
    }

    public void setViewpoint(Viewpoint v){
        viewpoint = v;
    }
	  
	  public void setSlideSize(int w, int h){
		  slideWidth=w;
		  slideHeight=h;
		  if(thumbnailWidth>0)scaleFactor = slideWidth / thumbnailWidth;
		  System.out.println("###scaleFactor="+slideWidth+"/"+thumbnailWidth+"="+scaleFactor);
	  }
	  
	  public void setThumbnailSize(int w, int h){
		  thumbnailWidth=w;
		  thumbnailHeight=h;
		  if(thumbnailWidth>0)scaleFactor = slideWidth / thumbnailWidth;
		  System.out.println("&&&scaleFactor="+slideWidth+"/"+thumbnailWidth+"="+scaleFactor);

		  System.out.println("scaleFactor="+scaleFactor);
	  }
	  
	  public void setSelectionBoxSize(int w){
          System.out.println("SELECTION BOX SIZE = "+w);
          if(w < MINIMUMSELECTIONBOXSIZE)
              selectionBoxSize = MINIMUMSELECTIONBOXSIZE;
          else
              selectionBoxSize=w;
	  }
	  
	  public int translateSlide2Thumb(int p){
		  if(scaleFactor == 0){ System.err.println("scaleFactor 0");
            return 0;
          }
		  return p / scaleFactor;
		  
	  }
	  
	  public int translateThumb2Slide(int p){
		  if(scaleFactor == 0) System.err.println("scaleFactor 0");
		  return p * scaleFactor;
	  }
	  
	  public int getThumbX(){ 
		  if(X < 0) return 0;
		  return X; 
	  }
	  
	  public int getThumbY(){ 
		  if(Y < 0) 
			  return 0;
		  return Y; 
		 
	  }

	public void mouseEntered(MouseEvent event){}

	public void mouseExited(MouseEvent event){}

	public void mousePressed(MouseEvent event){

	}

	public void mouseClicked(MouseEvent event) {
	}

	public void mouseReleased(MouseEvent event) {
        double zoomLevel = viewpoint.zoomLevel;



	    X = (int) event.getPoint().getX() - (selectionBoxSize/2);
	    Y = (int) event.getPoint().getY() - (selectionBoxSize/2);

        viewpoint.currentX = (int) Math.round(translateThumb2Slide(getThumbX())*zoomLevel);
        viewpoint.currentY = (int) Math.round(translateThumb2Slide(getThumbY())*zoomLevel);


        if(viewpoint.needOpticalZoom){
            viewpoint.opticalX = (int) Math.round(translateThumb2Slide(getThumbX())*1.0);
            viewpoint.opticalY = (int) Math.round(translateThumb2Slide(getThumbY())*1.0);
        }

	    repaint();
	}

	public void mouseMoved(MouseEvent event) {
	    if(event.getButton() == 0){
	    	return;
	    }

      //  if(viewpoint.getState() != State.IDLE) return;

        X = (int) event.getPoint().getX() - (selectionBoxSize/2);
	    Y = (int) event.getPoint().getY() - (selectionBoxSize/2);

        repaint();
	  }
	  
	  public void mouseDragged(MouseEvent event) {
	    // mouseMoved(event);  // JDK 1.5 bug getButton() does not always work correctly on all platforms

        // if(viewpoint.getState() != State.IDLE) return;

         X = (int) event.getPoint().getX() - (selectionBoxSize/2);
         Y = (int) event.getPoint().getY() - (selectionBoxSize/2);

         repaint();
	  }

    @Override
	  public void update(Graphics graphics) {
	    paint(graphics);
	  }
	  
    @Override
	  public void paint(Graphics g) {
			System.out.println("size in paint"+getWidth()+","+getHeight()+"imageAvailable:"+imageAvailable);
		
            if(imageGraphics == null) return;
            
            if(imageAvailable){
				imageGraphics.drawImage(cleanImage, 0, 0, null);
	 
				paintOffscreen(imageGraphics);
				g.drawImage(image, 0, 0, null);
				setSize(thumbnailWidth, thumbnailHeight);
			}

	  }
	  
	  public int setImage(String filename) {
		    Dimension dim = getSize();
		    imageAvailable = false;

            if(filename == null){
                System.err.println("null filename in setImage");
                return -1;
            }
	    	System.out.println("/////setImage creating image:"+filename+dim.width+","+dim.height);
	    	ImageIcon pic = new ImageIcon(filename);
	    	unscaledImage = pic.getImage();

	    	
	    	if(pic == null){
	    		System.out.println("setimage pic is null");
	    		return -1;
	    	}
	    	
	    	if( pic.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE ){
	    		System.out.println("setImage load not complete");
	    		return -1;
			}
	    	
	    	System.out.println("COMPLETE pic load of size"+pic.getIconWidth()+","+pic.getIconHeight());
	    	
	    	if(pic.getIconWidth() > pic.getIconHeight()){
	    		System.out.println("width > height");
	    		thumbScaleFactor = pic.getIconWidth() / maxThumbSize;

	    		
	    	} else {
	    		System.out.println("height >= width");
	    		thumbScaleFactor = pic.getIconWidth() / maxThumbSize;
	    	}
	    	
	    	int mywidth=pic.getIconWidth()/thumbScaleFactor;
	    	int myheight=pic.getIconHeight()/thumbScaleFactor;
	    	
	    	System.out.println("Creating IMAGE:"+mywidth+","+myheight);
	        image = createImage(mywidth+2,myheight+2);
	        imageGraphics = image.getGraphics();

	        cleanImage = createImage(mywidth+2, myheight+2);
			setThumbnailSize(mywidth+2, myheight+2);
            viewpoint.setThumbnailSize(mywidth+2,myheight+2);
            
			setBounds(0,0,mywidth+2,myheight+2);


			Graphics graphicsCI = cleanImage.getGraphics();
            graphicsCI.setColor(Color.BLACK);
            graphicsCI.fillRect(0, 0, mywidth+2, myheight+2);
			graphicsCI.drawImage(unscaledImage, 1, 1, mywidth, myheight, null);
	    	
	    	System.out.println("unscaledImage dims:"+unscaledImage.getWidth(this)+","+unscaledImage.getHeight(this)+"thumbScalefactor"+thumbScaleFactor);
	    	
	    	return 1; 
	  }
	  
	  public void setAvailable(boolean val){
		  imageAvailable = val;
	  }

	  public void paintOffscreen(Graphics g) {
	    g.setColor(Color.red);
	    g.drawRect(X , Y, selectionBoxSize , selectionBoxSize );
	  }


      public void changeSelectionBox() {
          System.out.println("change selection box");
          selectionBoxColor = Color.green;
          setAvailable(true);
          repaint();
	  }

      public void setXY(int x, int y){
          System.out.println("setXY"+x+","+y);
          X=x;
          Y=y;
          repaint();
      }

      public void moveXY(int x, int y){
          X+=x;
          Y+=y;
          repaint();
      }
}

