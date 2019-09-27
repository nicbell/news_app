package net.nicbell.news.api.news

data class NewsArticleImages(
    val mainImage: Image,
    val mainImageThumbnail: Image,
    val additionalImages: List<Image>
)