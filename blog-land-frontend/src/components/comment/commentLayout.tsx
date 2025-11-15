import { CommentResponse } from '../../types/comment/response';
import styles from './comments.module.css';
import FallbackAvatars from '../common/Avatar';
import IconButton from '@mui/material/IconButton';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import { useGetPublicUser } from '../../hooks/useUser';
import { useState } from 'react';
import { useUpdateComment } from '../../hooks/useComment';

interface CommentLayoutProps {
  postId: string;
  comment: CommentResponse;
  isOwner: boolean;
  isAuthenticated: boolean;
  onMenuClick: (e: React.MouseEvent<HTMLElement>) => void;
  onDelete: () => void;
  menuOpen: boolean;
  anchorEl: HTMLElement | null;
  onCloseMenu: () => void;
}

const CommentLayout: React.FC<CommentLayoutProps> = ({
  postId,
  comment,
  isOwner,
  isAuthenticated,
  onMenuClick,
  onDelete,
  menuOpen,
  anchorEl,
  onCloseMenu,
}) => {
  const { data: user } = useGetPublicUser(comment.userId);

  // Local edit state
  const [isEditing, setIsEditing] = useState(false);
  const [editContent, setEditContent] = useState(comment.content);

  const updateComment = useUpdateComment(); // mutation inside the layout

  const handleSaveEdit = () => {
    updateComment.mutateAsync({
      postId,
      commentId: comment.id,
      data: {
        content: editContent,
      },
    });

    setIsEditing(false);
  };

  const handleCancelEdit = () => {
    setIsEditing(false);
    setEditContent(comment.content);
  };

  return (
    <div className={styles.comment} key={comment.id}>
      {/* User info */}
      <div className={styles.col1}>
        <FallbackAvatars user={user ?? null} />
      </div>

      <div className={styles.col2}>
        <div className={styles.text}>
          <div className={styles.subtext}>
            <p className={styles.username}>{user?.firstname + ' ' + user?.lastname}</p>
            <p className={styles.date}>{comment.createdAt}</p>
          </div>

          <div className={styles.commentText}>
            {isEditing ? (
              <form
                onSubmit={(e) => {
                  e.preventDefault();
                  handleSaveEdit();
                }}
              >
                <input
                  value={editContent}
                  onChange={(e) => setEditContent(e.target.value)}
                  className={styles.editInput}
                  aria-label="Edit your comment"
                />
                <div>
                  <button type="submit">Save</button>
                  <button className={styles.cancel} type="button" onClick={handleCancelEdit}>
                    Cancel
                  </button>
                </div>
              </form>
            ) : (
              <p>{comment.content}</p>
            )}
          </div>
        </div>

        {isOwner && isAuthenticated && (
          <>
            <IconButton sx={{ height: 'max-content', width: 'max-content' }} onClick={onMenuClick}>
              <img src="/icons/menu-v.png" alt="menu" />
            </IconButton>

            <Menu
              anchorEl={anchorEl}
              open={menuOpen}
              onClose={onCloseMenu}
              PaperProps={{ elevation: 4, className: styles.menuPaper }}
              transformOrigin={{ horizontal: 'right', vertical: 'top' }}
              anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
              disableScrollLock
            >
              <MenuItem
                key="update"
                onClick={() => {
                  setIsEditing(true); // enable editing directly here
                  onCloseMenu();
                }}
                className={styles.menuItem}
              >
                <img className={styles.menuIcon} src="/icons/update.png" alt="update icon" />
                Update
              </MenuItem>

              <MenuItem
                key="delete"
                onClick={() => {
                  onDelete();
                  onCloseMenu();
                }}
                className={styles.menuItem}
              >
                <img className={styles.menuIcon} src="/icons/delete.png" alt="delete icon" />
                Delete
              </MenuItem>
            </Menu>
          </>
        )}
      </div>
    </div>
  );
};

export default CommentLayout;
