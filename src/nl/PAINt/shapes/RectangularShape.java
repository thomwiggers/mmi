package nl.PAINt.shapes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import org.apache.log4j.Logger;

public abstract class RectangularShape implements Shape {
	protected double x, y, width, height;
	protected boolean selectionBox;
	protected Color color = Color.black;
	protected Color lineColor = Color.black;
	protected float lineWidth = 3.0f;
	protected boolean isFilled = false;

	protected Logger logger;

	/**
	 * @param color
	 *          the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	private enum CornerLock {
		TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT
	};

	private CornerLock lockedCorner = null;
	private Point xyLock = null;
	private Point whLock = null;
	private double angle = 10;
	private static int instanceNr = 0;


	public RectangularShape(final double x, final double y, final double width,
			final double height) {
		instanceNr++;
		selectionBox = false;
		setFrame(x, y, width, height);
		logger = Logger.getLogger(getClass());
	}

	public abstract void draw(Graphics2D g2d);

	public void setFrame(final double x, final double y, final double width,
			final double height) {
		this.x = x;
		this.y = y;

		this.width = width;
		this.height = height;

		if (this.width < 0) {
			this.x = x + this.width;
			this.width = -this.width;
		}

		if (this.height < 0) {
			this.y = y + this.height;
			this.height = -this.height;
		}
	}

	public void move(final double dx, final double dy) {
		logger.debug("Moving object (dx, dy): (" + dx + ", " + dy + ")");
		x += dx;
		y += dy;
	}

	protected void drawSelectionBox(final Graphics2D g2d) {

		Rectangle2D r2d = new Rectangle2D.Double(x, y, width, height);
		// g2d.rotate(angle, x + width / 2, y + height / 2);

		g2d.setPaint(Color.BLACK);
		final float dash[] = { 7.0f };
		g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
		g2d.draw(r2d);

		g2d.setPaint(Color.BLUE);
		g2d.setStroke(new BasicStroke(0.0f));

		r2d = new Rectangle2D.Double(x - 5, y - 5, 10, 10);
		g2d.fill(r2d);

		r2d = new Rectangle2D.Double(x + width - 5, y - 5, 10, 10);
		g2d.fill(r2d);

		r2d = new Rectangle2D.Double(x + width - 5, y + height - 5, 10, 10);
		g2d.fill(r2d);

		r2d = new Rectangle2D.Double(x - 5, y + height - 5, 10, 10);
		g2d.fill(r2d);


	}

	public boolean lockCorner(final Point p) {
		xyLock = new Point((int) x, (int) y);
		whLock = new Point((int) width, (int) height);
		if (p.x >= x - 5 && p.x <= x + 5 && p.y >= y - 5 && p.y <= y + 5) {
			logger.debug("Locked Top-Left corner");
			lockedCorner = CornerLock.TOPLEFT;
			return true;
		}
		if (p.x >= x + width - 5 && p.x <= x + width + 5 && p.y >= y - 5
				&& p.y <= y + 5) {
			logger.debug("Locked Top-Right corner");
			lockedCorner = CornerLock.TOPRIGHT;
			return true;
		}
		if (p.x >= x - 5 && p.x <= x + 5 && p.y >= y + height - 5
				&& p.y <= y + height + 5) {
			logger.debug("Locked Bottom-Left corner");
			lockedCorner = CornerLock.BOTTOMLEFT;
			return true;
		}
		if (p.x >= x - 5 + width && p.x <= x + 5 + width && p.y >= y - 5 + height
				&& p.y <= y + 5 + height) {
			logger.debug("Locked Bottom Right corner");
			lockedCorner = CornerLock.BOTTOMRIGHT;
			return true;
		}
		logger.debug("did not lock corner");
		xyLock = null;
		return false;
	}

	public void setFrameFromCorner(final Point start, final Point current) {
		if (lockedCorner == null)
			return;

		switch (lockedCorner) {
		case BOTTOMLEFT:
			setFrame(current.x, xyLock.y, whLock.x - (current.x - xyLock.x),
					current.y - xyLock.y);
			break;
		case BOTTOMRIGHT:
			setFrame(xyLock.x, xyLock.y, current.x - xyLock.x, current.y - xyLock.y);
			break;
		case TOPLEFT:
			setFrame(current.x, current.y, whLock.x - (current.x - xyLock.x),
					whLock.y - (current.y - xyLock.y));
			break;
		case TOPRIGHT:
			setFrame(xyLock.x, current.y, current.x - xyLock.x, whLock.y
					- (current.y - xyLock.y));
			break;
		}

	}

	public void unlockCorner() {
		logger.debug("Unlocked corner");
		lockedCorner = null;
	}

	public void setSelectionBox(final boolean bool) {
		logger.debug((bool ? "enabled " : "disabled") + " selection box");
		selectionBox = bool;
	}

	public abstract boolean checkHit(Point point);

	public void setLineColor(Color color) {
		logger.debug("Set line color of " + this.toString() + " to "
				+ color.toString());
		this.lineColor = color;
	}

	public void setLineWidth(float f) {
		logger.debug("set line width " + f);
		this.lineWidth = f;
	}

	public void setFilled(boolean isFilled) {
		logger.debug((isFilled ? "enabled" : "disabled") + " fill of "
				+ this.toString());
		this.isFilled = isFilled;
	}

	public String toString() {
		return getClass().getSimpleName() + " " + instanceNr;
	}

	public void rotate(int i) {
		this.angle += (i / 180) * Math.PI;
	}

	public boolean getFilled() {
		return this.isFilled;
	}
}
