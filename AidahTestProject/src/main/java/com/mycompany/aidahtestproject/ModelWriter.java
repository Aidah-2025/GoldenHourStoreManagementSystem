
package com.mycompany.aidahtestproject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author nuraidahmaisarahbintiazeman
 */
public class ModelWriter {
    
    public static void saveModel (Model mod){
        try {
            try (PrintWriter pw = new PrintWriter (new FileWriter ("model.csv", true))) {
                pw.print(mod.getModelId() + "," + mod.getPrice());
                for (int stock : mod.getStockQuantity()){
                    pw.print("," + stock);
                }
                pw.println();
            }
            System.out.println("Model saved: " + mod);
        }catch (IOException e){
        }
    }
}
    

