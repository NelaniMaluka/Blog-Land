export const stripHtml = (html: string) => html.replace(/<\/?[^>]+(>|$)/g, '');

export const formatDate = (dateString?: string): string => {
  if (!dateString) return '';

  const date = new Date(dateString);
  const now = new Date();

  const diffInMs = now.getTime() - date.getTime();
  const diffInSeconds = Math.floor(diffInMs / 1000);
  const diffInMinutes = Math.floor(diffInSeconds / 60);
  const diffInHours = Math.floor(diffInMinutes / 60);
  const diffInDays = Math.floor(diffInHours / 24);
  const diffInWeeks = Math.floor(diffInDays / 7);
  const diffInMonths = Math.floor(diffInDays / 30);
  const diffInYears = Math.floor(diffInDays / 365);

  if (diffInYears >= 1) {
    return diffInYears === 1 ? '1 year ago' : `${diffInYears} years ago`;
  }

  if (diffInMonths >= 1) {
    return diffInMonths === 1 ? '1 month ago' : `${diffInMonths} months ago`;
  }

  if (diffInWeeks >= 1) {
    return diffInWeeks === 1 ? '1 week ago' : `${diffInWeeks} weeks ago`;
  }

  if (diffInDays === 1) {
    return 'yesterday';
  }

  if (diffInDays > 1) {
    return `${diffInDays} days ago`;
  }

  if (diffInHours >= 1) {
    return diffInHours === 1 ? '1 hour ago' : `${diffInHours} hours ago`;
  }

  if (diffInMinutes >= 1) {
    return diffInMinutes === 1 ? '1 minute ago' : `${diffInMinutes} minutes ago`;
  }

  if (diffInSeconds >= 10) {
    return `${diffInSeconds} seconds ago`;
  }

  return 'just now';
};

export function formatViews(num: number) {
  if (num >= 1000) {
    return (num / 1000).toFixed(num % 1000 === 0 ? 0 : 1) + 'K';
  }
  return num.toString();
}
