# 🚀  JetPack Compose Fundamentals
<img width="1251" height="1353" alt="image" src="https://github.com/user-attachments/assets/986f4efa-7a62-4717-b82b-29cfea926df0" />


[Jetpack Doc](https://developer.android.com/develop/ui/compose/documentation?hl=pt-br)

[Jetpack Video](https://youtu.be/6_wK_Ud8--0?si=UTcm1MSDCpWt-8Eh)

# Guia Técnico: Jetpack Compose para Android
> **Nível:** Zero (sem experiência prévia)  
> **Linguagem:** Kotlin  
> **Fonte de referência:** [developer.android.com/compose](https://developer.android.com/compose)  
> **Versão BOM:** `2025.05.01`

---

## Parte 1 — Introdução e Contextualização

---

### 1.1 O que é Jetpack Compose?

Jetpack Compose é o **toolkit oficial e moderno do Android para construção de interfaces de usuário (UI)**.

Ele foi anunciado pelo Google no Google I/O 2019 e se tornou estável em 2021. Hoje é a abordagem **recomendada pelo Google** para todo desenvolvimento Android nativo.

---

### 1.2 Problema que o Compose resolve

Antes do Compose, a UI Android era construída com o sistema de **Views + XML**. Esse modelo tem limitações estruturais importantes.

#### O que era o XML de layout no Android?

XML tem dois usos distintos na computação. É importante não confundir:

| Uso do XML | Contexto | Exemplo |
|---|---|---|
| **Troca de dados entre sistemas** | APIs, configurações, serialização | `<usuario><nome>João</nome></usuario>` |
| **Definição de layout de UI Android** | Sistema de Views do Android | `<Button android:text="Clique" />` |

O Android **reutilizou a sintaxe XML** para um propósito completamente diferente: descrever a estrutura visual de uma tela. O arquivo `.xml` de layout é **compilado pelo Android Studio** e transformado em objetos Kotlin (`Button`, `TextView`, etc.) em tempo de build. Não há tráfego de rede, não há troca de dados entre sistemas.

```xml
<!-- res/layout/activity_main.xml -->
<!-- Este XML não trafega por rede.
     É lido pelo compilador e convertido em objetos de UI dentro do app. -->
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <Button
        android:id="@+id/meuBotao"
        android:text="Clique aqui"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

</LinearLayout>
```

Para criar um botão no sistema antigo, você definia a **aparência** no XML e o **comportamento** no Kotlin — dois arquivos obrigatoriamente acoplados:

```kotlin
// Kotlin — você buscava o botão pelo ID definido no XML
val botao = findViewById<Button>(R.id.meuBotao)
botao.setOnClickListener {
    // ação ao clicar
}
```

Esse acoplamento entre dois arquivos separados é um dos problemas que o Compose elimina.

**Comparativo de limitações:**

| Problema (View System / XML) | Como o Compose resolve |
|---|---|
| UI em XML separado do código Kotlin | UI e lógica coexistem no mesmo arquivo Kotlin |
| Atualização de UI exige manipulação manual | UI atualiza automaticamente quando os dados mudam |
| Hierarquia de Views profunda e custosa | Composição de funções leves, sem herança de View |
| Dificuldade para criar componentes reutilizáveis | Qualquer função Kotlin pode ser um componente de UI |

---

### 1.3 O paradigma declarativo

Este é o conceito mais importante para entender o Compose.

Existem dois paradigmas de construção de UI:

---

#### Paradigma Imperativo (View System — o modelo antigo)

"Imperativo" significa dar **ordens sequenciais**. Você controla cada passo da mudança visual manualmente.

Exemplo: tela de login. Quando o usuário digita a senha errada, você quer mostrar uma mensagem de erro e desabilitar o botão.

```kotlin
// Você é responsável por CADA mudança visual manualmente.
// O app não tem memória do estado atual da tela —
// você precisa dizer explicitamente o que mudar.

fun mostrarErro() {
    // Passo 1: encontrar cada elemento na tela pelo ID do XML
    val mensagemErro = findViewById<TextView>(R.id.textoErro)
    val campoSenha   = findViewById<EditText>(R.id.campoSenha)
    val botaoLogin   = findViewById<Button>(R.id.botaoLogin)

    // Passo 2: alterar cada propriedade manualmente
    mensagemErro.visibility = View.VISIBLE
    mensagemErro.text       = "Senha incorreta"
    campoSenha.setBackgroundColor(Color.RED)
    botaoLogin.isEnabled    = false
}

fun esconderErro() {
    // Você também precisa desfazer tudo manualmente,
    // em uma função separada
    val mensagemErro = findViewById<TextView>(R.id.textoErro)
    val campoSenha   = findViewById<EditText>(R.id.campoSenha)
    val botaoLogin   = findViewById<Button>(R.id.botaoLogin)

    mensagemErro.visibility = View.GONE
    campoSenha.setBackgroundColor(Color.WHITE)
    botaoLogin.isEnabled    = true
}
```

**O problema central:** se você esquecer de chamar `esconderErro()` em algum caminho do código, a mensagem de erro fica visível para sempre. A UI e os dados ficam **dessincronizados** — e o compilador não detecta esse erro.

---

#### Paradigma Declarativo (Compose — o modelo moderno)

"Declarativo" significa **descrever o resultado desejado** para cada estado possível, não os passos para chegar lá.

```kotlin
// Existe uma variável que representa o estado atual.
// A UI é apenas um reflexo visual dessa variável.

@Composable
fun TelaLogin(senhaIncorreta: Boolean) {

    // Esta função descreve a UI para QUALQUER valor de senhaIncorreta.
    // Você não chama "mostrar" ou "esconder" separadamente —
    // a função já cobre todos os casos de uma só vez.

    Column {
        // O campo muda de cor automaticamente conforme o estado
        TextField(
            value = "",
            onValueChange = {},
            colors = if (senhaIncorreta)
                         TextFieldDefaults.colors(focusedContainerColor = Color.Red)
                     else
                         TextFieldDefaults.colors()
        )

        // A mensagem só existe na tela quando senhaIncorreta == true.
        // Quando é false, ela simplesmente não é renderizada —
        // sem chamar "esconder", sem setar visibility
        if (senhaIncorreta) {
            Text(text = "Senha incorreta")
        }

        Button(
            onClick = {},
            enabled = !senhaIncorreta  // habilitado ou não, sem código extra
        ) {
            Text("Entrar")
        }
    }
}
```

**A diferença fundamental:**

| | Imperativo | Declarativo |
|---|---|---|
| Quando a tela muda | Você chama funções manualmente | O Compose reexecuta a função automaticamente |
| Quem controla a UI | O desenvolvedor, passo a passo | O estado dos dados |
| Risco de bug | UI e dados ficam dessincronizados | Impossível: UI é sempre derivada do estado |
| Código para 2 estados | 2 funções separadas | 1 função que cobre todos os estados |

**Regra mental fundamental:**
```
UI = f(estado)
```
A interface é sempre uma **função direta do estado atual dos dados**. Não existe sincronização manual.

---

### 1.4 O que é uma função Composable?

Uma função Composable é o **bloco básico de construção** de toda UI no Compose.

#### O que significa "emitir UI"

Em Kotlin, funções normalmente **retornam um valor** que o chamador usa:

```kotlin
// Função comum: produz um dado e entrega ao chamador
fun somar(a: Int, b: Int): Int {
    return a + b
}

val resultado = somar(2, 3)  // resultado = 5 — você usa o retorno
```

Uma função `@Composable` **não funciona assim**. Ela não produz um dado. Em vez disso, ela **registra elementos visuais na árvore de UI** do Compose. O tipo de retorno `Unit` significa exatamente isso: não há nada para devolver ao chamador.

```kotlin
// Função Composable: não retorna nada (Unit = sem retorno útil)
// Ela "emite" — ou seja, registra — um elemento visual na tela
@Composable
fun MeuTexto(conteudo: String) {
    Text(text = conteudo)
    // "Text()" não retorna um objeto Text para você usar
    // Ela registra um texto na árvore de UI do Compose
    // O efeito é visual, não um dado
}
```

```kotlin
// Você NUNCA captura o retorno de um Composable — não existe retorno
val elemento = MeuTexto("Olá")  // ERRO DE COMPILAÇÃO

// Você apenas chama — o efeito acontece na tela
MeuTexto("Olá")  // correto
```

**Analogia:**
```
Função comum     → fábrica: você pede um produto, ela te entrega
Função Composable → pintor: você pede para pintar algo na tela,
                    ele pinta — você não recebe nada de volta,
                    o resultado aparece diretamente na tela
```

---

#### Anatomia de uma função Composable

```kotlin
// A anotação @Composable transforma uma função Kotlin comum
// em um componente de UI gerenciado pelo Compose
@Composable
fun MeuBotao(texto: String) {
    // Button é um composable nativo do Compose (Material Design)
    Button(onClick = { /* ação */ }) {
        // Text é outro composable — composables podem conter outros composables
        Text(text = texto)
    }
}
```

**Regras obrigatórias:**

| Regra | Motivo |
|---|---|
| Sempre anotadas com `@Composable` | O compilador precisa identificar e processar essas funções de forma especial |
| Nome começa com letra maiúscula (PascalCase) | Convenção oficial do Kotlin/Compose para distinguir de funções normais |
| Não retornam valores (`Unit`) | Elas registram UI na tela, não produzem dados para o chamador |
| Podem chamar outras funções `@Composable` | É assim que a árvore de UI é construída por composição |
| Não podem ser chamadas de funções não-Composable | O contexto de composição só existe dentro da árvore Compose |

---

### 1.5 Como o Compose se integra ao Android

O Compose se integra dentro de uma `Activity` normal via `setContent {}`:

```kotlin
// MainActivity.kt
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContent é o ponto de entrada do Compose na Activity.
        // Tudo dentro deste bloco é gerenciado pelo Compose.
        setContent {

            // MaterialTheme aplica o tema visual (cores, tipografia, formas).
            // É prática padrão envolver toda a UI no tema.
            MaterialTheme {

                // Aqui você chama seus composables
                TelaLogin(senhaIncorreta = false)
            }
        }
    }
}
```

**Fluxo de execução:**
```
Activity.onCreate()
    └── setContent { }
            └── MaterialTheme { }
                    └── TelaLogin()
                            └── Column()
                                    ├── TextField()
                                    └── Button()
                                            └── Text()
```

---

### 1.6 Configuração mínima do projeto

```kotlin
// app/build.gradle.kts

android {
    // Compose requer compileSdk 34 ou superior
    compileSdk = 35

    buildFeatures {
        // Habilita o processamento do compilador Compose
        compose = true
    }
}

dependencies {
    // BOM (Bill of Materials): arquivo centralizado de versões.
    // Ao declarar apenas a versão do BOM, todas as dependências
    // Compose ficam automaticamente em versões compatíveis entre si.
    // Isso evita conflitos de versão entre bibliotecas.
    val composeBom = platform("androidx.compose:compose-bom:2025.05.01")
    implementation(composeBom)

    // UI core do Compose
    implementation("androidx.compose.ui:ui")

    // Componentes Material Design 3 (botões, cards, temas, etc.)
    implementation("androidx.compose.material3:material3")

    // Ferramentas de preview no Android Studio
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Integração do Compose com Activity
    implementation("androidx.activity:activity-compose:1.9.0")
}
```

---

### 1.7 Resumo da Parte 1

| Conceito | Definição resumida |
|---|---|
| Jetpack Compose | Toolkit oficial Android para UI declarativa em Kotlin |
| XML de layout | Sintaxe XML usada para descrever telas no sistema antigo — não é troca de dados entre sistemas |
| Paradigma imperativo | Você comanda cada alteração visual manualmente, passo a passo |
| Paradigma declarativo | Você descreve a UI para cada estado; o Compose decide quando e como atualizar |
| `@Composable` | Anotação que faz a função registrar elementos visuais na tela em vez de retornar dados |
| `Unit` | Tipo de retorno que indica "sem retorno útil" — a função emite UI, não produz dados |
| `setContent {}` | Ponto de entrada do Compose dentro de uma Activity |
| BOM | Gerenciador de versões das dependências Compose |

---

### Próxima Parte

**Parte 2 — State, Recomposition e Ciclo de Vida**

# Guia Técnico: Jetpack Compose para Android
> **Parte 2 — State, Recomposition e Fluxo de Dados**

---

## 2.1 O que é State (Estado)?

No Compose, **estado** é qualquer valor que pode mudar ao longo do tempo e que, quando muda, deve causar uma atualização visual na tela.

Exemplos de estado:
- O texto digitado em um campo de busca
- Se uma caixa de seleção está marcada ou não
- O número atual em um contador
- Se um menu está aberto ou fechado

O Compose só detecta mudanças de estado e atualiza a UI automaticamente **se o estado for armazenado de uma forma que ele consiga observar**. Uma variável Kotlin comum não serve para isso:

```kotlin
// ERRADO — variável Kotlin comum
// O Compose não consegue observar mudanças nesta variável.
// A tela nunca vai atualizar quando o valor mudar.
@Composable
fun ContadorErrado() {
    var contador = 0  // variável comum — invisível para o Compose

    Button(onClick = { contador++ }) {  // incrementa, mas a tela não reage
        Text("Valor: $contador")
    }
}
```

Para que o Compose detecte a mudança e atualize a UI, o estado precisa ser declarado com `mutableStateOf`:

```kotlin
// CORRETO — estado observável
// O Compose monitora este valor. Quando ele muda, a UI é atualizada.
@Composable
fun ContadorCorreto() {
    val contador = mutableStateOf(0)  // estado observável pelo Compose

    Button(onClick = { contador.value++ }) {
        Text("Valor: ${contador.value}")  // lê o valor com .value
    }
}
```

### O tipo `MutableState<T>`

`mutableStateOf(valor)` retorna um objeto do tipo `MutableState<T>`. Esse objeto tem dois componentes:

| Componente | Função |
|---|---|
| `.value` (leitura) | Retorna o valor atual do estado |
| `.value = novoValor` (escrita) | Atualiza o valor e notifica o Compose para redesenhar |

```kotlin
val nome: MutableState<String> = mutableStateOf("Android")

// Leitura
println(nome.value)       // "Android"

// Escrita — isto notifica o Compose automaticamente
nome.value = "Compose"    // a UI que exibe 'nome' será atualizada
```

---

## 2.2 `remember` — preservando o estado entre recomposições

Há um problema no exemplo anterior. Toda vez que o Compose roda novamente a função `ContadorCorreto` (o que acontece sempre que qualquer estado muda), a linha `val contador = mutableStateOf(0)` seria executada novamente, **resetando o contador para zero**.

```kotlin
// O problema sem remember:
@Composable
fun ContadorSemRemember() {
    // A cada recomposição, esta linha executa de novo.
    // O valor é sempre reiniciado para 0.
    val contador = mutableStateOf(0)

    Button(onClick = { contador.value++ }) {
        Text("Valor: ${contador.value}")  // sempre mostra 0
    }
}
```

A solução é o `remember`. Ele instrui o Compose a **executar o bloco apenas na primeira composição** e reutilizar o valor armazenado nas recomposições seguintes:

```kotlin
@Composable
fun ContadorComRemember() {
    // remember { } executa o bloco apenas uma vez (primeira composição).
    // Nas recomposições seguintes, retorna o valor já armazenado.
    val contador = remember { mutableStateOf(0) }

    Button(onClick = { contador.value++ }) {
        Text("Valor: ${contador.value}")  // agora incrementa corretamente
    }
}
```

**Fluxo de execução com `remember`:**
```
1ª execução da função:
    remember { mutableStateOf(0) }
        → bloco executado → valor 0 armazenado na Composition

Clique no botão → contador.value = 1 → recomposição disparada

2ª execução da função (recomposição):
    remember { mutableStateOf(0) }
        → bloco NÃO é executado → valor 1 é retornado do armazenamento
```

### Sintaxe alternativa com delegação (`by`)

O Kotlin permite uma sintaxe mais limpa usando o operador `by`. Com ele, você acessa o valor diretamente, sem precisar escrever `.value`:

```kotlin
@Composable
fun ContadorDelegado() {
    // Com 'by': o Kotlin lida com .value automaticamente nos bastidores
    var contador by remember { mutableStateOf(0) }

    Button(onClick = { contador++ }) {   // sem .value
        Text("Valor: $contador")         // sem .value
    }
}
```

**Comparativo das duas sintaxes:**

| Sintaxe | Declaração | Leitura | Escrita |
|---|---|---|---|
| `val x = remember { mutableStateOf(0) }` | `val` | `x.value` | `x.value = novo` |
| `var x by remember { mutableStateOf(0) }` | `var` | `x` | `x = novo` |

As duas são equivalentes. A sintaxe com `by` é mais comum em código Compose moderno por ser mais limpa.

---

## 2.3 O que é Recomposição?

**Recomposição** é o processo pelo qual o Compose **reexecuta as funções Composable** cujos estados foram alterados, atualizando a UI com os novos valores.

É o mecanismo central que implementa a regra `UI = f(estado)` na prática.

```kotlin
@Composable
fun Placar(pontos: Int) {
    // Toda vez que 'pontos' mudar, esta função é reexecutada
    // e um novo Text é emitido com o valor atualizado
    Text(text = "Pontos: $pontos")
}
```

### Como o Compose decide o que recomor

O Compose **não reexecuta a árvore inteira**. Ele é inteligente: reexecuta apenas as funções que leram o estado que mudou.

```kotlin
@Composable
fun Tela() {
    var contador by remember { mutableStateOf(0) }
    var nome     by remember { mutableStateOf("João") }

    Column {
        // Este composable lê 'contador' — será recomposto quando contador mudar
        Text("Contagem: $contador")

        // Este composable lê 'nome' — será recomposto quando nome mudar
        Text("Usuário: $nome")

        // Este botão só altera 'contador'
        Button(onClick = { contador++ }) {
            Text("Incrementar")
        }
    }
}
```

Quando o botão é clicado e `contador` muda:
- `Text("Contagem: $contador")` → **recomposto** (leu `contador`)
- `Text("Usuário: $nome")` → **ignorado** (não leu `contador`)
- O `Button` → **ignorado** (não leu `contador`)

Este comportamento é uma otimização de performance fundamental. Sem ele, qualquer mudança de estado redesenharia a tela inteira.

### Recomposição não é recriação

Um erro conceitual comum: recomposição **não destrói e recria** os elementos. O Compose compara o resultado anterior com o novo e aplica apenas as diferenças — processo chamado de **diffing**.

```
Antes:  Text("Contagem: 4")
Depois: Text("Contagem: 5")

O Compose não cria um novo Text. Ele atualiza apenas o conteúdo
textual do Text já existente na árvore de UI.
```

---

## 2.4 `rememberSaveable` — sobrevivendo a mudanças de configuração

O `remember` armazena o estado **dentro da Composition**. Isso significa que ele existe enquanto o composable que o declarou estiver na tela. Quando o sistema destrói e recria a Activity — o que acontece, por exemplo, ao **rotacionar a tela** — o `remember` perde tudo.

```kotlin
@Composable
fun FormularioFragil() {
    // Se o usuário rotacionar a tela, este texto some.
    // remember não sobrevive à recriação da Activity.
    var texto by remember { mutableStateOf("") }

    TextField(
        value = texto,
        onValueChange = { texto = it },
        label = { Text("Digite algo") }
    )
}
```

Para preservar o estado durante rotação de tela e outros eventos de recriação do sistema, use `rememberSaveable`:

```kotlin
@Composable
fun FormularioRobusto() {
    // rememberSaveable salva o valor em um Bundle do Android.
    // O Bundle sobrevive à recriação da Activity.
    var texto by rememberSaveable { mutableStateOf("") }

    TextField(
        value = texto,
        onValueChange = { texto = it },
        label = { Text("Digite algo") }
    )
}
```

### Tabela comparativa: `remember` vs `rememberSaveable`

| Situação | `remember` | `rememberSaveable` |
|---|---|---|
| Recomposição normal | ✅ Preserva | ✅ Preserva |
| Rotação de tela | ❌ Perde | ✅ Preserva |
| Troca de idioma do sistema | ❌ Perde | ✅ Preserva |
| Usuário fecha o app (swipe) | ❌ Perde | ❌ Perde |
| Tipos suportados diretamente | Qualquer objeto | Apenas tipos do `Bundle` (String, Int, Boolean, etc.) |

> **Quando usar qual:**  
> - `remember` → estado temporário de UI que não precisa sobreviver a rotações (ex: se um dropdown está aberto)  
> - `rememberSaveable` → estado que o usuário preencheu e espera encontrar após rotacionar a tela (ex: texto digitado, item selecionado)

---

## 2.5 Fluxo Unidirecional de Dados (UDF)

**Unidirectional Data Flow (UDF)** é o padrão arquitetural que o Compose adota para organizar como o estado e os eventos circulam entre os composables. <br>
O problema que o UDF resolve precisa ser entendido antes do padrão em si. <br>
O problema: dois composables precisam do mesmo dado <br>
Imagine dois composables separados — um campo de texto e um texto que exibe o que foi digitado:  <br>
```text
┌─────────────────────────────┐
│  [ campo de texto           ]│  ← o usuário digita aqui
│                             │
│  Você digitou: ___          │  ← este texto precisa refletir o que foi digitado
└─────────────────────────────┘
````
A pergunta é: quem é dono do estado textoBusca? <br>
* Se o estado ficar dentro do campo de texto, o texto exibido abaixo não consegue acessá-lo. <br>
* Se o estado ficar dentro do texto exibido, o campo de texto não consegue atualizá-lo. <br> 
* A única solução é que o estado fique no pai, que é quem contém os dois. <br>
Isso é state hoisting: elevar o estado para o ancestral comum mais próximo que precisa dele. <br>
```kotlin
// ─────────────────────────────────────────────
// COMPOSABLE FILHO — CampoBusca
// ─────────────────────────────────────────────
// Este composable NÃO sabe o que é "textoBusca".
// Ele recebe um texto pronto para exibir,
// e uma função para avisar "o usuário digitou algo novo".
// Ele não decide nada — apenas exibe e reporta.

@Composable
fun CampoBusca(
    texto: String,                    // recebe o valor atual para exibir
    onTextChange: (String) -> Unit    // função para avisar o pai quando o valor muda
) {
    TextField(
        value = texto,                // exibe o que o pai mandou
        onValueChange = onTextChange  // quando o usuário digita, avisa o pai
    )
}
````
````kotlin
// ─────────────────────────────────────────────
// COMPOSABLE PAI — TelaBusca
// ─────────────────────────────────────────────
// Este composable É o dono do estado.
// Ele decide o valor inicial, armazena as mudanças
// e distribui o valor para quem precisar.

@Composable
fun TelaBusca() {
    // O estado vive aqui — no pai
    var textoBusca by remember { mutableStateOf("") }

    Column {
        CampoBusca(
            texto = textoBusca,                 // envia o valor atual para o filho
            onTextChange = { textoBusca = it }  // quando o filho avisar, atualiza o estado
        )

        // O pai pode usar o mesmo estado em qualquer outro composable
        Text("Buscando por: $textoBusca")
    }
}
```

---

#### O ciclo completo, passo a passo
```
1. App abre
   → textoBusca = ""
   → CampoBusca recebe texto = ""
   → Text exibe "Buscando por: "

2. Usuário digita "A"
   → TextField detecta a digitação
   → chama onTextChange("A")
   → onTextChange é a função { textoBusca = it } definida no pai
   → textoBusca passa a ser "A"

3. textoBusca mudou → Compose dispara recomposição
   → CampoBusca recebe texto = "A" → exibe "A" no campo
   → Text recebe "A" → exibe "Buscando por: A"

4. Usuário digita "An"
   → repete o ciclo acima com "An"
````

````kotlin
// Se o estado ficasse dentro do CampoBusca:
@Composable
fun CampoBusca() {
    var texto by remember { mutableStateOf("") }

    TextField(
        value = texto,
        onValueChange = { texto = it }
    )
    // 'texto' está preso aqui dentro.
    // O Text("Buscando por: $texto") em TelaBusca
    // não tem como acessar este valor.
}
```

O `Text` fora do `CampoBusca` simplesmente não enxerga a variável `texto`. No Kotlin, variáveis locais de uma função não são acessíveis de fora dela.

---

#### Resumo do padrão em uma frase

> O filho **nunca guarda estado**. Ele recebe o valor atual do pai e devolve eventos ao pai. O pai guarda o estado e decide o que fazer com os eventos.
```
PAI  ──── estado desce como parâmetro ────▶  FILHO
PAI  ◀─── evento sobe como callback ──────  FILHO
````


A regra é:
```
Estado desce   → do pai para os filhos (como parâmetro)
Eventos sobem  → dos filhos para o pai (como callbacks)
```

Sem este padrão, os composables tendem a controlar seu próprio estado internamente, o que os torna difíceis de testar e reutilizar:

```kotlin
// PROBLEMA — composable com estado interno (stateful)
// Ninguém de fora consegue saber qual é o valor atual de 'texto',
// nem controlá-lo. Impossível de testar em isolamento.
@Composable
fun CampoBusca() {
    var texto by remember { mutableStateOf("") }

    TextField(
        value = texto,
        onValueChange = { texto = it }
    )
}
```

Com UDF, o estado é movido para o pai (**state hoisting**) e o filho apenas recebe o valor e reporta eventos:

```kotlin
// SOLUÇÃO — state hoisting: estado elevado para o pai

// Filho: stateless (sem estado próprio)
// Recebe o valor atual e uma função para reportar mudanças.
// Pode ser reutilizado em qualquer contexto.
@Composable
fun CampoBusca(
    texto: String,                   // estado desce como parâmetro
    onTextChange: (String) -> Unit   // evento sobe como callback
) {
    TextField(
        value = texto,
        onValueChange = onTextChange  // repassa o evento para o pai
    )
}

// Pai: stateful (dono do estado)
// Controla o estado e passa para os filhos.
@Composable
fun TelaBusca() {
    var textoBusca by remember { mutableStateOf("") }

    Column {
        CampoBusca(
            texto = textoBusca,                // estado desce
            onTextChange = { textoBusca = it } // evento sobe
        )

        // O pai tem acesso ao estado e pode usá-lo em outros composables
        Text("Buscando por: $textoBusca")
    }
}
```

**Diagrama do fluxo:**
```
TelaBusca (dono do estado)
    │
    │  texto = textoBusca     ← estado desce
    ▼
CampoBusca
    │
    │  onTextChange(novoValor) ← evento sobe
    ▲
TelaBusca atualiza textoBusca → recomposição
```

### Por que este padrão importa?

| Benefício | Explicação |
|---|---|
| **Fonte única de verdade** | O estado existe em um só lugar — não há risco de dois composables terem versões diferentes do mesmo dado |
| **Testabilidade** | `CampoBusca` pode ser testado passando qualquer valor como parâmetro, sem depender de estado interno |
| **Reutilização** | O mesmo composable stateless pode ser usado em diferentes telas com diferentes estados |
| **Previsibilidade** | Dado que você sabe o estado atual, você sabe exatamente como a tela vai parecer |

---

## 2.6 Resumo da Parte 2

| Conceito | Definição resumida |
|---|---|
| Estado (`State`) | Valor que muda ao longo do tempo e deve atualizar a UI quando muda |
| `mutableStateOf` | Cria um estado observável — o Compose detecta mudanças nele |
| `remember` | Armazena um valor na Composition; sobrevive a recomposições, mas não a recriações da Activity |
| `rememberSaveable` | Como `remember`, mas persiste o valor em um Bundle; sobrevive a rotações de tela |
| Recomposição | Reexecução das funções Composable que leram um estado que mudou |
| UDF (Unidirectional Data Flow) | Padrão onde estado desce como parâmetro e eventos sobem como callbacks |
| State Hoisting | Técnica de mover o estado de um composable filho para o pai, tornando o filho stateless |

---

## Próxima Parte

**Parte 3 — Layouts: Column, Row, Box e Modifier**

# Guia Técnico: Jetpack Compose para Android
> **Parte 3 — Layouts: Column, Row, Box e Modifier**

---

## 3.1 O que é um Layout no Compose?

Um layout é um **composable contêiner**: ele não exibe conteúdo visual próprio, mas define **como os composables filhos serão posicionados na tela**.

No sistema antigo (XML), existiam: `LinearLayout`, `RelativeLayout`, `FrameLayout`, `ConstraintLayout`. No Compose, a grande maioria dos casos é coberta por três layouts fundamentais:

| Layout | O que faz |
|---|---|
| `Column` | Empilha filhos **verticalmente** (de cima para baixo) |
| `Row` | Empilha filhos **horizontalmente** (da esquerda para a direita) |
| `Box` | Empilha filhos **uns sobre os outros** (sobreposição) |

---

## 3.2 Column

`Column` posiciona cada filho abaixo do anterior, em sequência vertical.

```kotlin
@Composable
fun ExemploColumn() {
    Column {
        Text("Primeiro")   // fica em cima
        Text("Segundo")    // fica abaixo do primeiro
        Text("Terceiro")   // fica abaixo do segundo
    }
}
```

```
┌──────────────┐
│  Primeiro    │
│  Segundo     │
│  Terceiro    │
└──────────────┘
```

### Parâmetros de alinhamento e arranjo

`Column` possui dois parâmetros para controlar o posicionamento dos filhos:

| Parâmetro | Eixo que controla | O que faz |
|---|---|---|
| `verticalArrangement` | Vertical (↕) | Distribui o espaço entre os filhos no eixo vertical |
| `horizontalAlignment` | Horizontal (↔) | Alinha todos os filhos no eixo horizontal |

```kotlin
@Composable
fun ColumnComAlinhamento() {
    Column(
        // Distribui os filhos com espaço igual entre eles
        verticalArrangement = Arrangement.SpaceBetween,

        // Centraliza todos os filhos horizontalmente
        horizontalAlignment = Alignment.CenterHorizontally,

        // fillMaxSize: ocupa todo o espaço disponível da tela
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Topo")
        Text("Meio")
        Text("Base")
    }
}
```

```
┌──────────────────┐
│     Topo         │  ← espaço entre os filhos distribuído por SpaceBetween
│                  │
│     Meio         │
│                  │
│     Base         │
└──────────────────┘
```

### Valores de `verticalArrangement` para Column

| Valor | Comportamento |
|---|---|
| `Arrangement.Top` | Filhos agrupados no topo (padrão) |
| `Arrangement.Bottom` | Filhos agrupados na base |
| `Arrangement.Center` | Filhos agrupados no centro vertical |
| `Arrangement.SpaceBetween` | Espaço igual **entre** os filhos; sem espaço nas bordas |
| `Arrangement.SpaceAround` | Espaço igual entre os filhos; metade do espaço nas bordas |
| `Arrangement.SpaceEvenly` | Espaço igual entre os filhos **e** nas bordas |
| `Arrangement.spacedBy(8.dp)` | Espaço fixo de `8.dp` entre cada filho |

### Valores de `horizontalAlignment` para Column

| Valor | Comportamento |
|---|---|
| `Alignment.Start` | Alinha à esquerda (padrão) |
| `Alignment.CenterHorizontally` | Centraliza horizontalmente |
| `Alignment.End` | Alinha à direita |

---

## 3.3 Row

`Row` posiciona cada filho à direita do anterior, em sequência horizontal.

```kotlin
@Composable
fun ExemploRow() {
    Row {
        Text("A")   // fica à esquerda
        Text("B")   // fica à direita de A
        Text("C")   // fica à direita de B
    }
}
```

```
┌─────────────────────┐
│  A   B   C          │
└─────────────────────┘
```

### Parâmetros de alinhamento e arranjo

`Row` possui os parâmetros inversos aos do `Column`:

| Parâmetro | Eixo que controla | O que faz |
|---|---|---|
| `horizontalArrangement` | Horizontal (↔) | Distribui o espaço entre os filhos no eixo horizontal |
| `verticalAlignment` | Vertical (↕) | Alinha todos os filhos no eixo vertical |

```kotlin
@Composable
fun RowComAlinhamento() {
    Row(
        // Distribui os filhos com espaço igual entre eles horizontalmente
        horizontalArrangement = Arrangement.SpaceBetween,

        // Centraliza todos os filhos verticalmente
        verticalAlignment = Alignment.CenterVertically,

        // fillMaxWidth: ocupa toda a largura disponível
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Esquerda")
        Text("Centro")
        Text("Direita")
    }
}
```

```
┌──────────────────────────────┐
│  Esquerda  Centro   Direita  │
└──────────────────────────────┘
```

> **Regra para memorizar a diferença entre Column e Row:**
> - `Column` → arranjo no eixo **vertical** (onde os filhos se empilham)
> - `Row` → arranjo no eixo **horizontal** (onde os filhos se empilham)
> O parâmetro de `Arrangement` sempre controla o eixo em que os elementos se acumulam.

---

## 3.4 Box

`Box` empilha os filhos **uns sobre os outros**, como camadas. O último filho declarado fica na camada mais acima visualmente.

```kotlin
@Composable
fun ExemploBox() {
    Box {
        // Primeiro filho: camada de baixo (fundo)
        Image(
            painter = painterResource(R.drawable.fundo),
            contentDescription = "Fundo"
        )

        // Segundo filho: camada de cima (sobrepõe a imagem)
        Text(
            text = "Texto sobre a imagem",
            color = Color.White
        )
    }
}
```

```
┌──────────────────┐
│ ░░░░░░░░░░░░░░░░ │  ← Image (camada de baixo)
│ ░ Texto sobre ░░ │  ← Text (camada de cima, sobrepõe)
│ ░ a imagem   ░░░ │
│ ░░░░░░░░░░░░░░░░ │
└──────────────────┘
```

### Alinhamento de filhos no Box

`Box` usa o parâmetro `contentAlignment` para definir onde os filhos serão posicionados dentro dele:

```kotlin
@Composable
fun BoxComAlinhamento() {
    Box(
        contentAlignment = Alignment.BottomEnd, // canto inferior direito
        modifier = Modifier.size(200.dp)
    ) {
        Image(painter = painterResource(R.drawable.foto), contentDescription = null)

        // Este ícone será posicionado no canto inferior direito da Box
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Editar"
        )
    }
}
```

Os 9 valores de `Alignment` disponíveis para `Box`:

```
TopStart      TopCenter      TopEnd
CenterStart   Center         CenterEnd
BottomStart   BottomCenter   BottomEnd
```

### Alinhamento individual com `align`

Cada filho de um `Box` pode ter seu próprio alinhamento usando o `Modifier.align()`:

```kotlin
@Composable
fun BoxAlinhamentoIndividual() {
    Box(modifier = Modifier.fillMaxSize()) {

        Text(
            "Topo esquerda",
            modifier = Modifier.align(Alignment.TopStart)
        )

        Text(
            "Centro",
            modifier = Modifier.align(Alignment.Center)
        )

        Text(
            "Base direita",
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}
```

---

## 3.5 Modifier

`Modifier` é o sistema do Compose para **decorar, dimensionar, posicionar e configurar o comportamento** de qualquer composable.

Em vez de o composable ter dezenas de parâmetros para cada propriedade visual possível, o Compose centraliza tudo isso no `Modifier`. Ele é passado como parâmetro para praticamente todos os composables nativos.

```kotlin
@Composable
fun ExemploModifier() {
    Text(
        text = "Olá",
        modifier = Modifier
            .padding(16.dp)        // espaçamento interno
            .background(Color.Blue) // cor de fundo
            .fillMaxWidth()        // ocupa toda a largura disponível
    )
}
```

### Como o encadeamento de Modifier funciona

O `Modifier` é uma **lista ordenada de instruções**. Cada chamada adiciona uma instrução ao final da lista. A **ordem importa**: cada instrução afeta o que vem depois dela.

```kotlin
// Exemplo A: padding ANTES do background
// O espaçamento de 16dp fica FORA do fundo azul
Text(
    text = "Exemplo A",
    modifier = Modifier
        .padding(16.dp)          // 1. aplica espaço fora da caixa
        .background(Color.Blue)  // 2. pinta o fundo — o padding já foi subtraído
)

// Exemplo B: background ANTES do padding
// O fundo azul cobre também a área de espaçamento
Text(
    text = "Exemplo B",
    modifier = Modifier
        .background(Color.Blue)  // 1. pinta o fundo completo
        .padding(16.dp)          // 2. o conteúdo é recuado 16dp para dentro do fundo
)
```

```
Exemplo A:                   Exemplo B:
  [ ][ ][ ][ ][ ]              [█][█][█][█][█]
  [ ]  Exemplo A  [ ]          [█]  Exemplo B [█]
  [ ][ ][ ][ ][ ]              [█][█][█][█][█]

  fundo não cobre o padding    fundo cobre o padding
```

### Modificadores mais utilizados

```kotlin
Modifier
    // ── DIMENSIONAMENTO ──────────────────────────────────
    .fillMaxWidth()          // largura = largura do pai (100%)
    .fillMaxHeight()         // altura = altura do pai (100%)
    .fillMaxSize()           // largura e altura = pai (100%)
    .width(120.dp)           // largura fixa de 120dp
    .height(60.dp)           // altura fixa de 60dp
    .size(80.dp)             // largura e altura iguais: 80dp x 80dp
    .wrapContentSize()       // dimensão definida pelo conteúdo interno

    // ── ESPAÇAMENTO ──────────────────────────────────────
    .padding(16.dp)          // padding igual em todos os lados
    .padding(horizontal = 16.dp, vertical = 8.dp)  // padding por eixo
    .padding(top = 8.dp, bottom = 4.dp)            // padding por lado

    // ── APARÊNCIA ────────────────────────────────────────
    .background(Color.Gray)                        // cor de fundo sólida
    .background(Color.Blue, shape = RoundedCornerShape(8.dp)) // fundo com borda arredondada
    .clip(RoundedCornerShape(12.dp))               // recorta o conteúdo em formato arredondado
    .border(1.dp, Color.Black, RoundedCornerShape(8.dp)) // borda

    // ── INTERAÇÃO ────────────────────────────────────────
    .clickable { /* ação ao clicar */ }            // torna o composable clicável
    .alpha(0.5f)                                   // opacidade (0f = invisível, 1f = opaco)

    // ── POSICIONAMENTO ───────────────────────────────────
    .align(Alignment.CenterHorizontally)           // alinhamento dentro do pai
    .weight(1f)                                    // divide espaço proporcional (só em Row/Column)
```

### `weight` — distribuição proporcional de espaço

O `Modifier.weight()` é exclusivo de filhos de `Row` e `Column`. Ele distribui o espaço disponível **proporcionalmente** entre os filhos com peso definido:

```kotlin
@Composable
fun BarraDeNavegacao() {
    Row(modifier = Modifier.fillMaxWidth()) {

        // Este botão ocupa 2/3 do espaço total da Row
        Button(
            onClick = {},
            modifier = Modifier.weight(2f)  // peso 2
        ) {
            Text("Principal")
        }

        // Este botão ocupa 1/3 do espaço total da Row
        Button(
            onClick = {},
            modifier = Modifier.weight(1f)  // peso 1
        ) {
            Text("Secundário")
        }
    }
}
```

```
┌──────────────────────────────────────┐
│  Principal (2/3)  │  Secundário(1/3) │
└──────────────────────────────────────┘
```

---

## 3.6 Composição de layouts

Na prática, telas reais são construídas **combinando** `Column`, `Row`, `Box` e `Modifier`. Não existe limite de aninhamento — o Compose processa layouts aninhados eficientemente.

Exemplo: card de perfil de usuário

```kotlin
@Composable
fun CardPerfil(nome: String, cargo: String) {
    // Row externa: foto à esquerda, textos à direita
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)  // padding interno após o background
    ) {
        // Box: foto com ícone de status sobreposto
        Box(modifier = Modifier.size(56.dp)) {
            Image(
                painter = painterResource(R.drawable.avatar),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)  // recorta a imagem em círculo
            )
            // Indicador de status: ponto verde no canto inferior direito
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(Color.Green, CircleShape)
                    .align(Alignment.BottomEnd)
            )
        }

        // Espaço fixo entre a foto e os textos
        Spacer(modifier = Modifier.width(12.dp))

        // Column interna: nome acima, cargo abaixo
        Column {
            Text(text = nome, style = MaterialTheme.typography.titleMedium)
            Text(text = cargo, style = MaterialTheme.typography.bodySmall)
        }
    }
}
```

**Estrutura da árvore de UI deste componente:**
```
Row
├── Box (foto + status)
│   ├── Image (avatar circular)
│   └── Box (ponto verde — sobreposto via Box)
├── Spacer (espaço fixo)
└── Column (textos)
    ├── Text (nome)
    └── Text (cargo)
```

---

## 3.7 Resumo da Parte 3

| Conceito | Definição resumida |
|---|---|
| `Column` | Empilha filhos verticalmente; `verticalArrangement` controla distribuição; `horizontalAlignment` controla alinhamento |
| `Row` | Empilha filhos horizontalmente; `horizontalArrangement` controla distribuição; `verticalAlignment` controla alinhamento |
| `Box` | Sobrepõe filhos em camadas; o último filho declarado fica acima visualmente |
| `Modifier` | Lista ordenada de instruções de aparência, dimensionamento e comportamento |
| Ordem do `Modifier` | A sequência de encadeamento importa — cada instrução afeta o que vem depois |
| `weight` | Distribui espaço proporcional entre filhos de `Row` ou `Column` |
| `Spacer` | Composable vazio usado para inserir espaço fixo entre elementos |

---

## Próxima Parte

**Parte 4 — Componentes Material Design 3**

# Guia Técnico: Jetpack Compose para Android
> **Parte 4 — Componentes Material Design 3**

---

## 4.1 O que é Material Design 3?

Material Design 3 (M3) é o **sistema de design oficial do Google** para aplicativos Android. Ele define regras visuais — cores, formas, tipografia, espaçamentos — e fornece componentes de UI prontos que já seguem essas regras.

O Compose oferece implementação nativa de todos os componentes M3. Ao usá-los, seu app automaticamente:
- Segue as diretrizes visuais do Android moderno
- Suporta tema claro e escuro
- Suporta cores dinâmicas (extraídas do papel de parede do usuário, no Android 12+)

<function_calls>
<invoke name="bash_tool">
<parameter name="command">cat >> /mnt/user-data/outputs/jetpack_compose_guia_parte4.md << 'ENDOFFILE'

O M3 é composto por três subsistemas de tema:

| Subsistema | O que define |
|---|---|
| **ColorScheme** | Paleta de cores do app (primária, secundária, fundo, erro, etc.) |
| **Typography** | Estilos de texto (tamanho, peso, espaçamento para títulos, corpo, labels) |
| **Shapes** | Formas dos componentes (raio de arredondamento de botões, cards, etc.) |

---

## 4.2 MaterialTheme — o contêiner de tema

`MaterialTheme` é o composable que **fornece o tema para toda a árvore de UI abaixo dele**. Todo componente M3 consulta o tema atual para saber quais cores, fontes e formas usar.

```kotlin
// MainActivity.kt
setContent {
    // MaterialTheme deve envolver toda a UI do app.
    // Sem ele, os componentes M3 usam um tema roxo padrão (baseline).
    MeuAppTheme {
        // toda a UI do app fica aqui dentro
        TelaInicial()
    }
}
```

### Acessando valores do tema em qualquer composable

Dentro de qualquer composable, você pode acessar os valores do tema atual via `MaterialTheme`:

```kotlin
@Composable
fun TituloDestaque(texto: String) {
    Text(
        text = texto,
        // acessa a cor primária definida no tema
        color = MaterialTheme.colorScheme.primary,

        // acessa o estilo de texto "headlineMedium" definido no tema
        style = MaterialTheme.typography.headlineMedium
    )
}
```

---

## 4.3 Scaffold — estrutura base de uma tela

Antes de apresentar os componentes individuais, é necessário entender o `Scaffold`. Ele é o **esqueleto estrutural de uma tela completa** no M3. Ele reserva os espaços corretos para os componentes de navegação e garante que o conteúdo não fique sobreposto à barra de status ou de navegação do sistema.

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaExemplo() {
    Scaffold(
        // Barra superior da tela
        topBar = {
            TopAppBar(
                title = { Text("Meu App") }
            )
        },

        // Botão de ação flutuante (canto inferior direito)
        floatingActionButton = {
            FloatingActionButton(onClick = { /* ação */ }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar")
            }
        },

        // Barra de navegação inferior
        bottomBar = {
            NavigationBar { /* itens de navegação */ }
        }

    ) { paddingValues ->
        // 'paddingValues' contém o espaçamento calculado pelo Scaffold
        // para que o conteúdo não fique atrás da topBar ou bottomBar.
        // É OBRIGATÓRIO aplicá-lo ao conteúdo principal.
        Column(modifier = Modifier.padding(paddingValues)) {
            Text("Conteúdo da tela")
        }
    }
}
```

**Estrutura visual do Scaffold:**
```
┌─────────────────────────┐
│       TopAppBar         │  ← topBar
├─────────────────────────┤
│                         │
│    Conteúdo principal   │  ← content (com paddingValues)
│                    [+]  │  ← floatingActionButton
│                         │
├─────────────────────────┤
│     NavigationBar       │  ← bottomBar
└─────────────────────────┘
```

---

## 4.4 Botões

O M3 define **cinco variantes de botão**, cada uma com nível de ênfase visual diferente. A escolha correta comunica a importância da ação ao usuário.

| Variante | Ênfase | Uso recomendado |
|---|---|---|
| `Button` | Mais alta — fundo sólido na cor primária | Ação principal da tela |
| `FilledTonalButton` | Alta — fundo em tom secundário | Ação secundária importante |
| `ElevatedButton` | Média — fundo elevado com sombra | Ação em superfície colorida |
| `OutlinedButton` | Média — apenas borda | Ação alternativa |
| `TextButton` | Mais baixa — apenas texto | Ação de baixo impacto (cancelar, ver mais) |

```kotlin
@Composable
fun ExemploBotoes() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        // Ação principal — use apenas um por tela
        Button(onClick = { /* salvar */ }) {
            Text("Salvar")
        }

        // Ação secundária com destaque visual
        FilledTonalButton(onClick = { /* exportar */ }) {
            Text("Exportar")
        }

        // Ação em superfície com cor de fundo
        ElevatedButton(onClick = { /* compartilhar */ }) {
            Text("Compartilhar")
        }

        // Ação alternativa
        OutlinedButton(onClick = { /* editar */ }) {
            Text("Editar")
        }

        // Ação de baixo impacto
        TextButton(onClick = { /* cancelar */ }) {
            Text("Cancelar")
        }
    }
}
```

### Botão com ícone

```kotlin
Button(onClick = { /* enviar */ }) {
    // Icon dentro do Button precisa de tamanho específico
    Icon(
        imageVector = Icons.Default.Send,
        contentDescription = null,              // null pois o texto já descreve a ação
        modifier = Modifier.size(ButtonDefaults.IconSize)
    )
    // Spacer padrão entre ícone e texto em botões
    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
    Text("Enviar")
}
```

### Parâmetros comuns a todos os botões

```kotlin
Button(
    onClick = { /* ação */ },
    enabled = true,           // false desabilita o botão visualmente e funcionalmente
    shape = RoundedCornerShape(4.dp),  // sobrescreve o shape do tema
    colors = ButtonDefaults.buttonColors(
        containerColor = Color.Red,    // cor de fundo do botão
        contentColor = Color.White     // cor do texto e ícone dentro do botão
    )
) {
    Text("Ação")
}
```

---

## 4.5 TextField e OutlinedTextField

`TextField` e `OutlinedTextField` são os componentes de entrada de texto do M3. A diferença é apenas visual: o `TextField` tem fundo preenchido; o `OutlinedTextField` tem apenas borda.

```kotlin
@Composable
fun ExemploCampoTexto() {
    // O estado do texto precisa ser gerenciado externamente (state hoisting)
    var email by remember { mutableStateOf("") }

    // TextField — fundo preenchido
    TextField(
        value = email,                           // valor atual exibido no campo
        onValueChange = { email = it },          // atualiza o estado a cada digitação
        label = { Text("E-mail") },              // texto flutuante que sobe ao focar
        placeholder = { Text("usuario@email.com") }, // texto de dica (some ao digitar)
        singleLine = true,                       // impede quebra de linha
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email    // abre teclado com @
        )
    )
}
```

```kotlin
@Composable
fun ExemploOutlinedTextField() {
    var senha by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }

    // OutlinedTextField — apenas borda, sem fundo preenchido
    OutlinedTextField(
        value = senha,
        onValueChange = { senha = it },
        label = { Text("Senha") },
        singleLine = true,

        // visualTransformation oculta o texto com bullets
        visualTransformation = if (senhaVisivel)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),

        // trailingIcon: ícone no lado direito do campo
        trailingIcon = {
            IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                Icon(
                    imageVector = if (senhaVisivel)
                        Icons.Default.Visibility
                    else
                        Icons.Default.VisibilityOff,
                    contentDescription = "Alternar visibilidade da senha"
                )
            }
        },

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        )
    )
}
```

### Exibindo mensagens de erro no TextField

```kotlin
@Composable
fun CampoComValidacao() {
    var texto by remember { mutableStateOf("") }

    // isError ativa o estado visual de erro do componente (borda/texto ficam vermelhos)
    val temErro = texto.length > 20

    OutlinedTextField(
        value = texto,
        onValueChange = { texto = it },
        label = { Text("Nome de usuário") },
        isError = temErro,  // ativa visual de erro quando true

        // supportingText aparece abaixo do campo — use para erro ou dica
        supportingText = {
            if (temErro) {
                Text(
                    "Máximo 20 caracteres",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text("${texto.length}/20")
            }
        }
    )
}
```

---

## 4.6 Card e Surface

`Card` é um contêiner com elevação, fundo e forma arredondada — usado para agrupar conteúdo relacionado. `Surface` é a versão mais básica: apenas aplica cor de fundo, forma e elevação, sem opiniões sobre layout interno.

```kotlin
@Composable
fun ExemploCard() {
    // Card padrão — elevação e fundo automáticos pelo tema
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        // O conteúdo interno é livre — Card é apenas o contêiner
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Título do Card", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Descrição do conteúdo.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
```

### Variantes de Card

```kotlin
// Card clicável — superfície interativa
Card(onClick = { /* navegar para detalhe */ }) {
    Text("Card clicável", modifier = Modifier.padding(16.dp))
}

// ElevatedCard — sombra mais pronunciada
ElevatedCard {
    Text("Card elevado", modifier = Modifier.padding(16.dp))
}

// OutlinedCard — apenas borda, sem elevação
OutlinedCard {
    Text("Card com borda", modifier = Modifier.padding(16.dp))
}
```

---

## 4.7 TopAppBar

`TopAppBar` é a barra superior da tela. Ela contém título, ícone de navegação (voltar/menu) e ações.

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinhaTopBar(onVoltarClick: () -> Unit) {
    TopAppBar(
        // Título da tela
        title = { Text("Detalhes") },

        // Ícone de navegação — geralmente "voltar" ou "abrir menu"
        navigationIcon = {
            IconButton(onClick = onVoltarClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar"
                )
            }
        },

        // Ações no lado direito da barra
        actions = {
            IconButton(onClick = { /* buscar */ }) {
                Icon(Icons.Default.Search, contentDescription = "Buscar")
            }
            IconButton(onClick = { /* mais opções */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Mais opções")
            }
        },

        // Cores da barra — usa o tema por padrão
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}
```

> **Nota:** `TopAppBar` requer a anotação `@OptIn(ExperimentalMaterial3Api::class)` por enquanto. Isso não significa que o componente seja instável — apenas que sua API pode receber pequenos ajustes em versões futuras.

---

## 4.8 NavigationBar

`NavigationBar` é a barra de navegação inferior com ícones para as seções principais do app. Deve conter entre **3 e 5 destinos**.

```kotlin
@Composable
fun BarraNavegacao() {
    var itemSelecionado by remember { mutableStateOf(0) }

    // Lista de destinos de navegação
    val itens = listOf("Início", "Busca", "Perfil")
    val icones = listOf(Icons.Default.Home, Icons.Default.Search, Icons.Default.Person)

    NavigationBar {
        // Para cada destino, cria um NavigationBarItem
        itens.forEachIndexed { index, titulo ->
            NavigationBarItem(
                // selected define qual item está ativo visualmente
                selected = itemSelecionado == index,
                onClick = { itemSelecionado = index },
                icon = {
                    Icon(
                        imageVector = icones[index],
                        contentDescription = titulo
                    )
                },
                label = { Text(titulo) }
            )
        }
    }
}
```

---

## 4.9 AlertDialog

`AlertDialog` exibe uma janela modal para confirmação ou informação. O usuário precisa interagir com ela antes de continuar.

```kotlin
@Composable
fun DialogExclusao(
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        // onDismissRequest é chamado quando o usuário clica fora do dialog
        // ou pressiona o botão voltar — normalmente chama onCancelar
        onDismissRequest = onCancelar,

        // Ícone opcional no topo do dialog
        icon = {
            Icon(Icons.Default.Delete, contentDescription = null)
        },

        title = { Text("Excluir item?") },

        text = {
            Text("Esta ação não pode ser desfeita. O item será removido permanentemente.")
        },

        // Botão de ação principal (direita)
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text("Excluir", color = MaterialTheme.colorScheme.error)
            }
        },

        // Botão de ação secundária (esquerda)
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
        }
        }
    )
}
```

### Controlando a visibilidade do Dialog com estado

O `AlertDialog` não tem visibilidade própria — você controla se ele aparece ou não com um booleano de estado:

```kotlin
@Composable
fun TelaComDialog() {
    var mostrarDialog by remember { mutableStateOf(false) }

    Button(onClick = { mostrarDialog = true }) {
        Text("Excluir")
    }

    // O Dialog só é renderizado quando mostrarDialog == true
    if (mostrarDialog) {
        DialogExclusao(
            onConfirmar = {
                // executa a exclusão
                mostrarDialog = false  // fecha o dialog
            },
            onCancelar = {
                mostrarDialog = false  // fecha o dialog sem fazer nada
            }
        )
    }
}
```

---

## 4.10 Configurando o tema do app

O Android Studio gera automaticamente os arquivos de tema ao criar um projeto Compose. Entender a estrutura é importante para customizar as cores do app.

### Estrutura padrão dos arquivos de tema

```
ui/
└── theme/
    ├── Color.kt     → define todas as cores brutas
    ├── Theme.kt     → monta o ColorScheme e expõe o composable de tema
    └── Type.kt      → define os estilos de tipografia
```

### Color.kt — cores brutas

```kotlin
// Color.kt
// Aqui ficam as cores em hexadecimal.
// Elas não têm semântica — apenas valores de cor.
val Verde80   = Color(0xFFB5CCAD)
val Verde40   = Color(0xFF3D6B34)
val Neutro90  = Color(0xFFE8E0EC)
val Neutro10  = Color(0xFF1C1B1F)
```

### Theme.kt — montagem do tema

```kotlin
// Theme.kt
// Aqui as cores brutas são atribuídas a papéis semânticos.
// "primary" é a cor principal do app; "background" é o fundo, etc.

private val EsquemaClaroDeColores = lightColorScheme(
    primary          = Verde40,       // cor principal — botões, FAB, seleção
    onPrimary        = Color.White,   // cor do conteúdo SOBRE o primary
    primaryContainer = Verde80,       // versão suave do primary — cards, chips
    background       = Neutro90,      // cor de fundo das telas
    onBackground     = Neutro10,      // cor do texto sobre o background
    // ... outros papéis
)

private val EsquemaEscuroDeColores = darkColorScheme(
    primary          = Verde80,
    onPrimary        = Verde40,
    // ... valores invertidos para dark theme
)

@Composable
fun MeuAppTheme(
    // isSystemInDarkTheme() retorna true se o sistema está em modo escuro
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val esquema = if (darkTheme) EsquemaEscuroDeColores else EsquemaClaroDeColores

    MaterialTheme(
        colorScheme = esquema,
        content = content
    )
}
```

### Os papéis de cor do M3

O M3 trabalha com pares de cores: uma cor de **contêiner** e uma cor **"on"** (que representa o conteúdo sobre aquele contêiner):

| Papel | Uso |
|---|---|
| `primary` / `onPrimary` | Componentes de destaque principal (botões, FAB) |
| `primaryContainer` / `onPrimaryContainer` | Contêineres de destaque suave (chips selecionados, badges) |
| `secondary` / `onSecondary` | Componentes de destaque secundário |
| `background` / `onBackground` | Fundo das telas e texto sobre ele |
| `surface` / `onSurface` | Superfícies de componentes (cards, sheets, menus) |
| `error` / `onError` | Estados de erro |

> **Regra:** sempre que uma cor for usada como fundo de algo, o conteúdo (texto, ícone) que vai sobre ela deve usar a cor `on` correspondente. Isso garante contraste acessível automaticamente.

---

## 4.11 Resumo da Parte 4

| Componente | Função |
|---|---|
| `MaterialTheme` | Fornece o tema (cores, tipografia, formas) para toda a árvore abaixo |
| `Scaffold` | Esqueleto estrutural da tela — reserva espaço para TopAppBar, FAB, NavigationBar |
| `Button` / variantes | Ações do usuário em 5 níveis de ênfase visual |
| `TextField` / `OutlinedTextField` | Entrada de texto com suporte a label, erro, ícones e transformações |
| `Card` | Contêiner com elevação e forma para agrupar conteúdo relacionado |
| `TopAppBar` | Barra superior com título, navegação e ações |
| `NavigationBar` | Barra inferior de navegação entre seções principais (3–5 destinos) |
| `AlertDialog` | Janela modal para confirmação ou informação |
| `ColorScheme` | Paleta semântica de cores — pares `cor` / `onCor` |

---

## Próxima Parte

**Parte 5 — Listas e Navegação**

# Guia Técnico: Jetpack Compose para Android
> **Parte 5 — Listas e Navegação**

---

## 5.1 Por que `Column` não serve para listas longas

Na Parte 3, `Column` foi apresentado como um contêiner que empilha filhos verticalmente. É tentador usá-lo para exibir uma lista de itens com um `for` ou `forEach`:

```kotlin
// PROBLEMA — Column com loop
@Composable
fun ListaComColumn(itens: List<String>) {
    Column {
        // Este loop cria TODOS os composables de uma vez,
        // mesmo que apenas 10 caibam na tela.
        // Com 1.000 itens, cria 1.000 composables simultaneamente.
        itens.forEach { item ->
            Text(text = item, modifier = Modifier.padding(16.dp))
        }
    }
}
```

**O problema:** `Column` **não é virtualizado**. Ele renderiza todos os filhos imediatamente, independentemente de estarem visíveis na tela. Com 500 ou 1.000 itens, isso consome memória e processamento desnecessários, causando lentidão e potencialmente travando o app.

**A solução:** `LazyColumn` e `LazyRow`.

---

## 5.2 LazyColumn

`LazyColumn` é uma lista vertical com **renderização virtualizada**: ele só compõe e renderiza os itens que estão **visíveis na tela no momento**. Quando o usuário rola, os itens que saem da tela são descartados e novos são compostos.

É o equivalente ao `RecyclerView` do sistema antigo de Views.

```kotlin
// CORRETO — LazyColumn com lista de dados
@Composable
fun ListaComLazyColumn(itens: List<String>) {
    LazyColumn {
        // 'items' é uma função do escopo LazyListScope.
        // Ela instrui o LazyColumn a criar um composable por elemento da lista,
        // mas apenas para os elementos atualmente visíveis.
        items(itens) { item ->
            Text(text = item, modifier = Modifier.padding(16.dp))
        }
    }
}
```

### As três funções de conteúdo do LazyColumn

Dentro do bloco `LazyColumn { }`, você não chama composables diretamente — você usa funções do escopo `LazyListScope`:

```kotlin
@Composable
fun ExemploLazyCompleto(tarefas: List<Tarefa>) {
    LazyColumn(
        // Espaçamento entre todos os itens da lista
        verticalArrangement = Arrangement.spacedBy(8.dp),
        // Padding ao redor de toda a lista (não de cada item)
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        modifier = Modifier.fillMaxSize()
    ) {

        // ── item ────────────────────────────────────────────────
        // Insere UM composable fixo na lista — não se repete.
        // Usado para cabeçalhos, separadores, banners.
        item {
            Text(
                "Minhas Tarefas",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // ── items ────────────────────────────────────────────────
        // Itera sobre uma coleção e cria um composable por elemento.
        // 'tarefa' é cada elemento da lista 'tarefas'.
        items(tarefas) { tarefa ->
            CardTarefa(tarefa = tarefa)
        }

        // ── itemsIndexed ─────────────────────────────────────────
        // Igual ao 'items', mas também fornece o índice (posição)
        // de cada elemento. Útil quando você precisa saber
        // "este é o item número X da lista".
        itemsIndexed(tarefas) { index, tarefa ->
            Text("${index + 1}. ${tarefa.titulo}")
        }
    }
}
```

### O parâmetro `key` — otimização de recomposição

Por padrão, o `LazyColumn` identifica cada item pela sua **posição** na lista. Isso causa um problema: se você remover ou reordenar itens, o Compose não sabe quais composables reutilizar e recria tudo.

A solução é fornecer um `key` único e estável para cada item:

```kotlin
// Sem key: o Compose usa a posição (0, 1, 2...) como identificador.
// Ao remover o item na posição 0, todos os outros são recriados.
items(tarefas) { tarefa ->
    CardTarefa(tarefa)
}

// Com key: o Compose usa o ID único do dado como identificador.
// Ao remover um item, apenas ele é descartado. Os outros são reutilizados.
items(tarefas, key = { tarefa -> tarefa.id }) { tarefa ->
    CardTarefa(tarefa)
}
```

**Regra:** sempre forneça `key` quando os itens da lista puderem ser adicionados, removidos ou reordenados.

---

## 5.3 LazyRow

`LazyRow` é idêntico ao `LazyColumn`, mas na direção **horizontal**. Usa as mesmas funções de conteúdo (`item`, `items`, `itemsIndexed`) e o mesmo parâmetro `key`.

```kotlin
@Composable
fun CarrosselCategorias(categorias: List<Categoria>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(categorias, key = { it.id }) { categoria ->
            // Chip de categoria rolável horizontalmente
            FilterChip(
                selected = false,
                onClick = { /* selecionar */ },
                label = { Text(categoria.nome) }
            )
        }
    }
}
```

---

## 5.4 Comparativo: Column vs LazyColumn

| Critério | `Column` | `LazyColumn` |
|---|---|---|
| Renderização | Todos os filhos de uma vez | Apenas os visíveis na tela |
| Rolagem | Não tem por padrão (precisa de `verticalScroll`) | Embutida |
| Performance com muitos itens | Ruim — memória proporcional ao total | Boa — memória proporcional ao visível |
| Uso correto | Layouts fixos com poucos elementos | Listas com quantidade variável ou grande de itens |
| Suporte a `item`, `items`, `itemsIndexed` | ❌ | ✅ |

---

## 5.5 Navigation — conceitos fundamentais

O sistema de navegação do Compose é composto por três peças:

| Peça | Responsabilidade |
|---|---|
| `NavController` | Gerencia o back stack e executa as navegações |
| `NavHost` | Contêiner que exibe a tela correspondente à rota atual |
| Rotas (`routes`) | Strings que identificam cada tela — como endereços |

**Analogia:** o `NavController` é o GPS, as rotas são os endereços, e o `NavHost` é a janela que exibe o local atual.

### Dependência necessária

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("androidx.navigation:navigation-compose:2.7.7")
}
```

---

## 5.6 Configuração básica da navegação

```kotlin
// AppNavigation.kt
// É boa prática centralizar toda a navegação em um único arquivo.

// Passo 1: definir as rotas como constantes
// Usar um object evita erros de digitação em strings espalhadas pelo código
object Rotas {
    const val LISTA   = "lista"    // tela de lista de tarefas
    const val DETALHE = "detalhe"  // tela de detalhe de uma tarefa
    const val PERFIL  = "perfil"   // tela de perfil do usuário
}

@Composable
fun AppNavigation() {

    // Passo 2: criar o NavController
    // rememberNavController() cria e memoriza o controlador.
    // Ele sobrevive a recomposições e rotações de tela.
    // Deve ser criado no nível mais alto da hierarquia de UI.
    val navController = rememberNavController()

    // Passo 3: configurar o NavHost
    // NavHost associa rotas a composables.
    // Quando navController.navigate("lista") é chamado,
    // o NavHost exibe o composable registrado para a rota "lista".
    NavHost(
        navController = navController,
        startDestination = Rotas.LISTA  // tela exibida ao abrir o app
    ) {

        // Cada bloco composable() registra uma rota e seu composable correspondente
        composable(route = Rotas.LISTA) {
            // Passamos o navController como callback — não diretamente
            // (ver seção 5.8 sobre boas práticas)
            TelaLista(
                onNavegar = { tarefaId ->
                    // Navega para o detalhe passando o ID na rota
                    navController.navigate("${Rotas.DETALHE}/$tarefaId")
                }
            )
        }

        composable(route = Rotas.PERFIL) {
            TelaPerfil(
                onVoltar = { navController.navigateUp() }
            )
        }
    }
}
```

---

## 5.7 Passagem de argumentos entre telas

Argumentos são passados **embutidos na rota**, como parâmetros de URL. A sintaxe é `"rota/{nomeDoArgumento}"`.

```kotlin
// AppNavigation.kt

object Rotas {
    const val LISTA   = "lista"
    // A rota do detalhe declara que espera um argumento "tarefaId"
    const val DETALHE = "detalhe/{tarefaId}"
}

NavHost(navController = navController, startDestination = Rotas.LISTA) {

    composable(route = Rotas.LISTA) {
        TelaLista(
            onNavegar = { tarefaId ->
                // Substitui o placeholder {tarefaId} pelo valor real
                // Exemplo: navega para "detalhe/42"
                navController.navigate("detalhe/$tarefaId")
            }
        )
    }

    composable(
        route = Rotas.DETALHE,
        // Declara o tipo do argumento esperado
        // O sistema valida o tipo em tempo de execução
        arguments = listOf(
            navArgument("tarefaId") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        // backStackEntry contém os argumentos da rota atual
        // Extrai o valor de "tarefaId" do bundle de argumentos
        val tarefaId = backStackEntry.arguments?.getInt("tarefaId") ?: 0

        TelaDetalhe(
            tarefaId = tarefaId,
            onVoltar = { navController.navigateUp() }
        )
    }
}
```

**Fluxo da passagem de argumentos:**
```
TelaLista clica no item com id = 42
    │
    ▼
navController.navigate("detalhe/42")
    │
    ▼
NavHost reconhece a rota "detalhe/{tarefaId}"
    │
    ▼
Extrai tarefaId = 42 do backStackEntry
    │
    ▼
TelaDetalhe recebe tarefaId = 42
```

### Tipos de argumento suportados

| Tipo Kotlin | NavType correspondente |
|---|---|
| `Int` | `NavType.IntType` |
| `String` | `NavType.StringType` |
| `Boolean` | `NavType.BoolType` |
| `Float` | `NavType.FloatType` |
| `Long` | `NavType.LongType` |

> **Limitação importante:** o sistema de navegação só suporta tipos primitivos como argumentos de rota. Para passar objetos complexos entre telas, a prática correta é passar apenas o **ID** do objeto pela rota e buscar o objeto completo no `ViewModel` da tela de destino. Isso será coberto na Parte 6.

---

## 5.8 Boa prática: não passar o NavController para os composables

A documentação oficial do Android recomenda **não passar o `navController` diretamente** para composables filhos. Em vez disso, passe callbacks de navegação.

```kotlin
// ERRADO — navController passado diretamente para o composable
@Composable
fun TelaLista(navController: NavController) {
    Button(onClick = { navController.navigate("detalhe/1") }) {
        Text("Ver detalhe")
    }
    // Problema: TelaLista agora depende de NavController.
    // Para testá-la em isolamento, você precisa de um NavController real ou mockado.
}

// CORRETO — composable recebe apenas um callback
@Composable
fun TelaLista(onNavegar: (Int) -> Unit) {
    Button(onClick = { onNavegar(1) }) {
        Text("Ver detalhe")
    }
    // Agora TelaLista não sabe nada sobre navegação.
    // Para testá-la, basta passar { } como callback.
    // A lógica de navegação fica concentrada no NavHost.
}
```

**Por que isso importa:**

| Aspecto | NavController direto | Callback |
|---|---|---|
| Testabilidade | Requer instância de NavController | Apenas uma função lambda |
| Acoplamento | Composable acoplado à biblioteca de navegação | Composable independente |
| Reutilização | Só funciona em contexto de navegação | Pode ser usado em qualquer contexto |

---

## 5.9 Controle do back stack

O `NavController` oferece funções para controlar o histórico de navegação:

```kotlin
// Volta uma tela (equivalente ao botão "voltar" do Android)
navController.navigateUp()

// Navega para uma rota removendo telas do back stack até encontrar
// a rota especificada — evita empilhar telas infinitamente
navController.navigate(Rotas.LISTA) {
    // Remove todas as telas do back stack até "lista" (inclusive)
    // antes de navegar. Útil para fluxos de login onde
    // o usuário não deve "voltar" para a tela de autenticação.
    popUpTo(Rotas.LISTA) { inclusive = true }
}

// Navega evitando duplicatas no topo do stack
// Se o usuário clicar duas vezes no mesmo item de navegação,
// não empilha a mesma tela duas vezes
navController.navigate(Rotas.PERFIL) {
    launchSingleTop = true
}
```

---

## 5.10 Estrutura completa de um app com navegação

Reunindo todos os conceitos em uma estrutura de app real:

```
MainActivity.kt
    └── setContent {
            └── MeuAppTheme {
                    └── AppNavigation()   ← NavController e NavHost vivem aqui
                            ├── composable("lista")   → TelaLista(onNavegar)
                            ├── composable("detalhe/{id}") → TelaDetalhe(id, onVoltar)
                            └── composable("perfil")  → TelaPerfil(onVoltar)

TelaLista.kt       → composable stateless, recebe callbacks de navegação
TelaDetalhe.kt     → composable stateless, recebe tarefaId e onVoltar
TelaPerfil.kt      → composable stateless, recebe onVoltar
AppNavigation.kt   → toda a lógica de navegação centralizada aqui
```

---

## 5.11 Resumo da Parte 5

| Conceito | Definição resumida |
|---|---|
| `LazyColumn` / `LazyRow` | Listas virtualizadas — renderizam apenas os itens visíveis na tela |
| `item { }` | Insere um composable fixo e único na lazy list |
| `items(lista) { }` | Itera sobre uma coleção, criando um composable por elemento |
| `itemsIndexed(lista) { index, elem }` | Igual ao `items`, mas fornece o índice de cada elemento |
| `key` | Identificador único por item — evita recriação desnecessária na recomposição |
| `NavController` | Gerencia o back stack e executa navegações |
| `NavHost` | Exibe o composable correspondente à rota atual |
| Rotas | Strings que identificam cada tela — devem ser centralizadas em um `object` |
| Argumentos de rota | Passagem de dados primitivos entre telas via `"rota/{argumento}"` |
| Callbacks de navegação | Padrão correto — composables recebem lambdas, não o `NavController` diretamente |

---

## Próxima Parte

**Parte 6 — ViewModel e Arquitetura**

# Guia Técnico: Jetpack Compose para Android
> **Parte 6 — ViewModel e Arquitetura**

---

## 6.1 O problema que o ViewModel resolve

Na Parte 2, o estado foi gerenciado diretamente nos composables com `remember`. Isso funciona para estados simples de UI, mas apresenta dois problemas estruturais à medida que o app cresce:

**Problema 1 — Perda de estado em mudanças de configuração**

Quando o usuário rotaciona a tela, o Android **destrói e recria a Activity**. O `remember` vive dentro da Composition, que é destruída junto com a Activity. Todo o estado gerenciado por `remember` é perdido.

```
Usuário rotaciona a tela
    → Activity.onDestroy()
    → Composition destruída
    → remember { mutableStateOf(...) } destruído
    → estado perdido
    → Activity.onCreate()
    → Composition recriada do zero
```

`rememberSaveable` resolve rotação de tela, mas tem limitações: só suporta tipos primitivos e não é adequado para lógica de negócio.

**Problema 2 — Lógica de negócio misturada com UI**

Quando os composables crescem, eles acumulam responsabilidades que não deveriam ser deles: chamadas a repositórios, validações, transformações de dados. Isso torna o código difícil de testar e manter.

```kotlin
// Composable fazendo tudo — ERRADO
@Composable
fun TelaLogin() {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var erro  by remember { mutableStateOf("") }

    // Lógica de negócio dentro do composable — não deveria estar aqui
    fun fazerLogin() {
        if (!email.contains("@")) {
            erro = "E-mail inválido"
            return
        }
        // chamada ao repositório, tratamento de erro, navegação...
    }
    // ...
}
```

**A solução para os dois problemas é o `ViewModel`.**

---

## 6.2 O que é ViewModel?

`ViewModel` é uma classe da biblioteca Android Jetpack responsável por:

1. **Sobreviver a mudanças de configuração** — o `ViewModel` não é destruído quando a Activity é destruída por rotação de tela. O Android o mantém vivo e o reconecta à nova instância da Activity.
2. **Armazenar e gerenciar o estado da UI** — o estado que antes ficava no composable com `remember` passa a viver no `ViewModel`.
3. **Conter a lógica de negócio da tela** — validações, chamadas a repositórios, transformações de dados.

```
Ciclo de vida comparado:

Activity/Composable:   [criado] ──────── [destruído] ── [criado] ──── [destruído]
                                         ↑ rotação ↑
ViewModel:             [criado] ──────────────────────────────────── [destruído]
                                                                      ↑ usuário
                                                                        fecha o app
```

O `ViewModel` só é destruído quando o usuário efetivamente sai da tela (navegação de volta) ou fecha o app. Rotações e outras mudanças de configuração não o afetam.

---

## 6.3 StateFlow — estado observável do ViewModel

Na Parte 2, o estado nos composables usava `mutableStateOf`. No `ViewModel`, o padrão recomendado é `StateFlow`.

`StateFlow` é um **fluxo de dados observável** da biblioteca Kotlin Coroutines. Ele:
- Sempre tem um valor atual (nunca está vazio)
- Notifica todos os observadores quando o valor muda
- É seguro para uso com coroutines
- Funciona bem com o ciclo de vida do Android

### O padrão MutableStateFlow / StateFlow

O `ViewModel` usa **dois objetos para o mesmo estado**:

```kotlin
// MutableStateFlow: versão interna e modificável
// O prefixo '_' é convenção para indicar que é privado
private val _uiState = MutableStateFlow(EstadoInicial())

// StateFlow: versão pública e somente-leitura exposta para a UI
// asStateFlow() converte MutableStateFlow em StateFlow imutável
val uiState: StateFlow<EstadoInicial> = _uiState.asStateFlow()
```

**Por que dois objetos?**

O `ViewModel` modifica o estado internamente via `_uiState` (privado). A UI apenas lê via `uiState` (público, imutável). Isso garante que **somente o ViewModel pode alterar o estado** — a UI nunca escreve diretamente nele.

```
UI ──── lê ────▶ uiState (StateFlow — somente leitura)
                     │
                     │ é uma vista de
                     ▼
ViewModel ──── escreve ────▶ _uiState (MutableStateFlow — privado)
```

---

## 6.4 Estrutura completa de um ViewModel

```kotlin
// TarefasViewModel.kt

// data class que representa TODOS os dados que a tela precisa exibir.
// Agrupar tudo em uma data class é o padrão recomendado pelo Google —
// a UI observa um único objeto em vez de vários StateFlows separados.
data class TarefasUiState(
    val tarefas: List<Tarefa> = emptyList(),  // lista de tarefas a exibir
    val carregando: Boolean   = false,         // true enquanto busca dados
    val erro: String?         = null           // mensagem de erro, se houver
)

class TarefasViewModel : ViewModel() {

    // Estado interno modificável — privado, só o ViewModel acessa
    private val _uiState = MutableStateFlow(TarefasUiState())

    // Estado exposto para a UI — público, somente leitura
    val uiState: StateFlow<TarefasUiState> = _uiState.asStateFlow()

    // init é executado uma vez quando o ViewModel é criado
    init {
        carregarTarefas()
    }

    private fun carregarTarefas() {
        // viewModelScope é um CoroutineScope vinculado ao ciclo de vida
        // do ViewModel. Quando o ViewModel é destruído, todas as coroutines
        // lançadas aqui são canceladas automaticamente — sem vazamentos.
        viewModelScope.launch {

            // 1. Indica que o carregamento começou
            _uiState.update { estadoAtual ->
                // .copy() cria uma NOVA cópia do estado com apenas
                // o campo 'carregando' alterado. Os outros campos
                // permanecem os mesmos.
                estadoAtual.copy(carregando = true)
            }

            // 2. Busca os dados (simulado aqui; viria de um repositório)
            try {
                val tarefas = repositorio.buscarTarefas()  // suspend function

                // 3. Atualiza o estado com os dados recebidos
                _uiState.update { it.copy(tarefas = tarefas, carregando = false) }

            } catch (e: Exception) {
                // 4. Em caso de erro, armazena a mensagem no estado
                _uiState.update { it.copy(erro = e.message, carregando = false) }
            }
        }
    }

    // Função chamada pela UI quando o usuário realiza uma ação
    fun adicionarTarefa(titulo: String) {
        viewModelScope.launch {
            repositorio.salvar(Tarefa(titulo = titulo))
            carregarTarefas()  // recarrega a lista após adicionar
        }
    }

    fun removerTarefa(id: Int) {
        viewModelScope.launch {
            repositorio.remover(id)
            // Atualiza o estado filtrando o item removido localmente
            // sem precisar buscar a lista completa novamente
            _uiState.update { estadoAtual ->
                estadoAtual.copy(
                    tarefas = estadoAtual.tarefas.filter { it.id != id }
                )
            }
        }
    }
}
```

---

## 6.5 Conectando o ViewModel ao Compose

### Obtendo a instância do ViewModel

Para obter o `ViewModel` dentro de um composable, use a função `viewModel()`:

```kotlin
// Dependência necessária no build.gradle.kts:
// implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")

@Composable
fun TelaTarefas() {
    // viewModel() cria o ViewModel na primeira composição e
    // retorna a mesma instância em todas as recomposições seguintes.
    // Também sobrevive a rotações de tela.
    val viewModel: TarefasViewModel = viewModel()

    // collectAsStateWithLifecycle coleta o StateFlow de forma
    // segura para o ciclo de vida — para a coleta quando o app
    // vai para segundo plano, economizando recursos.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // A partir daqui, 'uiState' é um State<TarefasUiState>
    // que o Compose observa e dispara recomposição quando muda.
    TelaTarefasConteudo(
        uiState = uiState,
        onAdicionarTarefa = { titulo -> viewModel.adicionarTarefa(titulo) },
        onRemoverTarefa   = { id -> viewModel.removerTarefa(id) }
    )
}
```

### Por que `collectAsStateWithLifecycle` e não `collectAsState`?

```kotlin
// collectAsState — coleta o flow sempre, mesmo com o app em segundo plano.
// O flow continua emitindo e consumindo recursos mesmo quando a tela
// não está visível para o usuário.
val uiState by viewModel.uiState.collectAsState()

// collectAsStateWithLifecycle — para a coleta quando o app vai para
// segundo plano (lifecycle abaixo de STARTED) e retoma quando volta
// ao primeiro plano. É o padrão recomendado pelo Google para Android.
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

| | `collectAsState` | `collectAsStateWithLifecycle` |
|---|---|---|
| Coleta em segundo plano | Sim | Não |
| Consumo de recursos | Maior | Menor |
| Uso recomendado | Código multiplataforma (não-Android) | Apps Android |

---

## 6.6 Separação de responsabilidades na prática

O padrão completo separa o composable em dois: um **stateful** (que tem o ViewModel) e um **stateless** (que apenas recebe dados e callbacks).

```kotlin
// ── Composable STATEFUL (Route) ──────────────────────────────────────
// Responsabilidades: obter o ViewModel, coletar o estado, conectar callbacks.
// NÃO deve conter nenhuma lógica visual — apenas faz a ponte entre
// o ViewModel e o composable stateless.
@Composable
fun TelaTarefas(
    onNavegar: (Int) -> Unit       // callback de navegação vindo do NavHost
) {
    val viewModel: TarefasViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TelaTarefasConteudo(           // repassa tudo para o composable stateless
        uiState          = uiState,
        onAdicionarTarefa = viewModel::adicionarTarefa,
        onRemoverTarefa   = viewModel::removerTarefa,
        onVerDetalhe      = onNavegar
    )
}

// ── Composable STATELESS (Conteúdo) ──────────────────────────────────
// Responsabilidades: apenas renderizar a UI com base nos dados recebidos.
// Não sabe que ViewModel existe. Não sabe que há navegação.
// Pode ser testado em isolamento passando qualquer TarefasUiState.
@Composable
fun TelaTarefasConteudo(
    uiState: TarefasUiState,
    onAdicionarTarefa: (String) -> Unit,
    onRemoverTarefa: (Int) -> Unit,
    onVerDetalhe: (Int) -> Unit
) {
    when {
        // Estado de carregamento
        uiState.carregando -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        // Estado de erro
        uiState.erro != null -> {
            Text(
                text = "Erro: ${uiState.erro}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Estado com dados
        else -> {
            LazyColumn {
                items(uiState.tarefas, key = { it.id }) { tarefa ->
                    CardTarefa(
                        tarefa        = tarefa,
                        onRemover     = { onRemoverTarefa(tarefa.id) },
                        onVerDetalhe  = { onVerDetalhe(tarefa.id) }
                    )
                }
            }
        }
    }
}
```

---

## 6.7 O padrão MVVM completo no Compose

MVVM significa **Model — View — ViewModel**. No Compose, cada camada tem responsabilidades bem definidas:

```
┌─────────────────────────────────────────────────────────┐
│                        VIEW                             │
│  Composables stateless                                  │
│  Responsabilidade: renderizar a UI                      │
│  Sabe sobre: UiState, callbacks                         │
│  NÃO sabe sobre: repositórios, coroutines, banco        │
└───────────────────────┬─────────────────────────────────┘
                        │ coleta StateFlow
                        │ chama funções do ViewModel
                        ▼
┌─────────────────────────────────────────────────────────┐
│                     VIEWMODEL                           │
│  Responsabilidade: estado da UI + lógica de negócio     │
│  Sabe sobre: repositórios, UiState, coroutines          │
│  NÃO sabe sobre: composables, navegação, Context        │
└───────────────────────┬─────────────────────────────────┘
                        │ chama funções do repositório
                        │ recebe dados (Flow/suspend)
                        ▼
┌─────────────────────────────────────────────────────────┐
│                       MODEL                             │
│  Repository + DataSource (API, banco de dados)          │
│  Responsabilidade: fornecer e persistir dados           │
│  NÃO sabe sobre: UI, ViewModel, composables             │
└─────────────────────────────────────────────────────────┘
```

### Fluxo de uma ação do usuário no MVVM

```
1. Usuário clica em "Remover tarefa"
        │
        ▼
2. Composable chama onRemoverTarefa(id)
        │
        ▼
3. ViewModel.removerTarefa(id) é executado
        │
        ▼
4. ViewModel chama repositorio.remover(id)
        │
        ▼
5. Repositório executa a operação no banco/API
        │
        ▼
6. ViewModel atualiza _uiState via .update { }
        │
        ▼
7. StateFlow emite novo valor
        │
        ▼
8. collectAsStateWithLifecycle detecta mudança
        │
        ▼
9. Compose recompõe apenas os composables afetados
        │
        ▼
10. Tela exibe a lista sem o item removido
```

---

## 6.8 Dependências necessárias

```kotlin
// app/build.gradle.kts
dependencies {
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")

    // collectAsStateWithLifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.5")

    // Coroutines (viewModelScope depende disto)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
```

---

## 6.9 Resumo da Parte 6

| Conceito | Definição resumida |
|---|---|
| `ViewModel` | Classe que sobrevive a rotações de tela e armazena o estado e a lógica da UI |
| `MutableStateFlow` | Versão interna e modificável do estado — sempre privada no ViewModel |
| `StateFlow` | Versão pública e somente-leitura do estado — exposta para a UI |
| `_uiState` / `uiState` | Padrão de dois objetos: um privado para escrita, um público para leitura |
| `UiState` (data class) | Agrupamento de todos os dados que a tela precisa em um único objeto |
| `viewModelScope` | CoroutineScope do ViewModel — cancela coroutines automaticamente ao destruir o ViewModel |
| `.update { it.copy(...) }` | Forma correta de modificar o estado — cria nova cópia em vez de mutar o objeto |
| `viewModel()` | Função Compose que obtém (ou cria) a instância do ViewModel |
| `collectAsStateWithLifecycle` | Coleta o StateFlow com ciência do ciclo de vida — para em segundo plano |
| Composable stateful (Route) | Faz a ponte entre ViewModel e UI — não tem lógica visual |
| Composable stateless (Conteúdo) | Apenas renderiza — recebe UiState e callbacks, não sabe que ViewModel existe |

---

## Próxima Parte

**Parte 7 — Persistência de Dados: DataStore e Room**

# Guia Técnico: Jetpack Compose para Android
> **Parte 7 — Persistência de Dados: DataStore e Room**

---

## 7.1 Os dois mecanismos de persistência em disco

Na Parte 6 foi estabelecido que `remember` e `rememberSaveable` vivem na RAM e são perdidos ao fechar o app. Para persistir dados em disco, o Android moderno oferece dois mecanismos principais:

| Mecanismo | O que armazena | Quando usar |
|---|---|---|
| **DataStore Preferences** | Pares chave-valor simples | Preferências do usuário: tema, idioma, flags de onboarding |
| **Room** | Dados estruturados em tabelas SQL | Listas, registros, dados relacionais: tarefas, contatos, histórico |

Eles não são substitutos um do outro — cada um tem seu propósito. Um app real frequentemente usa os dois.

---

## 7.2 DataStore Preferences

`DataStore` é o substituto oficial do `SharedPreferences`. Ele armazena pares **chave-valor** em um arquivo no armazenamento interno do app, de forma **assíncrona e segura para coroutines**.

### Por que não usar SharedPreferences?

| Problema do SharedPreferences | Como o DataStore resolve |
|---|---|
| Opera na thread principal (pode travar a UI) | Opera apenas em coroutines, nunca na thread principal |
| Não é type-safe — retorna `Any?` | Chaves tipadas com `Preferences.Key<T>` |
| Não suporta Flow | Expõe os dados como `Flow` — reativo por padrão |
| Não é seguro para múltiplas threads | Thread-safe internamente |

### Dependência

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("androidx.datastore:datastore-preferences:1.1.1")
}
```

### Criando a instância do DataStore

```kotlin
// DataStoreManager.kt
// O DataStore deve ser uma instância única (singleton) no app.
// A forma mais simples é criá-lo como propriedade de extensão do Context.

// preferencesDataStore cria o arquivo "configuracoes_usuario.preferences_pb"
// no armazenamento interno do app.
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "configuracoes_usuario"
)
```

### Definindo as chaves

```kotlin
// DataStoreManager.kt

// As chaves são objetos tipados — o tipo define o que pode ser armazenado.
// Tipos suportados: Int, String, Boolean, Float, Long, Double, Set<String>
object PreferenciasChaves {
    val TEMA_ESCURO    = booleanPreferencesKey("tema_escuro")
    val NOME_USUARIO   = stringPreferencesKey("nome_usuario")
    val TAMANHO_FONTE  = intPreferencesKey("tamanho_fonte")
}
```

### Lendo dados do DataStore

```kotlin
// DataStoreManager.kt

class DataStoreManager(private val context: Context) {

    // dataStore.data retorna um Flow<Preferences>.
    // Toda vez que qualquer valor é alterado no DataStore,
    // este Flow emite um novo objeto Preferences com todos os valores atuais.
    val temaEscuroFlow: Flow<Boolean> = context.dataStore.data
        .map { preferencias ->
            // Lê o valor da chave. Se ainda não foi salvo,
            // retorna o valor padrão fornecido (false neste caso).
            preferencias[PreferenciasChaves.TEMA_ESCURO] ?: false
        }

    val nomeUsuarioFlow: Flow<String> = context.dataStore.data
        .map { preferencias ->
            preferencias[PreferenciasChaves.NOME_USUARIO] ?: ""
        }
}
```

### Escrevendo dados no DataStore

```kotlin
// DataStoreManager.kt (continuação)

class DataStoreManager(private val context: Context) {

    // edit é uma suspend function — deve ser chamada dentro de uma coroutine.
    // O bloco recebe um MutablePreferences onde você pode definir os valores.
    suspend fun salvarTemaEscuro(ativado: Boolean) {
        context.dataStore.edit { preferencias ->
            preferencias[PreferenciasChaves.TEMA_ESCURO] = ativado
        }
    }

    suspend fun salvarNomeUsuario(nome: String) {
        context.dataStore.edit { preferencias ->
            preferencias[PreferenciasChaves.NOME_USUARIO] = nome
        }
    }
}
```

### Conectando o DataStore ao ViewModel e ao Compose

```kotlin
// ConfiguracoesViewModel.kt
class ConfiguracoesViewModel(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    // Converte o Flow do DataStore em StateFlow para a UI observar.
    // stateIn: converte Flow em StateFlow com valor inicial e escopo definidos.
    val temaEscuro: StateFlow<Boolean> = dataStoreManager.temaEscuroFlow
        .stateIn(
            scope          = viewModelScope,       // cancela quando o ViewModel é destruído
            started        = SharingStarted.Eagerly, // começa a coletar imediatamente
            initialValue   = false                 // valor inicial antes do primeiro emit
        )

    fun alternarTema(ativado: Boolean) {
        viewModelScope.launch {
            dataStoreManager.salvarTemaEscuro(ativado)
        }
    }
}

// TelaConfiguracoes.kt
@Composable
fun TelaConfiguracoes(viewModel: ConfiguracoesViewModel = viewModel()) {
    val temaEscuro by viewModel.temaEscuro.collectAsStateWithLifecycle()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Tema escuro", modifier = Modifier.weight(1f))

        // Switch reativo — muda quando temaEscuro muda
        Switch(
            checked  = temaEscuro,
            onCheckedChange = { viewModel.alternarTema(it) }
        )
    }
}
```

---

## 7.3 Room — Banco de Dados Local

Room é uma biblioteca de persistência que fornece uma **camada de abstração sobre o SQLite**. Você trabalha com classes Kotlin e anotações — o Room gera o SQL necessário em tempo de compilação.

Room tem três componentes obrigatórios:

```
┌─────────────────────────────────────────────┐
│  Entity       → define uma tabela           │
│  Dao          → define as operações SQL     │
│  Database     → ponto de acesso ao banco    │
└─────────────────────────────────────────────┘
```

### Dependências

```kotlin
// app/build.gradle.kts
plugins {
    // KSP: processador de anotações do Kotlin — necessário para o Room
    // gerar o código SQL em tempo de compilação
    id("com.google.devtools.ksp")
}

dependencies {
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")  // suporte a coroutines e Flow
    ksp("androidx.room:room-compiler:2.6.1")         // gerador de código — use ksp, não kapt
}
```

---

### 7.3.1 Entity — definindo uma tabela

`@Entity` transforma uma `data class` Kotlin em uma tabela do banco de dados. Cada propriedade da classe vira uma coluna da tabela.

```kotlin
// Tarefa.kt

@Entity(tableName = "tarefas")  // nome da tabela no banco de dados
data class Tarefa(

    // @PrimaryKey identifica cada linha da tabela de forma única.
    // autoGenerate = true: o banco gera um ID numérico automaticamente
    // para cada nova tarefa inserida (1, 2, 3...).
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // @ColumnInfo define o nome da coluna no banco.
    // Se omitido, o Room usa o nome da propriedade Kotlin.
    @ColumnInfo(name = "titulo")
    val titulo: String,

    @ColumnInfo(name = "concluida")
    val concluida: Boolean = false,

    @ColumnInfo(name = "data_criacao")
    val dataCriacao: Long = System.currentTimeMillis()  // timestamp em milissegundos
)
```

**Mapeamento Kotlin → SQL:**
```
data class Tarefa         →   CREATE TABLE tarefas (
    id: Int               →       id INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo: String        →       titulo TEXT NOT NULL,
    concluida: Boolean    →       concluida INTEGER NOT NULL,
    dataCriacao: Long     →       data_criacao INTEGER NOT NULL
)                             )
```

---

### 7.3.2 DAO — definindo as operações

`@Dao` é uma interface onde você declara as operações que serão executadas no banco. O Room **gera automaticamente a implementação** dessas operações em tempo de compilação.

```kotlin
// TarefaDao.kt

