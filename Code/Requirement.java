import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class Requirement {
    

    //Variable declaration
    
    public String name;
    public String index;
    public String base_Class;
    public String id;
    public String text;
    public Double TraceableProperty;
    public Constraint ImposedConstraint;

    public Requirement(){

    }

    /**
     * 
     * @param scanreq scanner used in the parser
     * @param list    list containing File lines
     */
    public Requirement(Scanner scanreq,ArrayList<String> list,Map<String,Property> Mappro){

        //id recovery
        scanreq.findInLine("xmi:id=");
        this.index = scanreq.next();
        scanreq.findInLine(" id=");
        this.id    = scanreq.next();
        //text recovery
        scanreq.findInLine("text=");
        this.text  = scanreq.next();

        scanreq.findInLine("base_Class=");
        this.base_Class = scanreq.next().replaceAll("/|>", "");


        int namefind = 1;
    
            for (int i=0;i<list.size();i++) {   //New reading of all lines to find the right block and its attributes
                String line = list.get(i);      //Line recovery

                try (Scanner scantemp = new Scanner(line)) {
                    if (namefind==1) {

                        if (scantemp.findInLine("xmi:type=uml:Class")!=null) {  //Find the requirement

                            if (scantemp.findInLine(this.base_Class)!=null) {

                                //name
                                scantemp.findInLine("name=");
                                this.name = scantemp.next();
                                namefind=2;
                                
                                // Constraint and Property constrained
                                i++;
                                Scanner scantemp2 = new Scanner(list.get(i));

                                    while (scantemp2.findInLine("packagedElement")==null) { //Continue to read the lines as long as the requirement is defined
                                        
                                        if (scantemp2.findInLine("xmi:type=uml:Constraint")!=null) {

                                            //Operation
                                            Constraint tempconstr = new Constraint(scantemp2,Mappro);

                                            i=i+3;
                                            scantemp2.reset();
                                            scantemp2 = new Scanner(list.get(i));
                                            tempconstr.setExpression2(scantemp2);
                                            this.ImposedConstraint = tempconstr;
                                            
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

    public void RequirementDisplay(){
        System.out.println("name:"+this.name);
        System.out.println("UML index:"+this.index);
        System.out.println("base Class:"+this.base_Class);
        System.out.println("id:"+this.id);
        System.out.println("text:"+this.text);
    }
}
