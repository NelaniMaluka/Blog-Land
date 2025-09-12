import styles from './Hero.module.css';

export const Hero = () => {
  return (
    <section className="container">
      <div className={styles.hero}>
        <div className={styles.column1}>
          <h2>Da Vinci Had Paint, You Have Blogs</h2>
          <p>
            Every masterpiece starts with expression. Blog Land is your digital canvas — a place to
            share ideas, stories, and perspectives that stand the test of time. Whether you’re
            crafting insights, sharing your passions, or painting the world with words, this is
            where your voice becomes art.
          </p>
        </div>
      </div>
    </section>
  );
};
