package drawEqu;

import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.Vector;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * This is the class which forms the hough transform which is a method for
 * detecting lines in an image along with canny's line detection
 * 
 * @author Hoda, Menna
 * 
 */
public class houghTransform  {

	// The size of the neighborhood in which to search for other local maxima
	final int neighborSize = 4;

	// Number of values of theta to be checked
	final int thetaMax = 180;

	// Using thetaMax to get the step
	final double thetaStep = Math.PI / thetaMax;

	// the width and height of the image
	protected int width, height;

	// the hough array
	protected int[][] houghArr;

	// the coordinates of the center of the image
	protected float Xcenter, Ycenter;

	// the height of the hough array
	protected int houghHeight;

	// double the hough height ( for negative values )
	protected int doubleHeight;

	// the number of points that have been added
	protected int numPoints;

	// Values of sin and cos for different theta values.
	private double[] sinValues;
	private double[] cosValues;

	/**
	 * constructor for the class which initialize the width and height with the
	 * dimensions of the image 
	 * @param width
	 * @param height
	 */
	public houghTransform(int width, int height) {

		this.width = width;
		this.height = height;
		initialise();
	}

	/**
	 * Initializes the hough array. Called by the constructor can be used to
	 * reset the transform in case you need to add another image but it has to
	 * be of the same dimension
	 */
	public void initialise() {

		// Calculate the maximum height of the hough array
		houghHeight = (int) (Math.sqrt(2) * Math.max(height, width)) / 2;

		// using double the height value for negative values of R
		doubleHeight = 2 * houghHeight;

		houghArr = new int[thetaMax][doubleHeight];

		// find the center point in the array
		Xcenter = width / 2;
		Ycenter = height / 2;

		// for counting number of points
		numPoints = 0;

		// Collecting the values of sin and cos for faster processing
		sinValues = new double[thetaMax];
		cosValues = sinValues.clone();
		for (int t = 0; t < thetaMax; t++) {
			double actuallTheta = t * thetaStep;
			sinValues[t] = Math.sin(actuallTheta);
			cosValues[t] = Math.cos(actuallTheta);
		}
	}

	/**
	 * @param image
	 *            : the image which is scanned to count the pixels which are
	 *            assumed to be edges and adds them as points the image has to
	 *            be in grey scale mode the image has to be of the same
	 *            dimensions as the one passed to the constructor
	 */
	public void getPoints(BufferedImage image) {

		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {

				// Find non-black pixels
				if ((image.getRGB(x, y) & 0x000000ff) != 0)
					addPoint(x, y);
			}
		}
	}

	/**
	 * adds a single point to the hough array 
	 * @param x
	 * @param y
	 */
	public void addPoint(int x, int y) {

		// Go through each value of theta
		for (int i = 0; i < thetaMax; i++) {

			// find the R values for each theta
			int R = (int) (((x - Xcenter) * cosValues[i]) + ((y - Ycenter) * sinValues[i]));

			// this copes with negative values of r
			R += houghHeight;

			if (R < 0 || R >= doubleHeight)
				continue;

			// Increment the hough array
			houghArr[i][R]++;
		}

		numPoints++;
	}

	/**
	 * This method extracts the lines from the hough array and saves them in a
	 * vector of hough line objects
	 * 
	 * @param threshold
	 *            : to determine the minimum number of intersections between
	 *            curves of hough array
	 * @return vector of hough line objects
	 */
	public Vector<houghLine> getLines(int threshold) {

		Vector<houghLine> Hlines = new Vector<houghLine>(100);

		// Continue if the hough array isn't empty
		if (numPoints == 0)
			return Hlines;

		// Search for local peaks above threshold to draw
		for (int t = 0; t < thetaMax; t++) {
			loop: for (int R = neighborSize; R < doubleHeight - neighborSize; R++) {
				// Only consider points above threshold
				if (houghArr[t][R] > threshold) {

					int peak = houghArr[t][R];

					// Check that this peak is indeed the local maxima
					for (int dx = -neighborSize; dx <= neighborSize; dx++) {
						for (int dy = -neighborSize; dy <= neighborSize; dy++) {
							int dt = t + dx;
							int dr = R + dy;
							if (dt < 0)
								dt = dt + thetaMax;
							else if (dt >= thetaMax)
								dt = dt - thetaMax;
							if (houghArr[dt][dr] > peak) {
								// if a bigger point is found skip
								continue loop;
							}
						}
					}

					// calculate the true value of theta
					double theta = t * thetaStep;

					// add the line to the vector
					Hlines.add(new houghLine(theta, R));

				}
			}
		}

		return Hlines;
	}

	/** 
	 * @return the maximum value in the hough array to be used in drawing hough
	 *         array image
	 */
	public int getMaximumValue() {
		int max = 0;
		for (int i = 0; i < thetaMax; i++) {
			for (int j = 0; j < doubleHeight; j++) {
				if (houghArr[i][j] > max) {
					max = houghArr[i][j];
				}
			}
		}
		return max;
	}

	/**
	 * Gets an image for the hough array in the form of a buffered image with
	 * black color for the curves of hough array and white color for the back
	 * ground just in case you want to.
	 * @return image of an equation which is pre-calculated
	 */
	public BufferedImage formHoughArrImage() {
		int max = getMaximumValue();
		BufferedImage image = new BufferedImage(thetaMax, doubleHeight,
				BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < thetaMax; i++) {
			for (int j = 0; j < doubleHeight; j++) {
				double value = 255 * ((double) houghArr[i][j]) / max;
				int rgb = 255 - (int) value;
				int c = new Color(rgb, rgb, rgb).getRGB();
				image.setRGB(i, j, c);
			}
		}
		return image;
	}

}
