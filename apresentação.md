
# Explicação do Código – Labirinto com Backtracking

Esse código implementa o jogo de labirinto no terminal onde:

- Você controla o jogador manualmente
- Pode pedir pro algoritmo resolver sozinho (backtracking)
- O sistema compara seu caminho com o do algoritmo
  
Como se fosse um **gps inteligente** x **eu explorando na raça**.

---

## Classe `Main`

```java
int[][] labirinto = MazeFactory.criarLabirintoAleatorio(8, 8);

Posicao inicio = new Posicao(0, 0);
Posicao saida = new Posicao(labirinto.length - 1, labirinto[0].length - 1);

MazeGame jogo = new MazeGame(labirinto, inicio, saida);
jogo.iniciar(scanner);
```

Aqui o labirinto é gerado.

Cria um mapa novo toda vez que você roda o jogo.

---

## Classe `MazeGame`

Essa é a **engine do jogo**.

Ela controla:
- posição do jogador
- mapa
- comandos
- estado do jogo

---

### Loop principal

```java
while (emExecucao) {
    limparTela();
    imprimirCabecalho();
    imprimirMapaSemSolucao();
    imprimirMensagemStatus();

    String entrada = scanner.nextLine().trim().toUpperCase();
    char comando = entrada.charAt(0);

    switch (comando) {
        case 'W' -> moverJogador(-1, 0);
        case 'A' -> moverJogador(0, -1);
        case 'S' -> moverJogador(1, 0);
        case 'D' -> moverJogador(0, 1);
        case 'B' -> mostrarSolucaoComBacktracking();
    }
}
```

Esse loop mantém o jogo rodando.

---

### Movimento do jogador

```java
private void moverJogador(int deltaLinha, int deltaColuna) {
    int novaLinha = jogador.linha() + deltaLinha;
    int novaColuna = jogador.coluna() + deltaColuna;

    jogador = new Posicao(novaLinha, novaColuna);
    trilhaJogador[novaLinha][novaColuna] = true;
    caminhoJogador.add(jogador);
}
```

Cada comando altera a posição.

---

### Validação de movimento

```java
if (!estaDentroDoLabirinto(novaLinha, novaColuna)) {
    return;
}

if (labirintoOriginal[novaLinha][novaColuna] == 1) {
    return;
}
```

Se for `1`, é parede.

---

### Trilha do jogador

```java
trilhaJogador[novaLinha][novaColuna] = true;
caminhoJogador.add(jogador);
```

Marca onde você já passou.

---

## Backtracking (o cérebro do rolê)

### Classe `MazeSolver`
```java
public boolean resolver(int linhaAtual, int colunaAtual, int linhaFinal, int colunaFinal) {
    return buscarCaminho(linhaAtual, colunaAtual, linhaFinal, colunaFinal);
}
```

Essa classe resolve o labirinto automaticamente.

---

### Função recursiva

```java
private boolean buscarCaminho(int linhaAtual, int colunaAtual, int linhaFinal, int colunaFinal) {
    if (!ehPosicaoValida(linhaAtual, colunaAtual)) {
        return false;
    }

    visitado[linhaAtual][colunaAtual] = true;
    caminhoMarcado[linhaAtual][colunaAtual] = 1;
    caminhoFinal.add(new Posicao(linhaAtual, colunaAtual));

    if (linhaAtual == linhaFinal && colunaAtual == colunaFinal) {
        return true;
    }

    if (buscarCaminho(linhaAtual + 1, colunaAtual, linhaFinal, colunaFinal)
        || buscarCaminho(linhaAtual, colunaAtual + 1, linhaFinal, colunaFinal)
        || buscarCaminho(linhaAtual - 1, colunaAtual, linhaFinal, colunaFinal)
        || buscarCaminho(linhaAtual, colunaAtual - 1, linhaFinal, colunaFinal)) {
        return true;
    }

    caminhoMarcado[linhaAtual][colunaAtual] = 0;
    caminhoFinal.remove(caminhoFinal.size() - 1);
    return false;
}
```

Ela tenta caminhos até achar a saída.

---

### Verificação de posição válida

```java
private boolean ehPosicaoValida(int linha, int coluna) {
    return linha >= 0
        && linha < labirinto.length
        && coluna >= 0
        && coluna < labirinto[0].length
        && labirinto[linha][coluna] == 0
        && !visitado[linha][coluna];
}
```

Confere:
- está dentro do mapa
- não é parede
- não foi visitado

---

### Escolha de caminhos

```java
buscarCaminho(linhaAtual + 1, colunaAtual, linhaFinal, colunaFinal);
buscarCaminho(linhaAtual, colunaAtual + 1, linhaFinal, colunaFinal);
buscarCaminho(linhaAtual - 1, colunaAtual, linhaFinal, colunaFinal);
buscarCaminho(linhaAtual, colunaAtual - 1, linhaFinal, colunaFinal);
```

Testa direções:

- baixo
- direita
- cima
- esquerda

Como escolher portas em um corredor.

---

### Backtracking

```java
caminhoMarcado[linhaAtual][colunaAtual] = 0;
caminhoFinal.remove(caminhoFinal.size() - 1);
```

Se não achou o final, volta atrás.

Se entrou numa rua sem saída, dá meia volta e tenta outro caminho.

---

## Geração do Labirinto

### Classe `MazeFactory`

```java
public static int[][] criarLabirintoAleatorio(int linhas, int colunas) {
    int[][] labirinto = new int[linhas][colunas];
    boolean[][] caminhoGarantido = new boolean[linhas][colunas];

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

    return labirinto;
}
```

Responsável por criar o mapa.

---

### Caminho garantido

```java
private static void construirCaminhoGarantido(boolean[][] caminho, int linha, int coluna, int linhaFinal, int colunaFinal) {
    caminho[linha][coluna] = true;

    while (linha != linhaFinal || coluna != colunaFinal) {
        if (linha < linhaFinal) linha++;
        else if (coluna < colunaFinal) coluna++;

        caminho[linha][coluna] = true;
    }
}
```

Cria um caminho da entrada até a saída.

O jogo meio que “trapaceia” pra garantir que sempre existe solução.

---

### Aleatoriedade

```java
labirinto[i][j] = RANDOM.nextDouble() < 0.35 ? 1 : 0;
```

Cria paredes aleatórias.

cada partida é diferente.

---

### Verificação final

```java
MazeSolver solver = new MazeSolver(labirinto);
if (solver.resolver(0, 0, linhas - 1, colunas - 1)) {
    return labirinto;
}
```

Confirma que o labirinto é solucionável antes de lançar o mapa.  

---

## Comparação de caminhos

```java
if (caminhoJogador.equals(caminhoBacktracking)) {
    return "Mesmo caminho";
}
```

Compara:
- caminho do jogador
- caminho do algoritmo

---