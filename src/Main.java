import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Gera um labirinto novo sempre que o programa comeca.
        int[][] labirinto = MazeFactory.criarLabirintoAleatorio(8, 8);

        Posicao inicio = new Posicao(0, 0);
        Posicao saida = new Posicao(labirinto.length - 1, labirinto[0].length - 1);

        try (Scanner scanner = new Scanner(System.in)) {
            MazeGame jogo = new MazeGame(labirinto, inicio, saida);
            jogo.iniciar(scanner);
        }
    }
}

class MazeGame {
    private int[][] labirintoOriginal;
    private final Posicao inicio;
    private Posicao saida;
    private Posicao jogador;
    private boolean[][] trilhaJogador;
    private List<Posicao> caminhoJogador;
    private boolean[][] solucaoVisivel;
    private String mensagemStatus;

    public MazeGame(int[][] labirintoOriginal, Posicao inicio, Posicao saida) {
        this.labirintoOriginal = copiarMatriz(labirintoOriginal);
        this.inicio = inicio;
        this.saida = saida;
        reiniciar();
    }

    public void iniciar(Scanner scanner) {
        boolean emExecucao = true;

        while (emExecucao) {
            // A cada rodada, a tela e redesenhada como se fosse um "quadro" novo do jogo.
            limparTela();
            imprimirCabecalho();
            imprimirMapaSemSolucao();
            imprimirMensagemStatus();

            if (jogador.equals(saida)) {
                prepararResumoFinal();
                limparTela();
                imprimirCabecalho();
                imprimirMapaSemSolucao();
                imprimirMensagemStatus();
                System.out.println("Voce chegou na saida. Labirinto concluido!");
                return;
            }

            System.out.print("Digite um comando: ");
            String entrada = scanner.nextLine().trim().toUpperCase();

            if (entrada.isEmpty()) {
                mensagemStatus = "Informe um comando valido.";
                continue;
            }

            char comando = entrada.charAt(0);
            switch (comando) {
                case 'W' -> moverJogador(-1, 0);
                case 'A' -> moverJogador(0, -1);
                case 'S' -> moverJogador(1, 0);
                case 'D' -> moverJogador(0, 1);
                case 'B' -> mostrarSolucaoComBacktracking();
                case 'R' -> {
                    reiniciar();
                    mensagemStatus = "Jogo reiniciado.";
                }
                case 'N' -> gerarNovoLabirinto();
                case 'Q' -> {
                    limparTela();
                    System.out.println("Jogo encerrado.");
                    emExecucao = false;
                }
                default -> mensagemStatus = "Comando invalido. Use W, A, S, D, B, R, N ou Q.";
            }
        }
    }

    private void moverJogador(int deltaLinha, int deltaColuna) {
        int novaLinha = jogador.linha() + deltaLinha;
        int novaColuna = jogador.coluna() + deltaColuna;

        if (!estaDentroDoLabirinto(novaLinha, novaColuna)) {
            mensagemStatus = "Esse movimento sai dos limites do labirinto.";
            return;
        }

        if (labirintoOriginal[novaLinha][novaColuna] == 1) {
            mensagemStatus = "Existe uma parede nessa direcao.";
            return;
        }

        // Se o jogador andou, a solucao antiga some para o mapa voltar ao modo normal.
        jogador = new Posicao(novaLinha, novaColuna);
        trilhaJogador[novaLinha][novaColuna] = true;
        // Aqui guardamos a sequencia real de passos do jogador, como um historico da partida.
        caminhoJogador.add(jogador);
        solucaoVisivel = null;
        mensagemStatus = "Voce se moveu para (" + novaLinha + ", " + novaColuna + ").";
    }

    private void mostrarSolucaoComBacktracking() {
        MazeSolver solver = new MazeSolver(labirintoOriginal);
        boolean encontrouSolucao = solver.resolver(inicio.linha(), inicio.coluna(), saida.linha(), saida.coluna());

        if (!encontrouSolucao) {
            mensagemStatus = "O algoritmo nao encontrou uma saida para este labirinto.";
            return;
        }

        // Copia o caminho encontrado para exibir no mapa com asteriscos.
        solucaoVisivel = solver.copiarCaminhoMarcado();
        mensagemStatus = "Solucao automatica encontrada com backtracking.";
        mensagemStatus += System.lineSeparator() + "Backtracking: " + solver.formatarCaminhoHorizontal();
        mensagemStatus += System.lineSeparator() + "Jogador: " + formatarCaminhoJogador();
        mensagemStatus += System.lineSeparator() + compararCaminhos(solver.getCaminhoFinal());
    }

