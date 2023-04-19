import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.mariuszgromada.math.mxparser.*;

public class Checker {


    
    public void EvaluateExpressions(Map<String,Constraint> Mapconst,HashMap<String,Property> Mapprop){

        ArrayList<Argument> ArgList = new ArrayList<Argument>();
        for (Map.Entry mapentry : Mapconst.entrySet()) {

            
            Constraint tempconst = (Constraint) mapentry.getValue();
            System.out.println("Expression evaluated:");
            System.out.println(tempconst.expression);
           

            for(Map.Entry mapentry2 : Mapprop.entrySet()){
                Property temp = (Property) mapentry2.getValue();  
               
                Pattern p     = Pattern.compile(temp.name);
                Matcher m     = p.matcher(tempconst.expression);
                    
                boolean test2 = m.find();

                    if (test2 == true) {
                        Argument argtemp = new Argument(temp.name, temp.value);
                        ArgList.add(argtemp);
                        
                    }

            }

            Argument[] Argvector = new Argument[ArgList.size()];
            int i                = 0;
            //Add arguments in function

            for(Argument arg: ArgList){
                Argvector[i]      = arg;
                i++;
            }

            Expression EXptemp = new Expression(tempconst.expression);
            EXptemp.addArguments(Argvector);
           // mXparser.consolePrintln(EXptemp.calculate());
            Double temp = EXptemp.calculate();
    

            if (temp==0.0) {
                System.out.println("Relationship no respected");
            }else{
                System.out.println("Relationship respected");
            }
        }

    }
    

    public HashMap<String,Property> ParameterChange(HashMap<String,Property> Mapprop){

        Scanner scantemp                     = new Scanner(System.in);
        LinkedHashMap<String,Property> Maptemp     = new LinkedHashMap<String,Property>(Mapprop);
        String parameter;
        int check = 1;
        

        do {
            System.out.println("Which parameter do you want change?");
            parameter = scantemp.next();
            
            for(Property prop : Maptemp.values()){
                Property tempprop = new Property();
                tempprop = prop.copy();
                if (parameter.equals(tempprop.name)) {
                    check = 2;
                    //System.out.println("Parameter find");
                    System.out.println("Old value:");
                    System.out.println(prop.value);
                    System.out.println("New value ?");
                    tempprop.value = scantemp.nextDouble();
                    //System.out.println("New value:");
                    //System.out.println(tempprop.value);
                    Maptemp.replace(prop.id, tempprop);
                }
            }
        } while (check !=2);

    return Maptemp;    
    }

