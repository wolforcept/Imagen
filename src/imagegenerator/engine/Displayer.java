package imagegenerator.engine;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class Displayer {

	private static JFrame frame = null;
	private static JTabbedPane pane = null;

	public static void add(BufferedImage img, String name) {
		if (frame == null) {
			frame = new JFrame();
			pane = new JTabbedPane();
			frame.add(pane);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
		pane.add(new JPanel() {
			private static final long serialVersionUID = 1L;

			private float scale = 1;
			private boolean dragging = false;
			private Point origin = new Point(0, 0);
			private Point startDrag = new Point(0, 0);

			private void multScale(float scaleFactor, float mx, float my) {
				origin.x -= mx;
				origin.y -= my;
				scale *= scaleFactor;
				origin.x *= scaleFactor;
				origin.y *= scaleFactor;
				origin.x += mx;
				origin.y += my;
			}

			{
				setPreferredSize(new Dimension(900, 900));
				MouseAdapter adapter = new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						if (e.getButton() == MouseEvent.BUTTON1) {
							dragging = true;
							startDrag.x = origin.x - e.getX();
							startDrag.y = origin.y - e.getY();
						}
						if (e.getButton() == MouseEvent.BUTTON3) {
							if (scale == 1)
								multScale(0.5f / scale, e.getX(), e.getY());
							else if (scale == 0.5)
								multScale(0.25f / scale, e.getX(), e.getY());
							else
								multScale(1 / scale, e.getX(), e.getY());
							repaint();
						}
					};

					public void mouseExited(MouseEvent e) {
						if (e.getButton() == MouseEvent.BUTTON1)
							dragging = false;
					};

					public void mouseReleased(MouseEvent e) {
						if (e.getButton() == MouseEvent.BUTTON1)
							dragging = false;
					};

					public void mouseDragged(MouseEvent e) {
						if (dragging) {
							origin.x = startDrag.x + e.getX();
							origin.y = startDrag.y + e.getY();
							repaint();
						}
					};

					public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
						multScale((float) Math.pow(1.1, -e.getPreciseWheelRotation()), e.getX(), e.getY());
						repaint();
					};

				};
				addMouseListener(adapter);
				addMouseMotionListener(adapter);
				addMouseWheelListener(adapter);
			}

			@Override
			public void paint(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.clearRect(0, 0, getWidth(), getHeight());
				if (scale < 1) {
					g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				}
//				float f = (float) img.getHeight() / (float) img.getWidth();
				g2.drawImage(img, origin.x, origin.y, (int) (img.getWidth() * scale), (int) (img.getHeight() * scale), null);
			}
		}, name);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}

}
