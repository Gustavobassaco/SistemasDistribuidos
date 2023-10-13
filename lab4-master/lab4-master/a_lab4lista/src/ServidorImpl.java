/**
  * Laboratorio 4  
  * Autor: Lucio Agostinho Rocha
  * Ultima atualizacao: 04/04/2023
  */
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

public class ServidorImpl implements IMensagem{
    
	private Principal aux;

    public ServidorImpl() {
          this.aux = new Principal();
    }
    
    //Cliente: invoca o metodo remoto 'enviar'
    //Servidor: invoca o metodo local 'enviar'
    @Override
    public Mensagem enviar(Mensagem mensagem) throws RemoteException {
        Mensagem resposta;
        try {
        	System.out.println("Mensagem recebida: " + mensagem.getMensagem());
			resposta = new Mensagem(parserJSON(mensagem.getMensagem()));
		} catch (Exception e) {
			e.printStackTrace();
			resposta = new Mensagem("{\n" + "\"result\": false\n" + "}");
		}
        return resposta;
    }    
    
    public String parserJSON(String json) throws Exception {
    	
    	String[] partes = json.split("\"");
    	String method = partes[3];
    	String args = partes[7];
    	String jsonFinal = "";
    	
        if ("read".equals(method)) {
        	String arg = this.aux.read();
        	jsonFinal =  "{\n\"result\": \"" + arg + "\"\n}";

        } else if ("write".equals(method)) {
        	if(args.endsWith("\\n")) {
        		String argaux = args.substring(0, args.length() - 2);
        		
        		this.aux.write(argaux);
        		jsonFinal =  "{\n\"result\":\"" + argaux + "\"\n}";

	        }else {
	        	throw new Exception("A mensagem deve terminar com \\n!");
	        }
        }
		return jsonFinal;
	}
    
    
    public void iniciar() {
        try {
            // Obtém a lista de peers a partir da enumeração Peer
            List<Peer> listaPeers = Arrays.asList(Peer.values());

            // Declaração do registro RMI
            Registry servidorRegistro;

            try {
                // Tenta criar um novo registro RMI na porta 1099
                servidorRegistro = LocateRegistry.createRegistry(1099);
            } catch (java.rmi.server.ExportException e) { // Registro já iniciado
                System.out.print("Registro já iniciado. Usar o ativo.\n");
            }

            // Obtém o registro RMI existente (registro é único para todos os peers)
            servidorRegistro = LocateRegistry.getRegistry();
            
            // Obtém a lista de nomes de peers ativos no registro
            String[] listaAlocados = servidorRegistro.list();
            for (int i = 0; i < listaAlocados.length; i++)
                System.out.println(listaAlocados[i] + " ativo.");

            // Gera um número aleatório para escolher um peer da lista
            SecureRandom sr = new SecureRandom();
            Peer peer = listaPeers.get(sr.nextInt(listaPeers.size()));

            int tentativas = 0;
            boolean repetido = true;
            boolean cheio = false;

            // Loop para garantir que o peer escolhido não esteja repetido ou que o registro não esteja cheio
            while (repetido && !cheio) {
                repetido = false;
                peer = listaPeers.get(sr.nextInt(listaPeers.size()));

                // Verifica se o peer escolhido já está ativo
                for (int i = 0; i < listaAlocados.length && !repetido; i++) {

                    if (listaAlocados[i].equals(peer.getNome())) {
                        System.out.println(peer.getNome() + " ativo. Tentando próximo...");
                        repetido = true;
                        tentativas = i + 1;
                    }

                }

                // Verifica se o registro está cheio (todos os peers alocados)
                if (listaAlocados.length > 0 && tentativas == listaPeers.size()) {
                    cheio = true;
                }
            }

            // Se o registro estiver cheio, imprime uma mensagem e encerra o programa
            if (cheio) {
                System.out.println("Sistema cheio. Tente mais tarde.");
                System.exit(1);
            }

            // Exporta o objeto do servidor RMI
            IMensagem skeleton = (IMensagem) UnicastRemoteObject.exportObject(this, 0); // 0: sistema operacional indica a porta (porta anônima)
            
            // Registra o objeto no registro RMI com o nome do peer escolhido
            servidorRegistro.rebind(peer.getNome(), skeleton);
            System.out.print(peer.getNome() + " Servidor RMI: Aguardando conexões...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        ServidorImpl servidor = new ServidorImpl();
        servidor.iniciar();
    }    
}
