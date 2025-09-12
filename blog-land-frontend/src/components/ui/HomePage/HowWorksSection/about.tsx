import { FaGaugeHigh, FaUserShield, FaCookieBite, FaLock, FaComments } from 'react-icons/fa6';
import styles from './about.module.css';

const ABOUT_DATA = [
  {
    img: '/icons/limit.png',
    icon: <FaGaugeHigh />,
    title: 'Write Without Limits',
    description:
      'Enjoy a smooth, lightning-fast platform that lets you focus on your creativity, not technical hiccups.',
  },
  {
    img: '/icons/privacy.png',
    icon: <FaUserShield />,
    title: 'Your Words, Your Privacy',
    description:
      'We protect your identity and your content. No ads, no trackers — just a safe space for your ideas.',
  },
  {
    img: '/icons/community.png',
    icon: <FaCookieBite />,
    title: 'Community First',
    description:
      'Blog Land is built for writers and readers. Share your stories, connect with others, and be part of a supportive network.',
  },
  {
    img: '/icons/secure.png',
    icon: <FaLock />,
    title: 'Trusted and Secure',
    description:
      'Your work is valuable. We keep it safe, so you can write with confidence every time you log in.',
  },
  {
    img: '/icons/communication.png',
    icon: <FaComments />,
    title: 'Connect Anytime',
    description:
      'Engage with a passionate community, get feedback, and collaborate with fellow writers around the clock.',
  },
];

export default function AboutSection() {
  return (
    <section className={styles.wrapper}>
      <div className="container">
        <h2 className={styles.title}>About Blog Land</h2>
        <p className={styles.description}>
          Blog Land is a community-driven platform where writers, readers, and creators connect.
          Discover trending posts, share your thoughts, and be part of an ever-growing community.
        </p>

        <div className={styles.grid}>
          {ABOUT_DATA.map((item, idx) => (
            <div key={idx} className={styles.card}>
              <img src={item.img} alt="" className={styles.icon} />
              <h5 className={styles.title}>{item.title}</h5>
              <p className={styles.description}>{item.description}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
