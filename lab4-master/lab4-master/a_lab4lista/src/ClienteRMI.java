/**
  * Laboratorio 4  
  * Autor: Lucio Agostinho Rocha
  * Ultima atualizacao: 04/04/2023
  */

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class ClienteRMI {
    
    public static void main(String[] args) {
                
    	List<Peer> listaPeers = Arrays.asList(Peer.values());
		
        try {
                        
            Registry registro = LocateRegistry.getRegistry("127.0.0.1", 1099);

            
        	//Escolhe um peer aleatorio da lista de peers para conectar
            SecureRandom sr = new SecureRandom();
    		
            IMensagem stub = null;
            Peer peer = null;
            
    		boolean conectou=false;
    		while(!conectou){
    			peer = listaPeers.get(sr.nextInt(listaPeers.size()));
    			try{    				
    				stub = (IMensagem) registro.lookup(peer.getNome());
    				conectou=true;
    			} catch(java.rmi.ConnectException e){
    				System.out.println(peer.getNome() + " indisponivel. ConnectException. Tentanto o proximo...");
    			} catch(java.rmi.NotBoundException e){
    				System.out.println(peer.getNome() + " indisponivel. NotBoundException. Tentanto o proximo...");
    			}
    		}
            System.out.println("Conectado no peer: " + peer.getNome());            
    		
    		
            String opcao="";
            Scanner leitura = new Scanner(System.in);
            do {
            	System.out.println("1) Read");
            	System.out.println("2) Write");
            	System.out.println("x) Exit");
            	System.out.print(">> ");
            	opcao = leitura.nextLine();
            	switch(opcao){
            	case "1": {
            		Mensagem mensagem = new Mensagem("", opcao);
            		Mensagem resposta = stub.enviar(mensagem); //dentro da mensagem tem o campo 'read'
            		System.out.println(resposta.getMensagem());
            		break;
            	}
            	case "2": {
            		//Monta a mensagem                		
            		System.out.print("Add fortune: ");
            		String fortune = leitura.nextLine();
            		
            		Mensagem mensagem = new Mensagem(fortune, opcao);
            		Mensagem resposta = stub.enviar(mensagem); //dentro da mensagem tem o campo 'write'
            		System.out.println(resposta.getMensagem());
            		break;
            	}
            	}
            } while(!opcao.equals("x"));
            leitura.close();
                        
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    
}
