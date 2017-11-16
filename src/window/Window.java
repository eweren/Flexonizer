/*
 * The MIT License
 *
 * Copyright 2017 NiHu.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package window;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.image.Image;
import static javafx.application.Application.launch;

/**
 * Main class for creating the application window and loading the FXML, as well
 * the controller for it.
 * @author NiHu
 */
public class Window extends Application {    
    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("WindowFXML.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Flexonizer - Tichen's To-Do-List");
        stage.getIcons().add(new Image(Window.class.getResourceAsStream("/res/icon_small.png")));
        stage.setMinWidth(600);
        stage.setMinHeight(600);
        stage.setMaximized(true);
        Platform.runLater( () -> root.requestFocus() );
        backup();
        /*
        Platz für Controllereinbindung
        */
        WindowFXMLController controller = loader.<WindowFXMLController>getController();
        
        EventHandler<WindowEvent> h;
        h = (WindowEvent event) -> {
            
            try {
                event.consume();
                controller.beenden();
            } catch (IOException ex) {
                Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        };

        stage.setOnCloseRequest(h);
        stage.setScene(scene);
        stage.show();
        
    }
    
    /**
     * Closes the application.
     */
    protected static void close(){
        Platform.exit();
        
    }
    
    /**
     * Simple backup method for automatically creating a backup on startup, if
     * backupFile does not exist.
     * @throws IOException 
     */
    private void backup() throws IOException{
        final String backupFileName = System.getProperty("user.home") + "\\flexonizer\\backup.txt";
        final String directoryName = System.getProperty("user.home") + "\\flexonizer";
        final String fileName = System.getProperty("user.home") + "\\flexonizer\\save.txt";
        Path path = Paths.get(backupFileName);
        if(!Files.exists(path)){
            if(!Files.exists(Paths.get(directoryName)))
                new File(directoryName).mkdirs();
            new File(backupFileName).createNewFile();
            String line;
            ArrayList<String> a = new ArrayList<>();
            
            FileReader fileReader = new FileReader(fileName);
            try (
                BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                while ((line = bufferedReader.readLine()) != null) {
                    a.add(line);
                }

                FileWriter fileWriter = 
                new FileWriter(backupFileName);
            try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                while(!a.isEmpty()) {
                    bufferedWriter.write(a.remove(0)+"\n");
                }
                bufferedWriter.close();
            }
                bufferedReader.close();
            }         
            
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}