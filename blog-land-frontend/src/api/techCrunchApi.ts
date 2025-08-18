export const getArticleApi = async () => {
  const response = await fetch(
    'https://techcrunch.com/wp-json/wp/v2/posts?per_page=3&page=11&_embed'
  );
  const posts = await response.json();
  return posts;
};

const API_KEY = import.meta.env.VITE_YOUTUBE_API_KEY;

export const getYoutubeVideosApi = async (): Promise<Response> => {
  const MAX_RESULTS = 50; // Fetch more to ensure enough long videos
  const API_URL = `https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=UCOmcA3f_RrH6b9NmcNa4tdg&maxResults=${MAX_RESULTS}&type=video&videoDuration=any&order=date&key=${API_KEY}`;

  const response = await fetch(API_URL);
  return response;
};
