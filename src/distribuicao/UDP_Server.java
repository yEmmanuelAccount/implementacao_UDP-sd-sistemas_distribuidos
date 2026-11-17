package distribuicao;

import java.net.*;
import java.io.*;

/**
 * Servidor UDP baseado nos conceitos de Comunicação por Datagrama
 * e primitivas de Requisição-Resposta (getRequest / sendReply).
 */
public class UDP_Server {
    public static void main(String[] args) {
        DatagramSocket aSocket = null;
        int serverPort = 6789;

        try {
            aSocket = new DatagramSocket(serverPort);
            byte[] buffer = new byte[1000];

            System.out.println("Servidor UDP iniciado na porta " + serverPort + ". Aguardando requisições...");

            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);

                String mensagemRecebida = new String(request.getData(), 0, request.getLength());
                System.out.println("Requisição recebida de " + request.getAddress() + ":" + request.getPort()
                        + " -> Conteúdo: " + mensagemRecebida);

                DatagramPacket reply = new DatagramPacket(
                        request.getData(),
                        request.getLength(),
                        request.getAddress(),
                        request.getPort()
                );

                aSocket.send(reply);
            }

        } catch (SocketException e) {
            System.out.println("Erro no Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Erro de E/S (IO): " + e.getMessage());
        } finally {
            if (aSocket != null) {
                aSocket.close();
            }
        }
    }
}