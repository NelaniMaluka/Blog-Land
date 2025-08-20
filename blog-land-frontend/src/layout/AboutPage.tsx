import styles from '../components/common/LegalPages.module.css';

export const AboutPage = () => {
  return (
    <div className="container">
      <div className={styles.LegalPages}>
        <h1 className={styles.title}>About Blog Land</h1>
        <p className={styles.text}>
          Welcome to <strong>Blog Land</strong>, your digital space to share ideas, stories, and
          insights. Our platform is designed to bring writers, readers, and creators together into
          one community-driven hub where everyone has a voice.
        </p>

        <p className={styles.text}>
          Whether you’re an aspiring blogger or an experienced writer, Blog Land provides the tools
          and audience to grow your passion for storytelling. Our mission is to make publishing
          simple, engaging, and rewarding.
        </p>

        <h2 className={styles.subtitle}>Our Mission</h2>
        <p className={styles.text}>
          At Blog Land, we believe that every story matters. We aim to empower individuals to
          express themselves and connect with others through the written word. From casual posts to
          professional insights, every blog contributes to a larger conversation.
        </p>

        <h2 className={styles.subtitle}>Why Blog Land?</h2>
        <ul className={styles.list}>
          <li> Easy-to-use publishing tools</li>
          <li> A growing community of readers & writers</li>
          <li>Personalization to fit your writing style</li>
          <li>Commitment to privacy and content security</li>
        </ul>

        <p className={styles.text}>
          Thank you for being a part of Blog Land. Together, let’s build a platform where creativity
          thrives and every voice can be heard.
        </p>
      </div>
    </div>
  );
};
