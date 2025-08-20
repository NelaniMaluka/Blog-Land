import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useGetCategories } from '../hooks/useCategory';
import { useGetCategoryPosts } from '../hooks/usePost';
import { PostsLayout } from '../components/layouts/postLayout/PostsLayout';
import { Order } from '../types/post/response';
import { PostResponse } from '../types/post/response';
import { Helmet } from 'react-helmet-async';

export const CategoryPage = () => {
  const { slug } = useParams<{ slug: string }>();
  const { data: categories } = useGetCategories();
  const [page, setPage] = useState(0);
  const [order, setOrder] = useState<Order>(Order.LATEST);

  const decodedSlug = decodeURIComponent(slug || '');
  const category = categories?.find((c) => c.name.toLowerCase() === decodedSlug.toLowerCase());

  const { data, isLoading, isError } = useGetCategoryPosts({
    categoryId: category?.id || 0,
    page,
    size: 12,
    order,
  });

  const posts: PostResponse[] = data?.content ?? [];
  const totalPages: number = data?.totalPages ?? 0;
  const totalElements: number = data?.totalElements || 0;
  const canonicalUrl = window.location.href;

  // Generate comma-separated keywords from posts
  const keywords = posts.map((p) => p.title).join(', ');

  const structuredData = {
    '@context': 'https://schema.org',
    '@type': 'CollectionPage',
    name: category?.name || 'Category',
    url: canonicalUrl,
    description: `Explore posts in the ${category?.name || 'category'} on Blog Land.`,
    hasPart: posts.map((p) => ({
      '@type': 'BlogPosting',
      headline: p.title,
      url: `${window.location.origin}/post/${p.id}`,
      author: {
        '@type': 'Person',
        name: p.author,
      },
      datePublished: p.createdAt,
      image: p.postImgUrl || `${window.location.origin}/default-post.jpg`,
    })),
  };

  return (
    <>
      <Helmet>
        <title>{category?.name ? `${category.name} – Blog Land` : 'Category – Blog Land'}</title>
        <meta
          name="description"
          content={`Read the latest posts in the ${
            category?.name || 'category'
          } on Blog Land, a community-driven platform for writers and readers.`}
        />
        <meta
          name="keywords"
          content={`Blog Land, ${category?.name}, blogging, posts, ${keywords}`}
        />
        <meta name="author" content="Nelani Maluka" />

        <link rel="canonical" href={canonicalUrl} />

        {/* Open Graph */}
        <meta property="og:title" content={category?.name || 'Category – Blog Land'} />
        <meta
          property="og:description"
          content={`Explore posts in the ${
            category?.name || 'category'
          } on Blog Land, where writers and readers connect.`}
        />
        <meta property="og:type" content="website" />
        <meta property="og:url" content={canonicalUrl} />
        <meta property="og:image" content={`${window.location.origin}/og-image-category.jpg`} />

        {/* Twitter Card */}
        <meta name="twitter:card" content="summary_large_image" />
        <meta name="twitter:title" content={category?.name || 'Category – Blog Land'} />
        <meta
          name="twitter:description"
          content={`Explore posts in the ${category?.name || 'category'} on Blog Land.`}
        />
        <meta name="twitter:image" content={`${window.location.origin}/og-image-category.jpg`} />

        {/* Structured Data */}
        <script type="application/ld+json">{JSON.stringify(structuredData)}</script>
      </Helmet>

      <PostsLayout
        title={category?.name || 'Category'}
        posts={posts}
        isLoading={isLoading}
        isError={isError}
        page={page}
        setPage={setPage}
        totalPages={totalPages}
        order={order}
        setOrder={setOrder}
        showOrderButtons={true}
        totalElements={totalElements}
      />
    </>
  );
};
