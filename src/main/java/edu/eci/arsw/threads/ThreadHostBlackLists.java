/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.threads;

/**
 *
 * @author Acer
 */
public class ThreadHostBlackLists extends Thread {
    private int a;
    private int b;

    public ThreadHostBlackLists(int a, int b) {
        this.a = a;
        this.b = b;
    }
    public void run(){  
        for(int i=a;i<b;i++){
            System.out.println(i);
        }
        
    }  
}
