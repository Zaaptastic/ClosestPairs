import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * PointSet.java
 * 
 * Take in an array of points and determines the closest pair of points. Uses a
 * divide and conquer paradigm with an expected runtime of O(nlogn)
 * 
 * @author Bobby Chen 2016
 */
public class PointSet {
	/**
	 * Callable method that wraps the recursive closestpair() call.
	 * 
	 * @param points
	 *            The array of points containing coordinates of all points to
	 *            consider. Assumes all points are unique.
	 * @return A PointPair object containing the two shortest points. Object
	 *         contains references to the two points constituting the pair as
	 *         well as the distance between them.
	 */
	public static PointPair closestPair(Point2D.Double[] points) {
		// Sorts the array by x-coordinate and then again by y-coordinate. These
		// two sorted arrays are then used by the recursive method to solve the
		// problem.
		Point2D.Double[] sortedByX = sortByX(points);
		Point2D.Double[] sortedByY = sortByY(points);
		long startTime = System.currentTimeMillis();
		PointPair closestTestPair = closestpair(sortedByX, sortedByY);
		long timeToFind = System.currentTimeMillis() - startTime;
		System.out.println("Operation took " + timeToFind + " ms");
		/*
		 * The below code calls the validator to ensure the correct closest pair
		 * has been found. However, doing so calls the bruteforce algorithm,
		 * which has an expected runtime of O(n^2). Therefore, it has been
		 * commented out unless necessary for debugging.
		 * 
		 * PointPair closestCorrectPair = bruteforce(points, 0, points.length);
		 * if (!validate(closestTestPair, closestCorrectPair)) {
		 * System.out.println("Incorrect closest pair calculated, should be " +
		 * closestCorrectPair); } else { System.out.println("Validated!"); }
		 */
		return closestTestPair;
	}

	/**
	 * Below is validation code that compares the distance of the closest pair
	 * found with the divide-and-conquer algorithm with the distance of the
	 * closest pair found with the bruteforce algorithm. Since an assumption of
	 * the incoming array of points is that all points are unique, this is
	 * sufficient to prove that the shortest pair has been found.
	 * 
	 * Note it does not guarantee that the SAME pair has been found, since it is
	 * possible for two different sets of points to have the same distance. Both
	 * are correct return values and thus even if this is the case and the pairs
	 * are not the same, the closest pair has still been found.
	 * 
	 * @param pair1
	 *            One of the two pairs of points to be compared. Does not matter
	 *            which is the result of bruteforce and which is the result of
	 *            the divide-and-conquer algorithm.
	 * @param pair2
	 *            One of the two pairs of points to be compared. Does not matter
	 *            which is the result of bruteforce and which is the result of
	 *            the divide-and-conquer algorithm.
	 * @return True if the pairs has an identical distance. False otherwise.
	 */
	private static boolean validate(PointPair pair1, PointPair pair2) {
		if (pair1.getDistance() == pair2.getDistance())
			return true;
		return false;
	}

