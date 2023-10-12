package jmorph.gui;

import geometry.MyPoint2D;
import gui.ImagePanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import jmorph.JMorph;
import jmorph.MeasurementVector;
import jmorph.MouseInteractionManager;
import jmorph.Sample;
import jmorph.measurements.Measurement;
import jmorph.measurements.ZoomMeasurement;

/** A panel for drawing a sample image and measurement overlays.
 * Modelled on Class "RasterPanel" from the book "Java programming for spatial sciences" by Jo Wood.
 * @author Peter Lelievre
 */
public final class SampleImagePanel extends ImagePanel {
    private static final long serialVersionUID = 1L;

    // ------------------- Properties ------------------

    private final JMorph controller;

    // ------------------ Constructor ------------------

    /** Creates the panel.
     * @param con The JMorph controller object.
     */
    public SampleImagePanel(JMorph con) {
        super();
        controller = con;
        // Set mouse and keyboard listeners:
        addMouseListener(new MouseClickMonitor()); // listens for mouse clicks
        addMouseMotionListener(new MouseMoveMonitor()); // listens for mouse motion
        addComponentListener(new PanelSizeMonitor()); // listens for panel resizing
        addKeyListener(new KeyMonitor()); // listens for key presses
    }

    // -------------------- Overridden Methods --------------------

    /** Paints graphics on the panel.
     * @param g Graphics context in which to draw.
     */
    @Override
    public void paintComponent(Graphics g) {

        // Paint background:
        super.paintComponent(g);
        
        // Return if no samples exist:
        if (!controller.hasSamples()) { return; }

        // Get the current sample image:
        BufferedImage image = controller.getCurrentSampleImage();

        // Return if no sample image exists:
        if (image==null) { return; }
        
        // Get the current sample:
        Sample sample = controller.getCurrentSample();

        // Tightly fit the image inside the panel but maintain aspect ratio:
        calculateTightFitTransform();
        tightFitImage(g,image);

        // Use Java2D graphics for overlays:
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(controller.getLineWidth())); // line style
        
        // Extract the measurement list for the sample:
        MeasurementVector mList = sample.getMeasurementList();

        // Loop over the sample measurements, and include the zoom box, calibration length and origin:
        for ( int i=MouseInteractionManager.ZOOM_INDEX ; i<mList.size() ; i++ ) {

            /* Get the current measurement from the sample and the
             * plotting colour from the JMorph measurement list: */
            Measurement m;
            //Color col;
            switch (i) {
                case MouseInteractionManager.ZOOM_INDEX:
                    m = sample.getZoom();
                    //col = m.getPrimaryColour();
                    break;
                case MouseInteractionManager.CALIBRATION_INDEX:
                    m = sample.getCalibration();
                    //col = controller.getCalibrationColor();
                    break;
                case MouseInteractionManager.ORIGIN_INDEX:
                    m = sample.getOrigin();
                    //col = controller.getCalibrationColor();
                    break;
                default:
                    m = mList.get(i);
                    //col = controller.getMeasurementList().get(i).getPrimaryColour();
                    break;
            }

            // Check the current measurement has coordinates to paint:
            Boolean measuring = controller.shouldPaintMeasurement(m);
            if ( m.hasCoordinates() &&
                    ( ( controller.getDisplayMeasurements() && m.getMarked() )
                      || measuring
                    )
               ) {
                // Paint the measurement:
                m.runBeforePainting(measuring);
                m.paint(g2, imageToPanel, scaling, measuring );
            }

        }
        
    }

    // -------------------- Public Methods --------------------

    /** Calculates the transformations required to maintain the sample image aspect ratio
     * and fit tightly within this panel, keeping zoom box information in mind. */
    public void calculateTightFitTransform() {

        // Get the current sample image:
        BufferedImage image = controller.getCurrentSampleImage();

        // If no sample image exists then return:
        if (image==null) { return; }

        // Width and height of panel in pixels:
        double panelWidth  = this.getWidth();
        double panelHeight = this.getHeight();

        // May have to apply zoom box:
        ZoomMeasurement zoom = controller.getCurrentSample().getZoom();
        double imageWidth;
        double imageHeight;
        double imageOriginX;
        double imageOriginY;
        boolean doZoom;
        if (!zoom.isMeasured()) {
            doZoom = false;
        } else {
            // Don't zoom if we are currently measuring the zoom box:
            doZoom = !controller.measuringZoomBox();
        }
        if (doZoom) {
            imageWidth  = zoom.getCoordinates().rangeX();
            imageHeight = zoom.getCoordinates().rangeY();
            imageOriginX = zoom.getCoordinates().minX();
            imageOriginY = zoom.getCoordinates().minY();
        } else {
            // Width and height of image in pixels:
            imageWidth  = image.getWidth();
            imageHeight = image.getHeight();
            imageOriginX = 0.0;
            imageOriginY = 0.0;
        }

        // Calculate scaling:
        scaling = Math.min( panelWidth/imageWidth , panelHeight/imageHeight );
        double scaledWidth  = scaling*imageWidth;
        double scaledHeight = scaling*imageHeight;
        double scaledOriginX  = scaling*imageOriginX;
        double scaledOriginY  = scaling*imageOriginY;

        // Determine translation required to centre the image on the panel:
        double translateX = 0.5*(panelWidth  - scaledWidth ) - scaledOriginX;
        double translateY = 0.5*(panelHeight - scaledHeight) - scaledOriginY;
        translation = new MyPoint2D(translateX,translateY);
        
        // Create forward and inverse affine transformations:
        createTightFitTransform();
        
    }

    // -------------------- Monitors --------------------

    /** Listens for mouse clicks. */
    private class MouseClickMonitor extends MouseAdapter {
        @Override
        public void mouseClicked (MouseEvent e) {
            requestFocusInWindow(); // this is important to help the keyTyped method fire
            if (panelToImage==null) { return; }
            // Transform the current cursor location:
            MyPoint2D p = new MyPoint2D(e.getPoint());
            p.transform(panelToImage); // transform from panel to sample image pixel coordinates
            // Tell the controller about the mouse click:
            controller.mouseClick(p);
        }
    }

    /** Listens for mouse movement. */
    private class MouseMoveMonitor extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            requestFocusInWindow(); // this is important to help the keyTyped method fire
            if (panelToImage==null) { return; }
            // Transform the current cursor location:
            MyPoint2D p = new MyPoint2D(e.getPoint());
            p.transform(panelToImage); // transform from panel to sample image pixel coordinates
            // Tell the controller about the mouse move:
            controller.mouseMove(p);
        }
    }

    /** Listens for key presses. */
    private class KeyMonitor extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent e) {
            if (panelToImage==null) { return; }
            // Get the character corresponding to the key pressed:
            char c = e.getKeyChar();
            // Tell the controller about the key press:
            controller.keyType(c);
        }
    }
    
}
