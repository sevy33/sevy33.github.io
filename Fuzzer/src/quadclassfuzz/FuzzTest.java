package quadclassfuzz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class FuzzTest {
	private ArrayList<String> errorlog = new ArrayList<String>();
	private ArrayList<String> fuzzErr = new ArrayList<String>();

	public static void main(String[] args) throws IOException {
		FuzzTest fuzz = new FuzzTest();
		ArrayList<String> shapeTest = new ArrayList<String>();
		Random r = new Random();
		
		double xB, yB, xC, yC, xD, yD;
		
		for(int i=0;i < 1000;i++) {
			xB = 0 + (100 - 0) * r.nextDouble();
			yB = 0 + (100 - 0) * r.nextDouble();
			xC = 0 + (100 - 0) * r.nextDouble();
			yC = 0 + (100 - 0) * r.nextDouble();
			xD = 0 + (100 - 0) * r.nextDouble();
			yD = 0 + (100 - 0) * r.nextDouble();
			
			fuzz.checkPoints(xB, yB, xC, yC, xD, yD);
		    Slopes slope = new Slopes(xB, yB, xC, yC, xD, yD);
		    Dists dist = new Dists(xB, yB, xC, yC, xD, yD);
		    shapeTest.add(fuzz.printShape(slope, dist));
		    
		    String fileName = "testCase" + i;
		    File yourFile = new File(fileName);
		    yourFile.createNewFile(); // if file already exists will do nothing 
		    FileOutputStream oFile = new FileOutputStream(yourFile, false);
		    String output = "xA,yA = 0,0 | xB,yB = " + xB + "," + yB + " | xC,yC = " + xC + "," + yC + " | xD,yD = " + xD + "," + yD;
		    oFile.write(output.getBytes());
		    oFile.flush();
		    oFile.close();
		    
		    //PrintWriter writer = new PrintWriter("test" + i + ".txt", "UTF-8");
		    //writer.println("xA,yA = 0,0 xB,yB = " + xB + "," + yB + " xC,yC = " + xC + "," + yC + " xD,y d= " + xD + "," + yD);
		    //writer.close();
		}
		
		// Square
	    xB = 6; yB = 0; xC = 6; yC = 6; xD = 0; yD = 6;
	    fuzz.checkPoints(xB, yB, xC, yC, xD, yD);
	    Slopes slope = new Slopes(xB, yB, xC, yC, xD, yD);
	    Dists dist = new Dists(xB, yB, xC, yC, xD, yD);
	    shapeTest.add(fuzz.printShape(slope, dist));
	    
	    // Rectangle
	    xB = 6; yB = 0; xC = 6; yC = 10; xD = 0; yD = 10;
	    fuzz.checkPoints(xB, yB, xC, yC, xD, yD);
	    slope = new Slopes(xB, yB, xC, yC, xD, yD);
	    dist = new Dists(xB, yB, xC, yC, xD, yD);
	    shapeTest.add(fuzz.printShape(slope, dist));
	    
	    // Rhombus
	    xB = 5; yB = 0; xC = 8; yC = 4; xD = 3; yD = 4;
	    fuzz.checkPoints(xB, yB, xC, yC, xD, yD);
	    slope = new Slopes(xB, yB, xC, yC, xD, yD);
	    dist = new Dists(xB, yB, xC, yC, xD, yD);
	    shapeTest.add(fuzz.printShape(slope, dist));
	    
	    // Trapezoid
	    xB = 6; yB = 0; xC = 4; yC = 5; xD = 2; yD = 5;
	    fuzz.checkPoints(xB, yB, xC, yC, xD, yD);
	    slope = new Slopes(xB, yB, xC, yC, xD, yD);
	    dist = new Dists(xB, yB, xC, yC, xD, yD);
	    shapeTest.add(fuzz.printShape(slope, dist));
	    
	    // Kite
	    xB = 6; yB = 0; xC = 8; yC = 4; xD = 2; yD = 6;
	    fuzz.checkPoints(xB, yB, xC, yC, xD, yD);
	    slope = new Slopes(xB, yB, xC, yC, xD, yD);
	    dist = new Dists(xB, yB, xC, yC, xD, yD);
	    shapeTest.add(fuzz.printShape(slope, dist));
	    
	    // Invalid number between 0 and 100
	    xB = -1; yB = 0; xC = 8; yC = 4; xD = 2; yD = 6;
	    fuzz.checkPoints(xB, yB, xC, yC, xD, yD);
	    slope = new Slopes(xB, yB, xC, yC, xD, yD);
	    dist = new Dists(xB, yB, xC, yC, xD, yD);
	    shapeTest.add(fuzz.printShape(slope, dist));
	    
	    // Two Points the same
	    xB = 6; yB = 0; xC = 5; yC = 3; xD = 5; yD = 3;
	    fuzz.checkPoints(xB, yB, xC, yC, xD, yD);
	    slope = new Slopes(xB, yB, xC, yC, xD, yD);
	    dist = new Dists(xB, yB, xC, yC, xD, yD);
	    shapeTest.add(fuzz.printShape(slope, dist));
	    
	    // Linear Cordinates
	    xB = 2; yB = 2; xC = 4; yC = 4; xD = 6; yD = 6;
	    fuzz.checkPoints(xB, yB, xC, yC, xD, yD);
	    slope = new Slopes(xB, yB, xC, yC, xD, yD);
	    dist = new Dists(xB, yB, xC, yC, xD, yD);
	    shapeTest.add(fuzz.printShape(slope, dist));
	    
	    // Point C line cross
	    xB = 4; yB = 4; xC = 6; yC = 2; xD = 2; yD = 4;
	    fuzz.checkPoints(xB, yB, xC, yC, xD, yD);
	    slope = new Slopes(xB, yB, xC, yC, xD, yD);
	    dist = new Dists(xB, yB, xC, yC, xD, yD);
	    shapeTest.add(fuzz.printShape(slope, dist));
	    
	    // Point D line cross
	    xB = 0; yB = 6; xC = 6; yC = 6; xD = 8; yD = 4;
	    fuzz.checkPoints(xB, yB, xC, yC, xD, yD);
	    slope = new Slopes(xB, yB, xC, yC, xD, yD);
	    dist = new Dists(xB, yB, xC, yC, xD, yD);
	    shapeTest.add(fuzz.printShape(slope, dist));
	    
	    //for(String s : shapeTest) {
	    	//System.out.println(s);
	    //}
		
		
		if(fuzz.getFuzzerSize() > 0) System.out.println("Error");
		else System.out.println("OK");
	}
	
	public void checkPoints(double xB, double yB, double xC, double yC, double xD, double yD) {
		try {
			ArrayList<Double> points = new ArrayList<Double>();
			points.add(0.0);
		    points.add(0.0);
		    points.add(xB);
		    points.add(yB);
		    points.add(xC);
		    points.add(yC);
		    points.add(xD);
		    points.add(yD);
		    
		    for(int i=0;i < points.size();i++) {
		        //if(points[i] == NULL) throw "Invalid number of points";
		        if(points.get(i) < 0 || points.get(i) > 100) errorlog.add("Values must be between 0 and 100");
		    }
		    
		    int i = 0;
		    while(i < points.size()) {
		        double xCheck = points.get(i);
		        double yCheck = points.get(i+1);
		        
		        int j = i + 2;
		        while(j < points.size()) {
		            if(xCheck == points.get(j) && yCheck == points.get(j+1)) errorlog.add("Coordinates cannot be the same");
		            j+=2;
		        }
		        i+=2;
		    }
		    
		    Slopes slope = new Slopes(xB, yB, xC, yC, xD, yD);
		    
		    if(slope.getSlopeAB() == slope.getSlopeBC() && slope.getSlopeBC() == slope.getSlopeCD()) {
		        if(slope.getSlopeAB() + slope.getSlopeBC() + slope.getSlopeBC() + slope.getSlopeDA() != 0) errorlog.add("Points cannot be colinear");
		    }
		    
		    if(xC > xB && yC < yB) errorlog.add("Lines cannot cross");
		    if(xD > xC && yD < yC) errorlog.add("Lines cannot cross");
		    
		} catch(Exception e) {
			fuzzErr.add(e.toString());
		}
	}
	
	public String printShape(Slopes slope, Dists dist) {
	    if(slope.isParallelograms()) {
	        if(dist.isSquareOrRhom()) {
	            if(slope.isSquare()) return "Square";
	            else return "Rhombus";
	        } else {
	            return "Rectangle";
	        }
	    } else {
	        if(slope.isTrapezoid()) return "Trapezoid";
	        else return "Kite";
	    }
	}
	
	public int getFuzzerSize() {
		return fuzzErr.size();
	}

}
