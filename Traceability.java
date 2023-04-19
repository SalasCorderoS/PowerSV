import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import javax.swing.JFileChooser;
import org.apache.commons.text.StringEscapeUtils;



public class Traceability {
    

    public static void main(String[] args) throws IOException {
        

        Parser parser = new Parser();                                          //Parser used to parse the .uml file and recovering information
        ArrayList<Requirement> ALR                     = new ArrayList<Requirement>();        // List containing model requirements
        ArrayList<Block>       ALB                     = new ArrayList<Block>();              // List containing model blocks
        ArrayList<Assumption>  ALA                     = new ArrayList<Assumption>();
        ArrayList<ConstraintBlock> ALC                 = new ArrayList<ConstraintBlock>();
        HashMap<String,Constraint> MapconstBlock       = new HashMap<String,Constraint>();
        HashMap<String,Constraint> MapconstReq         = new HashMap<String,Constraint>();
        HashMap<String,Constraint> MapconstAss         = new HashMap<String,Constraint>();
        LinkedHashMap<String,Constraint> MapconstCB    = new LinkedHashMap<String,Constraint>();
        Checker check1                                 = new Checker();
        HashMap <String,Property>  Mapprop             = new HashMap<String,Property>();

        // 1) ============================================== FILE CHOICE ============================================================
        JFileChooser dialogue = new JFileChooser();
        dialogue.showDialog(null, "Select .uml file" );
        //Recovery of the selected file
        File file           = dialogue.getSelectedFile();
        // ==========================================================================================================================

        // 2) ============================================== FILE READING AND STORAGE ===============================================
        Scanner scan          = new Scanner(file);
        ArrayList<String> ALS = new ArrayList<String>(); //List used to store File lines

        //Read the entire file
        while (scan.hasNextLine()) {
            ALS.add(scan.nextLine().replaceAll("\"", "")); //Line cleaning and adding
       }
       for(String str:ALS){
        str = StringEscapeUtils.unescapeHtml4(str); //translation of mathematical symbols
        //System.out.println(str);
       }
       //==============================================================================================================================

       // 3) ============================================== BLOCKS GENERATION =========================================================
       System.out.println("BLOCKS");
       ALB = parser.processLineByLineBlock2(ALS);

       // Property and Constraint Map generation
       for(Block bl:ALB){
        for(Property pt:bl.PropertyList){
                Mapprop.putIfAbsent(pt.id, pt); // add property
            
        }
        for(Constraint ct: bl.ConstraintList){
            MapconstBlock.putIfAbsent(ct.UMLid, ct); // add constraint
        }
    }
        //System.out.println(Mapprop.size());
        
       
        System.out.println("BLOCKS OK");

        // ============================================================================================================================

        // 4) ================================================ ASSUMPTIONS GENERATION =================================================

        System.out.println("ASSUMPTIONS");
        ALA = parser.processLineByLineAssumptions2(ALS);

       // Property and Constraint Map generation
       for(Assumption as:ALA){
        
        System.out.println(as.propertyassumed.name);
        Mapprop.putIfAbsent(as.propertyassumed.id, as.propertyassumed); // add property
        
    
        
        MapconstAss.putIfAbsent(as.valueconstraint.UMLid, as.valueconstraint); // add constraint
        
    }
       
        System.out.println("ASSUMPTIONS OK");

        // ============================================================================================================================

        // 6) =============================================== CONSTRAINTBLOCK GENERATION ==============================================
        System.out.println("CONSTRAINT BLOCKS");
        ArrayList<Property> Listtempoprop = new ArrayList<Property>(Mapprop.values());
        ALC = parser.processLineByLineCB2(ALS,Listtempoprop);

        // Property and Constraint Map generation
       for(ConstraintBlock cc:ALC){
        
        for(Property pt:cc.PropertyList){
            //System.out.println(pt.name);
            //System.out.println(pt.id);
            Mapprop.putIfAbsent(pt.id, pt); // add property
        }
        for(Constraint ct: cc.ConstraintList){
            MapconstCB.putIfAbsent(ct.UMLid, ct); // add constraint
            //ct.ConstraintDisplay();
        }
        
    }


        System.out.println("CONSTRAINT BLOCKS OK");
        // 7) ================================================ REQUIREMENTS GENERATION ================================================

        System.out.println("REQUIREMENTS");
        ALR = parser.processLineByLineReq2(ALS,Mapprop);


        for(Requirement rq:ALR){
            
            MapconstReq.putIfAbsent(rq.ImposedConstraint.UMLid, rq.ImposedConstraint);
        }

        //for(Requirement rq:ALR){

          //  rq.RequirementDisplay();
        //}

        System.out.println("REQUIREMENTS OK");

        //==========================================================================================================================

        // 8)================================================== TRACEABILITY =======================================================

        System.out.println("TRACEABILITY");
        String again;

        do {
            // 8.1) =============================== OLD VALUES RECOVERY =======================================================
            HashMap<String,Property>   Mapprop1      = (HashMap<String, Property>) Mapprop.clone();
            ArrayList<Property> OldPropList        = new ArrayList<Property>(Mapprop.values());
            // ================================================================================================================

            // 8.2) =============================== ASSUMPTION PROPERTIES VALUES ==============================================


            Scanner scanscan = new Scanner(System.in);

            Mapprop1 = check1.AssumptionParameters(MapconstAss, Mapprop1);

            // =================================================================================================================

            // 8.3) ====================== REQUIREMENT EVALUATION BEFORE PARAMETER CHANGE ======================================

            //System.out.println("EVALUATION BEFORE PARAMETER CHANGE");
            //check1.EvaluateExpressions(MapconstReq,Mapprop);

            // =================================================================================================================

            // 8.4) =========================== PARAMETER CHANGE ===============================================================
            HashMap<String,Property>   Mapprop2= (HashMap<String, Property>) Mapprop1.clone();
            System.out.println("2-Do you want to change a parameter value ? (yes or no)");

            String answer    = scanscan.next();
            String answer2;
            do {

                if (answer.equals("yes")) {
                Mapprop2 = check1.ParameterChange(Mapprop2);
                }
                System.out.println("2-Do you want to change another parameter value ? (yes or no)");
                answer2 = scanscan.next();
            } while (answer2.equals("yes"));

            Mapprop1.clear();

            // =================================================================================================================

            // 8.5) ================================= DISPLAY TABLE BEFORE CALCULATION =========================================

           // ArrayList<Property>   NewPropList2   = new ArrayList<Property>(Mapprop2.values());
           // System.out.println("DISPLAY TABLE BEFORE CALCULATION");
           // check1.ImpactDisplay2(OldPropList, NewPropList2);

            // =================================================================================================================

            // 8.6) =============================== POWER BUDGET CALCULATION ===================================================

            HashMap<String,Property>   Mapprop3= (HashMap<String, Property>) Mapprop2.clone();
            Mapprop2.clear();

            Mapprop3 = check1.ParametersUpdate(MapconstCB, Mapprop3);
            System.out.println("EVALUATION REQUIREMENT CONSTRAINTS AFTER CALCULATION");
            check1.EvaluateExpressions(MapconstReq,Mapprop3);

            // 8.7) ================================== EXCEL FILE ===========================================================

            ArrayList<Property>   NewPropList        = new ArrayList<Property>(Mapprop3.values());
            Mapprop = (HashMap<String, Property>) Mapprop3.clone();
            Mapprop3.clear();

            System.out.println("DISPLAY TABLE AFTER CALCULATION");
            check1.ExcelFile(OldPropList, NewPropList);

            //for(Property proper:NewPropList){
            //    proper.PropertyDisplay();
            //}

            System.out.println("Do you want do a new manipulation ? (yes or no)");
            again = scanscan.next();
        } while (again.equals("yes"));
    }
}
