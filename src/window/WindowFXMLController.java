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

import Code.Task;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author NiHu
 */
public class WindowFXMLController implements Initializable {

    @FXML TextField taskTitle;
    @FXML Button addButton;
    @FXML DatePicker date_to_do;
    @FXML GridPane gridpane;
    @FXML GridPane gridpane_a;
    @FXML GridPane gridpane_b;
    @FXML GridPane gridpane_c;
    @FXML GridPane gridpane_abc;
    @FXML ComboBox classpicker;
    @FXML ComboBox sortpicker;
    @FXML TabPane tabpane;
    @FXML TilePane chart;
    @FXML Tab to_do_tab;
    @FXML Tab abc_tab;
    @FXML Button reset_btn;
    private final DropShadow borderGlow= new DropShadow();
    private ArrayList<Task> tasks;
    private Task getsEdited;
    private SingleSelectionModel<Tab> selectionModel;
    boolean classpicker_is_set;
    Code.TaskHandler TH;
    int ia = 0;
    int ib = 0;
    int ic =0; 
    
    /**
     * Closes the application.
     */
    void beenden() throws IOException {
        TH.writeTasks(tasks);
        Platform.exit();
    }
    
    /**
     * Throws an alert.
     */
    public void technic() {
        Alert meldung = new Alert(Alert.AlertType.CONFIRMATION, "An Error occured.\n"
                + "Flexonizer has to be closed.\n"
                + "If the Error is still occuring on the next startup\n"
                + "rename the File save.txt in your USER/Flexonizer folder\n"
                + "an restart Flexonizer.", ButtonType.OK);
        meldung.setTitle("An Error occured!");
        meldung.setHeaderText("");
        Optional<ButtonType> antwort = meldung.showAndWait();
        if(antwort.isPresent())
            if(antwort.get().equals(ButtonType.YES)) 
                Platform.exit();
    }
    
    /**
     * If the drop-down menu was clicked, the datepicker gets enabled(task A/B).
     */
    public void pickerAction(){
        try{
            if(classpicker.getValue().toString().equals("Important")) {
                date_to_do.setDisable(false);
                classpicker_is_set = true;
                handleInput();
            }else if (classpicker.getValue().toString().equals("C")){
                date_to_do.setDisable(true);
                classpicker_is_set = true;
                handleInput();
            }
        }catch(Exception e){
        }
    }
    
    /**
     * Sorts all lists.
     * @throws IOException 
     */
    public void sortiereListen() throws IOException{
        TH.sortBy((String) sortpicker.getValue());
        setGridpane();
        ia = 0;
        ib = 0;
        ic = 0;
    }
    
    /**
     * Only sorts one list by the overgiven String.
     * @param s
     * @throws IOException 
     */
    public void sortiereListe(String s) throws IOException{
        setGridpane();
        switch (s) {
            case "A":
                TH.sortBy((String) sortpicker.getValue(), TH.aArrayListe);
                ia = 0;
                break;
            case "Important":
                TH.sortBy((String) sortpicker.getValue(), TH.bArrayListe);
                ib = 0;
                break;
            case "C":
                TH.sortBy("To-Do-Title", TH.cArrayListe);
                ic = 0;
                break;
            default:
                break;
        }
    }
    /**
     * Sets the add Button as active, when the tasktitle and the taskclass got set.
     */
    public void handleInput(){
        if(taskTitle.getText().length()>1 && classpicker_is_set)
            addButton.setDisable(false);
        else
            addButton.setDisable(true);
    }
    
        
    /**
     * Deletes the clicked row.
     * @param grid
     * @param row 
     */
    private void deleteRow(GridPane grid, int row, ArrayList<Task> a) throws IOException {
        tasks = TH.getTasksList();
        Set<Node> deleteNodes = new HashSet<>();
        grid.getChildren().forEach((child) -> {
            Integer rowIndex = GridPane.getRowIndex(child);
            int r = rowIndex == null ? 0 : rowIndex;
            if (r > row)
                GridPane.setRowIndex(child, r-1);
            else if (r == row) 
                deleteNodes.add(child);
        });
        grid.getChildren().removeAll(deleteNodes);
        Task r;
        r = a.remove(row-1);
        tasks.remove(r);
        TH.tasksList.remove(r);
        if(r.getCategory().equals("A")){
            Calendar  c = Calendar.getInstance();
            c.setTime(Date.from(Instant.now()));
            c.add(Calendar.DATE, -1);
            if(r.getDeadlineDate().before(c.getTime())){
                TH.aTasksDoneTooLate+=1;
            } else{
                TH.aTasksDoneInTime+=1;
            }
        }
        setGridRows();
        getGraph(chart, TH.getAArrayList().size(), TH.getBArrayList().size(), TH.getCArrayList().size());
    }
    
