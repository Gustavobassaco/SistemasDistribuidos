/**
  * Laboratorio 4  
  * Autor: Lucio Agostinho Rocha
  * Ultima atualizacao: 04/04/2023
  */

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IMensagem extends Remote {
    
    public Mensagem enviar(Mensagem mensagem) throws RemoteException;
    
}
