package distribuicao;

import java.net.*;
import java.io.*;

/**
 * Cliente UDP baseado nos conceitos de Comunicação por Datagrama
 * e primitivas de Invocação Remota (doOperation).
 */
public class UDP_Client {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso correto: java UDP_Client <mensagem> <servidor_host>");
            System.out.println("Exemplo: java UDP_Client \"Olá Servidor\" localhost");
            return;
        }

        DatagramSocket aSocket = null;

        try {
            // Cria um soquete em uma porta local livre
            aSocket = new DatagramSocket();

            byte[] m = args[0].getBytes(); // Empacotamento dos dados em bytes
            InetAddress aHost = InetAddress.getByName(args[1]); // Resolução do host
            int serverPort = 6789; // Porta padrão do servidor

            // Cria o datagrama de requisição
            DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);

            // Envia a requisição (Lança IOException se falhar)
            aSocket.send(request);
            System.out.println("Mensagem enviada para o servidor: " + args[0]);

            // Prepara o buffer para receber a resposta
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

            // Aguarda a resposta do servidor (Lança IOException se falhar)
            aSocket.receive(reply);

            // Exibe a resposta convertendo os dados recebidos para String
            System.out.println("Resposta recebida do servidor: " + new String(reply.getData(), 0, reply.getLength()));

        } catch (SocketException e) {
            System.out.println("Erro no Socket: " + e.getMessage());
        } catch (UnknownHostException e) {
            System.out.println("Host desconhecido: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Erro de E/S (IO): " + e.getMessage());
        } finally {
            if (aSocket != null) {
                aSocket.close();
            }
        }
    }
}