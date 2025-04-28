package com.example.matrixcalculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.matrixcalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // load native library
    companion object {
        init {
            System.loadLibrary("MatrixCalculator")
        }
    }

    external fun addMatrices(
        mat1: Array<DoubleArray>, mat2: Array<DoubleArray>, 
        rows1: Int, cols1: Int, rows2: Int, cols2: Int
    ): Array<DoubleArray>?
    
    external fun subtractMatrices(
        mat1: Array<DoubleArray>, mat2: Array<DoubleArray>, 
        rows1: Int, cols1: Int, rows2: Int, cols2: Int
    ): Array<DoubleArray>?
    
    external fun multiplyMatrices(
        mat1: Array<DoubleArray>, mat2: Array<DoubleArray>, 
        rows1: Int, cols1: Int, rows2: Int, cols2: Int
    ): Array<DoubleArray>?
    
    external fun divideMatrices(
        mat1: Array<DoubleArray>, mat2: Array<DoubleArray>, 
        rows1: Int, cols1: Int, rows2: Int, cols2: Int
    ): Array<DoubleArray>?
    
    external fun divideByInverseMatrices(
        mat1: Array<DoubleArray>, mat2: Array<DoubleArray>, 
        rows1: Int, cols1: Int, rows2: Int, cols2: Int
    ): Array<DoubleArray>?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAdd.setOnClickListener { performOperation("add") }
        binding.buttonSubtract.setOnClickListener { performOperation("subtract") }
        binding.buttonMultiply.setOnClickListener { performOperation("multiply") }
        binding.buttonDivideElement.setOnClickListener { performOperation("divide") }
        binding.buttonDivideInverse.setOnClickListener { performOperation("divideInverse") }
        binding.buttonClear.setOnClickListener { clearAll() }

        setupMatrixPreviewListeners()
    }
    
    private fun clearAll() {
        binding.editTextRows1.setText("")
        binding.editTextCols1.setText("")
        binding.editTextRows2.setText("")
        binding.editTextCols2.setText("")
        binding.editTextMatrix1.setText("")
        binding.editTextMatrix2.setText("")
        
        binding.textViewPreview1.text = ""
        binding.textViewPreview2.text = ""
        binding.textViewResult.text = getString(R.string.result)
        
        Toast.makeText(this, "All inputs cleared", Toast.LENGTH_SHORT).show()
    }

    private fun setupMatrixPreviewListeners() {
        val dimensionChangeListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                updateMatrixPreviews()
            }
        }

        binding.editTextRows1.addTextChangedListener(dimensionChangeListener)
        binding.editTextCols1.addTextChangedListener(dimensionChangeListener)
        binding.editTextRows2.addTextChangedListener(dimensionChangeListener)
        binding.editTextCols2.addTextChangedListener(dimensionChangeListener)

        binding.editTextMatrix1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                updateMatrixPreview(1)
            }
        })

        binding.editTextMatrix2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                updateMatrixPreview(2)
            }
        })
    }
    
    private fun updateMatrixPreviews() {
        updateMatrixPreview(1)
        updateMatrixPreview(2)
    }

    private fun updateMatrixPreview(matrixNum: Int) {
        val rowsText = if (matrixNum == 1) binding.editTextRows1.text else binding.editTextRows2.text
        val colsText = if (matrixNum == 1) binding.editTextCols1.text else binding.editTextCols2.text
        val matrixText = if (matrixNum == 1) binding.editTextMatrix1.text else binding.editTextMatrix2.text
        val previewText = if (matrixNum == 1) binding.textViewPreview1 else binding.textViewPreview2

        val rows = rowsText.toString().toIntOrNull() ?: 0
        val cols = colsText.toString().toIntOrNull() ?: 0

        if (rows <= 0 || cols <= 0 || matrixText.isNullOrBlank()) {
            previewText.text = ""
            return
        }

        try {
            val matrix = readMatrix(matrixText.toString(), rows, cols)
            previewText.text = formatMatrixPreview(matrix)
        } catch (e: Exception) {
            // if there's an error (like not enough elements), show partial preview
            val elements = matrixText.toString().split(Regex("\\s+")).filter { it.isNotBlank() }
            previewText.text = formatPartialMatrixPreview(elements, rows, cols)
        }
    }

    private fun formatPartialMatrixPreview(elements: List<String>, rows: Int, cols: Int): String {
        val sb = StringBuilder("Preview:\n")
        var idx = 0
        
        for (i in 0 until rows) {
            sb.append("[ ")
            for (j in 0 until cols) {
                if (idx < elements.size) {
                    try {
                        val value = elements[idx++].toDouble()
                        sb.append(String.format("%-8.2f", value))
                    } catch (e: NumberFormatException) {
                        sb.append("???     ")
                        idx++
                    }
                } else {
                    sb.append("?       ")
                }
            }
            sb.append("]\n")
        }
        
        // if extra elements
        if (idx < elements.size) {
            sb.append("\nWarning: ${elements.size - idx} extra elements")
        }
        
        return sb.toString()
    }

    private fun formatMatrixPreview(matrix: Array<DoubleArray>): String {
        val sb = StringBuilder("Preview:\n")
        
        for (row in matrix) {
            sb.append("[ ")
            for (value in row) {
                sb.append(String.format("%-8.2f", value))
            }
            sb.append("]\n")
        }
        
        return sb.toString()
    }

    private fun performOperation(operation: String) {
        try {
            val rows1 = binding.editTextRows1.text.toString().toIntOrNull() ?: 0
            val cols1 = binding.editTextCols1.text.toString().toIntOrNull() ?: 0
            val rows2 = binding.editTextRows2.text.toString().toIntOrNull() ?: 0
            val cols2 = binding.editTextCols2.text.toString().toIntOrNull() ?: 0
            
            if (rows1 <= 0 || cols1 <= 0 || rows2 <= 0 || cols2 <= 0) {
                showError("Invalid dimensions. All values must be positive numbers.")
                return
            }
            
            when (operation) {
                "add", "subtract", "divide" -> {
                    if (rows1 != rows2 || cols1 != cols2) {
                        showError("For $operation, both matrices must have the same dimensions.")
                        return
                    }
                }
                "multiply" -> {
                    if (cols1 != rows2) {
                        showError("For multiplication, columns of first matrix must equal rows of second matrix.")
                        return
                    }
                }
                "divideInverse" -> {
                    if (rows2 != cols2) {
                        showError("For matrix division, the second matrix must be square.")
                        return
                    }
                    if (cols1 != rows2) {
                        showError("For matrix division, columns of first matrix must equal rows of second matrix.")
                        return
                    }
                }
            }

            val matrix1String = binding.editTextMatrix1.text.toString().trim()
            val matrix2String = binding.editTextMatrix2.text.toString().trim()
            
            if (matrix1String.isEmpty() || matrix2String.isEmpty()) {
                showError("Please enter values for both matrices.")
                return
            }
            
            val matrix1 = try {
                readMatrix(matrix1String, rows1, cols1)
            } catch (e: Exception) {
                showError("Error in Matrix 1 input: ${e.message}")
                return
            }
            
            val matrix2 = try {
                readMatrix(matrix2String, rows2, cols2)
            } catch (e: Exception) {
                showError("Error in Matrix 2 input: ${e.message}")
                return
            }

            val result = when (operation) {
                "add" -> addMatrices(matrix1, matrix2, rows1, cols1, rows2, cols2)
                "subtract" -> subtractMatrices(matrix1, matrix2, rows1, cols1, rows2, cols2)
                "multiply" -> multiplyMatrices(matrix1, matrix2, rows1, cols1, rows2, cols2)
                "divide" -> divideMatrices(matrix1, matrix2, rows1, cols1, rows2, cols2)
                "divideInverse" -> divideByInverseMatrices(matrix1, matrix2, rows1, cols1, rows2, cols2)
                else -> throw IllegalArgumentException("Invalid operation")
            }

            if (result == null) {
                when (operation) {
                    "divide" -> showError("Division error: Matrix 2 contains zero elements.")
                    "divideInverse" -> showError("Matrix division error: Matrix 2 is not invertible (determinant is zero).")
                    else -> showError("Operation failed. Please check matrix dimensions and values.")
                }
                return
            }

            binding.textViewResult.text = formatMatrix(result)
        } catch (e: Exception) {
            showError("Error: ${e.message}")
        }
    }

    private fun readMatrix(input: String, rows: Int, cols: Int): Array<DoubleArray> {
        val lines = input.trim().split("\n")
        val matrix = Array(rows) { DoubleArray(cols) }
        
        if (lines.size <= 1) {
            val values = input.split(Regex("\\s+")).filter { it.isNotBlank() }
            
            if (values.size != rows * cols) {
                throw IllegalArgumentException("Expected ${rows * cols} elements, found ${values.size}")
            }
            
            var idx = 0
            for (i in 0 until rows) {
                for (j in 0 until cols) {
                    try {
                        matrix[i][j] = values[idx++].toDouble()
                    } catch (e: NumberFormatException) {
                        throw IllegalArgumentException("Invalid number format in matrix")
                    }
                }
            }
        } else {
            if (lines.size != rows) {
                throw IllegalArgumentException("Expected $rows rows, found ${lines.size}")
            }
            
            for (i in 0 until rows) {
                val values = lines[i].trim().split(Regex("\\s+")).filter { it.isNotBlank() }
                
                if (values.size != cols) {
                    throw IllegalArgumentException("Row ${i+1} has ${values.size} elements, expected $cols")
                }
                
                for (j in 0 until cols) {
                    try {
                        matrix[i][j] = values[j].toDouble()
                    } catch (e: NumberFormatException) {
                        throw IllegalArgumentException("Invalid number in row ${i+1}, column ${j+1}")
                    }
                }
            }
        }
        
        return matrix
    }

    @SuppressLint("DefaultLocale")
    private fun formatMatrix(matrix: Array<DoubleArray>): String {
        val sb = StringBuilder("Result Matrix (${matrix.size} × ${matrix[0].size}):\n\n")
        
        for (row in matrix) {
            sb.append("[ ")
            for (value in row) {
                sb.append(String.format("%-8.2f", value))
            }
            sb.append("]\n")
        }
        
        return sb.toString()
    }
    
    private fun showError(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
