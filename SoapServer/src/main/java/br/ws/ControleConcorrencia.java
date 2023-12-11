package br.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ControleConcorrencia {

    public static class TrancaInfo {

        private final String idCliente;
        private final long timestamp;
        private final String op;

        public TrancaInfo(String idCliente, String op) {
            this.idCliente = idCliente;
            this.op = op;
            this.timestamp = System.currentTimeMillis();
        }

        public String getIdCliente() {
            return idCliente;
        }

        public String getOp() {
            return op;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    private static final Map<String, List<TrancaInfo>> locks = new HashMap<>();
    private static final Map<String, BlockingQueue<TrancaInfo>> filasEspera = new HashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final ScheduledExecutorService filaEsperaScheduler = Executors.newScheduledThreadPool(1);

    public static boolean adquirirLock(String idFilme, String idCliente, String op) {
        synchronized (locks) {
            // Verificar se o recurso está bloqueado por outro cliente
            if (!verificarConcorrencia(idFilme, idCliente)) {
                System.out.print("Lock: " + op + idCliente + "[" + idFilme + "]   ");
                TrancaInfo trancaInfo = new TrancaInfo(idCliente, op);

                // Verificar se já existe uma lista para essa chave
                locks.putIfAbsent(idFilme, new ArrayList<>());
                locks.get(idFilme).add(trancaInfo);

                return true;
            }

            System.out.print("Fila: " + op + idCliente + "[" + idFilme + "]   ");
            filasEspera.putIfAbsent(idFilme, new LinkedBlockingQueue<>());
            TrancaInfo trancaInfo = new TrancaInfo(idCliente, op);
            filasEspera.get(idFilme).add(trancaInfo);

            return false; // Lock não adquirido
        }
    }

    public static void liberarLock(String idFilme, String idCliente, String op) {
        synchronized (locks) {
            // Remover a transação que possui o mesmo idFilme e id
            locks.remove(idFilme);
            // Notificar o próximo cliente na fila de espera, se houver
            if (filasEspera.containsKey(idFilme) && !filasEspera.get(idFilme).isEmpty()) {
                TrancaInfo proximaTrancaInfo = filasEspera.get(idFilme).poll();
                if (proximaTrancaInfo != null) {
                    adquirirLock(idFilme, proximaTrancaInfo.getIdCliente(), op);
                }
            }
        }
    }

    // Método para verificar concorrência para um idFilme e idCliente específicos
    private static boolean verificarConcorrencia(String idFilme, String idCliente) {
        synchronized (locks) {
            // Verificar se a chave (idFilme) está presente no mapa
            if (locks.containsKey(idFilme)) {
                // Iterar sobre a lista associada à chave
                for (TrancaInfo trancaInfo : locks.get(idFilme)) {
                    String clienteExistente = trancaInfo.getIdCliente();

                    // Verificar se há outro idCliente diferente associado ao mesmo idFilme
                    if (!idCliente.equals(clienteExistente)) {
                        // Concorrência detectada
                        return true;
                    }
                }
            }
            return false;
        }
    }

    // Método para iniciar o agendamento da liberação de trancas
    public static void iniciarAgendamentoLiberacaoTrancas() {
        scheduler.scheduleAtFixedRate(() -> {
            liberarTrancasExpiradas();
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public static void iniciarAgendamentoVerificacaoFilasEspera() {
        filaEsperaScheduler.scheduleAtFixedRate(() -> {
            verificarFilasEspera();
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    private static void verificarFilasEspera() {
        synchronized (locks) {
            // Iterar sobre as operações na fila de espera e tentar adquirir o lock
            Iterator<Map.Entry<String, BlockingQueue<TrancaInfo>>> iterator = filasEspera.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, BlockingQueue<TrancaInfo>> entry = iterator.next();
                String idFilme = entry.getKey();
                BlockingQueue<TrancaInfo> filaEspera = entry.getValue();

                // Verificar se a fila de espera não está vazia
                if (!filaEspera.isEmpty()) {
                    TrancaInfo trancaInfo = filaEspera.peek(); // Obter o próximo elemento sem removê-lo
                    long tempoAtual = System.currentTimeMillis();
                    long tempoTransacao = tempoAtual - trancaInfo.getTimestamp();

                    // Verificar se o recurso está disponível para a próxima operação na fila
                    if (!verificarConcorrencia(idFilme, trancaInfo.getIdCliente())) {

                        // (por exemplo, enviar uma mensagem para interromper a execução)
                        filaEspera.poll(); // Remover o elemento da fila após adquirir o lock
                        adquirirLock(idFilme, trancaInfo.getIdCliente(), trancaInfo.op);
                        break;
                    }
                    // Se a transação estiver aguardando por mais de 2 segundos, remova 
                    if (tempoTransacao > 2000) {
                        System.out.print("DeadLock aqui");
                        System.out.print("Abort: " + trancaInfo.getOp() + trancaInfo.getIdCliente() + "[" + idFilme + "]   ");
                        filaEspera.poll(); // Remover o elemento da fila

                        break;
                    }
                }
                // Remover a fila se estiver vazia
                if (filaEspera.isEmpty()) {
                    iterator.remove();
                }
            }
        }
    }

/// Método para liberar trancas expiradas (executado periodicamente)
    private static void liberarTrancasExpiradas() {
        synchronized (locks) {
            long tempoAtual = System.currentTimeMillis();

            // Armazenar entradas a serem removidas
            List<String> trancasRemovidas = new ArrayList<>();

            // Iterar sobre as trancas e verificar se expiraram
            for (Map.Entry<String, List<TrancaInfo>> entry : locks.entrySet()) {
                List<TrancaInfo> trancaInfos = entry.getValue();

                for (TrancaInfo trancaInfo : trancaInfos) {
                    if (tempoAtual - trancaInfo.getTimestamp() > 1000) {
                        String idFilme = entry.getKey();
                        String idCliente = trancaInfo.getIdCliente();

                        // Obter o lock da fila de espera
                        synchronized (filasEspera) {
                            // Verificar se há outra operação na fila com o mesmo idCliente
                            if (!verificarOperacaoNaFila(idFilme, idCliente)) {
                                trancasRemovidas.add(idFilme);
                            }
                        }
                    }
                }
            }

            // Remover as trancas expiradas
            trancasRemovidas.forEach(idFilme -> {
                List<TrancaInfo> trancaInfos = locks.remove(idFilme);

                if (trancaInfos != null) {
                    trancaInfos.forEach(trancaInfo -> {
                        System.out.print("Unlock: " + trancaInfo.op
                                + trancaInfo.getIdCliente()
                                + "[" + idFilme + "]   ");
                    });
                    if (trancaInfos.isEmpty()) {
                        locks.remove(idFilme);
                    }
                }
            });
        }
    }

    private static boolean verificarOperacaoNaFila(String idFilme, String idCliente) {
        synchronized (locks) {
            // Verificar se há outra operação na fila com o mesmo idCliente
            return filasEspera.values().stream()
                    .anyMatch(queue -> queue.stream().anyMatch(trancaInfo -> trancaInfo.getIdCliente().equals(idCliente)));

        }
    }

    private static String transacaoToString(String idFilme, TrancaInfo trancaInfo) {
        return idFilme + "-" + trancaInfo.getIdCliente() + "-" + trancaInfo.getOp();
    }

    public static void imprimirFilasEspera() {
        synchronized (locks) {
            System.out.println("Estado das filas de espera:");

            for (Map.Entry<String, BlockingQueue<TrancaInfo>> entry : filasEspera.entrySet()) {
                String idFilme = entry.getKey();
                BlockingQueue<TrancaInfo> filaEspera = entry.getValue();

                System.out.print("Fila de espera para " + idFilme + ": ");
                filaEspera.forEach(trancaInfo -> System.out.print("id: " + trancaInfo.getIdCliente()));
                System.out.println();
            }
        }
    }

    public static void imprimirLocks() {
        synchronized (locks) {
            System.out.println("Estado dos locks:");

            for (Map.Entry<String, List<TrancaInfo>> entry : locks.entrySet()) {
                String idFilme = entry.getKey();
                List<TrancaInfo> trancaInfos = entry.getValue();
                for (TrancaInfo trancaInfo : trancaInfos) {
                    System.out.println("Lock: " + trancaInfo.op + trancaInfo.getIdCliente() + "[" + idFilme + "]");
                }
            }
        }
    }

}
