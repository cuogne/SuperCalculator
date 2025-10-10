package com.cuogne.calculatorkotlin

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.view.View
import android.widget.HorizontalScrollView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.cuogne.calculatorkotlin.R.*
import net.objecthunter.exp4j.ExpressionBuilder

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
            findViewById<Button>(id.point),
            findViewById<Button>(id.parentheses_open),
            findViewById<Button>(id.parentheses_close),
        )

        val equal = findViewById<Button>(id.equal)
        val expression = findViewById<TextView>(id.expression)
        val result = findViewById<TextView>(id.result)
        val delOne = findViewById<Button>(id.delete_one)

        val expressionScroll = findViewById<HorizontalScrollView>(R.id.expression_scroll)
        val resultScroll = findViewById<HorizontalScrollView>(R.id.result_scroll)

        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (result?.text.toString() == "0"){
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
            val expressionStr = result.text.toString()
            expression.text = expressionStr
            try {
                val resultValue = ExpressionBuilder(expressionStr).build().evaluate()
                if (resultValue % 1 == 0.0) {
                    result.text = resultValue.toInt().toString()
                }
                else result.text = resultValue.toString()
            } catch (e: Exception) {
                result.text = "Error"
            }
        }

        result?.addTextChangedListener {
            resultScroll.post { resultScroll.fullScroll(View.FOCUS_RIGHT) }
            expressionScroll.post { expressionScroll.fullScroll(View.FOCUS_RIGHT) }
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
}