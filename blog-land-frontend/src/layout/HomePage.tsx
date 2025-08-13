import { Article } from '../components/ui/article/Article';
import { LatestSection } from '../components/ui/latest/latestSection';
import { VideoSection } from '../components/ui/videoSection/Video';
import { TrendingSection } from '../components/ui/trending/Trending';
import QandASection from '../components/ui/QandA/QandA';

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
