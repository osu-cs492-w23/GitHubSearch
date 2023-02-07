package com.example.githubsearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubsearch.api.GitHubService
import com.example.githubsearch.data.GitHubRepo
import com.example.githubsearch.data.GitHubSearchResults
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.squareup.moshi.Moshi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



/*
 * {
 *   "name": "Hobbes",
 *   "type": "tiger",
 *   "age": 8,
 *   "favoriteFood": "tuna"
 * }
 */
//data class Cat(
//    val name: String,
//    val type: String,
//    val age: Int,
//    val favoriteFood: String
//)

/*
 * {
 *   "name": "Hobbes",
 *   "bestFriend": {
 *     "name": "Calvin",
 *     "age": 5,
 *     "alterEgos": ["Spaceman Spiff", "Tracer Bullet", "Stupendous Man"]
 *   }
 */
data class Person(
    val name: String,
    val age: Int,
    val alterEgos: List<String>
)

data class Cat(
    val name: String,
    val bestFriend: Person
)

class MainActivity : AppCompatActivity() {
    private val gitHubService = GitHubService.create()
    private val githubRepoAdapter = GitHubRepoListAdapter()

    private lateinit var searchResultsListRV: RecyclerView
    private lateinit var errorMessageTV: TextView
    private lateinit var loadingIndicator: CircularProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchBoxET: EditText = findViewById(R.id.et_search_box)
        val searchBtn: Button = findViewById(R.id.btn_search)

        errorMessageTV = findViewById(R.id.tv_error_message)
        loadingIndicator = findViewById(R.id.loading_indicator)

        searchResultsListRV = findViewById(R.id.rv_search_results)
        searchResultsListRV.layoutManager = LinearLayoutManager(this)
        searchResultsListRV.setHasFixedSize(true)
        searchResultsListRV.adapter = githubRepoAdapter

        searchBtn.setOnClickListener {
            val query = searchBoxET.text.toString()
            if (!TextUtils.isEmpty(query)) {
                doRepoSearch(query)
//                adapter.updateRepoList(dummySearchResults)
                searchResultsListRV.scrollToPosition(0)
            }
        }
    }

    private fun doRepoSearch(query: String) {
        loadingIndicator.visibility = View.VISIBLE
        gitHubService.searchRepositories(query)
            .enqueue(object : Callback<GitHubSearchResults> {
                override fun onResponse(call: Call<GitHubSearchResults>, response: Response<GitHubSearchResults>) {
                    loadingIndicator.visibility = View.INVISIBLE

                    Log.d("MainActivity", "Status code: ${response.code()}")
                    if (response.isSuccessful) {
//                        val moshi = Moshi.Builder().build()
//                        val jsonAdapter = moshi.adapter(GitHubSearchResults::class.java)
//                        val searchResults = jsonAdapter.fromJson(response.body())
//                        Log.d("MainActivity", "Search results: $searchResults")
                        githubRepoAdapter.updateRepoList(response.body()?.items)
                        searchResultsListRV.visibility = View.VISIBLE
                        errorMessageTV.visibility = View.INVISIBLE
                    } else {
                        errorMessageTV.text = "Error: ${response.errorBody()?.string()}"
                        searchResultsListRV.visibility = View.INVISIBLE
                        errorMessageTV.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<GitHubSearchResults>, t: Throwable) {
                    errorMessageTV.text = "Error: ${t.message}"
                    loadingIndicator.visibility = View.INVISIBLE
                    searchResultsListRV.visibility = View.INVISIBLE
                    errorMessageTV.visibility = View.VISIBLE
                    Log.d("MainActivity", "Error making API call: ${t.message}")
                }
            })
    }
}