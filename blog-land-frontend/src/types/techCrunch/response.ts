export interface Article {
  id: number;
  title: string;
  summary: string;
  link: string;
  date: string;
}

export interface YoutubeVideo {
  id: {
    videoId: string;
  };
  snippet: {
    title: string;
    description: string;
    thumbnails: {
      medium: { url: string };
      high: { url: string };
    };
    publishedAt: string;
    channelTitle: string;
  };
}
