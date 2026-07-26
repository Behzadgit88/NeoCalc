package com.neocalc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeoCalcTheme {
                CalculatorScreen()
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var display by remember { mutableStateOf("0") }
    var expression by remember { mutableStateOf("") }
    var isDark by remember { mutableStateOf(true) }

    val backgroundColor = if (isDark) Color(0xFF121212) else Color(0xFFF5F5F5)
    val textColor = if (isDark) Color.White else Color.Black
    val buttonColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    val operatorColor = Color(0xFFFF9500)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        // Theme Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { isDark = !isDark }) {
                Text(
                    text = if (isDark) "تم روشن" else "تم تاریک",
                    color = textColor
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Display
        Text(
            text = expression,
            color = textColor.copy(alpha = 0.6f),
            fontSize = 24.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )
        Text(
            text = display,
            color = textColor,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Buttons
        val buttons = listOf(
            listOf("C", "±", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "−"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "=")
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { btn ->
                    val weight = if (btn == "0") 2f else 1f
                    Button(
                        onClick = {
                            when (btn) {
                                "C" -> {
                                    display = "0"
                                    expression = ""
                                }
                                "=" -> {
                                    try {
                                        val result = evaluate(expression + display)
                                        expression = ""
                                        display = result
                                    } catch (e: Exception) {
                                        display = "Error"
                                    }
                                }
                                "±" -> {
                                    display = if (display.startsWith("-")) display.drop(1) else "-$display"
                                }
                                "%" -> {
                                    display = (display.toDoubleOrNull()?.div(100) ?: 0.0).toString()
                                }
                                in listOf("+", "−", "×", "÷") -> {
                                    expression += display + btn
                                    display = "0"
                                }
                                else -> {
                                    display = if (display == "0" || display == "Error") btn else display + btn
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(weight)
                            .aspectRatio(1f),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (btn in listOf("÷", "×", "−", "+", "=")) operatorColor else buttonColor,
                            contentColor = if (btn in listOf("÷", "×", "−", "+", "=")) Color.White else textColor
                        )
                    ) {
                        Text(text = btn, fontSize = 24.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

fun evaluate(expr: String): String {
    var expression = expr
        .replace("×", "*")
        .replace("÷", "/")
        .replace("−", "-")

    return try {
        val result = expression.toDoubleOrNull() ?: calculateSimple(expression)
        if (result % 1.0 == 0.0) result.toInt().toString() else result.toString()
    } catch (e: Exception) {
        "Error"
    }
}

fun calculateSimple(expr: String): Double {
    // Simple left-to-right calculation
    val tokens = mutableListOf<String>()
    var current = ""
    for (c in expr) {
        if (c.isDigit() || c == '.') {
            current += c
        } else if (c in "+-*/") {
            if (current.isNotEmpty()) {
                tokens.add(current)
                current = ""
            }
            tokens.add(c.toString())
        }
    }
    if (current.isNotEmpty()) tokens.add(current)

    if (tokens.isEmpty()) return 0.0
    var result = tokens[0].toDouble()
    var i = 1
    while (i < tokens.size - 1) {
        val op = tokens[i]
        val num = tokens[i + 1].toDouble()
        result = when (op) {
            "+" -> result + num
            "-" -> result - num
            "*" -> result * num
            "/" -> result / num
            else -> result
        }
        i += 2
    }
    return result
}

@Composable
fun NeoCalcTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(),
        content = content
    )
}
