import { Article } from '../components/ui/HomePage/article/Article';
import { LatestSection } from '../components/ui/HomePage/latest/LatestSection';
import { VideoSection } from '../components/ui/HomePage/videoSection/Video';
import { TrendingSection } from '../components/ui/HomePage/trending/Trending';
import QandASection from '../components/ui/HomePage/QandA/QandA';

function HomePage() {
  return (
    <>
      <LatestSection />
      <VideoSection />
      <TrendingSection />
      <Article />
      <QandASection />
    </>
  );
}

export default HomePage;
