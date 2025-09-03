# BlogLand Frontend

BlogLand Frontend is a **React + TypeScript** web application built with **Vite**. It serves as the user interface for the BlogLand platform, providing a modern, responsive, and feature-rich blogging experience.

The project follows a clean modular folder structure and leverages modern libraries like Redux Toolkit, React Query, Zod, and MUI for robust state management, form validation, and UI design.

---

## Features

### Authentication & Authorization
- Login, Register, Logout
- JWT decoding for session handling
- Persistent state with Redux Persist

### Content Management
- Create, update, and browse blog posts
- Category filtering and search
- Commenting and likes

### User Experience
- Responsive UI built with MUI + CSS Modules
- SweetAlert2 modals for confirmation and alerts

### Validation
- Form validation with Zod schemas
- Centralized validation utilities

### API Integration
- REST API communication via Axios
- Data fetching and caching powered by React Query

### Extras
- Newsletter subscription form
- Contact form with location autocomplete via Google Places API
- SEO-ready with React Helmet Async

---

## Tech Stack

- **Core**: React 19, TypeScript, Vite
- **UI/Styling**: MUI (Material + Joy + Lab), CSS Modules, MDB React UI Kit, classnames/clsx
- **State Management**: Redux Toolkit, Redux Persist
- **Data Fetching**: React Query, Axios
- **Validation**: Zod
- **Utilities**: jwt-decode, sweetalert2, react-icons, lottie-react
- **Routing**: React Router v7
- **SEO**: React Helmet Async

---

## Folder Structure

blog-land-frontend/  
├── public/              # Static assets (images, icons, etc.)  
├── src/  
│   ├── api/             # API calls (axios clients, endpoints)  
│   ├── components/      # Reusable UI components  
│   ├── constants/       # Global constants  
│   ├── features/        # Feature-based slices/modules  
│   ├── hooks/           # Custom React hooks  
│   ├── layout/          # Layout components (HomePage, SinglePostPage, etc.)  
│   ├── routes/          # Application routes  
│   ├── schemas/         # Zod validation schemas  
│   ├── services/        # Service layer (API wrappers, helpers)  
│   ├── store/           # Redux store & slices  
│   ├── types/           # TypeScript types/interfaces  
│   ├── utils/           # Utility functions  
│   ├── App.tsx          # App entry  
│   └── main.tsx         # ReactDOM entry  
└── vite.config.ts       # Vite configuration  

---

## Getting Started

### Prerequisites
- Node.js 18+
- npm or yarn

### Clone and Install
```bash
git clone https://github.com/NelaniMaluka/blog-land-frontend.git
cd blog-land-frontend
npm install --legacy-peer-deps
```

### Run in Development
```bash
npm run dev
```

App will be available at:  
[http://localhost:5173](http://localhost:5173)

### Build for Production
```bash
npm run build
```

Preview production build locally:
```bash
npm run preview
```

### Deployment
- **Frontend Hosting**: Firebase / Vercel / Netlify (recommended)
- **Backend API**: Connects to BlogLand Backend (Spring Boot on Render)

Environment variables required (.env):
```bash
VITE_API_BASE_URL=https://your-backend-api.com/api
VITE_GOOGLE_MAPS_API_KEY=your-google-maps-api-key
```

---

## Roadmap

- ✅ User authentication and session handling
- ✅ CRUD for blog posts, categories, and comments
- ✅ Responsive UI with MUI and CSS Modules
- ✅ Zod-based form validation
- ⏳ Offline support with service workers
- ⏳ Enhanced analytics dashboards

---

## Contributing
Contributions are welcome!  
Please open an issue or submit a pull request for discussions.

---

## License
MIT License. See [LICENSE](LICENSE) for details.