@Dao  // Data Access Object — interface de acesso ao banco
interface TarefaDao {

    // ── LEITURA ─────────────────────────────────────────────────────────

    // @Query executa SQL customizado.
    // Retornar Flow<List<T>> (sem suspend) faz o Room emitir automaticamente
    // uma nova lista toda vez que os dados da tabela mudam.
    // A UI se mantém sincronizada com o banco sem nenhum código extra.
    @Query("SELECT * FROM tarefas ORDER BY data_criacao DESC")
    fun buscarTodas(): Flow<List<Tarefa>>

    // Busca uma tarefa específica por ID.
    // suspend: operação única, não reativa.
    @Query("SELECT * FROM tarefas WHERE id = :id")
    suspend fun buscarPorId(id: Int): Tarefa?

    // @Query com parâmetro de filtragem.
    // :concluida referencia o parâmetro da função.
    @Query("SELECT * FROM tarefas WHERE concluida = :concluida")
    fun buscarPorStatus(concluida: Boolean): Flow<List<Tarefa>>


    // ── ESCRITA ─────────────────────────────────────────────────────────

    // @Insert insere um ou mais registros.
    // OnConflictStrategy.REPLACE: se já existe uma tarefa com o mesmo ID,
    // substitui o registro existente pelo novo.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(tarefa: Tarefa)