	/**
	 * Recursive method that does most of the heavy lifting in the
	 * divide-and-conquer algorithm. Note the two sorted arrays given should
	 * contain the same set of points.
	 * 
	 * @param sortedByX
	 *            Given array of points sorted by x-coordinate.
	 * @param sortedByY
	 *            Given array of points sorted by y-coordinate.
	 * @return A PointPair object containing the closest pair.
	 */
	private static PointPair closestpair(Point2D.Double[] sortedByX,
			Point2D.Double[] sortedByY) {
		// This is our base case, since the shortest path is computable with a
		// constant number of operations (max 6)
		if (sortedByX.length <= 3)
			return bruteforce(sortedByX, 0, sortedByX.length);

		// If we are not in he base case, then the sortedByX array needs to be
		// split in half and closestpair() should be called on each of the
		// subarrays. To do this we create new arrays.
		int start = 0;
		int end = sortedByX.length;
		int middle = (start + end) / 2;

		// x1 - left half of points in sortedByX [0 - middle). Should still be
		// sorted by x-coordinate.
		// x2 - right half of points in sortedByX [middle - end), or [middle -
		// end-1]. Should still be sorted by x-coordinate.
		// y1 - points in x1 sorted by y-coordinate.
		// y2 - points in x2 sorted by y-coordinate.
		// x1Hash - in-between for y1, hashes the points in x1 so that they can
		// be added to y1 in linear time.
		// x2Hash - in-between for y2, hashes the points in x2 so that they can
		// be added to y2 in linear time.
		Point2D.Double[] x1 = new Point2D.Double[middle - start];
		Point2D.Double[] x2 = new Point2D.Double[end - middle];
		Point2D.Double[] y1 = new Point2D.Double[x1.length];
		Point2D.Double[] y2 = new Point2D.Double[x2.length];
		HashSet<Point2D.Double> x1Hash = new HashSet<Point2D.Double>();
		HashSet<Point2D.Double> x2Hash = new HashSet<Point2D.Double>();

		// Goes through each point in sortedByX, adding and hashing the first
		// half to x1 and the second half to x2
		for (int i = start; i < middle; i++) {
			x1[i] = sortedByX[i];
			x1Hash.add(sortedByX[i]);
		}
		for (int i = middle; i < end; i++) {
			x2[i - middle] = sortedByX[i];
			x2Hash.add(sortedByX[i]);
		}

		// Now we iterate through sortedByY and check which HashSet each point
		// is stored in. If we have a match with either x1Hash or x2Hash, we add
		// that point to x1 or x2 respectively. At the end, y1 and y2 should
		// contain all the points x1 and x2 contain respectively, except sorted
		// by y-coordinate.
		int y1Index = 0;
		int y2Index = 0;
		for (int i = start; i < end; i++) {
			if (x1Hash.contains(sortedByY[i])) {
				y1[y1Index++] = sortedByY[i];
			} else if (x2Hash.contains(sortedByY[i])) {
				y2[y2Index++] = sortedByY[i];
			}
		}

		// Now that x1, y1, x2, and y2 are properly populated. Perform the
		// recursive call on the two sets of sorted arrays to find the closest
		// pair in each half. Determine the closer of the two answers.
		PointPair closestLeft = closestpair(x1, y1);
		PointPair closestRight = closestpair(x2, y2);
		PointPair currentClosestPair = (closestLeft.getDistance() < closestRight
				.getDistance()) ? closestLeft : closestRight;

		// Set up the comparison of points in the My set. d is the current
		// closest pair distance. The dividing line is defined as the
		// x-coordinate halfway through the rightmost point of the left array
		// and the leftmost point of the right array.
		double d = currentClosestPair.getDistance();
		double dividingLineXIndex = (x1[x1.length - 1].x + x2[0].x) / 2;
		ArrayList<Point2D.Double> My = new ArrayList<Point2D.Double>();

		// Proceed to add appropriate points to My. Note it is important that
		// they are sorted by y-coordinate and thus we iterate through
		// sortedByY. Points are added if their x-coordinate is within +/- d of
		// the dividing line.
		for (Point2D.Double p : sortedByY) {
			if (p.x >= dividingLineXIndex - d && p.x <= dividingLineXIndex + d)
				My.add(p);
		}

		// My is now populated with points within d of the dividingLine.
		// Reconsider points within this set to be potential closest pairs.

		// 10/3/16: Changed to only consider points that occur below the current
		// point. This insures that the search within My is not n^2 time.
		for (int i = 0; i < My.size(); i++) {
			Point2D.Double p1 = My.get(i);
			for (int j = i; j < My.size(); j++) {
				Point2D.Double p2 = My.get(j);
				if (p1 == p2) {
					continue;
				}
				if (p2.y <= p1.y + d) {
					// Only consider if the points are also within d of each
					// other vertically.
					PointPair tempPair = new PointPair(p1, p2);
					if (tempPair.getDistance() < d) {
						currentClosestPair = tempPair;
					}
				} else {
					// If that's not true, and we are already outside of that
					// range, we will only keep getting farther away. Thus we
					// can just skip to the next point.
					break;
				}
			}
		}

		return currentClosestPair;
	}

