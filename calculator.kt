package com.example.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CalculatorUI()
        }
    }
}



@Composable
fun CalculatorUI() {


    var expression by remember { mutableStateOf("") }
    var display by remember { mutableStateOf("0") }
    fun append(value: String) {
        expression += value
        display = expression
    }

    fun calculateExpression(expression: String): Double {

        val tokens = Regex("(?<=[-+*/])|(?=[-+*/])")
            .split(expression)

        val numbers = mutableListOf<Double>()
        val operators = mutableListOf<String>()

        tokens.forEach {
            if (it.matches(Regex("[+\\-*/]"))) {
                operators.add(it)
            } else {
                numbers.add(it.toDouble())
            }
        }

        var result = numbers[0]

        for (i in operators.indices) {

            val next = numbers[i + 1]

            result = when (operators[i]) {
                "+" -> result + next
                "-" -> result - next
                "*" -> result * next
                "/" -> result / next
                else -> result
            }
        }

        return result
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // DISPLAY
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // ocupa parte superior
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = display,
                fontSize = 48.sp
            )
        }

        // TECLADO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f) // teclado ocupa mais espaço
        ) {

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CalcButton("7") { append("7") }
                CalcButton("8") { append("8") }
                CalcButton("9") { append("9") }
                CalcButton("/") { append("/") }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CalcButton("4") { append("4") }
                CalcButton("5") { append("5") }
                CalcButton("6") { append("6") }
                CalcButton("-") { append("-") }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CalcButton("1") { append("1") }
                CalcButton("2") { append("2") }
                CalcButton("3") { append("3") }
                CalcButton("+") { append("+") }


            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CalcButton("0") { append("0") }
                CalcButton("C") {
                    expression = ""
                    display = "0"
                }

                CalcButton("=") {
                    display = calculateExpression(display).toString()
                }
                CalcButton("*") { append("*") }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}


@Composable
fun RowScope.CalcButton(
    text: String,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)      // distribui igualmente no Row
          //  .fillMaxHeight()  ocupa altura da linha
            .padding(4.dp)
    ) {
        Text(
            text = text,
            fontSize = 20.sp
        )
    }
}
