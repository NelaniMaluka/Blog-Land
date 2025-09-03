import { useGetPostLikesCount, useAddLike } from '../../hooks/useLike';
import ThumbUpIcon from '@mui/icons-material/ThumbUp';
import { useState } from 'react';
import { store, RootState } from '../../store/store';
import { useEffect } from 'react';
import { useGetUserLikes } from '../../hooks/useLike';
import { useRemoveLike } from '../../hooks/useLike';
import { useQueryClient } from '@tanstack/react-query';
import { useSelector } from 'react-redux';
import { useDialog } from '../LoginProvider/LoginProvider';

import styles from './LikeButton.module.css';

interface LikeButtonProps {
  postId: number;
}

const LikeButton = ({ postId }: LikeButtonProps) => {
  const { showLogin } = useDialog();
  const { data } = useGetPostLikesCount(postId);
  const [isLiked, setIsLiked] = useState(false);
  const [openLoginDialog, setOpenLoginDialog] = useState(false);
  const addLike = useAddLike();
  const removeLike = useRemoveLike();
  const { data: userLikes } = useGetUserLikes();
  const queryClient = useQueryClient();
  const auth = useSelector((state: RootState) => state.auth.isAuthenticated);

  useEffect(() => {
    if (auth && userLikes) {
      const liked = userLikes.some((like) => like.postId === postId);
      if (liked) {
        setIsLiked(true);
      }
    } else {
      setIsLiked(false);
    }
  }, [auth, userLikes, postId]);

  const handleClick = async () => {
    if (!store.getState().auth.isAuthenticated) {
      showLogin();
      return;
    }

    if (isLiked) {
      const like = userLikes?.find((like) => like.postId === postId);
      if (!like) return;

      try {
        await removeLike.mutateAsync(like.likeId, {
          onSuccess: () => {
            setIsLiked(false);
            queryClient.invalidateQueries({ queryKey: ['postLikes', postId] });
            queryClient.invalidateQueries({ queryKey: ['userLikes'] });
          },
        });
      } catch (err) {}
    } else {
      try {
        await addLike.mutateAsync(postId, {
          onSuccess: () => {
            setIsLiked(true);
            queryClient.invalidateQueries({ queryKey: ['postLikes', postId] });
            queryClient.invalidateQueries({ queryKey: ['userLikes'] });
          },
        });
      } catch (err) {}
    }
  };

  return (
    <>
      <button
        className={`${styles.likeButton} ${isLiked ? styles.liked : ''}`}
        onClick={handleClick}
      >
        <ThumbUpIcon />
        {data}
      </button>
    </>
  );
};

export default LikeButton;
