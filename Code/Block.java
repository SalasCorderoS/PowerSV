import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Block{
    
    //Variable declaration
    public String name;
    public String index;
    public String base_Class;
    public ArrayList<Property> PropertyList;
    public ArrayList<Constraint> ConstraintList;


    /**
     * Constructor
     * @param scanblock scanner used in the parser
     * @param list      list containing File lines 
     * @throws FileNotFoundException
     */
    public Block(Scanner scanblock,ArrayList<String> list) throws FileNotFoundException{

        // xmi id Recovery
        scanblock.findInLine("xmi:id=");
        this.index      = scanblock.next();

        //base_class Recovery
        scanblock.findInLine("base_Class=");
        this.base_Class = scanblock.next().replaceAll("/|>", "");

        //Properties and Constraints recovery
        boolean namefind = true;                                         //Boolean checker
        ArrayList<Property> Listtemp    = new ArrayList<Property>();     //Temporary Property list
        ArrayList<Constraint> Listtemp2 = new ArrayList<Constraint>();   //Temporary Constraint list

            for (int i=0;i<list.size();i++) { //New reading of all lines to find the right block and its attributes

                String line = list.get(i);    //Line recovery

                try (Scanner scantemp = new Scanner(line)) {
                    if (scantemp.findInLine("xmi:type=uml:Class")!=null) { //pattern check
                        if (namefind) {
                            if (scantemp.findInLine(this.base_Class)!=null) {      //Base class recovery

                                //name recovery
                                scantemp.findInLine("name=");
                                String temp = scantemp.next();
                                this.name  = temp.replaceAll(">|/", "");
                                namefind=false;

                                
                                i++;                                               //Necessary indentation



                                Scanner scantemp2 = new Scanner(list.get(i));     //New Scanner

                                    while (scantemp2.findInLine("packagedElement")==null) {

                                        if (scantemp2.findInLine("xmi:type=uml:Property")!=null) {
                                            
                                            //properties recovery
                                           Property tempprop = new Property(scantemp2); //Property creation
                                           Listtemp.add(tempprop);
                                              
                                        }else if (scantemp2.findInLine("xmi:type=uml:Constraint")!=null) {

                                            //Constraint

                                            Constraint tempconstr = new Constraint(scantemp2,Listtemp); //Constraint creation

                                            i=i+3;

                                            scantemp2.reset();
                                            scantemp2 = new Scanner(list.get(i));
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

        this.PropertyList   = Listtemp;
        this.ConstraintList = Listtemp2;
        }
            
        public Block(Scanner scanblock,ArrayList<String> list,int k) throws FileNotFoundException{

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
                                if (scantemp.findInLine(this.base_Class)!=null) {  //Find the block
    
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

    public void BlockDisplay(){
        System.out.println("name:"+this.name);
        System.out.println("UML index:"+this.index);
        System.out.println("base_Class:"+this.base_Class);
        
        for(Property i:PropertyList){
            i.PropertyDisplay();
        }

        for(Constraint u:ConstraintList){
            u.ConstraintDisplay();
        }
    }

     

    

}
