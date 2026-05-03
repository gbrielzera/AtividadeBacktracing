# Resolução de Labirinto com Backtracking

## Introdução

Ativdade da disciplina de Inteligência Artificial.

O algoritmo de **backtracking** é uma técnica clássica de resolução de problemas baseada em tentativa e erro (força bruta). Ele explora possíveis soluções construindo caminhos passo a passo e, quando identifica que uma escolha que não leva ao objetivo, retorna (faz o backtrack) para tentar outras alternativas.

Neste trabalho, o backtracking foi utilizado para resolver automaticamente um labirinto, encontrando um caminho válido entre a entrada e a saída.

---

## Problema

Dado um labirinto representado por uma matriz:

- `0` → caminho livre  
- `1` → parede  

O objetivo é encontrar um caminho da posição inicial `(0,0)` até a posição final `(n-1, m-1)`.

---

## Estratégia de Backtracking

O algoritmo funciona tipo alguém explorando um labirinto real:

1. Começa no início.
2. Marca a posição atual como "visitada".
3. Tenta se mover para posições vizinhas válidas ainda não exploradas.
4. Se encontrar a saída, sucesso.
5. Se não houver caminhos possíveis, volta para a posição anterior.

---

## Funcionamento do Algoritmo

### Etapas principais:

- Verifica se a posição atual é válida:
  - Está dentro do labirinto
  - Não é parede
  - Ainda não foi visitada

- Marcar a posição como parte do caminho atual

- Testar movimentos nas quatro direções:
  - Baixo
  - Direita
  - Cima
  - Esquerda

- Caso nenhuma direção leve a saída:
  - Remove a posição do caminho (backtracking)
  - Retornar para tentar outro caminho

---

## Trecho do Código

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

---

## Complexidade

- **Tempo:**  
  No pior dos casos, o algoritmo explora todas as posições:  
  **O(4^(n*m))** (exponencial)

- **Espaço:**  
  Armazena matriz de visitados e caminho:  
  **O(n * m)**

---

## Aplicação no Projeto

No atividade:

- O jogador pode navegar manualmente pelo labirinto  
- O algoritmo de backtracking pode ser acionado para:
  - Resolver automaticamente o labirinto  
  - Exibir o caminho encontrado (`*`)
- O caminho do jogador é comparado com o melhor caminho encontrado pelo backtracing

---

## Comparação de Caminhos

O sistema permite analisar:

- Se o jogador encontrou o mesmo caminho do algoritmo  
- Se chegou a saída por um caminho diferente  
- Ou se ainda está a caminho

---

## Conclusão

O backtracking é uma abordagem poderosa para problemas de busca em espaços desconhecidos, como labirintos. Apesar de seu custo computacional elevado em casos extremos, ele garante encontrar uma solução se ela existir.

## Referências

- GeeksforGeeks. Rat in a Maze Backtracking.
- LeetCode. Backtracking Cheat Sheet in Java (From Basics to Advanced)
- StackOverflow. Recursive Backtracking Algorithm in C to solve a Sudoku.
