// Profile.tsx
import * as React from 'react';
import styles from './Profile.module.css';
import {
  UserResponse,
  ExperienceLabels,
  ExperienceLevel,
  Role,
  Provider,
} from '../../../../types/user/response';
import { useGetUser } from '../../../../hooks/useUser';

interface ProfileProps {
  user?: UserResponse;
}

export const Profile = () => {
  const { data: user, isLoading, isError } = useGetUser();

  if (isLoading) return <p>Loading...</p>;
  if (isError || !user) return <p>Failed to load user data.</p>;

  return (
    <div className={styles.profile}>
      <div className={styles.profile}>
        <img src={user.profileIconUrl || '/default-avatar.png'} alt="Profile" />
        <h3>
          {user.firstname} {user.lastname}
        </h3>
        <p>{user.email}</p>
      </div>

      <div className={styles.profile}>
        <form className={styles.form}>
          <label>
            First Name:
            <input name="firstname" value={user.firstname} readOnly />
          </label>
          <label>
            Last Name:
            <input name="lastname" value={user.lastname} readOnly />
          </label>
          <label>
            Email:
            <input name="email" value={user.email} readOnly />
          </label>
          {/* Add editable fields if you want */}
        </form>
      </div>
    </div>
  );
};
