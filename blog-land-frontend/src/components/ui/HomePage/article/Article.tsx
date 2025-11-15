import LoadingScreen from '../../../../features/LoadingScreen/LoadingScreen';
import { useGetLatestPosts } from '../../../../hooks/usePost';
import styles from './Article.module.css';

export const Article = () => {
  const { data, isLoading, error } = useGetLatestPosts({ page: 30, size: 3 });

  if (error) return <></>;

  return (
    <section className={styles.articleContainers}>
      <LoadingScreen isLoading={isLoading}>
        <div className="container">
          <div className={styles.row1}>
            <h2>News</h2>
          </div>

          <article className={styles.row2}>
            {data?.length
              ? data.map((article, index) => (
                  <div key={index} className={styles.article}>
                    <img src="techC.png" alt="Logo" />
                    <div>
                      <a href="https://techcrunch.com/" target="_blank" rel="noopener noreferrer">
                        {article.title}
                      </a>
                    </div>
                    <span className={styles.date}>{article.createdAt}</span>
                    <p>{article.summary}</p>
                  </div>
                ))
              : null}
          </article>
        </div>
      </LoadingScreen>
    </section>
  );
};
