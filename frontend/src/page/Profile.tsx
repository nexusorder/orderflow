import axios from 'axios'
import { useEffect } from 'react'
import { BsFillPersonFill } from "react-icons/bs"
import { useNavigate } from 'react-router-dom'
import { useRecoilState } from 'recoil'
import '../asset/css/Profile.css'
import Button from '../component/Button'
import TabBar from '../component/TabBar'
import { logout, profileAtom } from '../service/AuthService'

function Profile() {
  const client = axios.create({
    baseURL: '',
    headers: {
      'Content-Type': 'application/json',
    },
  })

  const [profileStatus, setProfileStatus] = useRecoilState(profileAtom)

  const navigate = useNavigate()

  useEffect(() => {
    
  }, [])

  function maskPhoneNumber(phoneNumber: string): string {
    if (!phoneNumber) {
      return '';
    }
    const regex = /^(\d*)-(\d*)-(\d*)$/;
    const match = phoneNumber.match(regex);
    
    if (match) {
      return `${match[1]}-****-${match[3]}`;
    } else {
      return phoneNumber;
    }
  }

  return (
    <div className="profile">
      <div className='container'>
        <div className='header'>내 정보</div>
        <div className='about'>
          <div className='left'>
            <BsFillPersonFill className='icon'/>
          </div>
          <div className='right'>
            <div className='name'>{profileStatus.nickname}</div>
            <div className='phone'>{maskPhoneNumber(profileStatus.phone)}</div>
          </div>
        </div>
        <div className='logout-wrapper'>
          <Button 
          className='logout' 
          value='로그아웃' 
          style={{background: 'var(--gray3)'}}
          onClick={() => {
            logout()
            .finally(() => {
              setProfileStatus({});
              navigate('/login');
            });

          }} />
        </div>
      </div>
      <TabBar activeTab={3} />
    </div>
  )
}

export default Profile
