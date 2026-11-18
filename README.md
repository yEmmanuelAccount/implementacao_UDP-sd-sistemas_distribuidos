# Implementação do Protocolo UDP - Java

Implementa uma aplicação cliente-servidor orientada a datagramas utilizando o protocolo **UDP** em Java (`java.net.DatagramSocket` e `DatagramPacket`), conforme os conceitos de Comunicação por Processos e Invocação Remota (Requisição-Resposta).

---

## Pré-requisitos e Instalação

1. **Java Development Kit (JDK)** instalado (versão 11 ou superior recomendada).
2. Clonar o repositório em sua máquina local:
   ```bash
   git clone https://github.com/yEmmanuelAccount/implementacao_UDP-sd-sistemas_distribuidos
   ```
3. Certifique-se de que a estrutura do projeto segue o padrão de pacotes distribuição dentro da pasta `src`.

## Como executar

### 0. Preparativos

Tenha certeza que os arquivos necessários estejam compilados antes das suas execuções. Para isso, execute o comando abaixo:

```bash
javac -d out src/distribuicao/UDP_Server.java src/distribuicao/UDP_Client.java
```

### 1. Inicie o Servidor (Terminal 01)

Inicie a classe do servidor, abrindo um terminal na raiz do projeto e executando:

```bash
java -cp out distribuicao.UDP_Server
```

O servidor ficará escutando na porta 6789, aguardando requisições.

### 2. Executar o Cliente (Terminal 02)

Inicie a classe do cliente, abrindo um segundo terminal na raiz do projeto e executando os comandos de teste. A seguir está um exemplo base:

```bash
java distribuicao.UDP_Client "<comando_ou_mensagem>" localhost
```

## Casos de Teste

Abaixo estão descritos os principais testes realizados para validar a robustez, os limites e o comportamento do protocolo UDP implementado.

### Teste 01: Operação de Soma (Comandos e Parâmetros)

O servidor foi programado para interpretar comandos no formato `SOMA <num1> <num2>`.

Caso de **SUCESSO**:
1. Comando enviado pelo cliente: `SOMA 10 10`.
2. Comando executado: O cliente envia os bytes da string, o servidor intercepta, faz o `split` do texto, calcula a soma e devolve o resultado.
3. Resultado esperado: O cliente recebe a resposta `Resultado da Soma: 20`.

Caso de **ERRO**:
1. Comando enviado pelo cliente: `SOMA 15 abc` ou apenas `SOMA`.
2. Comando executado: O servidor tenta converter letras em números ou falta parâmetros, gerando uma exceção interna (`NumberFormatException` ou `ArrayIndexOutOfBoundsException`).
3. Resultado esperado: O servidor trata a exceção internamente para não cair (crashar) e retorna uma mensagem de erro tratada ao cliente: `Erro: Parâmetros inválidos para a soma`.

### Teste 02: Limite de Tamanho e Truncamento de Mensagens

Como o protocolo UDP utiliza buffers de tamanho fixo de bytes para envio e recebimento de pacotes, esse caso testa o envio de mensagens que excedem o limite do buffer estruturado.

Caso de **SUCESSO**:
1. Comando enviado pelo cliente: Uma string com menos de 1000 caracteres.
2. Resultado esperado: O pacote é entregue integralmente e ecoado/processado sem perdas de dados.

Caso de **ERRO**:
1. Comando enviado pelo cliente: Uma string gigante gerada propositalmente com mais de 1000 caracteres (ultrapassando o vetor byte[1000] configurado no buffer do servidor/cliente).
2. Comando executado: O sistema operacional/rede descarta o excedente ou o buffer lê apenas os primeiros 1000 bytes.
3. Resultado esperado: Ocorre o truncamento da mensagem (o texto chega cortado no destino) sem que o protocolo UDP emita nenhum aviso prévio de perda ao remetente, evidenciando a natureza não confiável da camada de transporte UDP.

Para facilitação, abaixo está uma mensagem com mais de 1000 caracteres, com números apenas após o caracter 1000:

```txt
****************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************123456789
```

### Teste 3: Falha por Omissão (Servidor Desligado)

Cenário: Executar o UDP_Client enquanto o UDP_Server está completamente desligado.

Resultado esperado: O cliente envia os bytes para a rede, mas o método de recebimento (`aSocket.receive`) do cliente fica bloqueado indefinidamente à espera de uma resposta que nunca chegará, ilustrando a ausência de confirmações nativas e o conceito de falhas por omissão do protocolo UDP[cite: 1, 2].

## Comandos para os Casos de Teste

### Teste de Soma

Para o caso de sucesso, com o retorno esperado `20`:

```bash
java -cp out distribuicao.UDP_Client "SOMA 10 20" localhost
```

Para o caso de erro (parâmetros errados), com retorno esperado `Erro: Parâmetros inválidos. Insira apenas números inteiros.`

```bash
java -cp out distribuicao.UDP_Client "SOMA 15 abc" localhost
```

### Teste de Limite de Caracteres (Buffer)

Para o caso de erro (mais caracteres que o limite permitido), com retorno esperado `mensagem incompleta`:

```bash
java -cp out distribuicao.UDP_Client "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA123456789" localhost
```

### Teste de Falha

Com o `UDP_Server` encerrado, execute o cliente:

```bash
java distribuicao.UDP_Client "Teste Servidor Desligado" localhost
```

Resultado esperado: O cliente envia o datagrama, mas o método de recebimento (`aSocket.receive`) fica bloqueado indefinidamente à espera de uma resposta, ilustrando a ausência de confirmação nativa no protocolo.