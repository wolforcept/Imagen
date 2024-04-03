package wolforce.imagen4;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class Displayer {

	private final JFrame frame;
	private static JTabbedPane tabs = null;
	private static HashMap<String, PanelImage> panels = new HashMap<>();

	public Displayer() {
		frame = new JFrame();
		tabs = new JTabbedPane();
		frame.add(tabs);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				tabs.removeAll();
				panels.clear();
			}
		});
	}

	public void render(BufferedImage img, String name) {
		if (!panels.containsKey(name)) {
			PanelImage panel = new PanelImage(img);
			tabs.add(panel, name);
			panels.put(name, panel);
		} else {
			panels.get(name).img = img;
			panels.get(name).repaint();
		}
		if (panels.get(name) != null)
			tabs.setSelectedComponent(panels.get(name));
	}

	public void show() {
		frame.setVisible(true);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}

	private static class PanelImage extends JPanel {

		private static final long serialVersionUID = 1L;

		BufferedImage img;
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

		public PanelImage(BufferedImage img) {

			this.img = img;

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
			g.setColor(Color.DARK_GRAY);
			g2.fillRect(-1000, -1000, 10000, 10000);
			if (scale < 1) {
				g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			}
			// float f = (float) img.getHeight() / (float) img.getWidth();
			g2.drawImage(img, origin.x, origin.y, (int) (img.getWidth() * scale), (int) (img.getHeight() * scale),
					null);
		}

	}

	public boolean isVisible() {
		return frame.isVisible();
	}

}
