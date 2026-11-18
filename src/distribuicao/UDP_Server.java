package distribuicao;

import java.net.*;
import java.io.*;

public class UDP_Server {
    public static void main(String[] args) {
        DatagramSocket aSocket = null;
        int serverPort = 6789;

        try {
            aSocket = new DatagramSocket(serverPort);
            // Buffer de 1000 bytes para recebimento de pacotes
            byte[] buffer = new byte[1000];

            System.out.println("Servidor UDP iniciado na porta " + serverPort + ". Aguardando requisições...");

            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request); // Bloqueia aguardando pacote (Cenário de falha por omissão se o servidor estivesse desligado)

                String mensagemRecebida = new String(request.getData(), 0, request.getLength()).trim();
                System.out.println("Requisição recebida de " + request.getAddress() + ":" + request.getPort()
                        + " (Tamanho: " + request.getLength() + " bytes) -> Conteúdo: " +
                        (mensagemRecebida.length() > 100 ? mensagemRecebida.substring(0, 100) + "..." : mensagemRecebida));

                String respostaProcessada;

                // --- TESTE 1: Processamento do comando SOMA (Sucesso e Erro) ---
                if (mensagemRecebida.startsWith("SOMA")) {
                    try {
                        String[] partes = mensagemRecebida.split("\\s+");

                        // Caso de Erro: Faltam parâmetros
                        if (partes.length < 3) {
                            respostaProcessada = "Erro: Parâmetros insuficientes. Use o formato: SOMA <num1> <num2>";
                        } else {
                            // Tenta converter para inteiro (pode gerar NumberFormatException se passarem letras)
                            int num1 = Integer.parseInt(partes[1]);
                            int num2 = Integer.parseInt(partes[2]);
                            int soma = num1 + num2;

                            // Caso de Sucesso
                            respostaProcessada = "Resultado da Soma: " + soma;
                        }
                    } catch (NumberFormatException e) {
                        // Caso de Erro: Parâmetros inválidos (ex: letras em vez de números)
                        respostaProcessada = "Erro: Parâmetros inválidos. Insira apenas números inteiros.";
                    } catch (Exception e) {
                        respostaProcessada = "Erro interno ao processar o comando.";
                    }
                } else {
                    // Comportamento padrão (Eco / Teste de truncamento)
                    respostaProcessada = "Eco (Recebido com sucesso): " + mensagemRecebida;
                }

                // Envia a resposta de volta ao cliente
                byte[] respostaBytes = respostaProcessada.getBytes();
                DatagramPacket reply = new DatagramPacket(
                        respostaBytes,
                        respostaBytes.length,
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