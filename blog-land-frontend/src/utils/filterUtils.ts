import { YoutubeVideo } from '../types/techCrunch/response';
import he from 'he';

// Check if title is English
export const isEnglish = (text: string) => /^[\x00-\x7F]*$/.test(text);

// Optional: trusted channels list
const trustedChannels = [
  // Global
  'BBC News',
  'CNN',
  'Reuters',
  'Associated Press',
  'Sky News',
  'Al Jazeera English',
  'Euronews',
  'Bloomberg',
  'The Guardian',
  'Financial Times',

  // North America
  'ABC News',
  'NBC News',
  'CBS News',
  'Fox News',
  'MSNBC',
  'CBC News',
  'CTV News',

  // Europe
  'DW News',
  'France 24 English',
  'RTÉ News',
  'ITV News',
  'Channel 4 News',

  // Africa
  'SABC News',
  'eNCA',
  'Africa News',
  'Channels Television',
  'NTV Kenya',
  'Citizen TV Kenya',

  // Asia-Pacific
  'NDTV',
  'WION',
  'The Times of India',
  'CNA',
  'ABC News Australia',
  '7NEWS Australia',
  'NHK World-Japan',

  // Middle East
  'TRT World',
  'Arirang News',
  'i24NEWS English',
  'Middle East Eye',

  // Latin America
  'TeleSUR English',
  'NTN24',
  'TV Globo',
  'Univision Noticias',
  'Telemundo',
];

// Filter function
export const filterVideos = (videos: YoutubeVideo[]): YoutubeVideo[] => {
  return (videos || [])
    .filter((video) => isEnglish(he.decode(video.snippet.title))) // English titles
    .filter((video) => trustedChannels.includes(video.snippet.channelTitle)) // professional channels
    .filter((video) => video.snippet.thumbnails?.high?.url) // high-quality thumbnails
    .map((video) => ({
      ...video,
      snippet: {
        ...video.snippet,
        title: he.decode(video.snippet.title), // decode title for display
      },
    }));
};
