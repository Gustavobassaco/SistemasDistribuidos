/**
 * Laboratorio 3  
 * Autor: Lucio Agostinho Rocha
 * Ultima atualizacao: 04/04/2023
 */
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

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
			resposta = new Mensagem("{\n\"result\": \"false\"\n}");
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
            // Cria um registro RMI na porta 1099
            Registry servidorRegistro = LocateRegistry.createRegistry(1099);
            // Exporta o objeto 'this' (instância da classe ServidorImpl) como um objeto remoto
            IMensagem skeleton = (IMensagem) UnicastRemoteObject.exportObject(this, 0);
            // Registra o objeto remoto (skeleton) no registro RMI com o nome "servidorFortunes"
            servidorRegistro.rebind("servidorFortunes", skeleton);
            // Mensagem indicando que o servidor está aguardando conexões
            System.out.print("Servidor RMI: Aguardando conexoes...");

        } catch(Exception e) {
            // Em caso de exceção, imprime o rastreamento da pilha
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        ServidorImpl servidor = new ServidorImpl();
        servidor.iniciar();
    }    
}