    private void prepararResumoFinal() {
        // Quando o jogador vence, mostramos o proprio caminho ao lado da rota encontrada pelo algoritmo.
        MazeSolver solver = new MazeSolver(labirintoOriginal);
        boolean encontrouSolucao = solver.resolver(inicio.linha(), inicio.coluna(), saida.linha(), saida.coluna());

        if (!encontrouSolucao) {
            mensagemStatus = "Voce chegou na saida. Nao foi possivel comparar com o backtracking.";
            return;
        }

        solucaoVisivel = solver.copiarCaminhoMarcado();
        mensagemStatus = "Parabens! Voce terminou o labirinto.";
        mensagemStatus += System.lineSeparator() + "Backtracking: " + solver.formatarCaminhoHorizontal();
        mensagemStatus += System.lineSeparator() + "Jogador: " + formatarCaminhoJogador();
        mensagemStatus += System.lineSeparator() + compararCaminhos(solver.getCaminhoFinal());
    }

    private void imprimirMapaSemSolucao() {
        imprimirMapaAtual();
        System.out.println();
    }

    private void imprimirMapaAtual() {
        System.out.println("Mapa atual:");

        for (int i = 0; i < labirintoOriginal.length; i++) {
            for (int j = 0; j < labirintoOriginal[i].length; j++) {
                if (jogador.linha() == i && jogador.coluna() == j) {
                    System.out.print("P ");
                } else if (inicio.linha() == i && inicio.coluna() == j) {
                    System.out.print("E ");
                } else if (saida.linha() == i && saida.coluna() == j) {
                    System.out.print("X ");
                } else if (labirintoOriginal[i][j] == 1) {
                    System.out.print("# ");
                } else if (solucaoVisivel != null && solucaoVisivel[i][j]) {
                    System.out.print("* ");
                } else if (trilhaJogador[i][j]) {
                    System.out.print("+ ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }

    private void imprimirCabecalho() {
        System.out.println("Jogo do Labirinto com Backtracking");
        System.out.println("----------------------------------");
        System.out.println("Comandos: W/A/S/D mover | B resolver | R reiniciar | N novo labirinto | Q sair");
        System.out.println("Legenda: P jogador | E entrada | X saida | # parede | . livre | + trilha | * solucao");
        System.out.println();
    }

    private void imprimirMensagemStatus() {
        if (mensagemStatus == null || mensagemStatus.isBlank()) {
            System.out.println("Use W, A, S, D para se mover ou B para ver a solucao.");
        } else {
            System.out.println(mensagemStatus);
        }
        System.out.println();
    }

    private void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void reiniciar() {
        // Reiniciar mantem o mesmo labirinto, mas apaga trilha, solucao e posicao antiga do jogador.
        this.jogador = inicio;
        this.trilhaJogador = new boolean[labirintoOriginal.length][labirintoOriginal[0].length];
        this.trilhaJogador[inicio.linha()][inicio.coluna()] = true;
        this.caminhoJogador = new ArrayList<>();
        this.caminhoJogador.add(inicio);
        this.solucaoVisivel = null;
        this.mensagemStatus = null;
    }

    private void gerarNovoLabirinto() {
        // Aqui nasce um mapa totalmente novo, como se fosse uma nova fase.
        this.labirintoOriginal = MazeFactory.criarLabirintoAleatorio(labirintoOriginal.length, labirintoOriginal[0].length);
        this.saida = new Posicao(labirintoOriginal.length - 1, labirintoOriginal[0].length - 1);
        reiniciar();
        mensagemStatus = "Novo labirinto aleatorio gerado.";
    }

    private String formatarCaminhoJogador() {
        return formatarCaminhoHorizontal(caminhoJogador);
    }

    private String compararCaminhos(List<Posicao> caminhoBacktracking) {
        // A comparacao mostra se o jogador seguiu exatamente a mesma rota ou apenas chegou por outro caminho.
        if (caminhoJogador.equals(caminhoBacktracking)) {
            return "Comparacao: voce fez exatamente o mesmo caminho do backtracking.";
        }

        Posicao ultimaPosicaoJogador = caminhoJogador.get(caminhoJogador.size() - 1);
        boolean chegouNaSaida = ultimaPosicaoJogador.equals(saida);

        if (chegouNaSaida) {
            return "Comparacao: voce chegou na saida, mas por uma rota diferente do backtracking.";
        }

        return "Comparacao: seu caminho ainda eh parcial ou diferente; o backtracking mostrou uma rota completa ate a saida.";
    }

    private String formatarCaminhoHorizontal(List<Posicao> caminho) {
        StringBuilder texto = new StringBuilder();

        for (int i = 0; i < caminho.size(); i++) {
            Posicao posicao = caminho.get(i);
            texto.append("(")
                    .append(posicao.linha())
                    .append(", ")
                    .append(posicao.coluna())
                    .append(")");

            if (i < caminho.size() - 1) {
                texto.append(" -> ");
            }
        }

        return texto.toString();
    }

    private boolean estaDentroDoLabirinto(int linha, int coluna) {
        return linha >= 0
                && linha < labirintoOriginal.length
                && coluna >= 0
                && coluna < labirintoOriginal[0].length;
    }

    private int[][] copiarMatriz(int[][] matrizOriginal) {
        int[][] copia = new int[matrizOriginal.length][matrizOriginal[0].length];

        for (int i = 0; i < matrizOriginal.length; i++) {
            System.arraycopy(matrizOriginal[i], 0, copia[i], 0, matrizOriginal[i].length);
        }

        return copia;
    }
}

class MazeSolver {
    private final int[][] labirinto;
    private final boolean[][] visitado;
    private final int[][] caminhoMarcado;
    private final List<Posicao> caminhoFinal;

    public MazeSolver(int[][] labirinto) {
        this.labirinto = labirinto;
        this.visitado = new boolean[labirinto.length][labirinto[0].length];
        this.caminhoMarcado = new int[labirinto.length][labirinto[0].length];
        this.caminhoFinal = new ArrayList<>();
    }

    // Funcao principal da busca: tenta construir um caminho valido da entrada ate a saida.
    public boolean resolver(int linhaAtual, int colunaAtual, int linhaFinal, int colunaFinal) {
        return buscarCaminho(linhaAtual, colunaAtual, linhaFinal, colunaFinal);
    }

    /*
     * Funcao recursiva de backtracking.
     * Ela faz uma tentativa de avancar no labirinto, guarda a escolha parcial
     * e desfaz essa escolha se descobrir que ela nao leva a uma solucao.
     * E como andar em um corredor novo: se ele nao levar a saida, o algoritmo volta
     * ate a ultima encruzilhada e tenta outro caminho.
     */
    private boolean buscarCaminho(int linhaAtual, int colunaAtual, int linhaFinal, int colunaFinal) {
        // Verifica se a posicao atual pode participar da solucao parcial.
        if (!ehPosicaoValida(linhaAtual, colunaAtual)) {
            return false;
        }

        // Marca a posicao atual como visitada para evitar ciclos.
        visitado[linhaAtual][colunaAtual] = true;

        // Registra essa tentativa no caminho parcial construido ate agora.
        // Pense como deixar pegadas no chao para lembrar por onde ja passou.
        caminhoMarcado[linhaAtual][colunaAtual] = 1;
        caminhoFinal.add(new Posicao(linhaAtual, colunaAtual));

        // Condicao de parada da recursao: chegou na saida do labirinto.
        if (linhaAtual == linhaFinal && colunaAtual == colunaFinal) {
            return true;
        }

        // Momento em que o algoritmo avanca para novas tentativas.
        // A ordem aqui e: baixo, direita, cima e esquerda.
        // Ele testa uma porta por vez; se uma funcionar, nao precisa testar as outras.
        if (buscarCaminho(linhaAtual + 1, colunaAtual, linhaFinal, colunaFinal)
                || buscarCaminho(linhaAtual, colunaAtual + 1, linhaFinal, colunaFinal)
                || buscarCaminho(linhaAtual - 1, colunaAtual, linhaFinal, colunaFinal)
                || buscarCaminho(linhaAtual, colunaAtual - 1, linhaFinal, colunaFinal)) {
            return true;
        }

        /*
         * Se nenhuma tentativa deu certo, ocorre o retrocesso.
         * O algoritmo desfaz a escolha atual para voltar ao estado anterior
         * e procurar outro caminho possivel.
         * E como perceber que entrou em um beco sem saida e apagar essa parte
         * das pegadas para tentar outra rota.
         */
        caminhoMarcado[linhaAtual][colunaAtual] = 0;
        caminhoFinal.remove(caminhoFinal.size() - 1);
        return false;
    }

    // Criterio de validade: a posicao deve estar dentro da matriz, ser livre e ainda nao visitada.
    private boolean ehPosicaoValida(int linha, int coluna) {
        boolean dentroDosLimites = linha >= 0
                && linha < labirinto.length
                && coluna >= 0
                && coluna < labirinto[0].length;

        if (!dentroDosLimites) {
            return false;
        }

        boolean livre = labirinto[linha][coluna] == 0;
        boolean naoVisitada = !visitado[linha][coluna];
        return livre && naoVisitada;
    }

    public boolean[][] copiarCaminhoMarcado() {
        boolean[][] copia = new boolean[caminhoMarcado.length][caminhoMarcado[0].length];

        for (int i = 0; i < caminhoMarcado.length; i++) {
            for (int j = 0; j < caminhoMarcado[i].length; j++) {
                copia[i][j] = caminhoMarcado[i][j] == 1;
            }
        }

        return copia;
    }

    public String formatarCaminhoHorizontal() {
        return formatarCaminhoHorizontal(caminhoFinal);
    }

    public List<Posicao> getCaminhoFinal() {
        return new ArrayList<>(caminhoFinal);
    }

    private String formatarCaminhoHorizontal(List<Posicao> caminho) {
        StringBuilder texto = new StringBuilder();

        for (int i = 0; i < caminho.size(); i++) {
            Posicao posicao = caminho.get(i);
            texto.append("(")
                    .append(posicao.linha())
                    .append(", ")
                    .append(posicao.coluna())
                    .append(")");

            if (i < caminho.size() - 1) {
                texto.append(" -> ");
            }
        }

        return texto.toString();
    }
}

record Posicao(int linha, int coluna) {
}

class MazeFactory {
    private static final Random RANDOM = new Random();

    private MazeFactory() {
    }

    public static int[][] criarLabirintoAleatorio(int linhas, int colunas) {
        while (true) {
            int[][] labirinto = new int[linhas][colunas];
            boolean[][] caminhoGarantido = new boolean[linhas][colunas];

            // Primeiro garantimos um "corredor secreto" da entrada ate a saida.
            construirCaminhoGarantido(caminhoGarantido, 0, 0, linhas - 1, colunas - 1);

            for (int i = 0; i < linhas; i++) {
                for (int j = 0; j < colunas; j++) {
                    if (caminhoGarantido[i][j]) {
                        labirinto[i][j] = 0;
                    } else {
                        labirinto[i][j] = RANDOM.nextDouble() < 0.35 ? 1 : 0;
                    }
                }
            }

            // Depois abrimos alguns vizinhos para o labirinto nao ficar reto demais.
            abrirVizinhosDoCaminho(labirinto, caminhoGarantido);
            labirinto[0][0] = 0;
            labirinto[linhas - 1][colunas - 1] = 0;

            // Essa verificacao final confirma que o mapa realmente pode ser resolvido.
            MazeSolver solver = new MazeSolver(labirinto);
            if (solver.resolver(0, 0, linhas - 1, colunas - 1)) {
                return labirinto;
            }
        }
    }

    private static void construirCaminhoGarantido(boolean[][] caminho, int linha, int coluna, int linhaFinal, int colunaFinal) {
        caminho[linha][coluna] = true;

        while (linha != linhaFinal || coluna != colunaFinal) {
            boolean podeDescer = linha < linhaFinal;
            boolean podeIrDireita = coluna < colunaFinal;

            // O caminho base vai descendo ou andando para a direita ate alcancar a saida.
            if (podeDescer && podeIrDireita) {
                if (RANDOM.nextBoolean()) {
                    linha++;
                } else {
                    coluna++;
                }
            } else if (podeDescer) {
                linha++;
            } else {
                coluna++;
            }

            caminho[linha][coluna] = true;
        }
    }

    private static void abrirVizinhosDoCaminho(int[][] labirinto, boolean[][] caminho) {
        int[] deltaLinha = {-1, 1, 0, 0};
        int[] deltaColuna = {0, 0, -1, 1};

        for (int i = 0; i < caminho.length; i++) {
            for (int j = 0; j < caminho[i].length; j++) {
                if (!caminho[i][j]) {
                    continue;
                }

                for (int k = 0; k < deltaLinha.length; k++) {
                    int novaLinha = i + deltaLinha[k];
                    int novaColuna = j + deltaColuna[k];

                    if (novaLinha >= 0
                            && novaLinha < labirinto.length
                            && novaColuna >= 0
                            && novaColuna < labirinto[0].length
                            && RANDOM.nextDouble() < 0.30) {
                        labirinto[novaLinha][novaColuna] = 0;
                    }
                }
            }
        }
    }
}
