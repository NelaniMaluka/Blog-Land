export const stripHtml = (html: string) => html.replace(/<\/?[^>]+(>|$)/g, '');

export const formatDate = (dateString?: string): string => {
  if (!dateString) return '';

  const date = new Date(dateString);
  const now = new Date();

  const diffInMs = now.getTime() - date.getTime();
  const diffInYears = Math.floor(diffInMs / (1000 * 60 * 60 * 24 * 365));

  if (diffInYears >= 1) {
    return diffInYears === 1 ? '1 year ago' : `${diffInYears} years ago`;
  }

  // If less than a year. show Month Day
  const options: Intl.DateTimeFormatOptions = { month: 'short', day: 'numeric' };
  return date.toLocaleDateString(undefined, options);
};

export function formatViews(num: number) {
  if (num >= 1000) {
    return (num / 1000).toFixed(num % 1000 === 0 ? 0 : 1) + 'K';
  }
  return num.toString();
}
