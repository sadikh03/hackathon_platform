export interface TeamRequest {
  name: string;
}

export interface TeamMemberResponse {
  userId: number;
  username: string;
}

export interface TeamResponse {
  id: number;
  name: string;
  createdBy: string;
  members: TeamMemberResponse[];
}