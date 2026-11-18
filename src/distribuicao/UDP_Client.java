package distribuicao;

import java.net.*;
import java.io.*;

public class UDP_Client {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso correto: java UDP_Client <mensagem_ou_comando> <servidor_host>");
            System.out.println("Exemplo de Soma: java UDP_Client \"SOMA 15 25\" localhost");
            System.out.println("Exemplo de Erro: java UDP_Client \"SOMA 15 abc\" localhost");
            return;
        }

        DatagramSocket aSocket = null;

        try {
            aSocket = new DatagramSocket();

            byte[] m = args[0].getBytes();
            InetAddress aHost = InetAddress.getByName(args[1]);
            int serverPort = 6789;

            DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
            aSocket.send(request);
            System.out.println("Mensagem enviada (" + m.length + " bytes) para o servidor.");

            // Buffer de resposta
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

            aSocket.receive(reply);

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