import {
  useGetCommentsCountByPost,
  useGetCommentsWithPostId,
  useAddComment,
  useDeleteComment,
  useUpdateComment,
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
import CommentLayout from './commentLayout';

import styles from './comments.module.css';

interface CommentsProps {
  postId: number;
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
  const [editContent, setEditContent] = useState('');
  const [showEmojiPicker, setShowEmojiPicker] = useState(false);
  const pickerRef = useRef<HTMLDivElement>(null);
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [menuCommentId, setMenuCommentId] = useState<number | null>(null);
  const [readyPostId, setReadyPostId] = useState<number | null>(null);
  const open = Boolean(anchorEl);

  // Auth data
  const user = useSelector((state: RootState) => state.auth.user);
  const isAuthenticated = useSelector((state: RootState) => state.auth.isAuthenticated);

  // Mutations
  const addComment = useAddComment();
  const updateComment = useUpdateComment();
  const deleteComment = useDeleteComment();

  // Checks if post id is here before running
  useEffect(() => {
    if (postId) {
      setReadyPostId(postId);
    }
  }, [postId]);

  // Fetching post data
  const { data: postCount } = useGetCommentsCountByPost(readyPostId ?? 0);
  const { data: comments } = useGetCommentsWithPostId({
    postId: readyPostId ?? 0,
    page: 0,
    size: 20,
  });
  const { data: userComments = [] } = useGetUserCommentsWithPostId(readyPostId ?? 0, {
    enabled: isAuthenticated && !!readyPostId,
  });

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

  const handleClick = (event: React.MouseEvent<HTMLElement>, commentId: number) => {
    setAnchorEl(event.currentTarget);
    setMenuCommentId(commentId);
  };

  const handleCloseMenu = () => {
    setAnchorEl(null);
    setMenuCommentId(null);
  };

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

  const handleUpdateComment = (id: number) => {
    updateComment.mutateAsync({ id: id, content: editContent, postId: postId! });
  };

  const handleEmojiSelect = (emoji: EmojiClickData) => {
    setIsComment((prev) => prev + emoji.native);
    setShowEmojiPicker(false); // close after selecting
  };

  const handleDeleteComment = (id: number) => {
    deleteComment.mutateAsync(id);
  };

  return (
    <section className={styles.commentSection}>
      <h5>{postCount} Comments:</h5>

      {/* Add comment form */}
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
          const isOwner = !!userComments?.find((uc) => uc.id === comment.id);

          return (
            <CommentLayout
              key={comment.id}
              postId={postId}
              comment={comment}
              isOwner={!!userComments?.find((uc) => uc.id === comment.id)}
              isAuthenticated={isAuthenticated}
              onMenuClick={(e: any) => handleClick(e, comment.id)}
              onDelete={() => handleDeleteComment(comment.id)}
              menuOpen={open && menuCommentId === comment.id}
              anchorEl={anchorEl}
              onCloseMenu={handleCloseMenu}
            />
          );
        })
      ) : (
        <p>No comments yet.</p>
      )}
    </section>
  );
};

export default Comments;
