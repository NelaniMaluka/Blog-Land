// declarations.d.ts (or vite-env.d.ts)

declare module '*.module.css' {
  const classes: { [key: string]: string };
  export default classes;
}

// Add this for react-places-autocomplete
declare module 'react-places-autocomplete';

// Vite env interface
interface ImportMetaEnv {
  readonly VITE_GOOGLE_MAPS_API_KEY: string;
  readonly VITE_YOUTUBE_API_KEY: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