    // @Update atualiza o registro que tem o mesmo ID que o objeto passado.
    @Update
    suspend fun atualizar(tarefa: Tarefa)

    // @Delete remove o registro que tem o mesmo ID que o objeto passado.
    @Delete
    suspend fun remover(tarefa: Tarefa)

    // @Query também pode executar DELETE com condição específica.
    @Query("DELETE FROM tarefas WHERE id = :id")
    suspend fun removerPorId(id: Int)

    // Remove todos os registros da tabela.
    @Query("DELETE FROM tarefas")
    suspend fun removerTodas()
}
```

**Regra importante sobre `suspend` e `Flow`:**

| Tipo de retorno | Quando usar |
|---|---|
| `suspend fun`: retorna `T` | Operação única: inserir, atualizar, deletar, buscar por ID |
| `fun`: retorna `Flow<T>` | Observação reativa: listar dados que podem mudar ao longo do tempo |

---

### 7.3.3 Database — ponto de acesso ao banco

```kotlin
// TarefaDatabase.kt

@Database(
    entities  = [Tarefa::class],  // lista todas as tabelas do banco
    version   = 1,                // versão do schema — incrementar ao alterar tabelas
    exportSchema = false          // desativa exportação do schema para arquivo JSON
)
abstract class TarefaDatabase : RoomDatabase() {

