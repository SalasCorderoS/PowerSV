import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.text.StringEscapeUtils;
import org.mariuszgromada.math.mxparser.*;


public class Constraint {

    //Variable declaration
    public String name;
    public String UMLid;
    public String expression;
    public ArrayList<String> CPI             = new ArrayList<String>(); //Constrained properties id
    public ArrayList<Property> PropConstList = new ArrayList<Property>();
    public Expression function;
    
    /**
     * 
     * @param scan
     * @param listprop
     */

    public Constraint(Scanner scan,ArrayList<Property> Listprop){

        // xmi id
        scan.findInLine("xmi:id=");
        UMLid     = scan.next();

        // name
        scan.findInLine("name=");
        this.name = scan.next();

        //constrainedElement id
        scan.findInLine("constrainedElement=");
        while (scan.hasNext()) {
            this.CPI.add(scan.next().replaceAll("/|>", ""));
        }

        //constrainedElement
        for(String idCPI:this.CPI){
            Property temp = findConstrainedElement(idCPI, Listprop);
            this.PropConstList.add(temp);
        }
        
          
    
}

public Constraint(Scanner scan, Property Listprop){

    // xmi id
    scan.findInLine("xmi:id=");
    UMLid     = scan.next();

    // name
    scan.findInLine("name=");
    this.name = scan.next();


    //constrainedElement
    this.PropConstList.add(Listprop);
    
      

}
    public Constraint(Scanner scan, Map<String,Property> Mappro){

            // xmi id
            scan.findInLine("xmi:id=");
            UMLid     = scan.next();

            // name
            scan.findInLine("name=");
            this.name = scan.next();

            //constrainedElement id
            scan.findInLine("constrainedElement=");
            while (scan.hasNext()) {
                this.CPI.add(scan.next().replaceAll("/|>", ""));
            }

         //constrainedElement
        for(String idCPI:this.CPI){
            Property temp = Mappro.get(idCPI);
            this.PropConstList.add(temp);
        }   
              
        
    }

    

    public void setExpression(Scanner scan){
    
        scan.findInLine("value=");
        this.expression        = scan.next();
        this.function = new Expression(this.expression);

        for (int i = 0; i < this.PropConstList.size(); i++) {
               
            Argument temp = new Argument(this.PropConstList.get(i).name,this.PropConstList.get(i).value); // Arguments definition
            this.function.addArguments(temp);                                                             // Arguments addition
        }
        
          
    }

    public void setExpression2(Scanner scan){
    
        String str      = scan.nextLine().replaceAll("<body>|</body>", "");
        str             = StringEscapeUtils.unescapeHtml4(str);
        this.expression = str.replaceAll("\\s", "");
        
        //System.out.println(this.expression);
        this.function   = new Expression(this.expression);
        //System.out.println(this.PropConstList.size());
        for (int i = 0; i < this.PropConstList.size(); i++) {
            //System.out.println(this.PropConstList.get(i).name);
            Argument temp = new Argument(this.PropConstList.get(i).name,this.PropConstList.get(i).value); // Arguments definition
            this.function.addArguments(temp);                                                             // Arguments addition
        }
        
          
    }


    public Property findConstrainedElement(String idtest, ArrayList<Property> proplist){

        int index           = 0;
        Property returnprop = new Property();

            while (index<proplist.size()) {
                if (proplist.get(index).id.equals(idtest)) {
                    returnprop = proplist.get(index);
                    index++;
    
                } else {
                    index++;
                }
            }
          
        
        return returnprop;
    }

    public void ConstraintDisplay() {
        System.out.println("Constraint name:"+this.name);
        System.out.println("Expression:"+this.expression);
        for (int i = 0; i < this.PropConstList.size(); i++) {
            System.out.println("Constrained Element:"+this.PropConstList.get(i).name);
            System.out.println("Value:");
            System.out.println(this.PropConstList.get(i).value);
        }
        
    }
    
}
