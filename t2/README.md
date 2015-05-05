#TTToTCP - Tic Tac Toe over TCP
Por enquanto apenas o cliente está funcionando. O protocolo pode ser testado conectando um cliente à um telnet ou netcat e manualmente interagindo com ele.

##Compilação:
`ant jar` gera o .jar dentro de `build/jar/`

##Execução:
1. Para iniciar o servidor: `java -jar build/jar/T2.jar --server 1234`
2. Para iniciar os clientes: `java -jar build/jar/T2.jar `

##Protocolo:
Como o jogo só é de fato executado no servidor, os clientes atuam como terminais burros enviando uma jogada de cada vez para serem avaliadas, e recebendo de volta ou um indicador de erro ou um eco da jogada como confirmação.

## Cliente --> Servidor

###Hello:
**Sintaxe:** `HELLO`

Usado para iniciar a conexão. Não possui nnenhum argumento.

**Respostas esperadas:**
* `HELLO (X|O)`: Quando conectado corretamente.
* `DISCONNECT <err_id> [msg ...]`: Quando não pode acitar a conexão.

###Bye:
**Sintaxe:** `BYE [msg ...]`.

Usado para fechar a conexão com o servidor.
`msg` Mensagem opcional indicando o motivo da saída.
**Respostas esperadas:** *Nenhuma*.

##Move:
**Sintaxe:** `MOVE (0|1|2|3|4|5|6|7|8)`.

Usado para declarar uma jogada. Possuí apenas um argumento obrigatório, que indica qual casa o jogador deseja preencher. Os números das casas correspondentes estão na tabela abaixo:

 0 | 1 | 2    
:-:|:-:|:-:
 3 | 4 | 5
 6 | 7 | 8
 
 **Respostas esperadas:**
 * `MOVE (X|O) (0|1|2|3|4|5|6|7|8)` Quando a jogada é bem sucedida, o servidor deve ecoa-lá de volta a ambos clientes, indicando no primeiro argumento qual jogador está executando a jogada, e no segundo qual a posição afetada. 
 * `INVALID <errno> [msg ...]` Caso a jogada não seja aceita.

##Servidor --> Cliente

###Disconnect:
**Sintaxe:** `DISCONNECT [msg ...]`

Usado pelo servidor para indicar que a conexão será fechada.

`msg` Mensagem opcional indicando o motivo da desconexão.

###Game Over:
**Sintaxe:** `GAMEOVER (WIN|LOSE|DRAW)`

Comando enviado pelo servidor para indicar que o jogo acabou. Possuí apenas um argumento indicando se o jogador que recebeu a mensagem ganhou (WIN), perdeu (LOSE) ou se foi um empate (DRAW).

###Invalid:
**Sintaxe:** `INVALID <err_id> [msg ...]`

Resposta padrão do servidor em caso de comandos malformados ou recebidos em contextos errados.

`errno`: Código do erro.
`msg`: Mensagem opcional explicando o problema.

##Notação da sintaxe:
* `COMANDO` Quaisqueres caracteres em caixa alta e/ou dígitos devem ser interpretados como como comandos literais.
* `<arg>` Argumento obrigatório. Não deve conter espaço.
* `[arg]` Argumento opcional. Se usado não deve conter espaços.
* `[arg ...]` Argumento opcional. Se permitido será sempre o último argumento, podendo conter espaços. Só é delimitado pela quebra de linha no final do comando.
* `(opt1|opt2|...|optn)` Argumento obrigatório e tem que ser (apenas) uma das opções dadas.

**Nota:** *Todos os comandos devem ser terminados com uma quebra de linha ('\n')*

## Exemplos:
Linhas começando com `>` são enviadas do cliente para o servidor, linhas começando com `<` são enviadas do servidor para o cliente, e qualquer coisa precedia por `//` é um comentário.
```
> HELLO      // Tentando entrar no jogo
< HELLO X    // Conectou com sucesso e virou o jogador 'X'
> MOVE 4     // Tenta fazer uma jogada no centro do tabuleiro
< INVALID 2  // Não eh minha vez
< MOVE O 8   // O outro jogador fez uma jogada no canto inferior direito
> MOVE 4     // Tenta a mesma jogada novamente
< MOVE X 4   // Ecoou de volta. A jogada foi bem sucedida.
> BYE User closed the window. // Sai e avisa o motivo.
```

