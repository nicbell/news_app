package net.nicbell.news.ui.newsList

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import net.nicbell.news.BR
import net.nicbell.news.R
import net.nicbell.news.adapters.ListAdapter
import net.nicbell.news.api.news.NewsArticle
import net.nicbell.news.databinding.FragmentNewsListBinding
import net.nicbell.news.databinding.ListNewsBinding
import net.nicbell.news.ui.FragmentExtensions.observe
import net.nicbell.news.ui.FragmentExtensions.observeEvent
import net.nicbell.news.ui.MainActivity


class NewsListFragment : Fragment() {
    private val viewModel: NewsListViewModel by activityViewModels()

    private lateinit var binding: FragmentNewsListBinding
    private lateinit var newsAdapter: ListAdapter<List<NewsArticle>>

    private val newArticleGroupSize = 3

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewsListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        // Toolbar
        (activity as MainActivity).setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)

        // View model
        binding.viewModel = viewModel
        observeViewModel()

        initRecycler()

        // Swipe to refresh
        binding.layRefresh.setColorSchemeResources(
            R.color.design_default_color_secondary,
            R.color.design_default_color_primary
        )
        binding.layRefresh.setOnRefreshListener { viewModel.loadNews() }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if (!isHidden) {
            binding.layRefresh.isRefreshing = true
            viewModel.loadNews()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> {
                binding.layRefresh.isRefreshing = true
                viewModel.loadNews()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Observe changes in view model.
     */
    private fun observeViewModel() {
        // Error event
        observeEvent(viewModel.error) { error ->
            binding.layRefresh.isRefreshing = false
            error?.run { showError(this) }
        }

        // Open article event
        observeEvent(viewModel.openArticle) { article ->
            article?.run {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(article.contentURL)))
            }
        }

        // We chunk the articles into a list of lists because that's what th UI looks like.
        observe(viewModel.newsArticles) {
            binding.layRefresh.isRefreshing = false
            it?.let { pageOfNews -> newsAdapter.update(pageOfNews.content.chunked(newArticleGroupSize)) }
        }
    }

    /**
     * Really basic error feedback, should be replaced with friendly copy, made generic
     * and not be in this class so errors are show the same throughout app.
     */
    private fun showError(message: String) {
        val snackbar = Snackbar.make(binding.layCoordinator, message, Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction(R.string.btn_retry) {
            snackbar.dismiss()
            binding.layRefresh.isRefreshing = true
            viewModel.loadNews()
        }

        snackbar.show()
    }

    /**
     * Initialize recycler which has an item layout with another recycler inside it taking
     * @see newArticleGroupSize articles, these recyclers alternate orientation.
     */
    private fun initRecycler() {
        newsAdapter = ListAdapter(R.layout.list_news, newsChunkItemBinder)

        binding.recyclerNews.setHasFixedSize(true)
        binding.recyclerNews.adapter = newsAdapter
    }

    /**
     * Data binder for individual article
     */
    private val newsArticleBinder = object : ListAdapter.ItemBinder<NewsArticle> {
        override fun bindItem(binding: ViewDataBinding, item: NewsArticle, position: Int) {
            binding.setVariable(BR.newsArticle, item)
            binding.setVariable(BR.onClick, View.OnClickListener { viewModel.onOpenArticleClick(item) })
        }
    }

    /**
     * Data binder for a chunk or articles, uses recycler position to determine
     * whether the chunk should be vertical or horizontal. Maybe a bit nasty.
     * Could have used a view pager for horizontal chunk but decided to reuse the recycler
     * with a LinearSnapHelper.
     */
    private val newsChunkItemBinder = object : ListAdapter.ItemBinder<List<NewsArticle>> {
        override fun bindItem(binding: ViewDataBinding, item: List<NewsArticle>, position: Int) {
            if (binding is ListNewsBinding) {
                val recycler = binding.recyclerNews
                val layoutId: Int
                val horizontalChunkPadding = resources.getDimensionPixelSize(R.dimen.space_2)

                when (position % 2 == 1) {
                    true -> {
                        recycler.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                        recycler.onFlingListener = null
                        LinearSnapHelper().attachToRecyclerView(recycler)
                        recycler.setPadding(horizontalChunkPadding, 0, horizontalChunkPadding, 0)
                        layoutId = R.layout.list_item_news_horizontal
                    }
                    false -> {
                        recycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                        recycler.setPadding(0, 0, 0, 0)
                        layoutId = R.layout.list_item_news
                    }
                }

                val productsAdapter = ListAdapter(layoutId, newsArticleBinder)
                recycler.setHasFixedSize(true)
                recycler.adapter = productsAdapter

                productsAdapter.update(item)
            }
        }
    }
}