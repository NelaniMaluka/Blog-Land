// hooks/useWebSocket.ts
import { Client, StompSubscription, Frame } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useEffect, useRef } from 'react';

let client: Client | null = null;
const subscriptionsMap = new Map<string, StompSubscription>();

export const useWebSocket = (
  topic: string,
  onMessage: (message: string) => void,
  token?: string
) => {
  const onMessageRef = useRef(onMessage);
  onMessageRef.current = onMessage;

  useEffect(() => {
    // Initialize client if not yet created
    if (!client) {
      const socket = new SockJS('https://blog-land.onrender.com/ws');
      client = new Client({
        webSocketFactory: () => socket,
        reconnectDelay: 5000,
        debug: () => {},
      });

      client.activate();
    }

    const subscribeTopic = () => {
      if (client && client.connected && !subscriptionsMap.has(topic)) {
        const sub = client.subscribe(topic, (msg) => {
          onMessageRef.current(msg.body); // still calls your callback, no logs
        });
        subscriptionsMap.set(topic, sub);
      }
    };

    // Set JWT before connect
    if (client && token) {
      client.connectHeaders = { Authorization: `Bearer ${token}` };
    }

    if (client?.connected) {
      subscribeTopic();
    } else {
      const originalOnConnect = client!.onConnect;
      client!.onConnect = (frame: Frame) => {
        originalOnConnect?.(frame);
        subscribeTopic();
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
  }, [topic, token]);
};
