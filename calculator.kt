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
    fun UpdateDisplay(value: String) {
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
                CalcButton("7") { UpdateDisplay("7") }
                CalcButton("8") { UpdateDisplay("8") }
                CalcButton("9") { UpdateDisplay("9") }
                CalcButton("/") { UpdateDisplay("/") }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CalcButton("4") { UpdateDisplay("4") }
                CalcButton("5") { UpdateDisplay("5") }
                CalcButton("6") { UpdateDisplay("6") }
                CalcButton("-") { UpdateDisplay("-") }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CalcButton("1") { UpdateDisplay("1") }
                CalcButton("2") { UpdateDisplay("2") }
                CalcButton("3") { UpdateDisplay("3") }
                CalcButton("+") { UpdateDisplay("+") }


            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CalcButton("0") { UpdateDisplay("0") }
                CalcButton("C") {
                    expression = ""
                    display = "0"
                }

                CalcButton("=") {
                    display = calculateExpression(display).toString()
                }
                CalcButton("*") { UpdateDisplay("*") }
                //Spacer(modifier = Modifier.weight(1f))
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
