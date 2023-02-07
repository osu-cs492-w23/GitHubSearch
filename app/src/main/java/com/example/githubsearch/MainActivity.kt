package com.example.githubsearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubsearch.api.GitHubService
import com.example.githubsearch.data.GitHubRepo
import com.example.githubsearch.data.GitHubSearchResults
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchBoxET: EditText = findViewById(R.id.et_search_box)
        val searchBtn: Button = findViewById(R.id.btn_search)

        val searchResultsListRV: RecyclerView = findViewById(R.id.rv_search_results)
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
        gitHubService.searchRepositories(query)
            .enqueue(object : Callback<GitHubSearchResults> {
                override fun onResponse(call: Call<GitHubSearchResults>, response: Response<GitHubSearchResults>) {
                    Log.d("MainActivity", "Status code: ${response.code()}")
                    if (response.isSuccessful) {
//                        val moshi = Moshi.Builder().build()
//                        val jsonAdapter = moshi.adapter(GitHubSearchResults::class.java)
//                        val searchResults = jsonAdapter.fromJson(response.body())
//                        Log.d("MainActivity", "Search results: $searchResults")
                        githubRepoAdapter.updateRepoList(response.body()?.items)
                    }
                }

                override fun onFailure(call: Call<GitHubSearchResults>, t: Throwable) {
                    Log.d("MainActivity", "Error making API call: ${t.message}")
                }
            })
    }
}