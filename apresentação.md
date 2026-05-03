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
