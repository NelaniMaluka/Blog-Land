import ShuffleHero from '../components/ui/HomePage/hero/LatestCta';
import { Article } from '../components/ui/HomePage/article/Article';
import { LatestSection } from '../components/ui/HomePage/latest/LatestSection';
import { TrendingSection } from '../components/ui/HomePage/trending/Trending';
import QandASection from '../components/ui/HomePage/QandA/QandA';

function HomePage() {
  return (
    <>
      <LatestSection />
      <ShuffleHero />
      <TrendingSection />
      <Article />
      <QandASection />
    </>
  );
}

export default HomePage;
