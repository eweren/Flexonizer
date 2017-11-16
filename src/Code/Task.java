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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author NiHu
 */
public class Task {
    private final String taskTitle;
    private final Date taskDeadline;
    // Holding the switch-dates from which one tasks switches it's color.
    private Date datePink;
    private Date dateOrange;
    // Amount of day, the color switches.
    private final int pink = -30;
    private final int orange = -90;
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
    
    /**
     * Constructor for tasks of type A/B.
     * @param title
     * @param deadline 
     */
    public Task(String title, Date deadline){
        taskTitle = title;
        taskDeadline = deadline;
    }
    
    
    /**
     * Constructor for C-Tasks.
     * @param title
     */
    public Task(String title){
        taskTitle = title;
        taskDeadline = null;
    }
  
    /**
     * Returns the title of a task.
     * @return tasksTitle
     */
    public String getTitle(){
        return this.taskTitle;
    }
    /**
     * Returns the Deadline as String.
     * @return Deadline as String
     */
    public String getDeadline(){
        return taskDeadline == null ? "" : this.formatter.format(taskDeadline);
    }
    /**
     * Returns the Deadline as Date-format.
     * @return Deadline
     */
    public Date getDeadlineDate(){
        return this.taskDeadline;
    }
    
    
    /**
     * Returns the ColorCode.
     * @return ColorCode
     */
    public int getColorCode(){
        return calculateColorCode();
    }
    
    
    /**
     * Calculates the ColorCode of a Task.
     * @return 0 wenn gruen, 1 wenn orange, 2 wenn pink.
     */ 
    private int calculateColorCode(){
        int r;
        Date deadline = this.taskDeadline;
        if(deadline!=null){
            Date jetzt = new Date();
            Calendar  c = Calendar.getInstance();
            c.setTime(deadline);
            c.add(Calendar.DATE, pink);
            datePink = c.getTime();
            c.add(Calendar.DATE, orange - pink);
            dateOrange = c.getTime();
            if(jetzt.after(datePink))
                r = 2;
            else
                r = jetzt.after(dateOrange)?1:0;
            return r;
        }
        return -1;
    }
    
    /**
     * Returns the Task as String.
     * Typ A/B - Title°dd.MM.yyyy
     * Typ C   - Title
     * @return Task as String.
     */
    @Override
    public String toString(){
        return this.getDeadline().equals("") ? this.taskTitle 
                : this.taskTitle+ "°" +this.formatter.format(taskDeadline);
    }
    
    /**
     * Returns the Category of a tasks.
     * @return A,Important oder C.
     */
    public String getCategory() {
        if(!"".equals(this.getDeadline()))
            if(this.getColorCode()==2)
                return("A");
            else
                return("Important");
        else
            return("C");
    }
}
