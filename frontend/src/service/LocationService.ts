import { atom } from 'recoil';

export const locationAtom = atom<any>({
  key: 'location/location',
  default: {},
})

// service
export class LocationService {
  private static instance: LocationService
  private location: { latitude: number; longitude: number } | null = null

  private constructor() {
    // private constructor to prevent direct instantiation
  }

  static getInstance(): LocationService {
    if (!LocationService.instance) {
      LocationService.instance = new LocationService()
    }
    return LocationService.instance
  }

  getCurrentLocation(): Promise<{ latitude: number; longitude: number }> {
    return new Promise((resolve, reject) => {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          (position) => {
            resolve({
              latitude: position.coords.latitude,
              longitude: position.coords.longitude,
            })
          },
          (error) => {
            reject(error)
          }
        )
      } else {
        reject(new Error('Geolocation is not supported by this browser.'))
      }
    })
  }

  haversineDistance(
    lat1: number,
    lon1: number,
    lat2: number,
    lon2: number
  ): number {
    const R: number = 6371 // 지구의 평균 반경 (km)

    // 위도와 경도를 라디안으로 변환
    const [rlat1, rlon1, rlat2, rlon2] = [lat1, lon1, lat2, lon2].map(
      (x) => (x * Math.PI) / 180
    )

    // Haversine 공식
    const dlat: number = rlat2 - rlat1
    const dlon: number = rlon2 - rlon1
    const a: number =
      Math.sin(dlat / 2) ** 2 +
      Math.cos(rlat1) * Math.cos(rlat2) * Math.sin(dlon / 2) ** 2
    const c: number = 2 * Math.asin(Math.sqrt(a))

    const distance: number = R * c
    return distance
  }
}

export default LocationService.getInstance()
