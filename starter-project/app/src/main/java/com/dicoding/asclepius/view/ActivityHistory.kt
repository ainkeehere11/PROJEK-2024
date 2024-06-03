package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.HistoryAdapter.HistoryAdapter
import com.dicoding.asclepius.R
import com.dicoding.asclepius.riwayat.HistoryDatabase
import com.dicoding.asclepius.riwayat.HistoryPrediction
import com.dicoding.asclepius.view.ResultActivity.Companion.UPDATE_HISTORY
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ActivityHistory : AppCompatActivity(), HistoryAdapter.OnDeleteClickListener {
    private lateinit var historyRV: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private var historyList: MutableList<HistoryPrediction> = mutableListOf()
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var tvNotFound: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        historyRV = findViewById(R.id.rvhistory)
        bottomNavigationView = findViewById(R.id.menuBar)
        bottomNavigationView.selectedItemId = R.id.history
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.main -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.history -> {
                    true
                }
                else -> false
            }
        }
        historyRV = findViewById(R.id.rvhistory)
        tvNotFound = findViewById(R.id.tvNotFound)

        historyAdapter = HistoryAdapter(historyList)
        historyAdapter.setOnDeleteClickListener(this)
        historyRV.adapter = historyAdapter
        historyRV.layoutManager = LinearLayoutManager(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        GlobalScope.launch(Dispatchers.Main) {
            loadHistoryFromDatabase()
        }
    }


    companion object{
        const val TAG = "riwayatdata"
    }

    private fun loadHistoryFromDatabase() {
        GlobalScope.launch(Dispatchers.Main) {
            val predictions = HistoryDatabase.getDatabase(this@ActivityHistory).predictionHistoryDao().getAllPredictions()
            Log.d(TAG, "Number of predictions: ${predictions.size}")
            historyList.clear()
            historyList.addAll(predictions)
            historyAdapter.notifyDataSetChanged()
            showOrHideNoHistoryText()
        }
    }


    private fun showOrHideNoHistoryText() {
        if (historyList.isEmpty()) {
            historyRV.visibility = View.GONE
        } else {
            historyRV.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_HISTORY && resultCode == RESULT_OK) {
            GlobalScope.launch(Dispatchers.Main) {
                loadHistoryFromDatabase()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDeleteClick(position: Int) {
        val prediction = historyList[position]
        if (prediction.result.isNotEmpty()) {
            GlobalScope.launch(Dispatchers.IO) {
                HistoryDatabase.getDatabase(this@ActivityHistory).predictionHistoryDao().deletePrediction(prediction)
            }
            historyList.removeAt(position)
            historyAdapter.notifyDataSetChanged()
            showOrHideNoHistoryText()
        }
    }

}
