// hooks/useWebSocket.ts
import { Client, StompSubscription, Frame } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useEffect, useRef } from 'react';

let client: Client | null = null;
const subscriptionsMap = new Map<string, StompSubscription>();

export const useWebSocket = (topic: string, onMessage: (message: string) => void) => {
  const onMessageRef = useRef(onMessage);
  onMessageRef.current = onMessage; // always keep latest callback

  useEffect(() => {
    // Initialize client if not yet created
    if (!client) {
      const socket = new SockJS('http://localhost:8080/ws');
      client = new Client({
        webSocketFactory: () => socket,
        reconnectDelay: 5000,
        debug: (msg) => console.log('[STOMP]', msg),
      });

      client.activate();
    }

    const subscribeTopic = (t: string) => {
      if (client && client.connected && !subscriptionsMap.has(t)) {
        const sub = client.subscribe(t, (msg) => {
          onMessageRef.current(msg.body);
        });
        subscriptionsMap.set(t, sub);
      }
    };

    if (client?.connected) {
      // Already connected, subscribe immediately
      subscribeTopic(topic);
    } else {
      // If not connected, override onConnect once
      const originalOnConnect = client!.onConnect;
      client!.onConnect = (frame: Frame) => {
        originalOnConnect?.(frame); // call original with frame
        subscribeTopic(topic);
      };
    }

    // Cleanup on unmount
    return () => {
      const sub = subscriptionsMap.get(topic);
      if (sub) {
        sub.unsubscribe();
        subscriptionsMap.delete(topic);
      }
    };
  }, [topic]);
};
