import { useParams } from 'react-router-dom';
import { SinglePostLayout } from '../components/layouts/singlePostLayout/SinglePostLayout';
import { useGetPost } from '../hooks/usePost';
import { Helmet } from 'react-helmet-async';
import { useGetPublicUser } from '../hooks/useUser';

export const PostPage = () => {
  const { idAndSlug } = useParams<{ idAndSlug: string }>();
  const [idStr, ...slugParts] = idAndSlug?.split('-') || [];
  const id = Number(idStr);
  const slug = slugParts.join('-');

  const { data: post, isLoading, isError } = useGetPost({ id });
  const { data: user } = useGetPublicUser(post?.userId);

  const canonicalUrl = window.location.href;
  const title = post?.title || 'Blog Post – Blog Land';
  const description =
    post?.summary ||
    `Read "${post?.title}" by ${post?.author} on Blog Land. Insights, analysis, and references included.`;
  const imageUrl = post?.postImgUrl || `${window.location.origin}/og-image-default.jpg`;

  // Generate keywords dynamically
  const keywords = [
    post?.title,
    post?.categoryId ? `Category ${post.categoryId}` : '',
    post?.author,
    'Blog Land',
    'blog post',
    'article',
    ...(post?.references?.split(',') || []),
  ]
    .filter(Boolean)
    .join(', ');

  const structuredData = post
    ? {
        '@context': 'https://schema.org',
        '@type': 'BlogPosting',
        headline: title,
        image: [imageUrl],
        author: { '@type': 'Person', name: post.author || 'Blog Land' },
        editor:
          user?.firstname + ' ' + user?.lastname
            ? { '@type': 'Person', name: user?.firstname + ' ' + user?.lastname }
            : undefined,
        publisher: {
          '@type': 'Organization',
          name: 'Blog Land',
          logo: { '@type': 'ImageObject', url: `${window.location.origin}/logo.png` },
        },
        datePublished: post.createdAt,
        dateModified: post.updatedAt || post.createdAt,
        mainEntityOfPage: { '@type': 'WebPage', '@id': canonicalUrl },
        description: description,
        keywords: keywords,
        articleSection: post.categoryId ? `Category ${post.categoryId}` : undefined,
        interactionStatistic: [
          {
            '@type': 'InteractionCounter',
            interactionType: 'https://schema.org/CommentAction',
            userInteractionCount: post.commentCount,
          },
          {
            '@type': 'InteractionCounter',
            interactionType: 'https://schema.org/ReadAction',
            userInteractionCount: post.views,
          },
        ],
        wordCount: post.content?.split(/\s+/).length,
        url: canonicalUrl,
      }
    : null;

  return (
    <>
      <Helmet>
        <title>{title} – Blog Land</title>
        <meta name="description" content={description} />
        <meta name="keywords" content={keywords} />
        <meta name="author" content={post?.author || 'Blog Land'} />
        <link rel="canonical" href={canonicalUrl} />

        {/* Open Graph */}
        <meta property="og:title" content={title} />
        <meta property="og:description" content={description} />
        <meta property="og:type" content="article" />
        <meta property="og:url" content={canonicalUrl} />
        <meta property="og:image" content={imageUrl} />
        <meta property="article:published_time" content={post?.createdAt} />
        <meta property="article:modified_time" content={post?.updatedAt || post?.createdAt} />
        <meta property="article:author" content={post?.author} />
        {post?.references && <meta property="article:tag" content={post.references} />}

        {/* Twitter Card */}
        <meta name="twitter:card" content="summary_large_image" />
        <meta name="twitter:title" content={title} />
        <meta name="twitter:description" content={description} />
        <meta name="twitter:image" content={imageUrl} />
        <meta name="twitter:label1" content="Author" />
        <meta name="twitter:data1" content={post?.author} />

        {/* Structured Data */}
        {structuredData && (
          <script type="application/ld+json">{JSON.stringify(structuredData)}</script>
        )}
      </Helmet>

      <SinglePostLayout user={user} post={post} isLoading={isLoading} isError={isError} />
    </>
  );
};