    // Declara o DAO como função abstrata.
    // O Room gera a implementação automaticamente.
    abstract fun tarefaDao(): TarefaDao

    companion object {

        // @Volatile garante que o valor de INSTANCE seja sempre
        // lido da memória principal, nunca de cache de thread.
        // Isso evita que duas threads criem instâncias simultâneas.
        @Volatile
        private var INSTANCE: TarefaDatabase? = null

        fun getInstance(context: Context): TarefaDatabase {
            // synchronized garante que apenas uma thread por vez
            // execute este bloco — evita criação duplicada do banco.
            return INSTANCE ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,  // applicationContext evita memory leak
                    TarefaDatabase::class.java,
                    "tarefas.db"                 // nome do arquivo do banco em disco
                ).build()

                INSTANCE = instancia
                instancia
            }
        }
    }
}
```

**Por que Singleton?**
Criar múltiplas instâncias do `RoomDatabase` é caro (abre múltiplas conexões com o arquivo SQLite) e pode causar conflitos de acesso. O padrão Singleton garante que existe **exatamente uma instância** do banco em todo o app.

---

## 7.4 O padrão Repository

O `ViewModel` não deve acessar o `DAO` diretamente. A camada intermediária entre eles é o **Repository**.

**Por que essa separação?**

Imagine que no futuro você adicione uma API REST ao app. Sem o Repository, você precisaria modificar o ViewModel para decidir entre buscar do banco local ou da API. Com o Repository, o ViewModel não sabe de onde os dados vêm — ele apenas pede os dados e o Repository decide a fonte.

```kotlin
// TarefaRepository.kt

