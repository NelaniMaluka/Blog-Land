import { Article } from '../components/ui/article/Article';
import { VideoSection } from '../components/ui/videoSection/Video';
import QandASection from '../components/ui/QandA/QandA';

function HomePage() {
  return (
    <>
      <VideoSection />
      <Article />
      <QandASection />
    </>
  );
}

export default HomePage;
