export type Role = 'ROLE_ADMIN' | 'ROLE_PARTICIPANT' | 'ROLE_JURY';

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  role: Role;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface JwtResponse {
  token: string;
  username: string;
  role: Role;
}

export interface UserResponse {
  id: number;
  username: string;
  email: string;
  role: Role;
}