class TarefaRepository(private val dao: TarefaDao) {

    // Expõe o Flow do DAO diretamente.
    // O ViewModel observa este Flow — quando o banco muda,
    // o Flow emite e o ViewModel atualiza o UiState.
    val todasTarefas: Flow<List<Tarefa>> = dao.buscarTodas()

    suspend fun inserir(tarefa: Tarefa) = dao.inserir(tarefa)

    suspend fun atualizar(tarefa: Tarefa) = dao.atualizar(tarefa)

    suspend fun remover(tarefa: Tarefa) = dao.remover(tarefa)

    suspend fun buscarPorId(id: Int): Tarefa? = dao.buscarPorId(id)
}
```

---

## 7.5 Conectando Room ao ViewModel

```kotlin
// TarefasViewModel.kt

data class TarefasUiState(
    val tarefas: List<Tarefa> = emptyList(),
    val carregando: Boolean   = false,
    val erro: String?         = null
)

class TarefasViewModel(
    private val repositorio: TarefaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TarefasUiState(carregando = true))
    val uiState: StateFlow<TarefasUiState> = _uiState.asStateFlow()

    init {
        observarTarefas()
    }

    private fun observarTarefas() {
        viewModelScope.launch {
            // repositorio.todasTarefas é um Flow<List<Tarefa>>.
            // .collect { } executa o bloco toda vez que o Flow emite.
            // Como o DAO retorna Flow, qualquer INSERT/UPDATE/DELETE no banco
            // automaticamente dispara um novo emit e atualiza a UI.
            repositorio.todasTarefas.collect { listaDoBanco ->
                _uiState.update {
                    it.copy(tarefas = listaDoBanco, carregando = false)
                }
            }
        }
    }

    fun adicionarTarefa(titulo: String) {
        viewModelScope.launch {
            repositorio.inserir(Tarefa(titulo = titulo))
            // Não precisa atualizar o estado manualmente aqui.
            // O Flow do DAO já emite automaticamente com a nova tarefa.
        }
    }

    fun marcarConcluida(tarefa: Tarefa) {
        viewModelScope.launch {
            // .copy() cria uma nova instância da Tarefa com apenas
            // o campo 'concluida' alterado — padrão imutável
            repositorio.atualizar(tarefa.copy(concluida = !tarefa.concluida))
        }
    }

    fun removerTarefa(tarefa: Tarefa) {
        viewModelScope.launch {
            repositorio.remover(tarefa)
        }
    }
}
```

---

## 7.6 Estrutura completa de arquivos

```
app/
└── src/main/kotlin/
    └── com.exemplo.app/
        │
        ├── data/                          ← camada de dados
        │   ├── local/
        │   │   ├── TarefaDatabase.kt      → instância do Room (Singleton)
        │   │   ├── TarefaDao.kt           → interface de operações SQL
        │   │   └── Tarefa.kt              → Entity (tabela)
        │   ├── datastore/
        │   │   └── DataStoreManager.kt    → leitura e escrita de preferências
        │   └── repository/
        │       └── TarefaRepository.kt    → abstração entre ViewModel e fontes
        │
        ├── ui/                            ← camada de apresentação
        │   ├── tarefas/
        │   │   ├── TarefasViewModel.kt    → estado e lógica da tela
        │   │   └── TarefasScreen.kt       → composables da tela
        │   └── theme/
        │       ├── Color.kt
        │       ├── Theme.kt
        │       └── Type.kt
        │
        └── MainActivity.kt
