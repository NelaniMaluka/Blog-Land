export const getArticleApi = async () => {
  const response = await fetch(
    'https://techcrunch.com/wp-json/wp/v2/posts?per_page=3&page=11&_embed'
  );
  const posts = await response.json();
  return posts;
};
