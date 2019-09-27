package net.nicbell.news.api.news

data class NewsArticle(
    val contentId: String,
    val title: String,
    val summary: String,
    val contentSource: String,
    val contentSourceDisplay: String,
    val contentSourceLogo: String,
    val categories: List<String>,
    val categoriesEnglish: List<String>,
    val verticals: List<String>,
    val verticalsEnglish: List<String>,
    val images: NewsArticleImages,
    val countries: List<String>,
    val locale: String,
    val contentURL: String
)