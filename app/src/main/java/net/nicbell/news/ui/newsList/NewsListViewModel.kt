package net.nicbell.news.ui.newsList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.nicbell.news.api.NewsApi
import net.nicbell.news.api.Paging
import net.nicbell.news.api.news.NewsArticle
import net.nicbell.news.viewModel.Event
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.HttpException
import java.io.IOException

/**
 * News list view model.
 */
class NewsListViewModel : ViewModel(), KoinComponent {
    private val articlePageSize = 99

    private val newApi: NewsApi by inject()


    // Live data
    private val _newsArticles = MutableLiveData<Paging<NewsArticle>>()
    private val _error = MutableLiveData<Event<String>>()
    private val _openArticle = MutableLiveData<Event<NewsArticle>>()

    val newsArticles: LiveData<Paging<NewsArticle>>
        get() = _newsArticles

    val error: LiveData<Event<String>>
        get() = _error

    val openArticle: LiveData<Event<NewsArticle>>
        get() = _openArticle


    //region UI events

    fun onOpenArticleClick(item: NewsArticle) {
        _openArticle.value = Event(item)
    }

    //endregion


    /**
     * Load news:
     * Getting a fixed list of the latest articles, in the real-world we would not call
     * the API directly from the ViewModel we would instead setup a PageKeyedDataSource
     * which would use the API to load pages of articles, so the user can scroll articles
     * infinitely.
     */
    fun loadNews() {
        handleApiCallErrors {
            val news = newApi.getNews(articlePageSize, 0)
            _newsArticles.postValue(news)
        }
    }

    /**
     * Handle API call errors:
     * Do your API call in the body and errors will be catch
     * and handled.
     */
    private fun <T> handleApiCallErrors(body: suspend () -> T) {
        viewModelScope.launch {
            try {
                body.invoke()
            } catch (ex: HttpException) {
                _error.postValue(Event(("${ex.code()} - ${ex.message()}")))
            } catch (ex: IOException) {
                _error.postValue(Event(ex.message ?: "Unknown IOException"))
            }
        }
    }
}