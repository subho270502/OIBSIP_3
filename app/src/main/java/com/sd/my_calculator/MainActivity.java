package com.sd.my_calculator;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity {

    private TextView expressionTextView, solutionTextView;
    private boolean lastNumeric;
    private boolean stateError;
    private boolean lastDot;
    private Expression expression;
    private boolean calculated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Make sure this matches your XML layout file

        expressionTextView = findViewById(R.id.expression); // Replace with your result TextView ID
        solutionTextView = findViewById(R.id.solution); // Replace with your solution TextView ID
    }


    public void onOperatorClick(View view) {
        Button button = (Button) view;
        if (!calculated && !stateError && lastNumeric) {
            expressionTextView.append(button.getText());
            lastDot = false;
            lastNumeric = false;
            onEqual(false);
        }
    }

    public void onDecimalClick(View view) {
        if (lastNumeric && !stateError && !lastDot) {
            expressionTextView.append(".");
            lastNumeric = false;
            lastDot = true;
        }
    }


    public void onEqualClick(View view) {
        onEqual(true);
        if (!stateError){
            calculated = true;
        }
    }

    public void onDeleteClick(View view) {
        if (!calculated) {
            String currentText = expressionTextView.getText().toString();
            if (!currentText.isEmpty()) {
                expressionTextView.setText(currentText.substring(0, currentText.length() - 1));
                try {
                    char lastChar = currentText.charAt(currentText.length() - 2); // Check the new last char
                    if (Character.isDigit(lastChar)) {
                        onEqual(false);
                    }
                } catch (Exception e) {
                    solutionTextView.setText("");
                    solutionTextView.setVisibility(View.GONE);
                    Log.e("last char error", e.toString());
                }
            }
        }
    }

    public void onClearClick(View view) {
        expressionTextView.setText("");
        solutionTextView.setText("");
        stateError = false;
        lastDot = false;
        lastNumeric = false;
        solutionTextView.setVisibility(View.GONE);
        calculated = false;
    }

    private void onEqual(boolean isFinalResult) {
        if (lastNumeric && !stateError) {
            String txt = expressionTextView.getText().toString();
            if (txt.endsWith("+") || txt.endsWith("-") || txt.endsWith("*") || txt.endsWith("/")) {
                solutionTextView.setText("Error");
                stateError = true;
                lastNumeric = false;
                return;
            }
            expression = new ExpressionBuilder(txt).build();
            try {
                double result = expression.evaluate();
                if (isFinalResult) {
                    calculated = true;
                    solutionTextView.setText(String.valueOf(result));
                    solutionTextView.setTextSize(60);
                    solutionTextView.setAlpha(1.0f);
                    expressionTextView.setTextSize(45);
                } else {
                    solutionTextView.setVisibility(View.VISIBLE);
                    solutionTextView.setText("= " + result);
                    solutionTextView.setTextSize(40);
                    solutionTextView.setAlpha(0.7f);
                }
            } catch (ArithmeticException ex) {
                Log.e("evaluate error", ex.toString());
                solutionTextView.setText("Error");
                stateError = true;
                lastNumeric = false;
            }
        }
    }

    public void onDigitClick(View view) {
        if (!calculated) {
            Button button = (Button) view;
            if (stateError) {
                expressionTextView.setText(button.getText());
                stateError = false;
            } else {
                expressionTextView.append(button.getText());
            }
            lastNumeric = true;
            onEqual(false);
        }
    }

    public void onPercentageClick(View view) {
        if (!calculated && lastNumeric && !stateError){
           String currExp = expressionTextView.getText().toString();
           String lastNum = "";
           for (int i = currExp.length()-1; i>=0; i--){
               char c= currExp.charAt(i);
               if (Character.isDigit(c) || c == '.'){
                   lastNum = c + lastNum;
               } else {
                   break;
               }
           }
           if (!lastNum.isEmpty()){
               double percent = (Double.parseDouble(lastNum)*1)/100;
               String newExp = currExp.substring(0, currExp.length()-lastNum.length()) + percent;
               expressionTextView.setText(newExp);
               lastNumeric = true;
               lastDot = newExp.contains(".");
               onEqual(false);
           }
        }
    }
}