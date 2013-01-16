package org.joshy.gfx.stage.swing;

import org.joshy.gfx.draw.*;
import org.joshy.gfx.draw.Image;
import org.joshy.gfx.node.Bounds;

import javax.media.opengl.GLAutoDrawable;
import java.awt.*;
import java.awt.Font;
import java.awt.Paint;
import java.awt.geom.*;
import java.util.ArrayDeque;
import java.util.Deque;

public class SwingGFX extends GFX {
    private org.joshy.gfx.draw.Paint fill;
    private Graphics2D g;
    private Deque<GFXState> stateStack;
    private Shape mask;

    public SwingGFX(Graphics2D graphics) {
        this.g = graphics;
        this.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        this.g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //this.g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        stateStack = new ArrayDeque<GFXState>();
    }

    public Object getNative() {
        return this.g;
    }
    @Override
    public void setPaint(org.joshy.gfx.draw.Paint paint) {
        this.fill = paint;
        if(paint instanceof FlatColor) {
            FlatColor fc = (FlatColor) paint;
            g.setColor(new Color(fc.getRGBA(),true));
        }
        if(paint instanceof SwingPatternPaint) {
            SwingPatternPaint pp = (SwingPatternPaint) paint;
            double len = pp.getEnd().distance(0,0);
            TexturePaint tp = new TexturePaint(
                    pp.image,
                    new Rectangle2D.Double(
                            pp.getStart().getX(),pp.getStart().getY(),
                            pp.getEnd().getX()-pp.getStart().getX(),
                            pp.getEnd().getY()-pp.getStart().getY()
                    )
            );
            g.setPaint(tp);
        }
        if(paint instanceof MultiGradientFill) {
            MultiGradientFill grad = (MultiGradientFill) paint;

            java.util.List<MultiGradientFill.Stop> stops = grad.getStops();
            float[] positions = new float[stops.size()];
            java.awt.Color[] colors = new java.awt.Color[stops.size()];
            for(int i=0; i<stops.size(); i++) {
                positions[i] = (float) stops.get(i).getPosition();
                colors[i] = new java.awt.Color(stops.get(i).getColor().getRGBA(),true);
            }
            if(grad instanceof RadialGradientFill) {
                RadialGradientFill rad = (RadialGradientFill) grad;
                RadialGradientPaint radp = new RadialGradientPaint(
                        (float)rad.getCenterX(),
                        (float)rad.getCenterY(),
                        (float)rad.getRadius(),
                        positions,colors
                        );
                g.setPaint(radp);
            }
            if(grad instanceof LinearGradientFill) {
                LinearGradientFill lin = (LinearGradientFill) grad;
                LinearGradientPaint linp = new LinearGradientPaint(
                        (float)lin.getStartX(),
                        (float)lin.getStartY(),
                        (float)lin.getEndX(),
                        (float)lin.getEndY(),
                        positions,colors

                );
                g.setPaint(linp);
            }
        }
    }

    @Override
    public void dispose() {
        g.dispose();
    }


    @Override
    public void drawRect(double x, double y, double width, double height) {
        Rectangle2D.Double r = new Rectangle2D.Double(x, y, width, height);
        g.draw(r);
    }

    @Override
    public void fillRect(double x, double y, double width, double height) {
        Rectangle2D.Double r = new Rectangle2D.Double(x, y, width, height);
        /*if(fill instanceof PatternPaint) {
            PatternPaint pp = (PatternPaint) fill;
            double a = GeomUtil.calcAngle(new Point(0,0),pp.getEnd());
            a += Math.toRadians(45);
            g.rotate(-a);
            AffineTransform af = AffineTransform.getRotateInstance(a);
            Shape r2 = af.createTransformedShape(r);
            g.fill(r2);
            g.rotate(a);
        } else {*/
            g.fill(r);
        /*}*/
    }

