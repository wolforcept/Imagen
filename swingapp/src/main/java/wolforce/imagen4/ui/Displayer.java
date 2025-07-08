package wolforce.imagen4.ui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Displayer extends JPanel {

    PanelImage panelImage = null;
    int prevHash = -1;

    public Displayer() {
        setLayout(new GridLayout(0, 1));
        setBackground(Color.DARK_GRAY);
    }

    public void render(int hash, BufferedImage img) {
        PanelImage prevPanel = hash == prevHash ? panelImage : null;
        removeAll();
        panelImage = new PanelImage(img, prevPanel);
        add(panelImage);
        repaint();
        prevHash = hash;
    }

    private static class PanelImage extends JPanel {

        BufferedImage img;
        private float scale = 1;
        private boolean dragging = false;

        private Point origin = new Point(0, 0);
        private Point startDrag = new Point(0, 0);

        public PanelImage(BufferedImage img, PanelImage prev) {

            if (prev != null) {
                this.origin = prev.origin;
                this.startDrag = prev.startDrag;
                this.scale = prev.scale;
                this.dragging = prev.dragging;
            }

            this.img = img;

            MouseAdapter adapter = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        dragging = true;
                        startDrag.x = origin.x - e.getX();
                        startDrag.y = origin.y - e.getY();
                    }
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        if (scale == 1) multScale(0.5f / scale, e.getX(), e.getY());
                        else if (scale == 0.5) multScale(0.25f / scale, e.getX(), e.getY());
                        else multScale(1 / scale, e.getX(), e.getY());
                        repaint();
                    }
                }

                public void mouseExited(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) dragging = false;
                }

                public void mouseReleased(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) dragging = false;
                }

                public void mouseDragged(MouseEvent e) {
                    if (dragging) {
                        origin.x = startDrag.x + e.getX();
                        origin.y = startDrag.y + e.getY();
                        repaint();
                    }
                }

                public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
                    multScale((float) Math.pow(1.1, -e.getPreciseWheelRotation()), e.getX(), e.getY());
                    repaint();
                }

            };
            addMouseListener(adapter);
            addMouseMotionListener(adapter);
            addMouseWheelListener(adapter);
        }

        private void multScale(float scaleFactor, float mx, float my) {
            origin.x -= mx;
            origin.y -= my;
            scale *= scaleFactor;
            origin.x *= scaleFactor;
            origin.y *= scaleFactor;
            origin.x += mx;
            origin.y += my;
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.clearRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.DARK_GRAY);
            g2.fillRect(-1000, -1000, 10000, 10000);
            if (scale < 1) {
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }
            // float f = (float) img.getHeight() / (float) img.getWidth();
            g2.drawImage(img, origin.x, origin.y, (int) (img.getWidth() * scale), (int) (img.getHeight() * scale), null);
        }

    }

}