    public HashMap<String,Property> ParametersUpdate(LinkedHashMap<String,Constraint> Mapconst,HashMap<String,Property> Mapprop){

        HashMap<String,Property> TempMapprop = new HashMap<String,Property>(Mapprop);
        

        for(Map.Entry mapentry : Mapconst.entrySet()){

            Constraint tempconst = (Constraint) mapentry.getValue();
            
            //Constraint separaration to obtain function
            Scanner scanPU           = new Scanner(tempconst.expression).useDelimiter("=");
            String ParameterModified = scanPU.next();     //Parameter calculated in the function
            ParameterModified        = ParameterModified.replaceAll("\\s", "");

            //System.out.println(ParameterModified);
           
            
            String expression        = scanPU.next();     //Function body
           
            //System.out.println(expression);

            //Argument creation 1
            //System.out.println("ArgDEF DFE DEF DEF");
            ArrayList<Argument> ArgList = new ArrayList<Argument>();
            Property ParaModif          = new Property();

            for(Map.Entry mapentry2 : TempMapprop.entrySet()){
                Property temp = (Property) mapentry2.getValue();
                
                
                boolean test  = ParameterModified.equals(temp.name);
                
                
                if(ParameterModified.equals(temp.name)){
                    ParaModif = temp.copy();
                }else{
                    String expression2 = expression.replace("-", "   ");
                    //System.out.println(expression2);
                    Pattern p     = Pattern.compile(temp.name);
                    Matcher m     = p.matcher(expression2);
                    
                    boolean test2 = m.find();
                    //System.out.println("CHECK CHECK CHECK 3");
                    //System.out.println(test2);
                    if (test2 == true) {
                        Argument argtemp = new Argument(temp.name, temp.value);
                        ArgList.add(argtemp);
                        
                    }
                }
            }

            
            Argument[] Argvector     = new Argument[ArgList.size()];
            Double[] ArgvectorValues = new Double[ArgList.size()];
            int i                = 0;
            //Add arguments in function
            for(Argument arg: ArgList){
                //System.out.println("ADD ARGUMENT");
                Argvector[i]      = arg;
                ArgvectorValues[i]= arg.getArgumentValue();
                i++;
            }

            
            Function function = Addargument(expression, Argvector);
            //System.out.println("Syntax : " + function.checkSyntax());
            //System.out.println("Message : " + function.getErrorMessage());
            //System.out.println("Function Calculation:");
            //System.out.println(function.getFunctionName());
            //System.out.println(function.getFunctionExpressionString());
            //System.out.println(function.getArgumentsNumber());
            //System.out.println("Parameter Modified:");
            //System.out.println(ParaModif.name);
            //mXparser.consolePrintln(function.calculate());
            ParaModif.value = function.calculate();
            //System.out.println("New value:");
            //System.out.println(ParaModif.value);

            TempMapprop.replace(ParaModif.id,ParaModif);

            
            
        }
        return TempMapprop;
    }

    public void ImpactDisplay(HashMap<String,Property> oldMapprop,HashMap<String,Property> newMapprop){

        String[][] TableDisplay         = new String[3][newMapprop.size()+1];
        ArrayList<Property> OldPropList = new ArrayList<Property>();
        ArrayList<Property> NewPropList = new ArrayList<Property>();

        //Table filling
        TableDisplay[0][0]="Parameter Name";
        TableDisplay[1][0]="Old Value";
        TableDisplay[2][0]="New Value";

        for(Map.Entry ME1: oldMapprop.entrySet()){

            Property temp = (Property) ME1.getValue();
            if (temp.name.contains("_delete")== false) {
                OldPropList.add(temp);
            }
    
        }

        for(Map.Entry ME2: newMapprop.entrySet()){

            Property temp = (Property) ME2.getValue();
            if (temp.name.contains("_delete")== false) {
                NewPropList.add(temp);
            }
            
        }
        for (int j = 1; j < newMapprop.size()+1; j++) {
            TableDisplay[0][j]= NewPropList.get(j-1).name;
            TableDisplay[1][j]= OldPropList.get(j-1).value.toString();
            TableDisplay[2][j]= NewPropList.get(j-1).value.toString() ;
        }

        System.out.println("DISPLAY TABLE");

        for (int index = 0; index < 3; index++) {
            
            for (int i = 0; i < newMapprop.size()+1; i++) {
                System.out.print(TableDisplay[index][i]+"        ");
            }
            System.out.println("  ");
        }
    }



    public void ImpactDisplay2(ArrayList<Property> OldPropList,ArrayList<Property> NewPropList){

        String[][] TableDisplay         = new String[3][OldPropList.size()+1];

        //Table filling
        TableDisplay[0][0]="Parameter Name";
        TableDisplay[1][0]="Old Value";
        TableDisplay[2][0]="New Value";

        int u = 1;
        for (int j = 1; j < OldPropList.size()+1; j++) {
            
            if(NewPropList.get(j-1).name.contains("_delete")==false){
                TableDisplay[0][u]= NewPropList.get(j-1).name;
                Double inter1 = (double) Math.round(OldPropList.get(j-1).value*1000)/1000;
                TableDisplay[1][u]= inter1.toString();
                Double inter2 = (double) Math.round(NewPropList.get(j-1).value*1000)/1000;
                TableDisplay[2][u]= inter2.toString() ;
                u++;
            }
            
        }

        System.out.println("DISPLAY TABLE");

        int compteur = (int) Math.round(Math.ceil(u/5));

        for (int index2 = 0; index2 < compteur; index2++) {
            for (int index = 0; index < 3; index++) {
            
                for (int i = index2*5; i < (index2+1)*5; i++) {
                    System.out.print(TableDisplay[index][i]+"        ");
                }
                System.out.println("  ");
            }
        }

        
    }

