
import java.io.IOException;
import packages.testpattern;



public class problem {
    public static void main(String args[]) throws IOException{
       testpattern test=new testpattern("circuit.txt");
       test.identifyfault("net_f","SA0");

    }
}