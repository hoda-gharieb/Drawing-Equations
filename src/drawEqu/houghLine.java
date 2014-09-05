package drawEqu;

import java.awt.image.BufferedImage;
import java.util.Vector;

/**
 * it represents an equation for each line generated from the hough transform
 * this equation depends on an angle(theta) and a perpendicular line (R)
 * @author Hoda, Menna
 *
 */
public class houghLine 
{
	// value of the angle between the perpendicular on the line and the axis of the image
	protected double theta;
	
	// the length of the perpendicular from the origin to the line
	protected double R;

	/**
	 * constructor which initiate the theta ( angle between main axis in the image and the perpendicular on a line ) and R ( perpendicular line )
	 * @param th
	 * @param r
	 */
	public houghLine(double th, double r) 
	{
		this.theta = th;
		this.R = r;
	}
	

	/**
	 * This function form an equation for a line in the image and return an equation along with end points in the vector
	 * @param image the image chosen by the user to form an equation for it
	 * @return a vector of double which contains the theta (which is the angle between perpendicular on the line and axis) and R (the perpendicular) 
	 * along with the x-coordinates and y-coordinates boundaries of the line 
	 */
	public Vector<Double> formEquation(BufferedImage image) 
	{
		Vector<Double> V = new Vector();
		int height = image.getHeight();
		int width = image.getWidth();

		// During processing h_h is doubled so that -ve r values
		int houghHeight = (int) (Math.sqrt(2) * Math.max(height, width)) / 2;

		// Find edge points and vote in array
		float Xcenter = width / 2;
		float Ycenter = height / 2;

		// Draw edges in output array
		double sinTheta = Math.sin(theta);
		double cosTheta = Math.cos(theta);

		if (theta < Math.PI * 0.25 || theta > Math.PI * 0.75)
		{
			int X = 0, Y = 0, S = 0;
			
			// found boundaries of semi vertical lines
			for (int y = 0; y < height; y++)
			{
				int x = (int) ((((this.R - houghHeight) - ((y - Ycenter) * sinTheta)) / cosTheta) + Xcenter);
				if (x < width && x >= 0	&& ((image.getRGB(x, y) & 0x000000ff) != 0))
				{
					X = x;
					Y = y;
					if (S == 0) {
						V.add(this.theta);
						V.add(this.R);
						V.add((double) X);
						V.add((double) Y);
						S = 1;
					}
				} 
				else if (S == 1) 
				{
					V.add((double) X);
					V.add((double) Y);
					S = 0;
				}
			}
			if (S == 1) 
			{
				V.add((double) X);
				V.add((double) Y);
			}
		} 
		else 
		{
			int X = 0, Y = 0, S = 0;
			
			// found boundaries of semi horizontal lines
			for (int x = 0; x < width; x++)
			{
				int y = (int) ((((this.R - houghHeight) - ((x - Xcenter) * cosTheta)) / sinTheta) + Ycenter);
				if (y < height && y >= 0 && ((image.getRGB(x, y) & 0x000000ff) != 0))
				{
					X = x;
					Y = y;
					if (S == 0) {
						V.add(this.theta);
						V.add(this.R);
						V.add((double) X);
						V.add((double) Y);
						S = 1;
					}
				} 
				else if (S == 1)
				{
					V.add((double) X);
					V.add((double) Y);
					S = 0;
				}
			}
			if (S == 1) 
			{
				V.add((double) X);
				V.add((double) Y);
			}
		}
		return V;
	}
	
	/**
	 * 
     * This function draws a line using the theta and R which is the perpendicular from origin to the line
     * @param image buffered image to draw a line from an equation using theta and R
     * @param color color chosen by the user to draw his new image with it
     * @param height the height of the image
     * @param width the width of the image
     * boundaries of the line
     * @param x1 
     * @param y1
     * @param x2
     * @param y2
     */

	public void Draw(BufferedImage image, int color, int height, int width,	int X1, int Y1, int X2, int Y2) 
	{
		// Find center of the picture
		float Ycenter = height / 2;
		float Xcenter = width / 2;

		// sin & cos used in the equation which will generate the line
		double sinTheta = Math.sin(theta);
		double cosTheta = Math.cos(theta);

		int houghHeight = (int) (Math.sqrt(2) * Math.max(height, width)) / 2;

		if (theta < Math.PI * 0.25 || theta > Math.PI * 0.75) 
		{
			// Draw vertical & semi-vertical lines
			for (int y = Y1; y <= Y2; y++) 
			{
				int x = (int) ((((this.R - houghHeight) - ((y - Ycenter) * sinTheta)) / cosTheta) + Xcenter);
				if (x <= Math.max(X1, X2) && x >= Math.min(X1, X2))
					;
				image.setRGB(x, y, color);
			}
		} 
		else 
		{
			// Draw horizontal & semi-horizontal lines
			for (int x = X1; x <= X2; x++) {
				int y = (int) ((((this.R - houghHeight) - ((x - Xcenter) * cosTheta)) / sinTheta) + Ycenter);
				if (y <= Math.max(Y1, Y2) && y >= Math.min(Y1, Y2))
					image.setRGB(x, y, color);
			}

		}
	}

}