```

---

## 7.7 Fluxo completo de dados: da UI ao disco

```
Usuário digita título e clica em "Adicionar"
    │
    ▼
Composable chama onAdicionarTarefa("Comprar leite")
    │
    ▼
TarefasViewModel.adicionarTarefa("Comprar leite")
    │
    ▼
TarefaRepository.inserir(Tarefa(titulo = "Comprar leite"))
    │
    ▼
TarefaDao.inserir(tarefa) — suspend function
    │
    ▼
Room executa: INSERT INTO tarefas (titulo, ...) VALUES ("Comprar leite", ...)
    │
    ▼
Room detecta mudança na tabela → Flow<List<Tarefa>> emite nova lista
    │
    ▼
TarefasViewModel.observarTarefas() recebe nova lista via .collect { }
    │
    ▼
_uiState.update { it.copy(tarefas = novaLista) }
    │
    ▼
StateFlow emite novo UiState
    │
    ▼
collectAsStateWithLifecycle() detecta mudança → recomposição
    │
    ▼
LazyColumn exibe a lista com a nova tarefa
```

---

## 7.8 Resumo da Parte 7

| Conceito | Definição resumida |
|---|---|
| `DataStore Preferences` | Armazenamento assíncrono de pares chave-valor em disco — substituto do SharedPreferences |
| `Preferences.Key<T>` | Chave tipada que define o nome e o tipo do dado no DataStore |
| `dataStore.data` | Flow que emite os valores atuais toda vez que qualquer preferência muda |
| `dataStore.edit { }` | suspend function para escrever valores no DataStore |
| `@Entity` | Anotação que transforma uma data class em tabela do banco Room |
| `@PrimaryKey(autoGenerate)` | Chave primária com geração automática de ID numérico |
| `@Dao` | Interface onde as operações SQL são declaradas com anotações |
| `@Query` | Executa SQL customizado — retorna `Flow<T>` para reatividade ou `suspend T` para operação única |
| `@Insert` / `@Update` / `@Delete` | Operações CRUD geradas automaticamente pelo Room |
| `RoomDatabase` | Classe abstrata que centraliza o acesso ao banco — deve ser Singleton |
| `Repository` | Camada entre ViewModel e fontes de dados — abstrai de onde os dados vêm |
| `Flow` no DAO | Emite automaticamente quando o banco muda — elimina necessidade de recarregar manualmente |

---


