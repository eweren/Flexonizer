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
package Code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Class handling all the events for our tasks-arraylists.
 * @author NiHu
 */
public final class TaskHandler {
    
    public ArrayList<Task> tasksList = new ArrayList<>();
    private final String fileName = System.getProperty("user.home") + "\\flexonizer\\save.txt";
    private final String directoryName = System.getProperty("user.home") + "\\flexonizer";
    public ArrayList<Task> aArrayListe = new ArrayList<>();
    public ArrayList<Task> bArrayListe = new ArrayList<>();
    public ArrayList<Task> cArrayListe = new ArrayList<>();
    public String sortiertNach;
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
    public int aTasksDoneInTime = 0 ;
    public int aTasksDoneTooLate = 0;
    
    /**
     * Constructor. Directly catches all tasks.
     * @throws IOException 
     */
    public TaskHandler() throws IOException{
        getTasks();
    }
    
    /**
     * Returns the tasksList.
     * @return actual taskslist
     * @throws IOException 
     */
    public ArrayList<Task> getTasksList() throws IOException{
        return this.tasksList;
    }
    
    /**
     * Adds to the tasksList.
     * @param a
     * @throws IOException 
     */
    public void addToTasksList(Task a) throws IOException{
        this.tasksList.add(a);
    }
    
    /**
     * Returns the A-tasks list.
     * @return A-tasks list
     */
    public ArrayList<Task> getAArrayList(){
        return this.aArrayListe;
    }
    
    /**
     * Returns the B-tasks list.
     * @return B-tasks list
     */
    public ArrayList<Task> getBArrayList(){
        return this.bArrayListe;
    }
    
    /**
     * Returns the C-tasks list.
     * @return C-tasks list
     */
    public ArrayList<Task> getCArrayList(){
        return this.cArrayListe;
    }
    
    /**
     * Writes all tasks into the file.
     * @param a The overgiven arrayList of tasks
     * @throws java.io.IOException 
     */
    public void writeTasks(ArrayList<Task> a) throws IOException{
        this.tasksList = (ArrayList<Task>) a.clone();
        try {
            FileWriter fileWriter = 
                new FileWriter(fileName);
            try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                while(!a.isEmpty()) {
                    String s = a.remove(0).toString()+"\n";
                    bufferedWriter.write(s);
                }
                bufferedWriter.write("SortBy°"+this.sortiertNach+"\n");
                bufferedWriter.write("Statistics°"+this.aTasksDoneInTime+"°_°"+this.aTasksDoneTooLate);
                bufferedWriter.close();
            }
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");
        }
    }
    
    /**
     * Gets all tasks and reformat them into an ArrayList of tasks.
     * @throws java.io.IOException
     */
    public void getTasks() throws IOException{
        
        tasksList = new ArrayList<>();
        String line;
        try {
            FileReader fileReader = new FileReader(fileName);
            try (
                    BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                while ((line = bufferedReader.readLine()) != null) {
                    String[] Aufgabe = line.split("°");
                    switch (Aufgabe.length) {
                        case 2:
                            if(Aufgabe[0].equals("SortBy"))
                                this.sortiertNach = Aufgabe[1];
                            else
                                tasksList.add(new Task(Aufgabe[0], parseDatum(Aufgabe[1])));
                            break;
                        case 1:
                            tasksList.add(new Task(Aufgabe[0]));
                            break;
                        case 4:
                            this.aTasksDoneInTime = Integer.parseInt(Aufgabe[1]);
                            this.aTasksDoneTooLate = Integer.parseInt(Aufgabe[3]);
                            break;
                        default:
                            break;
                    }
                }
                bufferedReader.close();
                TaskHandler.this.sortBy(sortiertNach);
                if(tasksList.size()>0){
                    holAufgabenS();
                }
            }         
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "' - Creating a New Directory and File now.");
            new File(directoryName).mkdirs();
            new File(fileName).createNewFile();
            getTasks();
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");
        }
    }
    
    /**
     * Sorts all ArrayLists by the overgiven String.
     * @param s
     * @throws IOException 
     */
    public void sortBy(String s) throws IOException{
        if(s!=null)
            if(s.equals("Deadline")){
                sortiertNach = "Deadline";
                sortTasksByDeadline(this.aArrayListe);
                sortTasksByDeadline(this.bArrayListe);
            }else if (s.equals("To-Do-Title")){
                sortiertNach = "To-Do-Title";
                sortTasksByTitle(this.aArrayListe);
                sortTasksByTitle(this.bArrayListe);
                sortTasksByTitle(this.cArrayListe);
            }
        else
            TaskHandler.this.sortBy("Deadline");
    }
    
    /**
     * Sorts a given ArrayList by the overgiven String.
     * @param s
     * @param a
     * @throws IOException 
     */
    public void sortBy(String s, ArrayList a) throws IOException{
        if(s!=null)
            if(s.equals("Deadline")){
                sortiertNach = "Deadline";
                sortTasksByDeadline(a);
            }else if (s.equals("To-Do-Title")){
                sortiertNach = "To-Do-Title";
                sortTasksByTitle(a);
            }
        else
            TaskHandler.this.sortBy("Deadline");
    }
    
    /**
     * Refreshes the subTasksLists.
     * @throws java.io.IOException
     */
    public void holAufgabenS() throws IOException{
        ArrayList<Task> liste = this.getTasksList();
        tasksList = new ArrayList<>();
        aArrayListe = new ArrayList<>();
        bArrayListe = new ArrayList<>();
        cArrayListe = new ArrayList<>();
        if(liste != null){
            for (Task a : liste) {
                switch (a.getCategory()) {
                    case "A":
                        aArrayListe.add(a);
                        break;
                    case "Important":
                        bArrayListe.add(a);
                        break;
                    case "C":
                        cArrayListe.add(a);
                        break;
                    default:
                        break;
                }
                this.tasksList.add(a);
            }
        }
    }
    
    /**
     * Parses the date read from the file.
     * @param s
     * @return Datum
     */
    public Date parseDatum(String s){
        Date date=null;
        try {
            date = formatter.parse(s);
        } catch (ParseException e) {
        }
        return date;
    }   
    
    
    /**
     * Sorts tasks by title.
     * @param aufgaben 
     */
    private void sortTasksByTitle(ArrayList<Task> aufgaben) throws IOException{
        Collections.sort(aufgaben, (Task t, Task t1) -> t.getTitle().compareToIgnoreCase(t1.getTitle()));
    }
    
    
    /**
     * Sorts tasks by deadline.
     * @param aufgaben 
     */
    private void sortTasksByDeadline(ArrayList<Task> aufgaben) throws IOException{
        Collections.sort(aufgaben, (Task t, Task t1) -> t.getDeadlineDate().compareTo(t1.getDeadlineDate()));
    }
}