    /**
     * Clears the input-fields.
     */
    public void deleteInput(){
        taskTitle.setText("");
        date_to_do.setValue(LocalDate.now());
        classpicker.setValue(null);
        classpicker_is_set = false;
        addButton.setDisable(true);
    }
    
    /**
     * Fills the input-fields if the task is to be edited.
     * @param s
     * @param d
     * @param c 
     */
    public void fillInput(String s, LocalDate d, String c){
        taskTitle.setText(s);
        date_to_do.setValue(d);
        classpicker.setValue(c);
        classpicker_is_set = true;
        addButton.setDisable(false);
    }
    
    /**
     * Adds the task straight to its final category-list.
     * @throws java.io.IOException
     */
    public void addAufgabe() throws IOException{
        tasks = TH.getTasksList() == null ? new ArrayList<>() : TH.getTasksList();
        Date date = Date.from(date_to_do.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Task a = date_to_do.isDisabled() ? new Task(taskTitle.getText()):new Task(taskTitle.getText(), date);
        TH.addToTasksList(a);
        switch (a.getCategory()) {
            case "A":
                if(getsEdited!= null)
                    TH.aArrayListe.remove(getsEdited);
                TH.aArrayListe.add(a);
                sortiereListe("A");
                break;
            case "Important":
                if(getsEdited!= null)
                    TH.aArrayListe.remove(getsEdited);
                TH.bArrayListe.add(a);
                sortiereListe("Important");
                break;
            case "C":
                if(getsEdited!= null)
                    TH.aArrayListe.remove(getsEdited);
                TH.cArrayListe.add(a);
                sortiereListe("C");
                break;
            default:
                break;
        }
        if(getsEdited!= null)
            TH.tasksList.remove(getsEdited);
        deleteInput();
        date_to_do.setDisable(true);
        setGridpane();
        getsEdited = null;
    }
    
    /**
     * Sets the onclick-listener for the task, so that it can be edited.
     * @param h2
     * @param a 
     */
    private void setH2Listener(HBox h2, Task a){
        h2.setOnMouseClicked((Event event) -> {
            selectionModel = tabpane.getSelectionModel();
            selectionModel.select(to_do_tab);
            try{
                fillInput(a.getTitle(), a.getDeadlineDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), a.getCategory().equals("A")?"Important":a.getCategory());
            }catch(NullPointerException e){
                fillInput(a.getTitle(), Date.from(Instant.now()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), "C");
            }
            taskTitle.selectEnd();
            getsEdited = a;
        });
    }
    
    /**
     * Dynamically fills the gridpanes with tasks fitting to its category.
     */
    private void setGridRows() throws IOException{
        if(!TH.sortiertNach.equals("Deadline"))
            TH.sortBy("Deadline");
        Date d = null;
        if(TH.getAArrayList().size() > 0)
             d = TH.getAArrayList().get(0).getDeadlineDate();
        TH.sortBy(this.sortpicker.getValue().toString());
        ArrayList<Task> alist = TH.getAArrayList();
        ArrayList<Task> blist = TH.getBArrayList();
        ArrayList<Task> clist = TH.getCArrayList();
        deleteRows();
        
        for(int i = 0; i<alist.size(); i++){
            setGridRow(gridpane_a, alist.get(i), i, alist, d);
        }
        for(int i = 0; i<blist.size(); i++){
            setGridRow(gridpane_b, blist.get(i), i, blist, null);
        }
        for(int i = 0; i<clist.size(); i++){
            setGridRow(gridpane_c, clist.get(i), i, clist, null);
        }
    }
    
    
    /**
     * Fills the Gridpanes.
     * @param gridpane
     * @param task
     * @param i
     * @param tasks
     * @param date 
     */
    private void setGridRow(GridPane gridpane, Task task, int i, ArrayList<Task> tasks, Date date) {
        HBox h1 = new HBox();
        HBox h2 = new HBox();
        HBox h3 = new HBox();
        
        Label t = new Label(task.getTitle());
        Label t1 = new Label(task.getDeadline());

        setStyle(h2, task);
        setStyle(h3, task);

        h2.getChildren().add(t);
        h3.getChildren().add(t1);
        
        setCheckboxListener(h1, task, gridpane, tasks);
        setH2Listener(h2, task);
        
        switch (task.getCategory()) {
            case "A":
                if(!date.equals(task.getDeadlineDate())){
                    h1.setStyle("-fx-padding: 5px;-fx-background-color:rgba(240,96,123,0.1);");
                    h2.setStyle("-fx-padding: 5px;-fx-background-color:rgba(240,96,123,0.1);");
                    h3.setStyle("-fx-padding: 5px;-fx-background-color:rgba(240,96,123,0.1);");
                }
                gridpane.addRow(ia+1, h1, h2, h3);
                ia +=1;
                break;
            case "Important":
                gridpane.addRow(ib+1, h1, h2, h3);
                ib +=1;
                break;
            case "C":
                gridpane.addRow(ic+1, h1, h2);
                ic +=1;
                break;
            default:
                break;
        }
    }
    
