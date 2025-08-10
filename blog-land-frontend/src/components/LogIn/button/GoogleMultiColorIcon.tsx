import * as React from 'react';

export default function GoogleMultiColorIcon(props: React.SVGProps<SVGSVGElement>) {
  return (
    <svg
      {...props}
      xmlns="http://www.w3.org/2000/svg"
      width="18"
      height="18"
      viewBox="0 0 24 24"
      fill="none"
    >
      <path
        fill="#4285F4"
        d="M23.64 12.204c0-.796-.07-1.562-.202-2.298H12v4.348h6.356a5.43 5.43 0 01-2.348 3.566v2.958h3.797c2.225-2.05 3.502-5.064 3.502-8.574z"
      />
      <path
        fill="#34A853"
        d="M12 24c3.24 0 5.965-1.07 7.953-2.894l-3.797-2.958c-1.053.708-2.4 1.125-4.156 1.125-3.19 0-5.9-2.154-6.87-5.054H1.12v3.178A11.998 11.998 0 0012 24z"
      />
      <path
        fill="#FBBC05"
        d="M5.13 14.219a7.17 7.17 0 010-4.438V6.603H1.12a12 12 0 000 10.794l4.01-3.178z"
      />
      <path
        fill="#EA4335"
        d="M12 4.78c1.767 0 3.347.608 4.59 1.802l3.44-3.44C17.963 1.28 15.237 0 12 0a11.998 11.998 0 00-10.88 6.603l4.01 3.178c.974-2.9 3.68-5.002 6.87-5.002z"
      />
    </svg>
  );
}
