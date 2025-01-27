import axios from 'axios';
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
export const orderOne = (id: string) => {
  return client.get(`/api/v1/orders/${id}`)
}

export const ordersAll = () => {
  return client.get('/api/v1/orders')
}

export const createOrder = (data: {shopId: string, products: Array<{productId: string, quantity: number}>}) => {
  return client.post('/api/v1/orders', data)
}

// business logic
export function formatNumber(num: number) {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
    useGrouping: true,
  }).format(num)
}

export const prepareOrder = (order: any) => {
  return {
    ...order,
    products: order.products.map((product: any) => {
      return {
        ...product,
        price: formatNumber(product.price),
      }
    }),
    deliveryFee: formatNumber(order.deliveryFee),
    grandTotal: formatNumber(order.grandTotal),
  }
}
