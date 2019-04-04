import java.lang.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;

// Code for philosopher
class Phil extends Thread{
    Label box; // Status box
    int id; // Philosopher ID
    Random rd = new Random();
    Frame UI;

    public Phil(Frame UI,int id){
        box = new Label("P "+id);
        this.id=id;
        this.UI = UI;
        this.UI.add(box);

    }

    // Philosopher sleeps for some miliseconds
    void doNothing(int sleepTime){
        try{
            Thread.sleep(sleepTime);
        }
        catch(Exception e){
        }
    }

    //Display the status of the philosopher
    void printStatus(String s){
        box.setText(s);
        System.out.println(s);
        UI.setVisible(true);
    }

    // Philosopher is eating
    public void eating(){

        printStatus("P"+id+" eating ");
        doNothing(Test.sleepTime*(1+rd.nextInt(4)));

    }

    // Philosopher is thinking
    public void thinking(){
        printStatus("P"+id+" thinking ");
        doNothing(Test.sleepTime*(1+rd.nextInt(4)));

    }

    public void run() {
// Your code for running philosopher should be here

// Determine the requesting order of resources
// Make sure that j<i.
        int j=0;
        int k=0;
        int i=0;
        j=id;
        i=(id+1)%5;

        if(j>i){
            k=i;
            i=j;
            j=k;
        }


//printStatus("P"+ id +" compete for the sticks "+j+"-"+i);
        while(true){
            while(!Test.stop){

                printStatus("P"+id+" getting the stick "+j+"-"+i);
//Critial code

                while(Test.TestandSet(Test.sticks[j])){
                    printStatus("P"+id+" waiting the stick "+(j));
                    doNothing(Test.sleepTime);
                }

                printStatus("P"+id+" getting the stick "+(i));

                while(Test.TestandSet(Test.sticks)){
                    printStatus("P"+id+" waiting the stick "+(i));
                    doNothing(Test.sleepTime);
                }

                eating();

                printStatus("P"+ id +" drops the sticks "+j+"-"+i);
                Test.sticks[j].set(false);
                Test.sticks.set(false);
// End of critical code

                thinking();

            }
            try{

                wait();
            }
            catch(Exception e){
            }
        }
    }
}

class MyBool {
    boolean val;
    public MyBool(boolean v){
        val=v;
    }
    synchronized boolean get(){
        return val;
    }
    synchronized void set(boolean v){
        val =v;
    }
}


public class Test{

    public static synchronized boolean TestandSet(MyBool val){
        boolean temp = val.get();
        val.set(true);
        return temp;
    }

//Variable to be shared by philosophers

    public static boolean stop = false;
    public static boolean count = true;
    public static int sleepTime = 600;
    public static MyBool[] sticks = new MyBool[5];


    //Code to run program
    public static void main(String[] args) {
        int i=0;
        TestGUI test = new TestGUI();
// Initialise MyBool for TestandSet
        for(i=0;i<5;i++){
            sticks= new MyBool(false);
        }

//Starting Philosopher
        for(i=0;i<5;i++){
            System.out.println("Starting P"+i);
            (new Phil(test,i)).start();
        }

    }
}

//Graphic class
class TestGUI extends Frame{
    Button stopBtn;
    Button countBtn;
    Button addBtn;
    int count=1;
    public TestGUI(){

        super("Dinner philosophers");

        setSize(200, 500);
// add a demo component to this frame
        setLayout(new GridLayout(8,1));
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
                dispose();
                System.exit(0);
            }

        });
        init();

    }

    public void stop(){
        if(!Test.stop){
            stopBtn.setLabel("Start");
        }
        else
            stopBtn.setLabel("Stop");
        Test.stop = !Test.stop;
        if(!Test.stop){
            try{

                notifyAll();
            }
            catch(Exception e){
            }
        }
    }

    public void init(){

        Button b1 = new Button("Stop");
        b1.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        stop();
                    }
                }
        );
        add(b1);
        stopBtn=b1;


        b1 = new Button("Fast");
        b1.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent e){

                        if(Test.sleepTime <100)
                            Test.sleepTime=100;
                        else
                            Test.sleepTime=Test.sleepTime-100;

                    }
                }
        );
        add(b1);

        b1 = new Button("Slow");
        b1.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        Test.sleepTime=Test.sleepTime+100;
                    }
                }
        );
        add(b1);

        setVisible(true);
    }
}