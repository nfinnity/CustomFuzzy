package customFuzzy.project;

import java.util.Scanner;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Rule;

public class Main {
	
    public static void main(String[] args) throws Exception {

        // Set inputs from user
        Scanner reader = new Scanner(System.in);
        // Load from 'FCL' file
        System.out.println("Which distribution would you like (triangle, trapezoidal, or Gaussian): ");
        String fileName = reader.next();
        if (fileName.equals("triangle"))
        	fileName = "motorSpeedTriangle.fcl";
        else if (fileName.equals("trapezoidal"))
        	fileName = "motorSpeedTrap.fcl";
        else if (fileName.equals("Gaussian"))
        	fileName = "motorSpeedGauss.fcl";
        else 
        {
        	System.out.println("Not a valid input. The default triangle was selected.");
        	fileName = "motorSpeedTriangle.fcl";
        }
        
        FIS fis = FIS.load(fileName,true);

        // Error while loading?
        if( fis == null ) { 
            System.err.println("Can't load file: '" + fileName + "'");
            System.exit(1);
        }

        // Show 
        FunctionBlock block = fis.getFunctionBlock(null);
        System.out.println("Enter a current temperature value (0-9): ");
        double currTemp = reader.nextDouble();
        System.out.println("Enter a target temperature setting (0-9): ");
        double targetTemp = reader.nextDouble();
        block.setVariable("currentTemp", currTemp);
        block.setVariable("targetTemp", targetTemp);
        
        
        
        // Evaluate
        block.evaluate();

        // Show output variable's chart
        block.getVariable("motorOutput").defuzzify();

        // Print ruleSet
        System.out.println("Motor Output: " + block.getVariable("motorOutput").getValue());
        System.out.println("currentTemp Output: " + block.getVariable("currentTemp").getValue());
        System.out.println("TargetTemp Output: " + block.getVariable("targetTemp").getValue());
    
        JFuzzyChart.get().chart(block);
        
        double motorOutput = block.getVariable("motorOutput").getValue();
        double motorSpeed =  Math.abs(motorOutput - 12.5);
        String type = "";
        if (motorOutput < 12.5)
        	type = "cooling speed (in rpm)";
        else
        	type = "heating speed (in rpm)";
        motorSpeed *= 400;
        System.out.println("Resulting fan speed: " + Math.round(motorSpeed) + " " + type);
        
        System.out.println("Rules: ");
        for( Rule r : fis.getFunctionBlock("fanSpeed").getFuzzyRuleBlock("No1").getRules() )
            System.out.println(r);
    
    }
}
