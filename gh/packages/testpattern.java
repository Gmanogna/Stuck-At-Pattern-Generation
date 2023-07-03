package packages;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;




public class testpattern {
    private String faultNodeLocation;
    private String faultType;
    private FileWriter outputFile;
    Circuit circuit=new Circuit();

    public testpattern(String file){
        circuit.createCircuit(file);
    }
   
   
    
    public ArrayList<ArrayList<String>> estinputs= new ArrayList<>();
    private void traverse(String[] inputVectors) throws IOException{
        for(int i=0;i<inputVectors.length;i++){
            circuit.simulateCircuit(inputVectors[i]);
            int faultNodeoutput =circuit.getOutput(faultNodeLocation);
            int expectedoutput=faultType.equals("SA0")? 0:1;
        
            if(faultNodeoutput!=expectedoutput){
                circuit.simulateCircuit(inputVectors[i]);
                String ans=String.valueOf(circuit.getOutput("Z"));
                estinputs.add(new ArrayList<>(Arrays.asList(inputVectors[i], ans)));
            }
        }
       
        
    }
    public void finalans(String[] inputVectors) throws IOException{
         traverse(inputVectors);
          
         int expectedoutput=faultType.equals("SA0")? 0:1;
         for(ArrayList<String> e:estinputs){
            circuit.setValueCompute(faultNodeLocation,expectedoutput,e.get(0));
            int val=Integer.parseInt(e.get(1));
            if(val!=circuit.getOutput("Z")){
                printTestPattern(e.get(0));
                break;
               
            }
         }
    }
    private void printTestPattern(String vector) throws IOException {
        char[] ips=new char[(int) circuit.getNumberOfinputs()];
        for(int i=0;i<circuit.getNumberOfinputs();i++){
           ips[i]=(char)('A'+i);
        }
        char[] vec=vector.toCharArray();
        outputFile.write(Arrays.toString(ips)+" = "+Arrays.toString(vec)+" ,");
        outputFile.write("Z = "+ String.valueOf(circuit.getOutput("Z")) + "\n");
        outputFile.write("\n");
    }

    public void identifyfault(String faultNode,String faultType) throws IOException{
        this.faultType=faultType;
        this.faultNodeLocation=faultNode;

        String[] inputVectors=generateInputVectors(circuit.getNumberOfinputs());
        
       
       outputFile = new FileWriter("output.txt");
       finalans(inputVectors);
       outputFile.close();
    }
    public String[] generateInputVectors(int size) {
        int n = (int) Math.pow(2, size);
        String[] inputs = new String[n];
        for (int i = 0; i < n; i++) {
            inputs[i]=(String.format("%" + size + "s", Long.toBinaryString(i))
                    .replace(' ', '0'));
        }
      return inputs;
    }
}
