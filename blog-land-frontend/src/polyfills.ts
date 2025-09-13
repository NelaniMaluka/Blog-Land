// src/polyfills.ts
import { Buffer } from 'buffer';

declare const global: any;

if (typeof global === 'undefined') {
  (window as any).global = window;
}

if (typeof Buffer === 'undefined') {
  (window as any).Buffer = Buffer;
}
