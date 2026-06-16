public class Sudoku {
    private int N;              // Tamanho do tabuleiro (4, 9 ou 16)
    private int blockSize;      // Tamanho do bloco (2, 3 ou 4)

    private long backtracksMRV = 0;
    private long backtracksColoracao = 0;
    private long recursividadeMRV = 0;
    private long recursividadeColoracao = 0;

    public Sudoku(int tamanho) {
        this.N = tamanho;
        this.blockSize = (int) Math.sqrt(tamanho);
    }

    private boolean ehSeguro(int[][] tabuleiro, int linha, int coluna, int num) {
        for (int x = 0; x < N; x++) {
            if (tabuleiro[linha][x] == num) return false;
        }
        for (int x = 0; x < N; x++) {
            if (tabuleiro[x][coluna] == num) return false;
        }
        int inicioLinha = (linha / blockSize) * blockSize;
        int inicioColuna = (coluna / blockSize) * blockSize;
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                if (tabuleiro[i + inicioLinha][j + inicioColuna] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    private int[] encontrarMelhorCelula(int[][] tabuleiro) {
        int minOpcoes = Integer.MAX_VALUE;
        int melhorLinha = -1, melhorColuna = -1;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (tabuleiro[i][j] == 0) {
                    int opcoes = contarOpcoes(tabuleiro, i, j);
                    if (opcoes < minOpcoes && opcoes > 0) {
                        minOpcoes = opcoes;
                        melhorLinha = i;
                        melhorColuna = j;
                    }
                }
            }
        }
        return new int[]{melhorLinha, melhorColuna};
    }

    private int contarOpcoes(int[][] tabuleiro, int linha, int coluna) {
        int count = 0;
        for (int num = 1; num <= N; num++) {
            if (ehSeguro(tabuleiro, linha, coluna, num)) count++;
        }
        return count;
    }

    private boolean resolverComHeuristica(int[][] tabuleiro) {
        recursividadeMRV++;
        int[] celula = encontrarMelhorCelula(tabuleiro);//divisao e conquista,comeca escolhendo um subproblema
        int linha = celula[0];
        int coluna = celula[1];
        if (linha == -1) return true;//problema resolvido no caso base
        for (int num = 1; num <= N; num++) {//teste da divisao com cada possibilidade
            if (ehSeguro(tabuleiro, linha, coluna, num)) {
                tabuleiro[linha][coluna] = num;//realmente ocorre a divisao
                if (resolverComHeuristica(tabuleiro)) {//chamada recursiva para resolver o subproblema com 1 celula a menos
                    return true;//caso a conquista tenha sucesso
                }    
                tabuleiro[linha][coluna] = 0;
                backtracksMRV++;//caso a conquista falhe retrocede para fazer novamente com outra celula
            }
        }
        return false;//caso o caminho nao tenha solucao
    }

    private boolean resolverColoracaoGrafos(int[][] tabuleiro) {
        recursividadeColoracao++;
        int[] celula = encontrarMelhorCelula(tabuleiro);
        int linha = celula[0];
        int coluna = celula[1];

        if (linha == -1) return true;

        for (int cor = 1; cor <= N; cor++) {
            if (ehSeguro(tabuleiro, linha, coluna, cor)) {
                tabuleiro[linha][coluna] = cor;
                if (resolverColoracaoGrafos(tabuleiro)) return true;
                tabuleiro[linha][coluna] = 0;
                backtracksColoracao++;
            }
        }
        return false;
    }

    private void imprimirTabuleiro(int[][] tabuleiro) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.printf("%2d ", tabuleiro[i][j]);
            }
            System.out.println();
        }
    }

    private void imprimirEstatisticas() {
        System.out.println("=== Estatísticas ===");
        System.out.println("Heurística MRV foi utilizado: Backtracks: " + backtracksMRV + " | Recursoes: " + recursividadeMRV);
        System.out.println("Coloraçao + Heuristica foi utilizado: Backtracks: " + backtracksColoracao + " | Recursoes: " + recursividadeColoracao);
    }

    public static void main(String[] args) {
        int[] tamanhos = {4, 9, 16};
        for (int tam : tamanhos) {
            System.out.println("Sudoku " + tam + " x " + tam);
            Sudoku sudoku = new Sudoku(tam);
            int[][] tabuleiro = gerarTabuleiroExemplo(tam);
            System.out.println("Tabuleiro Inicial:");
            sudoku.imprimirTabuleiro(tabuleiro);
            int[][] copia1 = copiarTabuleiro(tabuleiro);
            long inicio = System.currentTimeMillis();
            boolean resolvidoMRV = sudoku.resolverComHeuristica(copia1);
            long tempoMRV = System.currentTimeMillis() - inicio;
            System.out.println("\n Resolvido com Heuristica MRV em " + tempoMRV + " ms");
            sudoku.imprimirTabuleiro(copia1);
            sudoku = new Sudoku(tam);
            int[][] copia2 = copiarTabuleiro(tabuleiro);
            inicio = System.currentTimeMillis();
            boolean resolvidoGrafos = sudoku.resolverColoracaoGrafos(copia2);
            long tempoGrafos = System.currentTimeMillis() - inicio;
            System.out.println("\n Resolvido com Coloraçao de Grafos + Heuristica em " + tempoGrafos + " ms");
            sudoku.imprimirTabuleiro(copia2);
            sudoku.imprimirEstatisticas();
        }
    }

    private static int[][] gerarTabuleiroExemplo(int tam) {
        if (tam == 4) {
            return new int[][]{
                {0, 0, 0, 2},
                {0, 3, 0, 0},
                {1, 0, 4, 0},
                {0, 0, 0, 3}
            };
        } else if (tam == 9) {
            return new int[][]{
                {5, 3, 0, 0, 7, 0, 0, 0, 0},
                {6, 0, 0, 1, 9, 5, 0, 0, 0},
                {0, 9, 8, 0, 0, 0, 0, 6, 0},
                {8, 0, 0, 0, 6, 0, 0, 0, 3},
                {4, 0, 0, 8, 0, 3, 0, 0, 1},
                {7, 0, 0, 0, 2, 0, 0, 0, 6},
                {0, 6, 0, 0, 0, 0, 2, 8, 0},
                {0, 0, 0, 4, 1, 9, 0, 0, 5},
                {0, 0, 0, 0, 8, 0, 0, 7, 9}
            };
        } else {
            int[][] tab = new int[16][16];
            tab[0][0] = 1; tab[0][1] = 2; tab[0][5] = 6; tab[0][8] = 9;
            tab[1][3] = 4; tab[1][6] = 7; tab[1][12] = 13;
            tab[2][4] = 5; tab[2][7] = 8; tab[2][11] = 12;
            tab[4][4] = 10; tab[5][5] = 11; tab[7][7] = 16;
            tab[8][8] = 3; tab[9][9] = 14;
            tab[15][15] = 15; tab[15][10] = 4;
            return tab;
        }
    }

    private static int[][] copiarTabuleiro(int[][] original) {
        int[][] copia = new int[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, copia[i], 0, original[i].length);
        }
        return copia;
    }
}