import { Article } from '../components/ui/article/Article';
import { VideoSection } from '../components/ui/videoSection/Video';
import { TrendingSection } from '../components/ui/trending/trending';
import QandASection from '../components/ui/QandA/QandA';

function HomePage() {
  return (
    <>
      <VideoSection />
      <TrendingSection />
      <Article />
      <QandASection />
    </>
  );
}

export default HomePage;
