import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class Parser {
    

    public Parser(){

    }

    /**
     * 
     * @param list  list containing File lines
     * @return   Block List
     * @throws IOException
     */

    public ArrayList<Block> processLineByLineBlock2(ArrayList<String> list) throws IOException {

        ArrayList<Block> ClassArrayBlock = new ArrayList<Block>();
        ArrayList<Property> PropArray    = new ArrayList<Property>();

        for(String i:list){                                            //Reading each line of the list
            Scanner scanner = new Scanner(i);
            if (scanner.findInLine("<Blocks:Block") != null) { //Indication that a block exists

                Block Blocktemp = new Block(scanner,list,1);        //Block generation
                
                ClassArrayBlock.add(Blocktemp);                       //Adding the block to the list
            }
            
        }
        for(Block bl:ClassArrayBlock){
            PropArray.addAll(bl.PropertyList);
        }
        
        for(Block bl:ClassArrayBlock){
            bl.SetOperations(list, PropArray);
        }
        return ClassArrayBlock;  //Return Constraint Block list
        
      }

      public ArrayList<ConstraintBlock> processLineByLineCB2(ArrayList<String> list,ArrayList<Property> listproperty) throws IOException {

        ArrayList<ConstraintBlock> ClassArrayBlock = new ArrayList<ConstraintBlock>();
        ArrayList<Property> PropArray    = new ArrayList<Property>();

        for(String i:list){                                            //Reading each line of the list
            Scanner scanner = new Scanner(i);
            if (scanner.findInLine("<ConstraintBlocks:ConstraintBlock") != null) { //Indication that a constraint block exists

                ConstraintBlock Blocktemp = new ConstraintBlock(scanner,list,1);        //ConstraintBlock generation
                
                ClassArrayBlock.add(Blocktemp);                       //Adding the block to the list
            }
            
        }
        PropArray.addAll(listproperty);
        for(ConstraintBlock bl:ClassArrayBlock){
            PropArray.addAll(bl.PropertyList);
        }
        
        for(ConstraintBlock bl:ClassArrayBlock){
            bl.SetOperations(list, PropArray);
        }
        return ClassArrayBlock;  //Return Block list
        
      }
      /**
     * 
     * @param list  list containing File lines
     * @return   Block List
     * @throws IOException
     */

    public ArrayList<Assumption> processLineByLineAssumptions2(ArrayList<String> list) throws IOException {

        ArrayList<Assumption> ClassArrayAss = new ArrayList<Assumption>();
        ArrayList<Property> PropArray    = new ArrayList<Property>();

        for(String i:list){                                            //Reading each line of the list
            Scanner scanner = new Scanner(i);
            if (scanner.findInLine("<AssumptionProfile:Assumption") != null) { //Indication that an assumption exists

                Assumption Asstemp = new Assumption(scanner,list,1);        //Assumption generation
                
                ClassArrayAss.add(Asstemp);                       //Adding the block to the list
            }
            
        }
        
        
        for(Assumption as :ClassArrayAss){
            as.SetOperations(list);
        }
        return ClassArrayAss;  //Return Assumption list
        
      }



       /**
     * 
     * @param list list containing File lines
     * @param Mappro map containing all properties
     * @return
     * @throws IOException
     */
    public ArrayList<Requirement> processLineByLineReq2(ArrayList<String> list,Map<String,Property> Mappro) throws IOException {

        ArrayList<Requirement> ClassArrayReq   = new ArrayList<Requirement>();

        for(String i:list){                                                        //Reading each line of the list
            Scanner scanner = new Scanner(i);
            if (scanner.findInLine("Requirements:Requirement") != null) { //Indication that a requirement exists
                Requirement Reqtemp = new Requirement(scanner,list,Mappro);        //Requirement generation
                ClassArrayReq.add(Reqtemp);
            }
        }
        return ClassArrayReq;  
        
      }
}
