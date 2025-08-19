import { useGetArticles } from '../../../../hooks/useTechCrunch';
import LoadingScreen from '../../../../features/LoadingScreen/LoadingScreen';
import styles from './Article.module.css';

export const Article = () => {
  const { data, isLoading, error } = useGetArticles();

  if (error) return <></>;

  return (
    <div className={styles.articleContainers}>
      <LoadingScreen isLoading={isLoading}>
        <div className="container">
          <div className={styles.row1}>
            <h2>News</h2>
          </div>

          <div className={styles.row2}>
            {data?.length
              ? data.map((article, index) => (
                  <div key={index} className={styles.article}>
                    <img src="techC.png" alt="Logo" />
                    <div>
                      <a href={article.link} target="_blank" rel="noopener noreferrer">
                        {article.title}
                      </a>
                    </div>
                    <span className={styles.date}>{article.date}</span>
                    <p>{article.summary}</p>
                  </div>
                ))
              : null}
          </div>
        </div>
      </LoadingScreen>
    </div>
  );
};
