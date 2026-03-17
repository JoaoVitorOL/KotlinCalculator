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

