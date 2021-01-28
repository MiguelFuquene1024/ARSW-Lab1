/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import edu.eci.arsw.threads.ThreadHostBlackLists;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT=5;
    private HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
    private LinkedList<ThreadHostBlackLists> listaThreads = new LinkedList<>();

    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress,int N) {
        
        LinkedList<Integer> blackListOcurrences=new LinkedList<>();
        int ocurrencesCount=0;
        
        
        int checkedListsCount=0;
        crearThreads(ipaddress,N);
        
        
        for(ThreadHostBlackLists t:listaThreads){
            t.start();
        }
        for(ThreadHostBlackLists t:listaThreads){
            try{
                t.join();
                ocurrencesCount += t.getOcurrencesCount();
                checkedListsCount += t.getCheckedListsCount();
                blackListOcurrences.add(t.getOcurrencesCount());
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        }
        
        
        for(int i=0;i<blackListOcurrences.size();i++){
            ocurrencesCount += blackListOcurrences.get(i);
        }
        System.out.println(ocurrencesCount);
        if (ocurrencesCount>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }                
        
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});
        
        return blackListOcurrences;
    }
    
    
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
    private void crearThreads(String ipaddress, int N){
        int totalServersRegistered = skds.getRegisteredServersCount();
        int amountForThread = (int)skds.getRegisteredServersCount()/N;
        
        for(int i=0;i<N;i++){
            if(totalServersRegistered % N != 0){
                ThreadHostBlackLists thread = new ThreadHostBlackLists(i*((int)(totalServersRegistered/N)+1),(i+1)*((int)(totalServersRegistered/N)+1)-1,skds,ipaddress,BLACK_LIST_ALARM_COUNT);
                listaThreads.add(thread);
            }
            else{
                ThreadHostBlackLists thread = new ThreadHostBlackLists(i*(totalServersRegistered/N),(i+1)*(totalServersRegistered/N)-1,skds,ipaddress,BLACK_LIST_ALARM_COUNT);
                listaThreads.add(thread);
            }
            
        }
        

    }
    
}
