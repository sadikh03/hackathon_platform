import { Injectable, signal } from '@angular/core';
import { Client } from '@stomp/stompjs';
import { LeaderboardEntry } from '../models/leaderboard.model';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private client: Client | null = null;

  liveLeaderboard = signal<LeaderboardEntry[] | null>(null);

  connect(): void {
    if (this.client?.active) return;

    this.client = new Client({
      brokerURL: 'ws://localhost:8080/ws-native',
      reconnectDelay: 5000,
      debug: (str) => console.log('[STOMP]', str),
    });

    this.client.onConnect = () => {
      this.client!.subscribe('/topic/leaderboard', (message) => {
        const data: LeaderboardEntry[] = JSON.parse(message.body);
        this.liveLeaderboard.set(data);
      });
    };

    this.client.onStompError = (frame) => {
      console.error('Erreur STOMP:', frame);
    };

    this.client.activate();
  }

  disconnect(): void {
    this.client?.deactivate();
    this.client = null;
  }
}