package com.example.listsearching

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.io.BufferedReader
import java.io.InputStreamReader
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.listsearching.R

class MainActivity : AppCompatActivity() {

    // Data class Student
    data class Student(val name: String, val studentId: String)

    // Adapter class StudentAdapter
    class StudentAdapter(private var studentList: List<Student>) :
        RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

        class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val nameTextView: TextView = view.findViewById(R.id.nameTextView)
            val idTextView: TextView = view.findViewById(R.id.idTextView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_student, parent, false)
            return StudentViewHolder(view)
        }

        override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
            val student = studentList[position]
            holder.nameTextView.text = student.name
            holder.idTextView.text = student.studentId
        }

        override fun getItemCount() = studentList.size

        fun updateList(newList: List<Student>) {
            studentList = newList
            notifyDataSetChanged()
        }
    }

    private lateinit var studentRecyclerView: RecyclerView
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var searchEditText: EditText
    private lateinit var studentList: List<Student>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        studentList = readStudentsFromCSV()
        searchEditText = findViewById(R.id.searchEditText)
        studentRecyclerView = findViewById(R.id.studentRecyclerView)

        // Khởi tạo adapter với danh sách sinh viên
        studentAdapter = StudentAdapter(studentList)
        studentRecyclerView.adapter = studentAdapter
        studentRecyclerView.layoutManager = LinearLayoutManager(this)

        // Thiết lập ô tìm kiếm
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
    private fun readStudentsFromCSV(): List<Student> {
        val studentList = mutableListOf<Student>()
        val inputStream = resources.openRawResource(R.raw.students)
        val reader = BufferedReader(InputStreamReader(inputStream))

        reader.forEachLine { line ->
            val tokens = line.split(",")
            if (tokens.size == 2) {
                val name = tokens[0].trim()
                val studentId = tokens[1].trim()
                studentList.add(Student(name, studentId))
            }
        }

        reader.close()
        return studentList
    }

    // Hàm filter để lọc danh sách sinh viên theo từ khóa
    private fun filter(query: String) {
        val filteredList = if (query.length > 2) {
            studentList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.studentId.contains(query, ignoreCase = true)
            }
        } else {
            studentList
        }
        studentAdapter.updateList(filteredList)
    }
}
