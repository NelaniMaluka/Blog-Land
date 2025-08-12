export const stripHtml = (html: string) => html.replace(/<\/?[^>]+(>|$)/g, '');

export const formatDate = (dateString?: string): string => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return isNaN(date.getTime()) ? '' : date.toLocaleDateString();
};
