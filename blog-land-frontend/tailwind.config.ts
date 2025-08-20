import type { Config } from 'tailwindcss';

const config: Config = {
  content: [
    './index.html',
    './src/**/*.{ts,tsx}', // Include .ts and .tsx files for TSX support
  ],
  theme: {
    extend: {},
  },
  plugins: [],
};

export default config;
