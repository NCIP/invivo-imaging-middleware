/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.nih.nci.ivi.camicroscope.client;


import edu.northwestern.radiology.aim.AnnotationUser;
import edu.northwestern.radiology.aim.Ellipse;
import edu.northwestern.radiology.aim.GeometricShape;
import edu.northwestern.radiology.aim.GeometricShapeImageAnnotation;
import edu.northwestern.radiology.aim.GeometricShapeSpatialCoordinateCollection;
import edu.northwestern.radiology.aim.ImageAnnotation;
import edu.northwestern.radiology.aim.ImageAnnotationGeometricShapeCollection;
import edu.northwestern.radiology.aim.ImageAnnotationTextAnnotationCollection;
import edu.northwestern.radiology.aim.Polyline;
import edu.northwestern.radiology.aim.SpatialCoordinate;
import edu.northwestern.radiology.aim.TextAnnotation;
import edu.northwestern.radiology.aim.TwoDimensionSpatialCoordinate;
import edu.northwestern.radiology.aim.User;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.xml.namespace.QName;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;

/**
 *
 * @author jim
 */
public class PaintPanel extends JLabel {

    private Viewpoint viewpoint = null;

    private int MAXSHAPES = 1000;
    private int MAXPOLYLINELINES = 5000;

    private int SHAPEWIDTH = 3;

    private Point start = null; // Where mouse is pressed.
    private Point end   = null; // Where mouse is dragged to or released.