	/**
	 * Brute force solution for the closest pairs problem. Considers all pairs
	 * and determines the shortest distance.
	 * 
	 * @param pointList
	 *            The array of points. Sorted order does not matter.
	 * @param start
	 *            Beginning index to consider.
	 * @param end
	 *            Last index to consider (non-inclusive).
	 * @return PointPair object containing the closest pair.
	 */
	private static PointPair bruteforce(Point2D.Double[] pointList, int start,
			int end) {
		PointPair currentClosest = new PointPair(pointList[start],
				pointList[end - 1]);
		for (Point2D.Double p1 : pointList) {
			for (Point2D.Double p2 : pointList) {
				if (p1 != p2) {
					PointPair tempPair = new PointPair(p1, p2);
					if (tempPair.getDistance() < currentClosest.getDistance())
						currentClosest = tempPair;
				}
			}
		}

		return currentClosest;
	}

	/**
	 * Sorts an array of points by their x-coordinate. Does so via recursive
	 * top-down mergesort.
	 * 
	 * @param pointsSubArray
	 *            Subarray of points that are to be sorted.
	 * @return A sorted array of points by x-coordinate.
	 */
	public static Point2D.Double[] sortByX(Point2D.Double[] pointsSubArray) {
		if (pointsSubArray.length < 2)
			return pointsSubArray;

		int start = 0;
		int end = pointsSubArray.length;
		int middle = (start + end) / 2;

		Point2D.Double[] left = new Point2D.Double[middle - start];
		Point2D.Double[] right = new Point2D.Double[end - middle];

		for (int i = start; i < middle; i++)
			left[i] = pointsSubArray[i];

		for (int i = middle; i < end; i++)
			right[i - middle] = pointsSubArray[i];

		left = sortByX(left);
		right = sortByX(right);

		return mergeByX(left, right);
	}

	/**
	 * Identical to sortByX, except by Y-coordinate.
	 */
	public static Point2D.Double[] sortByY(Point2D.Double[] pointsSubArray) {
		if (pointsSubArray.length < 2)
			return pointsSubArray;

		int start = 0;
		int end = pointsSubArray.length;
		int middle = (start + end) / 2;

		Point2D.Double[] left = new Point2D.Double[middle - start];
		Point2D.Double[] right = new Point2D.Double[end - middle];

		for (int i = start; i < middle; i++)
			left[i] = pointsSubArray[i];

		for (int i = middle; i < end; i++)
			right[i - middle] = pointsSubArray[i];

		left = sortByY(left);
		right = sortByY(right);

		return mergeByY(left, right);
	}

	/**
	 * Used by sortByX where it takes two sorted arrays and merges them into one
	 * sorted array.
	 * 
	 * @param left
	 *            One array of points sorted by x-coordinate.
	 * @param right
	 *            Another array of points sorted by x-coordinate.
	 * @return A single array of all points sorted by x-coordinate.
	 */
	private static Point2D.Double[] mergeByX(Point2D.Double[] left,
			Point2D.Double[] right) {
		int currRight = 0;
		int currLeft = 0;
		Point2D.Double[] toReturn = new Point2D.Double[left.length
				+ right.length];

		for (int i = 0; i < toReturn.length; i++) {
			if (currRight >= right.length) {
				toReturn[i] = left[currLeft++];
			} else if (currLeft >= left.length) {
				toReturn[i] = right[currRight++];
			} else if (left[currLeft].x < right[currRight].x) {
				toReturn[i] = left[currLeft++];
			} else {
				toReturn[i] = right[currRight++];
			}
		}
		return toReturn;
	}

	/**
	 * Identical to mergeByX except it merges by y-coordinate
	 */
	private static Point2D.Double[] mergeByY(Point2D.Double[] left,
			Point2D.Double[] right) {
		int currRight = 0;
		int currLeft = 0;
		Point2D.Double[] toReturn = new Point2D.Double[left.length
				+ right.length];

		for (int i = 0; i < toReturn.length; i++) {
			if (currRight >= right.length) {
				toReturn[i] = left[currLeft++];
			} else if (currLeft >= left.length) {
				toReturn[i] = right[currRight++];
			} else if (left[currLeft].y < right[currRight].y) {
				toReturn[i] = left[currLeft++];
			} else {
				toReturn[i] = right[currRight++];
			}
		}
		return toReturn;
	}
}
