package ur_os;

import java.util.ArrayList;

public class PruebaArray {
    
    
    public static void main(String[] args){
        ArrayList<Double> enteros = new ArrayList();
        
        for (int i = 0; i < 10; i++) {
            enteros.add((double)i);
        }
        
        for (int i = 0; i < enteros.size(); i++) {
            System.out.println(enteros.get(i)+" at "+i);
            if(enteros.get(i).equals((double)4) || enteros.get(i).equals((double)7)){
                System.out.println("Erase "+enteros.get(i)+" at "+i);
                enteros.remove(enteros.get(i));
                i--;
            }
        }
        
        for (Double entero : enteros) {
            System.out.println(entero);
        }
        
    }
    
    
    
}
