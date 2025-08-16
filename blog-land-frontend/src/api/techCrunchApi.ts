export const getArticleApi = async () => {
  const response = await fetch(
    'https://techcrunch.com/wp-json/wp/v2/posts?per_page=3&page=11&_embed'
  );
  const posts = await response.json();
  return posts;
};

const API_KEY = import.meta.env.VITE_YOUTUBE_API_KEY;
const API_URL = `https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=50&order=date&q=news&type=video&key=${API_KEY}`;

export const getYoutubeVideosApi = async () => {
  const response = await fetch(API_URL);
  return response;
};
