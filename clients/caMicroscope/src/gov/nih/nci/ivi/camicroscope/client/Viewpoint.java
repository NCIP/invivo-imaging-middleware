/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nih.nci.ivi.camicroscope.client;

/**
 *
 * @author jim
 */
class Viewpoint {
    //static File inputDir = null;

    double DEFAULT_ZOOM_LEVEL = 20;
    int currentX = 0;
    int currentY = 0;
    double zoomLevel = 1.0 / DEFAULT_ZOOM_LEVEL;

    int tileSize = 0;
    boolean remoteViewEnabled = true;
    boolean remoteViewPending = false;
    boolean annotationsListNeeded = true;
    String selectedTreeNodeFileName = null;

    boolean needOpticalZoom=false;
    double opticalZoomScale=1.0;
    int opticalX=0;
    int opticalY=0;
    int opticalTileSize=0;




    private int slideWidth = 0;
    private int slideHeight = 0;
    private int thumbnailWidth = 0;
    private int thumbnailHeight = 0;
    private int scaleFactor = 0;
    private int thumbScaleFactor = 0;
    private final Shape INITIAL_SHAPE = Shape.FREEHAND;
    private State state = State.IDLE;
    private Shape shape = INITIAL_SHAPE;
    private int currentClass = 0;

    public void Viewpoint() {
        tileSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
    }

    public void setSlideSize(int w, int h) {
        System.out.println("%%%%%%%%%%%% setSlideSize Viewpoint%%%%%%%%%%%%%%");
        slideWidth = w;
        slideHeight = h;
        if (thumbnailWidth > 0) {
            scaleFactor = slideWidth / thumbnailWidth;
        }
        System.out.println("%%%%%%%%%%###scaleFactor=" + slideWidth + "/" + thumbnailWidth + "=" + scaleFactor);
    }

    public void setThumbnailSize(int w, int h) {
        thumbnailWidth = w;
        thumbnailHeight = h;
        if (thumbnailWidth > 0) {
            scaleFactor = slideWidth / thumbnailWidth;
        }
        System.out.println("&&&scaleFactor=" + slideWidth + "/" + thumbnailWidth + "=" + scaleFactor);

        System.out.println("scaleFactor=" + scaleFactor);
    }

    /*  public void setSelectionBoxSize(int w){
    selectionBoxSize=w;
    }*/
    public int translateSlide2Thumb(int p) {
        if (scaleFactor == 0) {
            System.err.println("scaleFactor 0");
        }
        return p / scaleFactor;

    }

    public int translateThumb2Slide(int p) {
        if (scaleFactor == 0) {
            System.err.println("scaleFactor 0");
        }
        return p * scaleFactor;
    }

    public int translateScreen2Slide(int p) {
        return (int) Math.round(p / zoomLevel);
    }

    public int translateSlide2Screen(int p) {
        return (int) Math.round(p * zoomLevel);

    }

    public void setState(State s) {
        state = s;
    }

    public State getState() {
        return state;
    }

    public void setShape(Shape s) {
        shape = s;
    }

    public Shape getShape() {
        return shape;
    }

    public void setCurrentClass(int c) {
        currentClass = c;
    }

    public int getCurrentClass() {
        return currentClass;
    }
}
