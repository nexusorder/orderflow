import axios, { CancelTokenSource } from 'axios';
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
export function search(request: any, cancelTokenSource: CancelTokenSource = axios.CancelToken.source()) {
  const data = {
    ...request
  }
  return client.post('/api/v1/search', data, {cancelToken: cancelTokenSource.token})
}

export const shopOne = (id: string) => {
  return client.get(`/api/v1/shops/${id}`)
}

export const shopsAll = () => {
  return client.get('/api/v1/shops')
}

export const categories = () => {
  return client.get('/api/v1/shops/categories')
}

export const recommends = (count?: number) => {
  const params = count ? {'count': count} : {'count': 1};
  return client.get('/api/v1/shops/recommend', { params })
}

// business logic
export function formatNumber(num: number) {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
    useGrouping: true,
  }).format(num)
}

export const prepareShop = (shop: any, locationStatus: any, locationService: any) => {
  const newDelay = Math.ceil(
    (shop.delay ?? 0) +
      (locationStatus?.latitude && locationStatus?.longitude && shop.latitude && shop.longitude
        ? locationService.haversineDistance(
            locationStatus.latitude,
            locationStatus.longitude,
            shop.latitude!!,
            shop.longitude!!
          ) 
          / 0.54  // 오토바이 평균 속력(km/min)
          + 5
        : 30)
  )
  return {
    ...shop,
    delay: newDelay,
    minimumOrder: formatNumber(shop.minimumOrder),
    deliveryFee: formatNumber(shop.deliveryFee),
  }
}
