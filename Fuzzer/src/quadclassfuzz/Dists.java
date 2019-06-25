package quadclassfuzz;

public class Dists {
	private double distAB;
    private double distBC;
    private double distCD;
    private double distDA;
	
	public Dists(double xB, double yB, double xC, double yC, double xD, double yD) {   
	    this.distAB = findDist(0, 0, xB, yB);
	    this.distBC = findDist(xB, yB, xC, yC);
	    this.distCD = findDist(xC, yC, xD, yD);
	    this.distDA = findDist(xD, yD, 0, 0);
	}
	
	public double findDist(double x1, double y1, double x2, double y2) {
	    double powX = Math.pow((x2 - x1), 2);
	    double powY = Math.pow((y2 - y1), 2);
	    double dist = Math.sqrt((powX + powY));
	    return dist;
	}
	
	public boolean isSquareOrRhom() {
	    double sideTotal = 4 * this.distAB;
	    double sum = this.distAB + this.distBC + this.distCD + this.distDA;
	    if(sideTotal == sum) return true;
	    return false;
	}
}
