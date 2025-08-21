import styles from './Hero.module.css';

export const Hero = () => {
  return (
    <div className="container">
      <div className={styles.hero}>
        <div className={styles.column1}>
          <h2>We like to blog, you like to blog, so let's blog!</h2>
          <p>
            Welcome to <strong>BlogLand</strong> — a place where writers share ideas, stories, and
            knowledge. Whether you’re here to read, write, or just explore, BlogLand gives you the
            freedom to connect with a community of bloggers worldwide.
          </p>
          <div className={styles.actions}>
            <a className={styles.primaryBtn}>Start Writing</a>
            <a className={styles.secondaryBtn}>Explore Blogs</a>
          </div>
        </div>
        <div className={styles.column2}>
          {/* You can add an image, illustration, or animation here */}
          <img src="" alt="People blogging illustration" className={styles.heroImage} />
        </div>
      </div>
    </div>
  );
};
