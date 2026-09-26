import { Component , computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { rxResource } from '@angular/core/rxjs-interop';
import { LeaderboardService } from '../../core/services/leaderboard.service';
import { WebSocketService } from '../../core/services/websocket.service';

@Component({
  selector: 'app-leaderboard',
  imports: [CommonModule],
  templateUrl: './leaderboard.html',
  styleUrl: './leaderboard.css'
})
export class Leaderboard {

  leaderboardResource = rxResource({
    stream: () => this.leaderboardService.getLeaderboard()
  });

  // Combine : la donnée live WebSocket si elle existe, sinon le chargement initial HTTP
  entries = computed(() =>
    this.webSocketService.liveLeaderboard() ?? this.leaderboardResource.value() ?? []
  );

  constructor(
    private leaderboardService: LeaderboardService,
    private webSocketService: WebSocketService
  ) {}

  ngOnInit(): void {
    this.webSocketService.connect();
  }

  ngOnDestroy(): void {
    this.webSocketService.disconnect();
  }
}