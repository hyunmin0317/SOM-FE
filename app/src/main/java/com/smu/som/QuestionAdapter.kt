package com.smu.som

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class QuestionAdapter(
    var questionList: ArrayList<Question>,
    val inflater: LayoutInflater
) : RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val question: TextView
        val category: TextView

        init {
            question = itemView.findViewById(R.id.question)
            category = itemView.findViewById(R.id.category)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.question_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return questionList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.question.setText(questionList.get(position).question)
        holder.category.setText(questionList.get(position).category)
    }
}