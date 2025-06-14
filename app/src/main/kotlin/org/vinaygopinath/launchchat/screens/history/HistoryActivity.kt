package org.vinaygopinath.launchchat.screens.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.vinaygopinath.launchchat.R
import org.vinaygopinath.launchchat.helpers.DetailedActivityHelper
import org.vinaygopinath.launchchat.models.DetailedActivity
import org.vinaygopinath.launchchat.screens.main.MainActivity
import javax.inject.Inject

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {

    private val viewModel: HistoryViewModel by viewModels()

    @Inject
    lateinit var detailedActivityHelper: DetailedActivityHelper

    private val historyAdapter by lazy {
        HistoryAdapter(
            detailedActivityHelper,
            object : HistoryAdapter.HistoryClickListener {
                override fun onClick(detailedActivity: DetailedActivity) {
                    startActivity(
                        MainActivity.getHistoryIntent(
                            this@HistoryActivity,
                            detailedActivity.activity
                        )
                    )
                }
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        initializeView()
        initializeObservers()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.history_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_history_clear_all -> {
                showWarningDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showWarningDialog() {
        AlertDialog.Builder(this)
            .setTitle("Warning")
            .setMessage("Are you sure you want to delete all activity history entries? This action is irreversible.")
            .setPositiveButton("Yes") { _, _ ->
                lifecycleScope.launch {
                    viewModel.deleteAllActivities()
                    historyAdapter.refresh()

                }
            }

            .setNegativeButton("No", null)
            .show()
    }


    private fun initializeView() {
        with(findViewById<RecyclerView>(R.id.history_recycler_view)) {
            val linearLayoutManager = LinearLayoutManager(this@HistoryActivity)
            layoutManager = linearLayoutManager
            setPadding(resources.getDimensionPixelSize(R.dimen.padding_medium))
            adapter = historyAdapter
            addItemDecoration(
                DividerItemDecoration(
                    this@HistoryActivity,
                    linearLayoutManager.orientation
                )
            )
        }
    }

    private fun initializeObservers() {
        lifecycleScope.launch {
            viewModel.detailedActivities.collectLatest { pagingData ->
                historyAdapter.submitData(pagingData)
            }
        }
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, HistoryActivity::class.java)
    }
}