/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import java.util.List;

/**
 *
 * @author hcadavid
 */
public class Main {
    
    public static void main(String a[]){
        HostBlackListsValidator hblv=new HostBlackListsValidator();        
<<<<<<< HEAD
        List<Integer> blackListOcurrences=hblv.checkHost("205.24.34.55",8);
        System.out.println("The host was found in the following blacklists:"+blackListOcurrences);       
    }   
=======
        List<Integer> blackListOcurrences=hblv.checkHost("202.24.24.55",50);
        System.out.println("The host was found in the following blacklists:"+blackListOcurrences);
        Runtime run = Runtime.getRuntime();
        System.out.println("" + run.availableProcessors()+ " Nucleos");
        System.out.println(run.toString());
        
    }
    
>>>>>>> 955d188995a8b707a61bf5d8653346f478592d54
}