    /**
     * Sets the checkbox with its onclick and on ENTER event and the style of it.
     * @param h1
     * @param task
     * @param gridpane
     * @param tasks 
     */
    private void setCheckboxListener(HBox h1, Task task, GridPane gridpane, ArrayList<Task> tasks){
        CheckBox c1 = new CheckBox();
        if(task.getCategory().equals("C")){
            c1.setOnKeyPressed((event) -> { if(event.getCode() == KeyCode.ENTER) { try {
                deleteRow(gridpane, gridpane.getChildren().indexOf(h1)/2+1, tasks);
                } catch (IOException ex) {
                    Logger.getLogger(WindowFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } });
            c1.setOnMouseClicked((Event event) -> {
                c1.setSelected(true);
                try {
                    deleteRow(gridpane, gridpane.getChildren().indexOf(h1)/2+1, tasks);
                } catch (IOException ex) {
                    Logger.getLogger(WindowFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            h1.setStyle(getStyle(task.getColorCode()));
            h1.getChildren().add(c1);
        }else{ //If the task is of type Important/A
            c1.setOnKeyPressed((event) -> { if(event.getCode() == KeyCode.ENTER) { try {
                deleteRow(gridpane, gridpane.getChildren().indexOf(h1)/3+1, tasks);
                } catch (IOException ex) {
                    Logger.getLogger(WindowFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } });
            c1.setOnMouseClicked((Event event) -> {
                c1.setSelected(true);
                try {
                    deleteRow(gridpane, gridpane.getChildren().indexOf(h1)/3+1, tasks);
                } catch (IOException ex) {
                    Logger.getLogger(WindowFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            h1.setStyle(getStyle(task.getColorCode()));
            h1.getChildren().add(c1);
        }
    }
    
    /**
     * Sets the fitting background.
     * @param hbox
     * @param task
     */
    private void setStyle(HBox hbox, Task task) {
        hbox.setStyle(getStyle(task.getColorCode()));
    }
    
    /**
     * Returns the right background fitting to the overgiven colorcode.
     * @param color
     * @return Style as String
     */
    private String getStyle(int color){
        String ret = "";
        //wenn Kategorie A
        switch (color) {
            case 0:
                ret = "-fx-background-color: rgb(126,227,160); -fx-padding: 5px;";
                break;
            case 1:
                ret = "-fx-background-color: rgb(255,170,76); -fx-padding: 5px;";
                break;
            case 2:
                ret = "-fx-background-color: rgb(240,96,123); -fx-padding: 5px;";
                break;
            case -1:
                ret = "-fx-background-color: rgb(230,230,230); -fx-padding: 5px;";
                break;
            default:
                break;
        }
        return ret;
    }
    
    
    
    /**
     * Starts the refreshing of he UI.
     */
    private void setGridpane() throws IOException{
        tasks = TH.getTasksList();
        
        try{
            getGraph(chart, TH.getAArrayList().size(), TH.getBArrayList().size(), TH.getCArrayList().size());
        }catch(NullPointerException e){
            
        }
        if(tasks.isEmpty()) {
            selectionModel = tabpane.getSelectionModel();
            selectionModel.select(to_do_tab);
            this.taskTitle.requestFocus();
        } else
            setGridRows();
            
    }
    
    /**
     * Initialise the TaskHandler.
     * @throws IOException 
     */
    public void setTH() throws IOException{
        TH = new Code.TaskHandler();
    }
    
    /**
     * Initialises the Controller-Class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            setTH();
        } catch (IOException ex) {
            Logger.getLogger(WindowFXMLController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        date_to_do.setValue(LocalDate.now());
        ObservableList<String> ol 
                = FXCollections.observableArrayList("Important", "C");
        ObservableList<String> ol1 
                = FXCollections.observableArrayList("Deadline", "To-Do-Title");
        date_to_do.setDisable(true);
        sortpicker.setItems(ol1);
        classpicker.setItems(ol);
        sortpicker.getSelectionModel().select(this.TH.sortiertNach != null 
                ? this.TH.sortiertNach.equals("Deadline")?0:1 : 0);
        try {
            TH.sortBy(this.TH.sortiertNach != null
                    ? this.TH.sortiertNach.equals("Deadline")?"Deadline":"To-Do-Title" : "Deadline");
        } catch (IOException ex) {
            Logger.getLogger(WindowFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(Color.RED);
        borderGlow.setWidth(5);
        borderGlow.setHeight(5);
        try {
            setGridpane();
        } catch (IOException ex) {
            Logger.getLogger(WindowFXMLController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        
        this.taskTitle.setOnKeyPressed((event) -> { if(event.getCode() == KeyCode.ENTER) { trytoadd(); } });
        this.classpicker.setOnKeyPressed((event) -> { if(event.getCode() == KeyCode.ENTER) { trytoadd(); } });
        this.date_to_do.setOnKeyPressed((event) -> { if(event.getCode() == KeyCode.ENTER) { trytoadd(); } });
        this.addButton.setOnKeyPressed((event) -> { if(event.getCode() == KeyCode.ENTER) { trytoadd(); } });
        getGraph(chart, TH.getAArrayList().size(), TH.getBArrayList().size(), TH.getCArrayList().size());
    }

    /**
     * Delets all rows in all gridpanes.
     */
    private void deleteRows() {
        gridpane_a.getChildren().clear();
        gridpane_b.getChildren().clear();
        gridpane_c.getChildren().clear();
        ia=0;
        ib=0;
        ic=0;
    }

    /**
     * Gets called, if enter is pushed. If the length of the taskstitle is >2
     * and the classpicker got set, the task gets added.
     */
    private void trytoadd(){
        if(!this.addButton.isDisabled())
            try {
                this.addAufgabe();
                this.taskTitle.requestFocus();
        } catch (IOException ex) {
            Logger.getLogger(WindowFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        else
            if(this.classpicker_is_set)
                this.taskTitle.requestFocus();
            else
                this.classpicker.requestFocus();
    }
    

    /**
     * Resets the statistics.
     */
    public void resetStatistics(){
        TH.aTasksDoneInTime = 0;
        TH.aTasksDoneTooLate = 0;
        getGraph(chart, TH.getAArrayList().size(), TH.getBArrayList().size(), TH.getCArrayList().size());
        reset_btn.setVisible(false);
    }
    
    /**
     * Creates the stackedbarchart with tasks finished and the piechart with
     * task in progress.
     * @param chart
     * @param aTasks
     * @param bTasks
     * @param cTasks
     */
    public void getGraph(TilePane chart, int aTasks, int bTasks, int cTasks){
        chart.getChildren().clear();
        if(TH.aTasksDoneInTime+TH.aTasksDoneTooLate>0){
            final String tasksDoneInTime = "In Time";
            final String tasksDoneTooLate = "Too Late";
            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();
            final StackedBarChart<String, Number> sbc =
                    new StackedBarChart<>(xAxis, yAxis);
            final XYChart.Series<String, Number> series1;
            series1 = new XYChart.Series<>();
            xAxis.setLabel("Tasks finished");
            xAxis.setTickLabelRotation(90);
            xAxis.setCategories(FXCollections.<String>observableArrayList(
                Arrays.asList(tasksDoneInTime, tasksDoneTooLate)));
            yAxis.setLabel("Value");
            sbc.setLegendVisible(false);
            
            series1.getData().add(new XYChart.Data<>(tasksDoneInTime, TH.aTasksDoneInTime));
            series1.getData().add(new XYChart.Data<>(tasksDoneTooLate, TH.aTasksDoneTooLate));
            
            sbc.getData().addAll(series1);
            reset_btn.setVisible(true);
            sbc.setMaxWidth(400);
            chart.getChildren().add(sbc);
            chart.getChildren().sort((Node t, Node t1) -> t.getClass().toString().compareTo(t1.getClass().toString()));
        }
        if(aTasks!=0||bTasks!=0||cTasks!=0){
                PieChart pieChart = new PieChart();
                int i = aTasks + bTasks + cTasks;
                ObservableList<Data> answer = FXCollections.observableArrayList();
                answer.addAll(new PieChart.Data("Type A", (double)aTasks/i*100),
                    new PieChart.Data("Type B",(double)bTasks/i*100),
                    new PieChart.Data("Type C",(double)cTasks/i*100)
                    );
                pieChart.setData(answer);
                pieChart.setTitle("To-Do-Category");
                pieChart.setLegendVisible(true);
                pieChart.setLabelsVisible(false);
                pieChart.setManaged(true);
                chart.getChildren().add(pieChart);
            }

    }
}
