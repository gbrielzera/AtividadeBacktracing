## Análise do Algoritmo de Backtracking

### Objetivo da função principal e das funções auxiliares

A função principal do algoritmo é:

```java
public boolean resolver(int linhaAtual, int colunaAtual, int linhaFinal, int colunaFinal) {
    // Apenas delega a execução para a função recursiva principal
    return buscarCaminho(linhaAtual, colunaAtual, linhaFinal, colunaFinal);
}
```

Ela inicia o processo de busca por um caminho válido no labirinto, chamando a função recursiva `buscarCaminho`.

A função auxiliar principal é:

```java
private boolean buscarCaminho(int linhaAtual, int colunaAtual, int linhaFinal, int colunaFinal) {

    // 1. Verifica se a posição atual pode ser usada
    if (!ehPosicaoValida(linhaAtual, colunaAtual)) {
        return false; // caminho inválido, não segue por aqui
    }

    // 2. Marca como visitado (evita loops infinitos)
    visitado[linhaAtual][colunaAtual] = true;

    // 3. Marca no caminho atual (como se fosse "pisar" na posição)
    caminhoMarcado[linhaAtual][colunaAtual] = 1;

    // 4. Guarda no histórico do caminho final
    caminhoFinal.add(new Posicao(linhaAtual, colunaAtual));
}
```

Responsável por explorar o labirinto, construindo um caminho passo a passo até encontrar a saída.

Outras funções auxiliares importantes:

```java
private boolean ehPosicaoValida(int linha, int coluna) {
    // Verifica limites do labirinto
    boolean dentro = linha >= 0
        && linha < labirinto.length
        && coluna >= 0
        && coluna < labirinto[0].length;

    if (!dentro) return false;

    // Verifica se não é parede
    boolean livre = labirinto[linha][coluna] == 0;

    // Verifica se ainda não foi visitado
    boolean naoVisitado = !visitado[linha][coluna];

    return livre && naoVisitado;
}
```

Valida se uma posição pode ser usada na solução.

---

### Condição de parada da recursão

```java
// Se chegou exatamente na posição final, achou a solução
if (linhaAtual == linhaFinal && colunaAtual == colunaFinal) {
    return true;
}
```

A recursão termina quando o algoritmo chega na posição final do labirinto.

---

### Critério para verificar se uma escolha é válida

```java
private boolean ehPosicaoValida(int linha, int coluna) {
    return linha >= 0                          // dentro da matriz (linha)
        && linha < labirinto.length
        && coluna >= 0                         // dentro da matriz (coluna)
        && coluna < labirinto[0].length
        && labirinto[linha][coluna] == 0       // não é parede
        && !visitado[linha][coluna];           // não foi visitado ainda
}
```

Uma posição é válida quando:

* Está dentro dos limites do labirinto
* Não é uma parede (`0` representa caminho livre)
* Ainda não foi visitada

---

### Momento em que o algoritmo avança

```java
// Tenta todas as direções possíveis (DFS)
if (
    buscarCaminho(linhaAtual + 1, colunaAtual, linhaFinal, colunaFinal) || // baixo
    buscarCaminho(linhaAtual, colunaAtual + 1, linhaFinal, colunaFinal) || // direita
    buscarCaminho(linhaAtual - 1, colunaAtual, linhaFinal, colunaFinal) || // cima
    buscarCaminho(linhaAtual, colunaAtual - 1, linhaFinal, colunaFinal)    // esquerda
) {
    return true; // se qualquer direção funcionar, já encontrou caminho
}
```

O algoritmo avança quando tenta explorar novas posições vizinhas.

Aqui acontece um **DFS (Depth-First Search)** na prática.

---

### Momento do retrocesso (backtracking)

```java
// Nenhuma direção deu certo , desfaz a escolha atual

caminhoMarcado[linhaAtual][colunaAtual] = 0; 
// remove do "mapa visual" do caminho

caminhoFinal.remove(caminhoFinal.size() - 1); 
// remove do histórico do caminho

return false; 
// sinaliza que esse caminho não leva à solução
```

O retrocesso ocorre quando nenhuma das tentativas leva à solução.

Nesse momento:

* A posição atual é removida do caminho
* O algoritmo volta para a chamada anterior
* Uma nova alternativa é testada

---

## Estruturas de Dados Utilizadas

### Matriz do labirinto

```java
int[][] labirinto;
```

Representa o mapa do jogo.

* `0` : caminho livre
* `1` : parede

O labirinto é naturalmente uma grade (linhas e colunas), e a matriz permite acesso direto a qualquer posição. Ela define onde o algoritmo pode ou não se mover.

---

### Matriz de visitados

```java
boolean[][] visitado;
```

Controla quais posições já foram exploradas, evita que o algoritmo entre em loops infinitos e impede revisitar posições já testadas durante o backtracking.

---

### Matriz de caminho marcado

```java
int[][] caminhoMarcado;
```

Armazena o caminho atual sendo testado.

* `1` : faz parte do caminho
* `0` : não faz

Permite visualizar o caminho encontrado e também desfazer passos facilmente pois mostra visualmente a solução encontrada no labirinto.

---

### Lista de posições (caminho final)

```java
List<Posicao> caminhoFinal;
```

Guarda a sequência de posições percorridas.

Uma lista mantém a ordem dos passos, que é essencial para reconstruir o caminho, e armazena o caminho completo da entrada até a saída.

---

### Record `Posicao`

```java
record Posicao(int linha, int coluna) {}
```

Representa uma coordenada no labirinto.

É uma estrutura simples e imutável, boa para representar pontos. Facilita o armazenamento e comparação de posições.

---

## Exemplo de Entrada e Saída

### Entrada (labirinto)

```text
E . # .
# . # .
. . . #
# # . X
```

Legenda:

* `E`: entrada (0,0)
* `X`: saída
* `.`: caminho livre
* `#`: parede

---

### Execução do algoritmo

O algoritmo começa em `(0,0)` e tenta explorar os caminhos:

1. Tenta descer, está bloqueado
2. Tenta direita, caminho livre
3. Continua explorando até encontrar a saída
4. Quando encontra um beco sem saída, volta (backtracking)
5. Testa outra direção

---

### Saída (caminho encontrado)

```text
E * # .
# * # .
. * * #
# # * X
```

* `*` representa o caminho encontrado pelo algoritmo

---

### Explicação do comportamento

O algoritmo percorre o labirinto em profundidade (**DFS**):

* Vai avançando enquanto encontra caminhos válidos
* Marca o caminho atual
* Ao encontrar um bloqueio, desfaz o último passo
* Continua tentando até encontrar a saída

Ele **não procura o caminho mais curto**, mas sim **o primeiro caminho válido encontrado**.

---