    public Function Addargument(String exp,Argument[] argvector){

        int size = argvector.length;
        //System.out.println("Number of arguments:");
        //System.out.println(size);
        switch (size) {
            case 1:
            //System.out.println("-----------Case1-----------");
                Argument x1 = argvector[0];
                Function temp1 = new Function("f",exp,x1.getArgumentName());
                temp1.setArgumentValue(0, x1.getArgumentValue());
                mXparser.setToOverrideBuiltinTokens();
                return temp1;
                
            case 2:
            //System.out.println("-----------Case2-----------");
                Argument x2 = argvector[0];
               // System.out.println(x2.getArgumentName());
                //System.out.println(x2.getArgumentValue());

                Argument y2 = argvector[1];
                //System.out.println(y2.getArgumentName());
                //System.out.println(y2.getArgumentValue());

                Function temp2 = new Function("f",exp,x2.getArgumentName(),y2.getArgumentName());
                temp2.setArgumentValue(0, x2.getArgumentValue());
                temp2.setArgumentValue(1, y2.getArgumentValue());

                mXparser.setToOverrideBuiltinTokens();
                return temp2;
            
            case 3:
                Argument x3 = argvector[0];
                Argument y3 = argvector[1];
                Argument z3 = argvector[2];
                Function temp3 = new Function("f",exp,x3.getArgumentName(),y3.getArgumentName(),z3.getArgumentName());
                temp3.setArgumentValue(0, x3.getArgumentValue());
                temp3.setArgumentValue(1, y3.getArgumentValue());
                temp3.setArgumentValue(2, z3.getArgumentValue());
                return temp3;
            case 4:
                Argument x4 = argvector[0];
                Argument y4 = argvector[1];
                Argument z4 = argvector[2];
                Argument w4 = argvector[3];
                Function temp4 = new Function("f",exp,x4.getArgumentName(),y4.getArgumentName(),z4.getArgumentName(),w4.getArgumentName());
                temp4.setArgumentValue(0, x4.getArgumentValue());
                temp4.setArgumentValue(1, y4.getArgumentValue());
                temp4.setArgumentValue(2, z4.getArgumentValue());
                temp4.setArgumentValue(3, w4.getArgumentValue());
                return temp4;
            case 5:
                Argument x5 = argvector[0];
                Argument y5 = argvector[1];
                Argument z5 = argvector[2];
                Argument w5 = argvector[3];
                Argument l5 = argvector[4];
                Function temp5 = new Function("f",exp,x5.getArgumentName(),y5.getArgumentName(),z5.getArgumentName(),w5.getArgumentName(),l5.getArgumentName());
                temp5.setArgumentValue(0, x5.getArgumentValue());
                temp5.setArgumentValue(1, y5.getArgumentValue());
                temp5.setArgumentValue(2, z5.getArgumentValue());
                temp5.setArgumentValue(3, w5.getArgumentValue());
                temp5.setArgumentValue(4, l5.getArgumentValue());
                return temp5;
            default:
            return null;
                
        }
        
    }

