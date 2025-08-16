import { getArticleApi, getYoutubeVideosApi } from '../api/techCrunchApi';
import { Article } from '../types/techCrunch/response';
import { getAxiosErrorMessage } from '../utils/errorUtils';
import { stripHtml, formatDate } from '../utils/formatUtils';
import { YoutubeVideo } from '../types/techCrunch/response';
import { filterVideos } from '../utils/filterUtils';
import he from 'he';

export const fetchArticle = async (): Promise<Article[]> => {
  try {
    const response = await getArticleApi();

    const articles: Article[] = response.map((article: any) => ({
      id: article.id,
      title: he.decode(stripHtml(article.title.rendered)),
      summary: he.decode(stripHtml(article.excerpt.rendered)),
      link: article.link,
      date: formatDate(article.date),
    }));

    return articles;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get article details from techcrunch'));
  }
};

export const fetchYoutubeVideos = async (): Promise<YoutubeVideo[]> => {
  try {
    const response = await getYoutubeVideosApi();
    const data = await response.json();

    // Use the filter util
    const filteredVideos = filterVideos(data.items);

    // Return top 10 videos
    return filteredVideos.slice(0, 10);
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get YouTube videos'));
  }
};
