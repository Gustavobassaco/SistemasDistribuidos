/**
  * Laboratorio 4  
  * Autor: Lucio Agostinho Rocha
  * Ultima atualizacao: 04/04/2023
  */
public enum Peer {
    
    PEER1 {
        @Override
        public String getNome() {
            return "PEER1";
        }        
    },
    PEER2 {
        public String getNome() {
            return "PEER2";
        }        
    },
    PEER3 {
        public String getNome() {
            return "PEER3";
        }        
    };
    public String getNome(){
        return "NULO";
    }    
}
