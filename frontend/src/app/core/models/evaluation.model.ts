export interface EvaluationRequest {
  innovationScore: number;
  technicalScore: number;
  presentationScore: number;
  comment?: string;
}

export interface EvaluationResponse {
  id: number;
  innovationScore: number;
  technicalScore: number;
  presentationScore: number;
  totalScore: number;
  comment: string | null;
  projectId: number;
  projectTitle: string;
  juryUsername: string;
}