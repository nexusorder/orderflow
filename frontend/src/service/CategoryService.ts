import axios from 'axios'
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

// methods

export const categories = () => {
  return client.get('/api/v1/categories')
}
