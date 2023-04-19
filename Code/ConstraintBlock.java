import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ConstraintBlock {
    

    //Variable declaration
    public String name;
    public String index;
    public String base_Class;
    public ArrayList<Property> PropertyList;
    public ArrayList<Constraint> ConstraintList;

    public ConstraintBlock(Scanner scanblock,ArrayList<String> list,int k) throws FileNotFoundException{

        // xmi id recovery
        scanblock.findInLine("xmi:id=");
        this.index      = scanblock.next();

        //base_class recovery
        scanblock.findInLine("base_Class=");
        this.base_Class = scanblock.next().replaceAll("/|>", "");

        int namefind = 1;
        ArrayList<Property> Listtemp    = new ArrayList<Property>();
        

            for (int i=0;i<list.size();i++) { //New reading of all lines to find the right block and its attributes

                String line = list.get(i);    //Line recovery

                try (Scanner scantemp = new Scanner(line)) {
                    if (scantemp.findInLine("xmi:type=uml:Class")!=null) {
                        if (namefind==1) {
                            if (scantemp.findInLine(this.base_Class)!=null) {  //Find the constraint block

                                //name
                                scantemp.findInLine("name=");
                                String temp = scantemp.next();
                                this.name  = temp.replaceAll(">|/", "");
                                namefind=2;

                                
                                i++;
                                Scanner scantemp2 = new Scanner(list.get(i));
                                    while (scantemp2.findInLine("packagedElement")==null) { //Properties recovery
                                        if (scantemp2.findInLine("xmi:type=uml:Property")!=null) {
                                            
                                            //properties
                                           //System.out.println("FLAGFLAGFLAG"); 
                                           //System.out.println(list.get(i));
                                           Property tempprop = new Property(scantemp2);
                                           Listtemp.add(tempprop);
                                              
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
    
        this.PropertyList   = Listtemp;

        }


        public void SetOperations(ArrayList<String> list,ArrayList<Property> Listprop){

            ArrayList<Constraint> Listtemp2 = new ArrayList<Constraint>();
            int namefind = 1;

            for (int i=0;i<list.size();i++) { //New reading of all lines to find the right block and its attributes
                String line = list.get(i);    //Line recovery
                try (Scanner scantemp = new Scanner(line)) {
                    if (scantemp.findInLine("xmi:type=uml:Class")!=null) {
                        if (namefind==1) {
                            if (scantemp.findInLine(this.base_Class)!=null) {  //Find the block
                         
                                i++;
                                Scanner scantemp2 = new Scanner(list.get(i));
                                    while (scantemp2.findInLine("packagedElement")==null) { //Properties recovery
                                        if (scantemp2.findInLine("xmi:type=uml:Constraint")!=null) {

                                            //Constraint
                        
                                            Constraint tempconstr = new Constraint(scantemp2,Listprop);
                        
                                            i=i+3;
                                            scantemp2.reset();
                                            scantemp2 = new Scanner(list.get(i));
                                            //System.out.println(list.get(i));
                                            tempconstr.setExpression2(scantemp2);
                                            Listtemp2.add(tempconstr);
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
                this.ConstraintList = Listtemp2;
            
        }
}
