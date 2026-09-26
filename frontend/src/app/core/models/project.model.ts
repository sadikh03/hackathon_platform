export interface ProjectRequest {
  title: string;
  description: string;
  githubLink?: string; // le "?" = champ optionnel, comme githubLink optionnel côté Java
}

export interface ProjectResponse {
  id: number;
  title: string;
  description: string;
  githubLink: string | null;
  teamId: number;
  teamName: string;
  fileName: string | null;
}