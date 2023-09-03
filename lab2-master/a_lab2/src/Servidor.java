import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

	private static Socket socket;
	private static ServerSocket server;

	private static DataInputStream entrada;
	private static DataOutputStream saida;

	private int porta = 1025;

	public void iniciar() {
		System.out.println("Servidor iniciado na porta: " + porta);
		try {

			// Criar porta de recepcao
			server = new ServerSocket(porta);            
	
                socket = server.accept();
            	// Criar os fluxos de entrada e saida
    			entrada = new DataInputStream(socket.getInputStream());
    			saida = new DataOutputStream(socket.getOutputStream());
    			
                System.out.println("Conexão estabelecida com um cliente");

                String mensagem = entrada.readUTF();
                String resposta = processarMensagem(mensagem);
                
                saida.writeUTF(resposta);;
                saida.flush();

                socket.close();
                System.out.println("Conexão encerrada com o cliente");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		new Servidor().iniciar();

	}
	 private static String processarMensagem(String mensagem) {
	        try {
	            String[] partes = mensagem.split("\"");
	            String method = partes[3];
	            System.out.println(method);
	            String args = partes[7];

	            if ("read".equals(method)) {
	                String fortunaAleatoria = "Mensagem aleatória lida do banco de fortunas do servidor.";
	                String res = "{\"result\":\"" + fortunaAleatoria + "\"}";
	                System.out.println(res);
	                return res;
	            } else if ("write".equals(method)) {

	                return "{\"result\":\"" + args + "\"}";
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return "{\"result\":\"false\"}";
	    }
}
