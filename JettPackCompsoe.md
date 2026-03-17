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

Cobre os conceitos de:
- `LazyColumn` e `LazyRow` — listas eficientes para grandes volumes de dados
- Por que `Column` com loop não serve para listas longas
- `items`, `itemsIndexed` e `item` no contexto de lazy lists
- `NavController` e `NavHost` — navegação entre telas
- Passagem de argumentos entre telas
- Estrutura de rotas

---
*Aguardando aprovação para prosseguir para a Parte 5.*

