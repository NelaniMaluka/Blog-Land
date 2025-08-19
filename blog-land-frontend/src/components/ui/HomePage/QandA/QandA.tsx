import * as React from 'react';
import Accordion from '@mui/material/Accordion';
import AccordionActions from '@mui/material/AccordionActions';
import AccordionSummary from '@mui/material/AccordionSummary';
import AccordionDetails from '@mui/material/AccordionDetails';
import Typography from '@mui/material/Typography';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import Button from '@mui/material/Button';

import styles from './QandA.module.css';

export default function QandASection() {
  return (
    <div className={styles.QandAContainer}>
      <div className="container">
        <div className={styles.row1}>
          <div>
            <h2>Have a question?</h2>
            <h2>We are here to answer.</h2>
          </div>
          <div>
            <p>
              Whether it’s about our products, services, or anything else, feel free to ask. Our
              team is ready to help you find the answers you need.
            </p>
          </div>
        </div>

        <Accordion className={styles.QandA}>
          <AccordionSummary
            expandIcon={<ExpandMoreIcon />}
            aria-controls="panel1-content"
            id="panel1-header"
          >
            <Typography component="span" className={styles.question}>
              How do I create a new blog post?
            </Typography>
          </AccordionSummary>
          <AccordionDetails className={styles.text}>
            To create a new blog post, simply log in to your account, navigate to the dashboard, and
            click the “New Post” button. Fill in your title, content, and tags, then hit publish
            when ready.
          </AccordionDetails>
        </Accordion>

        <Accordion className={styles.QandA}>
          <AccordionSummary
            expandIcon={<ExpandMoreIcon />}
            aria-controls="panel2-content"
            id="panel2-header"
          >
            <Typography component="span" className={styles.question}>
              Can I schedule posts to publish later?
            </Typography>
          </AccordionSummary>
          <AccordionDetails className={styles.text}>
            Yes! When creating or editing a post, you can select a future date and time for
            publishing. This way, your post will go live automatically at the scheduled time.
          </AccordionDetails>
        </Accordion>

        <Accordion className={styles.QandA}>
          <AccordionSummary
            expandIcon={<ExpandMoreIcon />}
            aria-controls="panel3-content"
            id="panel3-header"
          >
            <Typography component="span" className={styles.question}>
              How do I customize the look of my blog?
            </Typography>
          </AccordionSummary>
          <AccordionDetails className={styles.text}>
            You can customize your blog’s appearance by visiting the “Themes” section in your
            dashboard. Choose from various themes and adjust colors, fonts, and layouts to match
            your style.
          </AccordionDetails>
        </Accordion>
      </div>
    </div>
  );
}
