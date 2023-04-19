import java.util.Scanner;

public class Property {
    
    //Attributes declaration
    public String id;
    public String name;
    public Double value;
    

    public Property(){};

    //Constructor

    public Property(Scanner scan){

        //id recovery
        scan.findInLine("xmi:id=");
        this.id   = scan.next();

        //name recovery
        scan.findInLine("name=");
        String temp = scan.next();
        this.name  = temp.replaceAll(">|/", "");

        //Initial value
        this.value = 0.0;
    }

    public void PropertyDisplay(){
        System.out.println("Property Name:"+this.name);
        System.out.println("Property ID:"+this.id);
        System.out.println("Value: ");
        System.out.println(this.value);
    }

    public void setvalue(Scanner scan){
        String temp = scan.next();
        this.value  = Double.valueOf(temp);
    }

    public Property copy(){
        Property temp = new Property();
        temp.id    = this.id;
        temp.name  = this.name;
        temp.value = this.value;
        return temp;
    }
}

