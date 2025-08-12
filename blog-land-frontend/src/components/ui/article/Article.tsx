import { useGetArticles } from '../../../hooks/useTechCrunch';
import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';

import styles from './Article.module.css';
import techC from '../../../assets/techC.png';

export const Article = () => {
  const { data, isLoading, error } = useGetArticles();

  if (error) {
    return <></>;
  }

  const FormattedData = () => {
    return (
      <div className={styles.row2}>
        {data?.length
          ? data.map((article, index) => (
              <div key={index} className={styles.article}>
                <img src={techC} alt="Logo" />
                <div>
                  <a href={article.link} target="_blank">
                    {article.title}
                  </a>
                </div>
                <span className={styles.date}>{article.date}</span>
                <p>{article.summary}</p>
              </div>
            ))
          : null}
      </div>
    );
  };

  return (
    <div className={styles.articleContainers}>
      <LoadingScreen isLoading={isLoading}>
        <div className="container">
          <div className={styles.row1}>
            <h2>News</h2>
          </div>
          <FormattedData />
        </div>
      </LoadingScreen>
    </div>
  );
};
