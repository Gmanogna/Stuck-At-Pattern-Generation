package packages;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


class CircuitNode{
    String name;
    String operation;
    CircuitNode input1;
    CircuitNode input2;
    int value;

    CircuitNode(String name,String operation){
        this.name=name;
        this.operation=operation;

    }

    void computeValue(){
         if (operation.equals("INPUT")) {
        // For input nodes, the value is already set, no need to compute anything
        return;
    }

    if (input1 == null || (input2 == null && !operation.equals("NOT"))) {
        throw new IllegalStateException("Input nodes are not properly connected for node " + name);
    }

        switch(operation){
            case "AND":
               value=input1.value & input2.value;
               break;
            case "OR":
               value=input1.value | input2.value;
               break;
            case "NOT":
               value= ~ input1.value & 1;
               break;
            case "XOR":
               value=input1.value ^ input2.value;
               break;
            default:
               throw new IllegalArgumentException("Invalid operation "+ operation);           
 
        }
    }
}
public class Circuit {
    HashMap<String,CircuitNode> nodes;
    public int inputsSize;
    public Circuit(){
        nodes=new HashMap<>();
        
    }

    public void addNode(String name,String operation){
         CircuitNode node =new CircuitNode(name,operation);
         nodes.put(name,node);
        
    }

    public void connectNodes(String outputNodename, String input1Nodename, String input2Nodename) {
        CircuitNode outputNode = nodes.get(outputNodename);
        CircuitNode input1Node = nodes.get(input1Nodename);
     
        if (input1Node == null) {
            throw new IllegalArgumentException("Invalid input node: " + input1Nodename);
        }
        outputNode.input1 = input1Node;
        if (input2Nodename != null) {
            CircuitNode input2Node = nodes.get(input2Nodename);
            if (input2Node == null) {
                throw new IllegalArgumentException("Invalid input node: " + input2Nodename);
            }
            outputNode.input2 = input2Node;
        }
    }


    
    public void createCircuit(String file){
        String filePath = file;
        HashMap<String,String> nodeOperations=new HashMap<>();
        HashSet<String> inputNodes =new HashSet<>();
        try(BufferedReader reader =new BufferedReader(new FileReader(filePath))){
            String line;
            while ((line = reader.readLine()) != null) {
                String pattern = "\\s*([^=\\s]+)\\s*=\\s*(.*)";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(line);
                String nodeName;
                String opera;
                if (m.find()) {
                    nodeName = m.group(1).trim();
                    opera = m.group(2).trim();
                    String[] nodesInOperation = opera.split("[&|~^]");
                    for (String node : nodesInOperation) {
                        String inputNode = node.trim();
                        if (!nodeOperations.containsKey(inputNode) && !inputNode.equals("")) {
                            nodeOperations.put(inputNode, "INPUT");
                            inputNodes.add(inputNode);
                        }
                    }
                
                    nodeOperations.put(nodeName,opera);
                 
                }


            }
            inputsSize=inputNodes.size();

            //add nodes
            for(String nodeName :nodeOperations.keySet()){
                String operation=nodeOperations.get(nodeName);
                if (operation.contains("&") ) {
                    addNode(nodeName,"AND");
                }else if (operation.contains("|") ) {
                    addNode(nodeName,"OR");
                }else if (operation.contains("^")) {
                    addNode(nodeName,"XOR");
                }else if (operation.startsWith("~ ")) {
               addNode(nodeName, "NOT");
                } else {
                addNode(nodeName, "INPUT");
                }
           
            }

            //connect nodes
            for (String nodeName : nodeOperations.keySet()) {
                String operation = nodeOperations.get(nodeName);
                String[] inputs = operation.split("[&|~^]",2);
                
                if (operation.contains("&")|| operation.contains("|") || operation.contains("^")) {
                    String input1Node = inputs[0].trim();
                    String input2Node = inputs[1].trim();
                    connectNodes(nodeName, input1Node, input2Node);
                } 
                else if (operation.startsWith("~")) {
                   
                    for(int i=1;i<inputs.length;i++){
                        if(inputs[i]!=null && !inputs[i].isEmpty()){
                           String inputNode = inputs[i].trim();
                           connectNodes(nodeName,inputNode,null);
                        }
                    }
                    
                    
                } 
            }
           

        }catch(IOException e){
            e.printStackTrace();
        }

    }
    
    public void simulateCircuit(String inputVector){
        for (int i = 0; i < inputVector.length(); i++) {
            char inputBit = inputVector.charAt(i);
            CircuitNode inputNode = nodes.get(""+(char)('A' + i));
           
            inputNode.value = (inputBit == '1') ? 1 : 0;
        
        }
        
        for (CircuitNode node : nodes.values()) {
         
        if (node.operation.equals("INPUT")) {
            continue; // Skip input nodes since their values are already set
        }
            node.computeValue();
        }
    }
    
    public void setValueCompute(String name,int value,String inputVector){
        for (int i = 0; i < inputVector.length(); i++) {
            char inputBit = inputVector.charAt(i);
            CircuitNode inputNode = nodes.get(""+(char)('A' + i));
           
            inputNode.value = (inputBit == '1') ? 1 : 0;
        
        }
        for (CircuitNode node : nodes.values()) {
         
        if (node.operation.equals("INPUT") || node.name==name) {
            continue; // Skip input nodes since their values are already set
        }else if(node.name==name){
            node.value=value;
        }else{
            node.computeValue();
             
        }
         
        }
       
    }

    public int getOutput(String nodeName){
        CircuitNode outputNode=nodes.get(nodeName);
        
        return outputNode.value;
    }
    public int getNumberOfinputs(){
       
       return inputsSize;
    }

        
    
}
