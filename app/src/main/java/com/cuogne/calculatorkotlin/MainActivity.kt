package com.cuogne.calculatorkotlin

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cuogne.calculatorkotlin.R.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val numberButtons = listOf(
            findViewById<Button>(id.no_zero),
            findViewById<Button>(id.no_one),
            findViewById<Button>(id.no_two),
            findViewById<Button>(id.no_three),
            findViewById<Button>(id.no_four),
            findViewById<Button>(id.no_five),
            findViewById<Button>(id.no_six),
            findViewById<Button>(id.no_seven),
            findViewById<Button>(id.no_eight),
            findViewById<Button>(id.no_nine),
        )

        val operators = listOf(
            findViewById<Button>(id.add),
            findViewById<Button>(id.subtract),
            findViewById<Button>(id.multiply),
            findViewById<Button>(id.divide),
//            findViewById<Button>(id.point),
            findViewById<Button>(id.parentheses_open),
            findViewById<Button>(id.parentheses_close),
        )

        val equal = findViewById<Button>(id.equal)
        val expression = findViewById<TextView>(id.expression)
        val result = findViewById<TextView>(id.result)
        val delOne = findViewById<Button>(id.delete_one)

        numberButtons.forEachIndexed { index, button ->

            button.setOnClickListener {
                if (result?.text.toString().startsWith("0")){
                    result?.text = index.toString()
                }
                else result?.append(index.toString())
            }
        }

        operators.forEach { button ->
            button.setOnClickListener {
                result?.append(button.text)
            }
        }

        equal.setOnClickListener {
            val expressionStr = result?.text.toString()
            expression?.text = expressionStr

            val expressionList: List<String> = handleExpression(expressionStr) // [1, +, 2, *, 3]

            val calculationResult: Double = rpnResult(shuntingYardAlgorithm(expressionList))
            if (calculationResult % 1 == 0.0) {
                 result.text = calculationResult.toInt().toString()
            } else {
                 result.text = calculationResult.toString()
            }
        }

        delOne.setOnClickListener {
            if (expression?.text?.isNotEmpty() == true){
                expression.text = ""
                result.text = "0"
            }
            else {
                result?.text = result.text?.dropLast(1)
                if (result?.text?.isEmpty() == true){
                    result.text = "0"
                }
            }
        }
    }

    private fun handleExpression(expression: String): List<String> {
        val tokens = mutableListOf<String>()
        val curNum = StringBuilder()

        expression.forEach{char ->
            if (char.isDigit()){
                curNum.append(char)
            }
            else {
                if (curNum.isNotEmpty()){
                    tokens.add(curNum.toString())
                    curNum.clear()
                }
                tokens.add(char.toString())
            }
        }

        if (curNum.isNotEmpty()){
            tokens.add(curNum.toString())
        }
        return tokens
    }


    // https://tylerpexton-70687.medium.com/the-shunting-yard-algorithm-b840844141b2
    // https://brilliant.org/wiki/shunting-yard-algorithm/
    private fun shuntingYardAlgorithm(expressionList: List<String>): List<String> {
        val outputQueue = mutableListOf<String>()
        val operatorStack = mutableListOf<String>()

        val precedence = mapOf(
            "+" to 1,
            "-" to 1,
            "*" to 2,
            "/" to 2
        )

        expressionList.forEach { token ->
            when {
                token.toIntOrNull() != null -> {
                    outputQueue.add(token)
                }

                token == "(" -> operatorStack.add(token)

                token == ")" -> {
                    while (operatorStack.last() != "(" && operatorStack.isNotEmpty()) {
                        outputQueue.add(operatorStack.removeAt(operatorStack.size - 1))
                    }
                    if (operatorStack.isNotEmpty()){
                        operatorStack.removeAt(operatorStack.size - 1)
                    }
                }

                token in precedence.keys -> {
                    while (
                        operatorStack.isNotEmpty() &&
                        operatorStack.last() in precedence.keys &&
                        precedence[token]!! <= precedence[operatorStack.last()]!!
                    )
                    {
                        // removeLast() requires API level 35
                        outputQueue.add(operatorStack.removeAt(operatorStack.size - 1))
                    }
                    operatorStack.add(token)
                }
            }
        }

        while (operatorStack.isNotEmpty()){
            outputQueue.add(operatorStack.removeAt(operatorStack.size - 1))
        }

        // return a list of reverse polish notation
        return outputQueue
    }

    private fun rpnResult(rpnList: List<String>): Double {
        val rpnStack = mutableListOf<Double>()

        rpnList.forEach {token ->
            when {
                token.toIntOrNull() != null -> {
                    rpnStack.add(token.toDouble())
                }

                token in listOf("+", "-", "*", "/") -> {
                    val a = rpnStack.removeAt(rpnStack.size - 1)
                    val b = rpnStack.removeAt(rpnStack.size - 1)
                    when (token) {
                        "+" -> rpnStack.add(b + a)
                        "-" -> rpnStack.add(b - a)
                        "*" -> rpnStack.add(b * a)
                        "/" -> {
                            if (a != 0.0) rpnStack.add(b / a)
                            else throw ArithmeticException("Division by zero")
                        }
                        else -> throw IllegalArgumentException("Invalid operator: $token")
                    }
                }
            }
        }
        return rpnStack.last()
    }
}