    public HashMap<String,Property> AssumptionParameters(Map<String,Constraint> Mapconst,HashMap<String,Property> Mapprop){

        HashMap<String,Property> TempMapprop = new HashMap<String,Property>(Mapprop);

        for(Map.Entry mapentry : Mapconst.entrySet()){

            Constraint tempconst = (Constraint) mapentry.getValue();
            
            //Constraint separaration to obtain function
            Scanner scanPU           = new Scanner(tempconst.expression).useDelimiter("=");
            String ParameterModified = scanPU.next();     //Parameter calculated in the function
            ParameterModified        = ParameterModified.replaceAll("\\s", "");

            //System.out.println(ParameterModified);
            
            String expression        = scanPU.next();     //Function body
            Expression express       = new Expression(expression);
            Property ParaModif       = new Property();


            for(Map.Entry mapentry2 : TempMapprop.entrySet()){

                Property temp = (Property) mapentry2.getValue();
                boolean test  = ParameterModified.equals(temp.name);
                
                
                if(ParameterModified.equals(temp.name)){
                    ParaModif       = temp.copy();
                    //System.out.println(ParaModif.value);
                    ParaModif.value = express.calculate();
                    //System.out.println(ParaModif.value);
                }
            }

            TempMapprop.replace(ParaModif.id,ParaModif);

        }

        return TempMapprop;
    }
    
    public void ExcelFile(ArrayList<Property> OldPropList,ArrayList<Property> NewPropList) {
        int n = 3;
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            //2. Créer une Feuille de calcul vide
            Sheet feuille = wb.createSheet("Display Table");
            //3. Créer une ligne et mettre qlq chose dedans
            Row row = feuille.createRow((short)0);
            System.out.println("OK creation");
            //4. Créer une Nouvelle cellule
            XSSFCellStyle cellStyle = wb.createCellStyle();
            Font font = wb.createFont();
            font.setFontHeightInPoints((short)12);
            font.setBold(true);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setFont(font);
            

            XSSFCellStyle cellStyle2 = wb.createCellStyle();
            Font font2 = wb.createFont();
            font2.setFontHeightInPoints((short)12);
            cellStyle2.setAlignment(HorizontalAlignment.CENTER);
            cellStyle2.setFont(font2);

            for (int index = 0; index < n; index++) {
                Cell cell0 = row.createCell(0+index*n);
                cell0.setCellValue("PARAMETER");
                cell0.setCellStyle(cellStyle);

                Cell cell1 = row.createCell(1+index*n);
                cell1.setCellValue("OLD VALUE");
                cell1.setCellStyle(cellStyle);
        
                Cell cell2 = row.createCell(2+index*n);
                cell2.setCellValue("NEW VALUE");
                cell2.setCellStyle(cellStyle);
            }
            

            int u = 1;
            int j = 1;    
            while (j < OldPropList.size()+1) {
                
                Row rowtemp = feuille.createRow((short)u);

                for (int index = 0; index < n;) {
                    
                    if (j < OldPropList.size()+1) {
                        if (NewPropList.get(j-1).name.contains("_delete")==false) {
                            Cell temp1 = rowtemp.createCell(0+index*n);
                            temp1.setCellValue(NewPropList.get(j-1).name);
                            temp1.setCellStyle(cellStyle);


                            Double inter1 = (double) Math.round(OldPropList.get(j-1).value*1000)/1000;
                            Cell temp2 = rowtemp.createCell(1+index*n);
                            temp2.setCellValue(inter1);
                            temp2.setCellStyle(cellStyle2);

                        
                            Double inter2 = (double) Math.round(NewPropList.get(j-1).value*1000)/1000;
                            Cell temp3 = rowtemp.createCell(2+index*n);
                            temp3.setCellValue(inter2);
                            temp2.setCellStyle(cellStyle2);
                            index++;
                            j++; 
                        } else {
                            j++;
                        }
                    } else {
                       break; 
                    }
                }
                u++;
            }    
            
 
            FileOutputStream fileOut;
            try {
                fileOut = new FileOutputStream("DisplayTable.xlsx");
                wb.write(fileOut);
                fileOut.close();
            } catch (FileNotFoundException e) {
                    e.printStackTrace();
            } catch (IOException e) {
                    e.printStackTrace();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
 }
}
    

    