    String[] annotationClass = { "class 0", "class 1", "class 2", "class 3"};
    Color[] annotationClassColors = { Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
    BasicStroke stroke = null;

    int numSavedShapes = 0;

    ImageAnnotation annotation = new ImageAnnotation();
    AnnotationUser user = new AnnotationUser();
    User u = new User();
    ImageAnnotationGeometricShapeCollection geometricCol = new ImageAnnotationGeometricShapeCollection();
    GeometricShape geometricShapes[] = new GeometricShape[MAXSHAPES];
    ImageAnnotation loadedAnnotation = null;

    TwoDimensionSpatialCoordinate tmpPolyLine[] = new TwoDimensionSpatialCoordinate[MAXPOLYLINELINES];
    int tmpPolyLineCount = 0;


    int numLoadedShapes = 0;
    java.awt.Shape[] boundingRect = new java.awt.Shape[MAXSHAPES];



    private BufferedImage bufImage = null;


    public PaintPanel() {
        MListener ML = new MListener();
        this.addMouseListener( ML);
        this.addMouseMotionListener(ML);
        stroke = new BasicStroke(SHAPEWIDTH);

        geometricCol.setGeometricShape(geometricShapes);
        annotation.setGeometricShapeCollection(geometricCol);

    }

    public void setProperties(Properties p){
        annotationClassColors = new Color[10];
        annotationClass = new String[10];

        for(int x=0; x<10; x++){
            int red = Integer.parseInt(p.getProperty("annotation.rcolor."+x));
            int green = Integer.parseInt(p.getProperty("annotation.gcolor."+x));
            int blue = Integer.parseInt(p.getProperty("annotation.bcolor."+x));
            System.out.println("RGB"+red+" "+green+" "+blue);

            annotationClassColors[x] = new Color(red,green,blue );

            annotationClass[x] = p.getProperty("annotation.class."+x);
        }


    }

    public void storeProperties(Properties p){

        try {

            p.store(new FileOutputStream("newoutput.properties"), "properties");

        } catch (Exception ex) {
            Logger.getLogger(PaintPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setViewpoint(Viewpoint v){
        viewpoint = v;
    }

    public void setState(State s){
        viewpoint.setState(s);
    }


    public void setShape(Shape s) {
        viewpoint.setShape(s);
    }

    public void setClass(int c) {
        viewpoint.setCurrentClass(c);
    }

    public void setStart(Point s) {
        start = s;
    }

    public void setEnd(Point s) {
        end = s;
    }

    public void setColor(Color c){

    }

    @Override public void paintComponent(Graphics g) {

        try {
          //  System.out.println("*********paintComponent, size:=" + this.getWidth() + "," + this.getHeight());
            Graphics2D g2 = (Graphics2D) g;  // Downcast to Graphics2D

            if (bufImage == null) {
                //... This is the first time, initialize
                int w = this.getWidth();
                int h = this.getHeight();
                bufImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

                Graphics2D gc = bufImage.createGraphics();
            }

            //Display the saved image.

            g2.drawImage(bufImage, null, 0, 0);

            drawSavedShapess(g2);
            drawLoadedShapes(g2);

            //Overwrite the screen display with currently dragged image.
            if (viewpoint.getState() == State.DRAGGING) {
                drawCurrentShape(g2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // for testing only
    private void drawSelection(Graphics2D g2) {
        g2.setColor(Color.YELLOW);
        g2.drawRect(start.x,start.y,end.x-start.x,end.y-start.y);

    }

    // for testing only
    private void drawBoundingBoxes(Graphics2D g2) {
        g2.setColor(Color.BLUE);
        if (boundingRect == null) return;

        System.out.println("bounding rects");

                for (int l = 0; l < numSavedShapes; l++) {


                    System.out.println("drawing bounding rect"+boundingRect[l].getBounds().x+","+
                            boundingRect[l].getBounds().y+","+
                            boundingRect[l].getBounds().width+","+
                            boundingRect[l].getBounds().height);

                    g2.drawRect(
                            viewpoint.translateSlide2Screen(
                            boundingRect[l].getBounds().x-viewpoint.translateScreen2Slide(viewpoint.currentX)),
                                                        viewpoint.translateSlide2Screen(
                            boundingRect[l].getBounds().y-viewpoint.translateScreen2Slide(viewpoint.currentY)),
                                                        viewpoint.translateSlide2Screen(
                            boundingRect[l].getBounds().width),
                                                        viewpoint.translateSlide2Screen(
                            boundingRect[l].getBounds().height));



                }



    }

    private void drawCurrentShape(Graphics2D g2) {        
        g2.setColor(annotationClassColors[viewpoint.getCurrentClass()]);    // Set the color
        g2.setStroke(stroke);
        if(end == null ) return;

        switch (viewpoint.getShape()) {
            case OVAL:
                g2.drawOval(start.x, start.y, end.x - start.x, end.y - start.y);

                break;

            case RECTANGLE:
                g2.drawRect(start.x, start.y, end.x - start.x, end.y - start.y);
                break;

            case FREEHAND:
                System.out.println("len" + tmpPolyLineCount);
                for (int l = 1; l < tmpPolyLineCount; l++) {

                    g2.drawLine(viewpoint.translateSlide2Screen((int) tmpPolyLine[l - 1].getX() - viewpoint.translateScreen2Slide(viewpoint.currentX)),
                            viewpoint.translateSlide2Screen((int) (tmpPolyLine[l - 1].getY() - viewpoint.translateScreen2Slide(viewpoint.currentY))),
                            viewpoint.translateSlide2Screen((int) (tmpPolyLine[l].getX() - viewpoint.translateScreen2Slide(viewpoint.currentX))),
                            viewpoint.translateSlide2Screen((int) (tmpPolyLine[l].getY() - viewpoint.translateScreen2Slide(viewpoint.currentY))));
                }

                break;

            case LINE:
                System.out.println("drawing line");
                g2.drawLine(start.x, start.y, end.x  , end.y);
                break;

            default:  // Should never happen!
                return;
        }

    }



    public String getAnnotationXml(String name, String author1, String txtStr1){
        
        if(annotation == null) return null;


        deleteEmptyShapes();

        int numberAnnotations = numSavedShapes;
        long currentTime = System.currentTimeMillis();
        String recordName = currentTime +"";
        String author = author1;
        u.setLoginName(author);
        user.setUser(u);

        annotation.setUser(user);
        annotation.setId(new BigInteger(recordName));
        annotation.setName(name);

        if(txtStr1.length() > 0){
            ImageAnnotationTextAnnotationCollection tac = new ImageAnnotationTextAnnotationCollection();

            TextAnnotation ta[] = new TextAnnotation[1];
            ta[0] = new TextAnnotation();
            ta[0].setText(txtStr1);


            if (tac == null) {
                System.err.println("NULL textannotationcollection");
            }

            if (ta == null) {
                System.err.println("NULL textannotation");
            }

            tac.setTextAnnotation(ta);
            annotation.setTextAnnotationCollection(tac);
        }



        try {
            return ObjectSerializer.toString(annotation, new QName("gme://caCORE.caCORE/3.2/edu.northwestern.radiology.AIM", "ImageAnnotation"));

        } catch (SerializationException ex) {
            Logger.getLogger(PaintPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private void drawSavedShapess(Graphics2D g2) {

            System.out.println("drawSaveShapess"+numSavedShapes);

           int x1, x2, y1, y2;
           ImageAnnotationGeometricShapeCollection gsc;
           GeometricShape[] gs;
           TwoDimensionSpatialCoordinate c1, c2;

        int oldClass = viewpoint.getCurrentClass();

        for(int x=0; x< numSavedShapes; x++){

            gsc = annotation.getGeometricShapeCollection();
            gs = gsc.getGeometricShape();

                if (gs[x] != null) {
                    g2.setColor(
                            //annotationClassColors[savedShapeClass[x]]);
                            annotationClassColors[Integer.parseInt(gs[x].getImageAnnotation().getImageAnnotation().getName())]);

                    g2.setStroke(stroke);

                    switch (getShapeFromString(gs[x].getClass().getSimpleName())) {
                        //savedShape[x]) {
                        case OVAL:

                            c1 = (TwoDimensionSpatialCoordinate) gs[x].getSpatialCoordinateCollection().getSpatialCoordinate(0);
                            c2 = (TwoDimensionSpatialCoordinate) gs[x].getSpatialCoordinateCollection().getSpatialCoordinate(1);

                            x1 = (int) c1.getX();
                            y1 = (int) c1.getY();
                            x2 = (int) c2.getX();
                            y2 = (int) c2.getY();



                            g2.drawOval(viewpoint.translateSlide2Screen(x1 - viewpoint.translateScreen2Slide(viewpoint.currentX)),
                                    viewpoint.translateSlide2Screen(y1 - viewpoint.translateScreen2Slide(viewpoint.currentY)),
                                    viewpoint.translateSlide2Screen(x2 - x1), viewpoint.translateSlide2Screen(y2 - y1));


                            break;

                        case RECTANGLE:

                            c1 = (TwoDimensionSpatialCoordinate) gs[x].getSpatialCoordinateCollection().getSpatialCoordinate(0);
                            c2 = (TwoDimensionSpatialCoordinate) gs[x].getSpatialCoordinateCollection().getSpatialCoordinate(1);

                            x1 = (int) c1.getX();
                            y1 = (int) c1.getY();
                            x2 = (int) c2.getX();
                            y2 = (int) c2.getY();

                            g2.drawRect(viewpoint.translateSlide2Screen(x1 - viewpoint.translateScreen2Slide(viewpoint.currentX)),
                                    viewpoint.translateSlide2Screen(y1 - viewpoint.translateScreen2Slide(viewpoint.currentY)),
                                    viewpoint.translateSlide2Screen(x2 - x1), viewpoint.translateSlide2Screen(y2 - y1));
                            break;

                        case FREEHAND:
                            System.out.println("DRAWING FREEHAND");
                            TwoDimensionSpatialCoordinate tdsc[] = (TwoDimensionSpatialCoordinate[]) gs[x].getSpatialCoordinateCollection().getSpatialCoordinate();

                            System.out.println("tdsc len" + tdsc.length);
                            for (int l = 1; l < tdsc.length; l++) {

                                g2.drawLine(viewpoint.translateSlide2Screen((int) tdsc[l - 1].getX() - viewpoint.translateScreen2Slide(viewpoint.currentX)),
                                        viewpoint.translateSlide2Screen((int) (tdsc[l - 1].getY() - viewpoint.translateScreen2Slide(viewpoint.currentY))),
                                        viewpoint.translateSlide2Screen((int) (tdsc[l].getX() - viewpoint.translateScreen2Slide(viewpoint.currentX))),
                                        viewpoint.translateSlide2Screen((int) (tdsc[l].getY() - viewpoint.translateScreen2Slide(viewpoint.currentY))));
                            }

                            break;
                        case LINE:
                            System.out.println("DRAWING LINE");


                            c1 = (TwoDimensionSpatialCoordinate) gs[x].getSpatialCoordinateCollection().getSpatialCoordinate(0);
                            c2 = (TwoDimensionSpatialCoordinate) gs[x].getSpatialCoordinateCollection().getSpatialCoordinate(1);

                            x1 = (int) c1.getX();
                            y1 = (int) c1.getY();
                            x2 = (int) c2.getX();
                            y2 = (int) c2.getY();
                            g2.drawLine(viewpoint.translateSlide2Screen(x1 - viewpoint.translateScreen2Slide(viewpoint.currentX)),
                                    viewpoint.translateSlide2Screen(y1 - viewpoint.translateScreen2Slide(viewpoint.currentY)),
                                    viewpoint.translateSlide2Screen(x2 - viewpoint.translateScreen2Slide(viewpoint.currentX)),
                                    viewpoint.translateSlide2Screen(y2 - viewpoint.translateScreen2Slide(viewpoint.currentY)));
                            break;

                        default:  // Should never happen!
                            break;
                    }
                }
            }

        viewpoint.setCurrentClass(oldClass);
    }



    public Point findLowShape(){

        int lowX, lowY;

        lowX = Integer.MAX_VALUE;
        lowY = 0;

        boolean hit = false;

        if(loadedAnnotation == null){ System.out.println("findlowshape loadedAnnotation null"); return null;}

        int x1, x2, y1, y2;

        TwoDimensionSpatialCoordinate c1, c2;


        ImageAnnotationGeometricShapeCollection gsc;

        GeometricShape[] gs;

        System.out.println("CHECKING for lowShape"+numLoadedShapes);
        int oldClass = viewpoint.getCurrentClass();

        for(int x=0; x< numLoadedShapes; x++){

            gsc = loadedAnnotation.getGeometricShapeCollection();
            gs = gsc.getGeometricShape();



            switch (getShapeFromString(gs[x].getClass().getSimpleName())) {
                case OVAL:

                    c1 = (TwoDimensionSpatialCoordinate) gs[x].getSpatialCoordinateCollection().getSpatialCoordinate(0);
                    c2 = (TwoDimensionSpatialCoordinate) gs[x].getSpatialCoordinateCollection().getSpatialCoordinate(1);

                    x1 = (int) c1.getX();
                    y1 = (int) c1.getY();
                    x2 = (int) c2.getX();
                    y2 = (int) c2.getY();

                    if((x1+y1) < (lowX+lowY)){
                        lowX = x1;
                        lowY = y1;
                    }

                     if((x2+y2) < (lowX+lowY)){
                        lowX = x2;
                        lowY = y2;
                    }
         
                    break;


                case FREEHAND:
                    System.out.println(" FREEHAND");

                    GeometricShapeSpatialCoordinateCollection rah = gs[x].getSpatialCoordinateCollection();
                    SpatialCoordinate[] tdsc = (SpatialCoordinate[]) rah.getSpatialCoordinate();

                    if(tdsc != null){
                        System.out.println("tdsc len" + tdsc.length);
                        for (int l = 1; l < tdsc.length; l++) {

                        // Some bug causing a cast error forces me to do this instead of accessing tdsc[l]
                            TwoDimensionSpatialCoordinate scur = (TwoDimensionSpatialCoordinate) rah.getSpatialCoordinate(l);
                            TwoDimensionSpatialCoordinate spre = (TwoDimensionSpatialCoordinate) rah.getSpatialCoordinate(l-1);


                            if ((scur.getX() + scur.getY()) < (lowX + lowY)) {
                                lowX = (int) scur.getX();
                                lowY = (int) scur.getY();
                            }
               
                        }
                    }
                    break;

                case LINE:
                    c1 = (TwoDimensionSpatialCoordinate) gs[x].getSpatialCoordinateCollection().getSpatialCoordinate(0);
                    c2 = (TwoDimensionSpatialCoordinate) gs[x].getSpatialCoordinateCollection().getSpatialCoordinate(1);

                    x1 = (int) c1.getX();
                    y1 = (int) c1.getY();
                    x2 = (int) c2.getX();
                    y2 = (int) c2.getY();


                    if((x1+y1) < (lowX+lowY)){
                        lowX = x1;
                        lowY = y1;
                    }

                     if((x2+y2) < (lowX+lowY)){
                        lowX = x2;
                        lowY = y2;
                    }

                    break;

                default:  // Should never happen!
                    break;
            }
        }

        System.out.println("returning"+lowX+","+lowY);

        return new Point(lowX,lowY);

    }

    private void drawLoadedShapes(Graphics2D g2) {

        if(loadedAnnotation == null){ System.out.println("loadedAnnotation null"); return;}


        int x1, x2, y1, y2;

        TwoDimensionSpatialCoordinate c1, c2;


        ImageAnnotationGeometricShapeCollection gsc;

        GeometricShape[] gs;

        System.out.println("Draw loaded shapes"+numLoadedShapes);
        int oldClass = viewpoint.getCurrentClass();

        for(int x=0; x< numLoadedShapes; x++){

            gsc = loadedAnnotation.getGeometricShapeCollection();
            gs = gsc.getGeometricShape();

            g2.setColor(annotationClassColors[Integer.parseInt(gs[x].getImageAnnotation().getImageAnnotation().getName())]);
            g2.setStroke(stroke);


            switch (getShapeFromString(gs[x].getClass().getSimpleName())) {
                case OVAL:

                    c1 = (TwoDimensionSpatialCoordinate) gs[x].getSpatialCoordinateCollection().getSpatialCoordinate(0);
                    c2 = (TwoDimensionSpatialCoordinate) gs[x].getSpatialCoordinateCollection().getSpatialCoordinate(1);

                    x1 = (int) c1.getX();
                    y1 = (int) c1.getY();
                    x2 = (int) c2.getX();
                    y2 = (int) c2.getY();

                    g2.drawOval(viewpoint.translateSlide2Screen(x1 - viewpoint.translateScreen2Slide(viewpoint.currentX)),
                            viewpoint.translateSlide2Screen(y1 - viewpoint.translateScreen2Slide(viewpoint.currentY)),
                            viewpoint.translateSlide2Screen(x2 - x1), viewpoint.translateSlide2Screen(y2 - y1));

                    break;

//                case RECTANGLE:
//                    g2.drawRect(viewpoint.translateSlide2Screen(loadedShapeX1[x]-viewpoint.translateScreen2Slide(viewpoint.currentX)/* - viewpoint.currentX*/),
//                            viewpoint.translateSlide2Screen(loadedShapeY1[x] -viewpoint.translateScreen2Slide(viewpoint.currentY)/*- viewpoint.currentY*/),
//                         viewpoint.translateSlide2Screen(loadedShapeX2[x] - loadedShapeX1[x]),
//                         viewpoint.translateSlide2Screen(loadedShapeY2[x] - loadedShapeY1[x]));
//                    break;

                case FREEHAND:
                    System.out.println("DRAWING FREEHAND");

                    GeometricShapeSpatialCoordinateCollection rah = gs[x].getSpatialCoordinateCollection();
                    SpatialCoordinate[] tdsc = (SpatialCoordinate[]) rah.getSpatialCoordinate();

                    if(tdsc != null){
                        System.out.println("tdsc len" + tdsc.length);
                        for (int l = 1; l < tdsc.length; l++) {

                           // Some bug causing a cast error forces me to do this instead of accessing tdsc[l]

                            TwoDimensionSpatialCoordinate scur = (TwoDimensionSpatialCoordinate) rah.getSpatialCoordinate(l);
                            TwoDimensionSpatialCoordinate spre = (TwoDimensionSpatialCoordinate) rah.getSpatialCoordinate(l-1);

                            g2.drawLine(viewpoint.translateSlide2Screen((int) spre.getX() - viewpoint.translateScreen2Slide(viewpoint.currentX)),
                                    viewpoint.translateSlide2Screen((int) (spre.getY() - viewpoint.translateScreen2Slide(viewpoint.currentY))),
                                    viewpoint.translateSlide2Screen((int) (scur.getX() - viewpoint.translateScreen2Slide(viewpoint.currentX))),
                                    viewpoint.translateSlide2Screen((int) (scur.getY() - viewpoint.translateScreen2Slide(viewpoint.currentY))));
                          }
                    } else {System.out.println("NULL shape");}
                    break;

                case LINE:
                    c1 = (TwoDimensionSpatialCoordinate) gs[x].getSpatialCoordinateCollection().getSpatialCoordinate(0);
                    c2 = (TwoDimensionSpatialCoordinate) gs[x].getSpatialCoordinateCollection().getSpatialCoordinate(1);

                    x1 = (int) c1.getX();
                    y1 = (int) c1.getY();
                    x2 = (int) c2.getX();
                    y2 = (int) c2.getY();

                    g2.drawLine(viewpoint.translateSlide2Screen(x1-viewpoint.translateScreen2Slide(viewpoint.currentX)/* - viewpoint.currentX*/),
                            viewpoint.translateSlide2Screen(y1 -viewpoint.translateScreen2Slide(viewpoint.currentY)/*- viewpoint.currentY*/),
                         viewpoint.translateSlide2Screen(x2 - viewpoint.translateScreen2Slide(viewpoint.currentX)),
                         viewpoint.translateSlide2Screen(y2 - viewpoint.translateScreen2Slide(viewpoint.currentY)));
                    break;

                default:  // Should never happen!
                    break;
            }
        }

        viewpoint.setCurrentClass(oldClass);
    }


    Shape getShapeFromString(String s){


        if(s.startsWith("Freehand")) return Shape.FREEHAND;


        if(s.startsWith("Ellipse")) return Shape.OVAL;

        if(s.startsWith("Polyline")) return Shape.FREEHAND;


        if(s.startsWith("OVAL")) return Shape.OVAL;

        if(s.startsWith("RECTANGLE")) return Shape.RECTANGLE;

        if(s.startsWith("LINE")) return Shape.LINE;

        return Shape.OVAL;

    }

    public void clearShapes() {
        System.out.println("clearing shapes");
        numSavedShapes = 0;
        numLoadedShapes = 0;

    }




    void loadAnnotation(FileReader reader){

        org.xml.sax.InputSource i = new org.xml.sax.InputSource(reader);

        try {
            System.out.println("loading annotation from file");
            loadedAnnotation = (ImageAnnotation) ObjectDeserializer.deserialize(i, ImageAnnotation.class);
        } catch (DeserializationException ex) {
            Logger.getLogger(PathologyClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Deserialized");

        if(loadedAnnotation == null) {
            System.out.println("loadedAnnotation null");
            repaint();
            return;
        }

        ImageAnnotationGeometricShapeCollection gsc = loadedAnnotation.getGeometricShapeCollection();

        GeometricShape[] gs = gsc.getGeometricShape();

        numLoadedShapes = gs.length;
        repaint();


    }

    void loadAnnotation(ImageAnnotation ann) {
        System.out.println("load annotation from ImageAnnotation");

        loadedAnnotation = ann;

        GeometricShape[] sh = ann.getGeometricShapeCollection().getGeometricShape();

        if(sh == null) {
            System.out.println("no shapes");
            repaint();
            return;
        }

        numLoadedShapes = sh.length;

    }


    void erase(int x){
        geometricCol.setGeometricShape(x, null);
    }



    public void eraseSegment(int x1, int y1, int x2, int y2){
        for (int x = 0; x < numSavedShapes; x++) {
            double bx1 = boundingRect[x].getBounds().getX();
            double by1 = boundingRect[x].getBounds().getY();
            double bw = boundingRect[x].getBounds().width;
            double bh = boundingRect[x].getBounds().height;

            if(
                boundingRect[x].contains(x1,y1,1,1) ||
                boundingRect[x].contains(x2,y2,1,1) ||
                java.awt.geom.Line2D.linesIntersect(x1,y1,x2,y2,bx1,by1, bx1+bw,by1+bh) ||
                java.awt.geom.Line2D.linesIntersect(x1,y1,x2,y2,bx1,by1, bx1,by1+bh)    ||
                java.awt.geom.Line2D.linesIntersect(x1,y1,x2,y2,bx1,by1, bx1+bw,by1)    ||
                
                java.awt.geom.Line2D.linesIntersect(x1+bw,y1,x2,y2,bx1,by1, bx1+bw,by1+bh) ||
                java.awt.geom.Line2D.linesIntersect(x1+bw,y1,x2,y2,bx1,by1, bx1,by1+bh) ||

                java.awt.geom.Line2D.linesIntersect(x1,y1+bh,x2,y2,bx1,by1, bx1+bw,by1+bh)
                ){

                erase(x);
            } 
              
        }
    }

    public void savePolySegment(Shape s, int c, int x1, int y1, int x2, int y2){

        if(tmpPolyLineCount == 0){

            tmpPolyLine[0] = new TwoDimensionSpatialCoordinate();
            tmpPolyLine[0].setX(x1);
            tmpPolyLine[0].setY(y1);
            tmpPolyLineCount++;


        }

        if(tmpPolyLineCount == (MAXPOLYLINELINES)){
            System.err.println("too many Polylines");
			JOptionPane.showMessageDialog(this, "Too many PolyLines");
        }

        tmpPolyLine[tmpPolyLineCount] = new TwoDimensionSpatialCoordinate();
        tmpPolyLine[tmpPolyLineCount].setX(x2);
        tmpPolyLine[tmpPolyLineCount].setY(y2);
        System.out.println("Setting to"+x2+","+y2);
        tmpPolyLineCount++;

    }

    // Only saves to array, not to xml database.
    public void saveShape(Shape s, int c, int x1, int y1, int x2, int y2){

        GeometricShapeImageAnnotation gsa = new GeometricShapeImageAnnotation();
        java.awt.geom.GeneralPath pathShape = new java.awt.geom.GeneralPath();


        int minX = x1;
        int minY = y1;
        int maxX = x1;
        int maxY = y1;

        int x = numSavedShapes;



        System.out.println("saving shape" + x);

        GeometricShape g = null;

        switch (s) {
            case FREEHAND:
            case LINE:
                g = new Polyline();
                break;

            case OVAL:
                g = new Ellipse();
                break;

        }

        ImageAnnotation ia = new ImageAnnotation();

        System.out.println("set ImageAnnotation name to"+c);
        ia.setName(c+"");


        gsa.setImageAnnotation(ia);

        g.setImageAnnotation(gsa);

        GeometricShapeSpatialCoordinateCollection spc = new GeometricShapeSpatialCoordinateCollection();


        if (viewpoint.getShape() == Shape.FREEHAND) {


            TwoDimensionSpatialCoordinate sc[] = new TwoDimensionSpatialCoordinate[tmpPolyLineCount];

            System.out.println("saving: " + tmpPolyLineCount + " tmpPolyLines");
            for (int l1=1; l1< tmpPolyLineCount; l1++){

                java.awt.geom.Line2D.Double l = new java.awt.geom.Line2D.Double(
                        tmpPolyLine[l1-1].getX(),
                        tmpPolyLine[l1-1].getY(),
                        tmpPolyLine[l1].getX(),
                        tmpPolyLine[l1].getY());
                pathShape.append(l, true);
                        //new java.awt.geom.Shape(tmpPolyLine[l1].getX(),tmpPolyLine[l1].getY(),1,1), true);

            }
            for (int l = 0; l < tmpPolyLineCount; l++) {

                if (tmpPolyLine[l].getX() < minX) {
                    minX = (int) tmpPolyLine[l].getX();
                }
                if (tmpPolyLine[l].getY() < minY) {
                    minY = (int) tmpPolyLine[l].getY();
                }

                if (tmpPolyLine[l].getX() > maxX) {
                    maxX = (int) tmpPolyLine[l].getX();
                }
                if (tmpPolyLine[l].getY() > maxY) {
                    maxY = (int) tmpPolyLine[l].getY();
                }

                System.out.println("saving" + tmpPolyLine[l].getX() + "," + tmpPolyLine[l].getY());
                sc[l] = tmpPolyLine[l];
            }
            spc.setSpatialCoordinate(sc);

        } else {

            if(x1 > x2) {maxX = x1; minX = x2;}
            else { maxX=x2; minX=x1;}

            if(y1 > y2) {maxY = y1; minY = y2;}
            else { maxY=y2; minY=y1;}


            TwoDimensionSpatialCoordinate sc[] = new TwoDimensionSpatialCoordinate[2];

            sc[0] = new TwoDimensionSpatialCoordinate();
            sc[0].setX(x1);
            sc[0].setY(y1);

            sc[1] = new TwoDimensionSpatialCoordinate();
            sc[1].setX(x2);
            sc[1].setY(y2);
            spc.setSpatialCoordinate(sc);

        }


        g.setSpatialCoordinateCollection(spc);


        if (geometricCol == null) {
            System.err.println("null geometricCol");
        }

        if (g == null) {
            System.err.println("null g");
        }

        if(x >= geometricCol.getGeometricShape().length)
            growShapes();

        geometricCol.setGeometricShape(x, g);

        switch (s) {
            case OVAL:
                boundingRect[numSavedShapes] = new Rectangle(x1, y1, x2 - x1, y2 - y1);

                break;

            case RECTANGLE:
                boundingRect[numSavedShapes] = new Rectangle(x1, y1, x2 - x1, y2 - y1);
                break;

            case LINE:
                System.out.println("BOUDNING RECT for:x1 y1 x2 y2"+x1+","+y1+" "+x2+","+y2);
                System.out.println("BOUDNING RECT minx miny maxx maxy:"+minX+","+minY+" "+maxX+","+maxY);
                System.out.println("BOUNDING RECT MIN/MAX:"+minX+","+minY+" "+maxX+","+maxY);



                java.awt.geom.Line2D.Double l = new java.awt.geom.Line2D.Double(x1,y1,x2,y2);

                boundingRect[numSavedShapes] = l.getBounds2D();
                        //Rectangle(minX, minY, maxX - minX, maxY - minY);

                //boundingRect[numSavedShapes] = new Rectangle(x1, y1, x2 - x1, y2 - y1);
                        //new java.awt.geom.Line2D.Double(x1, y1, x2, y2);
                break;

            case FREEHAND:


                boundingRect[numSavedShapes] = pathShape.getBounds2D();
                        //new Rectangle(minX, minY, maxX - minX, maxY - minY);
                 break;
        }



        if (numSavedShapes >= (MAXSHAPES - 1)) {
            JOptionPane.showMessageDialog(this, "Too many shapes");
            System.err.println("too many shapes.  Increase MAXSHAPES");
            return;
        }


        numSavedShapes++;
        return;
    }

    public void growShapes(){
        System.out.println("Growing more shapes");
        GeometricShape geometricShapes2[] = new GeometricShape[MAXSHAPES];

        int b=0;
        for(int a=0; a< numSavedShapes; a++){
            geometricShapes2[b] = geometricShapes[a];
            b++;
        }

        geometricCol.setGeometricShape(geometricShapes2);
        geometricShapes = geometricShapes2;
    }

    public void deleteEmptyShapes(){
        int nulls = 0;

        for(int a=0; a< numSavedShapes; a++){
            if(geometricShapes[a] == null ) nulls++;
        }


        GeometricShape geometricShapes2[] = new GeometricShape[numSavedShapes - nulls];

        int b=0;
        for(int a=0; a< numSavedShapes; a++){
            if(geometricShapes[a] != null){
                geometricShapes2[b] = geometricShapes[a];
                b++;
            }

        }

        geometricCol.setGeometricShape(geometricShapes2);

    }

    public void saveAnnotations(File file, String name, String author1, String txtStr1) {

        //String txtStr = txtStr1;
        

        String annXml = getAnnotationXml(name, author1, txtStr1);

        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            System.err.println("error");
        }

            try {
                ObjectSerializer.serialize(writer, annotation, new QName("gme://caCORE.caCORE/3.2/edu.northwestern.radiology.AIM", "ImageAnnotation"));
            } catch (SerializationException ex) {
                Logger.getLogger(PaintPanel.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(PathologyClient.class.getName()).log(Level.SEVERE, null, ex);
            }
    }


    private class MListener implements MouseListener, MouseMotionListener {
        public void mousePressed(MouseEvent e) {
            System.out.println("pressed");

            start = e.getPoint();     // Save start point, and also initially
            end = start;           // as end point, which drag will change.

            if (viewpoint.getState() != State.ENABLED) return;

            if (viewpoint.getState() == State.ERASING) return;

           
            viewpoint.setState(State.DRAGGING);   // Assume we're starting a drag.


        }

        public void mouseDragged(MouseEvent e) {
            if (viewpoint.getState() == State.IDLE) return;

            end = e.getPoint();

            if (viewpoint.getState() == State.ERASING) {
                eraseSegment( viewpoint.translateScreen2Slide(start.x+viewpoint.currentX), 
                        viewpoint.translateScreen2Slide(start.y+viewpoint.currentY),
                        viewpoint.translateScreen2Slide(end.x+viewpoint.currentX), 
                        viewpoint.translateScreen2Slide(end.y+viewpoint.currentY)); 
            } else

            //viewpoint.setState(State.DRAGGING);   // We're dragging to create a shape.

            if (viewpoint.getShape() == Shape.FREEHAND) {
                savePolySegment(viewpoint.getShape(), viewpoint.getCurrentClass(), viewpoint.translateScreen2Slide(start.x) + viewpoint.translateScreen2Slide(viewpoint.currentX),
                        viewpoint.translateScreen2Slide(start.y) + viewpoint.translateScreen2Slide(viewpoint.currentY),
                        viewpoint.translateScreen2Slide(end.x) + viewpoint.translateScreen2Slide(viewpoint.currentX),
                        viewpoint.translateScreen2Slide(end.y) + viewpoint.translateScreen2Slide(viewpoint.currentY));
            }
            repaint();            // After change, show new shape
        }

        public void mouseReleased(MouseEvent e) {

            //... If released at end of drag, write shape into the BufferedImage,
            //    which saves it in the drawing.
            end = e.getPoint();      // Set end point of drag.
            if (viewpoint.getState() == State.DRAGGING) {
                viewpoint.setState(State.ENABLED);


                System.out.println("%%%%%%currentX" + viewpoint.currentX + "," + viewpoint.currentY + " x,y=" + end.x + "," + end.y);
                saveShape(viewpoint.getShape(), viewpoint.getCurrentClass(), viewpoint.translateScreen2Slide(start.x) + viewpoint.translateScreen2Slide(viewpoint.currentX),
                        viewpoint.translateScreen2Slide(start.y) + viewpoint.translateScreen2Slide(viewpoint.currentY),
                        viewpoint.translateScreen2Slide(end.x) + viewpoint.translateScreen2Slide(viewpoint.currentX),
                        viewpoint.translateScreen2Slide(end.y) + viewpoint.translateScreen2Slide(viewpoint.currentY));

                repaint();
            }
            tmpPolyLineCount = 0;
        }

        public void mouseMoved(MouseEvent e) {
            if ((viewpoint.getState() == State.ENABLED) && (viewpoint.getShape() == Shape.LINE)) {
              //  System.out.println("line");
                end = e.getPoint();
                repaint();
            }
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {


            System.out.println("clicked");
            int shapesDeleted = 0;
            if (viewpoint.getState() == State.ERASING) {
                eraseSegment( viewpoint.translateScreen2Slide(start.x+viewpoint.currentX),
                        viewpoint.translateScreen2Slide(start.y+viewpoint.currentY),
                        viewpoint.translateScreen2Slide(start.x+viewpoint.currentX)+1,
                        viewpoint.translateScreen2Slide(start.y+viewpoint.currentY)+1);

                return;
            }

            start = e.getPoint();     // Save start point, and also initially
            end = start;           // as end point, which drag will change.

        }

    }




}



