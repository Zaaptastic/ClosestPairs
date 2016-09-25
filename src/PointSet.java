import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;

public class PointSet {
	public static String closestPair(Point2D.Double[] points){
		for (Point2D.Double p : points){
			System.out.println(p);
		}
		System.out.println("~~~~");
		//1) sort by x
		//2) sort by y
		//3) call recursive head
		
		Point2D.Double[] sortedByX = sortByX(points);
		Point2D.Double[] sortedByY = sortByY(points);
		
		return closestpair(sortedByX, sortedByY).toString();
	}
	
	private static PointPair closestpair(Point2D.Double[] sortedByX, Point2D.Double[] sortedByY){
		if (sortedByX.length <= 3)
			return bruteforce(sortedByX, 0, sortedByX.length);

		int start = 0;
		int end = sortedByX.length;
		int middle = (start + end) / 2;
		
		Point2D.Double[] x1 = new Point2D.Double[middle-start];
		Point2D.Double[] x2 = new Point2D.Double[end-middle];
		Point2D.Double[] y1 = new Point2D.Double[x1.length];
		Point2D.Double[] y2 = new Point2D.Double[x2.length];
		HashSet<Point2D.Double> x1Hash = new HashSet<Point2D.Double>();
		HashSet<Point2D.Double> x2Hash = new HashSet<Point2D.Double>();
		
		for (int i = start; i < middle; i++){
			x1[i] = sortedByX[i];
			x1Hash.add(sortedByX[i]);
		}
		for (int i = middle; i < end; i++){
			x2[i-middle] = sortedByX[i];
			x2Hash.add(sortedByX[i]);
		}
		
		int y1Index = 0;
		int y2Index = 0;
		for (int i = start; i < end; i++){
			if (x1Hash.contains(sortedByY[i])){
				y1[y1Index++] = sortedByY[i];
			}else if (x2Hash.contains(sortedByY[i])){
				y2[y2Index++] = sortedByY[i];
			}
		}
		
		PointPair closestLeft = closestpair(x1, y1);
		PointPair closestRight = closestpair(x2, y2);
		PointPair currentClosestPair = (closestLeft.getDistance() < closestRight.getDistance()) ? 
				closestLeft : closestRight;
		double d = currentClosestPair.getDistance();
		double dividingLineXIndex = (x1[x1.length-1].x + x1[0].x) / 2;
		ArrayList<Point2D.Double> My = new ArrayList<Point2D.Double>();
		
		for (Point2D.Double p : sortedByY){
			if (p.x >= dividingLineXIndex - d && p.x <= dividingLineXIndex + d)
				My.add(p);
		}
		
		//My is now populated with points within d of the dividingLine. Reconsider points within this set
		for (Point2D.Double p1 : My){
			for (Point2D.Double p2 : My){
				if (p1 == p2){
					continue;
				}
				if (p2.y <= p1.y + d){
					PointPair tempPair = new PointPair(p1, p2);
					if (tempPair.getDistance() < d){
						currentClosestPair = tempPair;
					}
				}else{
					break;
				}
			}
		}
		
		return currentClosestPair;
	}
	
	private static PointPair bruteforce(Point2D.Double[] pointList, int start, int end){
		PointPair currentClosest = new PointPair(pointList[start], pointList[end-1]);
		for (Point2D.Double p1 : pointList){
			for (Point2D.Double p2 : pointList){
				if (p1 != p2){
					PointPair tempPair = new PointPair(p1, p2);
					if (tempPair.getDistance() < currentClosest.getDistance())
						currentClosest = tempPair;
				}
			}
		}
		
		return currentClosest;
	}
	
	public static Point2D.Double[] sortByX(Point2D.Double[] pointsSubArray){
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
			right[i-middle] = pointsSubArray[i];
		
		left = sortByX(left);
		right = sortByX(right);
		
		return mergeByX(left, right);
	}
	
	public static Point2D.Double[] sortByY(Point2D.Double[] pointsSubArray){
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
			right[i-middle] = pointsSubArray[i];

		left = sortByY(left);
		right = sortByY(right);
		
		return mergeByY(left, right);
	}
	
	private static Point2D.Double[] mergeByX(Point2D.Double[] left, Point2D.Double[] right){
		int currRight = 0;
		int currLeft = 0;
		Point2D.Double[] toReturn = new Point2D.Double[left.length + right.length];
		
		for (int i = 0; i < toReturn.length; i++){
			if (currRight >= right.length){
				toReturn[i] = left[currLeft++];
			}else if (currLeft >= left.length){
				toReturn[i] = right[currRight++];
			}else if (left[currLeft].x < right[currRight].x){
				toReturn[i] = left[currLeft++];
			}else{
				toReturn[i] = right[currRight++];
			}
		}
		return toReturn;
	}
	
	private static Point2D.Double[] mergeByY(Point2D.Double[] left, Point2D.Double[] right){
		int currRight = 0;
		int currLeft = 0;
		Point2D.Double[] toReturn = new Point2D.Double[left.length + right.length];
		
		for (int i = 0; i < toReturn.length; i++){
			if (currRight >= right.length){
				toReturn[i] = left[currLeft++];
			}else if (currLeft >= left.length){
				toReturn[i] = right[currRight++];
			}else if (left[currLeft].y < right[currRight].y){
				toReturn[i] = left[currLeft++];
			}else{
				toReturn[i] = right[currRight++];
			}
		}
		return toReturn;
	}
}
