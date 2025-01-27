import axios from 'axios';
import { atom } from 'recoil';
import { sha256 } from '../util/HashUtil';
import { mockAxios } from '../util/MockUtil';

// client
mockAxios();
const client = axios.create({
  baseURL: '',
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
})

// atoms
export const profileAtom = atom<any>({
  key: 'auth/profile',
  default: {},
})

// methods
export function login(request: any) {
  const data = {
    ...request,
    password: sha256(request.password),
  }
  return client.post('/api/v1/auth/login', data)
}

export function signup(request: any) {
  const data = {
    ...request,
    password: sha256(request.password),
    passwordConfirm: sha256(request.passwordConfirm),
  }
  return client.post('/api/v1/auth/signup', data)
}

export function logout() {
  return client.post('/api/v1/auth/logout')
}

export function profile() {
  return client.get('/api/v1/auth/profile')
}
