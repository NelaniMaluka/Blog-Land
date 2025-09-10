import {
  useGetCommentsCountByPost,
  useGetCommentsWithPostId,
  useAddComment,
} from '../../hooks/useComment';
import { Avatar } from '@mui/material';
import FallbackAvatars from '../common/Avatar';
import { useSelector } from 'react-redux';
import { RootState } from '../../store/store';
import { FormEvent, useState, useRef } from 'react';
import { useDialog } from '../../features/LoginProvider/LoginProvider';
import Picker from '@emoji-mart/react';
import data from '@emoji-mart/data';
import { useEffect } from 'react';
import { useGetUserCommentsWithPostId } from '../../hooks/useComment';
import IconButton from '@mui/material/IconButton';

import styles from './comments.module.css';

interface CommentsProps {
  postId?: number;
}

interface EmojiClickData {
  id: string;
  name: string;
  native: string;
  colons: string;
  emoticons: string[];
  keywords: string[];
  skins?: any[];
}

const Comments = ({ postId }: CommentsProps) => {
  const { showLogin } = useDialog();
  const [isComment, setIsComment] = useState('');
  const [showEmojiPicker, setShowEmojiPicker] = useState(false);
  const pickerRef = useRef<HTMLDivElement>(null);

  const user = useSelector((state: RootState) => state.auth.user);
  const isAuthenticated = useSelector((state: RootState) => state.auth.isAuthenticated);
  const addComment = useAddComment();

  const [readyPostId, setReadyPostId] = useState<number | null>(null);

  useEffect(() => {
    if (postId) {
      setReadyPostId(postId);
    }
  }, [postId]);

  const { data: postCount } = useGetCommentsCountByPost(readyPostId ?? 0);
  const { data: comments } = useGetCommentsWithPostId({
    postId: readyPostId ?? 0,
    page: 0,
    size: 20,
  });

  const { data: userComments = [] } = useGetUserCommentsWithPostId(readyPostId ?? 0, {
    enabled: isAuthenticated && !!readyPostId,
  });

  const handleAddComment = (e: FormEvent) => {
    e.preventDefault();
    if (!isAuthenticated) {
      showLogin();
      return;
    }
    try {
      addComment.mutateAsync({ postId: postId!, content: isComment });
      setIsComment('');
    } catch {}
  };

  const handleEmojiSelect = (emoji: EmojiClickData) => {
    setIsComment((prev) => prev + emoji.native);
    setShowEmojiPicker(false); // close after selecting
  };

  // Close emoji picker on outside click
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (pickerRef.current && !pickerRef.current.contains(event.target as Node)) {
        setShowEmojiPicker(false);
      }
    };

    if (showEmojiPicker) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [showEmojiPicker]);

  return (
    <section className={styles.commentSection}>
      <h5>{postCount} Comments:</h5>
      <form className={styles.inputRow} onSubmit={handleAddComment}>
        <div className={styles.col1}>
          {isAuthenticated ? (
            <FallbackAvatars user={user} />
          ) : (
            <Avatar src="/broken-image.jpg" className={styles.avatarButton} />
          )}
        </div>
        <div className={styles.col2}>
          <input
            placeholder="Write a comment..."
            value={isComment}
            onChange={(e) => setIsComment(e.target.value)}
          />

          <div>
            {/* Emoji Toggle Button */}
            <IconButton>
              <img
                src="/icons/smile.png"
                alt="emoji-icon"
                onClick={() => setShowEmojiPicker((prev) => !prev)}
              />
            </IconButton>

            {/* Emoji Picker */}
            {showEmojiPicker && (
              <div ref={pickerRef} className={styles.emojiCont}>
                <Picker data={data} onEmojiSelect={handleEmojiSelect} />
              </div>
            )}
            <button type="submit" className={styles.button}>
              Comment
            </button>
          </div>
        </div>
      </form>

      {/* Comments List */}
      {comments && comments.length > 0 ? (
        comments.map((comment) => {
          const match = userComments?.find((userComment) => userComment.id === comment.id);

          return (
            <div className={styles.comment} key={comment.id}>
              <div className={styles.col1}>
                <FallbackAvatars user={comment.user} />
              </div>
              <div className={styles.col2}>
                <div className={styles.text}>
                  <div className={styles.subtext}>
                    <p className={styles.username}>
                      {comment.user.firstname + ' ' + comment.user.lastname}
                    </p>
                    <p className={styles.date}>{comment.createdAt}</p>
                  </div>
                  <div className={styles.commentText}>
                    <p>{comment.content} </p>
                  </div>
                </div>
                {match && isAuthenticated && (
                  <IconButton sx={{ height: 'max-content', width: 'max-content' }}>
                    <img src="/icons/menu-v.png" alt="menu" />
                  </IconButton>
                )}
              </div>
            </div>
          );
        })
      ) : (
        <p>No comments yet.</p>
      )}
    </section>
  );
};

export default Comments;
