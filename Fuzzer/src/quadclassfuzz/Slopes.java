package quadclassfuzz;

public class Slopes {
	private double slopeAB;
    private double slopeBC;
    private double slopeCD;
    private double slopeDA;
    
    public Slopes(double xB, double yB, double xC, double yC, double xD, double yD) {
    	this.slopeAB = findSlope(0, 0, xB, yB);
        this.slopeBC = findSlope(xB, yB, xC, yC);
        this.slopeCD = findSlope(xC, yC, xD, yD);
        this.slopeDA = findSlope(xD, yD, 0, 0);
    }
    
    public double findSlope(double x1, double y1, double x2, double y2) {
        double slope = ((y2 - y1)/(x2 - x1));
        if(slope == Double.POSITIVE_INFINITY || slope == Double.NEGATIVE_INFINITY) slope = -0;
        return slope;
    }
    
    public boolean isParallelograms() {
        if(this.slopeAB == this.slopeCD && this.slopeBC == this.slopeDA) return true;
        return false;
    }
    
    public boolean isTrapezoid() {
        if(this.slopeAB == this.slopeCD && this.slopeBC != this.slopeDA) return true;
        else if(this.slopeAB != this.slopeCD && this.slopeBC == this.slopeDA) return true;
        return false;
    }

	public boolean isSquare() {
        if(this.slopeAB == -this.slopeBC && this.slopeCD == -this.slopeDA) return true;
        return false;
    }
	
	public double getSlopeAB() {
		return slopeAB;
	}

	public double getSlopeBC() {
		return slopeBC;
	}

	public double getSlopeCD() {
		return slopeCD;
	}

	public double getSlopeDA() {
		return slopeDA;
	}
}
