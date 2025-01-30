import { useEffect, useState } from 'react'
import { useRecoilState } from 'recoil'
import '../asset/css/index.css'
import { profile, profileAtom } from '../service/AuthService'
import locationService, { locationAtom } from '../service/LocationService'
import MainRouter from './MainRouter'

function Initializer() {
  const [profileStatus, setProfileStatus] = useRecoilState(profileAtom)
  const [locationStatus, setLocationStatus] = useRecoilState(locationAtom)
  const [loaded, setLoaded] = useState(false)

  const checkLogin = () => {
    return profile()
      .then((it) => {
        setLoaded(true);
        if (it.data.data) {
          setProfileStatus(it.data.data)
          return new Promise((resolve, reject) => {
            resolve(true)
          })
        } else {
          if (window.location.pathname !== '/login') {
            window.history.pushState({}, '', '/login')
            window.location.reload()
          }
          setProfileStatus({})
          return new Promise((resolve, reject) => {
            resolve(false)
          })
        }
      })
      .catch(() => {
        setLoaded(true);
        if (window.location.pathname !== '/login') {
            window.history.pushState({}, '', '/login')
            window.location.reload()
        }
        return new Promise((resolve, reject) => {
          resolve(false)
        })
      })
  }

  const getLocation = () => {
    return locationService
      .getCurrentLocation()
      .then((it) => {
        console.log(`%clocation %c${JSON.stringify(it)}`, 'color: skyblue;', 'color: inherit;')
        if (it) {
          setLocationStatus(it)
          return Promise.resolve(true)
        } else {
          setLocationStatus(null)
          return Promise.resolve(false)
        }
      })
      .catch(() => {
        return Promise.resolve(false)
      })
  }

  useEffect(() => {
    console.log(`%cenv %c${process.env.REACT_APP_ENV}`, "color: skyblue;", "color: inherit;")
    checkLogin()
      .then((it) => {
        console.log(`%clogin %c${it}`, 'color: skyblue;', 'color: inherit;')
        if (it) {
          return getLocation()
        }
      })
  }, []);

  if (!loaded) {
    return <></>;
  }

  return <MainRouter/>;
}

export default Initializer
