export interface DeadlineRequest {
  submissionDeadline: string; // format ISO, ex: "2026-12-31T23:59:00"
}

export interface DeadlineResponse {
  submissionDeadline: string | null;
}