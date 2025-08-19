// DashboardPage.tsx
import { Dashboard } from '../components/ui/dashboard/Dashboard';
import * as React from 'react';
import { Profile } from '../components/ui/dashboard/profile/Profile';
import { Posts } from '../components/ui/dashboard/posts/posts';

export const DashboardPage = () => {
  return (
    <Dashboard
      renderContent={(selected) => {
        if (selected === 'profile') return <Profile />;
        if (selected === 'posts') return <Posts />;
        return null;
      }}
    />
  );
};
