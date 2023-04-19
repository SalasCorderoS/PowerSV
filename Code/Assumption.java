import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Assumption {
    

    //Variable declaration
    public String name;
    public String index;
    public String base_Class;
    public Property propertyassumed;
    public Constraint valueconstraint;

    /**
     * Constructor
     * @param scanblock scanner used in the parser
     * @param list      list containing File lines 
     * @throws FileNotFoundException
     */
    


        public Assumption(Scanner scanblock,ArrayList<String> list,int k) throws FileNotFoundException{

            // xmi id recovery
            scanblock.findInLine("xmi:id=");
            this.index      = scanblock.next();
    
            //base_class recovery
            scanblock.findInLine("base_Class=");
            this.base_Class = scanblock.next().replaceAll("/|>", "");
    
            int namefind = 1;
           
            
    
                for (int i=0;i<list.size();i++) { //New reading of all lines to find the right assumption and its attributes

                    String line = list.get(i);    //Line recovery
                    try (Scanner scantemp = new Scanner(line)) {

                        if (scantemp.findInLine("xmi:type=uml:Class")!=null) {
                            if (namefind==1) {
                                if (scantemp.findInLine(this.base_Class)!=null) {  //Find the assumption
    
                                    //name
                                    scantemp.findInLine("name=");
                                    String temp = scantemp.next();
                                    this.name  = temp.replaceAll(">|/", "");
                                    //System.out.println(this.name);
                                    namefind=2;
    
                                    
                                    i++;
                                    Scanner scantemp2 = new Scanner(list.get(i));
                                        while (scantemp2.findInLine("packagedElement")==null) { //Properties recovery
                                            if (scantemp2.findInLine("xmi:type=uml:Property")!=null) {
                                                
                                                //properties
                                               this.propertyassumed = new Property(scantemp2);
                                               //this.propertyassumed.PropertyDisplay();
                                                  
                                            }
                                            i++;
                                            scantemp2.reset();
                                            scantemp2 = new Scanner(list.get(i));
                                            
                                        }
                                    }
                                    
                                    
                                }
                            
                                
                            }
                        }
                        
                    }

            }






        public void SetOperations(ArrayList<String> list){


            int namefind = 1;

            for (int i=0;i<list.size();i++) { //New reading of all lines to find the right assumption and its attributes

                String line = list.get(i);    //Line recovery

                try (Scanner scantemp = new Scanner(line)) {
                    if (scantemp.findInLine("xmi:type=uml:Class")!=null) {
                        if (namefind==1) {
                            if (scantemp.findInLine(this.base_Class)!=null) {  //Find the assumption
                         
                                i++;
                                Scanner scantemp2 = new Scanner(list.get(i));
                                    while (scantemp2.findInLine("packagedElement")==null) { //Constraint recovery

                                        if (scantemp2.findInLine("xmi:type=uml:Constraint")!=null) {

                                            //Constraint
                        
                                            this.valueconstraint = new Constraint(scantemp2,this.propertyassumed);
                        
                                            i=i+3;
                                            scantemp2.reset();
                                            scantemp2 = new Scanner(list.get(i));
                                            //System.out.println(list.get(i));
                                            this.valueconstraint.setExpression2(scantemp2);
                                        }
                                        i++;
                                        scantemp2.reset();
                                        scantemp2 = new Scanner(list.get(i));
                                        
                                    }
                                }
                                
                                
                            }
                        
                            
                        }
                    }
                    
                }
            
        }

        public void AssumptionDisplay(){
            System.out.println("name:"+this.name);
            System.out.println("UML index:"+this.index);
            System.out.println("base_Class:"+this.base_Class);
            this.propertyassumed.PropertyDisplay();
            this.valueconstraint.ConstraintDisplay();

        }
}