    @Override
    public void drawGridNine(GridNine gridNine, double rx, double ry, double width, double height) {
        g.translate(rx,ry);
        double x = 0;
        double y = 0;

        SwingGridNine g9 = (SwingGridNine) gridNine;
        int iw = g9.getImage().getWidth();
        int ih = g9.getImage().getHeight();

        int dx0 = (int)x;
        int dx1 = (int)(x+g9.getLeft());
        int dx2 = (int)(x + width - g9.getRight());
        int dx3 = (int)(x + width);

        int sx0 = 0;
        int sx1 = (int)g9.getLeft();
        int sx2 = iw-(int)(g9.getRight());
        int sx3 = iw;

        int dy0 = (int)y;
        int dy1 = (int)(y+g9.getTop());
        int dy2 = (int)(y + height - g9.getBottom());
        int dy3 = (int)(y + height);

        int sy0 = 0;
        int sy1 = (int)g9.getTop();
        int sy2 = ih - (int)g9.getBottom();
        int sy3 = ih;

        if(gridNine.isFlipX()) {
            int[] temp = new int[4];
            temp[0] = sx0;
            temp[1] = sx1;
            temp[2] = sx2;
            temp[3] = sx3;
            sx0 = temp[3];
            sx1 = temp[2];
            sx2 = temp[1];
            sx3 = temp[0];
        }
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g.drawImage(g9.getImage(), dx0, dy0, dx1, dy1, sx0, sy0, sx1, sy1, null);
        g.drawImage(g9.getImage(), dx1, dy0, dx2, dy1, sx1, sy0, sx2, sy1, null);
        g.drawImage(g9.getImage(), dx2, dy0, dx3, dy1, sx2, sy0, sx3, sy1, null);
        g.drawImage(g9.getImage(), dx0, dy1, dx1, dy2, sx0, sy1, sx1, sy2, null);
        g.drawImage(g9.getImage(), dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
        g.drawImage(g9.getImage(), dx2, dy1, dx3, dy2, sx2, sy1, sx3, sy2, null);
        g.drawImage(g9.getImage(), dx0, dy2, dx1, dy3, sx0, sy2, sx1, sy3, null);
        g.drawImage(g9.getImage(), dx1, dy2, dx2, dy3, sx1, sy2, sx2, sy3, null);
        g.drawImage(g9.getImage(), dx2, dy2, dx3, dy3, sx2, sy2, sx3, sy3, null);
        g.translate(-rx,-ry);
    }

    @Override
    public void drawLine(double x, double y, double x2, double y2) {
        g.draw(new Line2D.Double(x,y,x2,y2));
    }

    @Override
    public void translate(double x, double y) {
        g.translate(x,y);
    }

    @Override
    public void scale(double scaleX, double scaleY) {
        g.scale(scaleX,scaleY);
    }

    @Override
    public void translate(double translateX, double translateY, double translateZ) {
        g.translate(translateX,translateY);
    }

    @Override
    public void rotate(double rotation, Transform rotationAxis) {
        g.rotate(Math.toRadians(rotation));
    }

    @Override
    public void push() {
        stateStack.push(new GFXState(g));
    }

    @Override
    public void pop() {
        GFXState s = stateStack.pop();
        s.restore(g);
    }

    @Override
    public boolean isGL() {
        return false;
    }

    @Override
    public GLAutoDrawable getDrawable() {
        return null;
    }

    @Override
    public void fillOval(double x, double y, double width, double height) {
        Ellipse2D.Double o = new Ellipse2D.Double(x, y, width, height);
        g.fill(o);
    }

    @Override
    public void drawOval(double x, double y, double width, double height) {
        Ellipse2D.Double o = new Ellipse2D.Double(x, y, width, height);
        g.draw(o);
    }

    @Override
    public void setClipRect(Bounds bounds) {
        g.setClip(
                (int)bounds.getX(),
                (int)bounds.getY(),
                (int)bounds.getWidth(),
                (int)bounds.getHeight());
    }

    @Override
    public Bounds getClipRect() {
        java.awt.Rectangle r = g.getClipBounds();
        return new Bounds(r.x,r.y,r.width,r.height);
    }


    @Override
    public void drawText(String text, org.joshy.gfx.draw.Font font, double x, double y) {
        Font fnt = font.getAWTFont();
        //Font fnt = new Font(jogltext.font.getName(), Font.PLAIN, 12);
        g.setFont(fnt);
        g.drawString(text,(int)x,(int)y);
    }

    @Override
    public void drawImage(Image img, double x, double y) {
        SwingImage image = (SwingImage) img;
        if(mask != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setClip(mask);
            g2.drawImage(image.buffer, (int) x, (int) y, null);
            g2.dispose();
        } else {
            g.drawImage(image.buffer, (int)x, (int)y, null);
        }
    }

    @Override
    public void drawImage(Image img, double dx, double dy, double dw, double dh, double sx, double sy, double sw, double sh) {
        SwingImage image = (SwingImage) img;
        g.drawImage(image.buffer,
                (int)dx, (int)dy, (int)(dx+dw), (int)(dy+dh),
                (int)sx, (int)sy, (int)(sx+sw), (int)(sy+sh),
                null
        );
    }

    @Override
    public void drawPolygon(double[] points) {
        drawPolygon(points,true);
    }

    @Override
    public void drawPolygon(double[] points, boolean closed) {
        if(points.length < 2) return;
        Path2D.Double p = new Path2D.Double();
        p.moveTo(points[0],points[1]);
        for(int i=2; i<points.length; i+=2) {
            p.lineTo(points[i],points[i+1]);
        }
        if(closed) {
            p.lineTo(points[0],points[1]);
        }
        g.draw(p);
    }

    @Override
    public void fillPolygon(double[] points) {
        Path2D.Double p = new Path2D.Double();
        p.moveTo(points[0],points[1]);
        for(int i=2; i<points.length; i+=2) {
            p.lineTo(points[i],points[i+1]);
        }
        g.fill(p);
    }

    @Override
    public void fillRoundRect(double x, double y, double w, double h, double aw, double ah) {
        g.fillRoundRect((int)x,(int)y,(int)w,(int)h,(int)aw,(int)ah);
    }

    /**
     * do an altered form of the rounded rect which has different radii for each corner 
     * @param x
     * @param y
     * @param width
     * @param height
     * @param ul
     * @param ur
     * @param lr
     * @param ll
     */
    @Override
    public void fillRoundRectAltered(double x, double y, double width, double height, double ul, double ur, double lr, double ll) {
        g.fillRoundRect((int)x,(int)y,(int)width,(int)height,(int)ul,(int)ul);
    }

    @Override
    public void fillCustomRoundRect(double x, double y, double w, double h, double ulw, double ulh, double urw, double urh, double lrw, double lrh, double llw, double llh) {
        Path2D path = createCustomRoundRect(x,y,w,h,ulw,ulh,urw,urh,lrw,lrh,llw,llh);
        g.fill(path);
    }

    private Path2D createCustomRoundRect(double x, double y, double w, double h, double ulw, double ulh, double urw, double urh, double lrw, double lrh, double llw, double llh) {
        Path2D path = new Path2D.Double();
        path.moveTo(x+ulw,y);
        //upper right
        path.lineTo(x+w-urw,y);
        path.quadTo(x+w,y, x+w, y+urh);
        //lower right
        path.lineTo(x+w,y+h-lrh);
        path.quadTo(x+w, y+h, x+w-lrw,y+h);
        //lower left
        path.lineTo(x+llw,y+h);
        path.quadTo(x,y+h,x,y+h-llh);
        //upper left
        path.lineTo(x, y+ulh);
        path.quadTo(x,y,x+ulw,y);

        path.closePath();
        return path;
    }

    @Override
    public void drawCustomRoundRect(double x, double y, double w, double h, double ulw, double ulh, double urw, double urh, double lrw, double lrh, double llw, double llh) {
        Path2D path = createCustomRoundRect(x,y,w,h,ulw,ulh,urw,urh,lrw,lrh,llw,llh);
        g.draw(path);
    }

    @Override
    public void drawPath(Path2D.Double path) {
        g.draw(path);
    }

    @Override
    public void fillPath(Path2D.Double path) {
        g.fill(path);
    }

    @Override
    public void setSmoothImage(boolean smooth) {
        if(smooth) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        } else {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);            
        }
    }

    @Override
    public void setMask(Shape mask) {
        this.mask = mask;
    }

    @Override
    public ImageBuffer createBuffer(int width, int height) {
        return new ImageBuffer(width,height);
    }

    @Override
    public void draw(ImageBuffer buf, double x, double y) {
        g.drawImage(buf.buf, (int)x, (int)y, null);
    }

    @Override
    public void setStrokeWidth(double strokeWidth) {
        g.setStroke(new BasicStroke((float) strokeWidth));
    }


    @Override
    public void setOpacity(double opacity) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) opacity));
    }
    @Override
    public double getOpacity() {
        Composite comp = g.getComposite();
        if(comp instanceof AlphaComposite) {
            AlphaComposite alpha = (AlphaComposite) comp;
            return alpha.getAlpha();
        }
        return 1.0;
    }

    @Override
    public void setPureStrokes(boolean pureStrokes) {
        if(pureStrokes) {
            this.g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        } else {
            this.g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
        }
    }


    @Override
    public void drawRoundRect(double x, double y, double w, double h, double aw, double ah) {
        g.drawRoundRect((int)x,(int)y,(int)w,(int)h,(int)aw,(int)ah);        
    }

    @Override
    public void fillCircle(double cx, double cy, double radius) {
        g.fillOval((int)(cx-radius),(int)(cy-radius),(int)(radius*2.0),(int)(radius*2.0));
    }

    @Override
    public void fillArc(double cx, double cy, double radius, double angleStart, double angleEnd) {
        g.fillArc((int)(cx-radius),(int)(cy-radius),(int)(radius*2.0),(int)(radius*2.0), (int)angleStart, (int)(angleEnd-angleStart));
    }


    private class GFXState {
        private AffineTransform oldTransform;
        private Paint oldPaint;

        public GFXState(Graphics2D g) {
            this.oldTransform = g.getTransform();
            this.oldPaint = g.getPaint();
        }

        public void restore(Graphics2D g) {
            g.setTransform(oldTransform);
            g.setPaint(oldPaint);
        }
    }
}
